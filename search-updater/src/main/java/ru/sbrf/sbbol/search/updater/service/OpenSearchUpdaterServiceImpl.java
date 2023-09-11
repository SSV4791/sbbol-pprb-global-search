package ru.sbrf.sbbol.search.updater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.opensearch.OpenSearchException;
import org.opensearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.opensearch.client.GetAliasesResponse;
import org.opensearch.client.Request;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.Response;
import org.opensearch.client.ResponseException;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import ru.sbrf.sbbol.search.updater.query.IndexQuery;
import ru.sbrf.sbbol.search.updater.query.IndexQueryFile;
import ru.sbrf.sbbol.search.updater.query.IndexQueryParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса проливки обновлений в Elasticsearch
 */
public class OpenSearchUpdaterServiceImpl implements OpenSearchUpdaterService {

    protected final RestHighLevelClient restClient;

    protected final ObjectMapper mapper;

    private UpdaterQueriesIndexService queriesIndexService;

    private static final Logger LOG = LoggerFactory.getLogger(OpenSearchUpdaterServiceImpl.class);

    public OpenSearchUpdaterServiceImpl(RestHighLevelClient restClient, ObjectMapper mapper, UpdaterQueriesIndexService queriesIndexService) {
        this.restClient = restClient;
        this.mapper = mapper;
        this.queriesIndexService = queriesIndexService;
    }

    @Override
    public final void process(IndexQueryFile queryFile) {
        LOG.info("Start processing file '{}'", queryFile.getName());
        try {
            for (IndexQuery query : queryFile.getData().getRequests()) {
                Map<IndexQueryParam, String> params = query.getParams();
                HttpMethod method = query.getHttpMethod();
                String endpoint = query.getEndpoint();
                Map<String, Object> body = query.getJsonBody();
                if (LOG.isInfoEnabled()) {
                    LOG.info("Start processing request : {} {}\nwith body : {}\nwith params : {}",
                        method, endpoint, mapper.writeValueAsString(body), mapper.writeValueAsString(params));
                }
                processQuery(query);
            }
            queriesIndexService.updateQueryInfo(queryFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        LOG.info("Finish processing file '{}'", queryFile.getName());

    }

    /**
     * Выполнить один скрипт обновления
     * <p>
     * Может быть переопределен в наследниках
     *
     * @param query скрипт обновления Elasticsearch
     */
    protected void processQuery(IndexQuery query) throws IOException {
        Map<IndexQueryParam, String> params = query.getParams();
        String aliasName = params.get(IndexQueryParam.ENTITY_NAME);
        if (aliasName != null) {
            LOG.info("Alias_name {}", aliasName);
            Collection<String> indexNames = getIndexNames(aliasName);
            LOG.debug("Index names for alias {}", indexNames);
            for (String indexName : indexNames) {
                String endpoint = StringUtils.replaceIgnoreCase(query.getEndpoint(), aliasName, indexName);
                IndexQuery indexQuery = new IndexQuery(query.getHttpMethod(), endpoint, query.getJsonBody(), Collections.emptyMap());
                if (LOG.isInfoEnabled()) {
                    LOG.info("Prepared request: {} {}\n{}", indexQuery.getHttpMethod(), indexQuery.getEndpoint(), mapper.writeValueAsString(indexQuery.getJsonBody()));
                }
                execQuery(indexQuery);
            }
        } else {
            execQuery(query);
        }
    }

    protected final Response execQuery(IndexQuery query) throws IOException {
        Set<RestStatus> ignoreErrors;
        Map<IndexQueryParam, String> params = query.getParams();
        if (params != null) {
            String ignoreErrorsStr = StringUtils.defaultString(params.get(IndexQueryParam.IGNORE_ERRORS)).toUpperCase();
            ignoreErrors = Arrays.stream(ignoreErrorsStr.split(","))
                .filter(x -> !x.isEmpty())
                .map(RestStatus::valueOf)
                .collect(Collectors.toSet());
        } else {
            ignoreErrors = Collections.emptySet();
        }
        Request request = new Request(query.getHttpMethod().name(), query.getEndpoint());
        Map<String, Object> jsonBody = query.getJsonBody();
        if (jsonBody != null) {
            String body = mapper.writeValueAsString(jsonBody);
            request.setEntity(new NStringEntity(body, ContentType.APPLICATION_JSON));
        }
        LOG.debug("Elasticsearch request = {}", request);
        Response response = null;
        try {
            response = restClient.getLowLevelClient().performRequest(request);
            LOG.debug("Elasticsearch response = {}", response);
        } catch (OpenSearchException e) {
            if (ignoreErrors.contains(e.status())) {
                LOG.warn("Script execution error '{}' ignored", e.getDetailedMessage());
            } else {
                throw e;
            }
        } catch (ResponseException e) {
            Set<Integer> ignoreErrorCodes = ignoreErrors.stream()
                .map(RestStatus::getStatus)
                .collect(Collectors.toSet());
            if (ignoreErrorCodes.contains(e.getResponse().getStatusLine().getStatusCode())) {
                LOG.warn("Script execution error '{}' ignored due to script params", e.getMessage());
            } else {
                throw e;
            }
        }
        return response;
    }

    protected final Collection<String> getIndexNames(String aliasName) throws IOException {
        Collection<String> result = new ArrayList<>();
        GetAliasesRequest request = new GetAliasesRequest(aliasName);
        GetAliasesResponse response = restClient.indices().getAlias(request, RequestOptions.DEFAULT);
        Collection<String> responseIndexNames = response.getAliases().keySet();
        if (!CollectionUtils.isEmpty(responseIndexNames)) {
            result.addAll(responseIndexNames);
        }
        return result;
    }
}
