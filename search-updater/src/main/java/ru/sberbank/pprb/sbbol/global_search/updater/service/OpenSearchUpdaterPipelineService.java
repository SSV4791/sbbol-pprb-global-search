package ru.sberbank.pprb.sbbol.global_search.updater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.client.RestHighLevelClient;
import ru.sberbank.pprb.sbbol.global_search.updater.query.IndexQuery;

import java.io.IOException;

/**
 * Реализация сервиса проливки обновлений pipeline'ов в Elasticsearch
 */
public class OpenSearchUpdaterPipelineService extends OpenSearchUpdaterServiceImpl {

    private static final String PIPELINE_ENDPOINT_PREFIX = "_ingest/pipeline";

    public OpenSearchUpdaterPipelineService(RestHighLevelClient restClient, ObjectMapper mapper, UpdaterQueriesIndexService queriesIndexService) {
        super(restClient, mapper, queriesIndexService);
    }

    @Override
    protected void processQuery(IndexQuery query) throws IOException {
        if (!query.getEndpoint().startsWith(PIPELINE_ENDPOINT_PREFIX)) {
            throw new IllegalArgumentException("Unexpected endpoint. Only \"" + PIPELINE_ENDPOINT_PREFIX + "\" allowed");
        }
        super.execQuery(query);
    }
}
