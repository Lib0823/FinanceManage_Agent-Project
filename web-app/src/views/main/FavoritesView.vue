<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import { mockSearchResults } from '@/services/mockData'

const router = useRouter()

const allItems = ref(mockSearchResults)

const favorites = computed(() => {
  return allItems.value.filter(item => item.isFavorite)
})

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
      <div v-if="favorites.length === 0" class="empty-state">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none">
          <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="var(--color-text-tertiary)" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        <p class="empty-text">관심 종목이 없습니다</p>
        <p class="empty-desc">종목 검색에서 관심 종목을 추가해보세요</p>
      </div>

      <div v-else class="favorites-list">
        <div
          v-for="item in favorites"
          :key="item.symbol"
          class="favorite-item"
          @click="goToCompany(item)"
        >
          <div class="item-thumb">
            <img :src="`https://logo.clearbit.com/${item.symbol.toLowerCase()}.com`" :alt="item.name" @error="$event.target.style.display='none'" />
          </div>
          <div class="item-content">
            <span class="item-name">{{ item.name }}</span>
            <span class="item-symbol">{{ item.symbol }}</span>
          </div>
          <button class="remove-btn" @click.stop="removeFavorite(item)">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="#F59E0B">
              <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"/>
            </svg>
          </button>
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
  background: var(--color-bg-primary);
  padding-bottom: var(--bottom-nav-height);
}

.content {
  padding: var(--spacing-lg);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-3xl);
  gap: var(--spacing-md);
}

.empty-text {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.empty-desc {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

.favorites-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.favorite-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-lg);
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-card);
  cursor: pointer;
  transition: transform 0.2s;
}

.favorite-item:hover {
  transform: translateY(-2px);
}

.item-thumb {
  width: 48px;
  height: 48px;
  background: var(--color-bg-tertiary);
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
  flex-direction: column;
  gap: 2px;
}

.item-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.item-symbol {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.remove-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: var(--spacing-sm);
}

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
