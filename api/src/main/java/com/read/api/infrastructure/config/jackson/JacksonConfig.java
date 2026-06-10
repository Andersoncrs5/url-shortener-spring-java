package com.read.api.infrastructure.config.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();

        module.addSerializer(
                ObjectId.class,
                new ToStringSerializer()
        );

        mapper.registerModule(module);

        mapper.findAndRegisterModules();

        return mapper;
    }
}
