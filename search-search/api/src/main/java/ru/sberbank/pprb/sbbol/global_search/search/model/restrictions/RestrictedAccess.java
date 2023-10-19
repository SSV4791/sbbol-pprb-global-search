package ru.sberbank.pprb.sbbol.global_search.search.model.restrictions;

import ru.sberbank.pprb.sbbol.global_search.search.model.Restriction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация отмечает сущности, запросы к которым должны содержать ограничения
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestrictedAccess {

    /**
     * Типы обязательных ограничений.
     * <p>Должны явно присутствовать в передаваемом поисковом фильтре.
     * Отсутствие хотя бы одного из них в фильтре приводит к ошибке выполнения запроса.
     *
     * @see ru.sberbank.pprb.sbbol.global_search.search.model.SearchFilter#entityRestrictions
     */
    Class<? extends Restriction>[] mandatoryRestrictions() default {};

    /**
     * Типы дополнительных ограничений.
     * <p>Могут, но не должны, присутствовать в поисковом фильтре. Ограничивают список доступных для сущности ограничений.
     *
     * @see ru.sberbank.pprb.sbbol.global_search.search.model.SearchFilter#entityRestrictions
     */
    Class<? extends Restriction>[] additionalRestrictions() default {};
}
