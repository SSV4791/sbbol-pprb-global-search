package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Условие IN
 *
 * @param <T> тип значений условия
 */
@Value
public class InCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    String fieldName;

    /**
     * Значения поля, удовлетворяющие условию
     */
    Collection<T> values;

    public InCondition(String fieldName, Collection<? extends T> values) {
        this.fieldName = fieldName;
        this.values = new ArrayList<>(values);
    }
}
