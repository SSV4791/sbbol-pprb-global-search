package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataLoader;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.SearchableEntitiesLocationScanner;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Реализация провайдера метаданных классов сущностей, используемых в поисковом сервисе
 */
@Slf4j
public class EntityMetadataProviderImpl implements EntityMetadataProvider {

    private final EntityMetadataLoader metadataLoader;

    private final SearchableEntitiesLocationScanner locationScanner;

    /**
     * кеш метаданных классов сущностей
     * <p>
     * в качестве ключа выступает объект класса сущности
     */
    private final ConcurrentMap<Class<?>, EntityMetadataHolder<?>> metadataStore = new ConcurrentHashMap<>();

    public EntityMetadataProviderImpl(EntityMetadataLoader metadataLoader, SearchableEntitiesLocationScanner locationScanner) {
        this.metadataLoader = metadataLoader;
        this.locationScanner = locationScanner;
    }

    @Override
    public <T> EntityMetadataHolder<T> getMetadata(Class<T> clazz) {
        log.debug("Получение метаданных для класса {}", clazz.getName());
        EntityMetadataHolder<?> holder = metadataStore.computeIfAbsent(clazz, metadataLoader::load);
        if (holder == null) {
            throw new IllegalStateException("Не удалось получить метаданные для класса " + clazz.getName());
        }
        @SuppressWarnings("unchecked")
        EntityMetadataHolder<T> result = (EntityMetadataHolder<T>) holder;
        log.debug("Метаданные для класса {} -> {}", clazz.getName(), result);
        return result;
    }

    @PostConstruct
    public void init() {
        Collection<Class<?>> searchableEntityClasses = locationScanner.getSearchableEntityClasses();
        log.info("Инициализация провайдера метаданных классов сущностей, используемых в поисковом сервисе, для классов: {}", searchableEntityClasses);
        for (Class<?> clazz : searchableEntityClasses) {
            EntityMetadataHolder<?> holder = metadataStore.computeIfAbsent(clazz, metadataLoader::load);
            if (holder == null) {
                throw new IllegalStateException("Не удалось загрузить метаданные для класса " + clazz.getName());
            }
        }
    }
}
