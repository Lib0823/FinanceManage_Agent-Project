# Web App - Tech Stack

## 핵심 기술

| 항목 | 기술 | 버전 |
|------|------|------|
| Framework | Vue | 3.5.x (Composition API) |
| Build Tool | Vite | 7.3.x |
| Package Manager | npm | - |
| Node.js | - | ^20.19.0 \|\| >=22.12.0 |

## 주요 라이브러리

### Dependencies

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| vue-router | 4.6.x | SPA 라우팅 |
| axios | 1.13.x | HTTP 클라이언트 |
| vant | 4.9.x | 모바일 UI 컴포넌트 |
| chart.js | 4.5.x | 차트 라이브러리 |
| vue-chartjs | 5.3.x | Vue Chart.js 래퍼 |
| v-calendar | 3.1.x | 캘린더/날짜 선택 |
| @popperjs/core | 2.11.x | 팝오버/툴팁 |

### DevDependencies

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| @vitejs/plugin-vue | 6.0.x | Vue SFC 지원 |
| vite-plugin-vue-devtools | 8.0.x | Vue 개발자 도구 |
| tailwindcss | 4.1.x | 유틸리티 CSS |
| postcss | 8.5.x | CSS 후처리 |
| autoprefixer | 10.4.x | CSS 벤더 프리픽스 |
| eslint | 9.39.x | 코드 린팅 |
| eslint-plugin-vue | 10.6.x | Vue 린트 규칙 |
| prettier | 3.7.x | 코드 포맷팅 |

## UI/스타일

| 항목 | 값 |
|------|-----|
| UI Library | Vant 4 (모바일 최적화) |
| CSS Framework | Tailwind CSS 4 |
| Scoped CSS | Vue SFC scoped |
| 아이콘 | Vant Icons |

### Vant 컴포넌트 사용

```javascript
// main.js에서 등록된 컴포넌트
Button, Tab, Tabs, NavBar, Icon, Cell, CellGroup,
Field, Switch, Toast, Dialog, Loading, Popup,
Tag, Badge, Skeleton, Empty, PullRefresh, List
```

### Chart.js 설정

```javascript
// 등록된 Chart.js 요소
Title, Tooltip, Legend, ArcElement,
CategoryScale, LinearScale, BarElement,
PointElement, LineElement
```

### 색상 시스템

```css
--primary: #4318FF;    /* 보라 - 주요 액션 */
--secondary: #05CD99;  /* 민트 - 상승/긍정 */
--accent: #FFB547;     /* 주황 - 강조 */
--danger: #FF5252;     /* 빨강 - 하락/위험 */
--background: #F4F7FE; /* 배경 */
--text: #2B3674;       /* 텍스트 */
```

## 개발 도구

| 도구 | 설정 파일 | 용도 |
|------|----------|------|
| ESLint | eslint.config.js | 코드 린트 |
| Prettier | .prettierrc.json | 코드 포맷팅 |
| EditorConfig | .editorconfig | 에디터 설정 |
| Vue DevTools | vite-plugin-vue-devtools | 디버깅 |

### 스크립트

```bash
npm run dev      # 개발 서버 (Vite)
npm run build    # 프로덕션 빌드
npm run preview  # 빌드 결과 미리보기
npm run lint     # ESLint 실행 + 자동 수정
npm run format   # Prettier 포맷팅
```

## 환경 변수

```bash
# .env.local (개발)
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=ws://localhost:8080/ws

# .env.production (운영)
VITE_API_BASE_URL=https://api.asset-agent.com/api
VITE_WS_URL=wss://api.asset-agent.com/ws
```

## API 통신

### Axios 설정
- Base URL: `VITE_API_BASE_URL` 환경변수
- Timeout: 10초
- Request Interceptor: JWT 토큰 자동 첨부
- Response Interceptor: 401 에러 시 로그인 페이지 리다이렉트

### 인증
- JWT (Access Token)
- localStorage 저장 (`accessToken`)
- `Authorization: Bearer {token}` 헤더

## 브라우저 지원

- Chrome (최신)
- Safari (iOS 13+)
- Samsung Internet
- Edge (Chromium)

> 모바일 우선 설계 (Mobile First)
