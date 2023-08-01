package ru.sberbank.pprb.sbbol.global_search.engine.query.searchafter;

import lombok.Value;

/**
 * Атрибут поиска после определённого значения
 *
 * @param <T> тип значения
 */
@Value
public class SearchAfter<T> {

    String sortFieldName;

    T value;
}
