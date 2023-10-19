package ru.sberbank.pprb.sbbol.global_search.search.service;

import ru.sberbank.pprb.sbbol.global_search.search.model.SearchFilter;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchResponse;

/**
 * Сервис полнотекстового поиска
 */
public interface SearchService {

    /**
     * Получение списка сущностей в соответствии с заданными условиями отбора
     *
     * @param filter условия отбора сущностей
     */
    SearchResponse find(SearchFilter filter);
}
