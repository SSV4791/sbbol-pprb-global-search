package ru.sberbank.pprb.sbbol.global_search.search;

import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntityService;
import ru.sberbank.pprb.sbbol.global_search.engine.query.EntityQuery;
import ru.sberbank.pprb.sbbol.global_search.engine.query.QueryResult;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.Condition;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchFilter;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchResponse;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchableEntityType;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.Restriction;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.RestrictionConverter;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.RestrictionConverterFactory;
import ru.sberbank.pprb.sbbol.global_search.search.service.SearchService;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса полнотекстового поиска с использованием OpenSearch в качестве хранилища
 */
public class SearchServiceImpl implements SearchService {

    private final SearchableEntityService searchableEntityService;
    private final RestrictionConverterFactory restrictionConverterFactory;

    public SearchServiceImpl(SearchableEntityService searchableEntityService, RestrictionConverterFactory restrictionConverterFactory) {
        this.searchableEntityService = searchableEntityService;
        this.restrictionConverterFactory = restrictionConverterFactory;
    }

    @Override
    public SearchResponse find(SearchFilter filter) throws IOException {
        Map<SearchableEntityType, Collection<Restriction>> entityTypeRestrictions = filter.getEntityTypeRestrictions();
        if (!CollectionUtils.isEmpty(entityTypeRestrictions)) {
            List<EntityQuery<?>> queries = new ArrayList<>(entityTypeRestrictions.entrySet().size());
            for (Map.Entry<SearchableEntityType, Collection<Restriction>> entry : entityTypeRestrictions.entrySet()) {
                SearchableEntityType entityType = entry.getKey();
                Collection<Restriction> restrictions = entry.getValue() != null ? entry.getValue() : Collections.emptyList();
                Class<?> entityClass = entityType.getEntityClass();
                checkRestrictions(entityType, restrictions, entityClass);
                Collection<Condition> conditions = getRestrictions(restrictions);

                EntityQuery<?> entityQuery = EntityQuery.builder(entityClass)
                    .queryString(filter.getQuery())
                    .maxResultCount(filter.getSize())
                    .startSearchFrom(filter.getOffset())
                    .conditions(conditions)
                    .build();
                queries.add(entityQuery);
            }
            Collection<QueryResult<?>> searchResults = searchableEntityService.find(queries);
            createServerResultResponse(new SearchResponse(), searchResults);
        }
        return new SearchResponse();
    }

    private void checkRestrictions(SearchableEntityType entityType, Collection<Restriction> restrictions, Class<?> entityClass) {
//        RestrictionCheckResult restrictionCheckResult = restrictionService.checkFilterEntityRestrictions(restrictions, entityClass);
//        if (restrictionCheckResult == RestrictionCheckResult.NOT_ENOUGH_MANDATORY_RESTRICTIONS) {
//            throw new IllegalArgumentException("Фильтр поискового запроса для сущности '" + entityType.name() +
//                "' содержит не все обязательные ограничения");
//        } else if (restrictionCheckResult == RestrictionCheckResult.UNKNOWN_RESTRICTION) {
//            throw new IllegalArgumentException("Фильтр поискового запроса для сущности '" + entityType.name() +
//                "' содержит недопустимый тип ограничения");
//        }
    }

    private List<Condition> getRestrictions(Collection<Restriction> restrictions) {
        return restrictions.stream()
            .map(restriction -> {
                RestrictionConverter converter = restrictionConverterFactory.getConverter(restriction.getClass());
                return converter.getCondition(restriction);
            })
            .collect(Collectors.toList());
    }

    private void createServerResultResponse(SearchResponse response, Collection<QueryResult<?>> searchResults) {
        response.setEntityId(UUID.randomUUID());
//        response.getServerResult().setResults(
//            searchResults.stream()
//                .map(this::map)
//                .collect(Collectors.toList())
//        );
    }
}
