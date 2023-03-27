package ru.sberbank.pprb.sbbol.global_search.core.entity;

/**
 * Маппер значения поля в строку, используемую в полях метаданных документа.
 * <p>
 * Само поле сохраняется в исходном виде в составе документа, результат маппинга используется только в полях метаданных.
 *
 * @param <T> исходный тип поля
 * @see EntityId
 * @see EntityIdMapperRef
 * @see RoutingValue
 * @see RoutingValueMapperRef
 */
public interface ValueToStringMapper<T> {

    /**
     * Преобразовать значение поля в строку
     *
     * @param propertyValue значение поля исходного типа
     */
    String map(T propertyValue);
}
