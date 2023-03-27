package ru.sberbank.pprb.sbbol.global_search.facade.query;

import ru.sberbank.pprb.sbbol.global_search.facade.search.InternalEntitySearchQuery;

import java.util.Collection;

/**
 * Интерфейс extractor`а токенов из поискового запроса
 */
public interface SearchFilterTokenExtractor {

    /**
     * Разобрать поисковый запрос на токены
     *
     * @param entitySearchQuery условия отбора сущностей
     */
    Collection<String> extract(InternalEntitySearchQuery<?> entitySearchQuery);
}
