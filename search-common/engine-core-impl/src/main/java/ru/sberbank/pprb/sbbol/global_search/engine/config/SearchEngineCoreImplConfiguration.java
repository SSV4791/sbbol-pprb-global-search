package ru.sberbank.pprb.sbbol.global_search.engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.SpringBeanProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntitiesLocationHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntityService;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntityServiceImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataLoader;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityToInternalEntityHolderConverter;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.SearchableEntitiesLocationScanner;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl.EntityMetadataLoaderImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl.EntityMetadataProviderImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl.EntityToInternalEntityHolderConverterImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl.SearchableEntitiesLocationScannerImpl;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchClientFacade;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchIndexApiFacade;
import ru.sberbank.pprb.sbbol.global_search.engine.index.IndexLifecycleService;
import ru.sberbank.pprb.sbbol.global_search.engine.index.IndexLifecycleServiceImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.ConditionMapper;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.impl.ConditionMapperImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Configuration
public class SearchEngineCoreImplConfiguration {

    @Bean
    public BeanProvider beanProvider() {
        return new SpringBeanProvider();
    }

    @Bean
    public SearchableEntityService searchableEntityService(OpenSearchClientFacade facade,
                                                           EntityToInternalEntityHolderConverter converter,
                                                           EntityMetadataProvider metadataProvider,
                                                           ConditionMapper conditionMapper
    ) {
        return new SearchableEntityServiceImpl(facade, converter, metadataProvider, conditionMapper);
    }

    @Bean
    public IndexLifecycleService indexLifecycleService(OpenSearchIndexApiFacade indexApiFacade, EntityMetadataProvider metadataProvider) {
        return new IndexLifecycleServiceImpl(indexApiFacade, metadataProvider);
    }

    @Bean
    EntityToInternalEntityHolderConverter entityToInternalEntityHolderConverter(EntityMetadataProvider metadataProvider) {
        return new EntityToInternalEntityHolderConverterImpl(metadataProvider);
    }

    @Bean
    EntityMetadataProvider metadataProvider(EntityMetadataLoader metadataLoader, SearchableEntitiesLocationScanner locationScanner) {
        return new EntityMetadataProviderImpl(metadataLoader, locationScanner);
    }

    @Bean
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    SearchableEntitiesLocationScanner locationScanner(Optional<List<SearchableEntitiesLocationHolder>> locationHolders) {
        return new SearchableEntitiesLocationScannerImpl(locationHolders.orElseGet(Collections::emptyList));
    }

    @Bean
    EntityMetadataLoader metadataLoader(BeanProvider beanProvider) {
        return new EntityMetadataLoaderImpl(beanProvider);
    }

    @Bean
    ConditionMapper conditionMapper() {
        return new ConditionMapperImpl();
    }
}
