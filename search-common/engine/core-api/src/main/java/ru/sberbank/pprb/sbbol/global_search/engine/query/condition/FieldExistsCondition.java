package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

/**
 * Условие наличия заданного поля у сущности
 */
@Value
public class FieldExistsCondition implements Condition {

    /**
     * Наименование поля
     */
    String fieldName;
}
