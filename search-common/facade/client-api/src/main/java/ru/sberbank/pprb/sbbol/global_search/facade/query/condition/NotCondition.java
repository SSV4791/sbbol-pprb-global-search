package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие отрицания
 */
@Value
public class NotCondition implements Condition {

    /**
     * Отрицаемое условие
     */
    Condition nestedCondition;

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.boolQuery().mustNot(nestedCondition.toQueryBuilder());
    }

    @Override
    public boolean useFilterContext() {
        return false;
    }
}
