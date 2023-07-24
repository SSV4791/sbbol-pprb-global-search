package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test;

import ru.sberbank.pprb.sbbol.global_search.core.entity.ValueToStringMapper;

public class SimpleTestEntityIdMapper implements ValueToStringMapper<String> {
    @Override
    public String map(String propertyValue) {
        return propertyValue;
    }
}
