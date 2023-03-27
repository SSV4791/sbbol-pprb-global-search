package ru.sberbank.pprb.sbbol.global_search.core.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Маркер поля, используемого в качестве версии объекта сущности во внешней системе
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalVersion {
}
