package ru.sbrf.sbbol.search.updater;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;
import ru.sbrf.sbbol.search.updater.config.OpenSearchUpdaterConfiguration;
import ru.sbrf.sbbol.search.updater.query.IndexQueryFile;
import ru.sbrf.sbbol.search.updater.service.OpenSearchUpdaterService;
import ru.sbrf.sbbol.search.updater.service.OpenSearchUpdaterServiceFactory;
import ru.sbrf.sbbol.search.updater.service.UpdaterQueriesIndexServiceImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OpenSearchUpdaterApplication {

    private static final Logger LOG = LoggerFactory.getLogger(OpenSearchUpdaterApplication.class);

    public static void main(String... args) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> LOG.error("Uncaught exception", e));
        try (ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(OpenSearchUpdaterConfiguration.class)) {

            Options options = new Options();
            for (OpenSearchUpdaterOption option : OpenSearchUpdaterOption.values()) {
                options.addOption(Option.builder()
                    .longOpt(option.getCode())
                    .desc(option.getDesc())
                    .hasArg(option.hasArg())
                    .required(option.required())
                    .build());
            }

            CommandLine commandLine = null;
            try {
                CommandLineParser parser = new DefaultParser();
                commandLine = parser.parse(options, args);
            } catch (ParseException e) {
                LOG.error("Parameters parsing error {}", e.getMessage());
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java " + OpenSearchUpdaterApplication.class.getName(), options);
                System.exit(1);
            }

            String pathParam = commandLine.getOptionValue(OpenSearchUpdaterOption.PATH.getCode());
            String stageParam = commandLine.getOptionValue(OpenSearchUpdaterOption.STAGE.getCode());
            String queryParam = commandLine.getOptionValue(OpenSearchUpdaterOption.QUERY.getCode());

            Collection<Stage> stages = StringUtils.hasText(stageParam) ?
                Collections.singletonList(Stage.valueOf(stageParam.toUpperCase())) :
                Arrays.asList(Stage.values());

            LOG.info("Start update with params: stage={} path={} ", stages, pathParam);

            OpenSearchUpdaterServiceFactory serviceFactory = context.getBean(OpenSearchUpdaterServiceFactory.class);
            UpdaterQueriesIndexServiceImpl queriesIndexService = context.getBean(UpdaterQueriesIndexServiceImpl.class);
            for (Stage stage : stages) {
                LOG.info("Start processing stage '{}'", stage.name());

                IndexQueryFilesReader parser = context.getBean(IndexQueryFilesReader.class);
                List<IndexQueryFile> queryFiles = parser.parseLocation(pathParam, stage.getRelativeDir(), queryParam);
                OpenSearchUpdaterService service = serviceFactory.getService(stage);

                for (IndexQueryFile queryFile : queryFiles) {
                    if (stage.isMandatoryExec() ||
                        Objects.equals(queryParam, queryFile.getName()) ||
                        queriesIndexService.isNew(queryFile) ||
                        (stage.isMultiExec() && queriesIndexService.isHashChanged(queryFile))) {
                        service.process(queryFile);
                    }
                }

                LOG.info("Finish processing stage '{}'", stage.name());
            }
        }
    }
}
