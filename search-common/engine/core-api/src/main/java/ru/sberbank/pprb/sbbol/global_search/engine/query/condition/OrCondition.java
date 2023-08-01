package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

import java.util.Collection;

/**
 * Условие соответствия хотя бы одному из переданных условий
 */
@Value
public class OrCondition implements Condition {

    /**
     * Коллекция условий соответствия
     */
    Collection<Condition> nestedConditions;
}
