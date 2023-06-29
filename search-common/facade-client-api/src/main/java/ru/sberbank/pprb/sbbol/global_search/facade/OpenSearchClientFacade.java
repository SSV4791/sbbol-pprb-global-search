package ru.sberbank.pprb.sbbol.global_search.facade;

import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;
import ru.sberbank.pprb.sbbol.global_search.facade.search.InternalEntitySearchQuery;
import ru.sberbank.pprb.sbbol.global_search.facade.search.InternalEntitySearchResult;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Сервис взаимодействия с хранилищем OpenSearch
 */
public interface OpenSearchClientFacade {

    /**
     * Получение списка сущностей из OpenSearch в соответствии с заданными условиями отбора
     *
     * @param entitySearchQueries коллекция условий отбора сущностей
     */
    Collection<InternalEntitySearchResult<?>> findAll(List<InternalEntitySearchQuery<?>> entitySearchQueries) throws IOException;

    /**
     * Получить список объектов сущности конкретного типа из поискового хранилища в соответствии с заданными условиями отбора
     *
     * @param query условие отбора объектов сущности
     * @param <T>   тип объекта сущности
     */
    <T> InternalEntitySearchResult<T> find(InternalEntitySearchQuery<T> query);

    /**
     * Сохранить сущность в OpenSearch
     *
     * @param entityHolder сохраняемый объект сущности с дополнительной информацией
     * @param <T>          тип объекта сущности
     */
    <T> ActionResultType save(InternalEntityHolder<T> entityHolder) throws IOException;

    /**
     * Добавить сущность в OpenSearch (если сущность уже есть, будет ошибка)
     *
     * @param entityHolder добавляемый объект сущности с дополнительной информацией
     * @param withRefresh  признак необходимости refresh'а индекса после добавления (применять с осторожностью - может привести к падению производительности)
     * @param <T>          тип объекта сущности
     */
    <T> ActionResultType create(InternalEntityHolder<T> entityHolder, boolean withRefresh) throws IOException;

    /**
     * Получить объект сущности из OpenSearch по идентификатору
     *
     * @param entityClass  класс объекта сущности
     * @param entityName   наименование сущности
     * @param entityId     идентификатор объекта
     * @param routingValue значение роутинга запросов (при отсутствии поиск объекта будет произведен по всем шардам)
     * @param <T>          тип объекта сущности
     */
    <T> InternalEntityHolder<T> get(Class<T> entityClass, String entityName, String entityId, String routingValue) throws IOException;

    /**
     * Получить объект сущности из OpenSearch по идентификатору (если известно наименование индекса объекта сущности)
     *
     * @param entityClass  класс объекта сущности
     * @param entityName   наименование сущности
     * @param indexName    наименование поискового индекса объекта сущности
     * @param entityId     идентификатор объекта
     * @param routingValue значение роутинга запросов (при отсутствии поиск объекта будет произведен по всем шардам)
     * @param <T>          тип объекта сущности
     */
    <T> InternalEntityHolder<T> get(Class<T> entityClass, String entityName, String indexName, String entityId, String routingValue) throws IOException;

    /**
     * Удалить объект сущности из OpenSearch
     *
     * @param entityHolder удаляемый объект сущности с дополнительной информацией
     * @param <T>          тип объекта сущности
     */
    <T> void delete(InternalEntityHolder<T> entityHolder) throws IOException;
}
