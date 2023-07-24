package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

/**
 * Условие: значение поля больше заданного
 *
 * @param <T> тип значения поля
 */
public class GtCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     * (нижняя граница, не включая, выборки)
     */
    private final T from;

    public GtCondition(String fieldName, T from) {
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
        return "GtCondition{" +
            "fieldName='" + fieldName + '\'' +
            ", from=" + from +
            '}';
    }
}
