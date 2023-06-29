package ru.sberbank.pprb.sbbol.global_search.core.common;

import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;

/**
 * Стратегия определения наименования индекса, возвращающая в качестве наименования индекса наименование сущности
 */
public class DefaultIndexNameResolvingStrategy implements IndexNameResolvingStrategy<Object> {
    @Override
    public String getIndexName(Object entity) {
        return entity.getClass().getAnnotation(SearchableEntity.class).name() + "_idx";
    }
}
