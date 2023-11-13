package ru.sberbank.pprb.sbbol.global_search.producer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.sberbank.pprb.sbbol.global_search.producer.api.SearchEventProducer;
import ru.sberbank.pprb.sbbol.global_search.producer.impl.SearchEventProducerImpl;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;

import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Configuration
@ConditionalOnProperty(prefix= "global-search.kafka", name = {"topic"})
@EnableKafka
@EnableConfigurationProperties(SearchEventProducerProperties.class)
public class SearchEventProducerConfiguration {

    private final SearchEventProducerProperties searchEventProducerProperties;

    public SearchEventProducerConfiguration(SearchEventProducerProperties searchEventProducerProperties) {
        this.searchEventProducerProperties = searchEventProducerProperties;
    }

    @Bean
    public ProducerFactory<String, BaseSearchableEntity> searchEventKafkaProducerFactory() {
        DefaultKafkaProducerFactory<String, BaseSearchableEntity> producerFactory =
            new DefaultKafkaProducerFactory<>(getProducerConfigs());
        ObjectMapper objectMapper = JacksonUtils.enhancedObjectMapper();
        objectMapper.setSerializationInclusion(NON_NULL);
        JsonSerializer<BaseSearchableEntity> valueSerializer = new JsonSerializer<>(objectMapper);
        valueSerializer.setAddTypeInfo(true);
        producerFactory.setValueSerializer(valueSerializer);
        return producerFactory;
    }

    @Bean
    public KafkaTemplate<String, BaseSearchableEntity> searchEventKafkaTemplate(ProducerFactory<String, BaseSearchableEntity> searchEventKafkaProducerFactory) {
        return new KafkaTemplate<>(searchEventKafkaProducerFactory);
    }

    @Bean
    SearchEventProducer searchEventProducer(KafkaTemplate<String, BaseSearchableEntity> searchEventKafkaTemplate) {
        return new SearchEventProducerImpl(searchEventKafkaTemplate);
    }

    private Map<String, Object> getProducerConfigs() {
        return new HashMap<>(searchEventProducerProperties.buildProducerProperties());
    }
}
