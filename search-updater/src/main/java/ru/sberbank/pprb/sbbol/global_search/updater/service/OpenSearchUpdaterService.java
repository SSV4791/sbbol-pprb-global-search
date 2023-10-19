package ru.sberbank.pprb.sbbol.global_search.updater.service;

import ru.sberbank.pprb.sbbol.global_search.updater.query.IndexQueryFile;

/**
 * Сервис проливки обновлений в Opensearch
 */
public interface OpenSearchUpdaterService {

    /**
     * Выполнить файл обновлений
     *
     * @param queryFile файл обновлений
     */
    void process(IndexQueryFile queryFile);

}
