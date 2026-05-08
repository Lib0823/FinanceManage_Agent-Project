package com.inbeom.apiserver.service;

import com.inbeom.apiserver.client.KisApiClient;
import com.inbeom.apiserver.domain.TradeHistory;
import com.inbeom.apiserver.domain.User;
import com.inbeom.apiserver.exception.UserNotFoundException;
import com.inbeom.apiserver.repository.TradeHistoryRepository;
import com.inbeom.apiserver.repository.UserRepository;
import com.inbeom.apiserver.service.KisAuthService.KisCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradingService {

    private final KisAuthService kisAuthService;
    private final KisApiClient kisApiClient;
    private final TradeHistoryRepository tradeHistoryRepository;
    private final UserRepository userRepository;

    /**
     * Execute buy order via KIS API (VTTC0802U)
     */
    @Transactional
    public Map<String, Object> executeBuy(Long userId, Long kisAccountId, String stockCode, String stockName,
                                           Integer quantity, BigDecimal orderPrice) {
        // 1. Get KIS credentials and token
        String kisToken = kisAuthService.getKisAccessToken(kisAccountId);
        KisCredentials credentials = kisAuthService.getKisCredentials(kisAccountId);

        // 2. Build request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("CANO", credentials.accountNumber());
        requestBody.put("ACNT_PRDT_CD", credentials.accountProductCode());
        requestBody.put("PDNO", stockCode);
        requestBody.put("ORD_DVSN", "01"); // 시장가
        requestBody.put("ORD_QTY", String.valueOf(quantity));
        requestBody.put("ORD_UNPR", "0"); // 시장가는 0

        // 3. Call KIS API
        ResponseEntity<Map> response = kisApiClient.post(
                "/uapi/domestic-stock/v1/trading/order-cash",
                "VTTC0802U",
                kisToken,
                credentials.appKey(),
                credentials.appSecret(),
                requestBody,
                Map.class
        );

        // 4. Save trade history
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        TradeHistory tradeHistory = TradeHistory.builder()
                .user(user)
                .orderNumber(extractOrderNumber(response.getBody()))
                .stockCode(stockCode)
                .stockName(stockName)
                .orderType("BUY")
                .orderStatus("PENDING")
                .quantity(quantity)
                .orderPrice(orderPrice)
                .orderedAt(LocalDateTime.now())
                .build();

        tradeHistoryRepository.save(tradeHistory);

        return response.getBody();
    }

    /**
     * Execute sell order via KIS API (VTTC0801U)
     */
    @Transactional
    public Map<String, Object> executeSell(Long userId, Long kisAccountId, String stockCode, String stockName,
                                            Integer quantity, BigDecimal orderPrice) {
        String kisToken = kisAuthService.getKisAccessToken(kisAccountId);
        KisCredentials credentials = kisAuthService.getKisCredentials(kisAccountId);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("CANO", credentials.accountNumber());
        requestBody.put("ACNT_PRDT_CD", credentials.accountProductCode());
        requestBody.put("PDNO", stockCode);
        requestBody.put("ORD_DVSN", "01");
        requestBody.put("ORD_QTY", String.valueOf(quantity));
        requestBody.put("ORD_UNPR", "0");

        ResponseEntity<Map> response = kisApiClient.post(
                "/uapi/domestic-stock/v1/trading/order-cash",
                "VTTC0801U",
                kisToken,
                credentials.appKey(),
                credentials.appSecret(),
                requestBody,
                Map.class
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        TradeHistory tradeHistory = TradeHistory.builder()
                .user(user)
                .orderNumber(extractOrderNumber(response.getBody()))
                .stockCode(stockCode)
                .stockName(stockName)
                .orderType("SELL")
                .orderStatus("PENDING")
                .quantity(quantity)
                .orderPrice(orderPrice)
                .orderedAt(LocalDateTime.now())
                .build();

        tradeHistoryRepository.save(tradeHistory);

        return response.getBody();
    }

    /**
     * Get trade history for user
     */
    public List<TradeHistory> getTradeHistory(Long userId) {
        return tradeHistoryRepository.findByUserIdOrderByOrderedAtDesc(userId);
    }

    private String extractOrderNumber(Map<String, Object> kisResponse) {
        if (kisResponse != null && kisResponse.containsKey("output")) {
            Map<String, Object> output = (Map<String, Object>) kisResponse.get("output");
            return (String) output.get("ODNO");
        }
        return null;
    }
}
