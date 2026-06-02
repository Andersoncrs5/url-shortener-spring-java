package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UserRoleRepositoryMapper;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;

import static com.write.api.generated.jooq.Tables.ROLES;
import static com.write.api.generated.jooq.Tables.USER_ROLES;
import com.write.api.ports.out.repository.IUserRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqUserRoleRepository implements IUserRoleRepository {

    DSLContext dsl;
    SnowflakeIdGenerator idGen;
    UserRoleRepositoryMapper mapper;

    @Override
    public UserRoleModel save(UserRoleModel entity) {

        int rows = dsl.update(USER_ROLES)
                .set(USER_ROLES.USER_ID, entity.getUserId())
                .set(USER_ROLES.ROLE_ID, entity.getRoleId())
                .set(USER_ROLES.ASSIGNED_BY_USER_ID, entity.getAssignedByUserId())
                .set(USER_ROLES.UPDATED_AT, LocalDateTime.now())
                .where(USER_ROLES.ID.eq(entity.getId()))
                .execute();

        if (rows == 0) {
            throw new IllegalStateException(
                    "UserRole not found: " + entity.getId()
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
    public UserRoleModel insert(UserRoleModel entity) {

        long id = idGen.nextId();
        LocalDateTime now = LocalDateTime.now();

        int rows = dsl.insertInto(USER_ROLES)
                .set(USER_ROLES.ID, id)
                .set(USER_ROLES.USER_ID, entity.getUserId())
                .set(USER_ROLES.ROLE_ID, entity.getRoleId())
                .set(USER_ROLES.ASSIGNED_BY_USER_ID, entity.getAssignedByUserId())
                .set(USER_ROLES.CREATED_AT, now)
                .set(USER_ROLES.UPDATED_AT, now)
                .execute();

        if (rows != 1) {
            throw new RuntimeException(
                    "Failed to insert user role"
            );
        }

        entity.setId(id);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return entity;
    }

    @Override
    public int deleteById(Long id) {
        return dsl.delete(USER_ROLES)
                .where(USER_ROLES.ID.eq(id))
                .execute();
    }

    @Override
    public Optional<UserRoleModel> findById(Long id) {
        return dsl.selectFrom(USER_ROLES)
                .where(USER_ROLES.ID.eq(id))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(USER_ROLES)
                        .where(USER_ROLES.ID.eq(id))
        );
    }

    @Override
    public List<String> findRoleByUserId(Long id) {
        return dsl.selectDistinct(ROLES.NAME)
            .from(USER_ROLES)
            .innerJoin(ROLES)
            .on(ROLES.ID.eq(USER_ROLES.ROLE_ID))
            .where(USER_ROLES.USER_ID.eq(id))
            .fetch(ROLES.NAME);
    }

    @Override
    public boolean existsByRoleIdAndUserId(Long roleId, Long userId) {
        return dsl.fetchExists(
                dsl.selectFrom(USER_ROLES)
                        .where(USER_ROLES.USER_ID.eq(userId))
                        .and(USER_ROLES.ROLE_ID.eq(roleId))
        );
    }
}