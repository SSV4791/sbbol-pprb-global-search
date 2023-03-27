package ru.sberbank.pprb.sbbol.global_search.core.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация, помечающая поля сущностей, доступных в полнотекстовом поиске, по которым может осуществляться сортировка.
 * <p>Поля сущностей не отмеченные аннотацией будут игнорироваться при сортировке.
 * <p>При применении сортировки по полю сущности отключается сортировка по весам.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sortable {

    /**
     * Реальное имя поля для сортировки по наименованию поля сущности.
     * Не следует использовать сортировку на поле с типом `text`, необходимо использовать сортировку на вложенное поле `keyword`
     */
    String value() default "";
}
