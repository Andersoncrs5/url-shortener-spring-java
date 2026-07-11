package com.notify.notify.modules.user.repository;

import com.notify.notify.modules.user.entities.UserEntity;
import com.notify.notify.utils.base.repository.BaseRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<UserEntity, Long> {

    public UserRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected Class<UserEntity> entityType() {
        return UserEntity.class;
    }

    @Override
    protected String tableName() {
        return "users";
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            UserEntity user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                UserEntity u = new UserEntity();
                u.setId(rs.getLong("id"));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setActive(rs.getBoolean("active"));
                u.setEmailVerified(rs.getBoolean("email_verified"));

                Timestamp blockedAtTs = rs.getTimestamp("blocked_at");
                u.setBlockedAt(blockedAtTs != null ? blockedAtTs.toLocalDateTime() : null);

                String rolesStr = rs.getString("roles");
                if (rolesStr != null && !rolesStr.isBlank()) {
                    u.setRoles(new HashSet<>(Arrays.asList(rolesStr.split(","))));
                } else {
                    u.setRoles(new HashSet<>());
                }
                return u;
            }, id);

            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public UserEntity insert(UserEntity entity) {
        String sql = "INSERT INTO users (id, name, email, active, email_verified, blocked_at, roles) VALUES (?, ?, ?, ?, ?, ?, ?)";

        String rolesStr = entity.getRoles() != null ? String.join(",", entity.getRoles()) : "";

        jdbcTemplate.update(sql,
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getActive() != null ? entity.getActive() : true,
                entity.getEmailVerified() != null ? entity.getEmailVerified() : false,
                entity.getBlockedAt() != null ? Timestamp.valueOf(entity.getBlockedAt()) : null,
                rolesStr
        );
        return entity;
    }

    @Override
    public UserEntity update(UserEntity entity) {
        String sql = "UPDATE users SET name = ?, email = ?, active = ?, email_verified = ?, blocked_at = ?, roles = ? WHERE id = ?";

        String rolesStr = entity.getRoles() != null ? String.join(",", entity.getRoles()) : "";

        jdbcTemplate.update(sql,
                entity.getName(),
                entity.getEmail(),
                entity.getActive(),
                entity.getEmailVerified(),
                entity.getBlockedAt() != null ? Timestamp.valueOf(entity.getBlockedAt()) : null,
                rolesStr,
                entity.getId()
        );
        return entity;
    }
}