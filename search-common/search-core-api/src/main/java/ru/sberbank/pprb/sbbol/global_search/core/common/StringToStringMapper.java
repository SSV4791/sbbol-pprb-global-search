package ru.sberbank.pprb.sbbol.global_search.core.common;

import ru.sberbank.pprb.sbbol.global_search.core.entity.ValueToStringMapper;

/**
 * Маппер строки в строку
 */
public class StringToStringMapper implements ValueToStringMapper<String> {
    @Override
    public String map(String propertyValue) {
        return propertyValue;
    }
}
