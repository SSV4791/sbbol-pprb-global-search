package ru.sberbank.pprb.sbbol.global_search.facade.query.sort;

import org.opensearch.search.sort.ScoreSortBuilder;
import org.opensearch.search.sort.SortBuilder;

/**
 * Условие сортировки по весам. Если не указан не один тип сортировки применяется по умолчанию
 */
public class ScoreQuerySorting implements QuerySorting {

    /**
     * Порядок сортировки
     */
    private final SortOrder order;

    public ScoreQuerySorting(SortOrder order) {
        this.order = order;
    }

    @Override
    public SortBuilder<?> toSortBuilder() {
        ScoreSortBuilder builder = new ScoreSortBuilder();
        if (order == SortOrder.ASC) {
            builder.order(org.opensearch.search.sort.SortOrder.ASC);
        }
        return builder;
    }
}
