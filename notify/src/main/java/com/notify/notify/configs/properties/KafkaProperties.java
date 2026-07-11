package com.notify.notify.configs.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    @NotBlank private String bootstrapServers;

    private Consumer consumer = new Consumer();

    private Producer producer = new Producer();

    @Getter
    @Setter
    public static class Consumer {

        @NotBlank private String groupId;

        @NotBlank private String autoOffsetReset;

        @NotBlank private String keyDeserializer;

        @NotBlank private String valueDeserializer;
    }

    @Getter
    @Setter
    public static class Producer {

        @NotBlank private String keySerializer;

        @NotBlank private String valueSerializer;
    }
}
