package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

/**
 * Условие: значение поля меньше заданного
 *
 * @param <T> тип значения поля
 */
public class LtCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (верхняя граница, не включая, выборки)
     */
    private final T to;

    public LtCondition(String fieldName, T to) {
        this.fieldName = fieldName;
        this.to = to;
    }

    public String getFieldName() {
        return fieldName;
    }

    public T getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "LtCondition{" +
            "fieldName='" + fieldName + '\'' +
            ", to=" + to +
            '}';
    }
}
