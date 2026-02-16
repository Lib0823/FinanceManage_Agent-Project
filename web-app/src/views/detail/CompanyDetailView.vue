<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import { mockCompanyInfo } from '@/services/mockData'

const route = useRoute()
const router = useRouter()

const activeTab = ref('basic')
const company = ref(mockCompanyInfo)
const isFavorite = ref(false)

const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
}

const tabs = [
  { key: 'basic', label: '기본정보' },
  { key: 'ai', label: 'AI분석' },
  { key: 'financial', label: '재무제표' },
  { key: 'disclosure', label: '공시정보' }
]

const aiAnalysis = ref({
  score: 92,
  rating: 'A+',
  recommendation: '매수',
  reasons: [
    '높은 성장 잠재력과 안정적인 수익 구조',
    '업계 평균 대비 우수한 재무 지표',
    '최근 3개월 긍정적인 시장 반응'
  ],
  strengths: [
    { title: '재무 안정성', score: 95 },
    { title: '성장성', score: 88 },
    { title: '수익성', score: 93 }
  ],
  warnings: [
    '단기 변동성 주의',
    '글로벌 경기 둔화 영향 가능성'
  ]
})

const financialData = ref({
  annual: [
    { year: '2024', revenue: 302840, operatingProfit: 45680, netProfit: 35920, eps: 4850 },
    { year: '2023', revenue: 280150, operatingProfit: 42300, netProfit: 32100, eps: 4320 },
    { year: '2022', revenue: 265800, operatingProfit: 38900, netProfit: 29500, eps: 3980 }
  ],
  ratios: [
    { label: 'ROE', value: 18.5, unit: '%', good: true },
    { label: 'ROA', value: 12.3, unit: '%', good: true },
    { label: 'PER', value: 15.2, unit: '배', good: true },
    { label: 'PBR', value: 2.8, unit: '배', good: true },
    { label: '부채비율', value: 45.6, unit: '%', good: true },
    { label: '유동비율', value: 185.3, unit: '%', good: true }
  ]
})

const disclosures = ref([
  {
    id: 1,
    type: '공정공시',
    title: '2024년 4분기 실적발표 (잠정)',
    date: '2025-01-15',
    important: true
  },
  {
    id: 2,
    type: '주요사항보고',
    title: '자기주식 취득 결정',
    date: '2025-01-10',
    important: false
  },
  {
    id: 3,
    type: '정기공시',
    title: '분기보고서 (2024.09)',
    date: '2024-11-14',
    important: false
  },
  {
    id: 4,
    type: '공정공시',
    title: '신규 사업 진출 계획',
    date: '2024-11-05',
    important: true
  },
  {
    id: 5,
    type: '주요사항보고',
    title: '배당 결정',
    date: '2024-10-28',
    important: false
  },
  {
    id: 6,
    type: '공정공시',
    title: '대규모 설비투자 계획',
    date: '2024-10-15',
    important: true
  }
])

const formatNumber = (num) => {
  return new Intl.NumberFormat('ko-KR').format(num)
}

const goToNews = () => {
  router.push(`/news?symbol=${company.value.symbol}`)
}

const goToTrade = () => {
  router.push(`/trading/${company.value.symbol}`)
}

onMounted(() => {
  // Fetch company data based on route.params.symbol

  // Check if AI analysis tab should be shown
  if (route.query.showAiAnalysis === 'true') {
    activeTab.value = 'ai'
    // Remove query parameter from URL
    router.replace({ query: {} })
  }
})
</script>

<template>
  <div class="company-detail-screen">
    <AppHeader title="기업 상세 정보" showBack />

    <div class="content">
      <!-- Company Header -->
      <div class="company-header">
        <div class="company-logo">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
            <line x1="9" y1="9" x2="15" y2="9"/>
            <line x1="9" y1="15" x2="15" y2="15"/>
          </svg>
        </div>
        <div class="company-info">
          <span class="company-name">{{ company.name }}</span>
          <span class="company-symbol">{{ company.symbol }}</span>
        </div>
        <button class="favorite-btn" @click="toggleFavorite">
          <van-icon
            :name="isFavorite ? 'star' : 'star-o'"
            :color="isFavorite ? '#F59E0B' : '#9CA3AF'"
            size="24"
          />
        </button>
      </div>

      <!-- Sub Tabs -->
      <div class="sub-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          :class="['sub-tab', { active: activeTab === tab.key }]"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- Basic Info Tab -->
      <div v-if="activeTab === 'basic'" class="tab-content">
        <!-- Score Gauges -->
        <div class="score-grid">
          <div v-for="(score, key) in company.scores" :key="key" class="score-item">
            <div class="gauge">
              <svg viewBox="0 0 100 100">
                <circle cx="50" cy="50" r="45" fill="none" stroke="#E5E7EB" stroke-width="8"/>
                <circle
                  cx="50" cy="50" r="45"
                  fill="none"
                  stroke="#F59E0B"
                  stroke-width="8"
                  :stroke-dasharray="`${score * 2.83} 283`"
                  transform="rotate(-90 50 50)"
                />
              </svg>
              <span class="gauge-label">{{ key.toUpperCase() }}</span>
              <span class="gauge-value">{{ score }}</span>
            </div>
          </div>
        </div>

        <!-- Company Info Card -->
        <div class="info-card">
          <div class="info-row">
            <span class="info-label">기업명</span>
            <span class="info-value">{{ company.nameEn }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">주소</span>
            <span class="info-value">{{ company.address }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">홈페이지</span>
            <a class="info-value link" :href="company.website" target="_blank">{{ company.website }}</a>
          </div>
          <div class="info-row">
            <span class="info-label">섹터</span>
            <span class="info-value">{{ company.sector }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">직원수</span>
            <span class="info-value">{{ formatNumber(company.employees) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">주식수</span>
            <span class="info-value">{{ formatNumber(company.shares) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">재무통화</span>
            <span class="info-value">{{ company.currency }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">시가총액</span>
            <span class="info-value">{{ formatNumber(company.marketCap) }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">배당</span>
            <span class="info-value">{{ company.dividend }}</span>
          </div>
          <div class="info-row full">
            <span class="info-label">개요</span>
            <p class="info-desc">{{ company.description }}</p>
          </div>
        </div>

        <!-- Action Buttons -->
        <div class="action-buttons">
          <button class="action-btn news" @click="goToNews">뉴스</button>
          <button class="action-btn trade" @click="goToTrade">매매</button>
        </div>
      </div>

      <!-- AI Analysis Tab -->
      <div v-else-if="activeTab === 'ai'" class="tab-content">
        <div class="ai-analysis-content">
          <!-- Compact AI Score Section -->
          <div class="ai-compact-header">
            <div class="ai-score-compact">
              <div class="score-circle-small">
                <svg viewBox="0 0 80 80">
                  <circle cx="40" cy="40" r="36" fill="none" stroke="#374151" stroke-width="6"/>
                  <circle
                    cx="40" cy="40" r="36"
                    fill="none"
                    stroke="url(#gradient)"
                    stroke-width="6"
                    :stroke-dasharray="`${aiAnalysis.score * 2.26} 226`"
                    transform="rotate(-90 40 40)"
                    stroke-linecap="round"
                  />
                  <defs>
                    <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="100%">
                      <stop offset="0%" style="stop-color:#4F46E5;stop-opacity:1" />
                      <stop offset="100%" style="stop-color:#7C3AED;stop-opacity:1" />
                    </linearGradient>
                  </defs>
                </svg>
                <div class="score-content-small">
                  <span class="score-value-small">{{ aiAnalysis.score }}</span>
                </div>
              </div>
              <div class="ai-info-compact">
                <div class="ai-header-inline">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                    <path d="M12 2L2 7l10 5 10-5-10-5z" stroke="#A78BFA" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M2 17l10 5 10-5" stroke="#A78BFA" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M2 12l10 5 10-5" stroke="#A78BFA" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <h3 class="ai-title-compact">AI 투자 분석</h3>
                </div>
                <div class="badges-row">
                  <div class="rating-badge-small" :class="aiAnalysis.rating">{{ aiAnalysis.rating }}</div>
                  <div class="recommendation-badge-small" :class="aiAnalysis.recommendation">
                    {{ aiAnalysis.recommendation }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="analysis-section">
            <h4 class="section-title">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
              추천 이유
            </h4>
            <ul class="reason-list">
              <li v-for="(reason, index) in aiAnalysis.reasons" :key="index">{{ reason }}</li>
            </ul>
          </div>

          <div class="analysis-section">
            <h4 class="section-title">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M13 10V3L4 14h7v7l9-11h-7z"/>
              </svg>
              주요 강점
            </h4>
            <div class="strength-list">
              <div v-for="(strength, index) in aiAnalysis.strengths" :key="index" class="strength-item">
                <div class="strength-header">
                  <span class="strength-title">{{ strength.title }}</span>
                  <span class="strength-score">{{ strength.score }}점</span>
                </div>
                <div class="strength-bar">
                  <div class="strength-progress" :style="{ width: `${strength.score}%` }"></div>
                </div>
              </div>
            </div>
          </div>

          <div class="analysis-section">
            <h4 class="section-title warning">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
              </svg>
              유의사항
            </h4>
            <ul class="warning-list">
              <li v-for="(warning, index) in aiAnalysis.warnings" :key="index">{{ warning }}</li>
            </ul>
          </div>

          <!-- Disclaimer -->
          <div class="ai-disclaimer">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
              <path d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            <p>본 분석은 AI를 통해 생성된 참고 자료이며, 투자 판단 및 그에 따른 결과에 대한 책임은 투자자 본인에게 있습니다.</p>
          </div>
        </div>
      </div>

      <!-- Financial Tab -->
      <div v-else-if="activeTab === 'financial'" class="tab-content">
        <div class="financial-content">
          <!-- Financial Ratios -->
          <div class="financial-section">
            <h4 class="section-title">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
              </svg>
              주요 재무비율
            </h4>
            <div class="ratios-grid">
              <div v-for="(ratio, index) in financialData.ratios" :key="index" class="ratio-card">
                <div class="ratio-label">{{ ratio.label }}</div>
                <div class="ratio-value" :class="{ good: ratio.good }">
                  {{ ratio.value }}{{ ratio.unit }}
                </div>
              </div>
            </div>
          </div>

          <!-- Annual Financial Data -->
          <div class="financial-section">
            <h4 class="section-title">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M3 10h18M3 14h18m-9-4v8m-7 0h14a2 2 0 002-2V8a2 2 0 00-2-2H5a2 2 0 00-2 2v8a2 2 0 002 2z"/>
              </svg>
              연간 실적 (단위: 억원)
            </h4>
            <div class="financial-table">
              <div class="table-header">
                <div class="table-cell">연도</div>
                <div class="table-cell">매출액</div>
                <div class="table-cell">영업이익</div>
                <div class="table-cell">순이익</div>
                <div class="table-cell">EPS</div>
              </div>
              <div v-for="data in financialData.annual" :key="data.year" class="table-row">
                <div class="table-cell year">{{ data.year }}</div>
                <div class="table-cell">{{ formatNumber(data.revenue) }}</div>
                <div class="table-cell">{{ formatNumber(data.operatingProfit) }}</div>
                <div class="table-cell">{{ formatNumber(data.netProfit) }}</div>
                <div class="table-cell">{{ formatNumber(data.eps) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Disclosure Tab -->
      <div v-else-if="activeTab === 'disclosure'" class="tab-content">
        <div class="disclosure-content">
          <div class="disclosure-section">
            <h4 class="section-title">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
              최근 공시
            </h4>
            <div class="disclosure-list">
              <div
                v-for="disclosure in disclosures"
                :key="disclosure.id"
                class="disclosure-item"
                :class="{ important: disclosure.important }"
              >
                <div class="disclosure-header">
                  <span class="disclosure-type">{{ disclosure.type }}</span>
                  <span class="disclosure-date">{{ disclosure.date }}</span>
                </div>
                <div class="disclosure-title">
                  <svg v-if="disclosure.important" width="14" height="14" viewBox="0 0 24 24" fill="currentColor" class="important-icon">
                    <path d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                  </svg>
                  {{ disclosure.title }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Other tabs placeholder -->
      <div v-else class="tab-content">
        <div class="empty-tab">
          <p>{{ tabs.find(t => t.key === activeTab)?.label }} 정보를 불러오는 중...</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.company-detail-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
}

.content {
  padding: 0 var(--spacing-lg) var(--spacing-lg);
}

.company-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.company-logo {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.company-logo svg {
  color: var(--color-text-tertiary);
}

.company-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.company-name {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.company-symbol {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.sub-tabs {
  display: flex;
  gap: 0;
  margin-bottom: var(--spacing-xl);
  background: var(--color-bg-tertiary);
  border-radius: var(--radius-lg);
  padding: 4px;
}

.sub-tab {
  flex: 1;
  padding: var(--spacing-sm) var(--spacing-xs);
  background: transparent;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s ease;
  font-weight: var(--font-weight-medium);
}

.sub-tab.active {
  background: var(--color-primary);
  color: var(--color-text-inverse);
  box-shadow: 0 2px 8px rgba(79, 70, 229, 0.3);
}

.favorite-btn {
  background: none;
  border: none;
  padding: var(--spacing-xs);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.score-grid {
  display: flex;
  justify-content: space-around;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, rgba(79, 70, 229, 0.05) 0%, rgba(124, 58, 237, 0.05) 100%);
  border-radius: var(--radius-lg);
}

.score-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
}

.gauge {
  position: relative;
  width: 70px;
  height: 70px;
}

.gauge svg {
  width: 100%;
  height: 100%;
}

.gauge-label {
  position: absolute;
  top: 18px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 10px;
  color: var(--color-text-tertiary);
  font-weight: var(--font-weight-medium);
  text-transform: uppercase;
}

.gauge-value {
  position: absolute;
  bottom: 18px;
  left: 50%;
  transform: translateX(-50%);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.info-card {
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  border-radius: var(--radius-xl);
  padding: var(--spacing-xl);
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  gap: var(--spacing-md);
}

.info-row.full {
  flex-direction: column;
  align-items: flex-start;
  gap: var(--spacing-sm);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  flex-shrink: 0;
  min-width: 80px;
}

.info-value {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  text-align: right;
  flex: 1;
  word-break: break-all;
}

.info-value.link {
  color: #A78BFA;
  text-decoration: none;
  transition: color 0.3s ease;
}

.info-value.link:hover {
  color: #7C3AED;
}

.info-desc {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.6;
  width: 100%;
}

.empty-tab {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  color: var(--color-text-tertiary);
}

.action-buttons {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-2xl);
}

.action-btn {
  flex: 1;
  padding: var(--spacing-md) var(--spacing-lg);
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
}

.action-btn:active {
  transform: translateY(0);
}

.action-btn.news {
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  color: var(--color-text-inverse);
}

.action-btn.trade {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  color: var(--color-text-inverse);
}

/* AI Analysis Tab */
.ai-analysis-content {
  padding-bottom: var(--spacing-lg);
}

.ai-compact-header {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, rgba(79, 70, 229, 0.08) 0%, rgba(124, 58, 237, 0.08) 100%);
  border-radius: var(--radius-lg);
  border: 1px solid rgba(79, 70, 229, 0.2);
}

.ai-score-compact {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.score-circle-small {
  position: relative;
  width: 80px;
  height: 80px;
  flex-shrink: 0;
}

.score-circle-small svg {
  width: 100%;
  height: 100%;
  transform: scaleX(-1);
}

.score-content-small {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.score-value-small {
  display: block;
  font-size: 24px;
  font-weight: var(--font-weight-bold);
  color: #F9FAFB;
  line-height: 1;
}

.ai-info-compact {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  min-width: 0;
}

.ai-header-inline {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.ai-header-inline svg {
  flex-shrink: 0;
}

.ai-title-compact {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-bold);
  color: #F9FAFB;
  margin: 0;
}

.badges-row {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.rating-badge-small {
  padding: 4px var(--spacing-md);
  background: linear-gradient(135deg, #4F46E5 0%, #7C3AED 100%);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: #fff;
  box-shadow: 0 2px 6px rgba(79, 70, 229, 0.3);
}

.recommendation-badge-small {
  padding: 4px var(--spacing-md);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
}

.recommendation-badge-small.매수 {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  color: #fff;
}

.recommendation-badge-small.보유 {
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  color: #fff;
}

.recommendation-badge-small.매도 {
  background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
  color: #fff;
}

.ai-disclaimer {
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-xs);
  padding: var(--spacing-md);
  background: rgba(255, 255, 255, 0.02);
  border-radius: var(--radius-md);
  border: 1px solid rgba(255, 255, 255, 0.05);
  margin-top: var(--spacing-xl);
}

.ai-disclaimer svg {
  color: var(--color-text-tertiary);
  flex-shrink: 0;
  margin-top: 2px;
}

.ai-disclaimer p {
  margin: 0;
  font-size: 11px;
  line-height: 1.5;
  color: var(--color-text-tertiary);
  opacity: 0.8;
}

.analysis-section {
  margin-bottom: var(--spacing-xl);
}

.section-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  color: #F9FAFB;
  margin-bottom: var(--spacing-md);
}

.section-title svg {
  color: #A78BFA;
}

.section-title.warning svg {
  color: #F59E0B;
}

.reason-list,
.warning-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.reason-list li,
.warning-list li {
  padding-left: var(--spacing-lg);
  position: relative;
  font-size: var(--font-size-sm);
  color: #D1D5DB;
  line-height: 1.5;
}

.reason-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #10B981;
  font-weight: var(--font-weight-bold);
}

.warning-list li::before {
  content: '⚠';
  position: absolute;
  left: 0;
  color: #F59E0B;
}

.strength-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.strength-item {
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--radius-md);
  padding: var(--spacing-md);
}

.strength-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-sm);
}

.strength-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: #F9FAFB;
}

.strength-score {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: #A78BFA;
}

.strength-bar {
  height: 6px;
  background: #374151;
  border-radius: var(--radius-full);
  overflow: hidden;
}

.strength-progress {
  height: 100%;
  background: linear-gradient(90deg, #4F46E5 0%, #7C3AED 100%);
  border-radius: var(--radius-full);
  transition: width 0.5s ease;
}

/* Financial Tab */
.financial-content {
  padding-bottom: var(--spacing-lg);
}

.financial-section {
  margin-bottom: var(--spacing-xl);
}

.ratios-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
}

.ratio-card {
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
  text-align: center;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.ratio-card:hover {
  transform: translateY(-2px);
  border-color: rgba(79, 70, 229, 0.3);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
}

.ratio-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-xs);
  font-weight: var(--font-weight-medium);
}

.ratio-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.ratio-value.good {
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.financial-table {
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  border-radius: var(--radius-lg);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.table-header {
  display: grid;
  grid-template-columns: 1fr 1.2fr 1.2fr 1.2fr 1fr;
  gap: var(--spacing-xs);
  padding: var(--spacing-md);
  background: rgba(79, 70, 229, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.table-row {
  display: grid;
  grid-template-columns: 1fr 1.2fr 1.2fr 1.2fr 1fr;
  gap: var(--spacing-xs);
  padding: var(--spacing-md);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: background 0.3s ease;
}

.table-row:last-child {
  border-bottom: none;
}

.table-row:hover {
  background: rgba(79, 70, 229, 0.05);
}

.table-cell {
  font-size: var(--font-size-xs);
  color: var(--color-text-primary);
  text-align: right;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.table-header .table-cell {
  color: #A78BFA;
  font-weight: var(--font-weight-semibold);
  text-align: right;
}

.table-cell.year {
  font-weight: var(--font-weight-bold);
  color: #A78BFA;
  justify-content: flex-start;
  text-align: left;
}

.table-header .table-cell:first-child {
  justify-content: flex-start;
  text-align: left;
}

/* Disclosure Tab */
.disclosure-content {
  padding-bottom: var(--spacing-lg);
}

.disclosure-section {
  margin-bottom: var(--spacing-xl);
}

.disclosure-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.disclosure-item {
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  border-radius: var(--radius-lg);
  padding: var(--spacing-md);
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
}

.disclosure-item:hover {
  transform: translateY(-1px);
  border-color: rgba(79, 70, 229, 0.3);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);
}

.disclosure-item.important {
  border-color: rgba(245, 158, 11, 0.3);
  background: linear-gradient(135deg, rgba(245, 158, 11, 0.05) 0%, rgba(217, 119, 6, 0.05) 100%);
}

.disclosure-item.important:hover {
  border-color: rgba(245, 158, 11, 0.5);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.2);
}

.disclosure-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xs);
}

.disclosure-type {
  font-size: var(--font-size-xs);
  color: #A78BFA;
  font-weight: var(--font-weight-semibold);
  padding: 2px var(--spacing-sm);
  background: rgba(79, 70, 229, 0.2);
  border-radius: var(--radius-sm);
}

.disclosure-item.important .disclosure-type {
  color: #F59E0B;
  background: rgba(245, 158, 11, 0.2);
}

.disclosure-date {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.disclosure-title {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  font-weight: var(--font-weight-medium);
  line-height: 1.4;
  display: flex;
  align-items: flex-start;
  gap: var(--spacing-xs);
}

.important-icon {
  color: #F59E0B;
  flex-shrink: 0;
  margin-top: 2px;
}
</style>
