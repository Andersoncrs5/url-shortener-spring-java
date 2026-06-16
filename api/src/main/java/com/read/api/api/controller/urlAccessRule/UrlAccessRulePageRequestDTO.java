package com.read.api.api.controller.urlAccessRule;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
public class UrlAccessRulePageRequestDTO {

    private Integer page = 0;

    private Integer size = 30;

    private UrlAccessRuleOrderBy orderBy = UrlAccessRuleOrderBy.CREATED_AT;

    private Direction direction = Direction.DESC;

    public Pageable toPageable() {
        return PageRequest.of(
                page,
                size,
                Sort.by(direction, orderBy.getField())
        );
    }
}
