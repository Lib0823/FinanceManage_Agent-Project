package com.inbeom.apiserver.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateKisAccountRequest {

    @NotBlank(message = "계좌번호는 필수입니다")
    private String accountNumber;

    @NotBlank(message = "APP Key는 필수입니다")
    private String appKey;

    @NotBlank(message = "APP Secret은 필수입니다")
    private String appSecret;
}
