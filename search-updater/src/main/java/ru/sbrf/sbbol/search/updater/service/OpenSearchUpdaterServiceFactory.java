package ru.sbrf.sbbol.search.updater.service;

import ru.sbrf.sbbol.search.updater.Stage;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Фабрика сервисов проливки обновлений в Elasticsearch
 */
public class OpenSearchUpdaterServiceFactory {

    /**
     * Сервисы проливки обновлений в Elasticsearch по стадиям
     */
    private Map<Stage, OpenSearchUpdaterService> stageServices;

    public OpenSearchUpdaterServiceFactory(Map<Stage, OpenSearchUpdaterService> stageServices) {
        this.stageServices = Collections.unmodifiableMap(new EnumMap<>(stageServices));
    }

    /**
     * Получить instance сервиса для заданной стадии
     *
     * @param stage стадия проливки обновлений
     */
    public OpenSearchUpdaterService getService(Stage stage) {
        return stageServices.get(stage);
    }
}
