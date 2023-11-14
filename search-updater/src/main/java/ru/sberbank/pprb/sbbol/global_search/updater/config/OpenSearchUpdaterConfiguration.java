package ru.sberbank.pprb.sbbol.global_search.updater.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import ru.sberbank.pprb.sbbol.global_search.facade.config.OpenSearchClientFacadeConfiguration;
import ru.sberbank.pprb.sbbol.global_search.updater.IndexQueryFilesReader;
import ru.sberbank.pprb.sbbol.global_search.updater.PlaceholderResolver;
import ru.sberbank.pprb.sbbol.global_search.updater.Stage;
import ru.sberbank.pprb.sbbol.global_search.updater.config.factory.YamlPropertySourceFactory;
import ru.sberbank.pprb.sbbol.global_search.updater.service.OpenSearchUpdaterPipelineService;
import ru.sberbank.pprb.sbbol.global_search.updater.service.OpenSearchUpdaterService;
import ru.sberbank.pprb.sbbol.global_search.updater.service.OpenSearchUpdaterServiceFactory;
import ru.sberbank.pprb.sbbol.global_search.updater.service.OpenSearchUpdaterServiceImpl;
import ru.sberbank.pprb.sbbol.global_search.updater.service.OpenSearchUpdaterTemplateService;
import ru.sberbank.pprb.sbbol.global_search.updater.service.UpdaterQueriesIndexService;
import ru.sberbank.pprb.sbbol.global_search.updater.service.UpdaterQueriesIndexServiceImpl;

import java.util.EnumMap;
import java.util.Map;

@Configuration
@Import(OpenSearchClientFacadeConfiguration.class)
@PropertySource(value = "file:${config}", factory = YamlPropertySourceFactory.class)
public class OpenSearchUpdaterConfiguration {

    @Value("${template.replicas.disable:false}")
    private boolean replicasDisable;

    @Value("${template.refresh_interval.disable:false}")
    private boolean refreshIntervalDisable;

    @Value("${template.translog.disable:false}")
    private boolean translogDisable;

    @Value("${template.reindex_batch_size:1000}")
    private int reindexBatchSize;

    @Value("${template.reindex.slices:0}")
    private int reindexSlices;

    private final OpenSearchClientFacadeConfiguration facadeConfiguration;

    public OpenSearchUpdaterConfiguration(OpenSearchClientFacadeConfiguration facadeConfiguration) {
        this.facadeConfiguration = facadeConfiguration;
    }

    @Bean
    IndexQueryFilesReader scriptReader() {
        return new IndexQueryFilesReader(placeholderResolver());
    }

    @Bean
    OpenSearchUpdaterServiceFactory openSearchUpdaterServiceFactory() {
        Map<Stage, OpenSearchUpdaterService> stageServices = new EnumMap<>(Stage.class);
        stageServices.put(Stage.SELF, openSearchUpdaterService());
        stageServices.put(Stage.MANDATORY_BEFORE, openSearchUpdaterService());
        stageServices.put(Stage.BEFORE, openSearchUpdaterService());
        stageServices.put(Stage.PIPELINE, openSearchUpdatePipelineService());
        stageServices.put(Stage.TEMPLATE, openSearchUpdaterTemplateService());
        stageServices.put(Stage.AFTER, openSearchUpdaterService());
        return new OpenSearchUpdaterServiceFactory(stageServices);
    }

    @Bean
    OpenSearchUpdaterService openSearchUpdaterService() {
        return new OpenSearchUpdaterServiceImpl(facadeConfiguration.restHighLevelClient(),
            facadeConfiguration.openSearchClientFacadeObjectMapper(),
            updaterQueriesIndexService());
    }

    @Bean
    OpenSearchUpdaterService openSearchUpdatePipelineService() {
        return new OpenSearchUpdaterPipelineService(facadeConfiguration.restHighLevelClient(),
            facadeConfiguration.openSearchClientFacadeObjectMapper(),
            updaterQueriesIndexService());
    }

    @Bean
    OpenSearchUpdaterService openSearchUpdaterTemplateService() {
        return new OpenSearchUpdaterTemplateService(facadeConfiguration.restHighLevelClient(),
            facadeConfiguration.openSearchClientFacadeObjectMapper(),
            updaterQueriesIndexService(),
            replicasDisable,
            refreshIntervalDisable,
            translogDisable,
            reindexBatchSize,
            reindexSlices
        );
    }


    @Bean
    UpdaterQueriesIndexService updaterQueriesIndexService() {
        return new UpdaterQueriesIndexServiceImpl(facadeConfiguration.restHighLevelClient(),
            facadeConfiguration.openSearchClientFacadeObjectMapper());
    }

    @Bean
    PlaceholderResolver placeholderResolver() {
        return new PlaceholderResolver();
    }

    @Bean
    static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
