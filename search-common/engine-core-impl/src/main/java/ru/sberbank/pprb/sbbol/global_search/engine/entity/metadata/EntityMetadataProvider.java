package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata;

/**
 * Провайдер метаданных классов сущностей, используемых в поисковом сервисе
 */
public interface EntityMetadataProvider {

    /**
     * Получить метаданные класса сущности
     *
     * @param clazz объект класса сущности
     * @param <T>   тип класса сущности
     */
    <T> EntityMetadataHolder<T> getMetadata(Class<T> clazz);
}
