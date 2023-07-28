package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Условие IN
 *
 * @param <T> тип значений условия
 */
@Value
public class InCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    String fieldName;

    /**
     * Значения поля, удовлетворяющие условию
     */
    Collection<T> values;

    InCondition(String fieldName, Collection<? extends T> values) {
        this.fieldName = fieldName;
        this.values = new ArrayList<>(values);
    }

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.termsQuery(fieldName, values);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
