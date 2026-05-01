# AI Agent - Tech Stack

> 상태: **MVP 개발 중** (분석 워크플로우 & AI 판단 파이프라인)

## 핵심 기술

| 항목 | 기술 | 버전 |
|------|------|------|
| Language | Python | 3.11+ |
| Framework | FastAPI | 0.100+ |
| Package Manager | pip | - |
| Scheduler | APScheduler | 3.10+ |

## 주요 라이브러리

### 웹 프레임워크
| 라이브러리 | 용도 |
|-----------|------|
| fastapi | REST API 프레임워크 |
| uvicorn | ASGI 서버 |
| pydantic | 데이터 검증 |
| sqlalchemy[asyncio] | 비동기 ORM |
| asyncpg | PostgreSQL 비동기 드라이버 |
| httpx | 비동기 HTTP 클라이언트 |
| apscheduler | 스케줄링 (평일 08:50 자동 실행) |

### 데이터 처리 & ML
| 라이브러리 | 용도 |
|-----------|------|
| pandas | 데이터 처리 및 분석 |
| numpy | 수치 연산 |
| scikit-learn | StandardScaler (종목 스코어링) |
| prophet | 시계열 예측 (Meta) |
| transformers | KR-FinBERT (감성 분석) |
| torch | PyTorch (transformers 의존성) |

### 시각화
| 라이브러리 | 용도 |
|-----------|------|
| matplotlib | 차트 생성 (4종 PNG) |
| seaborn | 히트맵 스타일링 (선택) |

### 크롤링
| 라이브러리 | 용도 |
|-----------|------|
| beautifulsoup4 | HTML 파싱 |
| feedparser | RSS 파싱 |
| lxml | XML/HTML 파서 |
| requests | 동기 HTTP 요청 (크롤링용) |

### 외부 API
| API | 통신 방식 | 용도 |
|-----|-----------|------|
| 한국투자증권 (KIS) | REST (asyncio) | 시세, 일봉, 수급 조회 |
| DART | REST | 재무지표 조회 |
| Gemini | REST | AI 판단 (하루 1회) |
| Spring Boot | REST | 매매 요청 POST, 보유 종목 GET |

## 데이터베이스

| DB | 용도 |
|----|------|
| PostgreSQL 16 | 메인 DB (api-server와 공유) |

**테이블 (8개)**:
- `users`, `user_trade_config`
- `stock_filter_score`, `stock_financial`
- `news_analysis`, `prophet_forecast`
- `ai_trade_decision`, `trade_history`

## 환경 변수

```bash
# Database
DATABASE_URL=postgresql+asyncpg://admin:password@localhost:5432/assetdb

# Spring Boot API
SPRING_API_URL=http://localhost:8080

# KIS API (한국투자증권)
KIS_API_KEY=
KIS_API_SECRET=
KIS_ACCOUNT_NUMBER=
KIS_BASE_URL=https://openapi.koreainvestment.com:9443

# DART API
DART_API_KEY=

# Gemini AI
GEMINI_API_KEY=

# 스케줄러 설정
SCHEDULER_ENABLED=true
SCHEDULER_CRON=0 50 8 * * mon-fri  # 평일 08:50

# 차트 설정
CHART_OUTPUT_DIR=./static/charts
MATPLOTLIB_BACKEND=Agg

# 파이프라인 설정
KOSPI_TOP_N=100          # 코스피 상위 종목 수
ANALYSIS_TARGET_N=30     # 분석 대상 종목 수
KIS_RATE_LIMIT=5         # KIS API 초당 요청 수
```

## 코딩 스타일

| 항목 | 도구/규칙 |
|------|-----------|
| 포맷터 | Black (4칸 들여쓰기) |
| 린터 | Ruff |
| 타입 힌트 | mypy |
| 독스트링 | Google style |
| 네이밍 | snake_case |

## 주요 의존성 (requirements.txt)

```txt
# Web Framework
fastapi>=0.100.0
uvicorn[standard]>=0.22.0
python-multipart
pydantic>=2.0.0

# Database
sqlalchemy[asyncio]>=2.0.0
asyncpg>=0.28.0
alembic>=1.11.0

# HTTP Client
httpx>=0.24.0
requests>=2.31.0

# Scheduler
apscheduler>=3.10.0

# Data Processing
pandas>=2.0.0
numpy>=1.24.0

# ML/AI
scikit-learn>=1.3.0
prophet>=1.1.4
transformers>=4.30.0
torch>=2.0.0

# Visualization
matplotlib>=3.7.0
seaborn>=0.12.0

# Scraping
beautifulsoup4>=4.12.0
feedparser>=6.0.10
lxml>=4.9.0

# Utilities
python-dotenv>=1.0.0
pydantic-settings>=2.0.0
```

## 폰트 요구사항

**matplotlib 한글 렌더링**:
- 서버 환경에 **NanumGothic** 폰트 필수 설치
- Docker 이미지에 포함 필요

```bash
# Ubuntu/Debian
apt-get install fonts-nanum

# macOS
brew install font-nanum-gothic
```

## API Rate Limit 관리

| API | 제한 | 대응 |
|-----|------|------|
| KIS | 5 req/sec | asyncio.sleep(0.2) |
| DART | 1000 req/day | 분기별 배치 수집 |
| Gemini | 무료티어 | 하루 1회 호출 |
