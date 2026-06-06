package com.write.api.adapters.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.helps.HelperTest;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SnowflakeIdGenerator idGen;
    @Autowired HelperTest helper;
}
