package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.Arrays;
import java.util.Collection;

/**
 * Условие соответствия хотя бы одному из переданных условий
 */
public class OrCondition implements Condition {

    /**
     * Коллекция условий соответствия
     */
    private final Collection<Condition> nestedConditions;

    OrCondition(Condition... nestedConditions) {
        this.nestedConditions = Arrays.asList(nestedConditions);
    }

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
