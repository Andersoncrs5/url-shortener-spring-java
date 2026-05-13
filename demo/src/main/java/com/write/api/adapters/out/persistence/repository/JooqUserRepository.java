package com.write.api.adapters.out.persistence.repository;

import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.out.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.write.api.generated.jooq.tables.Users.USERS;


@Repository
@RequiredArgsConstructor
public class JooqUserRepository implements IUserRepository {

    private final DSLContext dsl;
    private final SnowflakeIdGenerator idGen;

    @Override
    public UserModel save(UserModel user) {
        user.setUpdatedAt(Instant.now());

        int rows = dsl.update(USERS)
                .set(USERS.NAME, user.getName())
                .set(USERS.EMAIL, user.getEmail())
                .where(USERS.ID.eq(user.getId()))
                .execute();

        return user;
    }

    @Override
    public UserModel insert(UserModel user) {
        long id = idGen.nextId();
        Instant now = Instant.now();

        dsl.insertInto(USERS)
                .set(USERS.ID, id)
                .set(USERS.NAME, user.getName())
                .set(USERS.EMAIL, user.getEmail())
                .set(USERS.REFRESH_TOKEN, user.getRefreshToken())
                .set(USERS.PASSWORD_HASH, user.getPasswordHash())
                .set(USERS.ACTIVE, user.isActive() ? (byte) 1 : (byte) 0)
                .set(USERS.EMAIL_VERIFIED, (byte) 0)
                .set(USERS.CREATED_AT, LocalDateTime.ofInstant(now, ZoneOffset.UTC))
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
}
