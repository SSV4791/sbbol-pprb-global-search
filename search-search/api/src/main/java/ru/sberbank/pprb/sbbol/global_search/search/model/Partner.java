
package ru.sberbank.pprb.sbbol.global_search.search.model;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sberbank.pprb.sbbol.global_search.core.common.DefaultIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategyRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Queryable;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.RestrictedAccess;

@SearchableEntity(
    name = Partner.ENTITY_NAME,
    indexNameResolvingStrategy = @IndexNameResolvingStrategyRef(type = DefaultIndexNameResolvingStrategy.class)
)
@RestrictedAccess(mandatoryRestrictions = DigitalIdRestriction.class)
@EqualsAndHashCode(callSuper = false)
@ToString
public class Partner extends BaseSearchableEntity {

    static final String ENTITY_NAME = "partner";

    private String digitalId;

    @Queryable
    private String name;

    @Queryable
    private String account;

    @Queryable
    private String inn;
}
