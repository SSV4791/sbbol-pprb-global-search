package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

/**
 * Условие: значение поля меньше либо равно заданному
 *
 * @param <T> тип значения поля
 */
public class LteCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (верхняя граница, включительно, выборки)
     */
    private final T to;

    public LteCondition(String fieldName, T to) {
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
        return "LteCondition{" +
            "fieldName='" + fieldName + '\'' +
            ", to=" + to +
            '}';
    }
}
