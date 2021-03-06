package com.wemeet.dating.model.response;

public enum ResponseCode {
    SUCCESS, INVALID_TOKEN, RESOURCE_NOT_FOUND, ENTITY_NOT_FOUND,
    INVALID_USERNAME_PASSWORD, USER_NOT_VERIFIED, SUSPENDED_USER,
    VALIDATION_ERROR, ERROR, USERS_NOT_MATCHED, PREFERENCE_NOT_SET, USER_NOT_PREMIUM, BLOCKED_USER;
}
