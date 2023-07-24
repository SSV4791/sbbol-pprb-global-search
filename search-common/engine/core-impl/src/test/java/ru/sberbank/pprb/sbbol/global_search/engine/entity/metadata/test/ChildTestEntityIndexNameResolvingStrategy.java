package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test;

import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategy;

public class ChildTestEntityIndexNameResolvingStrategy implements IndexNameResolvingStrategy<ChildTestEntity> {

    public static final String INDEX_NAME = "child_test_entity_index";

    @Override
    public String getIndexName(ChildTestEntity entity) {
        return INDEX_NAME;
    }
}
