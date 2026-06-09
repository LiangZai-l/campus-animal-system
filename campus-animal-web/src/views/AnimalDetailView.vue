<!--
  动物详情页 — 展示档案信息 + 打卡时间轴。

  页面路由：/animals/:id
  通过 route.params.id 获取动物 ID。

  布局结构：
  1. 返回按钮（router.back()，保持浏览位置）
  2. 顶部 detail-hero（白色卡片）：封面图 + 名称 + 标签 + 描述 + 投喂次数
  3. 打卡时间轴卡片：标题 + "我来打卡"按钮 + CheckInTimeline 组件

  数据流：
  - mounted 时调用 GET /api/animals/:id
  - 返回的 AnimalDetailVO 包含 animal 基础字段 + timeline 数组
  - timeline 传递给 CheckInTimeline 组件展示
-->
<template>
  <div class="page-container" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="back-bar">
      <el-button text @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </el-button>
    </div>

    <template v-if="animal">
      <!-- ===== 顶部信息卡片 ===== -->
      <div class="detail-hero">
        <div class="hero-cover">
          <img v-if="animal.coverImage" :src="animal.coverImage" :alt="animal.name" />
          <div v-else class="hero-placeholder">🐾</div>
        </div>

        <div class="hero-info">
          <h1>{{ animal.name }}</h1>

          <div class="hero-tags">
            <el-tag size="large" :type="animal.status === 1 ? 'success' : 'info'">
              {{ animal.status === 1 ? '在校' : '已离校' }}
            </el-tag>
            <el-tag size="large">{{ animal.type }}</el-tag>
            <span class="hero-area">
              <el-icon><LocationFilled /></el-icon> {{ animal.area }}
            </span>
          </div>

          <p class="hero-desc" v-if="animal.description">{{ animal.description }}</p>

          <div class="hero-meta">
            <span>投喂 {{ animal.feederCount || 0 }} 次</span>
            <span v-if="animal.createdAt">录入于 {{ formatDate(animal.createdAt) }}</span>
          </div>
        </div>
      </div>

      <!-- ===== 打卡时间轴区域 ===== -->
      <div class="detail-section">
        <div class="section-header">
          <h3>📝 打卡时间轴</h3>
          <el-button type="primary" size="small" @click="goCheckIn">
            我来打卡
          </el-button>
        </div>
        <CheckInTimeline :items="animal.timeline || []" />
      </div>
    </template>

    <div v-else-if="!loading && fetchError" class="empty-state">
      <div class="icon">⚠️</div>
      <p>加载失败，请刷新重试</p>
    </div>
    <div v-else-if="!loading" class="empty-state">
      <div class="icon">🔍</div>
      <p>动物不存在或已被删除</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api'
import CheckInTimeline from '../components/CheckInTimeline.vue'

const route = useRoute()
const router = useRouter()
const animal = ref(null)
const loading = ref(true)
const fetchError = ref(false)

function formatDate(str) {
  if (!str) return ''
  return str.split('T')[0]
}

function goBack() {
  router.push('/')
}

function goCheckIn() {
  router.push(`/checkin/create?animalId=${route.params.id}`)
}

onMounted(async () => {
  try {
    const res = await api.get(`/animals/${route.params.id}`)
    animal.value = res.data
  } catch {
    fetchError.value = true
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.back-bar {
  margin-bottom: 16px;
}

.detail-hero {
  display: flex;
  gap: 28px;
  background: white;
  border-radius: var(--radius);
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  margin-bottom: 24px;
}

.hero-cover {
  width: 280px;
  height: 210px;
  border-radius: var(--radius);
  overflow: hidden;
  flex-shrink: 0;
  background: var(--primary-bg);
  display: flex;
  align-items: center;
  justify-content: center;
}
.hero-cover img { width: 100%; height: 100%; object-fit: cover; }
.hero-placeholder { font-size: 72px; opacity: 0.5; }

.hero-info { flex: 1; }
.hero-info h1 { font-size: 26px; margin-bottom: 14px; }
.hero-tags { display: flex; align-items: center; gap: 10px; margin-bottom: 14px; }
.hero-area { font-size: 14px; color: var(--text-secondary); display: flex; align-items: center; gap: 2px; }
.hero-desc { font-size: 15px; color: var(--text-secondary); line-height: 1.7; margin-bottom: 12px; }
.hero-meta { font-size: 13px; color: var(--text-secondary); display: flex; gap: 20px; }

.detail-section {
  background: white;
  border-radius: var(--radius);
  padding: 24px 28px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.section-header h3 { font-size: 18px; }

@media (max-width: 768px) {
  .detail-hero { flex-direction: column; }
  .hero-cover { width: 100%; height: 200px; }
}
</style>
