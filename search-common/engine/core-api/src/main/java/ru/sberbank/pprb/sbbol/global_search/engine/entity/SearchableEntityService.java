package ru.sberbank.pprb.sbbol.global_search.engine.entity;

import ru.sberbank.pprb.sbbol.global_search.engine.query.EntityQuery;
import ru.sberbank.pprb.sbbol.global_search.engine.query.QueryResult;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * Сервис взаимодейтвия с поисковым хранилищем
 */
public interface SearchableEntityService {

    /**
     * Сохранить объект сущности в поисковом хранилище
     *
     * @param entity сохраняемый объект сущности
     * @param <T>    тип объекта сущности
     */
    <T> void save(T entity) throws Exception;

    /**
     * Добавить сущность в OpenSearch (если сущность уже есть, будет ошибка)
     *
     * @param entity      добавляемый объект сущности
     * @param withRefresh признак необходимости refresh'а индекса после добавления (применять с осторожностью - может привести к падению производительности)
     * @param <T>         тип объекта сущности
     */
    <T> void create(T entity, boolean withRefresh) throws IOException, InvocationTargetException, IllegalAccessException;

    /**
     * Добавить сущность в OpenSearch (если сущность уже есть, будет ошибка)
     *
     * @param entity добавляемый объект сущности
     * @param <T>    тип объекта сущности
     */
    <T> void create(T entity) throws IOException, InvocationTargetException, IllegalAccessException;

    /**
     * Получить объект сущности из поискового хранилища по его идентификатору
     *
     * @param clazz        класс объекта сущности
     * @param entityId     идентификатор объекта сущности
     * @param routingValue значения поля роутинга запроса (если не указано, поиск будет произведен по всем шардам индекса)
     * @param <T>          тип объекта сущности
     * @throws IOException ошибка поискового хранилища
     */
    <T> T get(Class<T> clazz, String entityId, String routingValue) throws IOException;

    /**
     * Удалить объект сущности из поискового хранилища
     *
     * @param entity удаляемый объект сущности
     * @param <T>    тип объекта сущности
     */
    <T> void delete(T entity) throws IOException, InvocationTargetException, IllegalAccessException;

    /**
     * Получить список объектов сущностей из поискового хранилища в соответствии с заданными условиями отбора
     *
     * @param queries условия отбора объектов сущностей
     */
    Collection<QueryResult<?>> find(Collection<EntityQuery<?>> queries) throws IOException;

    /**
     * Получить список объектов сущности конкретного типа из поискового хранилища в соответствии с заданными условиями отбора
     *
     * @param query условие отбора объектов сущности
     * @param <T>   тип объекта сущности
     */
    <T> QueryResult<T> find(EntityQuery<T> query);
}
