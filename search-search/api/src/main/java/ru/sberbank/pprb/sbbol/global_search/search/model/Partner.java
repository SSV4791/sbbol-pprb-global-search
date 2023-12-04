
package ru.sberbank.pprb.sbbol.global_search.search.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

import java.util.UUID;

@SearchableEntity(
    name = Partner.ENTITY_NAME,
    indexNameResolvingStrategy = @IndexNameResolvingStrategyRef(type = DefaultIndexNameResolvingStrategy.class)
)
@RestrictedAccess(mandatoryRestrictions = DigitalIdRestriction.class)
@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
@Setter
@NoArgsConstructor
public class Partner extends BaseSearchableEntity {

    static final String ENTITY_NAME = "partner";

    @RoutingValue(
        mapper = @RoutingValueMapperRef(type = StringToStringMapper.class)
    )
    private String digitalId;

    @Queryable
    private String name;

    @Queryable
    private String inn;

    @Queryable
    private String kpp;

    @Builder
    public Partner(UUID entityId, String id, Integer version, String digitalId, String name, String inn, String kpp) {
        super(entityId, id, version);
        this.digitalId = digitalId;
        this.name = name;
        this.inn = inn;
        this.kpp = kpp;
    }

}
