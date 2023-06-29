package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata;

import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;

import java.lang.reflect.InvocationTargetException;

/**
 * Конвертер объекта сущности, используемой в поиске, в объект внутреннего представления сущности
 */
public interface EntityToInternalEntityHolderConverter {

    /**
     * Преобразовать объект сущности в объект внутреннего представления
     *
     * @param entity объект сущнсоти
     * @param <T>    тип объекта сущности
     */
    <T> InternalEntityHolder<T> convert(T entity) throws InvocationTargetException, IllegalAccessException;
}
