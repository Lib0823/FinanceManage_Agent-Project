package com.inbeom.apiserver.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTradeConfigRequest {

    @NotNull(message = "Order amount is required")
    @Min(value = 0, message = "Order amount must be positive")
    private Long orderAmount;

    @NotNull(message = "Max holdings is required")
    @Min(value = 1, message = "Max holdings must be at least 1")
    private Integer maxHoldings;

    @NotNull(message = "Order type is required")
    @Pattern(regexp = "market|limit", message = "Order type must be 'market' or 'limit'")
    private String orderType;

    @NotNull(message = "isActive is required")
    private Boolean isActive;
}
