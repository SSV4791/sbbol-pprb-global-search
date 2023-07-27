package ru.sberbank.pprb.sbbol.global_search.engine.entity;

import java.util.Collection;

/**
 * Пути к пакетам, в которых описаны классы объектов сущностей, доступных в поисковом сервисе
 */
public interface SearchableEntitiesLocationHolder {

    /**
     * Получить пути к пакетам сущностей, доступных в поисковом сервисе
     */
    Collection<String> getBasePackages();
}
