package com.inbeom.apiserver.exception;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(Long userId) {
        super(ErrorCode.USER_NOT_FOUND, "User not found: " + userId);
    }

    public UserNotFoundException(String username) {
        super(ErrorCode.USER_NOT_FOUND, "User not found: " + username);
    }
}
