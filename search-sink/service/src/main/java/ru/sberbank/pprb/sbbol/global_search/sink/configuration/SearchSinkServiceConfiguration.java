package ru.sberbank.pprb.sbbol.global_search.sink.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchClientFacade;
import ru.sberbank.pprb.sbbol.global_search.sink.mapper.SearchSinkMapper;
import ru.sberbank.pprb.sbbol.global_search.sink.mapper.impl.SearchSinkMapperImpl;
import ru.sberbank.pprb.sbbol.global_search.sink.service.SearchSinkService;
import ru.sberbank.pprb.sbbol.global_search.sink.service.impl.SearchSinkServiceImpl;

@Configuration
public class SearchSinkServiceConfiguration {

    @Bean
    SearchSinkMapper searchSinkMapper() {
        return new SearchSinkMapperImpl();
    }

    @Bean
    SearchSinkService searchSinkService(OpenSearchClientFacade openSearchClientFacade, SearchSinkMapper searchSinkMapper) {
        return new SearchSinkServiceImpl(openSearchClientFacade, searchSinkMapper);
    }
}
