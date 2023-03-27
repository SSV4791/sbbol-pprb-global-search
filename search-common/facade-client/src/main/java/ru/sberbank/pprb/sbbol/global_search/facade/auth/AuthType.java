package ru.sberbank.pprb.sbbol.global_search.facade.auth;

/**
 * Типы авторизации в OpenSearch
 */
public enum AuthType {
    /**
     * Авторизация по логину и паролю
     */
    NATIVE,

    /**
     * Без авторизации
     */
    NONE
}
