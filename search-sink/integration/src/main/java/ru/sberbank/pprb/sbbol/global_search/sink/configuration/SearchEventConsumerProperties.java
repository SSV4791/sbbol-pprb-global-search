package ru.sberbank.pprb.sbbol.global_search.sink.configuration;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
@ConfigurationProperties(prefix = "global-search.kafka")
public class SearchEventConsumerProperties extends KafkaProperties {

}
