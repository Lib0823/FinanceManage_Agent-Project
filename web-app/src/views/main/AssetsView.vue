<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import { Doughnut, Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  LinearScale,
  CategoryScale
} from 'chart.js'
import { mockAssetSummary } from '@/services/mockData'

ChartJS.register(ArcElement, Tooltip, Legend, LineElement, PointElement, LinearScale, CategoryScale)

const router = useRouter()

const assetSummary = ref(mockAssetSummary)

// 7일간 자산 추이 데이터 (Mock)
const assetTrendData = ref({
  labels: ['월', '화', '수', '목', '금', '토', '일'],
  datasets: [{
    label: '총 자산',
    data: [98000000, 98500000, 99200000, 99800000, 99500000, 100200000, 100000000],
    borderColor: '#3B82F6',
    backgroundColor: 'rgba(59, 130, 246, 0.1)',
    tension: 0.4,
    fill: true,
    pointRadius: 3,
    pointHoverRadius: 5
  }]
})

const formatNumber = (num) => {
  return new Intl.NumberFormat('ko-KR').format(num)
}

const formatChange = (change) => {
  const sign = change >= 0 ? '+' : ''
  return `${sign}${formatNumber(change)}`
}

const formatPercent = (percent) => {
  const sign = percent >= 0 ? '+' : ''
  return `${sign}${(percent * 100).toFixed(2)}%`
}

const calculatePercentage = (amount, total) => {
  return ((amount / total) * 100).toFixed(1)
}

const pieChartData = computed(() => ({
  labels: ['현금', '주식', '채권', '코인'],
  datasets: [{
    data: [
      assetSummary.value.breakdown.cash.amount,
      assetSummary.value.breakdown.stocks.amount,
      assetSummary.value.breakdown.bonds.amount,
      assetSummary.value.breakdown.coins.amount
    ],
    backgroundColor: ['#3B82F6', '#F97316', '#10B981', '#A855F7'],
    borderWidth: 0,
    hoverOffset: 4
  }]
}))

const pieChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          const label = context.label || ''
          const value = formatNumber(context.parsed)
          const percentage = calculatePercentage(context.parsed, assetSummary.value.totalAsset)
          return `${label}: ${value}원 (${percentage}%)`
        }
      }
    }
  },
  cutout: '70%'
}

const lineChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          return `${formatNumber(context.parsed.y)}원`
        }
      }
    }
  },
  scales: {
    y: {
      display: false,
      grid: {
        display: false
      }
    },
    x: {
      display: true,
      grid: {
        display: false
      },
      ticks: {
        font: {
          size: 10
        },
        color: '#9CA3AF'
      }
    }
  }
}

const assetColors = {
  cash: '#3B82F6',
  stocks: '#F97316',
  bonds: '#10B981',
  coins: '#A855F7'
}

const assetIcons = {
  cash: '💰',
  stocks: '📈',
  bonds: '📊',
  coins: '🪙'
}

const goToDetail = (type) => {
  router.push({
    path: '/assets/detail',
    query: {
      main: type,
      sub: type === 'stocks' ? 'overseas' : undefined
    }
  })
}

const handleRefresh = () => {
  // TODO: 실제 자산 정보 새로고침 API 호출
  console.log('자산 정보 새로고침')
  // 임시로 현재 시간으로 업데이트
  const now = new Date()
  assetSummary.value.updatedAt = now.toISOString().slice(0, 19).replace('T', ' ')
}
</script>

<template>
  <div class="assets-screen">
    <AppHeader title="자산 정보" showIcon icon="assets" />

    <div class="header-actions">
      <span class="update-time">기준 일시: {{ assetSummary.updatedAt }}</span>
      <button class="refresh-button" @click="handleRefresh">
        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M21.5 2v6h-6M2.5 22v-6h6M2 11.5a10 10 0 0 1 18.8-4.3M22 12.5a10 10 0 0 1-18.8 4.2"/>
        </svg>
      </button>
    </div>

    <div class="content">
      <!-- Total Asset Summary -->
      <section class="total-section">
        <div class="total-header">
          <h2 class="total-label">총 자산</h2>
          <div class="total-badge">
            <span :class="['badge-change', assetSummary.totalChange >= 0 ? 'positive' : 'negative']">
              {{ formatChange(assetSummary.totalChange) }}
            </span>
            <span :class="['badge-percent', assetSummary.changePercent >= 0 ? 'positive' : 'negative']">
              {{ formatPercent(assetSummary.changePercent) }}
            </span>
          </div>
        </div>
        <div class="total-value">{{ formatNumber(assetSummary.totalAsset) }}<span class="currency">원</span></div>

        <!-- Asset Distribution -->
        <div class="distribution-section">
          <div class="pie-chart-container">
            <div class="pie-chart">
              <Doughnut :data="pieChartData" :options="pieChartOptions" />
              <div class="chart-center">
                <div class="center-label">총 자산</div>
                <div class="center-value">100%</div>
              </div>
            </div>
          </div>

          <div class="legend-list">
            <div class="legend-item" v-for="(item, key) in assetSummary.breakdown" :key="key">
              <div class="legend-color" :style="{ backgroundColor: assetColors[key] }"></div>
              <div class="legend-info">
                <span class="legend-name">{{ key === 'cash' ? '현금' : key === 'stocks' ? '주식' : key === 'bonds' ? '채권' : '코인' }}</span>
                <span class="legend-percent">{{ calculatePercentage(item.amount, assetSummary.totalAsset) }}%</span>
              </div>
              <span class="legend-value">{{ formatNumber(item.amount) }}원</span>
            </div>
          </div>
        </div>

        <!-- 7-Day Trend Chart -->
        <div class="trend-section">
          <h3 class="section-title">최근 7일 자산 추이</h3>
          <div class="line-chart">
            <Line :data="assetTrendData" :options="lineChartOptions" />
          </div>
        </div>
      </section>

      <!-- Asset Cards -->
      <div class="asset-cards">
        <!-- Cash -->
        <section class="asset-card cash" @click="goToDetail('cash')">
          <div class="card-header">
            <div class="card-icon">{{ assetIcons.cash }}</div>
            <div class="card-title-group">
              <h3 class="card-title">현금</h3>
              <span class="card-percentage">{{ calculatePercentage(assetSummary.breakdown.cash.amount, assetSummary.totalAsset) }}%</span>
            </div>
            <div class="card-arrow">→</div>
          </div>

          <div class="card-body">
            <div class="card-value-section">
              <div class="value-label">보유 금액</div>
              <div class="value-amount">{{ formatNumber(assetSummary.breakdown.cash.amount) }}<span class="unit">원</span></div>
            </div>

            <div class="card-stats">
              <div class="stat-item">
                <span class="stat-label">전일 대비</span>
                <span :class="['stat-value', assetSummary.breakdown.cash.change >= 0 ? 'positive' : 'negative']">
                  {{ formatChange(assetSummary.breakdown.cash.change) }}
                </span>
              </div>
              <div class="stat-item">
                <span class="stat-label">변동률</span>
                <span :class="['stat-value', assetSummary.breakdown.cash.changePercent >= 0 ? 'positive' : 'negative']">
                  {{ formatPercent(assetSummary.breakdown.cash.changePercent) }}
                </span>
              </div>
            </div>
          </div>

          <div class="card-indicator" :style="{ backgroundColor: assetColors.cash }"></div>
        </section>

        <!-- Stocks -->
        <section class="asset-card stocks" @click="goToDetail('stocks')">
          <div class="card-header">
            <div class="card-icon">{{ assetIcons.stocks }}</div>
            <div class="card-title-group">
              <h3 class="card-title">주식</h3>
              <span class="card-percentage">{{ calculatePercentage(assetSummary.breakdown.stocks.amount, assetSummary.totalAsset) }}%</span>
            </div>
            <div class="card-arrow">→</div>
          </div>

          <div class="card-body">
            <div class="card-value-section">
              <div class="value-label">평가 금액</div>
              <div class="value-amount">{{ formatNumber(assetSummary.breakdown.stocks.amount) }}<span class="unit">원</span></div>
            </div>

            <div class="card-stats">
              <div class="stat-item">
                <span class="stat-label">평가 손익</span>
                <span :class="['stat-value', assetSummary.breakdown.stocks.change >= 0 ? 'positive' : 'negative']">
                  {{ formatChange(assetSummary.breakdown.stocks.change) }}
                </span>
              </div>
              <div class="stat-item">
                <span class="stat-label">수익률</span>
                <span :class="['stat-value', assetSummary.breakdown.stocks.changePercent >= 0 ? 'positive' : 'negative']">
                  {{ formatPercent(assetSummary.breakdown.stocks.changePercent) }}
                </span>
              </div>
            </div>
          </div>

          <div class="card-indicator" :style="{ backgroundColor: assetColors.stocks }"></div>
        </section>
      </div>

      <!-- Bonds (Disabled) -->
      <section class="asset-card disabled">
        <div class="disabled-text">채권 (비활성)</div>
      </section>

      <!-- Coins (Disabled) -->
      <section class="asset-card disabled">
        <div class="disabled-text">코인 (비활성)</div>
      </section>
    </div>

    <!-- Spacer for bottom nav -->
    <div class="bottom-spacer"></div>
  </div>
</template>

<style scoped>
.assets-screen {
  min-height: 100vh;
  background: linear-gradient(180deg, #0F172A 0%, #1E293B 100%);
  padding-bottom: var(--bottom-nav-height);
}

/* Header Override */
.assets-screen :deep(.app-header) {
  background: #0F172A;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.content {
  padding: var(--spacing-lg);
}

/* Header Actions */
.header-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 5px 14px 1px;
}

.refresh-button {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  background: transparent;
  border: none;
  padding: 0;
  color: #94A3B8;
  cursor: pointer;
  transition: all 0.3s;
}

.refresh-button:hover {
  color: #F1F5F9;
  transform: rotate(180deg);
}

.refresh-button:active {
  transform: rotate(180deg) scale(0.9);
}

.update-time {
  font-size: 11px;
  color: #94A3B8;
  font-weight: var(--font-weight-medium);
  white-space: nowrap;
}

.total-section {
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  border-radius: 24px;
  padding: 24px;
  margin-bottom: var(--spacing-xl);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.total-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.total-label {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: #F1F5F9;
}

.total-badge {
  display: flex;
  gap: 8px;
  align-items: center;
}

.badge-change, .badge-percent {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  padding: 4px 12px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
}

.badge-change.positive, .badge-percent.positive {
  color: #10B981;
  background: rgba(16, 185, 129, 0.1);
}

.badge-change.negative, .badge-percent.negative {
  color: #EF4444;
  background: rgba(239, 68, 68, 0.1);
}

.total-value {
  font-size: 36px;
  font-weight: var(--font-weight-bold);
  color: #F1F5F9;
  margin-bottom: 24px;
  letter-spacing: -0.02em;
}

.currency {
  font-size: 24px;
  font-weight: var(--font-weight-normal);
  color: #94A3B8;
  margin-left: 4px;
}

/* Distribution Section */
.distribution-section {
  margin-bottom: 24px;
}

.pie-chart-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.pie-chart {
  position: relative;
  width: 160px;
  height: 160px;
}

.chart-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.center-label {
  font-size: 12px;
  color: #94A3B8;
  margin-bottom: 4px;
}

.center-value {
  font-size: 20px;
  font-weight: var(--font-weight-bold);
  color: #F1F5F9;
}

.legend-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.legend-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-name {
  font-size: 14px;
  color: #F1F5F9;
  font-weight: var(--font-weight-medium);
}

.legend-percent {
  font-size: 12px;
  color: #94A3B8;
  background: rgba(255, 255, 255, 0.05);
  padding: 2px 8px;
  border-radius: 8px;
}

.legend-value {
  font-size: 14px;
  color: #F1F5F9;
  font-weight: var(--font-weight-semibold);
}

/* Trend Section */
.trend-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
  font-size: 16px;
  font-weight: var(--font-weight-semibold);
  color: #F1F5F9;
  margin-bottom: 16px;
}

.line-chart {
  height: 120px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
}

/* Asset Cards */
.asset-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.asset-card {
  position: relative;
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  border-radius: 20px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.asset-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, transparent 0%, rgba(255, 255, 255, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s;
}

.asset-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.asset-card:hover::before {
  opacity: 1;
}

.asset-card.disabled {
  background: rgba(30, 41, 59, 0.5);
  cursor: default;
  display: flex;
  margin-top: 10px;
  align-items: center;
  justify-content: center;
  min-height: 80px;
}

.asset-card.disabled:hover {
  transform: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.disabled-text {
  font-size: var(--font-size-base);
  color: #64748B;
  font-weight: var(--font-weight-medium);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.card-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.card-title-group {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title {
  font-size: 18px;
  font-weight: var(--font-weight-bold);
  color: #F1F5F9;
}

.card-percentage {
  font-size: 12px;
  color: #94A3B8;
  background: rgba(255, 255, 255, 0.08);
  padding: 4px 10px;
  border-radius: 10px;
  font-weight: var(--font-weight-semibold);
}

.card-arrow {
  font-size: 20px;
  color: #64748B;
  transition: transform 0.3s;
}

.asset-card:hover .card-arrow {
  transform: translateX(4px);
  color: #94A3B8;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-value-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.value-label {
  font-size: 12px;
  color: #94A3B8;
  font-weight: var(--font-weight-medium);
}

.value-amount {
  font-size: 28px;
  font-weight: var(--font-weight-bold);
  color: #F1F5F9;
  letter-spacing: -0.02em;
}

.unit {
  font-size: 18px;
  color: #94A3B8;
  margin-left: 4px;
}

.card-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 12px;
}

.stat-label {
  font-size: 11px;
  color: #94A3B8;
  font-weight: var(--font-weight-medium);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.stat-value {
  font-size: 16px;
  font-weight: var(--font-weight-bold);
}

.stat-value.positive {
  color: #10B981;
}

.stat-value.negative {
  color: #EF4444;
}

.card-indicator {
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  border-radius: 20px 0 0 20px;
}

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
