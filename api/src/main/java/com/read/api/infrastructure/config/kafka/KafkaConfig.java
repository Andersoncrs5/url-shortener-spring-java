package com.read.api.infrastructure.config.kafka;

import com.read.api.infrastructure.properties.KafkaProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaConfig {

    KafkaProperties properties;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                properties.getBootstrapServers()
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                classForName(
                        properties.getProducer()
                                .getKeySerializer()
                )
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                classForName(
                        properties.getProducer()
                                .getValueSerializer()
                )
        );

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> factory
    ) {
        return new KafkaTemplate<>(factory);
    }

    @SuppressWarnings("unchecked")
    private Class<?> classForName(
            String className
    ) {
        try {

            return Class.forName(className);

        } catch (ClassNotFoundException ex) {

            throw new IllegalStateException(
                    "Kafka class not found: " + className,
                    ex
            );
        }
    }
}