package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

import java.util.Collection;

/**
 * Условие соответствия всем переданным условиям
 */
@Value
public class AndCondition implements Condition {

    /**
     * Коллекция условий соответствия
     */
    Collection<Condition> nestedConditions;
}
