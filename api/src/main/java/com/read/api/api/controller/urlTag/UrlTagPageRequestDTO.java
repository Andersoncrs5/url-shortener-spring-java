package com.read.api.api.controller.urlTag;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class UrlTagPageRequestDTO {

    private Integer page = 0;
    private Integer size = 30;

    private UrlTagOrderBy orderBy = UrlTagOrderBy.CREATED_AT;

    private Sort.Direction direction = Sort.Direction.DESC;

    public Pageable toPageable() {
        return PageRequest.of(
                page,
                size,
                Sort.by(
                        direction,
                        orderBy.getField()
                )
        );
    }
}