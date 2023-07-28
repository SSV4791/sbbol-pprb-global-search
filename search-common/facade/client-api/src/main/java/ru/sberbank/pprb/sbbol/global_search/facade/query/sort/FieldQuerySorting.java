package ru.sberbank.pprb.sbbol.global_search.facade.query.sort;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortBuilder;

/**
 * Условие сортировки по полю
 */
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FieldQuerySorting implements QuerySorting {

    /**
     * Наименование поля
     */
    String fieldName;

    /**
     * Порядок сортировки
     */
    SortOrder order;

    @Override
    public SortBuilder<?> toSortBuilder() {
        FieldSortBuilder builder = new FieldSortBuilder(fieldName);
        if (order == SortOrder.DESC) {
            builder.order(org.opensearch.search.sort.SortOrder.DESC);
        } else {
            builder.order(org.opensearch.search.sort.SortOrder.ASC);
        }
        return builder;
    }
}
