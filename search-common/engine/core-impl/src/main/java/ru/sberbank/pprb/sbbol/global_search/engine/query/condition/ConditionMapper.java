package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

/**
 * Маппер условий запроса к поисковому сервису в условия к OpenSearch
 */
public interface ConditionMapper {

    /**
     * Преобразовать условие запроса к поисковому сервису в условие к OpenSearch
     *
     * @param source условие запроса к поисковому сервису
     */
    ru.sberbank.pprb.sbbol.global_search.facade.query.condition.Condition map(Condition source);
}
