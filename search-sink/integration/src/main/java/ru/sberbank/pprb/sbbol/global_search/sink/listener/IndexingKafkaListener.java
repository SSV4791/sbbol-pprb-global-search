package ru.sberbank.pprb.sbbol.global_search.sink.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.global_search.sink.service.IndexingService;

@Component
@Slf4j
public class IndexingKafkaListener {

    private final IndexingService indexingService;

    public IndexingKafkaListener(IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @Transactional
    @KafkaListener(
        topics = "${app.kafka.indexing.consumer.topic}",
        groupId = "${app.kafka.indexing.consumer.group-id}",
        containerFactory = "indexingKafkaListenerContainerFactory"
    )
    public void consume(String document) {
        indexingService.createSearchIndex(document);
    }
}
