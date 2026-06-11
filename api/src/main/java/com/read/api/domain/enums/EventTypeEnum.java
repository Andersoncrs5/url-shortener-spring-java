package com.read.api.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventTypeEnum {

    CREATED,
    UPDATED,
    DELETED,
    ACTIVATED,
    DEACTIVATED,

    USER_CREATED,
    USER_UPDATED,
    USER_BLOCKED,
    USER_DELETED,
    USER_LOGIN_SUCCESS,
    USER_LOGIN_FAILED,

    URL_ACCESS_RULE_CREATED,

    URL_CREATED,
    URL_UPDATED,
    URL_DELETED,

    API_KEY_CREATED,
    API_KEY_REVOKED,

    ADMINS_NOTIFICATION
}
