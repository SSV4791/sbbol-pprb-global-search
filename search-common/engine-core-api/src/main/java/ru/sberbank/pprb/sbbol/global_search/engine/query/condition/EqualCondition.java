package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

/**
 * Условие соответствия заданному значению поля
 *
 * @param <T> тип значения поля
 */
public final class EqualCondition<T> implements Condition {

    /**
     * Наименование поля, на которое накладывается условие
     */
    private final String fieldName;

    /**
     * Значение поля, удовлетворяющее условию
     */
    private final T value;

    public EqualCondition(String fieldName, T value) {
        this.fieldName = fieldName;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "EqualCondition{" +
            "fieldName='" + fieldName + '\'' +
            ", value=" + value +
            '}';
    }
}
