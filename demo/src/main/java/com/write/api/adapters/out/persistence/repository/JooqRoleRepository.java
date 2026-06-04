package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.RoleRepositoryMapper;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.RolesRecord;
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
    public RoleModel save(RoleModel entity) {
        if (entity.getId() == null)
            return insert(entity);

        LocalDateTime now = LocalDateTime.now();

        RolesRecord record = mapper.toRecord(entity);

        record.setUpdatedAt(now);

        int rows = dsl.executeUpdate(record);

        if (rows == 0) {
            throw new IllegalStateException(
                    "Role not found: " + entity.getId()
            );
        }

        if (rows > 1) {
            throw new IllegalStateException(
                    "More than one row affected"
            );
        }

        return mapper.toDomain(record);
    }

    @Override
    public RoleModel insert(RoleModel entity) {

        long id = generator.nextId();
        LocalDateTime now = LocalDateTime.now();

        RolesRecord record = mapper.toRecord(entity);

        record.setId(id);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);

        int rows = dsl.executeInsert(record);

        if (rows != 1) {
            throw new RuntimeException("Failed to insert role");
        }

        return mapper.toDomain(record);
    }

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
    public Optional<RoleModel> findByNameIgnoreCase(String name) {
        return dsl.selectFrom(ROLES)
                .where(ROLES.NAME.equalIgnoreCase(name))
                .fetchOptional()
                .map(mapper::toDomain);
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