<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import BottomNav from './components/common/BottomNav.vue'

const route = useRoute()

const showBottomNav = computed(() => route.meta.showBottomNav)
</script>

<template>
  <div class="app-container">
    <RouterView />
    <BottomNav v-if="showBottomNav" />
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
}
</style>
