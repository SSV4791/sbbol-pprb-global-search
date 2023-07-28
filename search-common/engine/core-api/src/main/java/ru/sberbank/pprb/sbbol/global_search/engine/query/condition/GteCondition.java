package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

/**
 * Условие: значение поля больше либо равно заданному
 *
 * @param <T> тип значения поля
 */
@Value
public class GteCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (нижняя граница, включительно, выборки)
     */
    T from;
}
