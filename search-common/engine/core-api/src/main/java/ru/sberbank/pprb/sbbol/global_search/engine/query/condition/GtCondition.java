package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

/**
 * Условие: значение поля больше заданного
 *
 * @param <T> тип значения поля
 */
@Value
public class GtCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (нижняя граница, не включая, выборки)
     */
    T from;
}
