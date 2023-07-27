package ru.sberbank.pprb.sbbol.global_search.sink.configuration;

import org.apache.kafka.clients.CommonClientConfigs;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Component
@ConfigurationProperties(prefix = "app.kafka.indexing")
public class IndexingKafkaProperties {

    private List<String> bootstrapServers = new ArrayList<>(Collections.singletonList("localhost:9092"));

    private String clientId;

    private final Map<String, String> properties = new HashMap<>();

    private final KafkaProperties.Consumer consumer = new KafkaProperties.Consumer();

    private final KafkaProperties.Producer producer = new KafkaProperties.Producer();

    private final KafkaProperties.Admin admin = new KafkaProperties.Admin();

    private final KafkaProperties.Streams streams = new KafkaProperties.Streams();

    private final KafkaProperties.Listener listener = new KafkaProperties.Listener();

    private final KafkaProperties.Ssl ssl = new KafkaProperties.Ssl();

    private final KafkaProperties.Jaas jaas = new KafkaProperties.Jaas();

    private final KafkaProperties.Template template = new KafkaProperties.Template();

    private final KafkaProperties.Security security = new KafkaProperties.Security();

    public List<String> getBootstrapServers() {
        return this.bootstrapServers;
    }

    public void setBootstrapServers(List<String> bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public KafkaProperties.Consumer getConsumer() {
        return this.consumer;
    }

    public KafkaProperties.Producer getProducer() {
        return this.producer;
    }

    public KafkaProperties.Listener getListener() {
        return this.listener;
    }

    public KafkaProperties.Admin getAdmin() {
        return this.admin;
    }

    public KafkaProperties.Streams getStreams() {
        return this.streams;
    }

    public KafkaProperties.Ssl getSsl() {
        return this.ssl;
    }

    public KafkaProperties.Jaas getJaas() {
        return this.jaas;
    }

    public KafkaProperties.Template getTemplate() {
        return this.template;
    }

    public KafkaProperties.Security getSecurity() {
        return this.security;
    }

    private Map<String, Object> buildCommonProperties() {
        Map<String, Object> properties = new HashMap<>();
        if (this.bootstrapServers != null) {
            properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, this.bootstrapServers);
        }
        if (this.clientId != null) {
            properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, this.clientId);
        }
        properties.putAll(this.ssl.buildProperties());
        properties.putAll(this.security.buildProperties());
        if (!CollectionUtils.isEmpty(this.properties)) {
            properties.putAll(this.properties);
        }
        return properties;
    }

    public Map<String, Object> buildConsumerProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.consumer.buildProperties());
        return properties;
    }

    public Map<String, Object> buildProducerProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.producer.buildProperties());
        return properties;
    }

    public Map<String, Object> buildAdminProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.admin.buildProperties());
        return properties;
    }

    public Map<String, Object> buildStreamsProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(this.streams.buildProperties());
        return properties;
    }
}
