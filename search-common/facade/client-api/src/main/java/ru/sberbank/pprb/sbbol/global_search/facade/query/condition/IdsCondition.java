package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import lombok.Value;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;

import java.util.Collection;

/**
 * Условие соответствия списку идентификаторов
 */
@Value
public class IdsCondition implements Condition {

    /**
     * Значения идентификатора, удовлетворяющие условию
     */
    Collection<String>  ids;

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.idsQuery().addIds(ids.toArray(new String[0]));
    }

    @Override
    public boolean useFilterContext() {
        return true;
    }
}
