# Web App - Project Structure

> 상태: **MVP 개발 중** (화면 구현 완료, API 연동 진행)

## 개발 범위 (현재 MVP)

### 구현 완료 화면
1. **인증 관련**
   - 스플래시 (/)
   - 온보딩 (/welcome)
   - 로그인 (/login)
   - 회원가입 (/register)
   - 금융정보 등록 (/register/finance)
   - 약관 동의 (/terms)
   - 비밀번호 재설정 (/reset-password)

2. **메인 화면 (하단 네비)**
   - 홈 (/home) - 시장 현황, 보유 자산 요약, 오늘 매매 내역
   - 내 자산 (/assets) - 자산 탭 (국내/미국/코인), 보유 종목
   - AI 투자 (/bot) - AI 분석 결과, 자동매매 ON/OFF
   - 종목 검색 (/search)
   - 관심 종목 (/favorites)
   - 거래 내역 (/transactions)
   - 프로필 (/profile)

3. **상세 화면**
   - 자산 상세 (/assets/detail)
   - 기업 상세 (/company/:symbol) - 차트, 재무제표, 공시, AI 분석
   - 매수/매도 (/trading/:symbol)
   - 뉴스 목록 (/news)
   - 뉴스 상세 (/news/:id)
   - 설정 (/settings)

### 추가 필요 화면
4. **오늘의 분석 (AI 분석 결과)**
   - 경로: `/analysis` (또는 `/bot`에 통합)
   - 4개 탭 구성:
     - 종합분석: Gemini 매수/매도 TOP3 + 히트맵 차트
     - 정량분석: 수급 TOP3 + 피처 차트
     - 감성분석: 감성 상위/하위 + 감성 차트
     - 시계열: Prophet 예측 + 추세 집계

### API 연동 상태
- **완료**: 인증 (로그인, 회원가입)
- **진행 중**:
  - 자산/보유 종목 (KIS API)
  - 거래 내역 조회
  - AI 분석 결과 (FastAPI 연동)
  - 기업 상세 (DART API)
- **대기**: 뉴스, 시장 지수

## 디렉토리 구조

```
web-app/
├── src/
│   ├── assets/               # 정적 스타일
│   │   ├── base.css          # CSS 변수, 기본 스타일
│   │   └── main.css          # 전역 스타일
│   │
│   ├── components/           # 재사용 컴포넌트
│   │   ├── common/           # 공통 UI 컴포넌트
│   │   │   ├── AppHeader.vue     # 상단 헤더
│   │   │   ├── AssetTabs.vue     # 자산 탭 (국내/미국/코인)
│   │   │   ├── BottomNav.vue     # 하단 네비게이션 바
│   │   │   └── StockCard.vue     # 종목 카드
│   │   └── icons/            # 아이콘 컴포넌트
│   │
│   ├── router/               # Vue Router
│   │   └── index.js          # 라우트 정의 + Navigation Guard
│   │
│   ├── services/             # API 서비스 레이어
│   │   ├── api.js            # Axios 인스턴스 + API 함수
│   │   └── mockData.js       # 개발용 Mock 데이터
│   │
│   ├── views/                # 페이지 컴포넌트
│   │   ├── auth/             # 인증 관련 페이지
│   │   │   ├── SplashView.vue        # 스플래시 화면
│   │   │   ├── WelcomeView.vue       # 웰컴/온보딩
│   │   │   ├── LoginView.vue         # 로그인
│   │   │   ├── RegisterView.vue      # 회원가입
│   │   │   ├── RegisterFinanceView.vue # 금융정보 등록
│   │   │   ├── TermsView.vue         # 약관 동의
│   │   │   └── ResetPasswordView.vue # 비밀번호 재설정
│   │   │
│   │   ├── main/             # 메인 탭 페이지 (하단 네비)
│   │   │   ├── HomeView.vue      # 홈 (시장 현황)
│   │   │   ├── AssetsView.vue    # 내 자산
│   │   │   ├── BotView.vue       # AI 봇
│   │   │   ├── SearchView.vue    # 종목 검색
│   │   │   └── FavoritesView.vue # 관심 종목
│   │   │
│   │   ├── detail/           # 상세 페이지
│   │   │   ├── AssetDetailView.vue   # 자산 상세
│   │   │   ├── CompanyDetailView.vue # 기업 상세
│   │   │   ├── TradingView.vue       # 매수/매도
│   │   │   ├── TransactionsView.vue  # 거래 내역
│   │   │   ├── NewsView.vue          # 뉴스 목록
│   │   │   └── NewsDetailView.vue    # 뉴스 상세
│   │   │
│   │   └── settings/         # 설정 페이지
│   │       ├── ProfileView.vue   # 프로필
│   │       └── SettingsView.vue  # 설정
│   │
│   ├── App.vue               # 루트 컴포넌트
│   └── main.js               # 엔트리 포인트
│
├── public/                   # 정적 파일
├── index.html
├── vite.config.js
├── tailwind.config.js        # Tailwind CSS 설정
├── eslint.config.js
└── package.json
```

## 폴더 구조 원칙

### views/ (페이지 기반)
- 기능 도메인별로 하위 폴더 구성 (auth, main, detail, settings)
- `{Name}View.vue` 네이밍 패턴

### components/common/ (공통)
- 2개 이상 페이지에서 사용하는 UI 컴포넌트
- 레이아웃 컴포넌트 (Header, BottomNav)
- 재사용 UI 요소 (StockCard, AssetTabs)

### services/ (API)
- Axios 인스턴스 및 인터셉터 설정
- 도메인별 API 함수 (authApi, stockApi, tradingApi 등)

## 네이밍 규칙

| 대상 | 패턴 | 예시 |
|------|------|------|
| 페이지 컴포넌트 | {Name}View.vue | LoginView.vue |
| 공통 컴포넌트 | PascalCase | StockCard.vue |
| 라우트 name | kebab-case | company-detail |
| API 객체 | {domain}Api | tradingApi |

## 라우트 구조

### 인증 (Public)
| 경로 | 컴포넌트 | 설명 |
|------|----------|------|
| / | SplashView | 스플래시 |
| /welcome | WelcomeView | 온보딩 |
| /login | LoginView | 로그인 |
| /register | RegisterView | 회원가입 |
| /register/finance | RegisterFinanceView | 금융정보 등록 |
| /terms | TermsView | 약관 |
| /reset-password | ResetPasswordView | 비밀번호 재설정 |

### 메인 (하단 네비 표시)
| 경로 | 컴포넌트 | 설명 |
|------|----------|------|
| /home | HomeView | 홈 |
| /assets | AssetsView | 내 자산 |
| /bot | BotView | AI 봇 |
| /search | SearchView | 검색 |
| /favorites | FavoritesView | 관심 종목 |
| /transactions | TransactionsView | 거래 내역 |
| /profile | ProfileView | 프로필 |

### 상세 (헤더만 표시)
| 경로 | 컴포넌트 | 설명 |
|------|----------|------|
| /assets/detail | AssetDetailView | 자산 상세 |
| /company/:symbol | CompanyDetailView | 기업 상세 |
| /trading/:symbol | TradingView | 매매 |
| /news | NewsView | 뉴스 목록 |
| /news/:id | NewsDetailView | 뉴스 상세 |
| /settings | SettingsView | 설정 |

## API 서비스 구조

```javascript
// services/api.js
export const authApi = { login, register, logout, ... }
export const userApi = { getProfile, updateProfile, ... }
export const assetApi = { getSummary, getStocks, ... }
export const stockApi = { getList, getDetail, search, ... }
export const companyApi = { getInfo, getFinancials, getAiAnalysis, ... }
export const tradingApi = { getOrders, placeOrder, cancelOrder, ... }
export const newsApi = { getList, getDetail, getByDate, ... }
export const marketApi = { getIndices, getExchangeRates, ... }
export const botApi = { getStatus, toggleBot, ... }
```
