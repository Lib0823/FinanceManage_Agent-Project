<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import { Pie, Bar } from 'vue-chartjs'
import { mockMarketIndices, mockExchangeRates, mockTopNews, mockAiRecommendations } from '@/services/mockData'

const router = useRouter()

const indices = ref(mockMarketIndices)
const exchangeRates = ref(mockExchangeRates)
const topNews = ref(mockTopNews)
const aiRecommendations = ref(mockAiRecommendations)

const activeTab = ref('domestic')

const formatNumber = (num) => {
  return new Intl.NumberFormat('ko-KR').format(num)
}

const formatChange = (change, percent) => {
  const sign = change >= 0 ? '+' : ''
  return `${sign}${change}(${sign}${percent}%)`
}

const goToNews = (news) => {
  router.push(`/news/${news.id}`)
}

onMounted(() => {
  // Fetch data from API
})
</script>

<template>
  <div class="home-screen">
    <AppHeader title="홈" showIcon icon="home" />

    <div class="content">
      <!-- Notification Banner -->
      <div class="notification-banner">
        <span class="notification-icon">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M18 8C18 6.4087 17.3679 4.88258 16.2426 3.75736C15.1174 2.63214 13.5913 2 12 2C10.4087 2 8.88258 2.63214 7.75736 3.75736C6.63214 4.88258 6 6.4087 6 8C6 15 3 17 3 17H21C21 17 18 15 18 8Z" stroke="#F59E0B" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M13.73 21C13.5542 21.3031 13.3019 21.5547 12.9982 21.7295C12.6946 21.9044 12.3504 21.9965 12 21.9965C11.6496 21.9965 11.3054 21.9044 11.0018 21.7295C10.6982 21.5547 10.4458 21.3031 10.27 21" stroke="#F59E0B" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </span>
        <span class="notification-text">알림 내용...</span>
        <span class="notification-highlight">테슬라 1주 매도 체결</span>
      </div>

      <!-- Market Indices -->
      <section class="section">
        <h2 class="section-title">주요 지수</h2>
        <div class="indices-card">
          <div class="indices-grid">
            <div class="index-item">
              <span class="index-label">코스피</span>
              <span class="index-value">{{ formatNumber(indices.kospi.value) }}</span>
              <span class="index-change negative">{{ formatChange(indices.kospi.change, indices.kospi.changePercent) }}</span>
            </div>
            <div class="index-item">
              <span class="index-label">코스닥</span>
              <span class="index-value">{{ formatNumber(indices.kosdaq.value) }}</span>
              <span class="index-change negative">{{ formatChange(indices.kosdaq.change, indices.kosdaq.changePercent) }}</span>
            </div>
            <div class="index-item">
              <span class="index-label">고용 지수</span>
              <span class="index-value">{{ formatNumber(indices.employment.value) }}</span>
              <span class="index-change negative">{{ formatChange(indices.employment.change, indices.employment.changePercent) }}</span>
            </div>
            <div class="index-item">
              <span class="index-label">??? 지수</span>
              <span class="index-value">{{ formatNumber(indices.unknown.value) }}</span>
              <span class="index-change negative">{{ formatChange(indices.unknown.change, indices.unknown.changePercent) }}</span>
            </div>
          </div>
          <div class="index-tab">
            <span class="tab-text">주식(국내)</span>
          </div>
        </div>
      </section>

      <!-- Top News -->
      <section class="section">
        <h2 class="section-title">속보 TOP5</h2>
        <div class="news-list">
          <div
            v-for="news in topNews"
            :key="news.id"
            class="news-item"
            @click="goToNews(news)"
          >
            <div class="news-thumb" v-if="news.image">
              <img :src="news.image" :alt="news.title" />
            </div>
            <div class="news-content">
              <span class="news-title">{{ news.title }}</span>
              <span class="news-desc" v-if="news.description">{{ news.description }}</span>
            </div>
            <div class="news-meta">
              <span class="news-source" v-if="news.source">{{ news.source }}</span>
              <span class="news-date">{{ news.date }}</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Exchange Rates -->
      <section class="section">
        <h2 class="section-title">환율</h2>
        <div class="exchange-grid">
          <div v-for="rate in exchangeRates" :key="rate.currency" class="exchange-card">
            <div class="exchange-header">
              <span class="country">{{ rate.country }}</span>
              <span class="currency">{{ rate.currency }}</span>
              <span class="rate">{{ formatNumber(rate.rate) }}</span>
              <span :class="['change', rate.change >= 0 ? 'positive' : 'negative']">
                {{ rate.change >= 0 ? '+' : '' }}{{ rate.change }}
              </span>
            </div>
            <div class="exchange-chart">
              <!-- Simple chart placeholder -->
              <div class="chart-placeholder"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- AI Recommendations -->
      <section class="section">
        <h2 class="section-title">AI 추천 종목</h2>
        <div class="ai-grid">
          <div v-for="(item, index) in aiRecommendations" :key="index" class="ai-card">
            <div class="ai-thumb">
              <img v-if="item.image" :src="item.image" :alt="item.title" />
            </div>
            <div class="ai-content">
              <span class="ai-title">{{ item.title }}</span>
              <span class="ai-desc">{{ item.description }}</span>
            </div>
            <span class="ai-time">{{ item.time }}</span>
          </div>
        </div>
      </section>
    </div>

    <!-- Spacer for bottom nav -->
    <div class="bottom-spacer"></div>
  </div>
</template>

<style scoped>
.home-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
  padding-bottom: var(--bottom-nav-height);
}

.content {
  padding: var(--spacing-lg);
}

.notification-banner {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
  margin-bottom: var(--spacing-lg);
}

.notification-icon {
  display: flex;
  align-items: center;
}

.notification-text {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.notification-highlight {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-medium);
}

.section {
  margin-bottom: var(--spacing-2xl);
}

.section-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
  text-align: center;
}

.indices-card {
  background: var(--color-bg-highlight);
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
  position: relative;
}

.indices-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);
}

.index-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
}

.index-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.index-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.index-change {
  font-size: var(--font-size-sm);
}

.index-change.positive {
  color: var(--color-stock-up);
}

.index-change.negative {
  color: var(--color-stock-down);
}

.index-tab {
  position: absolute;
  bottom: 40%;
  left: 50%;
  transform: translateX(-50%);
  background: var(--color-bg-primary);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  border: 1px solid #F59E0B;
}

.tab-text {
  font-size: var(--font-size-xs);
  color: #F59E0B;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.news-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  cursor: pointer;
}

.news-thumb {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  overflow: hidden;
  flex-shrink: 0;
}

.news-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.news-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.news-desc {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.news-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.news-source {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.news-date {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.exchange-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-md);
}

.exchange-card {
  background: var(--color-bg-highlight);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
}

.exchange-header {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin-bottom: var(--spacing-sm);
}

.country {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.currency {
  font-size: var(--font-size-xs);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
}

.rate {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.change {
  font-size: var(--font-size-xs);
}

.change.positive {
  color: var(--color-stock-up);
}

.change.negative {
  color: var(--color-stock-down);
}

.chart-placeholder {
  height: 40px;
  background: linear-gradient(90deg, transparent, rgba(var(--color-primary), 0.1), transparent);
  border-radius: var(--radius-sm);
}

.ai-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-sm);
}

.ai-card {
  background: var(--color-bg-accent);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.ai-thumb {
  width: 100%;
  height: 60px;
  background: var(--color-bg-highlight);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.ai-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ai-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.ai-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-inverse);
}

.ai-desc {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.7);
}

.ai-time {
  font-size: var(--font-size-xs);
  color: rgba(255, 255, 255, 0.5);
}

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
