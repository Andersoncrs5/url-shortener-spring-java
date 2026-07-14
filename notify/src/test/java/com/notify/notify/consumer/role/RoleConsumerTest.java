package com.notify.notify.consumer.role;

import com.notify.notify.consumer.BaseConsumerTest;
import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.cdc.TiCdcEventTypeEnum;
import com.notify.notify.modules.roles.dto.RoleCdcEvent;
import com.notify.notify.modules.roles.entities.RoleEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@DisplayName("RoleConsumer Integration Tests with Real Kafka and Database")
public class RoleConsumerTest extends BaseConsumerTest {

    private final String targetTopic = "roles";

    @Nested
    @DisplayName("Insert Event Processing")
    class InsertEventTests {

        @Test
        @DisplayName("Should read INSERT JSON payload from Kafka topic, process and save role to real database")
        void shouldConsumeAndProcessInsertSuccessfully() throws Exception {
            RoleCdcEvent roleCdc = new RoleCdcEvent(
                    200L, "ROLE_ADMIN", "Administrator Access", true,
                    LocalDateTime.now(), LocalDateTime.now()
            );

            TiCdcEvent<RoleCdcEvent> expectedEvent = new TiCdcEvent<>(
                    201L, "notify_db", "roles", List.of("id"), false,
                    TiCdcEventTypeEnum.INSERT, 1L, 987654321L, "",
                    null, null, List.of(roleCdc), null
            );

            String jsonPayload = objectMapper.writeValueAsString(expectedEvent);

            kafkaTemplate.send(targetTopic, jsonPayload).get();

            await()
                    .atMost(Duration.ofSeconds(8))
                    .pollDelay(Duration.ofMillis(700))
                    .pollInterval(Duration.ofMillis(400))
                    .untilAsserted(() -> {
                        boolean exists = roleRepository.existsById(roleCdc.id());
                        assertThat(exists).isTrue();
                    });

            var savedRole = roleRepository.findById(roleCdc.id()).orElseThrow();
            assertThat(savedRole.getName()).isEqualTo("ROLE_ADMIN");
            assertThat(savedRole.getDescription()).isEqualTo("Administrator Access");
        }
    }

    @Nested
    @DisplayName("Update Event Processing")
    class UpdateEventTests {

        @Test
        @DisplayName("Should read UPDATE payload from Kafka topic, process and update existing role")
        void shouldConsumeAndProcessUpdateSuccessfully() throws Exception {
            RoleEntity existingRole = createRole();
            Long roleId = existingRole.getId();

            RoleCdcEvent updatedRoleCdc = new RoleCdcEvent(
                    roleId, "ROLE_UPDATED" + generator.nextId(), "Updated Description", true,
                    LocalDateTime.now(), existingRole.getCreatedAt()
            );

            TiCdcEvent<RoleCdcEvent> expectedEvent = new TiCdcEvent<>(
                    202L, "notify_db", "roles", List.of("id"), false,
                    TiCdcEventTypeEnum.UPDATE, 1L, 987654322L, "",
                    null, null, List.of(updatedRoleCdc), null
            );

            String jsonPayload = objectMapper.writeValueAsString(expectedEvent);
            kafkaTemplate.send(targetTopic, jsonPayload).get();

            await()
                    .atMost(Duration.ofSeconds(6))
                    .pollDelay(Duration.ofMillis(600))
                    .pollInterval(Duration.ofMillis(300))
                    .untilAsserted(() -> {
                        var role = roleRepository.findById(roleId).orElse(null);

                        assertThat(role).isNotNull();

                        assertThat(role.getName()).isEqualTo(updatedRoleCdc.name());
                        assertThat(role.getDescription()).isEqualTo("Updated Description");
                    });
        }
    }

    @Nested
    @DisplayName("Delete Event Processing")
    class DeleteEventTests {

        @Test
        @DisplayName("Should read DELETE payload from Kafka topic, process and remove role from real database")
        void shouldConsumeAndProcessDeleteSuccessfully() throws Exception {
            RoleEntity role = createRole();
            Long roleId = role.getId();

            RoleCdcEvent oldRoleCdc = new RoleCdcEvent(
                    roleId, role.getName(), role.getDescription(), role.getActive(),
                    LocalDateTime.now(), LocalDateTime.now()
            );

            TiCdcEvent<RoleCdcEvent> expectedEvent = new TiCdcEvent<>(
                    203L, "notify_db", "roles", List.of("id"), false,
                    TiCdcEventTypeEnum.DELETE, 1L, 987654323L, "",
                    null, null, null, List.of(oldRoleCdc)
            );

            String jsonPayload = objectMapper.writeValueAsString(expectedEvent);
            kafkaTemplate.send(targetTopic, jsonPayload).get();

            await()
                    .atMost(Duration.ofSeconds(8))
                    .pollDelay(Duration.ofMillis(700))
                    .pollInterval(Duration.ofMillis(400))
                    .untilAsserted(() -> {
                        boolean exists = roleRepository.existsById(roleId);
                        assertThat(exists).isFalse();
                    });
        }
    }
}