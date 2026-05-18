package com.inbeom.apiserver.exception;

public class KisAccountNotFoundException extends BusinessException {

    public KisAccountNotFoundException(Long kisAccountId) {
        super(ErrorCode.KIS_ACCOUNT_NOT_FOUND, "KIS account not found: " + kisAccountId);
    }

    public KisAccountNotFoundException(String message) {
        super(ErrorCode.KIS_ACCOUNT_NOT_FOUND, message);
    }
}
