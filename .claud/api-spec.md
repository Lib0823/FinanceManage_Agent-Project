# API Specification

## Base URL

```
로컬: http://localhost:8080/api/v1
개발: http://dev.asset-agent.com/api/v1
운영: https://api.asset-agent.com/api/v1
```

## 인증

**JWT Bearer Token:**
```http
Authorization: Bearer {access_token}
```

## 표준 응답 형식

### 성공 응답

```json
{
  "success": true,
  "data": {
    // 응답 데이터
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 에러 응답

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "사용자용 에러 메시지",
    "details": "상세 정보 (개발용)"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 페이징 응답

```json
{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

## 에러 코드

| 코드 | HTTP Status | 설명 |
|------|-------------|------|
| AUTH_001 | 401 | 인증 실패 |
| AUTH_002 | 401 | 토큰 만료 |
| AUTH_003 | 403 | 권한 없음 |
| USER_001 | 404 | 사용자 없음 |
| USER_002 | 409 | 이메일 중복 |
| TRADING_001 | 400 | 잘못된 주문 |
| TRADING_002 | 400 | 잔고 부족 |
| TRADING_003 | 404 | 종목 없음 |
| SYSTEM_001 | 500 | 서버 오류 |
| EXTERNAL_001 | 503 | 외부 API 오류 |

---

## 1. 인증 (Auth)

### 1.1. 회원가입

```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123!",
  "name": "홍길동",
  "phoneNumber": "010-1234-5678"
}
```

**응답 (201 Created):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "createdAt": "2026-01-18T10:30:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 1.2. 로그인

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123!"
}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "name": "홍길동"
    }
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 1.3. 토큰 갱신

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 1.4. 로그아웃

```http
POST /api/v1/auth/logout
Authorization: Bearer {access_token}
```

**응답 (204 No Content)**

### 1.5. 내 정보 조회

```http
GET /api/v1/auth/me
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "name": "홍길동",
    "phoneNumber": "010-1234-5678",
    "createdAt": "2026-01-18T10:30:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

---

## 2. 주식 거래 (Trading)

### 2.1. 실시간 시세 조회

```http
GET /api/v1/trading/quotes/{symbol}
Authorization: Bearer {access_token}

# 예시
GET /api/v1/trading/quotes/005930  # 삼성전자
GET /api/v1/trading/quotes/AAPL    # Apple (미국)
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "symbol": "005930",
    "name": "삼성전자",
    "market": "KOSPI",
    "currentPrice": 75000,
    "changePrice": 1000,
    "changeRate": 1.35,
    "high": 76000,
    "low": 74000,
    "volume": 12345678,
    "timestamp": "2026-01-18T10:30:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 2.2. 호가 조회

```http
GET /api/v1/trading/quotes/{symbol}/orderbook
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "symbol": "005930",
    "asks": [
      {"price": 75100, "quantity": 1000},
      {"price": 75200, "quantity": 2000},
      {"price": 75300, "quantity": 1500}
    ],
    "bids": [
      {"price": 75000, "quantity": 3000},
      {"price": 74900, "quantity": 2500},
      {"price": 74800, "quantity": 1000}
    ],
    "timestamp": "2026-01-18T10:30:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 2.3. WebSocket - 실시간 시세 구독

```javascript
// 연결
const ws = new WebSocket('ws://localhost:8080/ws/v1/trading/quotes')

// 구독 요청
ws.send(JSON.stringify({
  action: 'subscribe',
  symbols: ['005930', 'AAPL']
}))

// 메시지 수신
ws.onmessage = (event) => {
  const data = JSON.parse(event.data)
  // {
  //   symbol: "005930",
  //   price: 75000,
  //   change: 1000,
  //   changeRate: 1.35,
  //   timestamp: "2026-01-18T10:30:00Z"
  // }
}
```

### 2.4. 주문 생성

```http
POST /api/v1/trading/orders
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "symbol": "005930",
  "type": "BUY",          # BUY | SELL
  "orderType": "LIMIT",   # LIMIT | MARKET
  "price": 75000,         # MARKET일 경우 null
  "quantity": 10
}
```

**응답 (201 Created):**
```json
{
  "success": true,
  "data": {
    "orderId": "ORD-20260118-001",
    "symbol": "005930",
    "name": "삼성전자",
    "type": "BUY",
    "orderType": "LIMIT",
    "price": 75000,
    "quantity": 10,
    "status": "PENDING",     # PENDING | FILLED | CANCELLED
    "createdAt": "2026-01-18T10:30:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 2.5. 주문 목록 조회

```http
GET /api/v1/trading/orders?status=PENDING&page=0&size=20
Authorization: Bearer {access_token}

# Query Parameters
# - status: PENDING | FILLED | CANCELLED | ALL
# - page: 페이지 번호 (0부터 시작)
# - size: 페이지 크기 (기본 20)
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "orderId": "ORD-20260118-001",
        "symbol": "005930",
        "name": "삼성전자",
        "type": "BUY",
        "orderType": "LIMIT",
        "price": 75000,
        "quantity": 10,
        "filledQuantity": 0,
        "status": "PENDING",
        "createdAt": "2026-01-18T10:30:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 5,
    "totalPages": 1
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 2.6. 주문 상세 조회

```http
GET /api/v1/trading/orders/{orderId}
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "orderId": "ORD-20260118-001",
    "symbol": "005930",
    "name": "삼성전자",
    "type": "BUY",
    "orderType": "LIMIT",
    "price": 75000,
    "quantity": 10,
    "filledQuantity": 10,
    "averagePrice": 74950,
    "status": "FILLED",
    "createdAt": "2026-01-18T10:30:00Z",
    "updatedAt": "2026-01-18T10:31:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 2.7. 주문 취소

```http
DELETE /api/v1/trading/orders/{orderId}
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "orderId": "ORD-20260118-001",
    "status": "CANCELLED",
    "cancelledAt": "2026-01-18T10:32:00Z"
  },
  "timestamp": "2026-01-18T10:32:00Z"
}
```

### 2.8. 체결 내역 조회

```http
GET /api/v1/trading/executions?page=0&size=20
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "executionId": "EXE-20260118-001",
        "orderId": "ORD-20260118-001",
        "symbol": "005930",
        "name": "삼성전자",
        "type": "BUY",
        "price": 74950,
        "quantity": 10,
        "executedAt": "2026-01-18T10:31:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 50,
    "totalPages": 3
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

---

## 3. 자산 (Asset)

### 3.1. 전체 자산 조회

```http
GET /api/v1/assets
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalAsset": 10500000,     # 총 자산
    "cash": 2000000,            # 예수금
    "stockValue": 8500000,      # 주식 평가액
    "profitLoss": 500000,       # 평가 손익
    "profitLossRate": 5.0,      # 수익률
    "updatedAt": "2026-01-18T10:30:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 3.2. 보유 주식 조회

```http
GET /api/v1/assets/stocks
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "symbol": "005930",
      "name": "삼성전자",
      "quantity": 100,
      "averagePrice": 70000,
      "currentPrice": 75000,
      "valuationAmount": 7500000,
      "profitLoss": 500000,
      "profitLossRate": 7.14
    },
    {
      "symbol": "AAPL",
      "name": "Apple Inc.",
      "quantity": 5,
      "averagePrice": 180.00,
      "currentPrice": 185.00,
      "valuationAmount": 925.00,
      "profitLoss": 25.00,
      "profitLossRate": 2.78,
      "currency": "USD"
    }
  ],
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 3.3. 예수금 조회

```http
GET /api/v1/assets/cash
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "totalCash": 2000000,
    "availableCash": 1800000,    # 주문 가능 금액
    "withdrawableCash": 1500000, # 출금 가능 금액
    "currency": "KRW"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 3.4. 수익률 조회

```http
GET /api/v1/assets/performance?period=1M
Authorization: Bearer {access_token}

# Query Parameters
# - period: 1D | 1W | 1M | 3M | 6M | 1Y | ALL
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "period": "1M",
    "startValue": 10000000,
    "endValue": 10500000,
    "profitLoss": 500000,
    "profitLossRate": 5.0,
    "dailyReturns": [
      {"date": "2026-01-01", "value": 10000000, "return": 0.0},
      {"date": "2026-01-02", "value": 10050000, "return": 0.5},
      {"date": "2026-01-03", "value": 10100000, "return": 1.0}
    ]
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

---

## 4. 뉴스 (News)

### 4.1. 뉴스 목록 조회

```http
GET /api/v1/news?page=0&size=20
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "삼성전자, 신제품 발표",
        "summary": "삼성전자가 신제품을 발표했습니다...",
        "source": "한국경제",
        "url": "https://example.com/news/1",
        "publishedAt": "2026-01-18T09:00:00Z",
        "relatedSymbols": ["005930"]
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 4.2. 뉴스 상세 조회

```http
GET /api/v1/news/{id}
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "삼성전자, 신제품 발표",
    "content": "전체 뉴스 내용...",
    "summary": "요약...",
    "source": "한국경제",
    "url": "https://example.com/news/1",
    "publishedAt": "2026-01-18T09:00:00Z",
    "relatedSymbols": ["005930"],
    "sentiment": "POSITIVE"  # POSITIVE | NEGATIVE | NEUTRAL
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 4.3. 종목별 뉴스 조회

```http
GET /api/v1/news/symbol/{symbol}?page=0&size=10
Authorization: Bearer {access_token}
```

**응답 형식:** 4.1과 동일

---

## 5. AI (AI)

### 5.1. 종목 분석

```http
POST /api/v1/ai/analyze
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "symbol": "005930"
}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "symbol": "005930",
    "name": "삼성전자",
    "signal": "BUY",           # BUY | SELL | HOLD
    "confidence": 0.85,        # 0.0 ~ 1.0
    "targetPrice": 80000,
    "currentPrice": 75000,
    "expectedReturn": 6.67,
    "risk": "MEDIUM",          # LOW | MEDIUM | HIGH
    "reasons": [
      "실적 개선 기대",
      "기술적 반등 신호",
      "업종 모멘텀 강화"
    ],
    "analyzedAt": "2026-01-18T10:30:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 5.2. 추천 종목 조회

```http
GET /api/v1/ai/recommendations
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "symbol": "005930",
      "name": "삼성전자",
      "signal": "BUY",
      "confidence": 0.85,
      "targetPrice": 80000,
      "expectedReturn": 6.67,
      "risk": "MEDIUM"
    },
    {
      "symbol": "035720",
      "name": "카카오",
      "signal": "BUY",
      "confidence": 0.78,
      "targetPrice": 55000,
      "expectedReturn": 10.0,
      "risk": "HIGH"
    }
  ],
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 5.3. 자동매매 활성화

```http
POST /api/v1/ai/auto-trading/enable
Authorization: Bearer {access_token}
Content-Type: application/json

{
  "strategy": "CONSERVATIVE",  # CONSERVATIVE | BALANCED | AGGRESSIVE
  "maxAmount": 5000000,        # 최대 투자 금액
  "stopLoss": -5.0,            # 손절 비율 (%)
  "takeProfit": 10.0           # 익절 비율 (%)
}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "autoTradingId": "AT-001",
    "status": "ACTIVE",
    "strategy": "CONSERVATIVE",
    "maxAmount": 5000000,
    "stopLoss": -5.0,
    "takeProfit": 10.0,
    "activatedAt": "2026-01-18T10:30:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```

### 5.4. 자동매매 비활성화

```http
POST /api/v1/ai/auto-trading/disable
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "autoTradingId": "AT-001",
    "status": "INACTIVE",
    "deactivatedAt": "2026-01-18T10:35:00Z"
  },
  "timestamp": "2026-01-18T10:35:00Z"
}
```

### 5.5. 자동매매 상태 조회

```http
GET /api/v1/ai/auto-trading/status
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "autoTradingId": "AT-001",
    "status": "ACTIVE",
    "strategy": "CONSERVATIVE",
    "maxAmount": 5000000,
    "usedAmount": 1500000,
    "profitLoss": 75000,
    "profitLossRate": 5.0,
    "totalTrades": 10,
    "successRate": 70.0,
    "activatedAt": "2026-01-18T10:30:00Z"
  },
  "timestamp": "2026-01-18T10:30:00Z"
}
```
