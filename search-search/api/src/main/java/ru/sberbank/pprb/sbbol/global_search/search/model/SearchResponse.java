package ru.sberbank.pprb.sbbol.global_search.search.model;


import lombok.Getter;
import lombok.Setter;
import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class SearchResponse implements Serializable {

    @EntityId(
        mapper = @EntityIdMapperRef(type = UuidToStringMapper.class)
    )
    private UUID entityId;
}

