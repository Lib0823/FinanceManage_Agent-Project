package com.inbeom.apiserver.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateKisAccountResponse {

    private boolean valid;
    private String message;
    private String errorCode;

    public static ValidateKisAccountResponse success() {
        return ValidateKisAccountResponse.builder()
                .valid(true)
                .message("KIS 계정 인증에 성공했습니다")
                .build();
    }

    public static ValidateKisAccountResponse failure(String message, String errorCode) {
        return ValidateKisAccountResponse.builder()
                .valid(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }
}
