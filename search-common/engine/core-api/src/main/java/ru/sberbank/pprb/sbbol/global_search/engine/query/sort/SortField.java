package ru.sberbank.pprb.sbbol.global_search.engine.query.sort;

import lombok.Value;

/**
 * Атрибут сортировки
 */
@Value
public class SortField {

    /**
     * Наименование поля сущности
     */
    String fieldName;

    /**
     * Тип сортировки
     */
    String orderType;
}
