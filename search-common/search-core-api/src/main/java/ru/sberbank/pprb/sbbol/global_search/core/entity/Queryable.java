package ru.sberbank.pprb.sbbol.global_search.core.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, помечающая поля сущностей, доступных в полнотекстовом поиске, по которым может осуществляться поиск.
 * <p>Поля сущностей, не отмеченные аннотацией, не участвуют в поиске.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Queryable {

    /**
     * Реальные имена полей для запроса по имени поля сущности
     * <p>Полю сущности может соответствовать несколько полей в индексе
     * (например, дата может быть представлена в виде строк разного формата)
     */
    String[] value() default {};
}
