package ru.sberbank.pprb.sbbol.global_search.sink.mapper.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanProvider;
import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.sink.mapper.SearchSinkMapper;

public class SearchSinkMapperImpl implements SearchSinkMapper {

    @Autowired
    private BeanProvider beanProvider;

    public <T extends BaseSearchableEntity> InternalEntityHolder<T> map(T searchableEntity) {
        return InternalEntityHolder.builder(
                (Class<T>) searchableEntity.getClass(),
                getEntityName(searchableEntity),
                searchableEntity,
                getIndexName(searchableEntity),
                searchableEntity.getEntityId().toString()
            )
            .build();
    }

    private <T extends BaseSearchableEntity> String getEntityName(T searchableEntity) {
        return searchableEntity.getClass().getAnnotation(SearchableEntity.class).name();
    }

    private <T extends BaseSearchableEntity> String getIndexName(T searchableEntity) {
        var indexNameResolvingStrategyRef = searchableEntity.getClass().getAnnotation(SearchableEntity.class).indexNameResolvingStrategy();
        var indexNameResolvingStrategy =  beanProvider.getBean(indexNameResolvingStrategyRef.type()).getBean();
        return indexNameResolvingStrategy.getIndexName(searchableEntity);
    }

}
