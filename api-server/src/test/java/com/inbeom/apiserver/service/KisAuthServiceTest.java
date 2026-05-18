package com.inbeom.apiserver.service;

import com.inbeom.apiserver.domain.UserKisAccount;
import com.inbeom.apiserver.dto.kis.KisTokenResponse;
import com.inbeom.apiserver.repository.UserKisAccountRepository;
import com.inbeom.apiserver.service.KisAuthService.KisCredentials;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KisAuthService 단위 테스트")
class KisAuthServiceTest {

    @Mock
    private UserKisAccountRepository kisAccountRepository;

    @Mock
    private StringEncryptor jasyptStringEncryptor;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KisAuthService kisAuthService;

    private UserKisAccount mockKisAccount;

    @BeforeEach
    void setUp() {
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(kisAuthService, "kisBaseUrl", "https://openapi.koreainvestment.com:9443");
        ReflectionTestUtils.setField(kisAuthService, "tokenCacheTtl", 86400000L); // 24h
        ReflectionTestUtils.setField(kisAuthService, "restTemplate", restTemplate);

        // Mock UserKisAccount
        mockKisAccount = UserKisAccount.builder()
                .id(1L)
                .accountNumber("12345678-01")
                .accountProductCode("01")
                .appKey("ENCRYPTED_APP_KEY")
                .appSecret("ENCRYPTED_APP_SECRET")
                .isVerified(true)
                .build();
    }

    @Test
    @DisplayName("첫 호출 시 캐시 미스 - DB 조회 + KIS API 호출 + 캐싱")
    void getKisAccessToken_CacheMiss_ShouldFetchFromDbAndApi() {
        // Given
        Long kisAccountId = 1L;
        String decryptedAppKey = "DECRYPTED_APP_KEY";
        String decryptedAppSecret = "DECRYPTED_APP_SECRET";
        String expectedToken = "MOCK_KIS_ACCESS_TOKEN";

        when(kisAccountRepository.findById(kisAccountId)).thenReturn(Optional.of(mockKisAccount));
        when(jasyptStringEncryptor.decrypt("ENCRYPTED_APP_KEY")).thenReturn(decryptedAppKey);
        when(jasyptStringEncryptor.decrypt("ENCRYPTED_APP_SECRET")).thenReturn(decryptedAppSecret);

        KisTokenResponse kisTokenResponse = new KisTokenResponse();
        kisTokenResponse.setAccessToken(expectedToken);
        kisTokenResponse.setTokenType("Bearer");
        kisTokenResponse.setExpiresIn(86400);

        ResponseEntity<KisTokenResponse> responseEntity = new ResponseEntity<>(kisTokenResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(KisTokenResponse.class)))
                .thenReturn(responseEntity);

        // When
        String actualToken = kisAuthService.getKisAccessToken(kisAccountId);

        // Then
        assertThat(actualToken).isEqualTo(expectedToken);
        verify(kisAccountRepository, times(1)).findById(kisAccountId);
        verify(jasyptStringEncryptor, times(2)).decrypt(anyString()); // appKey + appSecret
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(KisTokenResponse.class));
    }

    @Test
    @DisplayName("두 번째 호출 시 캐시 히트 - DB/API 호출 없이 캐시에서 반환")
    void getKisAccessToken_CacheHit_ShouldReturnFromCache() {
        // Given
        Long kisAccountId = 1L;
        String decryptedAppKey = "DECRYPTED_APP_KEY";
        String decryptedAppSecret = "DECRYPTED_APP_SECRET";
        String expectedToken = "MOCK_KIS_ACCESS_TOKEN";

        when(kisAccountRepository.findById(kisAccountId)).thenReturn(Optional.of(mockKisAccount));
        when(jasyptStringEncryptor.decrypt("ENCRYPTED_APP_KEY")).thenReturn(decryptedAppKey);
        when(jasyptStringEncryptor.decrypt("ENCRYPTED_APP_SECRET")).thenReturn(decryptedAppSecret);

        KisTokenResponse kisTokenResponse = new KisTokenResponse();
        kisTokenResponse.setAccessToken(expectedToken);
        ResponseEntity<KisTokenResponse> responseEntity = new ResponseEntity<>(kisTokenResponse, HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(), eq(KisTokenResponse.class)))
                .thenReturn(responseEntity);

        // When - First call (cache miss)
        String firstCallToken = kisAuthService.getKisAccessToken(kisAccountId);

        // When - Second call (cache hit)
        String secondCallToken = kisAuthService.getKisAccessToken(kisAccountId);

        // Then
        assertThat(firstCallToken).isEqualTo(expectedToken);
        assertThat(secondCallToken).isEqualTo(expectedToken);
        verify(kisAccountRepository, times(1)).findById(kisAccountId); // Only called once
        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(KisTokenResponse.class)); // Only called once
    }

    @Test
    @DisplayName("존재하지 않는 KIS 계정 조회 시 예외 발생")
    void getKisAccessToken_AccountNotFound_ShouldThrowException() {
        // Given
        Long kisAccountId = 999L;
        when(kisAccountRepository.findById(kisAccountId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> kisAuthService.getKisAccessToken(kisAccountId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("KIS account not found: 999");
    }

    @Test
    @DisplayName("getKisCredentials - 복호화된 인증 정보 반환")
    void getKisCredentials_ShouldReturnDecryptedCredentials() {
        // Given
        Long kisAccountId = 1L;
        String decryptedAppKey = "DECRYPTED_APP_KEY";
        String decryptedAppSecret = "DECRYPTED_APP_SECRET";

        when(kisAccountRepository.findById(kisAccountId)).thenReturn(Optional.of(mockKisAccount));
        when(jasyptStringEncryptor.decrypt("ENCRYPTED_APP_KEY")).thenReturn(decryptedAppKey);
        when(jasyptStringEncryptor.decrypt("ENCRYPTED_APP_SECRET")).thenReturn(decryptedAppSecret);

        // When
        KisCredentials credentials = kisAuthService.getKisCredentials(kisAccountId);

        // Then
        assertThat(credentials).isNotNull();
        assertThat(credentials.appKey()).isEqualTo(decryptedAppKey);
        assertThat(credentials.appSecret()).isEqualTo(decryptedAppSecret);
        assertThat(credentials.accountNumber()).isEqualTo("12345678-01");
        assertThat(credentials.accountProductCode()).isEqualTo("01");

        verify(jasyptStringEncryptor, times(2)).decrypt(anyString());
    }

    @Test
    @DisplayName("KIS API 호출 실패 시 예외 발생")
    void getKisAccessToken_KisApiFailure_ShouldThrowException() {
        // Given
        Long kisAccountId = 1L;
        String decryptedAppKey = "DECRYPTED_APP_KEY";
        String decryptedAppSecret = "DECRYPTED_APP_SECRET";

        when(kisAccountRepository.findById(kisAccountId)).thenReturn(Optional.of(mockKisAccount));
        when(jasyptStringEncryptor.decrypt("ENCRYPTED_APP_KEY")).thenReturn(decryptedAppKey);
        when(jasyptStringEncryptor.decrypt("ENCRYPTED_APP_SECRET")).thenReturn(decryptedAppSecret);

        // KIS API returns error
        ResponseEntity<KisTokenResponse> errorResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.postForEntity(anyString(), any(), eq(KisTokenResponse.class)))
                .thenReturn(errorResponse);

        // When & Then
        assertThatThrownBy(() -> kisAuthService.getKisAccessToken(kisAccountId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("KIS OAuth");
    }
}
