package com.write.api.application.service.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.infrastructure.config.cache.RedisCrudService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {

    @Mock protected RedisCrudService redisCrudService;

    @Mock protected SnowflakeIdGenerator idGen;

    @Mock protected ObjectMapper objectMapper;

}