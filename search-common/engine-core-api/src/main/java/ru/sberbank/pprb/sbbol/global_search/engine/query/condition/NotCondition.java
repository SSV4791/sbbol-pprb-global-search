package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

public class NotCondition implements Condition {

    private final Condition nestedCondition;

    public NotCondition(Condition nestedCondition) {
        this.nestedCondition = nestedCondition;
    }

    public Condition getNestedCondition() {
        return nestedCondition;
    }

    @Override
    public String toString() {
        return "NotCondition{" +
            "nestedCondition=" + nestedCondition +
            '}';
    }
}
