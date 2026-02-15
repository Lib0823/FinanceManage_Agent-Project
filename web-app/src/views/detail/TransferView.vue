<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'

const router = useRouter()

// 이체 가능 금액 (현금 상세 데이터에서 가져온다고 가정)
const availableAmount = ref(12000000)
const transferAmount = ref('')
const showSuccessModal = ref(false)

const formatNumber = (num) => {
  if (!num) return '0'
  return new Intl.NumberFormat('ko-KR').format(num)
}

// 입력값을 숫자로 변환
const numericTransferAmount = computed(() => {
  const cleaned = transferAmount.value.replace(/,/g, '')
  return parseInt(cleaned) || 0
})

// 이체 가능 여부
const canTransfer = computed(() => {
  return numericTransferAmount.value > 0 && numericTransferAmount.value <= availableAmount.value
})

// 금액 입력 핸들러
const handleAmountInput = (e) => {
  const value = e.target.value.replace(/[^0-9]/g, '')
  if (value) {
    transferAmount.value = formatNumber(parseInt(value))
  } else {
    transferAmount.value = ''
  }
}

// 빠른 금액 선택
const setQuickAmount = (amount) => {
  transferAmount.value = formatNumber(amount)
}

// 전액 선택
const setMaxAmount = () => {
  transferAmount.value = formatNumber(availableAmount.value)
}

// 이체 실행
const executeTransfer = () => {
  if (!canTransfer.value) return

  // TODO: 실제 이체 API 호출
  console.log('이체 금액:', numericTransferAmount.value)

  showSuccessModal.value = true

  // 2초 후 이전 화면으로 돌아가기
  setTimeout(() => {
    router.back()
  }, 2000)
}

const closeModal = () => {
  showSuccessModal.value = false
  router.back()
}
</script>

<template>
  <div class="transfer-screen">
    <AppHeader title="계좌이체" showBack />

    <div class="content">
      <!-- 이체 가능 금액 표시 -->
      <div class="available-section">
        <span class="available-label">이체 가능 금액</span>
        <span class="available-amount">{{ formatNumber(availableAmount) }}<span class="unit">원</span></span>
      </div>

      <!-- 이체 금액 입력 -->
      <div class="transfer-input-section">
        <label class="input-label">이체 금액</label>
        <div class="input-wrapper">
          <input
            type="text"
            class="amount-input"
            :value="transferAmount"
            @input="handleAmountInput"
            placeholder="0"
          />
          <span class="input-unit">원</span>
        </div>
      </div>

      <!-- 빠른 금액 선택 -->
      <div class="quick-amount-section">
        <button class="quick-btn" @click="setQuickAmount(100000)">+10만</button>
        <button class="quick-btn" @click="setQuickAmount(500000)">+50만</button>
        <button class="quick-btn" @click="setQuickAmount(1000000)">+100만</button>
        <button class="quick-btn max-btn" @click="setMaxAmount">전액</button>
      </div>

      <!-- 경고 메시지 -->
      <div v-if="numericTransferAmount > availableAmount" class="warning-message">
        이체 가능 금액을 초과했습니다.
      </div>

      <!-- 이체 버튼 -->
      <button
        class="transfer-submit-btn"
        :class="{ disabled: !canTransfer }"
        :disabled="!canTransfer"
        @click="executeTransfer"
      >
        이체하기
      </button>
    </div>

    <!-- 성공 모달 -->
    <van-popup
      v-model:show="showSuccessModal"
      round
      :style="{ width: '90%', maxWidth: '320px', background: '#1F2937', padding: '32px 24px' }"
    >
      <div class="success-modal">
        <div class="success-icon">✓</div>
        <h3 class="success-title">이체 완료</h3>
        <p class="success-message">{{ formatNumber(numericTransferAmount) }}원이 이체되었습니다.</p>
        <button class="success-btn" @click="closeModal">확인</button>
      </div>
    </van-popup>
  </div>
</template>

<style scoped>
.transfer-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
}

.content {
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

/* 이체 가능 금액 */
.available-section {
  background: var(--color-bg-secondary);
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
}

.available-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
}

.available-amount {
  font-size: 32px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
}

.unit {
  font-size: 20px;
  color: var(--color-text-secondary);
  margin-left: 4px;
}

/* 이체 금액 입력 */
.transfer-input-section {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.input-label {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.input-wrapper {
  position: relative;
  background: var(--color-bg-secondary);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  border: 2px solid transparent;
  transition: border-color 0.2s;
}

.input-wrapper:focus-within {
  border-color: #F59E0B;
}

.amount-input {
  width: 100%;
  background: transparent;
  border: none;
  outline: none;
  font-size: 28px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  text-align: right;
  padding-right: 40px;
}

.amount-input::placeholder {
  color: var(--color-text-tertiary);
}

.input-unit {
  position: absolute;
  right: var(--spacing-lg);
  top: 50%;
  transform: translateY(-50%);
  font-size: var(--font-size-lg);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-semibold);
}

/* 빠른 금액 선택 */
.quick-amount-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-sm);
}

.quick-btn {
  padding: var(--spacing-md);
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  cursor: pointer;
  transition: all 0.2s;
}

.quick-btn:hover {
  background: var(--color-bg-tertiary);
  border-color: #F59E0B;
}

.quick-btn:active {
  transform: scale(0.95);
}

.max-btn {
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  border: none;
  color: var(--color-text-inverse);
}

.max-btn:hover {
  background: linear-gradient(135deg, #D97706 0%, #B45309 100%);
}

/* 경고 메시지 */
.warning-message {
  padding: var(--spacing-md);
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid #EF4444;
  border-radius: var(--radius-md);
  font-size: var(--font-size-sm);
  color: #EF4444;
  text-align: center;
}

/* 이체 버튼 */
.transfer-submit-btn {
  width: 100%;
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  border: none;
  border-radius: var(--radius-lg);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-inverse);
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
  margin-top: auto;
}

.transfer-submit-btn:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(245, 158, 11, 0.4);
}

.transfer-submit-btn:active:not(.disabled) {
  transform: translateY(0);
}

.transfer-submit-btn.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: var(--color-bg-tertiary);
  box-shadow: none;
}

/* 성공 모달 */
.success-modal {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
}

.success-icon {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: white;
  font-weight: var(--font-weight-bold);
}

.success-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: #F9FAFB;
  margin: 0;
}

.success-message {
  font-size: var(--font-size-base);
  color: #9CA3AF;
  text-align: center;
  margin: 0;
}

.success-btn {
  width: 100%;
  padding: var(--spacing-md);
  background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-inverse);
  cursor: pointer;
  margin-top: var(--spacing-sm);
}
</style>
