package ru.sberbank.pprb.sbbol.global_search.producer.api;

import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;

/**
 * Producer сообщений в кафку системы Глобального Поиска
 */
public interface SearchEventProducer {

    /**
     *
     * @param event сообщение для отправки в систему Глобального Поиска
     */
    void produce(SearchEvent<? extends BaseSearchableEntity> event);
}
