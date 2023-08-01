package ru.sberbank.pprb.sbbol.global_search.facade;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.OpenSearchException;
import org.opensearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.client.GetAliasesResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.rest.RestStatus;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OpenSearchIndexApiFacadeImpl implements OpenSearchIndexApiFacade {

    RestHighLevelClient restClient;

    @Override
    public  Collection<String> indexNames(String entityName) throws IOException {
        GetAliasesRequest request = new GetAliasesRequest(entityName);
        GetAliasesResponse response = restClient.indices().getAlias(request, RequestOptions.DEFAULT);
        return response.getAliases().keySet();
    }

    @Override
    public boolean removeIndex(String indexName) throws IOException {
        boolean removed = false;
        log.info("Удаление индекса '{}'", indexName);
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            restClient.indices().delete(request, RequestOptions.DEFAULT);
            log.info("Индекс '{}' успешно удален", indexName);
            removed = true;
        } catch (OpenSearchException e) {
            if (e.status() == RestStatus.NOT_FOUND) {
                log.warn("Удаление индекса '{}' завершилось с ошибкой. Индекс не найден", indexName);
            } else {
                throw e;
            }
        }
        return removed;
    }
}
