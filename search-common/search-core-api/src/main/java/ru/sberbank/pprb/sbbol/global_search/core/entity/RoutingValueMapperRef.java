package ru.sberbank.pprb.sbbol.global_search.core.entity;

/**
 * Ссылка на маппер поля, используемого в качестве значения для routing'а запросов в поисковом индексе, в строку,
 * реализующий {@link ValueToStringMapper}
 * <p>
 * Ссылка может быть по типу бина маппера, либо по типу и наименованию бина
 */
public @interface RoutingValueMapperRef {

    /**
     * Наименование бина. Если не задано, поиск бина будет осущетсвляться только по типу
     */
    String beanName() default "";

    /**
     * Тип бина
     */
    Class<? extends ValueToStringMapper> type();
}
