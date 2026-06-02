package com.write.api.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AggregateTypeEnum {

    USER,
    URL,
    URL_TAG,
    API_KEY,
    ROLE
}