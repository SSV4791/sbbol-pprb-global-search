package ru.sberbank.pprb.sbbol.global_search.facade.auth;

import org.apache.http.client.CredentialsProvider;

/**
 * Сервис авторизации в OpenSearch
 */
public interface AuthorizationProvider {

    /**
     * Создание поставщика учетных данных для авторизации в OpenSearch
     */
    CredentialsProvider create();
}
