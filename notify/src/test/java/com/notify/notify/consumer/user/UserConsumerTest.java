package com.notify.notify.consumer.user;

import com.notify.notify.consumer.BaseConsumerTest;
import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.cdc.TiCdcEventTypeEnum;
import com.notify.notify.modules.user.dto.UserCdcEvent;
import com.notify.notify.modules.user.entities.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@DisplayName("UserConsumer Integration Tests with Real Kafka and Database")
public class UserConsumerTest extends BaseConsumerTest {

    @Test
    @DisplayName("Should read JSON payload from Kafka topic, process and save to real database")
    void shouldConsumeAndProcessTiCdcEventSuccessfully() throws Exception {
        String targetTopic = "users";

        UserCdcEvent userCdc = new UserCdcEvent(
                1L, 1L, "John Doe", "john@notify.com",
                "token", "hash", "USER", true,
                true, 0, null, null, LocalDateTime.now(), LocalDateTime.now()
        );

        TiCdcEvent<UserCdcEvent> expectedEvent = new TiCdcEvent<>(
                100L, "notify_db", "users", List.of("id"), false,
                TiCdcEventTypeEnum.INSERT, 1L, 123456789L, "",
                null, null, List.of(userCdc), null
        );

        String jsonPayload = objectMapper.writeValueAsString(expectedEvent);

        kafkaTemplate.send(targetTopic, jsonPayload).get();

        await()
                .atMost(Duration.ofSeconds(8))
                .pollDelay(Duration.ofMillis(700))
                .pollInterval(Duration.ofMillis(400))
                .untilAsserted(() -> {
                    boolean exists = repository.existsById(userCdc.id());
                    assertThat(exists).isTrue();
                });

        var savedUser = repository.findById(userCdc.id()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john@notify.com");
    }

    @Test
    @DisplayName("Should read UPDATE payload from Kafka topic, process and update existing user")
    void shouldUpdateConsumeAndProcessTiCdcEventSuccessfully() throws Exception {
        String targetTopic = "users";

        UserEntity existingUser = createUser();
        Long userId = existingUser.getId();

        UserCdcEvent updatedUserCdc = new UserCdcEvent(
                userId, existingUser.getVersion() + 1, "John Updated", "john.updated@notify.com",
                "token", "hash", existingUser.getRoles().toString(), existingUser.getActive(),
                existingUser.getEmailVerified(), 0, null, null, LocalDateTime.now(), LocalDateTime.now()
        );

        TiCdcEvent<UserCdcEvent> expectedEvent = new TiCdcEvent<>(
                101L, "notify_db", "users", List.of("id"), false,
                TiCdcEventTypeEnum.UPDATE, 1L, 123456790L, "",
                null, null, List.of(updatedUserCdc), null
        );

        String jsonPayload = objectMapper.writeValueAsString(expectedEvent);
        kafkaTemplate.send(targetTopic, jsonPayload).get();

        await()
                .atMost(Duration.ofSeconds(8))
                .untilAsserted(() -> {
                    var user = repository.findById(userId).orElseThrow();
                    assertThat(user.getName()).isEqualTo("John Updated");
                    assertThat(user.getEmail()).isEqualTo("john.updated@notify.com");
                });
    }

    @Test
    @DisplayName("Should read DELETE payload from Kafka topic, process and delete from real database")
    void shouldDeleteConsumeAndProcessTiCdcEventSuccessfully() throws Exception {
        String targetTopic = "users";

        UserEntity user = createUser();
        Long userId = user.getId();

        UserCdcEvent oldUserCdc = new UserCdcEvent(
                userId, user.getVersion(), user.getName(), user.getEmail(),
                "token", "hash", user.getRoles().toString(), user.getActive(),
                user.getEmailVerified(), 0, user.getBlockedAt(), null, LocalDateTime.now(), LocalDateTime.now()
        );

        TiCdcEvent<UserCdcEvent> expectedEvent = new TiCdcEvent<>(
                102L, "notify_db", "users", List.of("id"), false,
                TiCdcEventTypeEnum.DELETE, 1L, 123456791L, "",
                null,
                null,
                null,
                List.of(oldUserCdc)
        );

        String jsonPayload = objectMapper.writeValueAsString(expectedEvent);
        kafkaTemplate.send(targetTopic, jsonPayload).get();

        await()
                .atMost(Duration.ofSeconds(8))
                .pollDelay(Duration.ofMillis(700))
                .pollInterval(Duration.ofMillis(400))
                .untilAsserted(() -> {
                    boolean exists = repository.existsById(userId);
                    assertThat(exists).isFalse();
                });
    }
}