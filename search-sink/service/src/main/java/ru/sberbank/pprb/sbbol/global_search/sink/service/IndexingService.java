package ru.sberbank.pprb.sbbol.global_search.sink.service;

/**
 * Сервис создания полнотекстового индекса для документа
 */
public interface IndexingService {

    /**
     * Метод по загрузке документа в полнотектовый индекс
     * @param document - документ, загружаемый в полнотектовый индекс
     */
    void createSearchIndex(String document);
}
