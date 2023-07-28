package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

/**
 * Условие: значение поля меньше либо равно заданному
 *
 * @param <T> тип значения поля
 */
@Value
public class LteCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (верхняя граница, включительно, выборки)
     */
    T to;
}
