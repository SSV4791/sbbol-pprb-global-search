package ru.sberbank.pprb.sbbol.global_search.search.service;

import lombok.SneakyThrows;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntityService;
import ru.sberbank.pprb.sbbol.global_search.engine.query.EntityQuery;
import ru.sberbank.pprb.sbbol.global_search.engine.query.QueryResult;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.Condition;
import ru.sberbank.pprb.sbbol.global_search.search.mapper.SearchMapper;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.EntityRestrictions;
import ru.sberbank.pprb.sbbol.global_search.search.model.EntityType;
import ru.sberbank.pprb.sbbol.global_search.search.model.Pagination;
import ru.sberbank.pprb.sbbol.global_search.search.model.Restriction;
import ru.sberbank.pprb.sbbol.global_search.search.model.RestrictionCheckResult;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchFilter;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchResponse;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.RestrictionConverter;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.RestrictionConverterFactory;
import ru.sberbank.pprb.sbbol.global_search.search.service.restrictions.RestrictionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса полнотекстового поиска с использованием OpenSearch в качестве хранилища
 */
public class SearchServiceImpl implements SearchService {

    private final SearchableEntityService searchableEntityService;
    private final RestrictionConverterFactory restrictionConverterFactory;
    private final RestrictionService restrictionService;
    private final SearchMapper searchMapper;


    public SearchServiceImpl(
        SearchableEntityService searchableEntityService,
        RestrictionConverterFactory restrictionConverterFactory,
        RestrictionService restrictionService,
        SearchMapper searchMapper
    ) {
        this.searchableEntityService = searchableEntityService;
        this.restrictionConverterFactory = restrictionConverterFactory;
        this.restrictionService = restrictionService;
        this.searchMapper = searchMapper;
    }

    @SneakyThrows
    @Override
    public SearchResponse find(SearchFilter filter) {
        List<EntityRestrictions> entityRestrictions = filter.getEntityRestrictions();
        if (!CollectionUtils.isEmpty(entityRestrictions)) {
            List<EntityQuery<?>> queries = new ArrayList<>(entityRestrictions.size());
            for (EntityRestrictions entityRestriction : entityRestrictions) {
                EntityType entityType = entityRestriction.getEntityType();
                List<Restriction> restrictions = entityRestriction.getRestriction();
                Class<? extends BaseSearchableEntity> entityClass = searchMapper.toSearchableClass(entityType);
                checkRestrictions(entityType, restrictions, entityClass);
                Collection<Condition> conditions = getRestrictions(restrictions);
                Pagination pagination = filter.getPagination();
                EntityQuery<?> entityQuery = EntityQuery.builder(entityClass)
                    .queryString(filter.getQuery())
                    .maxResultCount(pagination.getSize())
                    .startSearchFrom(pagination.getOffset())
                    .conditions(conditions)
                    .build();
                queries.add(entityQuery);
            }
            Collection<QueryResult<?>> searchResults = searchableEntityService.find(queries);
            return searchMapper.createResultResponse(searchResults);
        }
        return new SearchResponse();
    }

    private void checkRestrictions(EntityType entityType, List<Restriction> restrictions, Class<? extends BaseSearchableEntity> entityClass) {
        RestrictionCheckResult restrictionCheckResult = restrictionService.checkFilterEntityRestrictions(restrictions, entityClass);
        if (restrictionCheckResult == RestrictionCheckResult.NOT_ENOUGH_MANDATORY_RESTRICTIONS) {
            throw new IllegalArgumentException("Фильтр поискового запроса для сущности '" + entityType.name() +
                "' содержит не все обязательные ограничения");
        } else if (restrictionCheckResult == RestrictionCheckResult.UNKNOWN_RESTRICTION) {
            throw new IllegalArgumentException("Фильтр поискового запроса для сущности '" + entityType.name() +
                "' содержит недопустимый тип ограничения");
        }
    }

    private List<Condition> getRestrictions(Collection<Restriction> restrictions) {
        return restrictions.stream()
            .map(restriction -> {
                RestrictionConverter converter = restrictionConverterFactory.getConverter(restriction.getClass());
                return converter.getCondition(restriction);
            })
            .collect(Collectors.toList());
    }
}
