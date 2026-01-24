<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

onMounted(() => {
  // Redirect to welcome after 2 seconds
  setTimeout(() => {
    router.push('/welcome')
  }, 2000)
})
</script>

<template>
  <div class="splash-screen">
    <div class="logo-container">
      <div class="logo">
        <span class="logo-text">F</span>
        <span class="logo-dot">.</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.splash-screen {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-primary);
}

.logo-container {
  animation: fadeIn 0.5s ease-out;
}

.logo {
  display: flex;
  align-items: baseline;
}

.logo-text {
  font-size: 72px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-inverse);
}

.logo-dot {
  font-size: 72px;
  font-weight: var(--font-weight-bold);
  color: var(--color-text-inverse);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
