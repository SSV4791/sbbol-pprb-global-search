package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test;

import ru.sberbank.pprb.sbbol.global_search.core.common.DefaultIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategyRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested.NestedTestEntityForCollection;

import java.util.List;
import java.util.UUID;

@SearchableEntity(
    name = TestEntityWithNestedCollection.ENTITY_NAME,
    indexNameResolvingStrategy = @IndexNameResolvingStrategyRef(type = DefaultIndexNameResolvingStrategy.class)
)
public class TestEntityWithNestedCollection {
    public static final String ENTITY_NAME = "with_nested_collection";

    @EntityId(
        mapper = @EntityIdMapperRef(type = UuidToStringMapper.class)
    )
    private UUID entityId;

    private List<NestedTestEntityForCollection> nestedEntities;

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public List<NestedTestEntityForCollection> getNestedEntities() {
        return nestedEntities;
    }

    public void setNestedEntities(List<NestedTestEntityForCollection> nestedEntities) {
        this.nestedEntities = nestedEntities;
    }
}
