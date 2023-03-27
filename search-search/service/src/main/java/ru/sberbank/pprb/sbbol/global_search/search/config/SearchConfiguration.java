package ru.sberbank.pprb.sbbol.global_search.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntityService;
import ru.sberbank.pprb.sbbol.global_search.search.SearchServiceImpl;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.DigitalIdRestriction;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.Restriction;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.DigitalIdRestrictionConverter;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.RestrictionConverter;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.RestrictionConverterFactory;
import ru.sberbank.pprb.sbbol.global_search.search.service.SearchService;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SearchConfiguration {

    @Bean
    RestrictionConverterFactory restrictionConverterFactory() {
        Map<Class<? extends Restriction>, RestrictionConverter> converters = new HashMap<>();
        converters.put(DigitalIdRestriction.class, new DigitalIdRestrictionConverter());
        return new RestrictionConverterFactory(converters);
    }

    @Bean
    SearchService searchService(SearchableEntityService searchableEntityService) {
        return new SearchServiceImpl(searchableEntityService, restrictionConverterFactory());
    }
}
