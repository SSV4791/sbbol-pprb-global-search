package ru.sberbank.pprb.sbbol.global_search.core.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Маркер поля, используемого в качестве идентификатора сущности
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityId {

    /**
     * Маппер поля, используемого в качестве идентификатора сущности, в строковый идентификатор документа в поисковом индексе
     *
     * @see ValueToStringMapper
     */
    EntityIdMapperRef mapper();
}
