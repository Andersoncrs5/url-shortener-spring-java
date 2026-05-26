package com.write.api.infrastructure.config;

import com.write.api.core.domain.service.SnowflakeIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfig {

@Bean
public SnowflakeIdGenerator snowflakeIdGenerator() {
    return new SnowflakeIdGenerator(1L);
}

}