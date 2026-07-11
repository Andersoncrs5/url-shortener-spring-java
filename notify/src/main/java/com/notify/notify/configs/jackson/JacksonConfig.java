package com.notify.notify.configs.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.notify.notify.configs.jackson.deserializer.Boolean01Deserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.coercionConfigFor(LogicalType.Boolean)
                .setCoercion(CoercionInputShape.String, CoercionAction.TryConvert);

        SimpleModule module = new SimpleModule();

        module.addDeserializer(Boolean.class, new Boolean01Deserializer());
        module.addDeserializer(boolean.class, new Boolean01Deserializer());

        mapper.registerModule(module);

        mapper.findAndRegisterModules();

        return mapper;
    }
}
