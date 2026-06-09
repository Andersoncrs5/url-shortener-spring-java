package com.read.api.application.usecase.base;

import com.read.api.domain.model.UserModel;
import com.read.api.domain.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseUseCaseTest {

    protected final SnowflakeIdGenerator generator =
            new SnowflakeIdGenerator(1);

    public UserModel createUser() {
        UserModel user = new UserModel();

        user.setId(generator.nextId());
        user.setName("pochita");
        user.setEmail("pochita@gmail.com");

        return user;
    }

}
