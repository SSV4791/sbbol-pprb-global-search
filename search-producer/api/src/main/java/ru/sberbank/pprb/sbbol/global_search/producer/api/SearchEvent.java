package ru.sberbank.pprb.sbbol.global_search.producer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sberbank.pprb.sbbol.global_search.search.common.ActionType;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;

/**
 * Поисковое сообщение
 * @param <T> Поисковая сущность
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchEvent<T extends BaseSearchableEntity> {

    /**
     * Поисковая сущность
     */
    private T searchableEntity;

    /**
     * Тип действия над поисковой сущностью
     */
    private ActionType actionType;
}
