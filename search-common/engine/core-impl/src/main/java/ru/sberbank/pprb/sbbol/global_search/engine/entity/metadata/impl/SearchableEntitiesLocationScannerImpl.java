package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.global_search.core.entity.NestedEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntitiesLocationHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.SearchableEntitiesLocationScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сканера классов сущностей, доступных в поисковом сервисе
 */
@Slf4j
public class SearchableEntitiesLocationScannerImpl implements SearchableEntitiesLocationScanner {

    private final List<SearchableEntitiesLocationHolder> locationHolders;

    public SearchableEntitiesLocationScannerImpl(Collection<SearchableEntitiesLocationHolder> locationHolders) {
        this.locationHolders = Collections.unmodifiableList(new ArrayList<>(locationHolders));
    }

    @Override
    public Collection<Class<?>> getSearchableEntityClasses() {
        Collection<Class<?>> searchableEntityClasses = new HashSet<>();
        Collection<String> packageNames = locationHolders.stream()
            .flatMap(locationHolder -> locationHolder.getBasePackages().stream())
            .collect(Collectors.toSet());
        for (String packageName : packageNames) {
            log.debug("Поиск классов сущностей, используемых в поиске, в пакете {} (включая вложенные)", packageName);
            List<ClassLoader> list = new ArrayList<>();
            for (ClassLoader classLoader : Arrays.asList(ClasspathHelper.contextClassLoader(),
                ClasspathHelper.staticClassLoader(), this.getClass().getClassLoader())) {
                if (classLoader != null) {
                    list.add(classLoader);
                }
            }
            ClassLoader[] classLoaders = list.toArray(new ClassLoader[0]);

            Configuration config = ConfigurationBuilder.build(packageName, new TypeAnnotationsScanner(), new SubTypesScanner(), classLoaders);

            Reflections reflections = new Reflections(config);
            Collection<Class<?>> entities = reflections.getTypesAnnotatedWith(SearchableEntity.class);
            log.debug("В пакете {} найдены классы, отмеченные аннотацией SearchableEntity: {}", packageName, entities);
            searchableEntityClasses.addAll(entities);
            Collection<Class<?>> nestedEntities = reflections.getTypesAnnotatedWith(NestedEntity.class);
            log.debug("В пакете {} найдены классы, отмеченные аннотацией NestedEntity: {}", packageName, nestedEntities);
            searchableEntityClasses.addAll(nestedEntities);
        }
        return searchableEntityClasses;
    }
}
