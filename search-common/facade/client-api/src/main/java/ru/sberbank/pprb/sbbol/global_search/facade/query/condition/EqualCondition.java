package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие соответствия заданному значению поля
 *
 * @param <T> тип значения поля
 */
public final class EqualCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     */
    private final T value;

    EqualCondition(String fieldName, T value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.termQuery(fieldName, value);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
