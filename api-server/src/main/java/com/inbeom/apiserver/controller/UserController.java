package com.inbeom.apiserver.controller;

import com.inbeom.apiserver.dto.common.ApiResponse;
import com.inbeom.apiserver.dto.user.TradeConfigResponse;
import com.inbeom.apiserver.dto.user.UpdateTradeConfigRequest;
import com.inbeom.apiserver.service.UserService;
import com.inbeom.apiserver.util.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * GET /api/users/trade-config
     * Get user's trade configuration
     */
    @GetMapping("/trade-config")
    public ResponseEntity<ApiResponse<TradeConfigResponse>> getTradeConfig(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        TradeConfigResponse config = userService.getTradeConfig(userId);

        return ResponseEntity.ok(
                ApiResponse.success("Trade configuration retrieved successfully", config)
        );
    }

    /**
     * PUT /api/users/trade-config
     * Update user's trade configuration
     */
    @PutMapping("/trade-config")
    public ResponseEntity<ApiResponse<TradeConfigResponse>> updateTradeConfig(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateTradeConfigRequest request
    ) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        TradeConfigResponse config = userService.updateTradeConfig(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("Trade configuration updated successfully", config)
        );
    }

    /**
     * DELETE /api/users/me
     * Delete user account (회원 탈퇴)
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        log.info("User deletion requested: userId={}", userId);
        userService.deleteAccount(userId);

        return ResponseEntity.ok(
                ApiResponse.success("Account deleted successfully", null)
        );
    }
}
