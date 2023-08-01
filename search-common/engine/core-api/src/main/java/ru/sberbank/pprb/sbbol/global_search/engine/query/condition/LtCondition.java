package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

/**
 * Условие: значение поля меньше заданного
 *
 * @param <T> тип значения поля
 */
@Value
public class LtCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (верхняя граница, не включая, выборки)
     */
    T to;
}
