package ru.sbrf.sbbol.search.updater.service;

import ru.sbrf.sbbol.search.updater.query.IndexQueryFile;

/**
 * Сервис проливки обновлений в Elasticsearch
 */
public interface OpenSearchUpdaterService {

    /**
     * Выполнить файл обновлений
     *
     * @param queryFile файл обновлений
     */
    void process(IndexQueryFile queryFile);

}
