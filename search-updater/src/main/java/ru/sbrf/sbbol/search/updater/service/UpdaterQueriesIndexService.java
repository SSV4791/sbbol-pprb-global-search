package ru.sbrf.sbbol.search.updater.service;

import ru.sbrf.sbbol.search.updater.query.IndexQueryFile;

import java.io.IOException;

/**
 * Сервис работы с индексом пролитых скриптов
 */
public interface UpdaterQueriesIndexService {

    /**
     * Проверить, что файл еще ни разу не был пролит
     *
     * @param queryFile файл скриптов проливки
     * @return <b>true</b>, если файл ранее не проливался
     */
    boolean isNew(IndexQueryFile queryFile) throws IOException;

    /**
     * Проверить, что hash от содержимого файла изменился с момента предыдущей проливки
     *
     * @param queryFile файл скриптов проливки
     * @return <b>true</b>, если hash изменился (либо файл ранее не проливался)
     */
    boolean isHashChanged(IndexQueryFile queryFile) throws IOException;

    /**
     * Обновить сведения о файле скриптов в индексе пролитых скриптов
     *
     * @param queryFile файл скриптов проливки
     */
    void updateQueryInfo(IndexQueryFile queryFile) throws IOException;
}
