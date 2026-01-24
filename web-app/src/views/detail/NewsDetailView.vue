<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import { mockNewsDetail } from '@/services/mockData'

const route = useRoute()
const router = useRouter()

const news = ref(mockNewsDetail)

const goToRelatedNews = (relatedNews) => {
  router.push(`/news/${relatedNews.id}`)
}

onMounted(() => {
  // Fetch news detail based on route.params.id
})
</script>

<template>
  <div class="news-detail-screen">
    <AppHeader title="기사 내용" showBack />

    <div class="content">
      <!-- News Meta -->
      <div class="news-meta">
        <span class="news-source">{{ news.source }}</span>
        <span class="news-date">{{ news.date }}</span>
      </div>

      <!-- News Title -->
      <h1 class="news-title">{{ news.title }}</h1>

      <!-- News Image -->
      <div class="news-image" v-if="news.image">
        <img :src="news.image" :alt="news.title" />
      </div>

      <!-- Tags -->
      <div class="news-tags">
        <span v-for="tag in news.tags" :key="tag" class="tag">{{ tag }}</span>
      </div>

      <!-- News Content -->
      <div class="news-content-card">
        <p class="news-content">{{ news.content }}</p>
      </div>

      <!-- Related News -->
      <section class="related-section">
        <h2 class="section-title">관련 뉴스</h2>
        <div class="related-list">
          <div
            v-for="related in news.relatedNews"
            :key="related.id"
            class="related-item"
            @click="goToRelatedNews(related)"
          >
            <div class="related-thumb">
              <img :src="related.image" :alt="related.title" />
            </div>
            <span class="related-title">{{ related.title }}</span>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.news-detail-screen {
  min-height: 100vh;
  background: var(--color-bg-primary);
}

.content {
  padding: var(--spacing-lg);
}

.news-meta {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.news-source {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

.news-date {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

.news-title {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  line-height: 1.4;
  margin-bottom: var(--spacing-lg);
}

.news-image {
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: var(--spacing-lg);
}

.news-image img {
  width: 100%;
  height: auto;
}

.news-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
}

.tag {
  padding: var(--spacing-xs) var(--spacing-md);
  background: var(--color-bg-secondary);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
}

.news-content-card {
  background: #FEF3C7;
  border-radius: var(--radius-xl);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.news-content {
  font-size: var(--font-size-base);
  color: var(--color-text-primary);
  line-height: 1.8;
  white-space: pre-line;
}

.related-section {
  margin-top: var(--spacing-xl);
}

.section-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
  text-align: center;
  margin-bottom: var(--spacing-md);
}

.related-list {
  display: flex;
  gap: var(--spacing-md);
  overflow-x: auto;
  padding-bottom: var(--spacing-sm);
}

.related-item {
  flex: 0 0 100px;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  cursor: pointer;
}

.related-thumb {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-md);
  overflow: hidden;
}

.related-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.related-title {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
