package com.write.api.adapters.out.persistence.help;

import com.write.api.core.domain.service.SnowflakeIdGenerator;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseRepositoryTest {
    @Autowired
    protected HelpRepositoryTest help;

    @Autowired
    protected SnowflakeIdGenerator generator;

    @Autowired
    protected DSLContext dsl;
}
