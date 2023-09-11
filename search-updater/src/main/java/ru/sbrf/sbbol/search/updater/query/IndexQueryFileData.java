package ru.sbrf.sbbol.search.updater.query;

import java.util.Collection;
import java.util.Map;

/**
 * Содержимое файла скриптов запросов для внесения изменений в Elasticsearch
 */
public class IndexQueryFileData {

    /**
     * Глобальные параметры файла
     */
    private Map<IndexQueryFileParam, String> params;

    /**
     * Скрипты
     */
    private Collection<IndexQuery> requests;

    public IndexQueryFileData(Map<IndexQueryFileParam, String> params, Collection<IndexQuery> requests) {
        this.params = params;
        this.requests = requests;
    }

    public Map<IndexQueryFileParam, String> getParams() {
        return params;
    }

    public Collection<IndexQuery> getRequests() {
        return requests;
    }

    @Override
    public String toString() {
        return "IndexQueryFileData{" +
            "params=" + params +
            ", requests=" + requests +
            '}';
    }
}
