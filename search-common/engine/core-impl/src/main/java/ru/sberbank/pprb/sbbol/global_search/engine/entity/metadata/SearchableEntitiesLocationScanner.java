package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata;

import ru.sberbank.pprb.sbbol.global_search.core.entity.NestedEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;

import java.util.Collection;

/**
 * Сканер классов сущностей, доступных в поисковом сервисе
 */
public interface SearchableEntitiesLocationScanner {

    /**
     * Получить классы сущностей, доступных в поисковом сервисе
     *
     * @see SearchableEntity
     * @see NestedEntity
     */
    Collection<Class<?>> getSearchableEntityClasses();
}
