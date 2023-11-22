package ru.sberbank.pprb.sbbol.global_search.search.mapper;

import java.util.Map;

public class SearchableEntityMapperRegistry {

    private final Map<Class<?>, SearchableEntityMapper> mappers;

    public SearchableEntityMapperRegistry(Map<Class<?>, SearchableEntityMapper> mappers) {
        this.mappers = mappers;
    }

    public SearchableEntityMapper getMapper(Class<?> entityClass) {
        return mappers.get(entityClass);
    }
}
