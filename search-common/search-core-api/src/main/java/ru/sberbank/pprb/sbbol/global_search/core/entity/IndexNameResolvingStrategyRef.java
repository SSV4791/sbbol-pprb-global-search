package ru.sberbank.pprb.sbbol.global_search.core.entity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ссылка на стратегию определения наименования индекса для сущности, реализующую {@link IndexNameResolvingStrategy}
 * <p>
 * Ссылка может быть по типу бина стратегии, либо по типу и наименованию бина
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface IndexNameResolvingStrategyRef {

    /**
     * Наименование бина. Если не задано, поиск бина будет осущетсвляться только по типу
     */
    String beanName() default "";

    /**
     * Тип бина
     */
    Class<? extends IndexNameResolvingStrategy> type();
}
