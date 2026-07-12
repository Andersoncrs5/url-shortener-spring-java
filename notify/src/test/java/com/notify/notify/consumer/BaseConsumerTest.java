package com.notify.notify.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notify.notify.TestContainersConfiguration;
import com.notify.notify.configs.snowflake.SnowflakeIdGenerator;
import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.modules.roles.repository.RoleRepository;
import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Set;

@Import({TestContainersConfiguration.class})
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=io.github.resilience4j.springboot3.retry.autoconfigure.RetryAutoConfiguration"
})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseConsumerTest {
    @Autowired protected KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected UserRepository repository;
    @Autowired protected RoleRepository roleRepository;
    @Autowired protected SnowflakeIdGenerator generator;

    public UserEntity createUser() {
        UserEntity user = new UserEntity();

        user.setId(generator.nextId());
        user.setName("any name" + generator.nextId());
        user.setEmail("user" + generator.nextId() + "@gmail.com");
        user.setActive(true);
        user.setEmailVerified(true);
        user.setRoles(Set.of("USER"));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        repository.insert(user);
        return user;
    }

    public RoleEntity createRole() {
        RoleEntity role = new RoleEntity();

        role.setId(generator.nextId());
        role.setName("role-" + generator.nextId());
        role.setName("description-" + generator.nextId());
        role.setActive(true);
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        roleRepository.insert(role);

        return role;
    }

}