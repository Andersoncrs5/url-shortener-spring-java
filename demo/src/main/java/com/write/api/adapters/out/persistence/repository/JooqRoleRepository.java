package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.base.JooqRepository;
import com.write.api.adapters.out.persistence.mapper.RoleRepositoryMapper;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.RolesRecord;
import com.write.api.ports.out.repository.IRoleRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.Tables.ROLES;
import static org.jooq.impl.DSL.lower;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqRoleRepository extends JooqRepository implements IRoleRepository {

    RoleRepositoryMapper mapper;

    @Override
    @Retry(name = "database")
    public RoleModel save(RoleModel entity) {
        return execute(() -> {
            if (entity.getId() == null) {
                return insert(entity);
            }

            entity.setUpdatedAt(LocalDateTime.now());

            RolesRecord record = mapper.toRecord(entity);

            int rows = dsl.executeUpdate(record);

            if (rows == 0) {
                throw new IllegalStateException("Role not found: " + entity.getId());
            }

            if (rows > 1) {
                throw new IllegalStateException("More than one row affected");
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public RoleModel insert(RoleModel entity) {
        return execute(() -> {
            long id = idGen.nextId();
            LocalDateTime now = LocalDateTime.now();

            entity.setId(id);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            RolesRecord record = mapper.toRecord(entity);

            int rows = dsl.executeInsert(record);

            if (rows != 1) {
                throw new RuntimeException("Failed to insert role");
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public boolean existsByNameIgnoreCase(String name) {
        return execute(() -> {
            if (name == null || name.isBlank()) {
                return false;
            }

            return dsl.fetchExists(
                    dsl.selectFrom(ROLES)
                            .where(lower(ROLES.NAME).eq(name.trim().toLowerCase()))
            );
        });
    }

    @Override
    @Retry(name = "database")
    public Optional<RoleModel> findByNameIgnoreCase(String name) {
        return execute(() ->
                dsl.selectFrom(ROLES)
                        .where(ROLES.NAME.equalIgnoreCase(name))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return execute(() ->
                dsl.deleteFrom(ROLES)
                        .where(ROLES.ID.eq(id))
                        .execute()
        );
    }

    @Override
    @Retry(name = "database")
    public Optional<RoleModel> findById(Long id) {
        return execute(() ->
                dsl.selectFrom(ROLES)
                        .where(ROLES.ID.eq(id))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return execute(() ->
                dsl.fetchExists(
                        dsl.selectFrom(ROLES)
                                .where(ROLES.ID.eq(id))
                )
        );
    }
}