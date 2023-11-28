package ru.sberbank.pprb.sbbol.global_search.search.mapper;

import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchableEntity;

public interface SearchableEntityMapper<T extends BaseSearchableEntity, R extends SearchableEntity> {
    Class<T> getEntityClass();

    R toSearchableEntity(Object entity);
}
