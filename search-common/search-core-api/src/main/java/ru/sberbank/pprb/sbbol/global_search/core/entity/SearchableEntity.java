package ru.sberbank.pprb.sbbol.global_search.core.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Маркер сущности, индексируемой в поисковом сервисе
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchableEntity {

    /**
     * Наименование сущности
     */
    String name();

    /**
     * Ссылка на стратегию определения наименования индекса для сущности
     *
     * @see IndexNameResolvingStrategy
     */
    IndexNameResolvingStrategyRef indexNameResolvingStrategy();
}
