package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UrlTagRepositoryMapper;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.UrlTagsRecord;
import com.write.api.ports.out.repository.IUrlTagRepository;
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
public class JooqUrlTagRepository implements IUrlTagRepository {

    UrlTagRepositoryMapper mapper;
    DSLContext dsl;
    SnowflakeIdGenerator idGen;

    @Override
    public UrlTagModel insert(UrlTagModel tag) {

        long id = idGen.nextId();
        LocalDateTime now = LocalDateTime.now();

        tag.setId(id);
        tag.setCreatedAt(now);
        tag.setUpdatedAt(now);

        UrlTagsRecord record = mapper.toRecord(tag);

        int rows = dsl.executeInsert(record);

        if (rows != 1) {
            throw new RuntimeException(
                    "Failed to insert url tag"
            );
        }

        return tag;
    }

    @Override
    public UrlTagModel save(UrlTagModel tag) {

        tag.setUpdatedAt(LocalDateTime.now());

        UrlTagsRecord record = mapper.toRecord(tag);

        int rows = dsl.executeUpdate(record);

        if (rows == 0) {
            throw new IllegalStateException(
                    "Tag not found: " + tag.getId()
            );
        }

        if (rows > 1) {
            throw new IllegalStateException(
                    "More than one row affected"
            );
        }

        return tag;
    }

    @Override
    public int deleteById(Long id) {
        return dsl.delete(URL_TAGS)
                .where(URL_TAGS.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<UrlTagModel> findById(Long id) {
        return dsl.selectFrom(URL_TAGS)
                .where(URL_TAGS.ID.eq(id))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(URL_TAGS)
                        .where(URL_TAGS.ID.eq(id))
        );
    }
}