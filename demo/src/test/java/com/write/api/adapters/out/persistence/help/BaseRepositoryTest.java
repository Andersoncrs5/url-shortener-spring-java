package com.write.api.adapters.out.persistence.help;

import com.write.api.TestcontainersConfiguration;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public abstract class BaseRepositoryTest {
    @Autowired protected HelpRepositoryTest help;
    @Autowired protected SnowflakeIdGenerator generator;
    @Autowired protected DSLContext dsl;
}
