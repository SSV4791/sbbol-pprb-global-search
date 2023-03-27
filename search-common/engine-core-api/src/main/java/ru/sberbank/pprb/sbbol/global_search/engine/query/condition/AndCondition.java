package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Условие соответствия всем переданным условиям
 */
public class AndCondition implements Condition {

    /**
     * Коллекция условий соответствия
     */
    private final Collection<Condition> nestedConditions;

    public AndCondition(Condition... nestedConditions) {
        this.nestedConditions = Arrays.asList(nestedConditions);
    }

    public AndCondition(Collection<Condition> nestedConditions) {
        this.nestedConditions = new ArrayList<>(nestedConditions);
    }

    public Collection<Condition> getNestedConditions() {
        return nestedConditions;
    }

    @Override
    public String toString() {
        return "AndCondition{" +
            "nestedConditions=" + nestedConditions +
            '}';
    }
}
