package com.write.api.adapters.out.persistence.help;

import com.write.api.adapters.out.persistence.repository.JooqUserRepository;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;

public class HelpRepositoryTest {

    private final SnowflakeIdGenerator generator;
    private final JooqUserRepository repository;

    public HelpRepositoryTest(
            SnowflakeIdGenerator generator,
            JooqUserRepository repository
    ) {
        this.generator = generator;
        this.repository = repository;
    }

    public UserModel createUser() {
        UserModel user = new UserModel();
        user.setName("John Doe" + generator.nextId());
        user.setEmail("john" + generator.nextId() + "@example.com");
        user.setPasswordHash("54356435645625467245425");
        user.setActive(true);

        return repository.insert(user);
    }
}