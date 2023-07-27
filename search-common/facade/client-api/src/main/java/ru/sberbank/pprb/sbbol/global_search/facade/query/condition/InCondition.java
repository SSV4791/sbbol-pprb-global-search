package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Условие IN
 *
 * @param <T> тип значений условия
 */
public final class InCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значения поля, удовлетворяющие условию
     */
    private final Collection<T> values;

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
