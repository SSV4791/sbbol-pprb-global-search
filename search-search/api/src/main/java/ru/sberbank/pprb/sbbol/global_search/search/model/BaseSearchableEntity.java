package ru.sberbank.pprb.sbbol.global_search.search.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.ExternalVersion;
import ru.sberbank.pprb.sbbol.global_search.core.entity.RoutingValue;
import ru.sberbank.pprb.sbbol.global_search.core.entity.RoutingValueMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Sortable;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseSearchableEntity extends SearchableEntity {

    @Sortable("id")
    private Long id;

    @ExternalVersion
    private Integer version;

    @RoutingValue(
        mapper = @RoutingValueMapperRef(type = UuidToStringMapper.class)
    )
    @Sortable("orgGuid")
    private UUID orgGuid;
}

