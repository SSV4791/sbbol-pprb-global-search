package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested;

import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.TestEntityWithNestedCollection;
import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.NestedEntity;

import java.util.UUID;

@NestedEntity(
    outerEntityType = TestEntityWithNestedCollection.class
)
public class NestedTestEntityForCollection {

    @EntityId(
        mapper = @EntityIdMapperRef(type = UuidToStringMapper.class)
    )
    private UUID outerEntityId;

    @EntityId(
        mapper = @EntityIdMapperRef(type = UuidToStringMapper.class)
    )
    private UUID entityId;

    public UUID getOuterEntityId() {
        return outerEntityId;
    }

    public void setOuterEntityId(UUID outerEntityId) {
        this.outerEntityId = outerEntityId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }
}
