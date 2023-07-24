package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test;

import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategy;

public class SimpleTestEntityIndexNameResolvingStrategy implements IndexNameResolvingStrategy<SimpleTestEntity> {

    public static final String INDEX_NAME = "simple_test_entity_index";

    @Override
    public String getIndexName(SimpleTestEntity entity) {
        return INDEX_NAME;
    }
}
