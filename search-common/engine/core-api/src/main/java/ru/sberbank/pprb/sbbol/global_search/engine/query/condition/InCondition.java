package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Условие IN
 *
 * @param <T> тип значений условия
 */
public final class InCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значения поля, удовлетворяющие условию
     */
    private final Collection<T> values;

    public InCondition(String fieldName, Collection<? extends T> values) {
        this.fieldName = fieldName;
        this.values = new ArrayList<>(values);
    }

    public String getFieldName() {
        return fieldName;
    }

    public Collection<T> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "InCondition{" +
            "fieldName='" + fieldName + '\'' +
            ", values=" + values +
            '}';
    }
}
