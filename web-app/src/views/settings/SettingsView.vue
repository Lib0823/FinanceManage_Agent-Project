<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import { mockSettings } from '@/services/mockData'

const router = useRouter()

const settings = ref(mockSettings)

const assetItems = [
  { key: 'stocks_overseas', label: '주식 (해외)', icon: '📈' },
  { key: 'stocks_domestic', label: '주식 (국내)', icon: '🏠' },
  { key: 'coins', label: '코인', icon: '🪙' },
  { key: 'bonds', label: '채권', icon: '📜' }
]

const handleSave = () => {
  console.log('Save settings:', settings.value)
  router.back()
}

const handleWithdraw = () => {
  if (confirm('정말 회원 탈퇴하시겠습니까?')) {
    localStorage.removeItem('accessToken')
    router.push('/welcome')
  }
}
</script>

<template>
  <div class="settings-screen">
    <AppHeader title="설정" showBack />

    <div class="content">
      <!-- Asset Order -->
      <section class="section">
        <h3 class="section-title">📊 관심 자산 순위</h3>
        <div class="asset-order-list">
          <div
            v-for="(item, index) in assetItems"
            :key="item.key"
            class="asset-order-item"
          >
            <span class="asset-icon">{{ item.icon }}</span>
            <span class="asset-label">{{ item.label }}</span>
            <button class="order-btn">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M7 10L12 5L17 10M7 14L12 19L17 14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </section>

      <!-- Dark Mode -->
      <section class="section">
        <div class="setting-row">
          <div class="setting-label">
            <span class="setting-icon">🌙</span>
            <span>다크 모드</span>
          </div>
          <label class="toggle-wrapper">
            <input type="checkbox" v-model="settings.darkMode" />
            <span class="toggle-slider"></span>
          </label>
        </div>
      </section>

      <!-- Auto Login -->
      <section class="section">
        <div class="setting-row">
          <div class="setting-label">
            <span class="setting-icon">🔐</span>
            <span>자동 로그인</span>
          </div>
          <label class="toggle-wrapper">
            <input type="checkbox" v-model="settings.autoLogin" />
            <span class="toggle-slider"></span>
          </label>
        </div>
      </section>

      <!-- Notifications -->
      <section class="section">
        <h3 class="section-title">🔔 알림</h3>

        <div class="notification-group">
          <div class="notification-header">
            <span>• 주식</span>
            <label class="toggle-wrapper small">
              <input type="checkbox" v-model="settings.notifications.stocks.news" />
              <span class="toggle-slider"></span>
            </label>
          </div>
          <div class="notification-items">
            <div class="notification-item">
              <span class="indent">뉴스</span>
              <label class="toggle-wrapper small">
                <input type="checkbox" v-model="settings.notifications.stocks.news" />
                <span class="toggle-slider"></span>
              </label>
            </div>
            <div class="notification-item">
              <span class="indent">매매</span>
              <label class="toggle-wrapper small">
                <input type="checkbox" v-model="settings.notifications.stocks.trading" />
                <span class="toggle-slider"></span>
              </label>
            </div>
          </div>
        </div>

        <div class="notification-group">
          <div class="notification-header">
            <span>• 코인</span>
            <label class="toggle-wrapper small">
              <input type="checkbox" v-model="settings.notifications.coins.news" />
              <span class="toggle-slider"></span>
            </label>
          </div>
          <div class="notification-items">
            <div class="notification-item">
              <span class="indent">뉴스</span>
              <label class="toggle-wrapper small">
                <input type="checkbox" v-model="settings.notifications.coins.news" />
                <span class="toggle-slider"></span>
              </label>
            </div>
            <div class="notification-item">
              <span class="indent">매매</span>
              <label class="toggle-wrapper small">
                <input type="checkbox" v-model="settings.notifications.coins.trading" />
                <span class="toggle-slider"></span>
              </label>
            </div>
          </div>
        </div>
      </section>

      <!-- Withdraw -->
      <button class="withdraw-btn" @click="handleWithdraw">
        회원 탈퇴
      </button>

      <!-- Save Button -->
      <button class="btn btn-save" @click="handleSave">
        저장
      </button>
    </div>
  </div>
</template>

<style scoped>
.settings-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
}

.content {
  padding: var(--spacing-lg);
}

.section {
  margin-bottom: var(--spacing-xl);
}

.section-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-md);
}

.asset-order-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.asset-order-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
}

.asset-icon {
  font-size: var(--font-size-lg);
}

.asset-label {
  flex: 1;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.order-btn {
  background: none;
  border: none;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: var(--spacing-xs);
}

.setting-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.setting-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.setting-icon {
  font-size: var(--font-size-lg);
}

.toggle-wrapper {
  position: relative;
  width: 50px;
  height: 28px;
}

.toggle-wrapper.small {
  width: 40px;
  height: 22px;
}

.toggle-wrapper input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: var(--color-bg-tertiary);
  border-radius: var(--radius-full);
  transition: 0.3s;
}

.toggle-slider::before {
  position: absolute;
  content: "";
  height: 22px;
  width: 22px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  border-radius: 50%;
  transition: 0.3s;
}

.toggle-wrapper.small .toggle-slider::before {
  height: 16px;
  width: 16px;
}

.toggle-wrapper input:checked + .toggle-slider {
  background-color: var(--color-success);
}

.toggle-wrapper input:checked + .toggle-slider::before {
  transform: translateX(22px);
}

.toggle-wrapper.small input:checked + .toggle-slider::before {
  transform: translateX(18px);
}

.notification-group {
  margin-bottom: var(--spacing-md);
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm) 0;
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
}

.notification-items {
  padding-left: var(--spacing-lg);
}

.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm) 0;
}

.indent {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.withdraw-btn {
  width: 100%;
  padding: var(--spacing-md);
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  margin-bottom: var(--spacing-lg);
}

.btn-save {
  width: 100%;
  padding: var(--spacing-lg);
  background: var(--color-primary);
  color: var(--color-text-inverse);
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
}

.btn-save:hover {
  background: var(--color-primary-dark);
}
</style>
