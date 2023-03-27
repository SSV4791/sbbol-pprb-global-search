package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие отрицания
 */
public class NotCondition implements Condition {

    /**
     * Отрицаемое условие
     */
    private final Condition nestedCondition;

    NotCondition(Condition nestedCondition) {
        this.nestedCondition = nestedCondition;
    }

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.boolQuery().mustNot(nestedCondition.toQueryBuilder());
    }

    @Override
    public boolean useFilterContext() {
        return false;
    }
}
