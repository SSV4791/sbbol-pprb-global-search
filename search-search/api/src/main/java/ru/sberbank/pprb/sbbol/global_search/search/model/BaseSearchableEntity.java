package ru.sberbank.pprb.sbbol.global_search.search.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.ExternalVersion;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Sortable;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseSearchableEntity {

    @EntityId(
        mapper = @EntityIdMapperRef(type = UuidToStringMapper.class)
    )
    @Sortable("entityId")
    private UUID entityId;

    @Sortable("id")
    private String id;

    @ExternalVersion
    private Integer version;

}

