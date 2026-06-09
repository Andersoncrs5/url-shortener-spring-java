package com.read.api.infrastructure.persistence.utils;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public final class QueryUtils {

    private QueryUtils() {}

    public static void addLike(
            Query query,
            String field,
            String value
    ) {

        if (value != null &&
                !value.isBlank()) {

            query.addCriteria(
                    Criteria.where(field)
                            .regex(value, "i")
            );
        }
    }

    public static void addEquals(
            Query query,
            String field,
            Object value
    ) {

        if (value != null) {

            query.addCriteria(
                    Criteria.where(field)
                            .is(value)
            );
        }
    }

    public static void addRange(
            Query query,
            String field,
            Object from,
            Object to
    ) {

        if (from == null && to == null) {
            return;
        }

        Criteria criteria =
                Criteria.where(field);

        if (from != null) {
            criteria.gte(from);
        }

        if (to != null) {
            criteria.lte(to);
        }

        query.addCriteria(criteria);
    }
}