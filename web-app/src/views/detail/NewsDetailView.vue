<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/common/AppHeader.vue'
import { mockNewsDetail } from '@/services/mockData'

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
      <!-- News Title -->
      <h1 class="news-title">{{ news.title }}</h1>

      <!-- News Meta -->
      <div class="news-meta">
        <span class="news-source">{{ news.source }}</span>
        <span class="news-date">{{ news.date }}</span>
      </div>

      <!-- Tags -->
      <div class="news-tags">
        <span v-for="(tag, index) in news.tags" :key="tag" :class="['tag', `tag-${index % 4}`]">{{ tag }}</span>
      </div>

      <!-- News Image -->
      <div class="news-image" v-if="news.image">
        <img :src="news.image" :alt="news.title" />
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
  padding-bottom: var(--spacing-2xl);
}

.news-title {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  line-height: 1.4;
  text-align: center;
  margin-bottom: var(--spacing-md);
  padding: 0 var(--spacing-md);
}

.news-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.news-source {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  font-weight: var(--font-weight-medium);
  position: relative;
}

.news-source::after {
  content: '';
  position: absolute;
  right: calc(var(--spacing-md) / -2 - 0.5px);
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 12px;
  background: var(--color-text-tertiary);
}

.news-date {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

.news-image {
  border-radius: var(--radius-lg);
  overflow: hidden;
  margin-bottom: var(--spacing-xl);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.news-image img {
  width: 100%;
  height: auto;
  display: block;
}

.news-tags {
  display: flex;
  flex-wrap: nowrap;

  overflow-x: auto;
  -webkit-overflow-scrolling: touch;

  justify-content: flex-start;

  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
  padding: 0 var(--spacing-md);

  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}

/* 크롬, 사파리에서 스크롤바 숨기기 */
.news-tags::-webkit-scrollbar {
  display: none;
}

.tag {
  flex: 0 0 auto;

  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--radius-full);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  background: var(--color-bg-secondary);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-bg-tertiary);
  transition: all 0.2s;
  cursor: default;
}

.news-content-card {
  border-radius: var(--radius-xl);
  padding: var(--spacing-sm);
  margin-bottom: var(--spacing-3xl);
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
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  text-align: left;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--color-bg-secondary);
}

.related-list {
  display: flex;
  gap: var(--spacing-md);
  overflow-x: auto;
  padding-bottom: var(--spacing-sm);
}

.related-item {
  flex: 0 0 120px;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
  cursor: pointer;
  transition: transform 0.2s;
}

.related-item:hover {
  transform: translateY(-4px);
}

.related-thumb {
  width: 120px;
  height: 120px;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.2s;
}

.related-item:hover .related-thumb {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
}

.related-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.related-title {
  font-size: var(--font-size-xs);
  color: var(--color-text-secondary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-weight: var(--font-weight-medium);
}
</style>
