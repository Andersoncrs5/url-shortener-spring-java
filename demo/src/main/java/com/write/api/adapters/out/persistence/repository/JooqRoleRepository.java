package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.RoleRepositoryMapper;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.out.repository.IRoleRepository;
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
public class JooqRoleRepository implements IRoleRepository {

    DSLContext dsl;
    SnowflakeIdGenerator generator;
    RoleRepositoryMapper mapper;

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        return dsl.fetchExists(
                dsl.selectFrom(ROLES)
                        .where(lower(ROLES.NAME).eq(name.trim().toLowerCase()))
        );
    }

    @Override
    public RoleModel save(RoleModel entity) {
        LocalDateTime now = LocalDateTime.now();

        int rows = dsl.update(ROLES)
                .set(ROLES.NAME, entity.getName())
                .set(ROLES.DESCRIPTION, entity.getDescription())
                .set(ROLES.ACTIVE, entity.isActive())
                .set(ROLES.UPDATED_AT, now)
                .where(ROLES.ID.eq(entity.getId()))
                .execute();

        if (rows == 0) {
            throw new IllegalStateException("Role not found: " + entity.getId());
        }

        if (rows > 1) {
            throw new IllegalStateException("More than one row affected");
        }

        entity.setUpdatedAt(now);
        return entity;
    }

    @Override
    public RoleModel insert(RoleModel entity) {
        long id = generator.nextId();
        LocalDateTime now = LocalDateTime.now();

        int rows = dsl.insertInto(ROLES)
                .set(ROLES.ID, id)
                .set(ROLES.NAME, entity.getName())
                .set(ROLES.DESCRIPTION, entity.getDescription())
                .set(ROLES.ACTIVE, entity.isActive())
                .set(ROLES.CREATED_AT, now)
                .set(ROLES.UPDATED_AT, now)
                .execute();

        if (rows != 1) {
            throw new RuntimeException("Failed to insert role");
        }

        entity.setId(id);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return entity;
    }

    @Override
    public int deleteById(Long id) {
        return dsl.deleteFrom(ROLES)
                .where(ROLES.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<RoleModel> findById(Long id) {
        return dsl.selectFrom(ROLES)
                .where(ROLES.ID.eq(id))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(ROLES)
                        .where(ROLES.ID.eq(id))
        );
    }
}