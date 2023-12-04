package ru.sberbank.pprb.sbbol.global_search.search.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.ExternalVersion;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Sortable;

import java.util.UUID;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Partner.class, name = "partner"),
    @JsonSubTypes.Type(value = Account.class, name = "account")
})

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
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

