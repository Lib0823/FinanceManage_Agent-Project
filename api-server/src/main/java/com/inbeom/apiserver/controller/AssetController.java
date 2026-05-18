package com.inbeom.apiserver.controller;

import com.inbeom.apiserver.dto.common.ApiResponse;
import com.inbeom.apiserver.service.AssetService;
import com.inbeom.apiserver.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * GET /api/assets/holdings
     * Get user's stock holdings from KIS API
     */
    @GetMapping("/holdings")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHoldings(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);  // Remove "Bearer "
        Long kisAccountId = jwtTokenProvider.getKisAccountIdFromToken(token);

        Map<String, Object> holdings = assetService.getHoldings(kisAccountId);

        return ResponseEntity.ok(
                ApiResponse.success("Holdings retrieved successfully", holdings)
        );
    }

    /**
     * GET /api/assets/balance
     * Get user's cash balance from KIS API
     */
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBalance(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.substring(7);
        Long kisAccountId = jwtTokenProvider.getKisAccountIdFromToken(token);

        Map<String, Object> balance = assetService.getBalance(kisAccountId);

        return ResponseEntity.ok(
                ApiResponse.success("Balance retrieved successfully", balance)
        );
    }
}
