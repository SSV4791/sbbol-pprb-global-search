package ru.sbrf.sbbol.search.updater;

/**
 * Стадия проливки изменений в Elasticsearch
 */
public enum Stage {
    /**
     * Обновление индексов самого проливщика
     */
    SELF("self", false, false),
    /**
     * Выполнение скриптов, обязательно проливаемых до основных скриптов
     */
    MANDATORY_BEFORE("mandatory-before", true, true),
    /**
     * Выполнение скриптов, проливаемых до основных скриптов
     */
    BEFORE("before", false, false),
    /**
     * Основные скрипты проливки pipeline'ов
     */
    PIPELINE("pipeline", true, false),
    /**
     * Основные скрипты проливки шаблонов индексов
     */
    TEMPLATE("template", true, false),
    /**
     * Выполнение скриптов, проливаемых после основных скриптов
     */
    AFTER("after", false, false);

    /**
     * Относительный путь к каталогу скриптов стадии
     */
    private String relativeDir;

    /**
     * Признак возможности неоднократной проливки скриптов стадии
     */
    private boolean multiExec;

    /**
     * Признак обязательной проливки скриптов на стадии
     */
    private boolean  mandatoryExec;

    Stage(String relativeDir, boolean multiExec, boolean mandatoryExec) {
        this.relativeDir = relativeDir;
        this.multiExec = multiExec;
        this.mandatoryExec = mandatoryExec;
    }

    public String getRelativeDir() {
        return relativeDir;
    }

    public boolean isMultiExec() {
        return multiExec;
    }

    public boolean isMandatoryExec() {
        return mandatoryExec;
    }
}
