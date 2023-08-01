
package ru.sberbank.pprb.sbbol.global_search.search.model;


import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.sberbank.pprb.sbbol.global_search.core.common.DefaultIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategyRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Queryable;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.DigitalIdRestriction;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.RestrictedAccess;

@SearchableEntity(
    name = Counterparty.ENTITY_NAME,
    indexNameResolvingStrategy = @IndexNameResolvingStrategyRef(type = DefaultIndexNameResolvingStrategy.class)
)
@RestrictedAccess(mandatoryRestrictions = DigitalIdRestriction.class)
@EqualsAndHashCode(callSuper = false)
@ToString
public class Counterparty extends BaseSearchableEntity {

    static final String ENTITY_NAME = "counterparty";

    private String digitalId;

    @Queryable
    private String name;

    @Queryable
    private String account;

    @Queryable
    private String inn;

    @Queryable
    private String kpp;

    @Queryable
    private String bankBic;

    private String bankAccount;
}
