package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие соответствия заданному значению идентификатора
 */
@Value
public class IdCondition implements Condition {

    /**
     * Значение идентификатора, удовлетворяющее условию
     */
    String id;

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.idsQuery().addIds(id);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
