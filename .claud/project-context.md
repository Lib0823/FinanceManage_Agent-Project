# Asset Manage Agent Project

## 프로젝트 개요

AI 기반 주식 자동 투자 및 포트폴리오 관리 모바일 웹 애플리케이션

**핵심 가치:**
- 국내/미국 주식 통합 관리
- 실시간 시세 조회 (한국투자증권 WebSocket)
- AI 기반 자동 매매 + 수동 거래 병행
- 투자 내역 추적 및 분석

**개발 정보:**
- 개발자: 인범
- 시작일: 2026-01-18
- 저장소: https://github.com/inbeom/AssetManage_Agent-Project

## 기술 스택

### Frontend (web-app)
- **Framework**: Vue 3 (Composition API)
- **Build Tool**: Vite 6.x
- **HTTP Client**: Axios 1.6.x
- **UI Library**: Vant 4.9.x (모바일 최적화)
- **Router**: Vue Router 4.x
- **State Management**: Pinia (필요시)

**색상 시스템:**
```css
--primary: #4318FF;    /* 보라 */
--secondary: #05CD99;  /* 민트 */
--accent: #FFB547;     /* 주황 */
```

### Backend (api-server)
- **Language**: Java 21
- **Framework**: Spring Boot 4.1.0
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA
- **Database**: PostgreSQL 16
- **Cache**: Redis 7
- **Search/Log**: Elasticsearch 8.16
- **API Docs**: Swagger (SpringDoc OpenAPI)
- **Test**: JUnit 5 + Mockito

### AI Agent (ai-agent)
- **Language**: Python 3.11
- **Framework**: FastAPI
- **Database**: PostgreSQL (shared)
- **Message Queue**: PGMQ (PostgreSQL-based)
- **ML/AI**: TensorFlow/PyTorch (예정)

### Infrastructure
- **Containerization**: Docker + Docker Compose
- **CI/CD**: GitHub Actions
- **Monitoring**: Prometheus + Grafana
- **OS**: Ubuntu (컨테이너)

## 주요 기능

### 1단계 (주식) - MVP
- [x] 회원 인증 (로그인/회원가입)
- [ ] 국내 주식 (KOSPI/KOSDAQ)
  - 실시간 시세 조회
  - 매수/매도 주문
  - 호가창 조회
- [ ] 미국 주식 (NYSE/NASDAQ)
  - 실시간 시세 조회 (환율 적용)
  - 매수/매도 주문
- [ ] 포트폴리오 관리
  - 보유 종목 조회
  - 수익률 계산
  - 평가 손익
- [ ] 거래 내역 관리
  - 체결 내역
  - 미체결 주문
  - 거래 통계

### 2단계
- [ ] 뉴스 알림
  - 주요 뉴스 피드
  - 종목별 뉴스
  - 실시간 공시
- [ ] AI 투자 지원 (기본)
  - 매매 추천
  - 리스크 분석

### 3단계
- [ ] AI 자동매매
  - 자동 매매 (사용자 설정)
  - 전략 백테스팅
- [ ] 알림 기능
- [ ] 모바일 최적화

### 4단계 (코인)
- [ ] 코인 거래 (업비트)
- [ ] 포트폴리오 통합 관리

## 외부 API 연동

### 한국투자증권 OpenAPI
- **REST API**: 주문, 잔고, 계좌 조회
- **WebSocket**: 실시간 시세 (국내/해외)
- 문서: https://apiportal.koreainvestment.com/

### 업비트 OpenAPI (2차 개발)
- 코인 거래 및 시세
- 문서: https://docs.upbit.com/

## 프로젝트 구조

### Monorepo 구조
```
AssetManage_Agent-Project/
├── api-server/              # Spring Boot Backend
│   └── src/main/java/com/inbeom/api/
│       ├── auth/            # 인증/인가
│       ├── trading/         # 주식 거래
│       ├── asset/           # 자산 관리
│       ├── news/            # 뉴스
│       ├── ai/              # AI 연동
│       └── common/          # 공통 (config, exception, dto)
│
├── web-app/                 # Vue 3 Frontend
│   └── src/
│       ├── features/        # 기능별 모듈
│       │   ├── auth/
│       │   ├── trading/
│       │   ├── asset/
│       │   └── news/
│       └── shared/          # 공통
│
├── ai-agent/                # Python AI Agent
│   └── app/
│       ├── trading/
│       ├── analysis/
│       └── recommendation/
│
├── .claud/                  # Claude Code 설정
├── docker-compose.yml
└── README.md
```

## 환경 설정

### 환경 구분
```
local     # 로컬 개발
dev       # 개발 서버 (Docker Compose)
prod      # 프로덕션 (향후)
```

### 주요 환경 변수

**Backend (.env):**
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/assetdb
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-secret-key
KIS_API_KEY=한국투자증권-API-KEY
KIS_API_SECRET=한국투자증권-API-SECRET
```

**Frontend (.env.local):**
```bash
VITE_API_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws
```

## 보안 고려사항

1. **JWT 인증**
   - Access Token: 1시간
   - Refresh Token: 7일
   - HttpOnly Cookie 저장

2. **API Rate Limiting**
   - 일반 API: 100 req/min
   - 주문 API: 10 req/min

3. **민감 정보 암호화**
   - 비밀번호: BCrypt
   - API Key: AES-256
   - 개인정보: 암호화 저장

## 참고 문서

- [아키텍처 설계](./architecture.md)
- [API 명세](./api-spec.md)
- [코딩 컨벤션](./coding-conventions.md)
- [데이터베이스 설계](./database-schema.md)

## 외부 문서

- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [Vue 3 공식 문서](https://vuejs.org/)
- [한국투자증권 OpenAPI](https://apiportal.koreainvestment.com/)
