# Database Schema

## ERD 개요

주식 자동 투자 시스템의 데이터베이스 스키마입니다.

**주요 테이블:**
- users (사용자)
- accounts (계좌)
- stocks (종목)
- orders (주문)
- executions (체결)
- assets (자산)
- news (뉴스)
- ai_signals (AI 신호)
- auto_trading_configs (자동매매 설정)

---

## 1. Users (사용자)

```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(50) NOT NULL,
  phone_number VARCHAR(20),
  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE, INACTIVE, SUSPENDED
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_user_status ON users(status);

COMMENT ON TABLE users IS '사용자';
COMMENT ON COLUMN users.email IS '이메일 (로그인 ID)';
COMMENT ON COLUMN users.password IS '암호화된 비밀번호 (BCrypt)';
COMMENT ON COLUMN users.status IS '계정 상태';
```

---

## 2. Refresh Tokens (리프레시 토큰)

```sql
CREATE TABLE refresh_tokens (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token VARCHAR(500) NOT NULL UNIQUE,
  expires_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_token_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_token_expires ON refresh_tokens(expires_at);

COMMENT ON TABLE refresh_tokens IS 'JWT Refresh Token';
```

---

## 3. Accounts (계좌)

```sql
CREATE TABLE accounts (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  account_number VARCHAR(50) NOT NULL UNIQUE,
  broker VARCHAR(50) NOT NULL,  -- 증권사 (한국투자증권 등)
  total_cash DECIMAL(15,2) NOT NULL DEFAULT 0,
  available_cash DECIMAL(15,2) NOT NULL DEFAULT 0,
  withdrawable_cash DECIMAL(15,2) NOT NULL DEFAULT 0,
  currency VARCHAR(3) NOT NULL DEFAULT 'KRW',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_account_user ON accounts(user_id);
CREATE UNIQUE INDEX idx_account_number ON accounts(account_number);

COMMENT ON TABLE accounts IS '증권 계좌';
COMMENT ON COLUMN accounts.total_cash IS '예수금 총액';
COMMENT ON COLUMN accounts.available_cash IS '주문 가능 금액';
COMMENT ON COLUMN accounts.withdrawable_cash IS '출금 가능 금액';
```

---

## 4. Stocks (종목)

```sql
CREATE TABLE stocks (
  id BIGSERIAL PRIMARY KEY,
  symbol VARCHAR(20) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  market VARCHAR(20) NOT NULL,  -- KOSPI, KOSDAQ, NYSE, NASDAQ
  currency VARCHAR(3) NOT NULL DEFAULT 'KRW',
  sector VARCHAR(50),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_stock_symbol ON stocks(symbol);
CREATE INDEX idx_stock_market ON stocks(market);
CREATE INDEX idx_stock_active ON stocks(is_active);

COMMENT ON TABLE stocks IS '종목 정보';
COMMENT ON COLUMN stocks.symbol IS '종목 코드 (005930, AAPL 등)';
COMMENT ON COLUMN stocks.market IS '시장 구분';
```

---

## 5. Stock Prices (주가)

```sql
CREATE TABLE stock_prices (
  id BIGSERIAL PRIMARY KEY,
  stock_id BIGINT NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
  current_price DECIMAL(15,2) NOT NULL,
  change_price DECIMAL(15,2) NOT NULL,
  change_rate DECIMAL(5,2) NOT NULL,
  high_price DECIMAL(15,2) NOT NULL,
  low_price DECIMAL(15,2) NOT NULL,
  volume BIGINT NOT NULL,
  recorded_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_price_stock ON stock_prices(stock_id);
CREATE INDEX idx_stock_price_recorded ON stock_prices(recorded_at DESC);

COMMENT ON TABLE stock_prices IS '주가 이력 (시계열)';
```

---

## 6. Orders (주문)

```sql
CREATE TABLE orders (
  id BIGSERIAL PRIMARY KEY,
  order_id VARCHAR(50) NOT NULL UNIQUE,  -- ORD-YYYYMMDD-XXX
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  stock_id BIGINT NOT NULL REFERENCES stocks(id),
  order_type VARCHAR(20) NOT NULL,  -- BUY, SELL
  price_type VARCHAR(20) NOT NULL,  -- LIMIT, MARKET
  price DECIMAL(15,2),
  quantity INT NOT NULL,
  filled_quantity INT NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, FILLED, PARTIALLY_FILLED, CANCELLED
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_order_order_id ON orders(order_id);
CREATE INDEX idx_order_user ON orders(user_id);
CREATE INDEX idx_order_account ON orders(account_id);
CREATE INDEX idx_order_stock ON orders(stock_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_created ON orders(created_at DESC);

COMMENT ON TABLE orders IS '주문';
COMMENT ON COLUMN orders.order_id IS '주문 ID (비즈니스 키)';
COMMENT ON COLUMN orders.order_type IS '주문 유형 (매수/매도)';
COMMENT ON COLUMN orders.price_type IS '가격 유형 (지정가/시장가)';
COMMENT ON COLUMN orders.status IS '주문 상태';
```

---

## 7. Executions (체결)

```sql
CREATE TABLE executions (
  id BIGSERIAL PRIMARY KEY,
  execution_id VARCHAR(50) NOT NULL UNIQUE,  -- EXE-YYYYMMDD-XXX
  order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
  price DECIMAL(15,2) NOT NULL,
  quantity INT NOT NULL,
  executed_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_execution_execution_id ON executions(execution_id);
CREATE INDEX idx_execution_order ON executions(order_id);
CREATE INDEX idx_execution_executed ON executions(executed_at DESC);

COMMENT ON TABLE executions IS '체결 내역';
COMMENT ON COLUMN executions.execution_id IS '체결 ID (비즈니스 키)';
```

---

## 8. Assets (보유 자산)

```sql
CREATE TABLE assets (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  stock_id BIGINT NOT NULL REFERENCES stocks(id),
  quantity INT NOT NULL,
  average_price DECIMAL(15,2) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(account_id, stock_id)
);

CREATE INDEX idx_asset_user ON assets(user_id);
CREATE INDEX idx_asset_account ON assets(account_id);
CREATE INDEX idx_asset_stock ON assets(stock_id);
CREATE UNIQUE INDEX idx_asset_account_stock ON assets(account_id, stock_id);

COMMENT ON TABLE assets IS '보유 종목';
COMMENT ON COLUMN assets.quantity IS '보유 수량';
COMMENT ON COLUMN assets.average_price IS '평균 매수가';
```

---

## 9. News (뉴스)

```sql
CREATE TABLE news (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(500) NOT NULL,
  content TEXT,
  summary VARCHAR(1000),
  source VARCHAR(100) NOT NULL,
  url VARCHAR(500),
  sentiment VARCHAR(20),  -- POSITIVE, NEGATIVE, NEUTRAL
  published_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_news_published ON news(published_at DESC);
CREATE INDEX idx_news_source ON news(source);
CREATE INDEX idx_news_sentiment ON news(sentiment);

COMMENT ON TABLE news IS '뉴스';
COMMENT ON COLUMN news.sentiment IS '감성 분석 결과';
```

---

## 10. News Stock Relations (뉴스-종목 관계)

```sql
CREATE TABLE news_stock_relations (
  id BIGSERIAL PRIMARY KEY,
  news_id BIGINT NOT NULL REFERENCES news(id) ON DELETE CASCADE,
  stock_id BIGINT NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE(news_id, stock_id)
);

CREATE INDEX idx_news_stock_news ON news_stock_relations(news_id);
CREATE INDEX idx_news_stock_stock ON news_stock_relations(stock_id);

COMMENT ON TABLE news_stock_relations IS '뉴스-종목 연관 관계 (N:M)';
```

---

## 11. AI Signals (AI 매매 신호)

```sql
CREATE TABLE ai_signals (
  id BIGSERIAL PRIMARY KEY,
  stock_id BIGINT NOT NULL REFERENCES stocks(id),
  signal VARCHAR(20) NOT NULL,  -- BUY, SELL, HOLD
  confidence DECIMAL(3,2) NOT NULL,  -- 0.00 ~ 1.00
  target_price DECIMAL(15,2),
  current_price DECIMAL(15,2) NOT NULL,
  expected_return DECIMAL(5,2),
  risk VARCHAR(20),  -- LOW, MEDIUM, HIGH
  reasons TEXT[],
  analyzed_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_signal_stock ON ai_signals(stock_id);
CREATE INDEX idx_ai_signal_signal ON ai_signals(signal);
CREATE INDEX idx_ai_signal_analyzed ON ai_signals(analyzed_at DESC);

COMMENT ON TABLE ai_signals IS 'AI 매매 신호';
COMMENT ON COLUMN ai_signals.confidence IS '신뢰도 (0~1)';
```

---

## 12. Auto Trading Configs (자동매매 설정)

```sql
CREATE TABLE auto_trading_configs (
  id BIGSERIAL PRIMARY KEY,
  auto_trading_id VARCHAR(50) NOT NULL UNIQUE,  -- AT-XXX
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  account_id BIGINT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
  status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',  -- ACTIVE, INACTIVE
  strategy VARCHAR(20) NOT NULL,  -- CONSERVATIVE, BALANCED, AGGRESSIVE
  max_amount DECIMAL(15,2) NOT NULL,
  used_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
  stop_loss DECIMAL(5,2) NOT NULL,  -- 손절 비율 (%)
  take_profit DECIMAL(5,2) NOT NULL,  -- 익절 비율 (%)
  activated_at TIMESTAMP,
  deactivated_at TIMESTAMP,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_auto_trading_id ON auto_trading_configs(auto_trading_id);
CREATE INDEX idx_auto_trading_user ON auto_trading_configs(user_id);
CREATE INDEX idx_auto_trading_status ON auto_trading_configs(status);

COMMENT ON TABLE auto_trading_configs IS '자동매매 설정';
COMMENT ON COLUMN auto_trading_configs.max_amount IS '최대 투자 금액';
COMMENT ON COLUMN auto_trading_configs.used_amount IS '사용된 금액';
```

---

## 13. Auto Trading Logs (자동매매 로그)

```sql
CREATE TABLE auto_trading_logs (
  id BIGSERIAL PRIMARY KEY,
  auto_trading_id BIGINT NOT NULL REFERENCES auto_trading_configs(id) ON DELETE CASCADE,
  order_id BIGINT REFERENCES orders(id),
  action VARCHAR(20) NOT NULL,  -- BUY, SELL, SKIP
  stock_id BIGINT NOT NULL REFERENCES stocks(id),
  reason TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_auto_trading_log_config ON auto_trading_logs(auto_trading_id);
CREATE INDEX idx_auto_trading_log_created ON auto_trading_logs(created_at DESC);

COMMENT ON TABLE auto_trading_logs IS '자동매매 실행 로그';
```

---

## ERD 관계도

```
users (1) ─────┬─ (N) accounts
               │
               ├─ (N) orders
               │
               ├─ (N) assets
               │
               └─ (N) auto_trading_configs

stocks (1) ────┬─ (N) orders
               │
               ├─ (N) assets
               │
               ├─ (N) stock_prices
               │
               ├─ (N) ai_signals
               │
               └─ (N) news_stock_relations ─ (N) news

orders (1) ────── (N) executions

accounts (1) ───┬─ (N) orders
                │
                ├─ (N) assets
                │
                └─ (N) auto_trading_configs

auto_trading_configs (1) ─── (N) auto_trading_logs
```

---

## 초기 데이터

### 주요 종목 (stocks)

```sql
INSERT INTO stocks (symbol, name, market, currency) VALUES
  ('005930', '삼성전자', 'KOSPI', 'KRW'),
  ('000660', 'SK하이닉스', 'KOSPI', 'KRW'),
  ('035720', '카카오', 'KOSPI', 'KRW'),
  ('035420', 'NAVER', 'KOSPI', 'KRW'),
  ('AAPL', 'Apple Inc.', 'NASDAQ', 'USD'),
  ('TSLA', 'Tesla Inc.', 'NASDAQ', 'USD'),
  ('GOOGL', 'Alphabet Inc.', 'NASDAQ', 'USD'),
  ('MSFT', 'Microsoft Corporation', 'NASDAQ', 'USD');
```

---

## 성능 최적화

### 인덱스 전략

```sql
-- 복합 인덱스 (자주 함께 조회되는 컬럼)
CREATE INDEX idx_order_user_created ON orders(user_id, created_at DESC);
CREATE INDEX idx_order_status_created ON orders(status, created_at DESC);
CREATE INDEX idx_asset_user_stock ON assets(user_id, stock_id);

-- 부분 인덱스 (특정 조건만)
CREATE INDEX idx_order_pending ON orders(user_id, created_at DESC) 
  WHERE status = 'PENDING';

-- 커버링 인덱스 (SELECT 시 인덱스만으로 조회)
CREATE INDEX idx_order_list ON orders(user_id, status, created_at DESC) 
  INCLUDE (order_id, stock_id, order_type, price, quantity);
```

### 파티셔닝 (대용량 데이터)

```sql
-- 주가 이력 (월별 파티션)
CREATE TABLE stock_prices_partitioned (
  LIKE stock_prices INCLUDING ALL
) PARTITION BY RANGE (recorded_at);

CREATE TABLE stock_prices_2026_01 PARTITION OF stock_prices_partitioned
  FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
```

---

## 백업 전략

```bash
# 전체 백업
pg_dump -U admin -Fc assetdb > assetdb_$(date +%Y%m%d).dump

# 특정 테이블만 백업
pg_dump -U admin -t orders -t executions assetdb > orders_$(date +%Y%m%d).sql

# 복원
pg_restore -U admin -d assetdb assetdb_20260118.dump
```

---

## 마이그레이션

### Flyway 사용

```
src/main/resources/db/migration/
├── V1__Create_users_table.sql
├── V2__Create_accounts_table.sql
├── V3__Create_stocks_table.sql
├── V4__Create_orders_table.sql
└── V5__Insert_initial_stocks.sql
```

**설정 (application.yml):**
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```
