package ru.sberbank.pprb.sbbol.global_search.sink.configuration;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import ru.sberbank.pprb.sbbol.global_search.sink.listener.SearchEventConsumerListener;
import ru.sberbank.pprb.sbbol.global_search.sink.service.SearchSinkService;

@ConditionalOnProperty(prefix = "global-search.kafka.consumer", name = "enable", havingValue = "true")
@Configuration
@EnableKafka
@EnableConfigurationProperties(SearchEventConsumerProperties.class)
public class SearchEventConsumerConfiguration {

    @Autowired
    private SearchEventConsumerProperties consumerProperties;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
            consumerProperties.buildConsumerProperties(),
            new StringDeserializer(),
            new StringDeserializer()
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> searchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(false);
        factory.setMessageConverter(new StringJsonMessageConverter());
        return factory;
    }

    @Bean
    public SearchEventConsumerListener searchEventConsumerListener(SearchSinkService searchSinkService) {
        return new SearchEventConsumerListener(searchSinkService);
    }
}
