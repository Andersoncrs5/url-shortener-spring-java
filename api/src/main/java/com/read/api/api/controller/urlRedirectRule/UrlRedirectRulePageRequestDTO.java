package com.read.api.api.controller.urlRedirectRule;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
public class UrlRedirectRulePageRequestDTO {

    private int page = 0;

    private int size = 30;

    private UrlRedirectRuleOrderBy orderBy = UrlRedirectRuleOrderBy.CREATED_AT;

    private Direction direction = Direction.DESC;

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