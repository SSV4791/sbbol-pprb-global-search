package ru.sberbank.pprb.sbbol.global_search.core.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Маркер поля, используемого в качестве значения для routing'а запросов в поисковом индексе.
 * В зависимости от значения routing'а определяются шарды, на которых запрос будет выполняться.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoutingValue {

    /**
     * Маппер поля, используемого в качестве значения routing'а запросов, в строку
     * (в запросах используется строковое представление).
     *
     * @see ValueToStringMapper
     */
    RoutingValueMapperRef mapper();
}
