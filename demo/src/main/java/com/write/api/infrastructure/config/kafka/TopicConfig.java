package com.write.api.infrastructure.config.kafka;

import com.write.api.core.domain.enums.TopicEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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

        List<String> topicsCdc = List.of(
                "api_key_permissions",
                "api_keys",
                "permissions",
                "roles",
                "url_access_rule",
                "url_redirect_rules",
                "url_tag_links",
                "url_tags",
                "urls",
                "user_roles",
                "users"
        );

        for (String topic: topicsCdc) {
            topics.add(
                    TopicBuilder.name(topic)
                            .partitions(5)
                            .replicas(1)
                            .build()
            );

            topics.add(
                    TopicBuilder.name(topic+".dlq")
                            .partitions(3)
                            .replicas(1)
                            .build()
            );
        }

        KafkaAdmin.NewTopics newTopics = new KafkaAdmin.NewTopics(
                topics.toArray(NewTopic[]::new)
        );

        topics.forEach(t -> log.info("Topic {} created with partition: {}", t.name(), t.numPartitions()));

        return newTopics;
    }


}
