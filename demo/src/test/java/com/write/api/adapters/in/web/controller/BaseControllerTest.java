package com.write.api.adapters.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.TestcontainersConfiguration;
import com.write.api.adapters.in.web.controller.util.helps.HelperTest;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FieldDefaults(level = AccessLevel.PROTECTED)
@Import(TestcontainersConfiguration.class)
public abstract class BaseControllerTest {
    @Autowired DSLContext dsl;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SnowflakeIdGenerator idGen;
    @Autowired HelperTest helper;
}
