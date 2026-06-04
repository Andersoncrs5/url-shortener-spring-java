package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.base.JooqRepository;
import com.write.api.adapters.out.persistence.mapper.UrlTagRepositoryMapper;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.UrlTagsRecord;
import com.write.api.ports.out.repository.IUrlTagRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.tables.UrlTags.URL_TAGS;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqUrlTagRepository extends JooqRepository implements IUrlTagRepository {

    UrlTagRepositoryMapper mapper;

    @Override
    @Retry(name = "database")
    public UrlTagModel insert(UrlTagModel tag) {
        return execute(() -> {
            long id = idGen.nextId();
            LocalDateTime now = LocalDateTime.now();

            tag.setId(id);
            tag.setCreatedAt(now);
            tag.setUpdatedAt(now);

            UrlTagsRecord record = mapper.toRecord(tag);

            int rows = dsl.executeInsert(record);

            if (rows != 1) {
                throw new RuntimeException("Failed to insert url tag");
            }

            return tag;
        });
    }

    @Override
    @Retry(name = "database")
    public UrlTagModel save(UrlTagModel tag) {
        return execute(() -> {
            tag.setUpdatedAt(LocalDateTime.now());

            UrlTagsRecord record = mapper.toRecord(tag);

            int rows = dsl.executeUpdate(record);

            if (rows == 0) {
                throw new IllegalStateException("Tag not found: " + tag.getId());
            }

            if (rows > 1) {
                throw new IllegalStateException("More than one row affected");
            }

            return tag;
        });
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return execute(() ->
                dsl.delete(URL_TAGS)
                        .where(URL_TAGS.ID.eq(id))
                        .execute()
        );
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlTagModel> findById(Long id) {
        return execute(() ->
                dsl.selectFrom(URL_TAGS)
                        .where(URL_TAGS.ID.eq(id))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return execute(() ->
                dsl.fetchExists(
                        dsl.selectFrom(URL_TAGS)
                                .where(URL_TAGS.ID.eq(id))
                )
        );
    }
}