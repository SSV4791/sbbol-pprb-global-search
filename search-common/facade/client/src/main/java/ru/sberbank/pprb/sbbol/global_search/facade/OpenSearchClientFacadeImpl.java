package ru.sberbank.pprb.sbbol.global_search.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.OpenSearchStatusException;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.MultiSearchRequest;
import org.opensearch.action.search.MultiSearchResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.support.IndicesOptions;
import org.opensearch.action.support.WriteRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.document.DocumentField;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.engine.VersionConflictEngineException;
import org.opensearch.index.mapper.RoutingFieldMapper;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.rest.RestStatus;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;
import ru.sberbank.pprb.sbbol.global_search.facade.query.SearchFilterTokenExtractor;
import ru.sberbank.pprb.sbbol.global_search.facade.query.condition.Condition;
import ru.sberbank.pprb.sbbol.global_search.facade.query.sort.QuerySorting;
import ru.sberbank.pprb.sbbol.global_search.facade.search.InternalEntitySearchQuery;
import ru.sberbank.pprb.sbbol.global_search.facade.search.InternalEntitySearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Реализация сервиса взаимодействия с хранилищем OpenSearch.
 * Использует OpenSearch Java High Level Rest Client
 */
public class OpenSearchClientFacadeImpl implements OpenSearchClientFacade {

    private final RestHighLevelClient restClient;

    private final SearchFilterTokenExtractor tokenExtractor;

    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(OpenSearchClientFacadeImpl.class);

    public OpenSearchClientFacadeImpl(RestHighLevelClient restClient,
                                      SearchFilterTokenExtractor tokenExtractor,
                                      ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.tokenExtractor = tokenExtractor;
        this.objectMapper = objectMapper;
    }

    @Override
    public Collection<InternalEntitySearchResult<?>> findAll(List<InternalEntitySearchQuery<?>> entitySearchQueries) throws IOException {
        log.debug("Выполняется коллекция поисковых запросов: {}", entitySearchQueries);
        List<InternalEntitySearchResult<?>> result = new ArrayList<>(entitySearchQueries.size());
        MultiSearchRequest request = createMultiRequest(entitySearchQueries);
        if (request != null) {
            log.trace("Запрос преобразованный к виду OpenSearch: {}", request.requests());
            MultiSearchResponse response = restClient.msearch(request, RequestOptions.DEFAULT);
            log.trace("Результат запроса в OpenSearch: {}", response);
            MultiSearchResponse.Item[] items = response.getResponses();
            for (int i = 0; i < items.length; i++) {
                // Порядок результатов соответствует порядку запросов, поэтому можем связать их по индексу
                // Подробнее в javadoc'е org.opensearch.action.search.MultiSearchRequest#add
                MultiSearchResponse.Item item = items[i];
                InternalEntitySearchQuery<?> entitySearchQuery = entitySearchQueries.get(i);
                Class<?> entityClass = entitySearchQuery.getEntityClass();
                String entityName = entitySearchQuery.getEntityName();
                InternalEntitySearchResult<?> entitySearchResult;
                Exception failure = item.getFailure();
                if (failure == null) {
                    try {
                        entitySearchResult = mapSearchResponse(item.getResponse(), entityClass, entityName);
                    } catch (Exception e) {
                        entitySearchResult = InternalEntitySearchResult.failure(entityClass, e);
                    }
                } else {
                    entitySearchResult = InternalEntitySearchResult.failure(entityClass, failure);
                }
                result.add(entitySearchResult);
            }
        }
        log.debug("Результат выполнения запроса: {}", result);
        return result;
    }

    @Override
    public <T> InternalEntitySearchResult<T> find(InternalEntitySearchQuery<T> query) {
        log.debug("Выполняется поисковый запрос: {}", query);
        InternalEntitySearchResult<T> result;
        SearchRequest request = createSearchRequest(query);
        Class<T> entityClass = query.getEntityClass();
        log.trace("Запрос преобразованный к виду OpenSearch: {}", request);
        try {
            SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
            log.trace("Результат запроса в OpenSearch: {}", response);
            result = mapSearchResponse(response, entityClass, query.getEntityName());
        } catch (IOException e) {
            result = InternalEntitySearchResult.failure(entityClass, e);
        }
        log.debug("Результат выполнения запроса: {}", result);
        return result;
    }

    @Override
    public <T> ActionResultType save(InternalEntityHolder<T> entityHolder) throws IOException {
        return saveInternal(entityHolder, false, false);
    }

    @Override
    public <T> ActionResultType create(InternalEntityHolder<T> entityHolder, boolean withRefresh) throws IOException {
        return saveInternal(entityHolder, true, withRefresh);
    }

    @Override
    public <T> InternalEntityHolder<T> get(Class<T> entityClass, String entityName, String entityId, String routingValue) throws IOException {
        log.debug("Выполняется запрос получения сущности: [entityClass = {}, entityName = {}, entityId = {}, routingValue = {}]",
            entityClass, entityName, entityId, routingValue);
        SearchRequest request = new SearchRequest(entityName)
            .indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN)
            .source(new SearchSourceBuilder()
                .seqNoAndPrimaryTerm(Boolean.TRUE)
                .query(Condition.id(entityId).toQueryBuilder()));
        if (routingValue != null) {
            request.routing(routingValue);
        }
        log.trace("Запрос преобразованный к виду OpenSearch: {}", request);
        SearchResponse response = restClient.search(request, RequestOptions.DEFAULT);
        log.trace("Результат запроса в OpenSearch: {}", response);

        SearchHits responseHits = response.getHits();
        long totalResultCount = responseHits.getTotalHits() != null ? responseHits.getTotalHits().value : 0;
        if (totalResultCount == 0) {
            return null;
        } else if (totalResultCount > 1) {
            throw new RuntimeException("Найдено более одного результата [entityName = " + entityName + ", entityId = " + entityId + "]");
        } else {
            SearchHit searchResult = responseHits.getAt(0);
            DocumentField routingField = searchResult.field(RoutingFieldMapper.NAME);
            InternalEntityHolder<T> result = InternalEntityHolder
                .builder(entityClass, entityName, objectMapper.readValue(searchResult.getSourceAsString(), entityClass), searchResult.getIndex(), entityId)
                .seqNo(searchResult.getSeqNo())
                .primaryTerm(searchResult.getPrimaryTerm())
                .routingValue(routingField != null ? routingField.getValue() : routingValue)
                .build();
            log.debug("Результат выполнения запроса: {}", result);
            return result;
        }
    }

    @Override
    public <T> InternalEntityHolder<T> get(Class<T> entityClass, String entityName, String indexName, String entityId, String routingValue) throws IOException {
        log.debug("Выполняется запрос получения сущности: [entityClass = {}, entityName = {}, indexName = {}, entityId = {}, routingValue = {}]",
            entityClass, entityName, indexName, entityId, routingValue);
        GetRequest request = new GetRequest(indexName, entityId);
        if (routingValue != null) {
            request.routing(routingValue);
        }
        log.trace("Запрос преобразованный к виду OpenSearch: {}", request);
        try {
            GetResponse response = restClient.get(request, RequestOptions.DEFAULT);
            log.trace("Результат запроса в OpenSearch: {}", response);
            if (response.isExists()) {
                DocumentField routingField = response.getField(RoutingFieldMapper.NAME);
                InternalEntityHolder<T> result = InternalEntityHolder
                    .builder(entityClass, entityName, objectMapper.readValue(response.getSourceAsString(), entityClass), indexName, entityId)
                    .seqNo(response.getSeqNo())
                    .primaryTerm(response.getPrimaryTerm())
                    .routingValue(routingField != null ? routingField.getValue() : routingValue)
                    .build();
                log.debug("Результат выполнения запроса: {}", result);
                return result;
            }
        } catch (OpenSearchStatusException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                log.warn("Попытка получения объекта для несуществующего индекса [entityClass = {}, entityName = {}, indexName = {}, entityId = {}, routingValue = {}]",
                    entityClass, entityName, indexName, entityId, routingValue);
            } else {
                throw e;
            }
        }
        return null;
    }

    @Override
    public <T> void delete(InternalEntityHolder<T> entityHolder) throws IOException {
        log.debug("Выполняется запрос удаления сущности: {}", entityHolder);
        DeleteRequest request = createDeleteRequest(entityHolder);
        log.trace("Запрос преобразованный к виду OpenSearch: {}", request);
        try {
            DeleteResponse response = restClient.delete(request, RequestOptions.DEFAULT);
            log.trace("Результат запроса в OpenSearch: {}", response);
        } catch (OpenSearchStatusException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                log.warn("Попытка удаления объекта из несуществующего индекса [entityClass = {}, entityName = {}, indexName = {}, entityId = {}, routingValue = {}]",
                    entityHolder.getEntityClass(), entityHolder.getEntityName(), entityHolder.getIndexName(), entityHolder.getEntityId(), entityHolder.getRoutingValue());
            } else {
                throw e;
            }

        }
    }

    private <T> ActionResultType saveInternal(InternalEntityHolder<T> entityHolder, boolean create, boolean withRefresh) throws IOException {
        if (log.isDebugEnabled()) {
            if (create) {
                log.debug("Выполняется запрос сохранения новой сущности: {}", entityHolder);
            } else {
                log.debug("Выполняется запрос сохранения сущности: {}", entityHolder);
            }
        }
        IndexRequest request = createIndexRequest(entityHolder, withRefresh)
            .create(create);
        log.trace("Запрос преобразованный к виду OpenSearch: {}", request);
        try {
            IndexResponse response = restClient.index(request, RequestOptions.DEFAULT);
            log.trace("Результат запроса в OpenSearch: {}", response);
            return ActionResultType.SUCCESS;
        } catch (VersionConflictEngineException e) {
            return ActionResultType.VERSION_CONFLICT;
        } catch (OpenSearchStatusException e) {
            if (e.status() == RestStatus.CONFLICT) {
                return ActionResultType.VERSION_CONFLICT;
            } else {
                throw e;
            }
        }
    }

    private <T> IndexRequest createIndexRequest(InternalEntityHolder<T> entityHolder, boolean withRefresh) throws IOException {
        String indexName = entityHolder.getIndexName();
        String entityId = entityHolder.getEntityId();
        T entity = entityHolder.getEntity();
        String routingValue = entityHolder.getRoutingValue();
        Long seqNo = entityHolder.getSeqNo();
        Long primaryTerm = entityHolder.getPrimaryTerm();
        IndexRequest request = new IndexRequest(indexName)
            .id(entityId)
            .source(objectMapper.writeValueAsString(entity), XContentType.JSON);
        if (routingValue != null) {
            request.routing(routingValue);
        }
        if (seqNo != null) {
            request.setIfSeqNo(seqNo);
        }
        if (primaryTerm != null) {
            request.setIfPrimaryTerm(primaryTerm);
        }
        if (withRefresh) {
            request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        }
        return request;
    }

    private <T> DeleteRequest createDeleteRequest(InternalEntityHolder<T> entityHolder) {
        DeleteRequest request = new DeleteRequest(entityHolder.getIndexName())
            .id(entityHolder.getEntityId())
            .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        String routingValue = entityHolder.getRoutingValue();
        if (routingValue != null) {
            request.routing(routingValue);
        }
        return request;
    }

    private MultiSearchRequest createMultiRequest(List<InternalEntitySearchQuery<?>> entitySearchQueries) {
        if (CollectionUtils.isEmpty(entitySearchQueries)) {
            return null;
        }
        MultiSearchRequest multiSearchRequest = new MultiSearchRequest();
        for (InternalEntitySearchQuery<?> query : entitySearchQueries) {
            SearchRequest request = createSearchRequest(query);
            multiSearchRequest.add(request);
        }
        return multiSearchRequest;
    }

    private <T> SearchRequest createSearchRequest(InternalEntitySearchQuery<T> query) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        for (Condition condition : query.getConditions()) {
            QueryBuilder queryBuilder = condition.toQueryBuilder();
            builder.filter(queryBuilder);
        }
        Collection<String> queryableFields = query.getQueryableFields();
        if (!queryableFields.isEmpty()) {
            Collection<String> tokens = tokenExtractor.extract(query);
            if (!tokens.isEmpty()) {
                builder.minimumShouldMatch(tokens.size());
                for (String token : tokens) {
                    builder.should(new MultiMatchQueryBuilder(token, queryableFields.toArray(new String[0])));
                }
            }
        }
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
            .seqNoAndPrimaryTerm(Boolean.TRUE)
            .size(query.getMaxResultCount())
            .from(query.getStartSearchFrom())
            .query(builder);

        for (QuerySorting querySorting : query.getQuerySorting()) {
            sourceBuilder.sort(querySorting.toSortBuilder());
        }

        Collection<Object> searchAfterValues = query.getSearchAfterValues();
        if (!CollectionUtils.isEmpty(searchAfterValues)) {
            sourceBuilder.searchAfter(query.getSearchAfterValues().toArray());
        }

        SearchRequest result = new SearchRequest(query.getEntityName())
            .indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN)
            .source(sourceBuilder);
        Collection<String> routingValues = query.getRoutingValues();
        if (!CollectionUtils.isEmpty(routingValues)) {
            result.routing(routingValues.toArray(new String[0]));
        }
        return result;
    }

    private <T> InternalEntitySearchResult<T> mapSearchResponse(SearchResponse response, Class<T> entityClass, String entityName) throws IOException {
        SearchHits responseHits = response.getHits();
        SearchHit[] hits = responseHits.getHits();
        Collection<InternalEntityHolder<T>> entities = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            DocumentField routingField = hit.field(RoutingFieldMapper.NAME);
            entities.add(
                InternalEntityHolder
                    .builder(entityClass, entityName, objectMapper.readValue(hit.getSourceAsString(), entityClass), hit.getIndex(), hit.getId())
                    .seqNo(hit.getSeqNo())
                    .primaryTerm(hit.getPrimaryTerm())
                    .routingValue(routingField != null ? routingField.getValue() : null)
                    .build());
        }
        return InternalEntitySearchResult.builder(entityClass)
            .entities(entities)
            .duration(response.getTook().getMillis())
            .totalResultCount(responseHits.getTotalHits() != null ? responseHits.getTotalHits().value : 0)
            .build();
    }
}
