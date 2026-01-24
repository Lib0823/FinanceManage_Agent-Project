<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const handleAgree = () => {
  localStorage.setItem('accessToken', 'mock-token')
  router.push('/home')
}

const handleDisagree = () => {
  router.push('/welcome')
}

const handleSendEmail = () => {
  console.log('Send terms via email')
}
</script>

<template>
  <div class="terms-screen">
    <div class="content">
      <!-- Header -->
      <div class="header">
        <div class="logo">
          <span class="logo-text">F</span>
          <span class="logo-dot">.</span>
        </div>
        <h1 class="title">이용 약관</h1>
      </div>

      <!-- Email Button -->
      <button class="email-btn" @click="handleSendEmail">
        이메일로 보내기
      </button>

      <!-- Terms Content -->
      <div class="terms-content">
        <section class="section">
          <h2 class="section-title">중요사항</h2>
          <p class="section-text">
            귀하의 iOS 기기를 사용하기 전에 다음의 약관을 읽으시기 바랍니다. 귀하는 iOS 기기를 사용함으로써, 귀하는 iOS 약관을 전적으로 따를 것에 동의하는 것입니다.
          </p>
        </section>

        <section class="section">
          <h2 class="section-title highlight">A. iOS 이용 약관</h2>
          <div class="expandable">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M9 18L15 12L9 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </section>

        <div class="terms-body">
          <p>
            귀하의 <strong>iOS</strong> 기기를 사용하기 전에 또는 본 사용권에 수반되는 소프트웨어 업데이트를 다운로드하기 전에 반드시 본 소프트웨어 사용권 약관(이하 '사용권')을 유의하여 읽어 주십시오. 귀하는 <strong>iOS</strong> 기기를 사용하거나 또는 소프트웨어 업데이트를 다운로드 함으로써 본 사용권 약관을 준수할 의무가 있음에 동의하는 것입니다.
          </p>
          <p>
            만일 귀하가 본 사용권 약관에 동의하지 않으면 <strong>iOS</strong> 기기를 사용하거나 소프트웨어 업데이트를 다운로드하지 마십시오. 귀하가 최근에 <strong>iOS</strong> 기기를 구입하였고 본 사용권 약관에 동의하지 않는 경우, 귀하는 <strong>http://www.apple.com/legal/sales_policies/</strong>에 명시된 <strong>Apple</strong>의 환불 정책에 따라 반환 기간 내에 <strong>iOS</strong> 기기를 구입한 <strong>Apple Store</strong>나 허가받은 유통업자에게 반환하면 환불받을 수 있습니다.
          </p>
        </div>
      </div>

      <!-- Actions -->
      <div class="actions">
        <button class="link-btn disagree" @click="handleDisagree">
          동의하지 않음
        </button>
        <button class="link-btn agree" @click="handleAgree">
          동의
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.terms-screen {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding: var(--spacing-3xl) var(--spacing-xl);
  background: var(--color-bg-primary);
}

.content {
  flex: 1;
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
  margin-bottom: var(--spacing-xl);
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

.title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.email-btn {
  background: none;
  border: none;
  font-size: var(--font-size-base);
  color: var(--color-primary);
  cursor: pointer;
  text-align: center;
  margin-bottom: var(--spacing-2xl);
}

.terms-content {
  flex: 1;
  overflow-y: auto;
}

.section {
  margin-bottom: var(--spacing-lg);
}

.section-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-sm);
}

.section-title.highlight {
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.expandable {
  color: var(--color-primary);
}

.section-text {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.terms-body {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  line-height: 1.8;
}

.terms-body p {
  margin-bottom: var(--spacing-lg);
}

.terms-body strong {
  color: var(--color-text-primary);
}

.actions {
  display: flex;
  justify-content: space-between;
  margin-top: var(--spacing-2xl);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--color-border-light);
}

.link-btn {
  background: none;
  border: none;
  font-size: var(--font-size-base);
  cursor: pointer;
}

.link-btn.disagree {
  color: var(--color-primary);
}

.link-btn.agree {
  color: var(--color-primary);
  font-weight: var(--font-weight-semibold);
}
</style>
