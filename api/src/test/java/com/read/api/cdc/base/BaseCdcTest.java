package com.read.api.cdc.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.TestcontainersConfiguration;
import com.read.api.domain.utils.SnowflakeIdGenerator;
import com.read.api.infrastructure.kafka.producer.DlqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class BaseCdcTest {

    @Autowired protected SnowflakeIdGenerator generator;
    @Autowired protected MongoTemplate template;
    @Autowired protected KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired protected DlqProducer producer;
    @Autowired protected ObjectMapper objectMapper;

}
