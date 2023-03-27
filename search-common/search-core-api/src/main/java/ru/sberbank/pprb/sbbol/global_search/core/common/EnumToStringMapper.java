package ru.sberbank.pprb.sbbol.global_search.core.common;

import ru.sberbank.pprb.sbbol.global_search.core.entity.ValueToStringMapper;

/**
 * Маппер enum'а в строку
 */
public class EnumToStringMapper implements ValueToStringMapper<Enum<?>> {
    @Override
    public String map(Enum<?> propertyValue) {
        return propertyValue.name();
    }
}
