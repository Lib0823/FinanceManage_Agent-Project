package com.inbeom.apiserver.service;

import com.inbeom.apiserver.domain.User;
import com.inbeom.apiserver.domain.UserTradeConfig;
import com.inbeom.apiserver.dto.user.TradeConfigResponse;
import com.inbeom.apiserver.dto.user.UpdateTradeConfigRequest;
import com.inbeom.apiserver.exception.ErrorCode;
import com.inbeom.apiserver.exception.BusinessException;
import com.inbeom.apiserver.exception.UserNotFoundException;
import com.inbeom.apiserver.repository.RefreshTokenRepository;
import com.inbeom.apiserver.repository.TradeHistoryRepository;
import com.inbeom.apiserver.repository.UserKisAccountRepository;
import com.inbeom.apiserver.repository.UserRepository;
import com.inbeom.apiserver.repository.UserTradeConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserTradeConfigRepository tradeConfigRepository;
    private final UserKisAccountRepository kisAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TradeHistoryRepository tradeHistoryRepository;

    /**
     * Get user's trade configuration
     */
    @Transactional(readOnly = true)
    public TradeConfigResponse getTradeConfig(Long userId) {
        UserTradeConfig config = tradeConfigRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ENTITY_NOT_FOUND,
                        "Trade configuration not found for user: " + userId
                ));

        return TradeConfigResponse.builder()
                .id(config.getId())
                .orderAmount(config.getOrderAmount())
                .maxHoldings(config.getMaxHoldings())
                .orderType(config.getOrderType())
                .isActive(config.getIsActive())
                .build();
    }

    /**
     * Update user's trade configuration
     */
    @Transactional
    public TradeConfigResponse updateTradeConfig(Long userId, UpdateTradeConfigRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        UserTradeConfig config = tradeConfigRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ENTITY_NOT_FOUND,
                        "Trade configuration not found for user: " + userId
                ));

        // Update configuration
        config.setOrderAmount(request.getOrderAmount());
        config.setMaxHoldings(request.getMaxHoldings());
        config.setOrderType(request.getOrderType());
        config.setIsActive(request.getIsActive());

        config = tradeConfigRepository.save(config);

        return TradeConfigResponse.builder()
                .id(config.getId())
                .orderAmount(config.getOrderAmount())
                .maxHoldings(config.getMaxHoldings())
                .orderType(config.getOrderType())
                .isActive(config.getIsActive())
                .build();
    }

    /**
     * Delete user account (회원 탈퇴)
     * Cascading delete: RefreshToken, UserKisAccount, UserTradeConfig, TradeHistory
     */
    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        log.info("Deleting account for user: {} ({})", user.getUsername(), userId);

        // 1. Delete refresh tokens
        refreshTokenRepository.deleteByUserId(userId);
        log.debug("Deleted refresh tokens for user: {}", userId);

        // 2. Delete KIS account (if exists)
        kisAccountRepository.findByUserId(userId).ifPresent(kisAccount -> {
            kisAccountRepository.delete(kisAccount);
            log.debug("Deleted KIS account for user: {}", userId);
        });

        // 3. Delete trade config
        tradeConfigRepository.findByUserId(userId).ifPresent(config -> {
            tradeConfigRepository.delete(config);
            log.debug("Deleted trade config for user: {}", userId);
        });

        // 4. Delete trade history
        tradeHistoryRepository.deleteByUserId(userId);
        log.debug("Deleted trade history for user: {}", userId);

        // 5. Finally delete user
        userRepository.delete(user);
        log.info("Successfully deleted account for user: {}", userId);
    }
}
