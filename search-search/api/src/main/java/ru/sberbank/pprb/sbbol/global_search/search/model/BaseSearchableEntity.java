package ru.sberbank.pprb.sbbol.global_search.search.model;

import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.*;

import java.util.Objects;
import java.util.UUID;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public UUID getOrgGuid() {
        return orgGuid;
    }

    public void setOrgGuid(UUID orgGuid) {
        this.orgGuid = orgGuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseSearchableEntity that = (BaseSearchableEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(version, that.version) && Objects.equals(orgGuid, that.orgGuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, version, orgGuid);
    }

    @Override
    public String toString() {
        return "BaseSearchableEntity{" +
                "id=" + id +
                ", version=" + version +
                ", orgGuid=" + orgGuid +
                '}';
    }
}
