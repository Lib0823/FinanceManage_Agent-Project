package com.inbeom.apiserver.service;

import com.inbeom.apiserver.client.KisApiClient;
import com.inbeom.apiserver.domain.TradeHistory;
import com.inbeom.apiserver.domain.User;
import com.inbeom.apiserver.repository.TradeHistoryRepository;
import com.inbeom.apiserver.repository.UserRepository;
import com.inbeom.apiserver.service.KisAuthService.KisCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TradingService 단위 테스트")
class TradingServiceTest {

    @Mock
    private KisAuthService kisAuthService;

    @Mock
    private KisApiClient kisApiClient;

    @Mock
    private TradeHistoryRepository tradeHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TradingService tradingService;

    private User mockUser;
    private KisCredentials mockCredentials;
    private String mockKisToken;
    private Long userId;
    private Long kisAccountId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        kisAccountId = 1L;
        mockKisToken = "MOCK_KIS_ACCESS_TOKEN";
        mockCredentials = new KisCredentials(
                "MOCK_APP_KEY",
                "MOCK_APP_SECRET",
                "12345678-01",
                "01"
        );

        mockUser = User.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .name("Test User")
                .build();
    }

    @Test
    @DisplayName("executeBuy - 매수 주문 실행 및 trade_history 저장 성공")
    void executeBuy_Success() {
        // Given
        String stockCode = "005930";
        String stockName = "삼성전자";
        Integer quantity = 10;
        BigDecimal orderPrice = new BigDecimal("70000");

        Map<String, Object> kisResponse = new HashMap<>();
        kisResponse.put("output", Map.of("ODNO", "ORDER123456"));
        kisResponse.put("rt_cd", "0");
        kisResponse.put("msg1", "주문이 완료되었습니다.");

        when(kisAuthService.getKisAccessToken(kisAccountId)).thenReturn(mockKisToken);
        when(kisAuthService.getKisCredentials(kisAccountId)).thenReturn(mockCredentials);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(kisApiClient.post(
                eq("/uapi/domestic-stock/v1/trading/order-cash"),
                eq("VTTC0802U"),
                eq(mockKisToken),
                eq("MOCK_APP_KEY"),
                eq("MOCK_APP_SECRET"),
                anyMap(),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(kisResponse, HttpStatus.OK));

        ArgumentCaptor<TradeHistory> tradeHistoryCaptor = ArgumentCaptor.forClass(TradeHistory.class);
        when(tradeHistoryRepository.save(any(TradeHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Map<String, Object> result = tradingService.executeBuy(userId, kisAccountId, stockCode, stockName, quantity, orderPrice);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.get("rt_cd")).isEqualTo("0");

        verify(tradeHistoryRepository, times(1)).save(tradeHistoryCaptor.capture());
        TradeHistory savedHistory = tradeHistoryCaptor.getValue();

        assertThat(savedHistory.getUser()).isEqualTo(mockUser);
        assertThat(savedHistory.getOrderNumber()).isEqualTo("ORDER123456");
        assertThat(savedHistory.getStockCode()).isEqualTo(stockCode);
        assertThat(savedHistory.getStockName()).isEqualTo(stockName);
        assertThat(savedHistory.getOrderType()).isEqualTo("BUY");
        assertThat(savedHistory.getOrderStatus()).isEqualTo("PENDING");
        assertThat(savedHistory.getQuantity()).isEqualTo(quantity);
        assertThat(savedHistory.getOrderPrice()).isEqualTo(orderPrice);
        assertThat(savedHistory.getOrderedAt()).isNotNull();
    }

    @Test
    @DisplayName("executeSell - 매도 주문 실행 및 trade_history 저장 성공")
    void executeSell_Success() {
        // Given
        String stockCode = "005930";
        String stockName = "삼성전자";
        Integer quantity = 5;
        BigDecimal orderPrice = new BigDecimal("75000");

        Map<String, Object> kisResponse = new HashMap<>();
        kisResponse.put("output", Map.of("ODNO", "ORDER789012"));
        kisResponse.put("rt_cd", "0");

        when(kisAuthService.getKisAccessToken(kisAccountId)).thenReturn(mockKisToken);
        when(kisAuthService.getKisCredentials(kisAccountId)).thenReturn(mockCredentials);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(kisApiClient.post(
                eq("/uapi/domestic-stock/v1/trading/order-cash"),
                eq("VTTC0801U"),
                anyString(),
                anyString(),
                anyString(),
                anyMap(),
                eq(Map.class)
        )).thenReturn(new ResponseEntity<>(kisResponse, HttpStatus.OK));

        ArgumentCaptor<TradeHistory> tradeHistoryCaptor = ArgumentCaptor.forClass(TradeHistory.class);
        when(tradeHistoryRepository.save(any(TradeHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Map<String, Object> result = tradingService.executeSell(userId, kisAccountId, stockCode, stockName, quantity, orderPrice);

        // Then
        assertThat(result).isNotNull();

        verify(tradeHistoryRepository, times(1)).save(tradeHistoryCaptor.capture());
        TradeHistory savedHistory = tradeHistoryCaptor.getValue();

        assertThat(savedHistory.getOrderType()).isEqualTo("SELL");
        assertThat(savedHistory.getOrderStatus()).isEqualTo("PENDING");
        assertThat(savedHistory.getOrderNumber()).isEqualTo("ORDER789012");
    }

    @Test
    @DisplayName("executeBuy - 사용자 없을 때 예외 발생")
    void executeBuy_UserNotFound_ThrowsException() {
        // Given
        when(kisAuthService.getKisAccessToken(kisAccountId)).thenReturn(mockKisToken);
        when(kisAuthService.getKisCredentials(kisAccountId)).thenReturn(mockCredentials);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Map<String, Object> kisResponse = new HashMap<>();
        kisResponse.put("output", Map.of("ODNO", "ORDER123"));
        when(kisApiClient.post(anyString(), anyString(), anyString(), anyString(), anyString(), anyMap(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(kisResponse, HttpStatus.OK));

        // When & Then
        assertThatThrownBy(() ->
                tradingService.executeBuy(userId, kisAccountId, "005930", "삼성전자", 10, new BigDecimal("70000"))
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found: 1");
    }

    @Test
    @DisplayName("getTradeHistory - 사용자 거래 내역 조회 성공")
    void getTradeHistory_Success() {
        // Given
        TradeHistory history1 = TradeHistory.builder()
                .user(mockUser)
                .stockCode("005930")
                .stockName("삼성전자")
                .orderType("BUY")
                .orderStatus("EXECUTED")
                .quantity(10)
                .orderPrice(new BigDecimal("70000"))
                .build();

        TradeHistory history2 = TradeHistory.builder()
                .user(mockUser)
                .stockCode("000660")
                .stockName("SK하이닉스")
                .orderType("SELL")
                .orderStatus("EXECUTED")
                .quantity(5)
                .orderPrice(new BigDecimal("120000"))
                .build();

        when(tradeHistoryRepository.findByUserIdOrderByOrderedAtDesc(userId))
                .thenReturn(List.of(history1, history2));

        // When
        List<TradeHistory> result = tradingService.getTradeHistory(userId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getStockCode()).isEqualTo("005930");
        assertThat(result.get(1).getStockCode()).isEqualTo("000660");

        verify(tradeHistoryRepository, times(1)).findByUserIdOrderByOrderedAtDesc(userId);
    }

    @Test
    @DisplayName("extractOrderNumber - KIS 응답에서 주문번호 추출 성공")
    void extractOrderNumber_Success() {
        // Given
        Map<String, Object> kisResponse = new HashMap<>();
        kisResponse.put("output", Map.of("ODNO", "ORDER999888"));

        when(kisAuthService.getKisAccessToken(kisAccountId)).thenReturn(mockKisToken);
        when(kisAuthService.getKisCredentials(kisAccountId)).thenReturn(mockCredentials);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(kisApiClient.post(anyString(), anyString(), anyString(), anyString(), anyString(), anyMap(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(kisResponse, HttpStatus.OK));

        ArgumentCaptor<TradeHistory> tradeHistoryCaptor = ArgumentCaptor.forClass(TradeHistory.class);
        when(tradeHistoryRepository.save(any(TradeHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        tradingService.executeBuy(userId, kisAccountId, "005930", "삼성전자", 10, new BigDecimal("70000"));

        // Then
        verify(tradeHistoryRepository).save(tradeHistoryCaptor.capture());
        assertThat(tradeHistoryCaptor.getValue().getOrderNumber()).isEqualTo("ORDER999888");
    }
}
