package ru.sberbank.pprb.sbbol.global_search.core.common;

import ru.sberbank.pprb.sbbol.global_search.core.entity.ValueToStringMapper;

import java.util.UUID;

/**
 * Маппер UUID в строку
 */
public class UuidToStringMapper implements ValueToStringMapper<UUID> {
    @Override
    public String map(UUID propertyValue) {
        return propertyValue.toString();
    }
}
