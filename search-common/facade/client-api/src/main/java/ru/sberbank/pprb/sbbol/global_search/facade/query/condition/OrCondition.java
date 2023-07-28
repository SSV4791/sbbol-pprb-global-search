package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.Collection;

/**
 * Условие соответствия хотя бы одному из переданных условий
 */
@Value
public class OrCondition implements Condition {

    /**
     * Коллекция условий соответствия
     */
    Collection<Condition> nestedConditions;

    @Override
    public QueryBuilder toQueryBuilder() {
        BoolQueryBuilder result = QueryBuilders.boolQuery();
        for (Condition condition : nestedConditions) {
            result.should(condition.toQueryBuilder());
        }
        return result;
    }

    @Override
    public boolean useFilterContext() {
        return false;
    }
}
