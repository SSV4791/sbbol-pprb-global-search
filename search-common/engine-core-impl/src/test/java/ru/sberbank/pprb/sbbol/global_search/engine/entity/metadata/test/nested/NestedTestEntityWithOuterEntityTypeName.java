package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested;

import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntityIdMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.NestedEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.OuterEntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Queryable;

@NestedEntity(
    outerEntityTypeName = "ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.ChildTestEntity"
)
public class NestedTestEntityWithOuterEntityTypeName {

    @OuterEntityId(
        mapper = @EntityIdMapperRef(type = SimpleTestEntityIdMapper.class)
    )
    private String outerEntityId;

    @EntityId(
        mapper = @EntityIdMapperRef(type = SimpleTestEntityIdMapper.class)
    )
    @Queryable
    private String entityId;

    public String getOuterEntityId() {
        return outerEntityId;
    }

    public void setOuterEntityId(String outerEntityId) {
        this.outerEntityId = outerEntityId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
