package ru.sbrf.sbbol.search.updater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sbrf.sbbol.search.updater.parser.IndexQueryParser;
import ru.sbrf.sbbol.search.updater.parser.ParseException;
import ru.sbrf.sbbol.search.updater.query.IndexQueryFile;
import ru.sbrf.sbbol.search.updater.util.PathFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class IndexQueryFilesReader {

    private PlaceholderResolver placeholderResolver;

    private static final String SCRIPT_FILE_EXTENSION = ".est";

    private static final Logger LOG = LoggerFactory.getLogger(IndexQueryFilesReader.class);

    public IndexQueryFilesReader(PlaceholderResolver placeholderResolver) {
        this.placeholderResolver = placeholderResolver;
    }

    public List<IndexQueryFile> parseLocation(String basePath, String relativeDirPath, String queryParam) {
        LOG.info("Parsing '{}' directory", relativeDirPath);
        File dir = PathFactory.getFileInstance(basePath, relativeDirPath);
        LOG.debug("Full path '{}'", dir);
        LOG.info("Start parsing location: {}", dir);
        File[] files = dir.listFiles();
        if (files != null) {
            Arrays.sort(files, Comparator.comparing(File::getName));
            if (LOG.isInfoEnabled()) {
                StringBuilder logStr = new StringBuilder("Files found:");
                for (File file : files) {
                    logStr.append('\n').append(file.getPath());
                }
                LOG.info(logStr.toString());
            }
            List<IndexQueryFile> result = new ArrayList<>(files.length);
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(SCRIPT_FILE_EXTENSION) && (queryParam == null || Objects.equals(file.getName(), queryParam))) {
                    String content = "";
                    try {
                        content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                        String converted = placeholderResolver.resolve(content);
                        IndexQueryParser parser = new IndexQueryParser(new ByteArrayInputStream(converted.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8.name());
                        result.add(new IndexQueryFile(file.getName(), parser.parse(), converted));
                    } catch (ParseException | IOException e) {
                        throw new RuntimeException("Parse file error. File content:\n" + content, e);
                    }
                }
            }
            return result;
        } else {
            LOG.info("Location is empty");
        }
        return Collections.emptyList();
    }
}
