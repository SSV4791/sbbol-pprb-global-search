package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие: значение поля больше либо равно заданному
 *
 * @param <T> тип значения поля
 */
public class GteCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (нижняя граница, включительно, выборки)
     */
    private final T from;

    GteCondition(String fieldName, T from) {
        this.fieldName = fieldName;
        this.from = from;
    }

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.rangeQuery(fieldName).gte(from);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
