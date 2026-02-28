<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import AssetTabs from '@/components/common/AssetTabs.vue'
import { mockTopNews } from '@/services/mockData'

const router = useRouter()

const tabs = ref({ main: 'stocks', sub: 'domestic' })
const newTabList = [
  { key: 'stocks', label: '주식', disabled: false },
  { key: 'coins', label: '코인', disabled: false }
]
const dateFilters = [
  { key: 'today', label: '오늘' },
  { key: 'yesterday', label: '어제' },
  { key: 'week', label: '일주일' },
  { key: 'month', label: '1개월' }
]
const selectedDateFilter = ref('today')
const sortOrders = ['최신순', '조회순', '추천순']
const sortOrderIndex = ref(0)
const searchQuery = ref('')
const newsList = ref(mockTopNews)

const selectDateFilter = (key) => {
  selectedDateFilter.value = key
}

const toggleSortOrder = () => {
  sortOrderIndex.value = (sortOrderIndex.value + 1) % sortOrders.length
}

const goToNewsDetail = (news) => {
  router.push(`/news/${news.id}`)
}

onMounted(() => {
  // Reset scroll position of news list container
  const newsListElement = document.querySelector('.news-list')
  if (newsListElement) {
    newsListElement.scrollTop = 0
  }
})
</script>

<template>
  <div class="news-screen">
    <AppHeader title="뉴스" showBack />

    <div class="content">
      <!-- Tabs -->
      <AssetTabs v-model="tabs" :tabs="newTabList" />

      <!-- Date Filter Buttons -->
      <div class="date-filter-section">
        <button
          v-for="filter in dateFilters"
          :key="filter.key"
          :class="['date-filter-btn', { active: selectedDateFilter === filter.key }]"
          @click="selectDateFilter(filter.key)"
        >
          {{ filter.label }}
        </button>
      </div>

      <!-- Search and Sort -->
      <div class="filter-bar">
        <button class="sort-btn" @click="toggleSortOrder">
          {{ sortOrders[sortOrderIndex] }}
        </button>
        <div class="search-input-wrapper">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="제목 / 내용"
            class="search-input"
          />
          <button class="search-btn">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <circle cx="11" cy="11" r="7" stroke="currentColor" stroke-width="2"/>
              <path d="M21 21L16.5 16.5" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- News List -->
      <div class="news-list">
        <div
          v-for="news in newsList"
          :key="news.id"
          class="news-item"
          @click="goToNewsDetail(news)"
        >
          <div class="news-thumb" v-if="news.image">
            <img :src="news.image" :alt="news.title" />
          </div>
          <div class="news-content">
            <h3 class="news-title">{{ news.title }}</h3>
            <p class="news-tags" v-if="news.tags">{{ news.tags.join(' ') }}</p>
          </div>
          <div class="news-meta">
            <span class="news-source" v-if="news.source">{{ news.source }}</span>
            <span class="news-date">{{ news.date }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.news-screen {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--color-bg-primary);
  overflow: hidden;
}

.content {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.content :deep(.asset-tabs) {
  flex-shrink: 0;
}

.date-filter-section {
  display: flex;
  background: var(--color-bg-tertiary);
  border-radius: var(--radius-full);
  padding: 4px;
  gap: 4px;
  margin: 0 var(--spacing-lg) var(--spacing-md);
  flex-shrink: 0;
}

.date-filter-btn {
  flex: 1;
  padding: var(--spacing-sm) var(--spacing-md);
  background: transparent;
  border: none;
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.date-filter-btn.active {
  background: #F59E0B;
  color: var(--color-text-inverse);
  font-weight: var(--font-weight-medium);
}

.date-filter-btn:hover:not(.active) {
  background: rgba(245, 158, 11, 0.1);
}

.filter-bar {
  display: flex;
  gap: var(--spacing-md);
  margin: 0 var(--spacing-lg) var(--spacing-md);
  flex-shrink: 0;
}

.sort-btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background: #F59E0B;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  color: var(--color-text-inverse);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 80px;
}

.sort-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.sort-btn:active {
  transform: translateY(0);
}

.search-input-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  padding: 0 var(--spacing-md);
}

.search-input {
  flex: 1;
  border: none;
  background: none;
  padding: var(--spacing-sm) 0;
  font-size: var(--font-size-sm);
  outline: none;
}

.search-btn {
  background: none;
  border: none;
  color: var(--color-text-secondary);
  cursor: pointer;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  flex: 1;
  overflow-y: auto;
  padding: 0 var(--spacing-lg) var(--spacing-lg);
}

.news-item {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: transform 0.2s;
}

.news-item:hover {
  transform: translateY(-2px);
}

.news-thumb {
  width: 60px;
  height: 60px;
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
  gap: var(--spacing-xs);
}

.news-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  line-height: 1.4;
}

.news-tags {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.news-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
  flex-shrink: 0;
}

.news-source {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.news-date {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}
</style>
