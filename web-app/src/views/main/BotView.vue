<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import AssetTabs from '@/components/common/AssetTabs.vue'
import StockCard from '@/components/common/StockCard.vue'
import { mockBotStatus, mockBotAnalysis, mockStocks } from '@/services/mockData'

const router = useRouter()

const tabs = ref({ main: 'stocks', sub: 'domestic' })
const botEnabled = ref(mockBotStatus.enabled)
const botStatus = ref(mockBotStatus)
const analysis = ref(mockBotAnalysis)
const currentStock = ref(mockStocks[0])

const formatNumber = (num) => {
  return new Intl.NumberFormat('ko-KR').format(num)
}

const toggleBot = () => {
  botEnabled.value = !botEnabled.value
}

const goToNews = (stock) => {
  router.push(`/news?symbol=${stock.symbol}`)
}

const goToTrade = (stock) => {
  router.push(`/trading/${stock.symbol}`)
}

const goToInfo = (stock) => {
  router.push(`/company/${stock.symbol}`)
}
</script>

<template>
  <div class="bot-screen">
    <AppHeader title="투자 봇" showIcon icon="bot" />

    <div class="content">
      <!-- Tabs -->
      <AssetTabs v-model="tabs" />

      <!-- Bot Toggle -->
      <div class="bot-toggle">
        <label class="toggle-wrapper">
          <input type="checkbox" v-model="botEnabled" @change="toggleBot" />
          <span class="toggle-slider"></span>
        </label>
        <span class="toggle-label">일하는 중</span>
      </div>

      <!-- Bot Status -->
      <div class="bot-status">
        <div class="bot-avatar">
          <img src="https://api.dicebear.com/7.x/bottts/svg?seed=finance" alt="Bot Avatar" />
        </div>
        <div class="status-info">
          <span class="status-label">전체 평가금액</span>
          <div class="status-value">
            <span class="amount">{{ formatNumber(botStatus.totalValuation) }}</span>
            <span class="percent">({{ botStatus.profitPercent }}%)</span>
          </div>
        </div>
      </div>

      <!-- Investment Info -->
      <div class="investment-row">
        <span class="investment-label">투자 금액</span>
        <span class="investment-value">{{ formatNumber(botStatus.totalInvestment) }}원</span>
        <button class="change-btn">변경</button>
      </div>

      <!-- Current Stock Card -->
      <StockCard
        :stock="currentStock"
        @news="goToNews"
        @trade="goToTrade"
        @info="goToInfo"
      />

      <!-- Analysis Section -->
      <div class="analysis-section">
        <h3 class="analysis-title">분석 정보</h3>
        <div class="analysis-card">
          <div class="analysis-header">
            <span class="company-name">{{ analysis.name }}</span>
            <span class="company-ticker">({{ analysis.symbol }})</span>
          </div>
          <div class="analysis-points">
            <div v-for="(point, index) in analysis.points" :key="index" class="analysis-point">
              <h4 class="point-title">• {{ point.title }}:</h4>
              <p class="point-content">{{ point.content }}</p>
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
.bot-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
  padding-bottom: var(--bottom-nav-height);
}

.content {
  padding: 0 var(--spacing-lg) var(--spacing-lg);
}

.bot-toggle {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.toggle-wrapper {
  position: relative;
  width: 50px;
  height: 28px;
}

.toggle-wrapper input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--color-bg-tertiary);
  border-radius: var(--radius-full);
  transition: 0.3s;
}

.toggle-slider::before {
  position: absolute;
  content: "";
  height: 22px;
  width: 22px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  border-radius: 50%;
  transition: 0.3s;
}

.toggle-wrapper input:checked + .toggle-slider {
  background-color: var(--color-success);
}

.toggle-wrapper input:checked + .toggle-slider::before {
  transform: translateX(22px);
}

.toggle-label {
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.bot-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.bot-avatar {
  width: 120px;
  height: 120px;
  border-radius: var(--radius-xl);
  overflow: hidden;
  background: var(--color-bg-highlight);
}

.bot-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-info {
  text-align: center;
}

.status-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.status-value {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-xs);
  justify-content: center;
}

.amount {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-primary);
}

.percent {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}

.investment-row {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.investment-label {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
}

.investment-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.change-btn {
  margin-left: auto;
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--color-primary);
  color: var(--color-text-inverse);
  border: none;
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  cursor: pointer;
}

.analysis-section {
  margin-top: var(--spacing-lg);
}

.analysis-title {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  text-align: center;
  margin-bottom: var(--spacing-md);
}

.analysis-card {
  background: #FEF3C7;
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
}

.analysis-header {
  margin-bottom: var(--spacing-md);
}

.company-name {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.company-ticker {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.analysis-points {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.analysis-point {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.point-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-primary);
}

.point-content {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
