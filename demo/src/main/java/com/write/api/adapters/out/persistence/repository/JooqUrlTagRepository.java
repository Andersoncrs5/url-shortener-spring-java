package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UrlTagRepositoryMapper;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.out.repository.IUrlTagRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.tables.UrlTags.URL_TAGS;

@Repository
@RequiredArgsConstructor
public class JooqUrlTagRepository implements IUrlTagRepository {

    private final UrlTagRepositoryMapper mapper;
    private final DSLContext dsl;
    private final SnowflakeIdGenerator idGen;

    @Override
    public UrlTagModel insert(UrlTagModel tag) {
        long id = idGen.nextId();
        LocalDateTime now = LocalDateTime.now();

        int rows = dsl.insertInto(URL_TAGS)
                .set(URL_TAGS.ID, id)
                .set(URL_TAGS.NAME, tag.getName())
                .set(URL_TAGS.SLUG, tag.getSlug())
                .set(URL_TAGS.COLOR, tag.getColor())
                .set(URL_TAGS.DESCRIPTION, tag.getDescription())
                .set(URL_TAGS.USER_ID, tag.getUserId())
                .set(URL_TAGS.PARENT_ID, tag.getParentId())
                .set(URL_TAGS.ACTIVE, tag.isActive())
                .set(URL_TAGS.CREATED_AT, now)
                .set(URL_TAGS.UPDATED_AT, now)
                .execute();

        if (rows != 1) {
            throw new RuntimeException("Failed to insert url tag");
        }

        tag.setId(id);
        tag.setCreatedAt(now);
        tag.setUpdatedAt(now);

        return tag;
    }

    @Override
    public UrlTagModel save(UrlTagModel tag) {
        tag.setUpdatedAt(LocalDateTime.now());

        int rows = dsl.update(URL_TAGS)
                .set(URL_TAGS.NAME, tag.getName())
                .set(URL_TAGS.SLUG, tag.getSlug())
                .set(URL_TAGS.COLOR, tag.getColor())
                .set(URL_TAGS.DESCRIPTION, tag.getDescription())
                .set(URL_TAGS.PARENT_ID, tag.getParentId())
                .set(URL_TAGS.USER_ID, tag.getUserId())
                .set(URL_TAGS.ACTIVE, tag.isActive())
                .set(URL_TAGS.UPDATED_AT, tag.getUpdatedAt())
                .where(URL_TAGS.ID.eq(tag.getId()))
                .execute();

        if (rows == 0) {
            throw new IllegalStateException("Tag not found: " + tag.getId());
        }

        if (rows > 1) {
            throw new IllegalStateException("More than one row affected");
        }

        return tag;
    }

    @Override
    public int deleteById(Long id) {
        return dsl.delete(URL_TAGS).where(URL_TAGS.ID.eq(id)).execute();
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
