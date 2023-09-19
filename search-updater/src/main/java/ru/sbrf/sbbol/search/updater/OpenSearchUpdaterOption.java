package ru.sbrf.sbbol.search.updater;

/**
 * Опция параметра командной строки
 */
public enum OpenSearchUpdaterOption {
    /**
     * Стадия
     */
    STAGE("stage", "Стадия проливки скрипта", true, false),
    /**
     * Путь к каталогу скриптов обновления
     */
    PATH("path", "Путь к каталогу скриптов обновления", true, true),
    /**
     * Наименование скрипта праливки
     */
    QUERY("query", "Наименование скрипта", true, false);

    /**
     * Код опции
     */
    private String code;

    /**
     * Описание
     */
    private String desc;

    /**
     * Признак наличия аргументов
     */
    private boolean hasArg;

    /**
     * Признак обязательности опции
     */
    private boolean required;

    OpenSearchUpdaterOption(String code, String desc, boolean hasArg, boolean required) {
        this.code = code;
        this.desc = desc;
        this.hasArg = hasArg;
        this.required = required;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public boolean hasArg() {
        return hasArg;
    }

    public boolean required() {
        return required;
    }}
