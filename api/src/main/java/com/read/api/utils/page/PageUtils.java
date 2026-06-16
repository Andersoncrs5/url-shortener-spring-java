package com.read.api.utils.page;

import org.springframework.data.domain.Page;

import java.util.function.Function;

public final class PageUtils {

    private PageUtils() {}

    public static <T, R> Page<R> map(
            Page<T> page,
            Function<T, R> mapper
    ) {
        return page.map(mapper);
    }
}
