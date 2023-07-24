package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие: значение поля меньше заданного
 *
 * @param <T> тип значения поля
 */
public class LtCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (верхняя граница, не включая, выборки)
     */
    private final T to;

    LtCondition(String fieldName, T to) {
        this.fieldName = fieldName;
        this.to = to;
    }

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.rangeQuery(fieldName).lt(to);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
