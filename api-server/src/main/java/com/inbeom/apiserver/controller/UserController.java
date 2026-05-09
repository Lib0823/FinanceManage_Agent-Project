package com.inbeom.apiserver.controller;

import com.inbeom.apiserver.dto.common.ApiResponse;
import com.inbeom.apiserver.dto.user.UpdateUserProfileRequest;
import com.inbeom.apiserver.dto.user.UpdateUserSettingsRequest;
import com.inbeom.apiserver.dto.user.UserProfileResponse;
import com.inbeom.apiserver.dto.user.UserSettingsResponse;
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
     * GET /api/users/me
     * Get user profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        UserProfileResponse profile = userService.getUserProfile(userId);

        return ResponseEntity.ok(
                ApiResponse.success("User profile retrieved successfully", profile)
        );
    }

    /**
     * PUT /api/users/me
     * Update user profile
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateUserProfileRequest request
    ) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        UserProfileResponse profile = userService.updateUserProfile(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("User profile updated successfully", profile)
        );
    }

    /**
     * GET /api/users/settings
     * Get user settings
     */
    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<UserSettingsResponse>> getUserSettings(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        UserSettingsResponse settings = userService.getUserSettings(userId);

        return ResponseEntity.ok(
                ApiResponse.success("User settings retrieved successfully", settings)
        );
    }

    /**
     * PUT /api/users/settings
     * Update user settings
     */
    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<UserSettingsResponse>> updateUserSettings(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody UpdateUserSettingsRequest request
    ) {
        String token = authHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        UserSettingsResponse settings = userService.updateUserSettings(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("User settings updated successfully", settings)
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
