# API Server - Project Structure

> 상태: **MVP 개발 중** (인증, 거래, 대시보드 API)

## 개발 범위 (현재 MVP)

### 핵심 기능
1. **인증 & 사용자 관리**
   - admin 단일 계정 로그인 (MVP, JWT 미사용)
   - 회원가입 (기본 정보 + 금융 정보)
   - 프로필 관리

2. **거래 API**
   - FastAPI 매매 요청 수신 → KIS 모의투자 API 호출
   - 주문 실행 (매수/매도)
   - 체결 내역 PostgreSQL 저장
   - 거래 내역 조회 (날짜 범위)

3. **자산 & 보유현황 API**
   - KIS API를 통한 보유 종목 조회
   - 자산 요약 (총 자산, 평가손익)
   - 종목별 수익률 계산

4. **대시보드 API**
   - 홈 화면용 시장 지수 (KOSPI, KOSDAQ)
   - 오늘 매매 내역 요약
   - 주요 뉴스

5. **종목 정보 API**
   - 기업 상세 정보
   - 재무제표 (DART)
   - 종목 검색

6. **AI 투자 설정 API**
   - 자동매매 ON/OFF (`user_trade_config.is_active`)
   - 투자 전략 설정 (주문 금액, 최대 보유 종목 수)

## 디렉토리 구조

```
api-server/
├── src/main/java/com/inbeom/api/
│   ├── auth/                 # 인증/인가 도메인
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── domain/           # User, RefreshToken
│   │   └── dto/
│   │
│   ├── trading/              # 주식 거래 도메인
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── domain/           # Order, Execution, Stock
│   │   ├── dto/
│   │   └── client/           # 한국투자증권 API 클라이언트
│   │
│   ├── asset/                # 자산 관리 도메인
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── domain/           # Account, Asset
│   │   └── dto/
│   │
│   ├── news/                 # 뉴스 도메인
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── domain/           # News
│   │   └── dto/
│   │
│   ├── ai/                   # AI 연동 도메인
│   │   ├── controller/
│   │   ├── service/
│   │   ├── domain/           # AiSignal, AutoTradingConfig
│   │   └── dto/
│   │
│   └── common/               # 공통 모듈
│       ├── config/           # Security, JPA, Redis, Swagger
│       ├── exception/        # GlobalExceptionHandler, ErrorCode
│       ├── dto/              # ApiResponse, PageResponse
│       └── util/
│
├── src/main/resources/
│   ├── application.yml
│   ├── application-local.yml
│   ├── application-dev.yml
│   └── db/migration/         # Flyway 마이그레이션
│
└── src/test/java/            # 테스트
```

## 도메인별 역할

| 도메인 | 책임 |
|--------|------|
| auth | 회원가입, 로그인, JWT 토큰 관리 |
| trading | 시세 조회, 주문 생성/취소, 체결 관리, WebSocket |
| asset | 계좌 관리, 보유 종목, 수익률 계산 |
| news | 뉴스 수집, 종목별 뉴스 조회 |
| ai | AI Agent 연동, 자동매매 설정 |
| common | 설정, 예외처리, 공통 DTO |

## 계층 구조

```
Controller → Service → Repository → Database
              ↓
           Domain (Entity)
              ↓
           DTO (Request/Response)
```

## 네이밍 규칙

| 대상 | 패턴 | 예시 |
|------|------|------|
| Controller | {Domain}Controller | AuthController |
| Service | {Domain}Service | OrderService |
| Repository | {Entity}Repository | UserRepository |
| Entity | 단수형 | User, Order |
| DTO | {Action}{Domain}Request/Response | LoginRequest |

## REST API 엔드포인트 (Vue3 연동)

### 1. 인증 API (`/api/auth`)
| Method | Path | 설명 | 화면 |
|--------|------|------|------|
| POST | `/api/auth/register` | 회원가입 | RegisterView |
| POST | `/api/auth/register/finance` | 금융정보 등록 | RegisterFinanceView |
| POST | `/api/auth/login` | 로그인 | LoginView |
| POST | `/api/auth/logout` | 로그아웃 | - |
| POST | `/api/auth/reset-password` | 비밀번호 재설정 | ResetPasswordView |
| POST | `/api/auth/verify-phone` | 휴대폰 인증 | RegisterView |
| GET | `/api/auth/check-duplicate/{id}` | 아이디 중복 확인 | RegisterView |

### 2. 사용자 API (`/api/user`)
| Method | Path | 설명 | 화면 |
|--------|------|------|------|
| GET | `/api/user/profile` | 프로필 조회 | ProfileView |
| PUT | `/api/user/profile` | 프로필 수정 | ProfileView |
| GET | `/api/user/settings` | 설정 조회 | SettingsView |
| PUT | `/api/user/settings` | 설정 수정 | SettingsView |

### 3. 자산 API (`/api/assets`)
| Method | Path | 설명 | 화면 |
|--------|------|------|------|
| GET | `/api/assets/summary` | 자산 요약 (총액, 손익) | HomeView, AssetsView |
| GET | `/api/assets/detail/{type}` | 자산 상세 (stocks/bonds/coins/cash) | AssetDetailView |
| GET | `/api/assets/stocks` | 보유 주식 목록 | AssetsView |
| GET | `/api/assets/holdings` | KIS API 실시간 보유 현황 | AssetsView |

### 4. 종목 API (`/api/stocks`)
| Method | Path | 설명 | 화면 |
|--------|------|------|------|
| GET | `/api/stocks` | 종목 목록 (페이징) | SearchView |
| GET | `/api/stocks/{symbol}` | 종목 상세 | CompanyDetailView |
| GET | `/api/stocks/{symbol}/price` | 실시간 시세 (KIS) | CompanyDetailView |
| GET | `/api/stocks/{symbol}/chart` | 차트 데이터 (period) | CompanyDetailView |
| GET | `/api/stocks/search?q={query}` | 종목 검색 | SearchView |

### 5. 기업 API (`/api/companies`)
| Method | Path | 설명 | 화면 |
|--------|------|------|------|
| GET | `/api/companies/{symbol}` | 기업 정보 | CompanyDetailView |
| GET | `/api/companies/{symbol}/financials` | 재무제표 (DART) | CompanyDetailView |
| GET | `/api/companies/{symbol}/disclosures` | 공시 정보 | CompanyDetailView |

### 6. 거래 API (`/api/trading`)
| Method | Path | 설명 | 화면 |
|--------|------|------|------|
| POST | `/api/trading/orders` | 주문 생성 (매수/매도) | TradingView |
| GET | `/api/trading/orders` | 주문 내역 | TradingView |
| GET | `/api/trading/orders/pending` | 미체결 주문 | TradingView |
| DELETE | `/api/trading/orders/{orderId}` | 주문 취소 | TradingView |
| GET | `/api/trading/history` | 거래 내역 (날짜 범위) | TransactionsView |
| POST | `/api/trading/execute` | FastAPI 매매 요청 수신 | (내부 API) |

### 7. 뉴스 API (`/api/news`)
| Method | Path | 설명 | 화면 |
|--------|------|------|------|
| GET | `/api/news` | 뉴스 목록 (페이징) | NewsView |
| GET | `/api/news/{id}` | 뉴스 상세 | NewsDetailView |
| GET | `/api/news/by-date?date={date}` | 날짜별 뉴스 | NewsView |
| GET | `/api/news/stock/{symbol}` | 종목별 뉴스 | CompanyDetailView |

### 8. 시장 API (`/api/market`)
| Method | Path | 설명 | 화면 |
|--------|------|------|------|
| GET | `/api/market/indices` | 주요 지수 (KOSPI, KOSDAQ) | HomeView |
| GET | `/api/market/exchange-rates` | 환율 | HomeView |
| GET | `/api/market/top-news` | 주요 뉴스 3건 | HomeView |

### 9. AI Bot API (`/api/bot`)
| Method | Path | 설명 | 화면 |
|--------|------|------|------|
| GET | `/api/bot/status` | 봇 상태 (활성/비활성) | BotView |
| POST | `/api/bot/toggle` | 자동매매 ON/OFF | BotView |
| PUT | `/api/bot/settings` | 투자 설정 (금액, 최대 종목) | BotView |
| GET | `/api/bot/settings` | 투자 설정 조회 | BotView |

### 10. KIS API Client (내부)
- 실시간 시세 조회
- 보유 종목 조회
- 주문 실행 (매수/매도)
- 주문 취소
- 체결 내역 조회
