package ru.sberbank.pprb.sbbol.global_search.core.entity;

/**
 * Ссылка на маппер поля, используемого в качестве идентификатора сущности, в строковый идентификатор документа
 * в поисковом индексе, реализующий {@link ValueToStringMapper}
 * <p>
 * Ссылка может быть по типу бина маппера, либо по типу и наименованию бина
 */
public @interface EntityIdMapperRef {

    /**
     * Наименование бина. Если не задано, поиск бина будет осуществляться только по типу
     */
    String beanName() default "";

    /**
     * Тип бина
     */
    Class<? extends ValueToStringMapper> type();
}
