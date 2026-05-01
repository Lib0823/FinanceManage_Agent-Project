# AI Agent - Project Structure

> 상태: **MVP 개발 중** (분석 워크플로우 & AI 판단 파이프라인)

## 개발 범위 (현재 MVP)

### 핵심 기능
1. **일일 파이프라인 (APScheduler @ 08:50 KST 평일)**
   - 코스피 100 종목 스캔 → StandardScaler 스코어링 → 상위 30개 선정
   - KIS API 데이터 수집 (asyncio 병렬 처리, rate limit: 5req/sec)
   - DART API 재무지표 조회
   - 뉴스 크롤링 (RSS + 네이버금융)

2. **3-Way 분석 시스템**
   - **정량 분석**: KIS 4개 피처 + DART 3개 재무지표
   - **감성 분석**: KR-FinBERT (시장 전반 + 종목별)
   - **시계열 예측**: Prophet (120거래일 → D+1~D+5)

3. **차트 생성 & 서빙**
   - matplotlib 4종 PNG 생성 (매일 덮어쓰기)
   - `/static/charts/` 정적 파일 서빙

4. **Gemini AI 판단**
   - 11개 피처 통합 분석
   - 매수/매도 TOP3 선정 + 판단 근거 생성

5. **매매 실행 요청**
   - `is_active=true` 시 Spring Boot로 POST 요청

## 디렉토리 구조

```
ai-agent/
├── app/
│   ├── main.py               # FastAPI 앱 진입점 + StaticFiles 설정
│   ├── config.py             # 환경 변수, KIS/DART/Gemini API 키
│   │
│   ├── scheduler/            # 스케줄링
│   │   ├── __init__.py
│   │   ├── pipeline.py       # APScheduler 설정 및 파이프라인 실행
│   │   └── jobs.py           # 일일 분석 Job 정의
│   │
│   ├── api/                  # FastAPI 라우터
│   │   ├── __init__.py
│   │   ├── analysis.py       # AI 분석 결과 조회 API (Vue3용)
│   │   └── health.py         # 헬스 체크
│   │
│   ├── services/             # 비즈니스 로직
│   │   ├── __init__.py
│   │   ├── stock_filter.py   # 종목 필터링 (StandardScaler)
│   │   ├── data_collector.py # KIS/DART/뉴스 수집
│   │   ├── quant_analyzer.py # 정량 피처 계산
│   │   ├── sentiment_analyzer.py # KR-FinBERT 감성 분석
│   │   ├── timeseries_forecaster.py # Prophet 예측
│   │   ├── chart_generator.py # matplotlib 차트 생성
│   │   ├── ai_judge.py       # Gemini API 판단
│   │   └── trade_executor.py # Spring Boot 매매 요청
│   │
│   ├── clients/              # 외부 API 클라이언트
│   │   ├── __init__.py
│   │   ├── kis_client.py     # 한국투자증권 API
│   │   ├── dart_client.py    # DART OpenAPI
│   │   ├── gemini_client.py  # Gemini AI API
│   │   └── spring_client.py  # Spring Boot 연동
│   │
│   ├── db/                   # 데이터베이스
│   │   ├── __init__.py
│   │   ├── database.py       # SQLAlchemy AsyncEngine
│   │   ├── models.py         # ORM 모델 (8개 테이블)
│   │   └── repositories.py   # 데이터 접근 레이어
│   │
│   ├── schemas/              # Pydantic 스키마
│   │   ├── __init__.py
│   │   ├── analysis.py       # 분석 결과 응답
│   │   ├── stock.py          # 종목 관련
│   │   └── signal.py         # AI 판단 결과
│   │
│   └── utils/                # 유틸리티
│       ├── __init__.py
│       ├── preprocessor.py   # 데이터 전처리
│       └── logger.py         # 로깅 설정
│
├── static/                   # 정적 파일
│   └── charts/               # matplotlib 차트 PNG
│       ├── heatmap_today.png
│       ├── quant_features_today.png
│       ├── sentiment_today.png
│       └── prophet_forecast_today.png
│
├── tests/                    # 테스트
│   ├── unit/
│   └── integration/
│
├── requirements.txt          # pip 의존성
├── Dockerfile
└── README.md
```

## 모듈별 역할

| 모듈 | 책임 |
|------|------|
| scheduler | APScheduler 설정, 평일 08:50 자동 실행 |
| api | Vue3 프론트엔드용 분석 결과 조회 API |
| services | 파이프라인 핵심 로직 (필터링, 수집, 분석, 판단) |
| clients | 외부 API 통신 (KIS, DART, Gemini, Spring Boot) |
| db | PostgreSQL 연동 (8개 테이블) |
| schemas | API 요청/응답 데이터 검증 |
| utils | 공통 유틸리티 (전처리, 로깅) |

## FastAPI 엔드포인트 (Vue3 연동)

| Method | Path | 설명 | 사용 탭 |
|--------|------|------|---------|
| GET | `/api/ai/today` | 시장 전반 요약 (stat-row 수치) | 종합분석 |
| GET | `/api/ai/top3/buy` | Gemini 매수 TOP3 + 판단 근거 | 종합분석 |
| GET | `/api/ai/top3/sell` | Gemini 매도 TOP3 + 판단 근거 | 종합분석 |
| GET | `/api/ai/top3/metrics` | TOP3 종목 DART 재무지표 | 정량분석 |
| GET | `/api/ai/quant/summary` | 정량 피처 요약 + 수급 TOP3 | 정량분석 |
| GET | `/api/ai/sentiment/summary` | 감성 분포 집계 | 감성분석 |
| GET | `/api/ai/sentiment/top3` | 감성 상위/하위 3종목 | 감성분석 |
| GET | `/api/ai/timeseries/summary` | 시계열 추세 집계 | 시계열 |
| GET | `/api/ai/timeseries/uncertainty` | 불확실성 상위 3종목 | 시계열 |
| GET | `/charts/{파일명}` | matplotlib 차트 PNG 서빙 | 전체 |
| GET | `/health` | 헬스 체크 | - |

## 통신 흐름

```
[APScheduler 08:50] → [파이프라인 실행]
                         ↓
                    [1. 종목 필터링]
              코스피 100 → 스코어링 → 상위 30개
                         ↓
                    [2. 데이터 수집]
        KIS API (asyncio) + DART + 뉴스 크롤링
                         ↓
                    [3. 3-Way 분석]
        정량(KIS+DART) + 감성(FinBERT) + 시계열(Prophet)
                         ↓
                    [4. 차트 생성]
                matplotlib → /static/charts/
                         ↓
                    [5. AI 판단]
              Gemini → 매수/매도 TOP3
                         ↓
                    [6. DB 저장]
               PostgreSQL (8개 테이블)
                         ↓
        [7. 매매 실행 요청 (is_active=true)]
                         ↓
              POST → Spring Boot → KIS API
```

## DB 테이블 (8개)

| 테이블명 | 설명 |
|---------|------|
| `users` | admin 계정 |
| `user_trade_config` | 투자 설정 (is_active 플래그) |
| `stock_filter_score` | 코스피 100 스코어링 결과 |
| `stock_financial` | DART 재무지표 (분기별) |
| `news_analysis` | 감성 분석 결과 |
| `prophet_forecast` | Prophet D+1~D+5 예측 |
| `ai_trade_decision` | Gemini 판단 결과 |
| `trade_history` | 체결 이력 (Spring Boot 저장) |
