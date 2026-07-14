package com.notify.notify.consumer.userRole;

import com.notify.notify.consumer.BaseConsumerTest;
import com.notify.notify.globals.classes.cdc.TiCdcEvent;
import com.notify.notify.globals.classes.cdc.TiCdcEventTypeEnum;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.userRole.dto.UserRoleCdcEvent;
import com.notify.notify.modules.userRole.entities.UserRoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@DisplayName("UserRoleConsumer Integration Tests with Real Kafka and Database")
public class UserRoleConsumerTest extends BaseConsumerTest {

    private final String targetTopic = "user_roles";

    private UserEntity existingUser;
    private RoleEntity existingRole;
    private UserEntity existingAdmin;

    @BeforeEach
    void setUpDependencies() {
        existingUser = createUser();
        existingRole = createRole();
        existingAdmin = createUser();
    }

    @Nested
    @DisplayName("Insert Event Processing")
    class InsertEventTests {

        @Test
        @DisplayName("Should read INSERT payload from user_roles topic, link role to user and update denormalized set")
        void shouldConsumeAndProcessInsertSuccessfully() throws Exception {
            Long relationshipId = generator.nextId();

            UserRoleCdcEvent userRoleCdc = new UserRoleCdcEvent(
                    relationshipId,
                    existingUser.getId(),
                    existingRole.getId(),
                    existingAdmin.getId(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            TiCdcEvent<UserRoleCdcEvent> expectedEvent = new TiCdcEvent<>(
                    301L, "notify_db", "user_roles", List.of("id"), false,
                    TiCdcEventTypeEnum.INSERT, 1L, 998877661L, "",
                    null, null, List.of(userRoleCdc), null
            );

            String jsonPayload = objectMapper.writeValueAsString(expectedEvent);

            kafkaTemplate.send(targetTopic, jsonPayload).get();

            await()
                    .atMost(Duration.ofSeconds(8))
                    .pollDelay(Duration.ofMillis(700))
                    .pollInterval(Duration.ofMillis(400))
                    .untilAsserted(() -> {
                        boolean exists = userRoleRepository.existsById(relationshipId);
                        assertThat(exists).isTrue();

                        var updatedUser = repository.findById(existingUser.getId()).orElseThrow();
                        assertThat(updatedUser.getRoles()).contains(existingRole.getName());
                    });
        }
    }

    @Nested
    @DisplayName("Update Event Processing")
    class UpdateEventTests {

        @Test
        @DisplayName("Should process UPDATE events as a re-sync of the relationship structure")
        void shouldConsumeAndProcessUpdateSuccessfully() throws Exception {
            UserRoleEntity existingRelation = createUserRoleRelation(existingUser, existingRole, existingAdmin);

            UserEntity newAdmin = createUser();
            UserRoleCdcEvent updatedCdc = new UserRoleCdcEvent(
                    existingRelation.getId(),
                    existingUser.getId(),
                    existingRole.getId(),
                    newAdmin.getId(), // Atribuído por um novo usuário
                    existingRelation.getCreatedAt(),
                    LocalDateTime.now()
            );

            TiCdcEvent<UserRoleCdcEvent> expectedEvent = new TiCdcEvent<>(
                    302L, "notify_db", "user_roles", List.of("id"), false,
                    TiCdcEventTypeEnum.UPDATE, 1L, 998877662L, "",
                    null, null, List.of(updatedCdc), null
            );

            String jsonPayload = objectMapper.writeValueAsString(expectedEvent);

            // Act
            kafkaTemplate.send(targetTopic, jsonPayload).get();

            // Assert
            await()
                    .atMost(Duration.ofSeconds(6))
                    .pollDelay(Duration.ofMillis(600))
                    .pollInterval(Duration.ofMillis(300))
                    .untilAsserted(() -> {
                        var relation = userRoleRepository.findById(existingRelation.getId()).orElse(null);
                        assertThat(relation).isNotNull();

                        var user = repository.findById(existingUser.getId()).orElseThrow();
                        assertThat(user.getRoles()).contains(existingRole.getName());
                    });
        }
    }

    @Nested
    @DisplayName("Delete Event Processing")
    class DeleteEventTests {

        @Test
        @DisplayName("Should read DELETE payload, remove pivot entry and clean denormalized string from user entity")
        void shouldConsumeAndProcessDeleteSuccessfully() throws Exception {
            UserRoleEntity existingRelation = createUserRoleRelation(existingUser, existingRole, existingAdmin);
            Long relationId = existingRelation.getId();

            UserRoleCdcEvent oldCdc = new UserRoleCdcEvent(
                    relationId,
                    existingUser.getId(),
                    existingRole.getId(),
                    existingAdmin.getId(),
                    existingRelation.getCreatedAt(),
                    existingRelation.getUpdatedAt()
            );

            TiCdcEvent<UserRoleCdcEvent> expectedEvent = new TiCdcEvent<>(
                    303L, "notify_db", "user_roles", List.of("id"), false,
                    TiCdcEventTypeEnum.DELETE, 1L, 998877663L, "",
                    null, null, null, List.of(oldCdc)
            );

            String jsonPayload = objectMapper.writeValueAsString(expectedEvent);

            kafkaTemplate.send(targetTopic, jsonPayload).get();

            await()
                    .atMost(Duration.ofSeconds(8))
                    .pollDelay(Duration.ofMillis(700))
                    .pollInterval(Duration.ofMillis(400))
                    .untilAsserted(() -> {
                        boolean exists = userRoleRepository.existsById(relationId);
                        assertThat(exists).isFalse();

                        var updatedUser = repository.findById(existingUser.getId()).orElseThrow();
                        assertThat(updatedUser.getRoles()).doesNotContain(existingRole.getName());
                    });
        }
    }


}