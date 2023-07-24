package ru.sberbank.pprb.sbbol.global_search.facade.auth;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;

/**
 * Поставщик учетных данных без авторизации в OpenSearch
 */
public class NoneAuthorizationProvider implements AuthorizationProvider {

    @Override
    public CredentialsProvider create() {
        return new BasicCredentialsProvider();
    }
}
