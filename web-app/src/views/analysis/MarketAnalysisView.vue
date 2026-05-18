<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'

const router = useRouter()

// 현재 날짜
const today = new Date()
const todayStr = `${today.getFullYear()}.${String(today.getMonth() + 1).padStart(2, '0')}.${String(today.getDate()).padStart(2, '0')}`

// TOP3 toggle state
const activeTop3 = ref('buy') // 'buy' or 'sell'

// 30종목 히트맵 데이터
const heatmapStats = ref({
  totalStocks: 30,
  buyCandidate: 18,
  sellCandidate: 4,
  neutral: 8
})

// Gemini AI 매수 TOP3
const buyTop3 = ref([
  {
    rank: 1,
    icon: 'SK',
    iconBg: 'rgba(248,113,113,.12)',
    iconColor: '#f87171',
    name: 'SK하이닉스',
    reason: '외국인 순매수 강세 · 감성 +0.44 · 가격 상승 기조',
    score: 0.89,
    scoreClass: 'up'
  },
  {
    rank: 2,
    icon: '삼',
    iconBg: 'rgba(251,191,36,.12)',
    iconColor: '#fbbf24',
    name: '삼성전자',
    reason: '기관 순매수 지속 · ROE 양호 · 장초반 모멘텀 양수',
    score: 0.72,
    scoreClass: 'yw'
  },
  {
    rank: 3,
    icon: '현',
    iconBg: 'rgba(52,211,153,.12)',
    iconColor: '#34d399',
    name: '현대차',
    reason: '거래량 배율 3.2배 · 감성 +0.31 · PER 저평가',
    score: 0.61,
    scoreClass: 'nt'
  }
])

// Gemini AI 매도 TOP3
const sellTop3 = ref([
  {
    rank: 1,
    icon: 'LG',
    iconBg: 'rgba(96,165,250,.12)',
    iconColor: '#60a5fa',
    name: 'LG에너지솔루션',
    reason: '외국인 순매도 · 감성 -0.21 · 하락 추세',
    score: 0.18,
    scoreClass: 'dn'
  },
  {
    rank: 2,
    icon: '—',
    iconBg: 'var(--color-bg-tertiary)',
    iconColor: 'var(--color-text-tertiary)',
    name: '해당 없음',
    reason: '',
    score: null,
    scoreClass: 'empty',
    isEmpty: true
  },
  {
    rank: 3,
    icon: '—',
    iconBg: 'var(--color-bg-tertiary)',
    iconColor: 'var(--color-text-tertiary)',
    name: '해당 없음',
    reason: '',
    score: null,
    scoreClass: 'empty',
    isEmpty: true
  }
])

// 시장 전반 감성분석 데이터
const marketSentiment = ref({
  score: 0.28,
  label: '긍정 우세',
  distribution: {
    positive: { count: 17, percent: 57, color: '#f87171' },
    neutral: { count: 9, percent: 30, color: '#4a5568' },
    negative: { count: 4, percent: 13, color: '#60a5fa' }
  },
  timeRange: '전날 18:00 — 당일 08:50',
  sources: '한경 · 매경 · 연합'
})

const toggleTop3 = (type) => {
  activeTop3.value = type
}

const goBack = () => {
  router.push('/bot')
}
</script>

<template>
  <div class="market-analysis-screen">
    <AppHeader title="종합분석" :showBack="true" @back="goBack">
      <template #right>
        <div class="header-meta">
          <div class="live-dot"></div>
          <div class="header-date">{{ todayStr }}</div>
        </div>
      </template>
    </AppHeader>

    <div class="content">
      <!-- ① 30종목 피처 히트맵 -->
      <div class="analysis-section">
        <div class="section-label">30종목 피처 히트맵</div>
        <div class="section-card">
          <div class="chart-container">
            <svg class="chart-placeholder" viewBox="0 0 800 400" xmlns="http://www.w3.org/2000/svg">
              <!-- Background grid -->
              <defs>
                <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
                  <rect width="40" height="40" fill="none" stroke="rgba(255,255,255,0.03)" stroke-width="1"/>
                </pattern>
              </defs>
              <rect width="800" height="400" fill="#141b2b"/>
              <rect width="800" height="400" fill="url(#grid)"/>

              <!-- Heatmap simulation -->
              <g opacity="0.6">
                <rect x="50" y="30" width="700" height="340" fill="none" stroke="rgba(167,139,250,0.2)" stroke-width="1"/>
                <!-- Random colored cells to simulate heatmap -->
                <rect x="100" y="60" width="50" height="30" fill="#f87171" opacity="0.4"/>
                <rect x="200" y="60" width="50" height="30" fill="#60a5fa" opacity="0.3"/>
                <rect x="300" y="60" width="50" height="30" fill="#34d399" opacity="0.5"/>
                <rect x="400" y="60" width="50" height="30" fill="#fbbf24" opacity="0.4"/>
                <rect x="500" y="60" width="50" height="30" fill="#f87171" opacity="0.6"/>
                <rect x="600" y="60" width="50" height="30" fill="#2dd4bf" opacity="0.4"/>

                <rect x="100" y="120" width="50" height="30" fill="#60a5fa" opacity="0.3"/>
                <rect x="200" y="120" width="50" height="30" fill="#f87171" opacity="0.5"/>
                <rect x="300" y="120" width="50" height="30" fill="#fbbf24" opacity="0.4"/>
                <rect x="400" y="120" width="50" height="30" fill="#34d399" opacity="0.6"/>
                <rect x="500" y="120" width="50" height="30" fill="#2dd4bf" opacity="0.5"/>
                <rect x="600" y="120" width="50" height="30" fill="#60a5fa" opacity="0.4"/>

                <rect x="100" y="180" width="50" height="30" fill="#34d399" opacity="0.4"/>
                <rect x="200" y="180" width="50" height="30" fill="#2dd4bf" opacity="0.5"/>
                <rect x="300" y="180" width="50" height="30" fill="#f87171" opacity="0.6"/>
                <rect x="400" y="180" width="50" height="30" fill="#60a5fa" opacity="0.3"/>
                <rect x="500" y="180" width="50" height="30" fill="#fbbf24" opacity="0.4"/>
                <rect x="600" y="180" width="50" height="30" fill="#f87171" opacity="0.5"/>
              </g>

              <!-- Label -->
              <text x="400" y="200" font-family="monospace" font-size="14" fill="#94a3b8" text-anchor="middle" opacity="0.6">
                11개 피처 × 30종목 히트맵
              </text>
            </svg>
            <span class="chart-badge">matplotlib · 11개 피처 × 30종목</span>
          </div>

          <div class="stats-grid">
            <div class="stat-box">
              <div class="stat-label">분석<br>종목</div>
              <div class="stat-value nt">{{ heatmapStats.totalStocks }}</div>
            </div>
            <div class="stat-box">
              <div class="stat-label">매수<br>후보</div>
              <div class="stat-value up">{{ heatmapStats.buyCandidate }}</div>
            </div>
            <div class="stat-box">
              <div class="stat-label">매도<br>후보</div>
              <div class="stat-value dn">{{ heatmapStats.sellCandidate }}</div>
            </div>
            <div class="stat-box">
              <div class="stat-label">중립</div>
              <div class="stat-value neutral">{{ heatmapStats.neutral }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- ② Gemini AI 매매 판단 -->
      <div class="analysis-section">
        <div class="section-label">Gemini AI 매매 판단</div>
        <div class="section-card">
          <!-- Toggle Buttons -->
          <div class="top3-toggle">
            <button
              :class="['toggle-btn', 'buy', { active: activeTop3 === 'buy' }]"
              @click="toggleTop3('buy')"
            >
              ▲ 매수 TOP3
            </button>
            <button
              :class="['toggle-btn', 'sell', { active: activeTop3 === 'sell' }]"
              @click="toggleTop3('sell')"
            >
              ▼ 매도 TOP3
            </button>
          </div>

          <!-- Buy TOP3 Panel -->
          <div v-show="activeTop3 === 'buy'" class="top3-panel">
            <div
              v-for="stock in buyTop3"
              :key="stock.rank"
              class="stock-row"
            >
              <div class="rank-badge buy">{{ stock.rank }}</div>
              <div
                class="stock-icon"
                :style="{ background: stock.iconBg, color: stock.iconColor }"
              >
                {{ stock.icon }}
              </div>
              <div class="stock-info">
                <div class="stock-name">{{ stock.name }}</div>
                <div v-if="stock.reason" class="stock-reason">{{ stock.reason }}</div>
              </div>
              <div :class="['stock-score', stock.scoreClass]">
                {{ stock.score }}
              </div>
            </div>
          </div>

          <!-- Sell TOP3 Panel -->
          <div v-show="activeTop3 === 'sell'" class="top3-panel">
            <div
              v-for="stock in sellTop3"
              :key="stock.rank"
              :class="['stock-row', { empty: stock.isEmpty }]"
            >
              <div class="rank-badge sell">{{ stock.rank }}</div>
              <div
                class="stock-icon"
                :style="{ background: stock.iconBg, color: stock.iconColor }"
              >
                {{ stock.icon }}
              </div>
              <div class="stock-info">
                <div class="stock-name">{{ stock.name }}</div>
                <div v-if="stock.reason" class="stock-reason">{{ stock.reason }}</div>
              </div>
              <div :class="['stock-score', stock.scoreClass]">
                {{ stock.score !== null ? stock.score : '—' }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- ③ 시장 전반 감성분석 -->
      <div class="analysis-section">
        <div class="section-label">시장 전반 감성분석</div>
        <div class="section-card">
          <div class="sentiment-block">
            <!-- Score Row -->
            <div class="sentiment-header">
              <div>
                <div class="sentiment-source">KR-FinBERT · RSS 피드 기반</div>
                <div class="sentiment-main">
                  <span class="sentiment-value">{{ marketSentiment.score > 0 ? '+' : '' }}{{ marketSentiment.score }}</span>
                  <span class="sentiment-label">시장 감성점수</span>
                </div>
              </div>
              <div class="sentiment-badge">{{ marketSentiment.label }}</div>
            </div>

            <div class="sentiment-divider"></div>

            <!-- Distribution Bar -->
            <div class="distribution-section">
              <div class="distribution-header">
                <span>종목 감성 분포 (30종목)</span>
              </div>
              <div class="distribution-bar">
                <div
                  class="bar-segment positive"
                  :style="{ width: marketSentiment.distribution.positive.percent + '%' }"
                ></div>
                <div
                  class="bar-segment neutral"
                  :style="{ width: marketSentiment.distribution.neutral.percent + '%' }"
                ></div>
                <div
                  class="bar-segment negative"
                  :style="{ width: marketSentiment.distribution.negative.percent + '%' }"
                ></div>
              </div>
              <div class="distribution-labels">
                <span class="label-positive">
                  긍정 {{ marketSentiment.distribution.positive.count }}개 ({{ marketSentiment.distribution.positive.percent }}%)
                </span>
                <span class="label-neutral">
                  중립 {{ marketSentiment.distribution.neutral.count }}개 ({{ marketSentiment.distribution.neutral.percent }}%)
                </span>
                <span class="label-negative">
                  부정 {{ marketSentiment.distribution.negative.count }}개 ({{ marketSentiment.distribution.negative.percent }}%)
                </span>
              </div>
            </div>

            <div class="sentiment-divider"></div>

            <!-- Collection Info -->
            <div class="collection-info">
              <div class="info-item">
                <div class="info-label">수집 범위</div>
                <div class="info-value">{{ marketSentiment.timeRange }}</div>
              </div>
              <div class="info-item align-right">
                <div class="info-label">뉴스 출처</div>
                <div class="info-value">{{ marketSentiment.sources }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Spacer for bottom nav -->
    <div class="bottom-spacer"></div>
  </div>
</template>

<style scoped>
.market-analysis-screen {
  min-height: 100vh;
  background: linear-gradient(180deg, #0F172A 0%, #1E293B 100%);
  padding-bottom: var(--bottom-nav-height);
}

/* Header Override */
.market-analysis-screen :deep(.app-header) {
  background: #0F172A;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-meta {
  display: flex;
  align-items: center;
  gap: 6px;
}

.live-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-stock-up);
  animation: pulse-dot 2s infinite;
}

@keyframes pulse-dot {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(0.8);
  }
}

.header-date {
  font-family: 'DM Mono', monospace;
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  background: var(--color-bg-tertiary);
  border: 1px solid var(--color-border);
  padding: 3px 8px;
  border-radius: 6px;
}

.content {
  padding: var(--spacing-md) var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

/* Section Label */
.section-label {
  font-size: 10.5px;
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-tertiary);
  letter-spacing: 0.8px;
  text-transform: uppercase;
  margin-bottom: var(--spacing-sm);
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-label::before {
  content: '';
  display: block;
  width: 2px;
  height: 11px;
  background: var(--color-primary);
  border-radius: 2px;
}

/* Section Card */
.section-card {
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  overflow: hidden;
}

/* Chart Container */
.chart-container {
  position: relative;
  padding: var(--spacing-md);
}

.chart-placeholder {
  width: 100%;
  display: block;
  border-radius: var(--radius-md);
  background: #141b2b;
  min-height: 180px;
}

.chart-badge {
  position: absolute;
  top: 20px;
  left: 20px;
  font-size: 9px;
  color: var(--color-text-secondary);
  background: rgba(8, 12, 20, 0.8);
  backdrop-filter: blur(8px);
  border: 1px solid var(--color-border);
  padding: 3px 8px;
  border-radius: 6px;
  font-family: 'DM Mono', monospace;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 6px;
  padding: var(--spacing-md);
  padding-top: var(--spacing-sm);
}

.stat-box {
  background: var(--color-bg-tertiary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  padding: 8px 4px;
  text-align: center;
}

.stat-label {
  font-size: 8px;
  color: var(--color-text-tertiary);
  margin-bottom: 4px;
  line-height: 1.4;
}

.stat-value {
  font-size: 13px;
  font-weight: var(--font-weight-bold);
  font-family: 'DM Mono', monospace;
}

.stat-value.up { color: #f87171; }
.stat-value.dn { color: #60a5fa; }
.stat-value.nt { color: #2dd4bf; }
.stat-value.neutral { color: var(--color-text-secondary); }

/* TOP3 Toggle */
.top3-toggle {
  display: flex;
  gap: 6px;
  padding: var(--spacing-md) var(--spacing-md) 0;
}

.toggle-btn {
  flex: 1;
  text-align: center;
  padding: 7px;
  border-radius: var(--radius-md);
  font-size: 11.5px;
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all 0.15s;
  border: 1px solid transparent;
}

.toggle-btn.buy {
  background: rgba(248, 113, 113, 0.1);
  color: #f87171;
  border-color: rgba(248, 113, 113, 0.15);
}

.toggle-btn.sell {
  background: rgba(96, 165, 250, 0.1);
  color: #60a5fa;
  border-color: rgba(96, 165, 250, 0.15);
}

.toggle-btn.buy.active {
  background: rgba(248, 113, 113, 0.2);
  border-color: rgba(248, 113, 113, 0.4);
  box-shadow: 0 0 12px rgba(248, 113, 113, 0.15);
}

.toggle-btn.sell.active {
  background: rgba(96, 165, 250, 0.2);
  border-color: rgba(96, 165, 250, 0.4);
  box-shadow: 0 0 12px rgba(96, 165, 250, 0.15);
}

/* TOP3 Panel */
.top3-panel {
  padding: var(--spacing-sm) var(--spacing-md) var(--spacing-md);
}

.stock-row {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: 9px 0;
  border-bottom: 1px solid var(--color-border);
  transition: opacity 0.2s;
}

.stock-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.stock-row.empty {
  opacity: 0.3;
}

.rank-badge {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: var(--font-weight-bold);
  flex-shrink: 0;
  font-family: 'DM Mono', monospace;
}

.rank-badge.buy {
  background: rgba(248, 113, 113, 0.15);
  color: #f87171;
}

.rank-badge.sell {
  background: rgba(96, 165, 250, 0.15);
  color: #60a5fa;
}

.stock-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  font-weight: var(--font-weight-bold);
  flex-shrink: 0;
}

.stock-info {
  flex: 1;
  min-width: 0;
}

.stock-name {
  font-size: 12.5px;
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.stock-reason {
  font-size: 10px;
  color: var(--color-text-secondary);
  margin-top: 2px;
  line-height: 1.35;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stock-score {
  font-size: 13px;
  font-weight: var(--font-weight-bold);
  flex-shrink: 0;
  font-family: 'DM Mono', monospace;
}

.stock-score.up { color: #f87171; }
.stock-score.dn { color: #60a5fa; }
.stock-score.yw { color: #fbbf24; }
.stock-score.nt { color: #2dd4bf; }
.stock-score.empty { color: var(--color-text-tertiary); }

/* Sentiment Block */
.sentiment-block {
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.sentiment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.sentiment-source {
  font-size: 9px;
  color: var(--color-text-tertiary);
  margin-bottom: 4px;
}

.sentiment-main {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.sentiment-value {
  font-size: 28px;
  font-weight: var(--font-weight-bold);
  font-family: 'DM Mono', monospace;
  color: #f87171;
}

.sentiment-label {
  font-size: 11px;
  color: var(--color-text-secondary);
}

.sentiment-badge {
  font-size: 10px;
  background: rgba(52, 211, 153, 0.1);
  color: var(--color-stock-up);
  border: 1px solid rgba(52, 211, 153, 0.2);
  padding: 3px 10px;
  border-radius: 20px;
}

.sentiment-divider {
  height: 1px;
  background: var(--color-border);
  margin: 2px 0;
}

/* Distribution */
.distribution-section {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.distribution-header {
  font-size: 9px;
  color: var(--color-text-tertiary);
}

.distribution-bar {
  height: 6px;
  background: var(--color-bg-tertiary);
  border-radius: 3px;
  overflow: hidden;
  display: flex;
}

.bar-segment {
  height: 6px;
}

.bar-segment.positive {
  background: #f87171;
  border-radius: 3px 0 0 3px;
}

.bar-segment.neutral {
  background: #4a5568;
}

.bar-segment.negative {
  background: #60a5fa;
  border-radius: 0 3px 3px 0;
}

.distribution-labels {
  display: flex;
  justify-content: space-between;
  font-size: 9px;
  font-family: 'DM Mono', monospace;
  gap: var(--spacing-xs);
}

.label-positive { color: #f87171; }
.label-neutral { color: var(--color-text-tertiary); }
.label-negative { color: #60a5fa; }

/* Collection Info */
.collection-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--spacing-md);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.info-item.align-right {
  text-align: right;
}

.info-label {
  font-size: 9px;
  color: var(--color-text-tertiary);
}

.info-value {
  font-size: 11px;
  color: var(--color-text-secondary);
}

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
