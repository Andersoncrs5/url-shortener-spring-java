package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UrlTagLinkRepositoryMapper;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.UrlTagLinksRecord;
import com.write.api.ports.out.repository.IUrlTagLinkRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.Tables.URL_TAG_LINKS;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqUrlTagLinkRepository implements IUrlTagLinkRepository {

    DSLContext dsl;
    SnowflakeIdGenerator idGen;
    UrlTagLinkRepositoryMapper mapper;

    @Override
    public UrlTagLinkModel save(UrlTagLinkModel entity) {

        UrlTagLinksRecord record = mapper.toRecord(entity);

        int rows = dsl.executeUpdate(record);

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

        entity.setId(id);
        entity.setCreatedAt(now);

        UrlTagLinksRecord record = mapper.toRecord(entity);

        int rows = dsl.executeInsert(record);

        if (rows != 1) {
            throw new RuntimeException(
                    "Failed to insert url tag link"
            );
        }

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
