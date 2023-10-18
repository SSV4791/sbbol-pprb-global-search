package ru.sbrf.sbbol.search.updater.query;

/**
 * Тип параметра скрипта обновления поискового индекса
 */
public enum IndexQueryParam {
    /**
     * Наименование сущности, доступной для полнотекстового поиска (alias индекса сущности в Elasticsearch)
     */
    ENTITY_NAME,

    /**
     * Список статусов ошибок, игнорируемых при проливке скрипта, через запятую
     * <p>
     * Например, {@code IGNORE_ERRORS=NOT_FOUND,CONFLICT}
     *
     */
    IGNORE_ERRORS,

    /**
     * Не проводить переиндексацию после обновления шаблона индекса
     * <p>
     * Параметр указывается для скриптов шаблонов. Если указать параметр для скрипта другого типа, он будет проигнорирован
     */
    SKIP_REINDEX,
}