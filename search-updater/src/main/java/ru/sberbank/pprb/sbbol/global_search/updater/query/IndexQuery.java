package ru.sberbank.pprb.sbbol.global_search.updater.query;

import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * Скрипт запроса для внесения изменений в Elasticsearch
 */
public class IndexQuery {

    /**
     * HTTP-метод запроса
     */
    private HttpMethod httpMethod;

    /**
     * URL запроса
     */
    private String endpoint;

    /**
     * Тело запроса
     */
    private Map<String, Object> jsonBody;

    /**
     * Параметры запроса
     */
    private Map<IndexQueryParam, String> params;

    public IndexQuery(HttpMethod httpMethod, String endpoint, Map<String, Object> jsonBody, Map<IndexQueryParam, String> params) {
        this.httpMethod = httpMethod;
        this.endpoint = endpoint;
        this.jsonBody = jsonBody;
        this.params = params;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Map<String, Object> getJsonBody() {
        return jsonBody;
    }

    public Map<IndexQueryParam, String> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "IndexQuery{" +
            "httpMethod=" + httpMethod +
            ", endpoint='" + endpoint + '\'' +
            ", jsonBody=" + jsonBody +
            ", params=" + params +
            '}';
    }
}
