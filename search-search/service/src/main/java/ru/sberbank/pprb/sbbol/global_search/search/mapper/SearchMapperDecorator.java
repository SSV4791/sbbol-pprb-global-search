package ru.sberbank.pprb.sbbol.global_search.search.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.sberbank.pprb.sbbol.global_search.engine.query.QueryResult;
import ru.sberbank.pprb.sbbol.global_search.search.model.Account;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.EntityType;
import ru.sberbank.pprb.sbbol.global_search.search.model.Partner;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchResponse;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.ServerSearchResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SearchMapperDecorator implements SearchMapper {

    @Autowired
    private SearchableEntityMapperRegistry mapperRegistry;

    @Override
    public SearchResponse createResultResponse(Collection<QueryResult<?>> searchResults) {
        var response = new SearchResponse();
        return response.results(
            searchResults.stream()
                .map(this::mapToServerSearchResult)
                .collect(Collectors.toList())
        );
    }

    private ServerSearchResult mapToServerSearchResult(QueryResult<?> queryResult) {
        var entityClass = queryResult.getEntityClass();
        return new ServerSearchResult()
            .entityType(toSearchableEntityType(queryResult.getEntityClass()))
            .entities(toSearchEntity(entityClass, queryResult.getEntities()))
            .duration(queryResult.getDuration())
            .resultCount(queryResult.getTotalResultCount());
    }

    private EntityType toSearchableEntityType(Class<?> clazz) {
        if (Partner.class == clazz) {
            return EntityType.PARTNER;
        }
        if (Account.class == clazz) {
            return EntityType.ACCOUNT;
        }
        throw new IllegalArgumentException("Отсутствует EntityType для класса " + clazz.getName());
    }

    private List<SearchableEntity> toSearchEntity(Class<?> entityClass, Collection<?> entities) {
        var result = new ArrayList<SearchableEntity>(entities.size());
        var mapper = mapperRegistry.getMapper(entityClass);
        for (var entity : entities) {
            result.add(mapper.toSearchableEntity(entity));
        }
        return result;
    }
}
