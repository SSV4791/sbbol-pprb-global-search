package ru.sberbank.pprb.sbbol.global_search.facade.auth;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;

/**
 * Поставщик учетных данных для авторизации в OpenSearch по логину и паролю
 */
public class NativeAuthorizationProvider implements AuthorizationProvider {

    @Value("${opensearch.auth.username}")
    private String username;

    @Value("${opensearch.auth.password}")
    private String password;

    @Override
    public CredentialsProvider create() {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
            AuthScope.ANY,
            new UsernamePasswordCredentials(username, password)
        );
        return provider;
    }
}
