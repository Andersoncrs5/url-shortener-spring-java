package com.notify.notify.utils.base.repository;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Optional;

public abstract class BaseRepository<T, ID> {

    protected final JdbcTemplate jdbcTemplate;

    protected BaseRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract Class<T> entityType();
    protected abstract String tableName();

    public Optional<T> findById(ID id) {
        String sql = "SELECT * FROM " + tableName() + " WHERE id = ?";
        try {
            T entity = jdbcTemplate.queryForObject(
                    sql,
                    new BeanPropertyRowMapper<>(entityType()),
                    id
            );
            return Optional.ofNullable(entity);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public int deleteAndCount(ID id) {
        String sql = "DELETE FROM " + tableName() + " WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public boolean existsById(ID id) {
        String sql = "SELECT COUNT(*) FROM " + tableName() + " WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public abstract T insert(T entity);
    public abstract T update(T entity);

}