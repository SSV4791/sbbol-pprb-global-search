package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

import java.util.Collection;

/**
 * Условие соответствия списку идентификаторов
 */
@Value
public class IdsCondition implements Condition {

    /**
     * Значения идентификатора, удовлетворяющие условию
     */
    Collection<String> ids;
}
