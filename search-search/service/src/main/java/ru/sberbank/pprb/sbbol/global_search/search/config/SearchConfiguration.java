package ru.sberbank.pprb.sbbol.global_search.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntityService;
import ru.sberbank.pprb.sbbol.global_search.search.mapper.AccountEntityMapper;
import ru.sberbank.pprb.sbbol.global_search.search.mapper.PartnerEntityMapper;
import ru.sberbank.pprb.sbbol.global_search.search.mapper.SearchMapper;
import ru.sberbank.pprb.sbbol.global_search.search.mapper.SearchableEntityMapper;
import ru.sberbank.pprb.sbbol.global_search.search.mapper.SearchableEntityMapperRegistry;
import ru.sberbank.pprb.sbbol.global_search.search.model.Account;
import ru.sberbank.pprb.sbbol.global_search.search.model.AccountEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.DigitalIdRestriction;
import ru.sberbank.pprb.sbbol.global_search.search.model.Partner;
import ru.sberbank.pprb.sbbol.global_search.search.model.PartnerEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.Restriction;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.DigitalIdRestrictionConverter;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.RestrictionConverter;
import ru.sberbank.pprb.sbbol.global_search.search.restrictions.RestrictionConverterFactory;
import ru.sberbank.pprb.sbbol.global_search.search.service.SearchService;
import ru.sberbank.pprb.sbbol.global_search.search.service.SearchServiceImpl;
import ru.sberbank.pprb.sbbol.global_search.search.service.restrictions.RestrictionService;
import ru.sberbank.pprb.sbbol.global_search.search.service.restrictions.RestrictionServiceImpl;

import java.util.HashMap;
import java.util.List;
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
    RestrictionService restrictionService() {
        return new RestrictionServiceImpl();
    }

    @Bean
    SearchService searchService(
        SearchableEntityService searchableEntityService,
        SearchMapper searchMapper
    ) {
        return new SearchServiceImpl(
            searchableEntityService,
            restrictionConverterFactory(),
            restrictionService(),
            searchMapper
        );
    }

    @Bean
    SearchableEntityMapper<Partner, PartnerEntity> partnerSearchableEntityMapper() {
        return new PartnerEntityMapper();
    }

    @Bean
    SearchableEntityMapper<Account, AccountEntity> accountSearchableEntityMapper() {
        return new AccountEntityMapper();
    }

    @Bean
    SearchableEntityMapperRegistry searchableEntityMapperRegistry(List<SearchableEntityMapper> mapperList) {
        var mappers = new HashMap<Class<?>, SearchableEntityMapper>(mapperList.size());
        for (var mapper : mapperList) {
            mappers.put(mapper.getEntityClass(), mapper);
        }
        return new SearchableEntityMapperRegistry(mappers);
    }
}
