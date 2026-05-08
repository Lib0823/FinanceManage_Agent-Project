package com.inbeom.apiserver.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeConfigResponse {

    private Long id;
    private Long orderAmount;
    private Integer maxHoldings;
    private String orderType;
    private Boolean isActive;
}
