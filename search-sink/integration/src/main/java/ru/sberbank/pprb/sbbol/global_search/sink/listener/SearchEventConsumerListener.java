package ru.sberbank.pprb.sbbol.global_search.sink.listener;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.transaction.annotation.Transactional;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.sink.service.SearchSinkService;

import static ru.sberbank.pprb.sbbol.global_search.search.common.SearchKafkaHeaders.ACTION_TYPE;

@Slf4j
public class SearchEventConsumerListener {

    private static final Logger LOG = LoggerFactory.getLogger(SearchEventConsumerListener.class);

    private final SearchSinkService searchSinkService;

    public SearchEventConsumerListener(SearchSinkService searchSinkService) {
        this.searchSinkService = searchSinkService;
    }

    @Transactional
    @KafkaListener(
        topics = "${global_search.kafka.consumer.topic}",
        groupId = "${global_search.kafka.consumer.group-id}",
        containerFactory = "searchKafkaListenerContainerFactory"
    )
    public void consume(BaseSearchableEntity searchableEntity, @Header(ACTION_TYPE) String actionType) {
        LOG.debug("Из топика кафки прочитана сущность {} c actionType: {}", searchableEntity, actionType);
        searchSinkService.send(searchableEntity, actionType);
    }

}
