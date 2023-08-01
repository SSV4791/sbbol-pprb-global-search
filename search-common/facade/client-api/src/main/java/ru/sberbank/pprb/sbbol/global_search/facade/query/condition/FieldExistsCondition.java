package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие наличия заданного поля у сущности
 */
@Value
public class FieldExistsCondition implements Condition {

    /**
     * Наименование поля
     */
    String fieldName;

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.existsQuery(fieldName);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
