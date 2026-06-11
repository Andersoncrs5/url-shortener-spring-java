package com.read.api.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AggregateTypeEnum {

    USER,
    ADMINS,
    URL,
    URL_ACCESS_RULE,
    URL_TAG,
    API_KEY,
    ROLE
}