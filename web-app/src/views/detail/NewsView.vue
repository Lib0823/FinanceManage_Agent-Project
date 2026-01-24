<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import AssetTabs from '@/components/common/AssetTabs.vue'
import { mockTopNews } from '@/services/mockData'

const router = useRouter()

const tabs = ref({ main: 'stocks', sub: 'domestic' })
const selectedDate = ref(new Date())
const sortOrder = ref('latest')
const searchQuery = ref('')
const newsList = ref(mockTopNews)

const goToNewsDetail = (news) => {
  router.push(`/news/${news.id}`)
}
</script>

<template>
  <div class="news-screen">
    <AppHeader title="뉴스" showBack />

    <div class="content">
      <!-- Tabs -->
      <AssetTabs v-model="tabs" />

      <!-- Calendar -->
      <div class="calendar-section">
        <VDatePicker v-model="selectedDate" mode="date" :columns="1" />
        <div class="selected-date">
          {{ selectedDate.toLocaleDateString('ko-KR') }}
        </div>
      </div>

      <!-- Search and Sort -->
      <div class="filter-bar">
        <button class="sort-btn">
          {{ sortOrder === 'latest' ? '최신순' : '인기순' }}
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
  min-height: 100vh;
  background: var(--color-bg-primary);
}

.content {
  padding: 0 var(--spacing-lg) var(--spacing-lg);
}

.calendar-section {
  margin-bottom: var(--spacing-lg);
}

.selected-date {
  text-align: center;
  padding: var(--spacing-sm);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.filter-bar {
  display: flex;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.sort-btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-bg-tertiary);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
  cursor: pointer;
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
