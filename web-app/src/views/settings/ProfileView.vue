<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import { mockUser } from '@/services/mockData'

const router = useRouter()

const user = ref(mockUser)

const goToSettings = () => {
  router.push('/settings')
}

const goToResetPassword = () => {
  router.push('/reset-password')
}

const handleLogout = () => {
  localStorage.removeItem('accessToken')
  router.push('/welcome')
}

const handleSave = () => {
  console.log('Save profile:', user.value)
}

const handleGetAppKey = () => {
  window.open('https://apiportal.koreainvestment.com', '_blank')
}
</script>

<template>
  <div class="profile-screen">
    <AppHeader title="내 정보" showIcon icon="profile">
      <template #right>
        <button class="icon-btn" @click="handleLogout">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M16 17L21 12L16 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M21 12H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
        <button class="icon-btn" @click="goToSettings">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
            <circle cx="12" cy="12" r="3" stroke="currentColor" stroke-width="2"/>
            <path d="M19.4 15C19.2669 15.3016 19.2272 15.6362 19.286 15.9606C19.3448 16.285 19.4995 16.5843 19.73 16.82L19.79 16.88C19.976 17.0657 20.1235 17.2863 20.2241 17.5291C20.3248 17.7719 20.3766 18.0322 20.3766 18.295C20.3766 18.5578 20.3248 18.8181 20.2241 19.0609C20.1235 19.3037 19.976 19.5243 19.79 19.71C19.6043 19.896 19.3837 20.0435 19.1409 20.1441C18.8981 20.2448 18.6378 20.2966 18.375 20.2966C18.1122 20.2966 17.8519 20.2448 17.6091 20.1441C17.3663 20.0435 17.1457 19.896 16.96 19.71L16.9 19.65C16.6643 19.4195 16.365 19.2648 16.0406 19.206C15.7162 19.1472 15.3816 19.1869 15.08 19.32C14.7842 19.4468 14.532 19.6572 14.3543 19.9255C14.1766 20.1938 14.0813 20.5082 14.08 20.83V21C14.08 21.5304 13.8693 22.0391 13.4942 22.4142C13.1191 22.7893 12.6104 23 12.08 23C11.5496 23 11.0409 22.7893 10.6658 22.4142C10.2907 22.0391 10.08 21.5304 10.08 21V20.91C10.0723 20.579 9.96512 20.258 9.77251 19.9887C9.5799 19.7194 9.31074 19.5143 9 19.4C8.69838 19.2669 8.36381 19.2272 8.03941 19.286C7.71502 19.3448 7.41568 19.4995 7.18 19.73L7.12 19.79C6.93425 19.976 6.71368 20.1235 6.47088 20.2241C6.22808 20.3248 5.96783 20.3766 5.705 20.3766C5.44217 20.3766 5.18192 20.3248 4.93912 20.2241C4.69632 20.1235 4.47575 19.976 4.29 19.79C4.10405 19.6043 3.95653 19.3837 3.85588 19.1409C3.75523 18.8981 3.70343 18.6378 3.70343 18.375C3.70343 18.1122 3.75523 17.8519 3.85588 17.6091C3.95653 17.3663 4.10405 17.1457 4.29 16.96L4.35 16.9C4.58054 16.6643 4.73519 16.365 4.794 16.0406C4.85282 15.7162 4.81312 15.3816 4.68 15.08C4.55324 14.7842 4.34276 14.532 4.07447 14.3543C3.80618 14.1766 3.49179 14.0813 3.17 14.08H3C2.46957 14.08 1.96086 13.8693 1.58579 13.4942C1.21071 13.1191 1 12.6104 1 12.08C1 11.5496 1.21071 11.0409 1.58579 10.6658C1.96086 10.2907 2.46957 10.08 3 10.08H3.09C3.42099 10.0723 3.74197 9.96512 4.01128 9.77251C4.28059 9.5799 4.48572 9.31074 4.6 9C4.73312 8.69838 4.77282 8.36381 4.714 8.03941C4.65519 7.71502 4.50054 7.41568 4.27 7.18L4.21 7.12C4.02405 6.93425 3.87653 6.71368 3.77588 6.47088C3.67523 6.22808 3.62343 5.96783 3.62343 5.705C3.62343 5.44217 3.67523 5.18192 3.77588 4.93912C3.87653 4.69632 4.02405 4.47575 4.21 4.29C4.39575 4.10405 4.61632 3.95653 4.85912 3.85588C5.10192 3.75523 5.36217 3.70343 5.625 3.70343C5.88783 3.70343 6.14808 3.75523 6.39088 3.85588C6.63368 3.95653 6.85425 4.10405 7.04 4.29L7.1 4.35C7.33568 4.58054 7.63502 4.73519 7.95941 4.794C8.28381 4.85282 8.61838 4.81312 8.92 4.68H9C9.29577 4.55324 9.54802 4.34276 9.72569 4.07447C9.90337 3.80618 9.99872 3.49179 10 3.17V3C10 2.46957 10.2107 1.96086 10.5858 1.58579C10.9609 1.21071 11.4696 1 12 1C12.5304 1 13.0391 1.21071 13.4142 1.58579C13.7893 1.96086 14 2.46957 14 3V3.09C14.0013 3.41179 14.0966 3.72618 14.2743 3.99447C14.452 4.26276 14.7042 4.47324 15 4.6C15.3016 4.73312 15.6362 4.77282 15.9606 4.714C16.285 4.65519 16.5843 4.50054 16.82 4.27L16.88 4.21C17.0657 4.02405 17.2863 3.87653 17.5291 3.77588C17.7719 3.67523 18.0322 3.62343 18.295 3.62343C18.5578 3.62343 18.8181 3.67523 19.0609 3.77588C19.3037 3.87653 19.5243 4.02405 19.71 4.21C19.896 4.39575 20.0435 4.61632 20.1441 4.85912C20.2448 5.10192 20.2966 5.36217 20.2966 5.625C20.2966 5.88783 20.2448 6.14808 20.1441 6.39088C20.0435 6.63368 19.896 6.85425 19.71 7.04L19.65 7.1C19.4195 7.33568 19.2648 7.63502 19.206 7.95941C19.1472 8.28381 19.1869 8.61838 19.32 8.92V9C19.4468 9.29577 19.6572 9.54802 19.9255 9.72569C20.1938 9.90337 20.5082 9.99872 20.83 10H21C21.5304 10 22.0391 10.2107 22.4142 10.5858C22.7893 10.9609 23 11.4696 23 12C23 12.5304 22.7893 13.0391 22.4142 13.4142C22.0391 13.7893 21.5304 14 21 14H20.91C20.5882 14.0013 20.2738 14.0966 20.0055 14.2743C19.7372 14.452 19.5268 14.7042 19.4 15V15Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </template>
    </AppHeader>

    <div class="content">
      <!-- Avatar Section -->
      <div class="avatar-section">
        <div class="avatar">
          <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=finance" alt="Avatar" />
        </div>
      </div>

      <!-- User Info Card -->
      <section class="info-card">
        <h3 class="card-title">회원 정보</h3>

        <div class="info-row">
          <span class="info-label">ID</span>
          <span class="info-value">{{ user.id }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">PW</span>
          <span class="info-value">************</span>
          <button class="action-btn" @click="goToResetPassword">비밀번호 재설정</button>
        </div>

        <div class="info-row">
          <span class="info-label">Phone</span>
          <span class="info-value">{{ user.phone }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">Name</span>
          <span class="info-value">{{ user.name }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">Birth</span>
          <span class="info-value highlight">{{ user.birth }}</span>
        </div>
      </section>

      <!-- Broker Info Card -->
      <section class="info-card broker">
        <div class="broker-header">
          <span class="broker-badge">증권 정보</span>
          <span class="broker-sub">*한국 투자</span>
        </div>

        <div class="info-row">
          <span class="info-label">계좌번호</span>
          <span class="info-value">{{ user.broker.accountNumber }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">APP_Key</span>
          <span class="info-value">{{ user.broker.appKey }}</span>
        </div>

        <div class="info-row">
          <span class="info-label">APP_Secret</span>
          <span class="info-value">{{ user.broker.appSecret }}</span>
        </div>

        <button class="link-btn" @click="handleGetAppKey">
          APP Key(Secret) 발급 >
        </button>

        <button class="save-small-btn">저장</button>
      </section>

      <!-- Save Button -->
      <button class="btn btn-save" @click="handleSave">
        저장
      </button>
    </div>

    <!-- Spacer for bottom nav -->
    <div class="bottom-spacer"></div>
  </div>
</template>

<style scoped>
.profile-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
  padding-bottom: var(--bottom-nav-height);
}

.icon-btn {
  background: none;
  border: none;
  color: var(--color-text-primary);
  cursor: pointer;
  padding: var(--spacing-sm);
}

.content {
  padding: var(--spacing-lg);
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: var(--spacing-xl);
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--color-bg-tertiary);
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.info-card {
  background: var(--color-bg-highlight);
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.card-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
  text-align: center;
  margin-bottom: var(--spacing-lg);
}

.info-row {
  display: flex;
  align-items: center;
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--color-border-light);
}

.info-row:last-of-type {
  border-bottom: none;
}

.info-label {
  width: 80px;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.info-value {
  flex: 1;
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
}

.info-value.highlight {
  color: var(--color-primary);
  background: var(--color-bg-primary);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-md);
  display: inline-block;
}

.action-btn {
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--color-bg-tertiary);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-xs);
  color: var(--color-text-primary);
  cursor: pointer;
}

.broker-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
  justify-content: center;
}

.broker-badge {
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--color-primary);
  color: var(--color-text-inverse);
  border-radius: var(--radius-full);
  font-size: var(--font-size-sm);
}

.broker-sub {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
}

.link-btn {
  display: block;
  width: 100%;
  background: none;
  border: none;
  font-size: var(--font-size-sm);
  color: var(--color-primary);
  cursor: pointer;
  text-align: center;
  margin-top: var(--spacing-md);
}

.save-small-btn {
  display: block;
  width: 100%;
  padding: var(--spacing-sm);
  background: var(--color-bg-tertiary);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  margin-top: var(--spacing-md);
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

.bottom-spacer {
  height: var(--bottom-nav-height);
}
</style>
