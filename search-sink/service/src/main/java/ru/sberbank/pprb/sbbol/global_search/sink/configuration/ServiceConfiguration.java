package ru.sberbank.pprb.sbbol.global_search.sink.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.global_search.sink.service.IndexingService;
import ru.sberbank.pprb.sbbol.global_search.sink.service.impl.IndexingServiceImpl;

@Configuration
public class ServiceConfiguration {

    @Bean
    IndexingService indexingService() {
        return new IndexingServiceImpl();
    }
}
