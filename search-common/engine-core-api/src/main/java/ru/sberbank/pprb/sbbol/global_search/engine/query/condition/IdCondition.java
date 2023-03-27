package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

/**
 * Условие соответствия заданному значению идентификатора
 */
public final class IdCondition implements Condition {

    /**
     * Значение идентификатора, удовлетворяющее условию
     */
    private final String id;

    public IdCondition(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "IdCondition{" +
            "id='" + id + '\'' +
            '}';
    }
}
