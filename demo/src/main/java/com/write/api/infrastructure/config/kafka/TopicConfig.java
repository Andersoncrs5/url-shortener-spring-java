package com.write.api.infrastructure.config.kafka;

import com.write.api.core.domain.enums.TopicEnum;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class TopicConfig {


    @Bean
    public KafkaAdmin.NewTopics topics() {

        List<NewTopic> topics = new ArrayList<>();

        for (TopicEnum topic : TopicEnum.values()) {

            topics.add(
                    TopicBuilder.name(topic.value())
                            .partitions(3)
                            .replicas(1)
                            .build()
            );

            topics.add(
                    TopicBuilder.name(topic.dlq())
                            .partitions(3)
                            .replicas(1)
                            .build()
            );
        }

        return new KafkaAdmin.NewTopics(
                topics.toArray(NewTopic[]::new)
        );
    }


}
