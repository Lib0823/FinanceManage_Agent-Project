<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const navItems = [
  { name: 'profile', path: '/profile', icon: 'profile', label: '내정보' },
  { name: 'bot', path: '/bot', icon: 'bot', label: 'AI' },
  { name: 'assets', path: '/assets', icon: 'assets', label: '자산' },
  { name: 'home', path: '/home', icon: 'home', label: '홈' },
  { name: 'favorites', path: '/favorites', icon: 'star', label: '관심' },
  { name: 'search', path: '/search', icon: 'search', label: '검색' },
  { name: 'transactions', path: '/transactions', icon: 'transactions', label: '거래' }
]

const currentPath = computed(() => route.path)

const navigate = (path) => {
  router.push(path)
}
</script>

<template>
  <nav class="bottom-nav safe-area-bottom">
    <button
      v-for="item in navItems"
      :key="item.name"
      :class="['nav-item', { active: currentPath === item.path }]"
      @click="navigate(item.path)"
    >
      <div class="icon-wrapper">
        <!-- Profile Icon -->
        <svg v-if="item.icon === 'profile'" width="24" height="24" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="8" r="4" stroke="currentColor" stroke-width="2"/>
          <path d="M4 20C4 16.6863 7.58172 14 12 14C16.4183 14 20 16.6863 20 20" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>

        <!-- AI Bot Icon -->
        <svg v-else-if="item.icon === 'bot'" width="24" height="24" viewBox="0 0 24 24" fill="none">
          <rect x="3" y="6" width="18" height="12" rx="2" stroke="currentColor" stroke-width="2"/>
          <circle cx="8" cy="12" r="2" fill="currentColor"/>
          <circle cx="16" cy="12" r="2" fill="currentColor"/>
          <path d="M12 2V6" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <path d="M9 18V22" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <path d="M15 18V22" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>

        <!-- Assets Icon -->
        <svg v-else-if="item.icon === 'assets'" width="24" height="24" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="9" stroke="currentColor" stroke-width="2"/>
          <path d="M12 7V12L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>

        <!-- Home Icon -->
        <svg v-else-if="item.icon === 'home'" width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M3 12L12 3L21 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M5 10V19C5 19.5523 5.44772 20 6 20H18C18.5523 20 19 19.5523 19 19V10" stroke="currentColor" stroke-width="2"/>
        </svg>

        <!-- Star Icon -->
        <svg v-else-if="item.icon === 'star'" width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>

        <!-- Search Icon -->
        <svg v-else-if="item.icon === 'search'" width="24" height="24" viewBox="0 0 24 24" fill="none">
          <circle cx="11" cy="11" r="7" stroke="currentColor" stroke-width="2"/>
          <path d="M21 21L16.5 16.5" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>

        <!-- Transactions Icon -->
        <svg v-else-if="item.icon === 'transactions'" width="24" height="24" viewBox="0 0 24 24" fill="none">
          <path d="M3 6H21" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <path d="M3 12H21" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <path d="M3 18H21" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <circle cx="7" cy="6" r="2" fill="currentColor"/>
          <circle cx="17" cy="12" r="2" fill="currentColor"/>
          <circle cx="10" cy="18" r="2" fill="currentColor"/>
        </svg>
      </div>
      <span class="label">{{ item.label }}</span>
    </button>
  </nav>
</template>

<style scoped>
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: var(--max-width-mobile);
  height: var(--bottom-nav-height);
  background: var(--color-bg-accent);
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: var(--spacing-sm) 0;
  border-radius: var(--radius-2xl) var(--radius-2xl) 0 0;
  z-index: 100;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
  background: none;
  border: none;
  cursor: pointer;
  padding: var(--spacing-sm);
  color: rgba(255, 255, 255, 0.6);
  transition: all 0.2s ease;
}

.nav-item.active {
  color: var(--color-text-inverse);
}

.icon-wrapper {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.label {
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
}

@media (min-width: 768px) {
  .bottom-nav {
    max-width: var(--max-width-mobile);
  }
}
</style>
