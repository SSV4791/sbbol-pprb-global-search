
package ru.sberbank.pprb.sbbol.global_search.search.model;


import lombok.Getter;
import lombok.Setter;
import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Sortable;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public abstract class SearchableEntity implements Serializable {

    @EntityId(
        mapper = @EntityIdMapperRef(type = UuidToStringMapper.class)
    )
    @Sortable("entityId")
    private UUID entityId;
}
