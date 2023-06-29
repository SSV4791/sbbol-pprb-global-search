package ru.sberbank.pprb.sbbol.global_search.engine.query.searchafter;

/**
 * Атрибут поиска после определённого значения
 *
 * @param <T> тип значения
 */
public class SearchAfter<T> {

    private final String sortFieldName;

    private final T value;

    public SearchAfter(String sortFieldName, T value) {
        this.sortFieldName = sortFieldName;
        this.value = value;
    }

    public String getSortFieldName() {
        return sortFieldName;
    }

    public T getValue() {
        return value;
    }
}
