package com.inbeom.apiserver.controller;

import com.inbeom.apiserver.dto.auth.*;
import com.inbeom.apiserver.dto.common.ApiResponse;
import com.inbeom.apiserver.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success("Login successful", response);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ApiResponse.success("Registration successful", response);
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password reset successful", null);
    }

    @GetMapping("/check-username")
    public ApiResponse<CheckAvailabilityResponse> checkUsername(@RequestParam String username) {
        CheckAvailabilityResponse response = authService.checkUsername(username);
        String message = response.isAvailable() ? "Username is available" : "Username is already taken";
        return ApiResponse.success(message, response);
    }

    @GetMapping("/check-email")
    public ApiResponse<CheckAvailabilityResponse> checkEmail(@RequestParam String email) {
        CheckAvailabilityResponse response = authService.checkEmail(email);
        String message = response.isAvailable() ? "Email is available" : "Email is already taken";
        return ApiResponse.success(message, response);
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ApiResponse.success("Token refreshed", response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.success("Logout successful", null);
    }
}
