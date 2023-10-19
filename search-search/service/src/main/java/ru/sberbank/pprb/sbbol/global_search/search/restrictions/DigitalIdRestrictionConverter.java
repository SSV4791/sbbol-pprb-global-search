package ru.sberbank.pprb.sbbol.global_search.search.restrictions;

import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.Condition;
import ru.sberbank.pprb.sbbol.global_search.search.model.DigitalIdRestriction;

/**
 * Конвертор ограничения по организации.
 */
public class DigitalIdRestrictionConverter implements RestrictionConverter<DigitalIdRestriction> {

    private static final String DIGITAL_ID_FIELD_NAME = "digitalId";

    @Override
    public Condition getCondition(DigitalIdRestriction restriction) {
        return Condition.equal(DIGITAL_ID_FIELD_NAME, restriction.getDigitalId());
    }
}
