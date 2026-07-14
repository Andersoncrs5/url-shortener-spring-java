package com.notify.notify;

import com.notify.notify.globals.enums.TopicEnum;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Arrays;

@TestConfiguration
public class KafkaTopicTestConfig {

    @Bean
    KafkaAdmin.NewTopics createTopics() {
        return new KafkaAdmin.NewTopics(
                Arrays.stream(TopicEnum.values())
                        .map(topic ->
                                TopicBuilder
                                        .name(topic.value())
                                        .partitions(1)
                                        .replicas(1)
                                        .build()
                        )
                        .toArray(NewTopic[]::new)
        );
    }
}