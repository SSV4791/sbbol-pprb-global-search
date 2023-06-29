package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test;

import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategyRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.ExternalVersion;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested.NestedTestEntity;

@SearchableEntity(
    name = ChildTestEntity.ENTITY_NAME,
    indexNameResolvingStrategy = @IndexNameResolvingStrategyRef(type = ChildTestEntityIndexNameResolvingStrategy.class)
)
public class ChildTestEntity extends SimpleTestEntity {

    public static final String ENTITY_NAME = "child_entity";

    @ExternalVersion
    private long version;

    private NestedTestEntity nestedTestEntity;

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public NestedTestEntity getNestedTestEntity() {
        return nestedTestEntity;
    }

    public void setNestedTestEntity(NestedTestEntity nestedTestEntity) {
        this.nestedTestEntity = nestedTestEntity;
    }
}
