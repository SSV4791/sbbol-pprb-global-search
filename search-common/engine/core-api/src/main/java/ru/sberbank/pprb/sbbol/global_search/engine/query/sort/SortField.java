package ru.sberbank.pprb.sbbol.global_search.engine.query.sort;

/**
 * Атрибут сортировки
 */
public class SortField {

    /**
     * Наименование поля сущности
     */
    private String fieldName;

    /**
     * Тип сортировки
     */
    private String orderType;

    public SortField(String fieldName, String orderType) {
        this.fieldName = fieldName;
        this.orderType = orderType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
