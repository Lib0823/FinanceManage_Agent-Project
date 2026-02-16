<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js'
import AppHeader from '@/components/common/AppHeader.vue'
import InvestmentTabs from '@/components/common/InvestmentTabs.vue'
import { mockSearchResults } from '@/services/mockData'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

const router = useRouter()

const tabs = ref({ main: 'stocks', sub: 'domestic' })
const portfolioTab = ref('favorites') // 'holdings' or 'favorites'
const allItems = ref(mockSearchResults)

const domesticCompany = {
  name: '삼성전자',
  symbol: '005930',
  keywords: ['반도체', 'IT', '대형주', '배당주']
}

const overseasCompany = {
  name: '테슬라',
  symbol: 'TSLA',
  keywords: ['전기차', 'IT', '성장주', '친환경']
}

const selectedCompany = computed(() => {
  return tabs.value.sub === 'domestic' ? domesticCompany : overseasCompany
})

const chartPeriod = ref('day') // 'day', 'month', 'year'

const companyData = ref({
  currentPrice: 71500,
  change: 500,
  changePercent: 0.70,
  open: 71200,
  high: 71800,
  low: 71000,
  volume: 15420000,
  aiAnalysis: 'AI 분석 결과, 단기 상승 모멘텀이 강화되고 있습니다. 기술적 지표상 매수 신호가 발생했으며, 거래량 증가세가 긍정적입니다.',
  charts: {
    day: {
      data: [68000, 69000, 68500, 70000, 71000, 71500],
      labels: ['12/10', '12/11', '12/12', '12/13', '12/14', '12/15']
    },
    month: {
      data: [65000, 67000, 69000, 68000, 70000, 71500],
      labels: ['7월', '8월', '9월', '10월', '11월', '12월']
    },
    year: {
      data: [55000, 58000, 62000, 68000, 71500],
      labels: ['2020', '2021', '2022', '2023', '2024']
    }
  },
  news: [
    { title: '삼성전자, 3분기 실적 호조', date: '2024-01-15' },
    { title: 'HBM3 수주 확대 기대감', date: '2024-01-14' }
  ],
  financials: {
    marketCap: '425조원',
    per: 12.5,
    eps: 5720,
    dividend: 1444
  }
})

const currentSlide = ref(0)
const scrollContainerRef = ref(null)

const chartData = computed(() => {
  const currentChart = companyData.value.charts[chartPeriod.value]
  return {
    labels: currentChart.labels,
    datasets: [
      {
        label: '주가',
        data: currentChart.data,
        borderColor: companyData.value.change >= 0 ? '#10B981' : '#EF4444',
        backgroundColor: (context) => {
          const ctx = context.chart.ctx
          const gradient = ctx.createLinearGradient(0, 0, 0, 200)
          if (companyData.value.change >= 0) {
            gradient.addColorStop(0, 'rgba(16, 185, 129, 0.3)')
            gradient.addColorStop(1, 'rgba(16, 185, 129, 0)')
          } else {
            gradient.addColorStop(0, 'rgba(239, 68, 68, 0.3)')
            gradient.addColorStop(1, 'rgba(239, 68, 68, 0)')
          }
          return gradient
        },
        fill: true,
        tension: 0.4,
        pointRadius: 4,
        pointHoverRadius: 6,
        pointBackgroundColor: companyData.value.change >= 0 ? '#10B981' : '#EF4444',
        pointBorderColor: '#fff',
        pointBorderWidth: 2
      }
    ]
  }
})

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    },
    tooltip: {
      mode: 'index',
      intersect: false,
      backgroundColor: 'rgba(30, 41, 59, 0.9)',
      titleColor: '#F9FAFB',
      bodyColor: '#F9FAFB',
      borderColor: 'rgba(139, 92, 246, 0.3)',
      borderWidth: 1,
      padding: 12,
      displayColors: false,
      callbacks: {
        label: (context) => {
          return `${new Intl.NumberFormat('ko-KR').format(context.parsed.y)}원`
        }
      }
    }
  },
  scales: {
    x: {
      grid: {
        display: false
      },
      ticks: {
        color: '#9CA3AF',
        font: {
          size: 11
        }
      }
    },
    y: {
      grid: {
        color: 'rgba(255, 255, 255, 0.05)',
        drawBorder: false
      },
      ticks: {
        color: '#9CA3AF',
        font: {
          size: 11
        },
        callback: (value) => {
          return `${(value / 1000).toFixed(0)}k`
        }
      }
    }
  },
  interaction: {
    mode: 'nearest',
    axis: 'x',
    intersect: false
  }
}

const favorites = computed(() => {
  return allItems.value.filter(item =>
    item.isFavorite && item.market === tabs.value.sub
  )
})

const holdings = computed(() => {
  return allItems.value.filter(item =>
    item.isFavorite && item.market === tabs.value.sub
  ).slice(0, 2)
})

const currentList = computed(() => {
  return portfolioTab.value === 'holdings' ? holdings.value : favorites.value
})

const formatNumber = (num) => {
  return new Intl.NumberFormat('ko-KR').format(num)
}

const formatChange = (change, percent) => {
  const sign = change >= 0 ? '+' : ''
  return `${sign}${formatNumber(change)} (${sign}${percent}%)`
}

const getChartPoints = (history) => {
  if (!history || history.length === 0) return ''
  const min = Math.min(...history)
  const max = Math.max(...history)
  const range = max - min || 1
  const points = history.map((value, index) => {
    const x = (index / (history.length - 1)) * 100
    const y = 60 - ((value - min) / range) * 56 - 2
    return `${x},${y}`
  })
  return points.join(' ')
}

const onScroll = () => {
  if (!scrollContainerRef.value) return
  const container = scrollContainerRef.value
  const scrollLeft = container.scrollLeft
  const itemWidth = container.offsetWidth * 0.92
  const newIndex = Math.round(scrollLeft / itemWidth)
  if (newIndex !== currentSlide.value && newIndex >= 0 && newIndex < 3) {
    currentSlide.value = newIndex
  }
}

const goToSlide = (index) => {
  if (!scrollContainerRef.value) return
  const container = scrollContainerRef.value
  const itemWidth = container.offsetWidth * 0.92
  container.scrollTo({ left: index * itemWidth, behavior: 'smooth' })
}

const removeFavorite = (item) => {
  item.isFavorite = false
}

const goToCompany = (item) => {
  router.push(`/company/${item.symbol}`)
}
</script>

<template>
  <div class="favorites-screen">
    <AppHeader title="관심 종목" showIcon icon="star" />

    <div class="content">
      <!-- Tabs -->
      <InvestmentTabs v-model="tabs" />

      <!-- Company Header -->
      <div class="company-header">
        <div class="company-name-row">
          <div class="company-logo">
            <svg width="44" height="44" viewBox="0 0 44 44" fill="none">
              <rect width="44" height="44" rx="12" fill="url(#companyGradient)"/>
              <defs>
                <linearGradient id="companyGradient" x1="0" y1="0" x2="44" y2="44">
                  <stop offset="0%" stop-color="#667EEA"/>
                  <stop offset="100%" stop-color="#764BA2"/>
                </linearGradient>
              </defs>
              <text x="22" y="28" font-size="20" font-weight="bold" fill="white" text-anchor="middle">삼</text>
            </svg>
          </div>
          <div class="company-text-wrapper">
            <div class="company-name-line">
              <h2 class="company-name">{{ selectedCompany.name }}</h2>
              <span class="company-code">{{ selectedCompany.symbol }}</span>
            </div>
            <div class="keyword-tags">
              <span v-for="keyword in selectedCompany.keywords" :key="keyword" class="keyword-tag">
                {{ keyword }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Horizontal Scroll Container -->
      <div class="info-swipe-container">
        <div
          ref="scrollContainerRef"
          class="info-scroll"
          @scroll="onScroll"
        >
          <!-- Slide 1: Price & AI Analysis -->
          <div class="info-card">
            <div class="price-section">
              <div class="current-price">{{ formatNumber(companyData.currentPrice) }}원</div>
              <div :class="['price-change', companyData.change >= 0 ? 'positive' : 'negative']">
                {{ formatChange(companyData.change, companyData.changePercent) }}
              </div>
            </div>

            <div class="price-details">
              <div class="detail-row">
                <span class="detail-label">시가</span>
                <span class="detail-value">{{ formatNumber(companyData.open) }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">고가</span>
                <span class="detail-value high">{{ formatNumber(companyData.high) }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">저가</span>
                <span class="detail-value low">{{ formatNumber(companyData.low) }}</span>
              </div>
              <div class="detail-row">
                <span class="detail-label">거래량</span>
                <span class="detail-value">{{ formatNumber(companyData.volume) }}</span>
              </div>
            </div>

            <div class="ai-analysis">
              <div class="ai-badge">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2L2 7L12 12L22 7L12 2Z"/>
                  <path d="M2 17L12 22L22 17"/>
                  <path d="M2 12L12 17L22 12"/>
                </svg>
                AI 분석
              </div>
              <p class="ai-text">{{ companyData.aiAnalysis }}</p>
            </div>
          </div>

          <!-- Slide 2: Chart -->
          <div class="info-card chart-card">
            <div class="chart-header">
              <h3 class="card-title">주가 추이</h3>
              <div class="period-selector">
                <button
                  v-for="period in ['day', 'month', 'year']"
                  :key="period"
                  :class="['period-btn', { active: chartPeriod === period }]"
                  @click="chartPeriod = period"
                >
                  {{ period === 'day' ? '일' : period === 'month' ? '월' : '년' }}
                </button>
              </div>
            </div>
            <div class="chart-container">
              <Line :data="chartData" :options="chartOptions" />
            </div>
          </div>

          <!-- Slide 3: News & Info -->
          <div class="info-card news-card">
            <div class="news-section">
              <h3 class="card-title">최근 소식</h3>
              <div class="news-items">
                <div v-for="(news, idx) in companyData.news" :key="idx" class="news-row">
                  <span class="news-title">{{ news.title }}</span>
                  <span class="news-date">{{ news.date }}</span>
                </div>
              </div>
            </div>
            <div class="financials-section">
              <h3 class="card-title">재무 정보</h3>
              <div class="financial-grid">
                <div class="financial-item">
                  <span class="fin-label">시가총액</span>
                  <span class="fin-value">{{ companyData.financials.marketCap }}</span>
                </div>
                <div class="financial-item">
                  <span class="fin-label">PER</span>
                  <span class="fin-value">{{ companyData.financials.per }}</span>
                </div>
                <div class="financial-item">
                  <span class="fin-label">EPS</span>
                  <span class="fin-value">{{ formatNumber(companyData.financials.eps) }}</span>
                </div>
                <div class="financial-item">
                  <span class="fin-label">배당금</span>
                  <span class="fin-value">{{ formatNumber(companyData.financials.dividend) }}원</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Slide Indicators -->
        <div class="slide-indicators">
          <span
            v-for="idx in 3"
            :key="idx"
            :class="['indicator', { active: currentSlide === idx - 1 }]"
            @click="goToSlide(idx - 1)"
          ></span>
        </div>
      </div>

      <!-- Portfolio Tabs Slider -->
      <div class="portfolio-tabs-slider">
        <div class="tabs-background">
          <div
            class="tab-indicator"
            :class="{ 'tab-right': portfolioTab === 'favorites' }"
          ></div>
        </div>
        <button
          class="tab-option"
          :class="{ active: portfolioTab === 'holdings' }"
          @click="portfolioTab = 'holdings'"
        >
          보유 종목
        </button>
        <button
          class="tab-option"
          :class="{ active: portfolioTab === 'favorites' }"
          @click="portfolioTab = 'favorites'"
        >
          관심 종목
        </button>
      </div>

      <!-- Items List -->
      <div class="items-container">
        <div v-if="currentList.length === 0" class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none">
            <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="var(--color-text-tertiary)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <p class="empty-text">종목이 없습니다</p>
        </div>

        <div v-else class="items-list">
          <div
            v-for="(item, idx) in currentList"
            :key="item.symbol"
            class="item-row"
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
              <div class="item-price">{{ formatNumber(companyData.currentPrice) }}원</div>
              <div :class="['item-change', companyData.change >= 0 ? 'positive' : 'negative']">
                {{ companyData.change >= 0 ? '+' : '' }}{{ companyData.changePercent }}%
              </div>
            </div>
            <button class="star-btn" @click.stop="removeFavorite(item)">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="#F59E0B">
                <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Spacer for bottom nav -->
    <div class="bottom-spacer"></div>
  </div>
</template>

<style scoped>
.favorites-screen {
  min-height: 100vh;
  background: linear-gradient(180deg, #0F172A 0%, #1E293B 100%);
  padding-bottom: var(--bottom-nav-height);
}

.favorites-screen :deep(.app-header) {
  background: #0F172A;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.content {
  padding: 0 var(--spacing-lg);
}

/* Company Header */
.company-header {
  margin-top: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.company-name-row {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.company-logo {
  flex-shrink: 0;
  border-radius: 12px;
}

.company-text-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.company-name-line {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.company-name {
  font-size: 20px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin: 0;
}

.company-code {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  background: rgba(255, 255, 255, 0.05);
  padding: 2px 8px;
  border-radius: 4px;
}

.keyword-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.keyword-tag {
  padding: 3px 10px;
  background: rgba(139, 92, 246, 0.15);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 6px;
  font-size: 11px;
  color: #A78BFA;
  font-weight: var(--font-weight-medium);
}

/* Horizontal Scroll */
.info-swipe-container {
  position: relative;
  overflow: hidden;
  margin-bottom: var(--spacing-xl);
}

.info-scroll {
  display: flex;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
  padding: 0 5%;
  gap: 12px;
}

.info-scroll::-webkit-scrollbar {
  display: none;
}

.info-card {
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  border-radius: 12px;
  padding: var(--spacing-lg);
  flex: 0 0 92%;
  scroll-snap-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

/* Slide 1: Price & AI */
.price-section {
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.current-price {
  font-size: 32px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: 4px;
}

.price-change {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
}

.price-change.positive {
  color: #10B981;
}

.price-change.negative {
  color: #EF4444;
}

.price-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
}

.detail-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.detail-value {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.detail-value.high {
  color: #EF4444;
}

.detail-value.low {
  color: #3B82F6;
}

.ai-analysis {
  background: rgba(139, 92, 246, 0.1);
  border: 1px solid rgba(139, 92, 246, 0.3);
  border-radius: 8px;
  padding: var(--spacing-md);
}

.ai-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: rgba(139, 92, 246, 0.2);
  border-radius: 6px;
  font-size: 11px;
  font-weight: var(--font-weight-semibold);
  color: #A78BFA;
  margin-bottom: var(--spacing-sm);
}

.ai-text {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.5;
  margin: 0;
}

/* Slide 2: Chart */
.chart-card {
  display: flex;
  flex-direction: column;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.card-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin: 0;
}

.period-selector {
  display: flex;
  gap: 4px;
  background: rgba(255, 255, 255, 0.05);
  padding: 3px;
  border-radius: 8px;
}

.period-btn {
  padding: 4px 12px;
  background: none;
  border: none;
  border-radius: 6px;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.period-btn.active {
  background: #8B5CF6;
  color: #FFFFFF;
  font-weight: var(--font-weight-semibold);
}

.period-btn:hover:not(.active) {
  background: rgba(255, 255, 255, 0.1);
  color: var(--color-text-primary);
}

.chart-container {
  height: 200px;
  margin-bottom: var(--spacing-sm);
}

/* Slide 3: News & Financials */
.news-card {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.news-section {
  flex: 1;
}

.news-items {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.news-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
  gap: var(--spacing-sm);
}

.news-title {
  flex: 1;
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  line-height: 1.4;
}

.news-date {
  font-size: 11px;
  color: var(--color-text-tertiary);
  white-space: nowrap;
}

.financials-section {
  padding-top: var(--spacing-md);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.financial-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-sm);
}

.financial-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 6px;
}

.fin-label {
  font-size: 11px;
  color: var(--color-text-secondary);
}

.fin-value {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

/* Slide Indicators */
.slide-indicators {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: var(--spacing-sm);
}

.indicator {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  transition: all 0.2s ease;
  cursor: pointer;
}

.indicator:hover {
  background: rgba(255, 255, 255, 0.4);
}

.indicator.active {
  width: 20px;
  border-radius: 3px;
  background: #8B5CF6;
}

/* Portfolio Tabs Slider */
.portfolio-tabs-slider {
  position: relative;
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-md);
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  padding: 4px;
}

.tabs-background {
  position: absolute;
  top: 4px;
  left: 4px;
  right: 4px;
  bottom: 4px;
  pointer-events: none;
}

.tab-indicator {
  position: absolute;
  width: calc(50% - 4px);
  height: calc(100% - 0px);
  background: #F59E0B;
  border-radius: 8px;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3);
}

.tab-indicator.tab-right {
  transform: translateX(100%);
}

.tab-option {
  flex: 1;
  position: relative;
  padding: 10px var(--spacing-md);
  background: none;
  border: none;
  border-radius: 8px;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: color 0.3s;
  z-index: 1;
}

.tab-option.active {
  color: #FFFFFF;
  font-weight: var(--font-weight-semibold);
}

/* Items Container */
.items-container {
  background: rgba(30, 41, 59, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: var(--spacing-md);
  min-height: 250px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl);
  gap: var(--spacing-sm);
}

.empty-text {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-tertiary);
}

/* Items List */
.items-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.item-row {
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

.item-row:hover {
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

.item-thumb img {
  width: 100%;
  height: 100%;
  object-fit: contain;
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

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
