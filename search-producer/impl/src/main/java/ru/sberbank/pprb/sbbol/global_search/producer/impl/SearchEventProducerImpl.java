package ru.sberbank.pprb.sbbol.global_search.producer.impl;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import ru.sberbank.pprb.sbbol.global_search.producer.api.SearchEvent;
import ru.sberbank.pprb.sbbol.global_search.producer.api.SearchEventProducer;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static ru.sberbank.pprb.sbbol.global_search.search.common.SearchKafkaHeaders.ACTION_TYPE;

public class SearchEventProducerImpl implements SearchEventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(SearchEventProducerImpl.class);

    @Value("${global_search.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, BaseSearchableEntity> kafkaTemplate;

    public SearchEventProducerImpl(KafkaTemplate<String, BaseSearchableEntity> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(SearchEvent<? extends BaseSearchableEntity> event) {
        LOG.debug("Отправляем поисковое сообщение  {} в топик {} кафки", event, topic);
        Integer partition = null;
        Long timestamp = null;
        var key = event.getSearchableEntity().getEntityId().toString();
        var value = event.getSearchableEntity();
        var actionType = event.getActionType().toString().getBytes(StandardCharsets.UTF_8);
        List<Header> headers = List.of(new RecordHeader(ACTION_TYPE, actionType));
        ProducerRecord<String, BaseSearchableEntity> record = new ProducerRecord<>(topic, partition, timestamp, key, value, headers);
        ListenableFuture<SendResult<String, BaseSearchableEntity>> future = kafkaTemplate.send(record);
        future.addCallback(new ProcessMessageCallback<>(value));
    }
}
