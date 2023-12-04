package ru.sberbank.pprb.sbbol.global_search.sink.service;

import ru.sberbank.pprb.sbbol.global_search.search.common.ActionType;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;

/**
 * Сервис создания полнотекстового индекса для документа
 */
public interface SearchSinkService {

    /**
     * Метод по загрузке документа в полнотектовый индекс
     * @param searchableEntity - документ, загружаемый в полнотектовый индекс
     * @param actionType - тип действия над сохраняемой сущностью
     */
    void send(BaseSearchableEntity searchableEntity, String actionType);
}
