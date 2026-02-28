<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import InvestmentTabs from '@/components/common/InvestmentTabs.vue'
import { mockSearchResults } from '@/services/mockData'

const router = useRouter()

const tabs = ref({ main: 'stocks', sub: 'domestic' })
const searchQuery = ref('')
const results = ref(mockSearchResults)

// Mock price data (나중에 API로 대체)
const priceData = {
  currentPrice: 71500,
  change: 500,
  changePercent: 0.70
}

const filteredResults = computed(() => {
  let filtered = results.value

  // 시장 필터링 (국내/해외)
  filtered = filtered.filter(item => item.market === tabs.value.sub)

  // 검색어 필터링
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      item => item.name.toLowerCase().includes(query) || item.symbol.toLowerCase().includes(query)
    )
  }

  return filtered
})

const formatNumber = (num) => {
  return new Intl.NumberFormat('ko-KR').format(num)
}

const toggleFavorite = (item) => {
  item.isFavorite = !item.isFavorite
}

const goToCompany = (item) => {
  router.push(`/company/${item.symbol}`)
}

onMounted(() => {
  // Reset scroll position of results container
  const resultsContainer = document.querySelector('.results-container')
  if (resultsContainer) {
    resultsContainer.scrollTop = 0
  }
})
</script>

<template>
  <div class="search-screen">
    <AppHeader title="종목 검색" showIcon icon="search" />

    <div class="content">
      <!-- Tabs -->
      <InvestmentTabs v-model="tabs" />

      <!-- Search Input -->
      <div class="search-bar">
        <input
          v-model="searchQuery"
          type="text"
          class="search-input"
          placeholder="종목명(종목코드)"
        />
        <button class="search-btn">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="7" stroke="currentColor" stroke-width="2"/>
            <path d="M21 21L16.5 16.5" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
      </div>

      <!-- Results List -->
      <div class="results-container">
        <div v-if="filteredResults.length === 0" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none">
            <circle cx="11" cy="11" r="7" stroke="var(--color-text-tertiary)" stroke-width="2"/>
            <path d="M21 21L16.5 16.5" stroke="var(--color-text-tertiary)" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <p class="empty-text">검색 결과가 없습니다</p>
        </div>

        <div v-else class="results-list">
          <div
            v-for="(item, idx) in filteredResults"
            :key="item.symbol"
            class="result-item"
            @click="goToCompany(item)"
          >
            <div class="item-left">
              <div class="item-thumb">
                <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
                  <rect width="40" height="40" rx="10" :fill="`url(#itemGradient${idx})`"/>
                  <defs>
                    <linearGradient :id="`itemGradient${idx}`" x1="0" y1="0" x2="40" y2="40">
                      <stop offset="0%" :stop-color="idx % 3 === 0 ? '#3B82F6' : idx % 3 === 1 ? '#10B981' : '#F59E0B'"/>
                      <stop offset="100%" :stop-color="idx % 3 === 0 ? '#1E40AF' : idx % 3 === 1 ? '#047857' : '#D97706'"/>
                    </linearGradient>
                  </defs>
                  <text x="20" y="26" font-size="16" font-weight="bold" fill="white" text-anchor="middle">{{ item.name.charAt(0) }}</text>
                </svg>
              </div>
              <div class="item-info">
                <span class="item-name">{{ item.name }}</span>
                <span class="item-symbol">{{ item.symbol }}</span>
              </div>
            </div>
            <div class="item-right">
              <div class="item-price">{{ formatNumber(priceData.currentPrice) }}원</div>
              <div :class="['item-change', priceData.change >= 0 ? 'positive' : 'negative']">
                {{ priceData.change >= 0 ? '+' : '' }}{{ priceData.changePercent }}%
              </div>
            </div>
            <button class="star-btn" @click.stop="toggleFavorite(item)">
              <svg width="18" height="18" viewBox="0 0 24 24" :fill="item.isFavorite ? '#F59E0B' : 'none'" :stroke="item.isFavorite ? 'none' : '#64748B'" stroke-width="2">
                <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.search-screen {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #0F172A 0%, #1E293B 100%);
  overflow: hidden;
}

/* Header Override */
.search-screen :deep(.app-header) {
  background: #0F172A;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
}

.content {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
  padding: 0 var(--spacing-lg);
  padding-bottom: var(--bottom-nav-height);
}

.content :deep(.investment-tabs) {
  flex-shrink: 0;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: 0 var(--spacing-md);
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-lg);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  flex-shrink: 0;
}

.search-btn {
  background: none;
  border: none;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: var(--spacing-sm);
  transition: color 0.2s;
}

.search-btn:hover {
  color: var(--color-text-primary);
}

.search-input {
  flex: 1;
  border: none;
  background: none;
  padding: var(--spacing-sm) 0;
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  outline: none;
}

.search-input::placeholder {
  color: var(--color-text-tertiary);
}

.results-container {
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: var(--spacing-md);
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl);
  gap: var(--spacing-sm);
  flex: 1;
}

.empty-text {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-tertiary);
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-bottom: var(--spacing-lg);
}

.result-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.result-item:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(139, 92, 246, 0.3);
  transform: translateX(2px);
}

.item-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex: 1;
}

.item-thumb {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.item-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.item-symbol {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.item-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
  margin-right: var(--spacing-sm);
}

.item-price {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.item-change {
  font-size: 11px;
  font-weight: var(--font-weight-medium);
}

.item-change.positive {
  color: #10B981;
}

.item-change.negative {
  color: #EF4444;
}

.star-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.8;
  transition: opacity 0.2s;
  flex-shrink: 0;
}

.star-btn:hover {
  opacity: 1;
}
</style>
