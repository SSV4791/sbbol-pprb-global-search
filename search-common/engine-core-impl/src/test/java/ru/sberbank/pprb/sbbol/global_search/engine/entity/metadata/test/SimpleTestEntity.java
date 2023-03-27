package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test;

import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategyRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Queryable;
import ru.sberbank.pprb.sbbol.global_search.core.entity.RoutingValue;
import ru.sberbank.pprb.sbbol.global_search.core.entity.RoutingValueMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Sortable;

@SearchableEntity(
    name = SimpleTestEntity.ENTITY_NAME,
    indexNameResolvingStrategy = @IndexNameResolvingStrategyRef(type = SimpleTestEntityIndexNameResolvingStrategy.class)
)
public class SimpleTestEntity {

    public static final String ENTITY_NAME = "simple_entity";

    @EntityId(
        mapper = @EntityIdMapperRef(type = SimpleTestEntityIdMapper.class)
    )
    @Queryable
    @Sortable("entityId.raw")
    private String entityId;

    @RoutingValue(
        mapper = @RoutingValueMapperRef(type = SimpleTestEntityRoutingValueMapper.class)
    )
    @Queryable({"realRoutingValue", "oneMoreValue"})
    private String routingValue;

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getRoutingValue() {
        return routingValue;
    }

    public void setRoutingValue(String routingValue) {
        this.routingValue = routingValue;
    }
}
