package ru.sberbank.pprb.sbbol.global_search.search.model;

/**
 * Результат проверки ограничений поискового фильтра для сущности
 */
public enum RestrictionCheckResult {

    /**
     * Проверка прошла успешно
     */
    OK,

    /**
     * Присутствуют не все обязательные ограничения
     */
    NOT_ENOUGH_MANDATORY_RESTRICTIONS,

    /**
     * Присутствует неизвестное ограничение
     */
    UNKNOWN_RESTRICTION
}
