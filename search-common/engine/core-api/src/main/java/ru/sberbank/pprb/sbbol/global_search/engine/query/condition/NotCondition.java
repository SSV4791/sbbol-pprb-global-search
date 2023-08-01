package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import lombok.Value;

@Value
public class NotCondition implements Condition {

    Condition nestedCondition;
}
