package ru.sberbank.pprb.sbbol.global_search.engine.entity.impl;

import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntitiesLocationHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Пути к пакетам, в которых описаны классы объектов сущностей, доступных в поисковом сервисе. Реализация по умолчанию
 */
public class DefaultSearchableEntitiesLocationHolder implements SearchableEntitiesLocationHolder {

    private final Collection<String> basePackages = new ArrayList<>();

    public DefaultSearchableEntitiesLocationHolder(String... basePackages) {
        this.basePackages.addAll(Arrays.asList(basePackages));
    }

    @Override
    public Collection<String> getBasePackages() {
        return basePackages;
    }
}
