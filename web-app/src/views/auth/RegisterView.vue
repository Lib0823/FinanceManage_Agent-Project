<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { authApi } from '@/services/api'
import { Toast } from 'vant'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  id: '',
  password: '',
  passwordConfirm: '',
  email: '',
  name: '',
  phone: '',
  birth: ['1990', '01', '01']
})

const showDatePicker = ref(false)
const isIdChecked = ref(false)
const isIdAvailable = ref(false)
const isCheckingId = ref(false)
const isEmailChecked = ref(false)
const isEmailAvailable = ref(false)
const isCheckingEmail = ref(false)

// 컴포넌트 마운트 시 기존 데이터 복원
onMounted(() => {
  if (authStore.hasStep1Data()) {
    const step1Data = authStore.registrationData.step1
    const validation = authStore.registrationData.validation

    form.value.id = step1Data.id || ''
    form.value.password = step1Data.password || ''
    form.value.passwordConfirm = step1Data.passwordConfirm || ''
    form.value.email = step1Data.email || ''
    form.value.name = step1Data.name || ''
    form.value.phone = step1Data.phone || ''

    // birthDate를 birth 배열로 변환
    if (step1Data.birthDate) {
      const [year, month, day] = step1Data.birthDate.split('-')
      form.value.birth = [year, month, day]
    }

    // 중복 확인 상태 복원
    if (step1Data.id && validation.isIdChecked) {
      isIdChecked.value = true
      isIdAvailable.value = validation.isIdAvailable
    }

    if (step1Data.email && validation.isEmailChecked) {
      isEmailChecked.value = true
      isEmailAvailable.value = validation.isEmailAvailable
    }
  }
})

// 비밀번호 검증 상태
const passwordValidation = computed(() => {
  const password = form.value.password
  if (!password) return { valid: false, message: '' }

  if (password.length < 8) {
    return { valid: false, message: '비밀번호는 8자 이상이어야 합니다' }
  }

  return { valid: true, message: '사용 가능한 비밀번호입니다' }
})

const passwordMatchValidation = computed(() => {
  const password = form.value.password
  const passwordConfirm = form.value.passwordConfirm

  if (!passwordConfirm) return { valid: false, message: '' }

  if (password !== passwordConfirm) {
    return { valid: false, message: '비밀번호가 일치하지 않습니다' }
  }

  return { valid: true, message: '비밀번호가 일치합니다' }
})

// 다음 버튼 활성화 여부
const isFormValid = computed(() => {
  // 필수: 아이디, 비밀번호, 비밀번호 확인, 이름, 핸드폰
  return (
    form.value.id &&
    isIdChecked.value &&
    isIdAvailable.value &&
    passwordValidation.value.valid &&
    passwordMatchValidation.value.valid &&
    form.value.name &&
    form.value.phone &&
    // 이메일은 선택이지만 입력했다면 중복확인 필수
    (form.value.email ? (isEmailChecked.value && isEmailAvailable.value) : true)
  )
})

const formattedBirth = computed(() => {
  const [year, month, day] = form.value.birth
  return `${year}년 ${month}월 ${day}일`
})

const onConfirmDate = ({ selectedValues }) => {
  form.value.birth = selectedValues
  showDatePicker.value = false
}

const handleCheckDuplicate = async () => {
  if (!form.value.id) {
    Toast.fail('아이디를 입력해주세요')
    return
  }

  try {
    isCheckingId.value = true
    const response = await authApi.checkUsername(form.value.id)
    isIdChecked.value = true
    isIdAvailable.value = response.data.available

    if (response.data.available) {
      Toast.success('사용 가능한 아이디입니다')
      authStore.setIdCheckResult(true)
    } else {
      Toast.fail('이미 사용 중인 아이디입니다')
      authStore.setIdCheckResult(false)
    }
  } catch (error) {
    console.error('ID check error:', error)
    Toast.fail('아이디 확인 중 오류가 발생했습니다')
  } finally {
    isCheckingId.value = false
  }
}

const handleCheckEmail = async () => {
  if (!form.value.email) {
    Toast.fail('이메일을 입력해주세요')
    return
  }

  // 이메일 형식 검증
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(form.value.email)) {
    Toast.fail('올바른 이메일 형식이 아닙니다')
    return
  }

  try {
    isCheckingEmail.value = true
    const response = await authApi.checkEmail(form.value.email)
    isEmailChecked.value = true
    isEmailAvailable.value = response.data.available

    if (response.data.available) {
      Toast.success('사용 가능한 이메일입니다')
      authStore.setEmailCheckResult(true)
    } else {
      Toast.fail('이미 사용 중인 이메일입니다')
      authStore.setEmailCheckResult(false)
    }
  } catch (error) {
    console.error('Email check error:', error)
    Toast.fail('이메일 확인 중 오류가 발생했습니다')
  } finally {
    isCheckingEmail.value = false
  }
}

const handleNext = () => {
  // 유효성 검사
  if (!form.value.id) {
    Toast.fail('아이디를 입력해주세요')
    return
  }

  if (!isIdChecked.value || !isIdAvailable.value) {
    Toast.fail('아이디 중복 확인을 해주세요')
    return
  }

  if (!form.value.password) {
    Toast.fail('비밀번호를 입력해주세요')
    return
  }

  if (form.value.password.length < 8) {
    Toast.fail('비밀번호는 8자 이상이어야 합니다')
    return
  }

  if (form.value.password !== form.value.passwordConfirm) {
    Toast.fail('비밀번호가 일치하지 않습니다')
    return
  }

  // 이메일 입력 시 중복 확인 필수
  if (form.value.email && (!isEmailChecked.value || !isEmailAvailable.value)) {
    Toast.fail('이메일 중복 확인을 해주세요')
    return
  }

  if (!form.value.name) {
    Toast.fail('이름을 입력해주세요')
    return
  }

  if (!form.value.phone) {
    Toast.fail('핸드폰 번호를 입력해주세요')
    return
  }

  // Pinia store에 데이터 저장
  const [year, month, day] = form.value.birth
  const birthDate = `${year}-${month.padStart(2, '0')}-${day.padStart(2, '0')}`

  // 전화번호에서 하이픈 제거 (백엔드는 숫자만 받음)
  const phoneNumber = form.value.phone.replace(/[^0-9]/g, '')

  authStore.saveStep1Data({
    id: form.value.id,
    password: form.value.password,
    passwordConfirm: form.value.passwordConfirm,
    email: form.value.email || null,
    name: form.value.name,
    phone: phoneNumber,
    birthDate: birthDate
  })

  router.push('/register/finance')
}
</script>

<template>
  <div class="register-screen">
    <div class="content">
      <!-- Header -->
      <div class="header">
        <div class="logo">
          <span class="logo-text">F</span>
          <span class="logo-dot">.</span>
        </div>
        <div class="title-group">
          <h1 class="title">회원가입</h1>
          <p class="step-indicator">• 기본 정보</p>
        </div>
      </div>

      <!-- Form -->
      <div class="form">
        <!-- ID -->
        <div class="form-group">
          <label class="label"><span class="required">*</span> 아이디</label>
          <div class="input-with-btn">
            <input
              v-model="form.id"
              type="text"
              class="input"
              :class="{ 'input-success': isIdChecked && isIdAvailable, 'input-error': isIdChecked && !isIdAvailable }"
              placeholder="ID"
              @input="isIdChecked = false"
            />
            <button class="inline-btn" @click="handleCheckDuplicate" :disabled="isCheckingId">
              {{ isCheckingId ? '확인 중...' : '중복 확인' }}
            </button>
          </div>
          <span v-if="isIdChecked && isIdAvailable" class="validation-message success">✓ 사용 가능한 아이디입니다</span>
          <span v-if="isIdChecked && !isIdAvailable" class="validation-message error">✗ 이미 사용 중인 아이디입니다</span>
        </div>

        <!-- Password -->
        <div class="form-group">
          <label class="label"><span class="required">*</span> 비밀번호</label>
          <input
            v-model="form.password"
            type="password"
            class="input"
            :class="{
              'input-success': form.password && passwordValidation.valid,
              'input-error': form.password && !passwordValidation.valid
            }"
            placeholder="Password (8자 이상)"
          />
          <span v-if="form.password && passwordValidation.valid" class="validation-message success">
            ✓ {{ passwordValidation.message }}
          </span>
          <span v-if="form.password && !passwordValidation.valid" class="validation-message error">
            ✗ {{ passwordValidation.message }}
          </span>
        </div>

        <!-- Password Confirm -->
        <div class="form-group">
          <label class="label"><span class="required">*</span> 비밀번호 확인</label>
          <input
            v-model="form.passwordConfirm"
            type="password"
            class="input"
            :class="{
              'input-success': form.passwordConfirm && passwordMatchValidation.valid,
              'input-error': form.passwordConfirm && !passwordMatchValidation.valid
            }"
            placeholder="Password Check"
          />
          <span v-if="form.passwordConfirm && passwordMatchValidation.valid" class="validation-message success">
            ✓ {{ passwordMatchValidation.message }}
          </span>
          <span v-if="form.passwordConfirm && !passwordMatchValidation.valid" class="validation-message error">
            ✗ {{ passwordMatchValidation.message }}
          </span>
        </div>

        <!-- Email -->
        <div class="form-group">
          <label class="label">이메일</label>
          <div class="input-with-btn">
            <input
              v-model="form.email"
              type="email"
              class="input"
              :class="{ 'input-success': isEmailChecked && isEmailAvailable, 'input-error': isEmailChecked && !isEmailAvailable }"
              placeholder="Email"
              @input="isEmailChecked = false"
            />
            <button class="inline-btn" @click="handleCheckEmail" :disabled="isCheckingEmail">
              {{ isCheckingEmail ? '확인 중...' : '중복 확인' }}
            </button>
          </div>
          <span v-if="isEmailChecked && isEmailAvailable" class="validation-message success">✓ 사용 가능한 이메일입니다</span>
          <span v-if="isEmailChecked && !isEmailAvailable" class="validation-message error">✗ 이미 사용 중인 이메일입니다</span>
        </div>

        <!-- Name -->
        <div class="form-group">
          <label class="label"><span class="required">*</span> 이름</label>
          <input
            v-model="form.name"
            type="text"
            class="input"
            placeholder="Name"
          />
        </div>

        <!-- Phone -->
        <div class="form-group">
          <label class="label"><span class="required">*</span> 핸드폰</label>
          <input
            v-model="form.phone"
            type="tel"
            class="input"
            placeholder="010-1234-5678"
          />
        </div>

        <!-- Birth Date -->
        <div class="form-group">
          <label class="label">생년월일</label>
          <div class="date-display" @click="showDatePicker = true">
            <span class="date-value">{{ formattedBirth }}</span>
            <van-icon name="arrow-down" class="date-icon" />
          </div>
        </div>

        <!-- Date Picker Popup -->
        <van-popup v-model:show="showDatePicker" position="bottom" round>
          <van-date-picker
            v-model="form.birth"
            title="생년월일 선택"
            :min-date="new Date(1900, 0, 1)"
            :max-date="new Date()"
            @confirm="onConfirmDate"
            @cancel="showDatePicker = false"
          />
        </van-popup>

        <!-- Next Button -->
        <button
          class="btn btn-next"
          :class="{ 'btn-disabled': !isFormValid }"
          :disabled="!isFormValid"
          @click="handleNext"
        >
          다음
        </button>

      </div>
    </div>
  </div>
</template>

<style scoped>
.register-screen {
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
  align-items: flex-start;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-2xl);
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: var(--color-primary);
  border-radius: var(--radius-lg);
  flex-shrink: 0;
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

.title-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.step-indicator {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.label {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.required {
  color: #EF4444;
  margin-right: 2px;
}

.input {
  flex: 1;
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

.input-with-btn {
  display: flex;
  gap: var(--spacing-sm);
  align-items: flex-end;
}

.inline-btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--color-bg-tertiary);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-xs);
  color: var(--color-text-primary);
  cursor: pointer;
  white-space: nowrap;
}

.inline-btn.orange {
  background: #F97316;
  color: var(--color-text-inverse);
}

.inline-btn:hover {
  opacity: 0.9;
}

.date-display {
  padding: var(--spacing-md);
  background: var(--color-bg-highlight);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
}

.date-display:active {
  opacity: 0.8;
}

.date-value {
  font-size: var(--font-size-base);
  color: var(--color-primary);
  font-weight: var(--font-weight-medium);
}

.date-icon {
  color: var(--color-text-tertiary);
  font-size: var(--font-size-md);
}

.btn-next {
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

.btn-next:hover {
  background: var(--color-primary-dark);
}

.validation-message {
  font-size: var(--font-size-xs);
  margin-top: 4px;
  display: block;
}

.validation-message.success {
  color: #10B981;
}

.validation-message.error {
  color: #EF4444;
}

.input-success {
  border-bottom-color: #10B981 !important;
}

.input-error {
  border-bottom-color: #EF4444 !important;
}

.inline-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: var(--color-bg-tertiary) !important;
  color: var(--color-text-tertiary) !important;
}

.form-requirements {
  margin-top: var(--spacing-lg);
  padding: var(--spacing-md);
  background: var(--color-bg-highlight);
  border-radius: var(--radius-md);
  border-left: 3px solid var(--color-primary);
}

.requirements-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-sm);
}

.requirements-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.requirements-list li {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  padding: var(--spacing-xs) 0;
}

.requirements-list li.completed {
  color: #10B981;
  font-weight: var(--font-weight-medium);
}
</style>
