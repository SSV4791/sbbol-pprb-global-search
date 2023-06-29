package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Условие соответствия хотя бы одному из переданных условий
 */
public class OrCondition implements Condition {

    /**
     * Коллекция условий соответствия
     */
    private final Collection<Condition> nestedConditions;

    public OrCondition(Condition... nestedConditions) {
        this.nestedConditions = Arrays.asList(nestedConditions);
    }

    public OrCondition(Collection<Condition> nestedConditions) {
        this.nestedConditions = new ArrayList<>(nestedConditions);
    }

    public Collection<Condition> getNestedConditions() {
        return nestedConditions;
    }

    @Override
    public String toString() {
        return "OrCondition{" +
            "nestedConditions=" + nestedConditions +
            '}';
    }
}
