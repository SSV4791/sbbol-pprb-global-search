package ru.sberbank.pprb.sbbol.global_search.search.restrictions;


import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.Condition;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.Restriction;

/**
 * Конвертор преобразования ограничения фильтра в условие запроса
 *
 * @param <T> тип ограничения фильтра
 */
public interface RestrictionConverter<T extends Restriction> {

    /**
     * Получить условие запроса по дополнительному ограничению фильтра
     *
     * @param restriction дополнительное ограничение фильтра
     */
    Condition getCondition(T restriction);
}
