package ru.sberbank.pprb.sbbol.global_search.search.restrictions;

import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.Condition;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.DigitalIdRestriction;

import java.util.stream.Collectors;

/**
 * Конвертор ограничения по организации.
 */
public class DigitalIdRestrictionConverter implements RestrictionConverter<DigitalIdRestriction> {

    private static final String ORG_GUID_FIELD_NAME = "digitalId";

    @Override
    public Condition getCondition(DigitalIdRestriction restriction) {
        return Condition.in(ORG_GUID_FIELD_NAME, restriction.getDigitalId().stream().map(Object::toString).collect(Collectors.toList()));
    }
}
