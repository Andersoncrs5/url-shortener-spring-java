package com.notify.notify.utils.base.repository;

import com.notify.notify.utils.annotations.tx.ResultTransaction;
import com.notify.notify.utils.base.entities.BaseEntity;
import jakarta.persistence.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CustomCdcRepositoryImpl<T extends BaseEntity> implements CustomCdcRepository<T> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @ResultTransaction
    public T forceInsertWithId(T entity) {
        try {
            Class<?> clazz = entity.getClass();

            String tableName = clazz.getSimpleName().toLowerCase();
            if (clazz.isAnnotationPresent(Table.class)) {
                tableName = clazz.getAnnotation(Table.class).name();
            }

            StringJoiner columns = new StringJoiner(", ");
            StringJoiner valuesPlaceholder = new StringJoiner(", ");
            List<Object> values = new ArrayList<>();

            Class<?> currentClass = clazz;
            while (currentClass != null && currentClass != Object.class) {

                for (Field field : currentClass.getDeclaredFields()) {
                    field.setAccessible(true);

                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
                            field.isSynthetic() ||
                            field.isAnnotationPresent(jakarta.persistence.Transient.class)) {
                        continue;
                    }

                    Object value = field.get(entity);
                    String columnName = null;

                    if (field.isAnnotationPresent(Column.class)) {
                        Column col = field.getAnnotation(Column.class);
                        if (!col.name().isEmpty()) {
                            columnName = col.name();
                        }
                    }

                    if (columnName == null) {
                        columnName = field.getName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
                    }

                    columns.add(columnName);
                    valuesPlaceholder.add("?");
                    values.add(value);
                }

                currentClass = currentClass.getSuperclass();
            }

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)",
                    tableName, columns.toString(), valuesPlaceholder.toString());

            Query nativeQuery = entityManager.createNativeQuery(sql);
            for (int i = 0; i < values.size(); i++) {
                nativeQuery.setParameter(i + 1, values.get(i));
            }

            nativeQuery.executeUpdate();
            return entity;

        } catch (Exception e) {
            throw new org.springframework.dao.DataIntegrityViolationException(e.getMessage(), e);
        }
    }
}
