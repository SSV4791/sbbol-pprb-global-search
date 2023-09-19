package ru.sbrf.sbbol.search.updater.query;

/**
 * Файл скриптов запросов для внесения изменений в Elasticsearch
 */
public class IndexQueryFile {

    /**
     * Наименование файла
     */
    private String name;

    /**
     * Содержимое файла
     */
    private IndexQueryFileData data;

    /**
     * Содержимое файла в виде строки
     */
    private String content;

    public IndexQueryFile(String name, IndexQueryFileData data, String content) {
        this.name = name;
        this.data = data;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public IndexQueryFileData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "IndexQueryFile{" +
            "name='" + name + '\'' +
            ", data=" + data +
            ", content='" + content + '\'' +
            '}';
    }
}
