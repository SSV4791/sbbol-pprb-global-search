
package ru.sberbank.pprb.sbbol.global_search.search.model;


import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Sortable;

import java.io.Serializable;
import java.util.UUID;

public abstract class SearchableEntity implements Serializable {

    @EntityId(
        mapper = @EntityIdMapperRef(type = UuidToStringMapper.class)
    )
    @Sortable("entityId")
    private UUID entityId;

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }
}
