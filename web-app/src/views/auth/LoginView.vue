<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/services/api'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  username: '',
  password: ''
})

const autoLogin = ref(true)
const loading = ref(false)
const errorMessage = ref('')

// Load auto-login preference from localStorage
onMounted(() => {
  const savedUiSettings = localStorage.getItem('uiSettings')
  if (savedUiSettings) {
    const uiSettings = JSON.parse(savedUiSettings)
    if (uiSettings.autoLogin !== undefined) {
      autoLogin.value = uiSettings.autoLogin
    }
  }
})

const handleLogin = async (event) => {
  // 기본 폼 제출 방지
  if (event) {
    event.preventDefault()
  }

  if (!form.value.username || !form.value.password) {
    errorMessage.value = '아이디와 비밀번호를 입력해주세요'
    return
  }

  loading.value = true
  errorMessage.value = ''

  try {
    const response = await authApi.login({
      username: form.value.username,
      password: form.value.password
    })

    // Save auto-login preference
    const uiSettings = JSON.parse(localStorage.getItem('uiSettings') || '{}')
    uiSettings.autoLogin = autoLogin.value
    localStorage.setItem('uiSettings', JSON.stringify(uiSettings))

    // Use auth store to manage authentication state
    // api.js interceptor가 response.data를 반환하므로
    // response = { success, message, data: { accessToken, refreshToken, user } }
    console.log('[Login] Response:', response)
    authStore.setAuthData({
      accessToken: response.data.accessToken,
      refreshToken: response.data.refreshToken,
      user: response.data.user
    })

    // Navigate to home
    router.push('/home')
  } catch (error) {
    console.error('Login failed:', error)

    // 로그인 화면에서는 401 에러를 정상적으로 처리 (인터셉터 우회)
    if (error.response?.status === 401) {
      errorMessage.value = '아이디 또는 비밀번호가 일치하지 않습니다'
    } else if (error.response?.data?.message) {
      errorMessage.value = error.response.data.message
    } else {
      errorMessage.value = '로그인에 실패했습니다'
    }
    // 입력값 유지 (form.value는 그대로 둠)
  } finally {
    loading.value = false
  }
}

const goToResetPassword = () => {
  router.push('/reset-password')
}
</script>

<template>
  <div class="login-screen">
    <div class="content">
      <!-- Logo & Title -->
      <div class="header">
        <div class="logo">
          <span class="logo-text">F</span>
          <span class="logo-dot">.</span>
        </div>
        <h1 class="title">로그인</h1>
      </div>

      <!-- Form -->
      <form class="form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="label">ID</label>
          <input
            v-model="form.username"
            type="text"
            class="input"
            placeholder="Input ID"
            :disabled="loading"
            autocomplete="username"
          />
        </div>

        <div class="form-group">
          <label class="label">PW</label>
          <input
            v-model="form.password"
            type="password"
            class="input"
            placeholder="Input Password"
            :disabled="loading"
            autocomplete="current-password"
            @keydown.enter.prevent="handleLogin"
          />
        </div>

        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <button type="submit" class="btn btn-login" :disabled="loading">
          {{ loading ? '로그인 중...' : '로그인' }}
        </button>

        <div class="options">
          <label class="checkbox-wrapper">
            <input type="checkbox" v-model="autoLogin" />
            <span class="checkmark"></span>
            <span class="checkbox-label">자동 로그인</span>
          </label>

          <button type="button" class="link-btn" @click="goToResetPassword">
            비밀번호 재설정
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.login-screen {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: var(--spacing-3xl) var(--spacing-xl);
  background: var(--color-bg-primary);
}

.content {
  display: flex;
  flex-direction: column;
  max-width: 340px;
  margin: 0 auto;
  width: 100%;
}

.header {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-3xl);
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: var(--color-primary);
  border-radius: var(--radius-lg);
}

.logo-text {
  font-size: 28px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-inverse);
}

.logo-dot {
  font-size: 28px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-inverse);
}

.title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.label {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.input {
  padding: var(--spacing-md) 0;
  border: none;
  border-bottom: 1px solid var(--color-border);
  font-size: var(--font-size-base);
  outline: none;
  background: transparent;
}

.input::placeholder {
  color: var(--color-text-tertiary);
}

.input:focus {
  border-bottom-color: var(--color-primary);
}

.input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  padding: var(--spacing-sm) 0;
  color: #DC2626;
  font-size: var(--font-size-sm);
  text-align: left;
  font-weight: var(--font-weight-medium);
  margin-top: calc(var(--spacing-xl) * -0.5);
}

.btn-login {
  width: 100%;
  padding: var(--spacing-lg);
  background: var(--color-primary);
  color: var(--color-text-inverse);
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  margin-top: var(--spacing-lg);
}

.btn-login:hover:not(:disabled) {
  background: var(--color-primary-dark);
}

.btn-login:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
}

.checkbox-wrapper input {
  width: 20px;
  height: 20px;
  accent-color: var(--color-success);
}

.checkbox-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
}

.link-btn {
  background: none;
  border: none;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
}

.link-btn:hover {
  color: var(--color-primary);
}
</style>
