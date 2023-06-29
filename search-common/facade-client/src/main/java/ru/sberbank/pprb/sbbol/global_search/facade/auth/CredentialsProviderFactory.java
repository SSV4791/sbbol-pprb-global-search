package ru.sberbank.pprb.sbbol.global_search.facade.auth;

import org.apache.http.client.CredentialsProvider;

import java.util.Map;

public class CredentialsProviderFactory {

    private Map<AuthType, AuthorizationProvider> provider;

    public CredentialsProviderFactory(Map<AuthType, AuthorizationProvider> provider) {
        this.provider = provider;
    }

    public CredentialsProvider init(AuthType type) {
        AuthorizationProvider authorizationProvider = provider.get(type);
        return authorizationProvider.create();
    }
}
