package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.Collection;

/**
 * Условие соответствия всем переданным условиям
 */
@Value
public class AndCondition implements Condition {

    /**
     * Коллекция условий соответствия
     */
    Collection<Condition> nestedConditions;

    @Override
    public QueryBuilder toQueryBuilder() {
        BoolQueryBuilder result = QueryBuilders.boolQuery();
        for (Condition condition : nestedConditions) {
            if (condition.useFilterContext()) {
                result.filter(condition.toQueryBuilder());
            } else {
                result.must(condition.toQueryBuilder());
            }
        }
        return result;
    }

    @Override
    public boolean useFilterContext() {
        return false;
    }
}
