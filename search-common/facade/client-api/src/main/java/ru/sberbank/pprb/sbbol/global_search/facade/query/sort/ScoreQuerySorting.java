package ru.sberbank.pprb.sbbol.global_search.facade.query.sort;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.opensearch.search.sort.ScoreSortBuilder;
import org.opensearch.search.sort.SortBuilder;

/**
 * Условие сортировки по весам. Если не указан не один тип сортировки применяется по умолчанию
 */
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScoreQuerySorting implements QuerySorting {

    /**
     * Порядок сортировки
     */
    SortOrder order;

    @Override
    public SortBuilder<?> toSortBuilder() {
        ScoreSortBuilder builder = new ScoreSortBuilder();
        if (order == SortOrder.ASC) {
            builder.order(org.opensearch.search.sort.SortOrder.ASC);
        }
        return builder;
    }
}
