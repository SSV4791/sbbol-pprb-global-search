package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие наличия заданного поля у сущности
 */
public class FieldExistsCondition implements Condition {

    /**
     * Наименование поля
     */
    private final String fieldName;

    FieldExistsCondition(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.existsQuery(fieldName);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
