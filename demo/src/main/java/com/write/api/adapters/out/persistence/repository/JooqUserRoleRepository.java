package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.base.JooqRepository;
import com.write.api.adapters.out.persistence.mapper.UserRoleRepositoryMapper;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;

import static com.write.api.generated.jooq.Tables.ROLES;
import static com.write.api.generated.jooq.Tables.USER_ROLES;

import com.write.api.generated.jooq.tables.records.UserRolesRecord;
import com.write.api.ports.out.repository.IUserRoleRepository;
import com.write.api.shared.persistence.DatabaseRetryTranslator;
import com.write.api.shared.persistence.RetryTranslation;
import io.github.resilience4j.retry.annotation.Retry;
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
public class JooqUserRoleRepository extends JooqRepository implements IUserRoleRepository {

    UserRoleRepositoryMapper mapper;

    @Override
    @Retry(name = "database")
    public Optional<UserRoleModel> findById(Long id) {
        return retryTranslator.execute(() ->
                dsl.selectFrom(USER_ROLES)
                        .where(USER_ROLES.ID.eq(id))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public UserRoleModel save(UserRoleModel entity) {
        return retryTranslator.execute(() -> {
            entity.setUpdatedAt(LocalDateTime.now());
            UserRolesRecord record = mapper.toRecord(entity);

            int rows = dsl.executeUpdate(record);

            if (rows == 0) {
                throw new IllegalStateException("UserRole not found: " + entity.getId());
            }
            if (rows > 1) {
                throw new IllegalStateException("More than one row affected");
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public UserRoleModel insert(UserRoleModel entity) {
        return retryTranslator.execute(() -> {
            long id = idGen.nextId();
            LocalDateTime now = LocalDateTime.now();

            entity.setId(id);
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            UserRolesRecord record = mapper.toRecord(entity);

            int rows = dsl.executeInsert(record);

            if (rows != 1) {
                throw new RuntimeException("Failed to insert user role");
            }

            return entity;
        });
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return retryTranslator.execute(() ->
                dsl.delete(USER_ROLES)
                        .where(USER_ROLES.ID.eq(id))
                        .execute()
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(USER_ROLES)
                        .where(USER_ROLES.ID.eq(id))
        );
    }

    @Override
    @Retry(name = "database")
    public List<String> findRoleByUserId(Long id) {
        return dsl.selectDistinct(ROLES.NAME)
            .from(USER_ROLES)
            .innerJoin(ROLES)
            .on(ROLES.ID.eq(USER_ROLES.ROLE_ID))
            .where(USER_ROLES.USER_ID.eq(id))
            .fetch(ROLES.NAME);
    }

    @Override
    @Retry(name = "database")
    public boolean existsByRoleIdAndUserId(Long roleId, Long userId) {
        return dsl.fetchExists(
                dsl.selectFrom(USER_ROLES)
                        .where(USER_ROLES.USER_ID.eq(userId))
                        .and(USER_ROLES.ROLE_ID.eq(roleId))
        );
    }
}