<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import { Doughnut, Bar } from 'vue-chartjs'
import { mockAssetSummary } from '@/services/mockData'

const router = useRouter()

const assetSummary = ref(mockAssetSummary)

const formatNumber = (num) => {
  return new Intl.NumberFormat('ko-KR').format(num)
}

const formatChange = (change) => {
  const sign = change >= 0 ? '+' : ''
  return `(${sign}${formatNumber(change)})`
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
    backgroundColor: ['#3B82F6', '#EF4444', '#10B981', '#F59E0B'],
    borderWidth: 0
  }]
}))

const pieChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    }
  },
  cutout: '60%'
}

const goToDetail = (type) => {
  router.push({ path: '/assets/detail', query: { type } })
}
</script>

<template>
  <div class="assets-screen">
    <AppHeader title="자산 정보" showIcon icon="assets">
      <template #right>
        <span class="update-time">기준 일시: {{ assetSummary.updatedAt }}</span>
      </template>
    </AppHeader>

    <div class="content">
      <!-- Total Asset -->
      <section class="total-section">
        <div class="total-header">
          <h2 class="total-label">총 자산</h2>
          <span :class="['total-change', assetSummary.totalChange >= 0 ? 'positive' : 'negative']">
            {{ formatChange(assetSummary.totalChange) }}
          </span>
        </div>
        <div class="total-value">{{ formatNumber(assetSummary.totalAsset) }}원</div>
        <div class="chart-row">
          <div class="pie-chart">
            <Doughnut :data="pieChartData" :options="pieChartOptions" />
          </div>
          <div class="bar-chart">
            <div class="chart-placeholder">
              <div class="bar-item" style="height: 60%"></div>
              <div class="bar-item" style="height: 80%"></div>
              <div class="bar-item" style="height: 40%"></div>
              <div class="bar-item" style="height: 90%"></div>
              <div class="bar-item" style="height: 70%"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- Cash -->
      <section class="asset-card" @click="goToDetail('cash')">
        <div class="card-header">
          <h3 class="card-title">현금</h3>
          <span :class="['card-change', assetSummary.breakdown.cash.change >= 0 ? 'positive' : 'negative']">
            {{ formatChange(assetSummary.breakdown.cash.change) }}
          </span>
          <span class="card-value">{{ formatNumber(assetSummary.breakdown.cash.amount) }}원</span>
        </div>
        <div class="chart-row">
          <div class="pie-chart small">
            <Doughnut :data="pieChartData" :options="pieChartOptions" />
          </div>
          <div class="bar-chart">
            <div class="chart-placeholder">
              <div class="bar-group">
                <div class="bar blue" style="height: 60%"></div>
                <div class="bar red" style="height: 40%"></div>
              </div>
              <div class="bar-group">
                <div class="bar blue" style="height: 80%"></div>
                <div class="bar red" style="height: 50%"></div>
              </div>
              <div class="bar-group">
                <div class="bar blue" style="height: 40%"></div>
                <div class="bar red" style="height: 70%"></div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Stocks -->
      <section class="asset-card" @click="goToDetail('stocks')">
        <div class="card-header">
          <h3 class="card-title">주식</h3>
          <span :class="['card-change', assetSummary.breakdown.stocks.change >= 0 ? 'positive' : 'negative']">
            {{ formatChange(assetSummary.breakdown.stocks.change) }}
          </span>
          <span class="card-value">{{ formatNumber(assetSummary.breakdown.stocks.amount) }}원</span>
        </div>
        <div class="chart-row">
          <div class="pie-chart small">
            <Doughnut :data="pieChartData" :options="pieChartOptions" />
          </div>
          <div class="bar-chart">
            <div class="chart-placeholder">
              <div class="bar-group">
                <div class="bar blue" style="height: 50%"></div>
                <div class="bar red" style="height: 60%"></div>
              </div>
              <div class="bar-group">
                <div class="bar blue" style="height: 70%"></div>
                <div class="bar red" style="height: 45%"></div>
              </div>
              <div class="bar-group">
                <div class="bar blue" style="height: 55%"></div>
                <div class="bar red" style="height: 80%"></div>
              </div>
            </div>
          </div>
        </div>
      </section>

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
  background: var(--color-bg-primary);
  padding-bottom: var(--bottom-nav-height);
}

.update-time {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.content {
  padding: var(--spacing-lg);
}

.total-section {
  margin-bottom: var(--spacing-lg);
}

.total-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-xs);
}

.total-label {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.total-change {
  font-size: var(--font-size-sm);
}

.total-change.positive {
  color: var(--color-stock-up);
}

.total-change.negative {
  color: var(--color-stock-down);
}

.total-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
}

.chart-row {
  display: flex;
  gap: var(--spacing-lg);
  align-items: center;
}

.pie-chart {
  width: 100px;
  height: 100px;
}

.pie-chart.small {
  width: 80px;
  height: 80px;
}

.bar-chart {
  flex: 1;
}

.chart-placeholder {
  display: flex;
  gap: var(--spacing-md);
  align-items: flex-end;
  height: 80px;
}

.bar-item {
  flex: 1;
  background: linear-gradient(180deg, var(--color-primary) 0%, var(--color-primary-light) 100%);
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
}

.bar-group {
  flex: 1;
  display: flex;
  gap: 2px;
  align-items: flex-end;
  height: 100%;
}

.bar {
  flex: 1;
  border-radius: var(--radius-sm) var(--radius-sm) 0 0;
}

.bar.blue {
  background: #3B82F6;
}

.bar.red {
  background: #EF4444;
}

.asset-card {
  background: var(--color-bg-accent);
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
  cursor: pointer;
  transition: transform 0.2s;
}

.asset-card:hover {
  transform: translateY(-2px);
}

.asset-card.disabled {
  background: var(--color-bg-tertiary);
  cursor: default;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60px;
}

.asset-card.disabled:hover {
  transform: none;
}

.disabled-text {
  font-size: var(--font-size-base);
  color: var(--color-text-tertiary);
}

.card-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
  flex-wrap: wrap;
}

.card-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-inverse);
}

.card-change {
  font-size: var(--font-size-sm);
}

.card-change.positive {
  color: var(--color-stock-up);
}

.card-change.negative {
  color: var(--color-stock-down);
}

.card-value {
  margin-left: auto;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-inverse);
}

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
