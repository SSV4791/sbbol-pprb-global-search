package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

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

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.rangeQuery(fieldName).lte(to);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
