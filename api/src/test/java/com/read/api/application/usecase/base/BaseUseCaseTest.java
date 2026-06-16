package com.read.api.application.usecase.base;

import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.service.RedisCrudService;
import com.read.api.domain.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public abstract class BaseUseCaseTest {

    @Mock
    protected RedisCrudService redis;

    protected final SnowflakeIdGenerator generator =
            new SnowflakeIdGenerator(1);

    public UserModel createUser() {
        UserModel user = new UserModel();

        user.setId(generator.nextId());
        user.setName("pochita");
        user.setEmail("pochita@gmail.com");

        return user;
    }

    public UrlAccessRuleModel createUrlAccessRule() {
        var rule = new UrlAccessRuleModel();

        rule.setId(generator.nextId());
        rule.setUrlId(generator.nextId());
        rule.setAssignedByUserId(generator.nextId());
        rule.setExpiresAt(LocalDateTime.now().plusDays(55));

        return rule;
    }

}
