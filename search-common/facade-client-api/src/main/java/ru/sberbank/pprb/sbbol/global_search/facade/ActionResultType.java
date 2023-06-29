package ru.sberbank.pprb.sbbol.global_search.facade;

/**
 * Результат действия над объектом сущности в поисковом сервисе
 */
public enum ActionResultType {
    /**
     * Действие завершено успешно
     */
    SUCCESS,
    /**
     * В результате выполнения действия возник конфликт версии объекта
     * (например, если версия изменяемого объекта меньше либо равна версии этого же объекта, хранящегося в поисковом сервисе)
     */
    VERSION_CONFLICT
}
