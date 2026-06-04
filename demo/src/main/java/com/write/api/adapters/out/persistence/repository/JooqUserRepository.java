package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UserRepositoryMapper;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.generated.jooq.tables.records.UsersRecord;
import com.write.api.ports.out.repository.IUserRepository;
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
public class JooqUserRepository implements IUserRepository {

    DSLContext dsl;
    SnowflakeIdGenerator idGen;
    UserRepositoryMapper mapper;

    @Override
    public UserModel save(UserModel user) {

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
    }

    @Override
    public UserModel insert(UserModel user) {

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
            throw new RuntimeException(
                    "Failed to insert user"
            );
        }

        return user;
    }

    @Override
    public int deleteById(Long id) {
        return dsl.delete(USERS)
                .where(USERS.ID.eq(id))
                .execute();
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        Integer count = dsl
                .selectCount()
                .from(USERS)
                .where(USERS.EMAIL.equalIgnoreCase(email))
                .fetchOne(0, Integer.class);

        return count != null && count > 0;
    }

    @Override
    public Optional<UserModel> findByEmailIgnoreCase(String email) {
        return dsl.selectFrom(USERS)
                .where(USERS.EMAIL.equalIgnoreCase(email))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserModel> findById(Long id) {
        return dsl.selectFrom(USERS)
                .where(USERS.ID.eq(id))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserModel> findByRefreshToken(String refreshToken) {
        return dsl.selectFrom(USERS)
                .where(USERS.REFRESH_TOKEN.equalIgnoreCase(refreshToken))
                .fetchOptional()
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return dsl.fetchExists(
                dsl.selectFrom(USERS)
                        .where(USERS.ID.eq(id))
        );
    }

}
