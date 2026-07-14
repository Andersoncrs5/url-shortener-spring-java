package com.notify.notify.modules.roles.repository;

import com.notify.notify.modules.roles.entities.RoleEntity;
import com.notify.notify.utils.base.repository.BaseRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RoleRepository extends BaseRepository<RoleEntity, Long> {

    public RoleRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected Class<RoleEntity> entityType() {
        return RoleEntity.class;
    }

    @Override
    protected String tableName() {
        return "roles";
    }

    @Override
    public RoleEntity insert(RoleEntity entity) {
        String sql = "INSERT INTO roles (id, name, description, active) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getActive() != null ? entity.getActive() : true
        );
        return entity;
    }

    @Override
    public RoleEntity update(RoleEntity entity) {
        String sql = "UPDATE roles SET name = ?, description = ?, active = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                entity.getName(),
                entity.getDescription(),
                entity.getActive(),
                entity.getId()
        );
        return entity;
    }
}