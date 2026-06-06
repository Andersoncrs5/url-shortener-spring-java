package com.write.api.core.domain.enums;

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