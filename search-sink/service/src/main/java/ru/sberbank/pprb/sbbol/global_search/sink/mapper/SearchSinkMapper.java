package ru.sberbank.pprb.sbbol.global_search.sink.mapper;

import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;

public interface SearchSinkMapper {

    <T extends BaseSearchableEntity> InternalEntityHolder<T> map(T searchableEntity);
}
