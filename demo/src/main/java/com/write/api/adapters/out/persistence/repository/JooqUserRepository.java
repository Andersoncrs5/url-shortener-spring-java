package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.base.JooqRepository;
import com.write.api.adapters.out.persistence.mapper.UserRepositoryMapper;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.UsersRecord;
import com.write.api.ports.out.repository.IUserRepository;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.tables.Users.USERS;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JooqUserRepository extends JooqRepository implements IUserRepository {

    UserRepositoryMapper mapper;

    @Override
    @Retry(name = "database")
    public UserModel save(UserModel user) {
        return execute(() -> {
            user.setUpdatedAt(LocalDateTime.now());

            UsersRecord record = mapper.toRecord(user);

            int rows = dsl.executeUpdate(record);

            if (rows == 0) {
                throw new IllegalStateException(
                        "User not found: " + user.getId()
                );
            }

            if (rows > 1) {
                throw new IllegalStateException(
                        "More than one row affected"
                );
            }

            return user;
        });
    }

    @Override
    @Retry(name = "database")
    public UserModel insert(UserModel user) {
        return execute(() -> {
            long id = idGen.nextId();
            LocalDateTime now = LocalDateTime.now();

            user.setId(id);
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            if (user.getVersion() == null) {
                user.setVersion(1L);
            }

            UsersRecord record = mapper.toRecord(user);

            int rows = dsl.executeInsert(record);

            if (rows != 1) {
                throw new RuntimeException("Failed to insert user");
            }

            return user;
        });
    }

    @Override
    @Retry(name = "database")
    public int deleteById(Long id) {
        return execute(() ->
                dsl.delete(USERS)
                        .where(USERS.ID.eq(id))
                        .execute()
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsByEmailIgnoreCase(String email) {
        return execute(() -> {
            Integer count = dsl
                    .selectCount()
                    .from(USERS)
                    .where(USERS.EMAIL.equalIgnoreCase(email))
                    .fetchOne(0, Integer.class);

            return count != null && count > 0;
        });
    }

    @Override
    @Retry(name = "database")
    public Optional<UserModel> findByEmailIgnoreCase(String email) {
        return execute(() ->
                dsl.selectFrom(USERS)
                        .where(USERS.EMAIL.equalIgnoreCase(email))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public Optional<UserModel> findById(Long id) {
        return execute(() ->
                dsl.selectFrom(USERS)
                        .where(USERS.ID.eq(id))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public Optional<UserModel> findByRefreshToken(String refreshToken) {
        return execute(() ->
                dsl.selectFrom(USERS)
                        .where(USERS.REFRESH_TOKEN.equalIgnoreCase(refreshToken))
                        .fetchOptional()
                        .map(mapper::toDomain)
        );
    }

    @Override
    @Retry(name = "database")
    public boolean existsById(Long id) {
        return execute(() ->
                dsl.fetchExists(
                        dsl.selectFrom(USERS)
                                .where(USERS.ID.eq(id))
                )
        );
    }
}