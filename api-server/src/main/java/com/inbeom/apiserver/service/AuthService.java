package com.inbeom.apiserver.service;

import com.inbeom.apiserver.domain.RefreshToken;
import com.inbeom.apiserver.domain.User;
import com.inbeom.apiserver.domain.UserKisAccount;
import com.inbeom.apiserver.domain.UserTradeConfig;
import com.inbeom.apiserver.dto.auth.*;
import com.inbeom.apiserver.exception.*;
import com.inbeom.apiserver.repository.RefreshTokenRepository;
import com.inbeom.apiserver.repository.UserKisAccountRepository;
import com.inbeom.apiserver.repository.UserRepository;
import com.inbeom.apiserver.repository.UserTradeConfigRepository;
import com.inbeom.apiserver.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserKisAccountRepository kisAccountRepository;
    private final UserTradeConfigRepository tradeConfigRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 비밀번호 확인 검증
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH, "Password confirmation does not match");
        }

        // 중복 확인
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATE, "Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATE, "Email already exists");
        }

        // User 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .name(request.getName())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .build();
        user = userRepository.save(user);

        // KIS Account 생성
        if (request.getKisAccount() != null) {
            UserKisAccount kisAccount = UserKisAccount.builder()
                    .user(user)
                    .accountNumber(request.getKisAccount().getAccountNumber())
                    .appKey(request.getKisAccount().getAppKey())
                    .appSecret(request.getKisAccount().getAppSecret())
                    .isVerified(false)
                    .build();
            kisAccountRepository.save(kisAccount);
        }

        // Trade Config 생성 (기본값)
        UserTradeConfig tradeConfig = UserTradeConfig.builder()
                .user(user)
                .orderAmount(1000000L)
                .maxHoldings(10)
                .orderType("market")
                .isActive(false)
                .build();
        tradeConfigRepository.save(tradeConfig);

        return RegisterResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 사용자 조회
        User user = userRepository.findByUsername(request.getUsername())
                 .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // KIS Account 조회
        UserKisAccount kisAccount = kisAccountRepository.findByUser(user)
                .orElseThrow(() -> new KisAccountNotFoundException(user.getId()));

        // JWT 토큰 생성 (userId, kisAccountId 포함)
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUsername(), user.getId(), kisAccount.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        // RefreshToken 저장
        saveRefreshToken(user, refreshToken);

        // 응답 생성
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .name(user.getName())
                        .build())
                .build();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 비밀번호 확인 검증
        if (!request.getNewPassword().equals(request.getPasswordConfirm())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH, "Password confirmation does not match");
        }

        // 사용자 조회 및 본인 인증 (username + phone)
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.PHONE_MISMATCH, "User not found or phone number mismatch"));

        if (!user.getPhone().equals(request.getPhone())) {
            throw new BusinessException(ErrorCode.PHONE_MISMATCH, "User not found or phone number mismatch");
        }

        // 비밀번호 변경
        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public CheckAvailabilityResponse checkUsername(String username) {
        boolean available = !userRepository.existsByUsername(username);
        return CheckAvailabilityResponse.builder()
                .available(available)
                .build();
    }

    @Transactional(readOnly = true)
    public CheckAvailabilityResponse checkEmail(String email) {
        boolean available = userRepository.findByEmail(email).isEmpty();
        return CheckAvailabilityResponse.builder()
                .available(available)
                .build();
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getRefreshToken();

        // 토큰 검증
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Invalid refresh token");
        }

        // RefreshToken 조회
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND, "Refresh token not found"));

        // 토큰이 revoke 되었는지 확인
        if (refreshToken.getRevokedAt() != null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_REVOKED, "Refresh token has been revoked");
        }

        // 새로운 AccessToken 생성
        String username = jwtTokenProvider.getUsernameFromToken(refreshTokenValue);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        UserKisAccount kisAccount = kisAccountRepository.findByUser(user)
                .orElseThrow(() -> new KisAccountNotFoundException(user.getId()));

        String newAccessToken = jwtTokenProvider.generateAccessToken(username, user.getId(), kisAccount.getId());

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpiration())
                .build();
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        // RefreshToken 조회 및 revoke
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND, "Refresh token not found"));

        if (refreshToken.getRevokedAt() != null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_REVOKED, "Refresh token has already been revoked");
        }

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);
    }

    private void saveRefreshToken(User user, String token) {
        // 기존 토큰 revoke
        refreshTokenRepository.findByUserAndRevokedAtIsNull(user)
                .ifPresent(rt -> {
                    rt.revoke();
                    refreshTokenRepository.save(rt);
                });

        // 새 토큰 저장
        LocalDateTime expiresAt = LocalDateTime.now()
                .plusSeconds(jwtTokenProvider.getRefreshTokenExpiration() / 1000);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
