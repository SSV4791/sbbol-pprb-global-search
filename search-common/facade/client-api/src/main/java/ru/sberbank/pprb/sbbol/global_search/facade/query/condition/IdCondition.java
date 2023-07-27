package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

/**
 * Условие соответствия заданному значению идентификатора
 */
public final class IdCondition implements Condition {

    /**
     * Значение идентификатора, удовлетворяющее условию
     */
    private final String id;

    IdCondition(String id) {
        this.id = id;
    }

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.idsQuery().addIds(id);
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
