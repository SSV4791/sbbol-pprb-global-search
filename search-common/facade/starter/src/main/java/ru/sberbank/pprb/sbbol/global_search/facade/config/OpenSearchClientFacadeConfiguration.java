package ru.sberbank.pprb.sbbol.global_search.facade.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchClientFacade;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchClientFacadeImpl;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchIndexApiFacade;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchIndexApiFacadeImpl;
import ru.sberbank.pprb.sbbol.global_search.facade.auth.AuthType;
import ru.sberbank.pprb.sbbol.global_search.facade.auth.AuthorizationProvider;
import ru.sberbank.pprb.sbbol.global_search.facade.auth.CredentialsProviderFactory;
import ru.sberbank.pprb.sbbol.global_search.facade.auth.NativeAuthorizationProvider;
import ru.sberbank.pprb.sbbol.global_search.facade.auth.NoneAuthorizationProvider;
import ru.sberbank.pprb.sbbol.global_search.facade.query.SearchFilterTokenExtractor;
import ru.sberbank.pprb.sbbol.global_search.facade.query.SearchFilterTokenExtractorImpl;

import java.util.EnumMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Stream;

@Configuration
public class OpenSearchClientFacadeConfiguration {

    @Value("${opensearch.urls}")
    private String[] openSearchUrls;
    @Value("${opensearch.auth.type}")
    private AuthType authType;
    @Value("${opensearch.timeout.request}")
    private int connectionRequestTimeout;
    @Value("${opensearch.timeout.connect}")
    private int connectTimeout;
    @Value("${opensearch.timeout.socket}")
    private int socketTimeout;

    @Bean
    @ConditionalOnMissingBean
    public RestClientBuilder restClient() {
        HttpHost[] hosts = Stream.of(openSearchUrls)
            .map(host -> host.replaceAll("[\\[\\]]", ""))
            .map(HttpHost::create)
            .toArray(HttpHost[]::new);
        return RestClient.builder(hosts)
            .setHttpClientConfigCallback(
                httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider().init(authType)))
            .setRequestConfigCallback(
                requestConfigBuilder -> requestConfigBuilder
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setSocketTimeout(socketTimeout));
    }

    private CredentialsProviderFactory credentialsProvider() {
        Map<AuthType, AuthorizationProvider> provider = new EnumMap<>(AuthType.class);
        provider.put(AuthType.NATIVE, nativeAuthenticationProvider());
        provider.put(AuthType.NONE, noneAuthenticationProvider());
        return new CredentialsProviderFactory(provider);
    }

    @Bean
    AuthorizationProvider noneAuthenticationProvider() {
        return new NoneAuthorizationProvider();
    }

    @Bean
    AuthorizationProvider nativeAuthenticationProvider() {
        return new NativeAuthorizationProvider();
    }

    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(restClient());
    }

    @Bean
    public OpenSearchClientFacade openSearchClientFacade() {
        return new OpenSearchClientFacadeImpl(
            restHighLevelClient(),
            tokenExtractor(),
            openSearchClientFacadeObjectMapper()
        );
    }

    @Bean
    public OpenSearchIndexApiFacade openSearchIndexApiFacade(RestHighLevelClient restHighLevelClient) {
        return new OpenSearchIndexApiFacadeImpl(restHighLevelClient);
    }

    @Bean
    public ObjectMapper openSearchClientFacadeObjectMapper() {
        return new ObjectMapper()
            .setTimeZone(TimeZone.getDefault())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public SearchFilterTokenExtractor tokenExtractor() {
        return new SearchFilterTokenExtractorImpl();
    }

}
