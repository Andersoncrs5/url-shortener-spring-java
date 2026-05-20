package com.write.api.adapters.out.persistence.repository;

import com.write.api.adapters.out.persistence.mapper.UserRepositoryMapper;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.out.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.write.api.generated.jooq.tables.Users.USERS;


@Repository
@RequiredArgsConstructor
public class JooqUserRepository implements IUserRepository {

    private final DSLContext dsl;
    private final SnowflakeIdGenerator idGen;
    private final UserRepositoryMapper mapper;

    @Override
    public UserModel save(UserModel user) {
        user.setUpdatedAt(LocalDateTime.now());

        int rows = dsl.update(USERS)
                .set(USERS.NAME, user.getName())
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.REFRESH_TOKEN ,user.getRefreshToken())
                .set(USERS.PASSWORD_HASH , user.getPasswordHash())
                .set(USERS.ACTIVE, user.isActive())
                .set(USERS.EMAIL_VERIFIED, user.isEmailVerified())
                .set(USERS.LAST_LOGIN_AT, user.getLastLoginAt())
                .set(USERS.UPDATED_AT, user.getUpdatedAt())
                .where(USERS.ID.eq(user.getId()))
                .execute();

        if (rows == 0) {
            throw new IllegalStateException("User not found: " + user.getId());
        }

        return user;
    }

    @Override
    public UserModel insert(UserModel user) {
        long id = idGen.nextId();
        LocalDateTime now = LocalDateTime.now();

        dsl.insertInto(USERS)
                .set(USERS.ID, id)
                .set(USERS.NAME, user.getName())
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.REFRESH_TOKEN, user.getRefreshToken())
                .set(USERS.PASSWORD_HASH, user.getPasswordHash())
                .set(USERS.ACTIVE, user.isActive())
                .set(USERS.EMAIL_VERIFIED, user.isEmailVerified())
                .set(USERS.CREATED_AT, now)
                .set(USERS.UPDATED_AT, now)
                .set(USERS.VERSION, 1L)
                .execute();

        user.setId(id);
        user.setCreatedAt(now);

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
        var check = dsl.selectCount().from(USERS).where(USERS.EMAIL.equalIgnoreCase(email)).execute();
        return check > 0;
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


}
