
# AI 주식 자동매매 시스템 — 프로젝트 컨텍스트

> **기말 프로젝트 정의서 v11 | 최종 확정본**  
> AI·SW대학원 이인범 / A74066  
> 브랜치: `ai-trading-pipeline` | 레포: `FinanceManageAgent` (모노레포)

---

## 1. 프로젝트 개요

기존 **FinanceManageAgent Vue3 앱** 기반으로, Python 파이프라인이 매일 코스피 100개 종목을 스캔·필터링하여 **상위 30개 종목**을 선정하고, **3가지 분석**(정량 피처 계산 / 감성분석 / 시계열 예측)을 수행한 뒤 **생성형 AI(Gemini)**가 매수/매도 TOP3를 판단하여 **KIS 모의투자 API**로 자동 주문을 실행하는 AI 기반 주식 자동매매 시스템.

### 핵심 설계 원칙

- 단일 admin 계정 운영 (회원가입/JWT 불필요, MVP 범위)
- KIS API 키 및 코스피 100 종목 코드 **하드코딩** (설정 UI 불필요)
- 분석 대상: 코스피 시가총액 상위 100개 → 매일 스코어링으로 상위 30개 확정
- 보유 종목은 매도 검토를 위해 최종 30개에 **무조건 포함**
- FastAPI가 matplotlib 차트 이미지 직접 서빙 (`/charts/` 정적 파일)
- Vue3 AI 분석 페이지(4탭)에서 시각화 결과 및 Gemini 판단 제공

---

## 2. 모노레포 디렉토리 구조

```
FinanceManageAgent/
  ├── /frontend          # Vue3 (FinanceManageAgent 기존 앱)
  ├── /collector         # Python FastAPI — 수집/분석/AI판단/차트
  ├── /api               # SpringBoot — 조회API / KIS 매매
  └── docker-compose.yml
```

---

## 3. 컨테이너 구성 (docker-compose)

| 서비스 | 역할 | 포트 |
|--------|------|------|
| `python-api` | FastAPI — 종목 스캔/필터링/분석/AI판단/차트 서빙 | 8000 |
| `spring-api` | SpringBoot — 조회 REST API / KIS 모의투자 매매 | 8080 |
| `frontend` | Vue3 (Nginx) | 3000 |
| `postgres` | 전체 데이터 저장 | 5432 |

---

## 4. 서비스 간 통신 흐름

```
Vue3 → SpringBoot (8080)   : 대시보드, 거래 내역, 설정, 로그인
Vue3 → FastAPI (8000)      : AI 분석 결과, matplotlib 차트 이미지
FastAPI → SpringBoot       : 매매 실행 요청 POST / 보유 종목 조회 GET
FastAPI → KIS API          : 코스피 100 시세 + 분석 30종목 시세/수급/일봉
FastAPI → DART API         : 분기별 재무데이터 수집
FastAPI → PostgreSQL       : 분석 결과 저장 (감성/예측/AI판단/거래내역)
SpringBoot → PostgreSQL    : 거래 내역 저장/조회
SpringBoot → KIS API       : 모의투자 주문 실행 / 보유현황 조회
```

---

## 5. 서비스 역할 분리

### FastAPI (Python) — 파이프라인 전용

- **스케줄링**: APScheduler 매일 08:50 자동 실행 (평일 cron, 코드 기반)
- **종목 스캔·필터링**: 코스피 100 → StandardScaler 정규화 → 가중합산 스코어 → 상위 30개
- **데이터 수집**: KIS 분봉/일봉/수급 (asyncio 병렬) + 뉴스 크롤링 (RSS·네이버금융)
- **정량 분석**: KIS 4개 피처 + DART 재무지표 3개 DB 조회
- **감성 분석**: 트랙1(RSS→시장전반) + 트랙2(네이버금융→종목별) → KR-FinBERT → `sentiment_score`
- **시계열 예측**: KIS API 120거래일 직접 조회 → Prophet → D+1~D+5 (`price_trend`/`volume_trend`/`uncertainty`)
- **차트 생성**: matplotlib 4종 PNG → `/static/charts/` 저장
- **AI 판단**: 11개 피처 + 보유 여부 → Gemini → 매수/매도 TOP3 JSON
- **매매 실행 요청**: `is_active=true` 시 SpringBoot로 주문 요청 POST

### SpringBoot (Java) — 애플리케이션 서버 전용

- admin 로그인 처리
- FastAPI 매매 요청 수신 → KIS 모의투자 API 호출
- 체결 내역 PostgreSQL 저장
- 거래 내역/대시보드/보유현황 REST API 제공 (Vue3용)
- 기존 Spring + MyBatis 스택 그대로 활용

---

## 6. 파이프라인 실행 흐름 (매일 08:50)

| 단계 | 내용 | 기술 | 비고 |
|------|------|------|------|
| ① | APScheduler 08:50 트리거 (평일 cron) | APScheduler | 코드 기반 형상관리 |
| ② | 보유 종목 조회: SpringBoot → KIS API | KIS API | 보유 종목은 ④에서 강제 포함 |
| ③ | 코스피 100 종목 KIS API 일괄 조회 (asyncio, 200회/40초) | KIS API, asyncio | Rate Limit: 초당 5건 |
| ④ | StandardScaler 정규화 + 가중합산 스코어 → 상위 27개 + 보유 종목 → 최종 30개 | pandas, NumPy | `stock_filter_score` 저장 |
| ⑤ | 분석 대상 30종목 추가 수집 (분봉 + 뉴스) | BeautifulSoup, asyncio | 종목당 약 2~3회 추가 호출 |
| ⑥ | 정량 피처 4개 계산 (KIS) | pandas, NumPy | DB 저장 없음 (메모리) |
| ⑦ | DART 재무지표 3개 DB 조회 | PostgreSQL | 분기 1회 갱신 데이터 활용 |
| ⑧ | KR-FinBERT 감성 분석 (트랙1+트랙2) | transformers | `sentiment_today.png` 생성 |
| ⑨ | Prophet 시계열 예측 (120거래일, 60회 KIS 호출) | Prophet, KIS API | `prophet_forecast_today.png` 생성 |
| ⑩ | matplotlib 차트 4종 생성 → `/static/charts/` 저장 | matplotlib, NanumGothic | 매일 덮어쓰기 |
| ⑪ | Gemini API 일괄 판단 (하루 1회) | Gemini API (무료티어) | JSON 강제 응답 |
| ⑫ | DB 저장: 감성/예측/AI판단 결과 | PostgreSQL | |
| ⑬ | 매매 실행: `is_active=true` 시 SpringBoot POST → KIS 체결 | KIS 모의투자 API | `is_active=false` 시 스킵 |

---

## 7. 종목 필터링 스코어링 공식

```python
# 100종목 기준 매일 새로 fit
score = (
    abs(foreign_net_buy_scaled)   * 0.3
  + abs(institutional_net_buy_scaled) * 0.3
  + vol_avg_multiple_scaled       * 0.3
  + price_volatility_scaled       * 0.1
)
# 절대값: 매수/매도 방향 무관하게 수급 강도가 강한 종목 포착
```

- 상위 27개 + 보유 종목(중복 제거) → **최종 30개 확정**
- `stock_filter_score` 테이블에 100개 전종목 스코어·지표값·`is_selected` 저장

---

## 8. Gemini 입력 피처 전체 (총 11개)

| # | 그룹 | 피처명 | 출처 |
|---|------|--------|------|
| 1 | KIS 수급 | `foreign_net_buy` | KIS API → 파이프라인 메모리 |
| 2 | KIS 수급 | `institutional_net_buy` | KIS API → 파이프라인 메모리 |
| 3 | KIS 가격 | `morning_return` | KIS API 분봉 → 파이프라인 메모리 |
| 4 | KIS 가격 | `close_position` | KIS API 일봉 → 파이프라인 메모리 |
| 5 | DART 재무 | `per` | `stock_financial` 테이블 (분기 갱신) |
| 6 | DART 재무 | `roe` | `stock_financial` 테이블 (분기 갱신) |
| 7 | DART 재무 | `operating_margin` | `stock_financial` 테이블 (분기 갱신) |
| 8 | KR-FinBERT | `sentiment_score` | `news_analysis` 테이블 (당일 생성) |
| 9 | Prophet | `prophet_price_trend` | `prophet_forecast` 테이블 (당일 생성) |
| 10 | Prophet | `prophet_volume_trend` | `prophet_forecast` 테이블 (당일 생성) |
| 11 | Prophet | `prophet_price_uncertainty` | `prophet_forecast` 테이블 (당일 생성) |

> KIS 피처 4개는 DB 저장 없이 파이프라인 메모리에서 처리 후 Gemini에 직접 전달

---

## 9. Prophet 시계열 피처 추출

| 피처명 | 계산 방법 | 설명 |
|--------|-----------|------|
| `prophet_price_trend` | D+1~D+5 yhat에 선형회귀 → slope | 양수: 상승 기조, 음수: 하락 기조 |
| `prophet_volume_trend` | D+1~D+5 순매수비율 yhat에 선형회귀 → slope | 순매수 비율 추세 방향성 |
| `prophet_price_uncertainty` | D+1~D+5 (yhat_upper-yhat_lower)/yhat 평균 | 값이 클수록 예측 신뢰도 낮음 |

- 이상값 제거: **NumPy percentile 기준** (1%·99% 범위 외 제거)
- 공휴일 처리: Prophet이 불규칙 날짜 간격 자동 처리, 별도 보간 없음

---

## 10. 감성 분석 구조

### 트랙 1 — 시장 전반 (RSS 피드)
- 한국경제·매일경제·연합뉴스 RSS 3개
- 수집 범위: 전날 18:00 ~ 당일 08:50
- 전처리: HTML 태그 제거 → 제목+본문 앞 200자 → 512토큰 자르기 → 중복 제거(제목 앞 20자)
- `stock_code = NULL`로 저장

### 트랙 2 — 종목별 (네이버금융 크롤링)
- 분석 대상 30종목 각각, 종목당 최대 5건
- `stock_code = 해당 종목코드`로 저장

**공통**: KR-FinBERT → P(긍정) - P(부정) = `-1.0 ~ +1.0` → 시간 가중 평균 (최신 기사 높은 가중치, 선형 감쇠)

---

## 11. Gemini 프롬프트 구조

```
역할: 한국 주식 시장 데이터 분석 전문가
입력: 30종목 × 11개 피처 (JSON) + is_holding 플래그
출력: JSON 강제 응답

{
  "buy": [
    {"rank": 1, "stock_code": "000660", "stock_name": "SK하이닉스", "reason": "판단 근거 2~3문장"},
    ...
  ],
  "sell": [
    {"rank": 1, "stock_code": "373220", "stock_name": "LG에너지솔루션", "reason": "판단 근거 2~3문장"},
    ...
  ]
}
```

- 하루 1회 호출 (Gemini 무료티어 제약)
- `ai_trade_decision` 테이블에 `stock_code` 기준 저장

---

## 12. DB 테이블 설계 (총 8개)

| 테이블명 | 주요 컬럼 | 설명 |
|----------|-----------|------|
| `users` | id, username, is_active | admin 단일 계정 |
| `user_trade_config` | user_id, order_amount, max_holdings, order_type, is_active | 투자 전략 설정 (`is_active`: 자동매매 ON/OFF) |
| `stock_filter_score` | stock_code, score_date, foreign_net_buy, institutional_net_buy, vol_avg_multiple, price_volatility, scaler_score, is_selected | 코스피 100 스코어링 결과 (매일 100건) |
| `stock_financial` | stock_code, base_date, per, roe, operating_margin | DART API 재무지표 (분기별 갱신) |
| `news_analysis` | stock_code, analysis_date, sentiment_score, news_count | 감성분석 결과 (`NULL`=시장전반, 종목코드=종목별) |
| `prophet_forecast` | stock_code, forecast_date, yhat_d1~d5, yhat_upper/lower_d1~d5, price_trend, volume_trend, price_uncertainty | Prophet D+1~D+5 예측 결과 |
| `ai_trade_decision` | stock_code, decision_date, decision(buy/sell/hold), rank, reason, prompt_summary | Gemini AI 판단 결과 |
| `trade_history` | user_id, stock_code, order_type, quantity, price, executed_at | 자동매매 체결 이력 |

> 제거된 테이블: `stock_timeseries`, `stock_daily_analysis`, `stock_ml_label`, `feature_importance`, `chart_files`

---

## 13. matplotlib 차트 4종

| 파일명 | 차트 내용 | 표시 탭 |
|--------|-----------|---------|
| `heatmap_today.png` | 11피처×30종목 히트맵 (파랑→빨강) | 종합분석 |
| `quant_features_today.png` | 외국인순매수/기관순매수/거래량배율 3분할 수평 바차트 | 정량분석 |
| `sentiment_today.png` | 30종목 sentiment_score 수평 바차트 (오름차순) | 감성분석 |
| `prophet_forecast_today.png` | 매수TOP3 Prophet 예측선+신뢰구간 | 시계열 |

- 저장 경로: `/static/charts/` (FastAPI StaticFiles)
- 파일명 고정, **매일 덮어쓰기**
- 서버 환경: **NanumGothic 폰트 필수 설치**

---

## 14. Vue3 화면 목록 (MVP 5페이지)

| 페이지 | 경로 | 주요 기능 |
|--------|------|-----------|
| 로그인 | `/login` | admin 입력 시 통과 (비밀번호 검증 없음) |
| 대시보드 | `/` | 보유 현황 + 오늘 매매 내역 요약 |
| AI 분석 | `/ai` | 4탭: 종합분석/정량분석/감성분석/시계열 + matplotlib 차트 이미지 |
| 거래 내역 | `/trades` | 자동매매 매수/매도 체결 이력 전체 조회 |
| 설정 | `/settings` | 자동매매 ON/OFF 토글 |

### AI 분석 페이지 4탭 구성

| 탭 | 차트 이미지 | 피처값 카드 | 요약/집계 카드 |
|----|------------|-------------|----------------|
| 종합분석 | `heatmap_today.png` | Gemini 매수/매도 TOP3 + 판단 근거 | 오늘 자동매매 체결 내역 |
| 정량분석 | `quant_features_today.png` | 외국인/기관 순매수 TOP3 바차트 | Gemini TOP3 PER·ROE·영업이익률 |
| 감성분석 | `sentiment_today.png` | 시장 전반 감성점수 + 파이차트 | 감성 상위/하위 3종목 |
| 시계열 | `prophet_forecast_today.png` | price_trend/volume_trend/uncertainty | 30종목 추세 방향 집계 |

---

## 15. FastAPI 엔드포인트 목록

| 메서드 | 경로 | 설명 | 탭 |
|--------|------|------|-----|
| GET | `/api/ai/today` | 시장 전반 요약 (stat-row 수치) | 종합 |
| GET | `/api/ai/top3/buy` | Gemini 매수 TOP3 + 판단 근거 | 종합 |
| GET | `/api/ai/top3/sell` | Gemini 매도 TOP3 + 판단 근거 | 종합 |
| GET | `/api/ai/top3/metrics` | TOP3 종목 DART 재무지표 | 정량 |
| GET | `/api/ai/quant/summary` | 정량 피처 요약 + 수급 TOP3 | 정량 |
| GET | `/api/ai/sentiment/summary` | 감성 분포 집계 | 감성 |
| GET | `/api/ai/sentiment/top3` | 감성 상위/하위 3종목 | 감성 |
| GET | `/api/ai/timeseries/summary` | 시계열 추세 집계 | 시계열 |
| GET | `/api/ai/timeseries/uncertainty` | 불확실성 상위 3종목 | 시계열 |
| GET | `/charts/{파일명}` | matplotlib 차트 PNG 정적 서빙 | 전체 |

---

## 16. 기술 스택 요약

| 영역 | 기술 |
|------|------|
| 스케줄링 | APScheduler (FastAPI 내장) |
| 데이터 수집 | Python, BeautifulSoup, RSS 파싱, KIS API (asyncio 병렬) |
| 필터링/피처 계산 | pandas, NumPy, StandardScaler (scikit-learn) |
| 재무 분석 | DART API (분기별) |
| 감성 분석 | transformers (KR-FinBERT) |
| 시계열 예측 | Prophet (Meta) |
| 시각화 | matplotlib + NanumGothic |
| 생성형 AI | Gemini API (무료티어, 하루 1회) |
| 백엔드 파이프라인 | Python FastAPI |
| 백엔드 서비스 | SpringBoot + MyBatis |
| 프론트엔드 | Vue3 (CSR, FinanceManageAgent 기반) |
| 인증 | admin 하드코딩 (MVP) |
| RDB | PostgreSQL |
| 인프라 | Docker, docker-compose (4컨테이너) |
| 형상관리 | GitHub 모노레포 / `ai-trading-pipeline` 브랜치 |
| 증권 API | KIS Developers 모의투자 (Rate Limit: 초당 5건) |

---

## 17. MVP 제외 항목 (OUT)

- 회원가입 / 다중 사용자 관리
- JWT 인증 / 권한 관리
- KIS 키 및 관심 종목 입력 UI
- 카카오톡 / 외부 알림
- n8n 워크플로우
- LSTM / 딥러닝 모델
- RandomForest (종목 스코어링은 StandardScaler만 사용)

---

## 18. 향후 개선 로드맵

| 단계 | 내용 |
|------|------|
| 단기 | 장중 분봉 실시간 수집, DART 공시·애널리스트 리포트 감성분석 확장 |
| 중기 | 누적 모의투자 체결 결과 레이블 활용 → **RandomForest 분류 모델** 고도화 |
| 장기 | Prophet → **LSTM 딥러닝** 고도화, 포트폴리오 관리 최적화, KIS 실투자 API 전환 |