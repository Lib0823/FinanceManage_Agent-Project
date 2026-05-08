package com.inbeom.apiserver.exception;

public class KisApiException extends BusinessException {

    public KisApiException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public KisApiException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    // Factory methods for specific KIS API errors
    public static KisApiException clientError(String message) {
        return new KisApiException(ErrorCode.KIS_API_CLIENT_ERROR, message);
    }

    public static KisApiException clientError(String message, Throwable cause) {
        return new KisApiException(ErrorCode.KIS_API_CLIENT_ERROR, message, cause);
    }

    public static KisApiException serverError(String message) {
        return new KisApiException(ErrorCode.KIS_API_SERVER_ERROR, message);
    }

    public static KisApiException serverError(String message, Throwable cause) {
        return new KisApiException(ErrorCode.KIS_API_SERVER_ERROR, message, cause);
    }

    public static KisApiException networkError(String message) {
        return new KisApiException(ErrorCode.KIS_API_NETWORK_ERROR, message);
    }

    public static KisApiException networkError(String message, Throwable cause) {
        return new KisApiException(ErrorCode.KIS_API_NETWORK_ERROR, message, cause);
    }

    public static KisApiException oauthFailed(String message) {
        return new KisApiException(ErrorCode.KIS_OAUTH_FAILED, message);
    }

    public static KisApiException oauthFailed(String message, Throwable cause) {
        return new KisApiException(ErrorCode.KIS_OAUTH_FAILED, message, cause);
    }
}
