package ru.sberbank.pprb.sbbol.global_search.search.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.EntityType;
import ru.sberbank.pprb.sbbol.global_search.search.model.Partner;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchableEntity;

import java.util.Collection;
import java.util.List;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SearchMapper {

    default Class<? extends BaseSearchableEntity> toSearchableClass(EntityType type) {
        return switch (type) {
            case COUNTERPARTY -> Partner.class;
            default -> throw new IllegalArgumentException();
        };
    }

    static EntityType toSearchableEntityType(Class<?> clazz) {
        if (Partner.class == clazz) {
            return EntityType.COUNTERPARTY;
        }
        return null;
    }

    static <T> List<SearchableEntity> toSearchEntity(Collection<T> entities) {
        return null;
    }
}
