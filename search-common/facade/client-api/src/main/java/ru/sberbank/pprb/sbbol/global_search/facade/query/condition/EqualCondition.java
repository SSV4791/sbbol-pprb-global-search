package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие соответствия заданному значению поля
 *
 * @param <T> тип значения поля
 */
@Value
public class EqualCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     */
    T value;

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.termQuery(fieldName, value);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
