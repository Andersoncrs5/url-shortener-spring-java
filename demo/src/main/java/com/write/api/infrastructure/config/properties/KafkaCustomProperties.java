package com.write.api.infrastructure.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaCustomProperties {

    @NotBlank
    private String bootstrapServers;

    @NotNull
    @Valid
    private Consumer consumer;

    @NotNull
    @Valid
    private Producer producer;

    private Map<String, String> properties;

    @Getter
    @Setter
    public static class Consumer {
        @NotBlank
        private String groupId;

        @NotBlank
        private String autoOffsetReset;

        @NotBlank
        private String keyDeserializer;

        @NotBlank
        private String valueDeserializer;
    }

    @Getter
    @Setter
    public static class Producer {
        @NotBlank
        private String keySerializer;

        @NotBlank
        private String valueSerializer;
    }
}
