package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.base.JooqRepository;
import com.write.api.adapters.out.persistence.mapper.UrlTagLinkRepositoryMapper;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.UrlTagLinksRecord;
import com.write.api.ports.out.repository.IUrlTagLinkRepository;
import io.github.resilience4j.retry.annotation.Retry;
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
public class JooqUrlTagLinkRepository extends JooqRepository implements IUrlTagLinkRepository {

    UrlTagLinkRepositoryMapper mapper;

    @Override
    @Retry(name = "database")
    public UrlTagLinkModel save(UrlTagLinkModel entity) {
        return execute(() -> {
            entity.setUpdatedAt(LocalDateTime.now());

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
        });
    }

    @Override
    @Retry(name = "database")
    public UrlTagLinkModel insert(UrlTagLinkModel entity) {
        return execute(() -> {
            long id = idGen.nextId();
            LocalDateTime now = LocalDateTime.now();

            entity.setId(id);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            UrlTagLinksRecord record = mapper.toRecord(entity);

            int rows = dsl.executeInsert(record);

            if (rows != 1) {
                throw new RuntimeException(
                        "Failed to insert url tag link"
                );
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return execute(() ->
                dsl.delete(URL_TAG_LINKS)
                        .where(URL_TAG_LINKS.ID.eq(id))
                        .execute()
        );
    }

    @Override
    @Retry(name = "database")
    public Optional<UrlTagLinkModel> findById(Long id) {
        return execute(() ->
                dsl.selectFrom(URL_TAG_LINKS)
                        .where(URL_TAG_LINKS.ID.eq(id))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return execute(() ->
                dsl.fetchExists(
                        dsl.selectFrom(URL_TAG_LINKS)
                                .where(URL_TAG_LINKS.ID.eq(id))
                )
        );
    }

    @Override
    public int countByUrlId(Long id) {
        return execute(() ->
                    dsl.selectCount().from(URL_TAG_LINKS).where(URL_TAG_LINKS.URL_ID.eq(id)).execute()
        );
    }
}