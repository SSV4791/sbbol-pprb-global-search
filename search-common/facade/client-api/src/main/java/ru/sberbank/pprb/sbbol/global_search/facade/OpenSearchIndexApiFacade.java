package ru.sberbank.pprb.sbbol.global_search.facade;

import java.io.IOException;
import java.util.Collection;

/**
 * Фасад к API OpenSearch по работе с индексами
 */
public interface OpenSearchIndexApiFacade {

    /**
     * Получить коллекцию наименований индексов для сущности по ее наименованию
     *
     * @param entityName наименование сущности
     */
    Collection<String> indexNames(String entityName) throws IOException;

    /**
     * Удалить индекс по наименованию
     *
     * @param indexName наименование удаляемого индекса
     */
    boolean removeIndex(String indexName) throws IOException;
}
