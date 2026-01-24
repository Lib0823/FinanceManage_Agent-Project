<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import AssetTabs from '@/components/common/AssetTabs.vue'
import StockCard from '@/components/common/StockCard.vue'
import { mockStockDetail, mockStocks } from '@/services/mockData'

const router = useRouter()

const tabs = ref({ main: 'stocks', sub: 'overseas' })
const stockDetail = ref(mockStockDetail)
const stocks = ref(mockStocks)

const formatNumber = (num) => {
  return new Intl.NumberFormat('ko-KR').format(num)
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
  <div class="asset-detail-screen">
    <AppHeader title="자산 상세 정보" showBack />

    <div class="content">
      <!-- Tabs -->
      <AssetTabs v-model="tabs" />

      <!-- Summary Card -->
      <div class="summary-card">
        <h3 class="summary-title">해외 주식 추정 자산</h3>
        <div class="summary-grid">
          <div class="summary-item">
            <span class="summary-label">D+2예수금</span>
            <span class="summary-value">{{ formatNumber(stockDetail.overseas.d2Deposit) }}원</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">총평가손익</span>
            <span class="summary-value positive">{{ formatNumber(stockDetail.overseas.totalProfit) }}원</span>
          </div>
          <div class="profit-badge">
            <span class="profit-percent">▲ {{ stockDetail.overseas.profitPercent }}%</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">총매입금액</span>
            <span class="summary-value">{{ formatNumber(stockDetail.overseas.totalPurchase) }}원</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">총평가금액</span>
            <span class="summary-value">{{ formatNumber(stockDetail.overseas.totalValuation) }}원</span>
          </div>
        </div>
      </div>

      <!-- Stock List -->
      <div class="stock-list">
        <StockCard
          v-for="stock in stocks"
          :key="stock.symbol"
          :stock="stock"
          @news="goToNews"
          @trade="goToTrade"
          @info="goToInfo"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.asset-detail-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
}

.content {
  padding: 0 var(--spacing-lg) var(--spacing-lg);
}

.summary-card {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.summary-title {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  text-align: center;
  margin-bottom: var(--spacing-md);
}

.summary-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
  position: relative;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.summary-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.summary-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.summary-value.positive {
  color: var(--color-stock-up);
}

.profit-badge {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #FEF3C7;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
}

.profit-percent {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-stock-up);
}

.stock-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}
</style>
