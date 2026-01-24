<script setup>
import { ref } from 'vue'
import AppHeader from '@/components/common/AppHeader.vue'
import AssetTabs from '@/components/common/AssetTabs.vue'
import { mockTradingOrders, mockTradingHistory, mockTransactionSummary } from '@/services/mockData'

const tabs = ref({ main: 'stocks', sub: 'domestic' })
const orders = ref(mockTradingOrders)
const history = ref(mockTradingHistory)
const summary = ref(mockTransactionSummary)

const selectedDate = ref(new Date())
const dateRange = ref({
  start: '2024.08.19',
  end: '2024.11.09'
})

const formatNumber = (num) => {
  return new Intl.NumberFormat('ko-KR').format(num)
}

const getTypeClass = (type) => {
  switch (type) {
    case 'buy': return 'buy'
    case 'sell': return 'sell'
    case 'dividend': return 'dividend'
    default: return ''
  }
}

const getTypeLabel = (type) => {
  switch (type) {
    case 'buy': return '매수'
    case 'sell': return '매도'
    case 'dividend': return '배당'
    default: return ''
  }
}
</script>

<template>
  <div class="transactions-screen">
    <AppHeader title="거래 내역" showIcon icon="news" />

    <div class="content">
      <!-- Tabs -->
      <AssetTabs v-model="tabs" />

      <!-- Pending/Reserved Orders -->
      <section class="section">
        <h3 class="section-title">미체결 / 예약 주문</h3>
        <div class="order-list">
          <div v-for="(order, idx) in orders.reserved" :key="'res-'+idx" class="order-item">
            <span class="order-label">{{ idx === 0 ? '미체결' : '예약' }}</span>
            <span :class="['order-type', order.type]">{{ getTypeLabel(order.type) }}</span>
            <span class="order-name">{{ order.name }}</span>
            <span class="order-price">{{ formatNumber(order.price) }}{{ order.currency }}</span>
          </div>
        </div>
      </section>

      <!-- Date Range Picker -->
      <section class="section">
        <h3 class="section-title">기간 거래 내역</h3>

        <!-- Calendar -->
        <div class="calendar-container">
          <VDatePicker v-model="selectedDate" mode="date" />
        </div>

        <div class="date-range">
          {{ dateRange.start }} - {{ dateRange.end }}
        </div>
      </section>

      <!-- Summary -->
      <div class="summary-container">
        <div class="summary-card">
          <div class="summary-item">
            <span class="summary-type buy">매수</span>
            <span class="summary-amount">{{ formatNumber(summary.buy.amount) }}</span>
            <span class="summary-detail">{{ summary.buy.name }} ({{ summary.buy.symbol }})</span>
          </div>
          <div class="summary-item">
            <span class="summary-type sell">매도</span>
            <span class="summary-amount">{{ formatNumber(summary.sell.amount) }}</span>
            <span class="summary-detail">{{ summary.sell.name }} ({{ summary.sell.symbol }})</span>
          </div>
          <div class="summary-item">
            <span class="summary-type other">기타</span>
            <span class="summary-amount">+{{ formatNumber(summary.other.amount) }}</span>
            <span class="summary-detail">{{ summary.other.label }}</span>
          </div>
        </div>
      </div>

      <!-- History List -->
      <div class="history-list">
        <div v-for="(item, idx) in history" :key="idx" class="history-item">
          <span :class="['history-type', item.type]">{{ getTypeLabel(item.type) }}</span>
          <span class="history-name">{{ item.name || item.label }}</span>
          <span class="history-amount">{{ formatNumber(item.amount) }}{{ item.currency }}</span>
        </div>
      </div>
    </div>

    <!-- Spacer for bottom nav -->
    <div class="bottom-spacer"></div>
  </div>
</template>

<style scoped>
.transactions-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
  padding-bottom: var(--bottom-nav-height);
}

.content {
  padding: 0 var(--spacing-lg) var(--spacing-lg);
}

.section {
  margin-bottom: var(--spacing-lg);
}

.section-title {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-md);
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.order-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
}

.order-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  width: 50px;
}

.order-type {
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.order-type.sell {
  background: var(--color-secondary);
  color: var(--color-text-inverse);
}

.order-type.buy {
  background: #F97316;
  color: var(--color-text-inverse);
}

.order-name {
  flex: 1;
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
}

.order-price {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.calendar-container {
  margin-bottom: var(--spacing-md);
}

.date-range {
  text-align: center;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  padding: var(--spacing-sm);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
}

.summary-container {
  background: var(--color-bg-accent);
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.summary-card {
  display: flex;
  justify-content: space-between;
}

.summary-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
}

.summary-type {
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.summary-type.buy {
  background: #F97316;
  color: var(--color-text-inverse);
}

.summary-type.sell {
  background: var(--color-secondary);
  color: var(--color-text-inverse);
}

.summary-type.other {
  background: var(--color-bg-tertiary);
  color: var(--color-text-secondary);
}

.summary-amount {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-inverse);
}

.summary-detail {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.7);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.history-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
}

.history-type {
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

.history-type.sell {
  background: var(--color-secondary);
  color: var(--color-text-inverse);
}

.history-type.buy {
  background: #F97316;
  color: var(--color-text-inverse);
}

.history-type.dividend {
  background: var(--color-bg-tertiary);
  color: var(--color-text-secondary);
}

.history-name {
  flex: 1;
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
}

.history-amount {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
