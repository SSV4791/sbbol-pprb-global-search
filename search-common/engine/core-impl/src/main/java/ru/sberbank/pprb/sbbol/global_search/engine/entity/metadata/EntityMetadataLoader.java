package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata;

import ru.sberbank.pprb.sbbol.global_search.core.entity.NestedEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;

/**
 * Загрузчик метаданных классов сущностей, используемых в поисковом сервисе
 *
 * @see SearchableEntity
 * @see NestedEntity
 */
public interface EntityMetadataLoader {

    /**
     * Загрузить метаданные класса сущности, используемой в поисковом сервисе
     *
     * @param entityClass класс сущности
     * @param <T>         параметр типа класса сущности
     */
    <T> EntityMetadataHolder<T> load(Class<T> entityClass);
}
