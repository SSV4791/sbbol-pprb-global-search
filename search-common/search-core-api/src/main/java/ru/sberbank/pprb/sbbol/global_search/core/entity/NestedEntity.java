package ru.sberbank.pprb.sbbol.global_search.core.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Маркер сущности, индексируемой в поисковом сервисе,
 * указывающий, что сущность является вложенной сущностью
 * <p>
 * Вложенная сущность должна иметь идентификатор внешней сущности, отмеченный аннотацией {@link OuterEntityId},
 * и собственный идентификатор сущности, отмеченный аннотацией {@link EntityId}
 * <p>
 * Допускается коллекция вложенных сущностей, но только на самом последнем уровне иерархии
 *
 * @see SearchableEntity
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NestedEntity {

    /**
     * Тип внешней сущности
     */
    Class<?> outerEntityType() default Void.class;

    /**
     * Наименование типа внешней сущности.
     * <p>
     * Следует использовать в случае, когда в compile-time класс внешней сущности не доступен (например, находится в другом модуле).
     */
    String outerEntityTypeName() default "";
}
