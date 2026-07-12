package com.notify.notify.modules.userRole.repository;

import com.notify.notify.modules.userRole.entities.UserRoleEntity;
import com.notify.notify.utils.base.repository.BaseRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRoleRepository extends BaseRepository<UserRoleEntity, Long> {

    public UserRoleRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected Class<UserRoleEntity> entityType() {
        return UserRoleEntity.class;
    }

    @Override
    protected String tableName() {
        return "user_roles";
    }

    @Override
    public Optional<UserRoleEntity> findById(Long id) {
        String sql = "SELECT * FROM user_roles WHERE id = ?";
        try {
            UserRoleEntity userRole = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                UserRoleEntity ur = new UserRoleEntity();
                ur.setId(rs.getLong("id"));
                ur.setUserId(rs.getLong("user_id"));
                ur.setRoleId(rs.getLong("role_id"));
                return ur;
            }, id);

            return Optional.ofNullable(userRole);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public UserRoleEntity insert(UserRoleEntity entity) {
        String sql = "INSERT INTO user_roles (id, user_id, role_id) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql,
                entity.getId(),
                entity.getUserId(),
                entity.getRoleId()
        );

        return entity;
    }

    @Override
    public UserRoleEntity update(UserRoleEntity entity) {
        String sql = "UPDATE user_roles SET user_id = ?, role_id = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                entity.getUserId(),
                entity.getRoleId(),
                entity.getId()
        );

        return entity;
    }
}