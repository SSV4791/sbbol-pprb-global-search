package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

/**
 * Условие соответствия заданному значению идентификатора
 */
@Value
public class IdCondition implements Condition {

    /**
     * Значение идентификатора, удовлетворяющее условию
     */
    String id;
}
