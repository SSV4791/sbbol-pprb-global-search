package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

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

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.rangeQuery(fieldName).gte(from);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
