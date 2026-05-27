package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UrlTagLinkRepositoryMapper;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.out.repository.IUrlTagLinkRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.Tables.URL_TAG_LINKS;

@Repository
@RequiredArgsConstructor
public class JooqUrlTagLinkRepository implements IUrlTagLinkRepository {

    private final DSLContext dsl;
    private final SnowflakeIdGenerator idGen;
    private final UrlTagLinkRepositoryMapper mapper;

    @Override
    public UrlTagLinkModel save(UrlTagLinkModel entity) {
        int rows = dsl.update(URL_TAG_LINKS)
                .set(URL_TAG_LINKS.URL_ID, entity.getUrlId())
                .set(URL_TAG_LINKS.TAG_ID, entity.getTagId())
                .set(URL_TAG_LINKS.SORT_ORDER, entity.getSortOrder())
                .set(URL_TAG_LINKS.NOTE, entity.getNote())
                .set(URL_TAG_LINKS.PRIMARY_TAG, entity.isPrimaryTag())
                .set(URL_TAG_LINKS.CREATED_BY, entity.getCreatedBy())
                .where(URL_TAG_LINKS.ID.eq(entity.getId()))
                .execute();

        if (rows == 0) {
            throw new IllegalStateException(
                    "UrlTagLink not found: " + entity.getId()
            );
        }

        if (rows > 1) {
            throw new IllegalStateException(
                    "More than one row affected"
            );
        }

        return entity;
    }

    @Override
    public UrlTagLinkModel insert(UrlTagLinkModel entity) {
        long id = idGen.nextId();
        LocalDateTime now = LocalDateTime.now();

        int rows = dsl.insertInto(URL_TAG_LINKS)
                .set(URL_TAG_LINKS.ID, id)
                .set(URL_TAG_LINKS.URL_ID, entity.getUrlId())
                .set(URL_TAG_LINKS.TAG_ID, entity.getTagId())
                .set(URL_TAG_LINKS.SORT_ORDER, entity.getSortOrder())
                .set(URL_TAG_LINKS.NOTE, entity.getNote())
                .set(URL_TAG_LINKS.PRIMARY_TAG, entity.isPrimaryTag())
                .set(URL_TAG_LINKS.CREATED_BY, entity.getCreatedBy())
                .set(URL_TAG_LINKS.CREATED_AT, now)
                .execute();

        if (rows != 1) {
            throw new RuntimeException(
                    "Failed to insert url tag link"
            );
        }

        entity.setId(id);
        entity.setCreatedAt(now);

        return entity;
    }

    @Override
    public int deleteById(Long aLong) {
        return dsl.delete(URL_TAG_LINKS)
                .where(URL_TAG_LINKS.ID.eq(aLong))
                .execute();
    }

    @Override
    public Optional<UrlTagLinkModel> findById(Long aLong) {
        return dsl.selectFrom(URL_TAG_LINKS)
                .where(URL_TAG_LINKS.ID.eq(aLong))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long aLong) {
        return dsl.fetchExists(dsl.selectFrom(URL_TAG_LINKS).where(URL_TAG_LINKS.ID.eq(aLong)));
    }
}
