# Architecture

## 시스템 아키텍처

### 전체 구조도

```
┌─────────────────────────────────────────────────────────┐
│                     Client (Mobile Web)                  │
│                      Vue 3 + Vant                        │
│                   실시간 시세 / 주문                      │
└────────────────────┬────────────────────────────────────┘
                     │ HTTPS/REST API
                     │ WebSocket (실시간 시세)
┌────────────────────▼────────────────────────────────────┐
│                   API Gateway (Nginx)                    │
│                   SSL/TLS, CORS, 라우팅                  │
└────────────────────┬────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
┌───────▼─────┐ ┌───▼─────┐ ┌───▼──────┐
│  API Server │ │AI Agent │ │ External │
│ Spring Boot │ │ FastAPI │ │   APIs   │
│             │ │         │ │          │
│ - REST API  │ │- 분석   │ │- 한투    │
│ - WebSocket │ │- 추천   │ │- 업비트  │
│ - Security  │ │- 자동화 │ │          │
└──────┬──────┘ └────┬────┘ └────┬─────┘
       │             │           │
       ├─────────────┼───────────┤
       │             │           │
┌──────▼──────┬──────▼────┬──────▼──────┐
│ PostgreSQL  │   Redis   │Elasticsearch│
│  (메인 DB)  │  (캐시)   │  (로그/검색) │
└─────────────┴───────────┴─────────────┘
```

## 컨테이너 구성 (Docker Compose)

### 서비스 맵

```
┌──────────────────────────────────────────────────┐
│                    Docker Host                    │
├──────────────────────────────────────────────────┤
│                                                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │ web-app  │  │api-server│  │ ai-agent │      │
│  │   :3000  │  │   :8080  │  │   :5000  │      │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘      │
│       │             │              │             │
│  ┌────┴─────────────┴──────────────┴─────┐      │
│  │            asset-network              │      │
│  └────┬──────────┬────────────┬──────────┘      │
│       │          │            │                  │
│  ┌────▼────┐ ┌──▼────┐ ┌─────▼──────┐          │
│  │postgres │ │ redis │ │elasticsearch│          │
│  │  :5432  │ │ :6379 │ │    :9200    │          │
│  └─────────┘ └───────┘ └─────────────┘          │
│                                                   │
│  ┌──────────┐  ┌───────────┐                    │
│  │prometheus│  │  grafana  │                    │
│  │   :9090  │  │   :3001   │                    │
│  └──────────┘  └───────────┘                    │
└──────────────────────────────────────────────────┘
```

### 포트 매핑

| 서비스 | 컨테이너 포트 | 호스트 포트 | 용도 |
|--------|--------------|------------|------|
| web-app | 3000 | 3000 | Vue 프론트엔드 |
| api-server | 8080 | 8080 | Spring Boot API |
| ai-agent | 5000 | 5000 | Python AI 서비스 |
| postgres | 5432 | 5432 | PostgreSQL DB |
| redis | 6379 | 6379 | Redis 캐시 |
| elasticsearch | 9200 | 9200 | 로그/검색 |
| prometheus | 9090 | 9090 | 메트릭 수집 |
| grafana | 3001 | 3000 | 모니터링 대시보드 |

## 통신 흐름

### 1. 사용자 로그인 플로우

```
[User] → [Vue App] → [API Server]
                          ↓
                     [PostgreSQL]
                          ↓
                     JWT 토큰 발급
                          ↓
                     [Vue App] ← Access/Refresh Token
                          ↓
                     로컬 저장 (HttpOnly Cookie)
```

### 2. 실시간 시세 조회 플로우

```
[Vue App] → WebSocket 연결 → [API Server]
                                   ↓
                              종목 구독 요청
                                   ↓
                          [한국투자증권 WebSocket]
                                   ↓
                              실시간 시세 수신
                                   ↓
                            [Redis] 캐싱 (1초)
                                   ↓
                          [Vue App] ← 실시간 전송
```

### 3. 주문 실행 플로우

```
[Vue App] → POST /api/v1/trading/orders → [API Server]
                                               ↓
                                          주문 검증
                                               ↓
                                    [한국투자증권 REST API]
                                               ↓
                                          주문 접수
                                               ↓
                                    [PostgreSQL] 저장
                                               ↓
                                    [Elasticsearch] 로그
                                               ↓
                                    [Vue App] ← 주문 결과
```

### 4. AI 자동매매 플로우

```
[Scheduler] → [AI Agent]
                  ↓
            시장 데이터 분석
                  ↓
            [PostgreSQL] 조회
                  ↓
            매매 신호 생성
                  ↓
            [PGMQ] 메시지 전송
                  ↓
       [API Server] ← 메시지 수신
                  ↓
            주문 실행
                  ↓
       [한국투자증권 API]
```

## 데이터 흐름

### 캐싱 전략

**Redis 캐싱:**
```
1. 실시간 시세
   - Key: stock:{symbol}:price
   - TTL: 1초
   - 목적: WebSocket 부하 감소

2. 사용자 세션
   - Key: session:{userId}
   - TTL: 1시간
   - 목적: DB 조회 감소

3. 뉴스 목록
   - Key: news:list:{page}
   - TTL: 5분
   - 목적: 외부 API 호출 감소

4. 호가 정보
   - Key: stock:{symbol}:orderbook
   - TTL: 1초
   - 목적: 실시간 업데이트
```

### 로그 저장

**Elasticsearch:**
```
1. API 요청 로그
   - Index: api-logs-{yyyy.MM.dd}
   - Fields: timestamp, method, path, status, duration

2. 거래 로그
   - Index: trading-logs-{yyyy.MM.dd}
   - Fields: timestamp, userId, symbol, type, price, quantity

3. 에러 로그
   - Index: error-logs-{yyyy.MM.dd}
   - Fields: timestamp, level, message, stackTrace

4. AI 분석 로그
   - Index: ai-logs-{yyyy.MM.dd}
   - Fields: timestamp, symbol, signal, confidence
```

## 확장성 고려사항

### MSA 전환 준비

**현재 (Monolith with Domain):**
```
api-server/
├── auth/
├── trading/
├── asset/
└── news/
```

**향후 (MSA):**
```
auth-service:8081      # 인증/인가
trading-service:8082   # 주식 거래
asset-service:8083     # 자산 관리
news-service:8084      # 뉴스
ai-service:8085        # AI 분석
```

### 메시지 큐 도입 (향후)

**현재:** PGMQ (PostgreSQL 기반)
**향후:** Kafka or RabbitMQ

```
[Trading Service] → [Kafka] → [AI Service]
                              [Notification Service]
                              [Analytics Service]
```

## 성능 최적화

### 1. 데이터베이스 최적화

**인덱싱 전략:**
```sql
-- 사용자 조회
CREATE INDEX idx_user_email ON users(email);

-- 주문 조회 (사용자별, 날짜별)
CREATE INDEX idx_order_user_created ON orders(user_id, created_at DESC);

-- 체결 조회
CREATE INDEX idx_execution_order ON executions(order_id);

-- 종목 검색
CREATE INDEX idx_stock_symbol ON stocks(symbol);
```

**커넥션 풀:**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

### 2. API 성능

**Rate Limiting:**
```java
// 일반 API: 100 req/min
@RateLimiter(name = "general", fallbackMethod = "rateLimitFallback")

// 주문 API: 10 req/min
@RateLimiter(name = "trading", fallbackMethod = "rateLimitFallback")
```

**비동기 처리:**
```java
@Async
public CompletableFuture<List<News>> fetchNews() {
  // 외부 API 호출
}
```

### 3. WebSocket 최적화

**구독 관리:**
```java
// 사용자당 최대 10개 종목 구독
private static final int MAX_SUBSCRIPTIONS = 10;

// 압축 전송
websocket.compress = true

// Heartbeat (30초)
websocket.heartbeat.interval = 30000
```

## 모니터링

### Prometheus 메트릭

```yaml
# JVM
- jvm.memory.used
- jvm.memory.max
- jvm.threads.live

# API
- http.server.requests.count
- http.server.requests.duration

# Database
- hikaricp.connections.active
- hikaricp.connections.idle

# Redis
- redis.commands.count
- redis.commands.duration

# Custom
- trading.orders.count
- trading.orders.success.rate
- ai.signals.count
```

### Grafana 대시보드

**패널 구성:**
1. 시스템 상태 (CPU, 메모리, 디스크)
2. API 응답 시간
3. 에러율
4. 거래량 (시간당)
5. 활성 사용자 수
6. DB 커넥션 사용률

## 보안 아키텍처

### 계층별 보안

```
[Client]
  ↓ HTTPS (SSL/TLS)
[Nginx]
  ↓ JWT 검증
[API Server]
  ↓ Spring Security
[Database]
  ↓ 암호화 저장
```

### JWT 흐름

```
1. 로그인 성공
   → Access Token (1시간)
   → Refresh Token (7일)

2. API 요청
   → Authorization: Bearer {accessToken}
   → 검증 및 권한 확인

3. Token 만료
   → Refresh Token으로 갱신
   → 새 Access Token 발급

4. Refresh Token 만료
   → 재로그인 필요
```

## 장애 대응

### 서킷 브레이커 (Resilience4j)

```java
@CircuitBreaker(name = "kis-api", fallbackMethod = "fallbackMethod")
public StockPrice getStockPrice(String symbol) {
  // 한국투자증권 API 호출
}

// 장애 시 캐시된 데이터 반환
public StockPrice fallbackMethod(String symbol, Exception ex) {
  return redis.get("stock:" + symbol + ":price");
}
```

### 헬스 체크

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  health:
    redis:
      enabled: true
    db:
      enabled: true
    elasticsearch:
      enabled: true
```

**엔드포인트:**
```
GET /actuator/health
→ {"status": "UP", "components": {...}}

GET /actuator/health/redis
GET /actuator/health/db
```

## CI/CD 파이프라인

### GitHub Actions 플로우

```
1. Push to main
   ↓
2. Build & Test
   - Backend: ./gradlew build test
   - Frontend: npm run build
   - AI: pytest
   ↓
3. Docker Build
   - api-server:latest
   - web-app:latest
   - ai-agent:latest
   ↓
4. Deploy
   - docker-compose down
   - docker-compose up -d --build
   ↓
5. Health Check
   - /actuator/health
```

## 백업 전략

### 데이터베이스

```bash
# 매일 자정 자동 백업
0 0 * * * docker exec postgres pg_dump -U admin assetdb > backup_$(date +%Y%m%d).sql

# 보관 정책
- 일별 백업: 7일
- 주별 백업: 4주
- 월별 백업: 12개월
```

### 로그

```bash
# Elasticsearch 스냅샷 (주 1회)
POST /_snapshot/my_backup/snapshot_1
{
  "indices": "api-logs-*,trading-logs-*",
  "ignore_unavailable": true
}
```
