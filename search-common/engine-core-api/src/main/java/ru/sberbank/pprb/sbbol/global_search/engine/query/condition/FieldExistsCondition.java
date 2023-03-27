package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

/**
 * Условие наличия заданного поля у сущности
 */
public class FieldExistsCondition implements Condition {

    /**
     * Наименование поля
     */
    private final String fieldName;

    public FieldExistsCondition(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String toString() {
        return "FieldExistsCondition{" +
            "fieldName='" + fieldName + '\'' +
            '}';
    }
}
