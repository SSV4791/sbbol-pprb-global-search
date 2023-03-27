package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

/**
 * Условие: значение поля больше либо равно заданному
 *
 * @param <T> тип значения поля
 */
public class GteCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (нижняя граница, включительно, выборки)
     */
    private final T from;

    public GteCondition(String fieldName, T from) {
        this.fieldName = fieldName;
        this.from = from;
    }

    public String getFieldName() {
        return fieldName;
    }

    public T getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return "GteCondition{" +
            "fieldName='" + fieldName + '\'' +
            ", from=" + from +
            '}';
    }
}
