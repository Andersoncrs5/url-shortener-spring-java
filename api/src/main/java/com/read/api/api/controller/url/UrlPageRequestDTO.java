package com.read.api.api.controller.url;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Setter
@Getter
public class UrlPageRequestDTO {
    private Integer page = 0;

    private Integer size = 30;

    private UrlOrderBy orderBy = UrlOrderBy.CREATED_AT;

    private Sort.Direction direction = Sort.Direction.DESC;

    public Pageable toPageable() {
        return PageRequest.of(
                page,
                size,
                Sort.by(direction, orderBy.getField())
        );
    }
}
