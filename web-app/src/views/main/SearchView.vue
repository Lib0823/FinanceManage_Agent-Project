<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import AssetTabs from '@/components/common/AssetTabs.vue'
import { mockSearchResults } from '@/services/mockData'

const router = useRouter()

const tabs = ref({ main: 'stocks', sub: 'domestic' })
const searchQuery = ref('')
const results = ref(mockSearchResults)

const filteredResults = computed(() => {
  if (!searchQuery.value) return results.value
  const query = searchQuery.value.toLowerCase()
  return results.value.filter(
    item => item.name.toLowerCase().includes(query) || item.symbol.toLowerCase().includes(query)
  )
})

const toggleFavorite = (item) => {
  item.isFavorite = !item.isFavorite
}

const goToCompany = (item) => {
  router.push(`/company/${item.symbol}`)
}
</script>

<template>
  <div class="search-screen">
    <AppHeader title="종목 검색" showIcon icon="search" />

    <div class="content">
      <!-- Tabs -->
      <AssetTabs v-model="tabs" />

      <!-- Search Input -->
      <div class="search-bar">
        <button class="filter-btn">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
            <path d="M4 6H20M4 12H20M4 18H20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
        </button>
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
        <div class="results-list">
          <div
            v-for="item in filteredResults"
            :key="item.symbol"
            class="result-item"
            @click="goToCompany(item)"
          >
            <div class="item-thumb">
              <img :src="`https://logo.clearbit.com/${item.symbol.toLowerCase()}.com`" :alt="item.name" @error="$event.target.style.display='none'" />
            </div>
            <div class="item-content">
              <span class="item-name">{{ item.name }}</span>
              <span v-if="item.isFavorite" class="favorite-star">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="#F59E0B">
                  <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"/>
                </svg>
              </span>
            </div>
            <span class="item-desc">Description</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Spacer for bottom nav -->
    <div class="bottom-spacer"></div>
  </div>
</template>

<style scoped>
.search-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
  padding-bottom: var(--bottom-nav-height);
}

.content {
  padding: 0 var(--spacing-lg);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
  margin-bottom: var(--spacing-lg);
}

.filter-btn,
.search-btn {
  background: none;
  border: none;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: var(--spacing-xs);
}

.search-input {
  flex: 1;
  border: none;
  background: none;
  font-size: var(--font-size-base);
  outline: none;
}

.search-input::placeholder {
  color: var(--color-text-tertiary);
}

.results-container {
  background: var(--color-bg-accent);
  border-radius: var(--radius-xl);
  padding: var(--spacing-md);
  min-height: 400px;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.result-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-secondary-light);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: background 0.2s;
}

.result-item:hover {
  background: var(--color-secondary);
}

.item-thumb {
  width: 40px;
  height: 40px;
  background: var(--color-bg-highlight);
  border-radius: var(--radius-md);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-thumb img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.item-content {
  flex: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.item-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-inverse);
}

.favorite-star {
  display: flex;
}

.item-desc {
  font-size: var(--font-size-sm);
  color: rgba(255, 255, 255, 0.7);
}

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
