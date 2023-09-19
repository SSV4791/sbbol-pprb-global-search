package ru.sbrf.sbbol.search.updater.query;

import java.util.Date;

/**
 * Сведения о пролитом скрипте обновления Elasticsearch
 */
public class UpdaterQueryInfo {

    /**
     * Наименование файла
     */
    private String fileName;

    /**
     * Hash содержимого файла
     */
    private String fileHash;

    /**
     * Содержимое файла
     */
    private String fileContent;

    /**
     * Дата (со временем) выполнения скрипта
     */
    private Date updateDate;

    /**
     * Продолжительность выполнения скрипта
     */
    private Long duration;

    public String getFileName() {
        return fileName;
    }

    public UpdaterQueryInfo setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileHash() {
        return fileHash;
    }

    public UpdaterQueryInfo setFileHash(String fileHash) {
        this.fileHash = fileHash;
        return this;
    }

    public String getFileContent() {
        return fileContent;
    }

    public UpdaterQueryInfo setFileContent(String fileContent) {
        this.fileContent = fileContent;
        return this;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public UpdaterQueryInfo setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
        return this;
    }

    public Long getDuration() {
        return duration;
    }

    public UpdaterQueryInfo setDuration(Long duration) {
        this.duration = duration;
        return this;
    }
}
