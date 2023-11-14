package ru.sberbank.pprb.sbbol.global_search.updater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.OpenSearchException;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.admin.indices.refresh.RefreshRequest;
import org.opensearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.opensearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.opensearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.opensearch.action.ingest.GetPipelineRequest;
import org.opensearch.action.ingest.GetPipelineResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.client.indices.GetMappingsRequest;
import org.opensearch.client.indices.GetMappingsResponse;
import org.opensearch.client.indices.IndexTemplatesExistRequest;
import org.opensearch.common.settings.Settings;
import org.opensearch.index.reindex.BulkByScrollResponse;
import org.opensearch.index.reindex.ReindexRequest;
import org.opensearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.global_search.updater.query.IndexQuery;
import ru.sberbank.pprb.sbbol.global_search.updater.query.IndexQueryParam;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Реализация сервиса проливки обновлений шаблонов в Opensearch
 */
public class OpenSearchUpdaterTemplateService extends OpenSearchUpdaterServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(OpenSearchUpdaterTemplateService.class);

    private static final String TEMPLATE_ENDPOINT_PREFIX = "_template";
    private static final String INDEX_PREFIX = "index";
    private static final String REFRESH_INTERVAL_PARAM = "refresh_interval";
    private static final String TRANSLOG_DURABILITY_PARAM = "translog.durability";
    private static final String NUMBER_OF_REPLICAS_PARAM = "number_of_replicas";

    private final boolean replicasDisable;
    private final boolean refreshIntervalDisable;
    private final boolean translogDisable;
    private final int reindexBatchSize;
    private final int reindexSlices;

    public OpenSearchUpdaterTemplateService(RestHighLevelClient restClient, ObjectMapper mapper, UpdaterQueriesIndexService queriesIndexService,
                                            boolean replicasDisable, boolean refreshIntervalDisable, boolean translogDisable, int reindexBatchSize, int reindexSlices) {
        super(restClient, mapper, queriesIndexService);
        this.replicasDisable = replicasDisable;
        this.refreshIntervalDisable = refreshIntervalDisable;
        this.translogDisable = translogDisable;
        this.reindexBatchSize = reindexBatchSize;
        this.reindexSlices = reindexSlices;
    }

    @Override
    protected void processQuery(IndexQuery query) throws IOException {
        Map<IndexQueryParam, String> params = query.getParams();
        String endpoint = query.getEndpoint();
        Map<String, Object> body = query.getJsonBody();

        if (!endpoint.startsWith(TEMPLATE_ENDPOINT_PREFIX)) {
            throw new IllegalArgumentException("Unexpected endpoint. Only \"" + TEMPLATE_ENDPOINT_PREFIX + "\" allowed");
        }

        String templateName = StringUtils.substringAfter(query.getEndpoint(), "_template/");
        if (checkTemplateExists(templateName)) {
            // удаление существующего шаблона
            LOG.info("Delete existing template");
            execQuery(new IndexQuery(HttpMethod.DELETE, endpoint, null, null));
        }

        // добавление нового шаблона
        LOG.info("Add new template");
        super.execQuery(query);

        @SuppressWarnings("unchecked")
        Map<String, Object> settings = new HashMap<>((Map<String, Object>) body.get("settings"));
        @SuppressWarnings("unchecked")
        Map<String, Object> index = new HashMap<>((Map<String, Object>) settings.get("index"));
        if (!CollectionUtils.isEmpty(index)) {
            String pipeline = (String) index.get("default_pipeline");
            if (checkTemplatePipeline(pipeline)) {
                LOG.warn("Pipeline {} not found", pipeline);
            }
        }

        String aliasName = params.get(IndexQueryParam.ENTITY_NAME);
        Collection<String> indexNames = getIndexNames(aliasName);
        LOG.debug("Index names for alias {}", indexNames);
        Collection<String> indicesForReindex = new ArrayList<>();
        for (String indexName : indexNames) {
            try {
                // обновление параметров индекса
                LOG.info("Update settings for index {}", indexName);
                Settings indexSettings = getIndexSettings(indexName);
                Map<String, Object> filteredSettings = filterSettings(settings, indexSettings, "");
                if (!filteredSettings.isEmpty()) {
                    IndexQuery settingsQuery = new IndexQuery(HttpMethod.PUT, indexName + "/_settings", filteredSettings, Collections.emptyMap());
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Prepared request: {} {}\n{}", settingsQuery.getHttpMethod(), settingsQuery.getEndpoint(), mapper.writeValueAsString(settingsQuery.getJsonBody()));
                    }
                    execQuery(settingsQuery);
                } else {
                    LOG.info("Request settings are identical with index settings. Request skipped");
                }

                //обновление маппинга
                LOG.info("Update mappings for index {}", indexName);
                @SuppressWarnings("unchecked")
                Map<String, Object> mappings = new HashMap<>((Map<String, Object>) body.get("mappings"));
                Map<String, Object> indexMappings = getIndexMappings(indexName);
                if (LOG.isInfoEnabled()) {
                    LOG.info("Current index mappings : {}", mapper.writeValueAsString(indexMappings));
                }
                Map<String, Object> filteredMappings = filterMappings(mappings, indexMappings);
                if (!filteredMappings.isEmpty()) {
                    IndexQuery mappingsQuery = new IndexQuery(HttpMethod.PUT, indexName + "/_mappings", filteredMappings, Collections.emptyMap());
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Prepared request: {} {}\n{}", mappingsQuery.getHttpMethod(), mappingsQuery.getEndpoint(), mapper.writeValueAsString(mappingsQuery.getJsonBody()));
                    }
                    execQuery(mappingsQuery);
                } else {
                    LOG.info("Request mappings are identical with index mappings. Request skipped");
                }
            } catch (IOException e) {
                // ошибка в обновлении индекса не должна ломать обновление остальных
                LOG.warn("Index '{}' update finished with error and will be processed later using reindex api", indexName, e);
                indicesForReindex.add(indexName);
            }
        }
        if (!indicesForReindex.isEmpty()) {
            boolean skipReindex = BooleanUtils.toBoolean(params.get(IndexQueryParam.SKIP_REINDEX));
            if (skipReindex) {
                LOG.info("Indices {} weren't updated due to skip reindex setting activated", indicesForReindex);
            } else {
                // обновление индексов с перезаливкой данных
                for (String indexName : indicesForReindex) {
                    reindex(indexName);
                    restoreIndexSetting(indexName, index);
                }
            }
        }
    }

    private boolean checkTemplatePipeline(String pipeline) throws IOException {
        if (!Objects.isNull(pipeline)) {
            GetPipelineRequest request = new GetPipelineRequest(pipeline);
            GetPipelineResponse response = restClient.ingest().getPipeline(request, RequestOptions.DEFAULT);
            return response.status() != RestStatus.OK;
        }
        return false;
    }

    /**
     * Метод исключает из settingsMap настройки, значения которых совпадают со значениями в индексе.
     */
    private Map<String, Object> filterSettings(Map<String, Object> settingsMap, Settings indexSettings, String rootKey) {
        /* результат запроса настроек из Elasticsearch приходит с префиксом "index." для каждой настройки,
           даже если в шаблоне корневым тегом "index" не является
         */
        String indexSettingsRootKey = (StringUtils.isBlank(rootKey) || rootKey.startsWith("index")) ? rootKey : "index." + rootKey;

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : settingsMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String leafKey = indexSettingsRootKey + (!"".equals(indexSettingsRootKey) ? "." : "") + key;
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> filteredLeafValue = filterSettings((Map<String, Object>) value, indexSettings, leafKey);
                if (!filteredLeafValue.isEmpty()) {
                    result.put(key, filteredLeafValue);
                }
            } else {
                String valueString = value != null ? value.toString() : "";
                if (!valueString.equals(indexSettings.get(leafKey))) {
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    /**
     * Метод исключает из mappingsMap настройки маппинга, значения которых совпадают со значениями в индексе.
     */
    private Map<String, Object> filterMappings(Map<String, Object> mappingsMap, Map<String, Object> indexMappingsMap) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : mappingsMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> indexMappingsLeafMap = (Map<String, Object>) indexMappingsMap.get(key);
                if (indexMappingsLeafMap == null) {
                    indexMappingsLeafMap = Collections.emptyMap();
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> filteredLeafValue = filterMappings((Map<String, Object>) value, indexMappingsLeafMap);
                if (!filteredLeafValue.isEmpty()) {
                    result.put(key, filteredLeafValue);
                }
            } else if (value instanceof Number) {
                BigDecimal comparedValue = new BigDecimal(value.toString());
                Object indexMappingValue = indexMappingsMap.get(key);
                BigDecimal comparedIndexValue = indexMappingValue != null ? new BigDecimal(indexMappingValue.toString()) : BigDecimal.ZERO;
                if (comparedValue.compareTo(comparedIndexValue) != 0) {
                    result.put(key, value);
                }
            } else {
                Object indexMappingValue = indexMappingsMap.get(key);
                if (!Objects.equals(value, indexMappingValue)) {
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    private boolean checkTemplateExists(String templateName) throws IOException {
        IndexTemplatesExistRequest request = new IndexTemplatesExistRequest(templateName);
        return restClient.indices().existsTemplate(request, RequestOptions.DEFAULT);

    }

    private Settings getIndexSettings(String indexName) throws IOException {
        GetSettingsRequest request = new GetSettingsRequest().indices(indexName)
            .includeDefaults(true);
        GetSettingsResponse response = restClient.indices().getSettings(request, RequestOptions.DEFAULT);
        return response.getIndexToSettings().get(indexName);
    }

    private Map<String, Object> getIndexMappings(String indexName) throws IOException {
        GetMappingsRequest request = new GetMappingsRequest()
            .indices(indexName);
        GetMappingsResponse response = restClient.indices().getMapping(request, RequestOptions.DEFAULT);
        return response.mappings().get(indexName).sourceAsMap();
    }

    private void reindex(String indexName) throws IOException {
        LocalDateTime startTime = LocalDateTime.now();
        LOG.info("Start reindex for index '{}'. Start time = {}", indexName, startTime);

        String tmpIndexName = indexName + "_temporal";

        GetIndexRequest getTemporalIndexRequest = new GetIndexRequest(tmpIndexName);
        while (restClient.indices().exists(getTemporalIndexRequest, RequestOptions.DEFAULT)) {
            try {
                removeIndex(tmpIndexName);
                LOG.debug("Waiting for delete index '{}'", tmpIndexName);
                TimeUnit.SECONDS.sleep(2L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        createIndex(tmpIndexName);

        ReindexRequest firstRequest = new ReindexRequest()
            .setSourceIndices(indexName)
            .setDestIndex(tmpIndexName)
            .setSourceBatchSize(reindexBatchSize)
            .setSlices(reindexSlices);
        BulkByScrollResponse bulkResponse = restClient.reindex(firstRequest, RequestOptions.DEFAULT);

        // время индексации документа в Elasticsearch не более 1 сек
        // для надежности ждем 2 сек
        try {
            LOG.debug("Waiting for finish indexing '{}'", tmpIndexName);
            TimeUnit.SECONDS.sleep(2L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        RefreshRequest tmpIndexRefreshRequest = new RefreshRequest(tmpIndexName);
        restClient.indices().refresh(tmpIndexRefreshRequest, RequestOptions.DEFAULT);

        removeIndex(indexName);

        // от получения команды на удаление индекса до его фактического удаления проходит определенное время
        // нужно дождаться фактического удаления
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        while (restClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
            try {
                LOG.debug("Waiting for delete index '{}'", indexName);
                TimeUnit.SECONDS.sleep(2L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        createIndex(indexName);

        LocalDateTime firstStageFinishTime = LocalDateTime.now();
        LOG.info("First stage finished in {} seconds with response\n{}", ChronoUnit.SECONDS.between(startTime, firstStageFinishTime), bulkResponse);

        ReindexRequest secondRequest = new ReindexRequest()
            .setSourceIndices(tmpIndexName)
            .setDestIndex(indexName)
            .setSourceBatchSize(reindexBatchSize)
            .setSlices(reindexSlices);
        bulkResponse = restClient.reindex(secondRequest, RequestOptions.DEFAULT);

        RefreshRequest refreshRequest = new RefreshRequest(indexName);
        restClient.indices().refresh(refreshRequest, RequestOptions.DEFAULT);

        removeIndex(tmpIndexName);

        LocalDateTime finishTime = LocalDateTime.now();
        LOG.info("Second stage finished in {} seconds with response\n{}", ChronoUnit.SECONDS.between(firstStageFinishTime, finishTime), bulkResponse);
        LOG.info("Finish reindex for index '{}'. Finish time = {}. Duration = {} seconds",
            indexName, finishTime, ChronoUnit.SECONDS.between(startTime, finishTime));
    }

    private void removeIndex(String indexName) throws IOException {
        LOG.info("Remove index '{}'", indexName);
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        try {
            restClient.indices().delete(request, RequestOptions.DEFAULT);
        } catch (OpenSearchException e) {
            if (e.status() != RestStatus.NOT_FOUND) {
                throw e;
            }
        }
        LOG.info("Index '{}' successfully removed", indexName);
    }

    private void createIndex(String indexName) throws IOException {
        LOG.info("Create index '{}'", indexName);
        Settings.Builder settings = Settings.builder();
        if (replicasDisable) {
            settings.put(INDEX_PREFIX + "." + NUMBER_OF_REPLICAS_PARAM, 0);
        }
        if (refreshIntervalDisable) {
            settings.put(INDEX_PREFIX + "." + REFRESH_INTERVAL_PARAM, "-1");
        }
        if (translogDisable) {
            settings.put(INDEX_PREFIX + "." + TRANSLOG_DURABILITY_PARAM, "async");
        }
        Settings build = settings.build();
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(build);
        restClient.indices().create(request, RequestOptions.DEFAULT);
        LOG.info("Index '{}' successfully created, setting '{}'", indexName, build);
    }

    private void restoreIndexSetting(String indexName, Map<String, Object> indexSettings) throws IOException {
        Settings currentIndexSettings = getIndexSettings(indexName);
        Map<String, Object> filterSettings = filterSettings(indexSettings, currentIndexSettings, "index");
        if (refreshIntervalDisable) {
            filterSettings.put(REFRESH_INTERVAL_PARAM, indexSettings.get(REFRESH_INTERVAL_PARAM));
        }
        if (translogDisable) {
            filterSettings.put(TRANSLOG_DURABILITY_PARAM, indexSettings.get(TRANSLOG_DURABILITY_PARAM));
        }
        if (replicasDisable) {
            filterSettings.put(NUMBER_OF_REPLICAS_PARAM, indexSettings.get(NUMBER_OF_REPLICAS_PARAM));
        }
        if (!filterSettings.isEmpty()) {
            UpdateSettingsRequest request = new UpdateSettingsRequest(indexName);
            request.settings(filterSettings);
            restClient.indices().putSettings(request, RequestOptions.DEFAULT);
            LOG.info("Index '{}' successfully changed the setting '{}'", indexName, filterSettings);
        }
    }
}
