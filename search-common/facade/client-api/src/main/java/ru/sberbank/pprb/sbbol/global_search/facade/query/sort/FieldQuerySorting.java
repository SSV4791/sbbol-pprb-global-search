package ru.sberbank.pprb.sbbol.global_search.facade.query.sort;


import org.opensearch.search.sort.FieldSortBuilder;
import org.opensearch.search.sort.SortBuilder;

/**
 * Условие сортировки по полю
 */
public class FieldQuerySorting implements QuerySorting {

    /**
     * Наименование поля
     */
    private final String fieldName;

    /**
     * Порядок сортировки
     */
    private final SortOrder order;

    public FieldQuerySorting(String fieldName) {
        this.fieldName = fieldName;
        this.order = SortOrder.ASC;
    }

    public FieldQuerySorting(String fieldName, SortOrder order) {
        this.fieldName = fieldName;
        this.order = order;
    }

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
