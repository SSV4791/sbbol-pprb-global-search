package ru.sberbank.pprb.sbbol.global_search.producer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import ru.sberbank.pprb.sbbol.global_search.producer.api.SearchEvent;
import ru.sberbank.pprb.sbbol.global_search.producer.api.SearchEventProducer;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;

public class SearchEventProducerImpl implements SearchEventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(SearchEventProducerImpl.class);

    @Value("${global-search.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, BaseSearchableEntity> kafkaTemplate;

    public SearchEventProducerImpl(KafkaTemplate<String, BaseSearchableEntity> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(SearchEvent<? extends BaseSearchableEntity> event) {
        LOG.debug("Отправляем поисковое сообщение  {} в топик {} кафки", event, topic);
        var searchableEntity = event.getSearchableEntity();
        var key = searchableEntity.getEntityId().toString();
        ListenableFuture<SendResult<String, BaseSearchableEntity>> future = kafkaTemplate.send(topic, key, searchableEntity);
        future.addCallback(new ProcessMessageCallback<>(searchableEntity));
    }
}
