package ru.sberbank.pprb.sbbol.global_search.facade.query.sort;

import org.opensearch.search.sort.SortBuilder;

/**
 * Условия сортировки к OpenSearch
 */
public interface QuerySorting {

    SortBuilder<?> toSortBuilder();
}
