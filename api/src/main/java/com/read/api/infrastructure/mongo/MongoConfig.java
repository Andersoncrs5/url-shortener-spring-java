package com.read.api.infrastructure.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoManagedTypes;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public MongoClient mongoClient(
            @Value("${spring.data.mongodb.uri}") String uri
    ) {
        return MongoClients.create(uri);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(
            MongoClient mongoClient,
            @Value("${spring.data.mongodb.database}") String database
    ) {
        return new SimpleMongoClientDatabaseFactory(
                mongoClient,
                database
        );
    }

    @Bean
    public MongoTransactionManager transactionManager(
            MongoDatabaseFactory factory
    ) {
        return new MongoTransactionManager(factory);
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory,
                                                       MongoMappingContext context,
                                                       MongoManagedTypes managedTypes) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);

        context.setManagedTypes(managedTypes);

        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);

        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }
}