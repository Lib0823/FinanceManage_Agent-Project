# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AI-powered stock auto-trading system that analyzes KOSPI top 100 stocks daily, filters to top 30 using ML scoring, performs 3-way analysis (quantitative features, sentiment analysis, time-series forecasting), and uses Gemini AI to execute buy/sell decisions via KIS mock trading API.

**Monorepo Structure:**
- `web-app/` — Vue3 SPA frontend (PWA-enabled)
- `api-server/` — Spring Boot backend for trading execution and REST API
- `ai-agent/` — Python FastAPI for ML pipeline, analysis, and AI decisions
- `database/` — PostgreSQL schema and ERD documentation

## Development Commands

### web-app (Vue3 Frontend)
```bash
cd web-app
npm install              # Install dependencies
npm run dev              # Development server (http://localhost:5173)
npm run build            # Production build
npm run preview          # Preview production build
npm run lint             # Run ESLint
npm run format           # Format code with Prettier
```

### api-server (Spring Boot Backend)
```bash
cd api-server
./gradlew build          # Build project
./gradlew test           # Run tests
./gradlew bootRun        # Run development server (port 8080)
./gradlew clean          # Clean build artifacts
```

### Full System (Docker Compose)
```bash
docker-compose up -d     # Start all services
docker-compose down      # Stop all services
docker-compose logs -f   # View logs
```

**Container Services:**
- `web-app` → Nginx (port 3000)
- `api-server` → Spring Boot (port 8080)
- `ai-agent` → FastAPI (port 8000)
- `postgres` → PostgreSQL (port 5432)
- `elasticsearch` → Elasticsearch (port 9200)

## Architecture & Data Flow

### Service Communication Pattern
```
Vue3 → Spring Boot (8080)     : Dashboard, trade history, settings, auth
Vue3 → FastAPI (8000)          : AI analysis results, matplotlib charts
FastAPI → Spring Boot          : POST trading requests, GET holdings
FastAPI → KIS API              : Real-time stock data, order execution
FastAPI → DART API             : Quarterly financial data
FastAPI ⇄ PostgreSQL           : Analysis results, predictions, AI decisions
Spring Boot ⇄ PostgreSQL       : Trade history, user settings
```

### Daily Pipeline Flow (APScheduler @ 08:50 KST)
1. **Stock Filtering**: KOSPI 100 → StandardScaler scoring → Top 30 stocks
   - `score = abs(foreign_net_buy)*0.3 + abs(institutional_net_buy)*0.3 + vol_avg_multiple*0.3 + price_volatility*0.1`
   - Holdings always included in final 30
2. **Data Collection**: KIS API (asyncio parallel), news crawling (RSS + Naver)
3. **3-Way Analysis**:
   - **Quantitative**: 4 KIS features + 3 DART financials
   - **Sentiment**: KR-FinBERT on market news (track 1) + stock-specific news (track 2)
   - **Time-Series**: Prophet 120-day forecasting → D+1~D+5 trends
4. **Chart Generation**: 4 matplotlib PNGs → `/static/charts/` (overwritten daily)
5. **AI Decision**: Gemini API judges 11 features → Buy/Sell TOP3 with reasons
6. **Trade Execution**: If `is_active=true`, POST to Spring Boot → KIS order execution

### Frontend Architecture (Vue3)
- **Router**: Vue Router 4 with lazy-loaded views
- **State**: LocalStorage for auth tokens
- **API Layer**: Axios with request/response interceptors (web-app/src/services/api.js)
- **Styling**: Tailwind CSS 4.1 + custom components
- **Build**: Vite 7.3 with PWA plugin
- **Auth Guard**: Dev mode skips auth, production checks localStorage token

**Key Routes:**
- `/` → Splash screen
- `/home` → Dashboard with holdings summary
- `/assets` → Asset breakdown (stocks/bonds/coins/cash)
- `/bot` → AI bot analysis and control
- `/search` → Stock search
- `/news` → Market news feed
- `/profile`, `/settings` → User management

### Backend Architecture (Spring Boot)
- **Java Version**: 21 (LTS)
- **Framework**: Spring Boot 4.1.0-SNAPSHOT
- **ORM**: Spring Data JPA
- **Database**: PostgreSQL
- **Security**: Spring Security (admin-only MVP, no JWT yet)
- **Build**: Gradle
- **Testing**: JUnit 5 + Mockito

**Package Structure:**
```
com.inbeom.apiserver/
  └── ApiServerApplication.java  # Main entry point
```

### AI Pipeline Architecture (FastAPI)
- **Scheduler**: APScheduler with cron expression (weekdays 08:50)
- **ML Stack**: pandas, NumPy, scikit-learn (StandardScaler), Prophet
- **NLP**: transformers (KR-FinBERT for sentiment analysis)
- **Charts**: matplotlib + NanumGothic font (required in server environment)
- **AI**: Gemini API (free tier, 1 call/day)
- **Async**: asyncio for parallel KIS API calls (rate limit: 5 req/sec)

**Chart Files (Static Serving):**
- `heatmap_today.png` → 11 features × 30 stocks heatmap
- `quant_features_today.png` → Foreign/institutional net buy + volume bars
- `sentiment_today.png` → Sentiment scores by stock
- `prophet_forecast_today.png` → Top 3 buy predictions with confidence intervals

## Database Schema

**Key Tables (8 total):**
- `users` — Admin account (single user MVP)
- `user_trade_config` — Investment settings (`is_active` flag for auto-trading)
- `stock_filter_score` — Daily KOSPI 100 scoring results
- `stock_financial` — DART quarterly financials (PER, ROE, operating margin)
- `news_analysis` — KR-FinBERT sentiment scores (market + stock-specific)
- `prophet_forecast` — D+1~D+5 price/volume trends, uncertainty
- `ai_trade_decision` — Gemini buy/sell TOP3 decisions with reasoning
- `trade_history` — KIS order execution history

See `database/database-erd.sql` for full schema and `database/database-erd-diagram.md` for ERD visualization.

## Technology Stack Summary

| Layer | Technology |
|-------|-----------|
| Frontend | Vue 3.5 (Composition API), Vite 7.3, Vue Router 4, Tailwind CSS 4.1, Chart.js, Vant UI, PWA |
| Backend API | Spring Boot 4.1, Spring Data JPA, Spring Security, PostgreSQL, Gradle |
| AI Pipeline | Python 3.11+, FastAPI, APScheduler, pandas, NumPy, scikit-learn, Prophet, transformers (KR-FinBERT), matplotlib |
| AI Model | Gemini API (free tier) |
| Database | PostgreSQL 16 |
| Search | Elasticsearch 8.x |
| Infra | Docker, Docker Compose |
| External APIs | KIS Developers (mock trading), DART (financial data) |

## Important Development Notes

### Frontend (web-app)
- **API Base URL**: Configured via `VITE_API_BASE_URL` environment variable, defaults to `http://localhost:8080/api`
- **Auth**: Dev mode (`import.meta.env.DEV`) skips authentication checks
- **PWA**: App is installable, configured in vite.config.js with service worker
- **Navigation**: Bottom nav shown via `meta.showBottomNav` in route config
- **Styling**: 2-space indentation, single quotes for JS strings

### Backend (api-server)
- **Java 21 Required**: Uses toolchain configuration in build.gradle
- **Lombok**: Used for boilerplate reduction (getters/setters/constructors)
- **Database**: PostgreSQL runtime dependency, JPA for ORM
- **Testing**: JUnit Platform Launcher for test execution
- **Indentation**: 4-space for Java files

### AI Pipeline (ai-agent)
- **Font Dependency**: NanumGothic must be installed in container for matplotlib Korean text rendering
- **Rate Limiting**: KIS API limited to 5 requests/second, handled via asyncio delays
- **Scheduling**: APScheduler configured programmatically (no external cron), weekday 08:50 KST only
- **Data Flow**: Always includes holdings in final 30 stocks to enable sell analysis
- **Chart Overwriting**: Daily charts overwrite previous day's files (no versioning)

### Database (database/)
- **Schema Files**: `database-erd.sql` contains complete table definitions
- **Documentation**: ERD summary and diagram in markdown format
- **Migration**: No automated migration framework, manual SQL execution required

### Project-Specific Context
This system is designed for a graduate school final project focusing on AI-powered algorithmic trading. The MVP scope deliberately excludes multi-user support, JWT authentication, and production KIS API integration. All configurations (KIS API keys, KOSPI 100 stock codes) are hardcoded in the codebase.

### Git Workflow
- Main branch: `main`
- Current development branch: `develop-analysis` (analysis features)
- Active AI pipeline branch: `ai-trading-pipeline` (referenced in project guide)

### Analysis View Files
- `web-app/analysis_view/overview.html` — Standalone analysis overview page
- `web-app/analysis_view/stock_detail.html` — Stock detail analysis page
- These are static HTML files separate from the Vue3 router system
