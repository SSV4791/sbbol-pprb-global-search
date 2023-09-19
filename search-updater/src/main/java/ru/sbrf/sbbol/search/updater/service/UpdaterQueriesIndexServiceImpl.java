package ru.sbrf.sbbol.search.updater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbrf.sbbol.search.updater.query.IndexQueryFile;
import ru.sbrf.sbbol.search.updater.query.IndexQueryFileParam;
import ru.sbrf.sbbol.search.updater.query.UpdaterQueryInfo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Objects;

/**
 * Реализация сервиса работы с индексом пролитых скриптов
 */
public class UpdaterQueriesIndexServiceImpl implements UpdaterQueriesIndexService {

    private RestHighLevelClient restClient;

    private ObjectMapper mapper;

    private static final Logger LOG = LoggerFactory.getLogger(UpdaterQueriesIndexServiceImpl.class);

    private static final String UPDATER_QUERIES_INDEX_NAME = "updater_queries";

    public UpdaterQueriesIndexServiceImpl(RestHighLevelClient restClient, ObjectMapper mapper) {
        this.restClient = restClient;
        this.mapper = mapper;
    }

    @Override
    public boolean isNew(IndexQueryFile queryFile) throws IOException {
        return getQueryInfo(queryFile) == null;
    }

    @Override
    public boolean isHashChanged(IndexQueryFile queryFile) throws IOException {
        UpdaterQueryInfo queryInfo = getQueryInfo(queryFile);
        if (queryInfo != null) {
            return !Objects.equals(queryInfo.getFileHash(), calcHash(queryFile));
        }
        return true;
    }

    @Override
    public void updateQueryInfo(IndexQueryFile queryFile) throws IOException {
        String requestId = queryFile.getData().getParams().get(IndexQueryFileParam.GUID);
        IndexRequest request = new IndexRequest(UPDATER_QUERIES_INDEX_NAME)
            .id(requestId)
            .source(mapper.writeValueAsString(toQueryInfo(queryFile)), XContentType.JSON);
        restClient.index(request, RequestOptions.DEFAULT);
    }

    private String calcHash(IndexQueryFile queryFile) {
        LOG.debug("Calc hash for file: '{}'", queryFile.getName());
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(queryFile.getContent().toLowerCase().getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }
            LOG.debug("Hash = '{}'", result);
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("Can't calculate hash", e);
        }
    }

    private UpdaterQueryInfo getQueryInfo(IndexQueryFile queryFile) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(UPDATER_QUERIES_INDEX_NAME);
        if (restClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT)) {
            String requestId = queryFile.getData().getParams().get(IndexQueryFileParam.GUID);
            GetRequest request = new GetRequest(UPDATER_QUERIES_INDEX_NAME, requestId);
            String source = restClient.get(request, RequestOptions.DEFAULT).getSourceAsString();
            if (source != null) {
                return mapper.readValue(source, UpdaterQueryInfo.class);
            }
        }
        return null;
    }

    private UpdaterQueryInfo toQueryInfo(IndexQueryFile queryFile) {
        if (queryFile == null) {
            return null;
        }
        return new UpdaterQueryInfo()
            .setFileName(queryFile.getName())
            .setFileContent(queryFile.getContent())
            .setFileHash(calcHash(queryFile))
            .setUpdateDate(new Date());
    }
}
