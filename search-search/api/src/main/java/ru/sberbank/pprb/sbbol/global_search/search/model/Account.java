
package ru.sberbank.pprb.sbbol.global_search.search.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.sberbank.pprb.sbbol.global_search.core.common.DefaultIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.common.StringToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategyRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Queryable;
import ru.sberbank.pprb.sbbol.global_search.core.entity.RoutingValue;
import ru.sberbank.pprb.sbbol.global_search.core.entity.RoutingValueMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.RestrictedAccess;

@SearchableEntity(
    name = Account.ENTITY_NAME,
    indexNameResolvingStrategy = @IndexNameResolvingStrategyRef(type = DefaultIndexNameResolvingStrategy.class)
)
@RestrictedAccess(mandatoryRestrictions = DigitalIdRestriction.class)
@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
@Setter
public class Account extends BaseSearchableEntity {

    static final String ENTITY_NAME = "account";

    @RoutingValue(
        mapper = @RoutingValueMapperRef(type = StringToStringMapper.class)
    )
    private String digitalId;

    @Queryable
    private String account;

    @Queryable
    private String bic;

    @Queryable
    private String bankAccount;
}
