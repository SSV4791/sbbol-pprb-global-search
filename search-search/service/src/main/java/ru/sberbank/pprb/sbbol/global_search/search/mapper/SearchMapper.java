package ru.sberbank.pprb.sbbol.global_search.search.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import ru.sberbank.pprb.sbbol.global_search.engine.query.QueryResult;
import ru.sberbank.pprb.sbbol.global_search.search.model.Account;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.EntityType;
import ru.sberbank.pprb.sbbol.global_search.search.model.Partner;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchResponse;

import java.util.Collection;

@Mapper
@DecoratedWith(SearchMapperDecorator.class)
public interface SearchMapper {

    default Class<? extends BaseSearchableEntity> toSearchableClass(EntityType type) {
        return switch (type) {
            case PARTNER -> Partner.class;
            case ACCOUNT -> Account.class;
            default -> throw new IllegalArgumentException("Отсутствует класс для типа " + type.getValue());
        };
    }

    default SearchResponse createResultResponse(Collection<QueryResult<?>> searchResults) {
        return new SearchResponse();
    }
}
