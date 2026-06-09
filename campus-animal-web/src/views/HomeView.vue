<!--
  首页 — 项目横幅 + 搜索筛选 + 动物卡片列表。

  桌面端（>768px）：分页模式，9 张/页。
  移动端（≤768px）：无限滚动模式。

  数据流：
  - mounted 时调用 fetchList() + fetchTypes() + fetchTodayTop()
  - banner 展示项目名称、意义、当日打卡最多动物的路径图
-->
<template>
  <div class="page-container">
    <!-- ===== 项目横幅 ===== -->
    <div class="hero-banner">
      <div class="hero-left">
        <h1 class="hero-title">
          <span class="hero-icon">🐾</span> 校园动物图鉴
        </h1>
        <p class="hero-subtitle">
          发现、记录、守护校园里的每一个小生命
        </p>
        <p class="hero-desc">
          每一位同学都是校园生态的观察者。偶遇校园里的小动物时，
          用打卡记录它们的踪迹，一起绘制属于我们的校园动物地图。
        </p>
      </div>
      <div class="hero-right">
        <div class="daily-top-card">
          <div class="dt-header">
            <span class="dt-badge">今日偶遇最多</span>
          </div>
          <template v-if="todayTop && todayTop.hasData">
            <div class="dt-animal-name" @click="$router.push(`/animals/${todayTop.animalId}`)">
              {{ todayTop.animalName }} <span class="dt-count">×{{ todayTop.count }}</span>
            </div>
            <!-- 位置路径图 -->
            <div class="path-diagram" v-if="todayTop.path && todayTop.path.length > 1">
              <svg :viewBox="`0 0 ${pathSvgWidth} 80`" class="path-svg">
                <!-- 连接曲线 -->
                <path
                  v-for="(seg, i) in pathCurves"
                  :key="'curve-' + i"
                  :d="seg.d"
                  fill="none"
                  stroke="#5D9C7B"
                  stroke-width="2"
                  stroke-dasharray="6,3"
                  opacity="0.6"
                />
                <!-- 节点圆圈 + 标签 -->
                <g v-for="(pt, i) in pathPoints" :key="'pt-' + i">
                  <circle :cx="pt.x" cy="30" r="8" fill="#EAF5EF" stroke="#5D9C7B" stroke-width="2" />
                  <text :x="pt.x" y="30" text-anchor="middle" font-size="9" fill="#5D9C7B" dy="4">🐾</text>
                  <text :x="pt.x" y="60" text-anchor="middle" font-size="10" fill="#6B7F7A">
                    {{ pt.label }}
                  </text>
                  <text :x="pt.x" y="74" text-anchor="middle" font-size="9" fill="#999">
                    {{ pt.time }}
                  </text>
                </g>
              </svg>
            </div>
            <div v-else class="path-empty">只有一个打卡点，暂无路径</div>
          </template>
          <template v-else>
            <div class="dt-empty">
              <span class="dt-empty-icon">🌿</span>
              <p>今天还没有打卡记录</p>
              <p class="dt-empty-sub">成为今天第一个打卡的人吧</p>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- ===== 搜索栏 ===== -->
    <div class="search-bar">
      <el-input
        v-model="query.name"
        placeholder="搜索动物名称..."
        clearable
        size="large"
        class="search-input"
        @keyup.enter="onSearch"
        @clear="onSearch"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select v-model="query.type" placeholder="全部类型" clearable size="large"
        class="type-select" @change="onSearch">
        <el-option v-for="t in types" :key="t" :label="t" :value="t" />
      </el-select>

      <el-button size="large" type="primary" @click="onSearch">
        <el-icon><Search /></el-icon>
      </el-button>
    </div>

    <!-- ===== 卡片网格 ===== -->
    <div v-if="animals.length" class="card-grid">
      <AnimalCard v-for="a in animals" :key="a.id" :animal="a" />
    </div>
    <div v-else-if="!loading" class="empty-state">
      <div class="icon">🐾</div>
      <p>还没有动物档案</p>
      <p class="sub">管理员添加后这里会显示动物卡片</p>
    </div>

    <!-- ===== 桌面端：分页器 ===== -->
    <div v-if="!isMobile && total > 0" class="pagination-wrap">
      <el-pagination
        v-model:current-page="query.page"
        :page-size="query.size"
        :total="total"
        background
        layout="prev, pager, next"
        @current-change="fetchList"
      />
    </div>

    <!-- ===== 移动端：无限滚动状态指示 ===== -->
    <div v-if="isMobile && animals.length" class="scroll-status">
      <span v-if="loadingMore">加载中...</span>
      <span v-else-if="!hasMore">-- 已加载全部 --</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import api from '../api'
import AnimalCard from '../components/AnimalCard.vue'

const animals = ref([])
const types = ref([])
const total = ref(0)
const loading = ref(false)
const todayTop = ref(null)

const isMobile = ref(window.innerWidth <= 768)
const hasMore = ref(true)
const loadingMore = ref(false)
let mobilePage = 1

const query = reactive({ name: '', type: '', page: 1, size: 9 })

// 实时搜索：输入停止 300ms 后自动触发
let searchTimer = null
watch(() => query.name, () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(onSearch, 300)
})

function onSearch() {
  if (isMobile.value) {
    mobilePage = 1
    animals.value = []
    hasMore.value = true
    loadMobile()
  } else {
    query.page = 1
    fetchList()
  }
}

async function fetchList() {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (query.name) params.name = query.name
    if (query.type) params.type = query.type

    const res = await api.get('/animals', { params })
    animals.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadMobile() {
  if (!hasMore.value || loadingMore.value) return
  loadingMore.value = true
  try {
    const params = { page: mobilePage, size: query.size }
    if (query.name) params.name = query.name
    if (query.type) params.type = query.type

    const res = await api.get('/animals', { params })
    const records = res.data.records
    animals.value.push(...records)
    total.value = res.data.total

    if (records.length < query.size || animals.value.length >= total.value) {
      hasMore.value = false
    }
    mobilePage++
  } finally {
    loadingMore.value = false
  }
}

function onScroll() {
  if (!isMobile.value) return
  const { scrollTop, scrollHeight, clientHeight } = document.documentElement
  if (scrollHeight - scrollTop - clientHeight < 200) {
    loadMobile()
  }
}

function onResize() {
  const wasMobile = isMobile.value
  isMobile.value = window.innerWidth <= 768
  if (wasMobile !== isMobile.value) {
    mobilePage = 1
    hasMore.value = true
    query.page = 1
    animals.value = []
    total.value = 0
    fetchList()
  }
}

async function fetchTypes() {
  try {
    const res = await api.get('/animals/types')
    types.value = res.data
  } catch { /* API 错误已在拦截器中提示 */ }
}

async function fetchTodayTop() {
  try {
    const res = await api.get('/checkins/today-top')
    todayTop.value = res.data
  } catch { /* 忽略错误 */ }
}

// 路径图计算
const pathSvgWidth = computed(() => {
  if (!todayTop.value?.path) return 300
  return Math.max(300, todayTop.value.path.length * 100)
})

const pathPoints = computed(() => {
  if (!todayTop.value?.path) return []
  const pts = todayTop.value.path
  const spacing = pathSvgWidth.value / (pts.length + 1)
  return pts.map((pt, i) => ({
    x: spacing * (i + 1),
    label: pt.location?.length > 6 ? pt.location.slice(0, 6) + '..' : (pt.location || '未知'),
    time: pt.time ? pt.time.slice(11, 16) : ''
  }))
})

const pathCurves = computed(() => {
  const pts = pathPoints.value
  if (pts.length < 2) return []
  const curves = []
  for (let i = 1; i < pts.length; i++) {
    const x1 = pts[i - 1].x
    const x2 = pts[i].x
    const y = 30
    const cpY = i % 2 === 0 ? 10 : 50
    curves.push({
      d: `M ${x1} ${y} Q ${(x1 + x2) / 2} ${cpY} ${x2} ${y}`
    })
  }
  return curves
})

onMounted(() => {
  fetchList()
  fetchTypes()
  fetchTodayTop()
  window.addEventListener('scroll', onScroll, { passive: true })
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  clearTimeout(searchTimer)
  window.removeEventListener('scroll', onScroll)
  window.removeEventListener('resize', onResize)
})
</script>

<style scoped>
/* ===== 项目横幅 ===== */
.hero-banner {
  display: flex;
  gap: 32px;
  background: linear-gradient(135deg, #EAF5EF 0%, #D4EDDA 40%, #F0F7F4 100%);
  border-radius: 16px;
  padding: 36px 40px;
  margin-bottom: 28px;
  border: 1px solid rgba(93, 156, 123, 0.15);
  position: relative;
  overflow: hidden;
}

.hero-banner::before {
  content: '';
  position: absolute;
  top: -40px;
  right: -40px;
  width: 160px;
  height: 160px;
  background: rgba(93, 156, 123, 0.06);
  border-radius: 50%;
}

.hero-banner::after {
  content: '';
  position: absolute;
  bottom: -30px;
  left: 50%;
  width: 200px;
  height: 100px;
  background: rgba(240, 160, 75, 0.05);
  border-radius: 50%;
}

.hero-left {
  flex: 1;
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 30px;
  font-weight: 800;
  color: var(--text);
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.hero-icon { font-size: 36px; }

.hero-subtitle {
  font-size: 17px;
  color: var(--primary);
  font-weight: 600;
  margin-bottom: 12px;
}

.hero-desc {
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.8;
  max-width: 480px;
}

.hero-right {
  flex-shrink: 0;
  width: 340px;
  position: relative;
  z-index: 1;
}

/* 今日偶遇卡片 */
.daily-top-card {
  background: white;
  border-radius: 12px;
  padding: 20px 24px;
  box-shadow: 0 4px 20px rgba(93, 156, 123, 0.12);
  min-height: 160px;
}

.dt-header {
  margin-bottom: 12px;
}

.dt-badge {
  display: inline-block;
  background: linear-gradient(135deg, var(--amber), var(--amber-light));
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 20px;
}

.dt-animal-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 16px;
  cursor: pointer;
  transition: opacity 0.2s;
}
.dt-animal-name:hover { opacity: 0.7; }

.dt-count {
  font-size: 14px;
  color: var(--amber);
  font-weight: 600;
}

/* 路径图 */
.path-diagram {
  overflow-x: auto;
  overflow-y: hidden;
}

.path-svg {
  width: 100%;
  height: 80px;
}

.path-empty {
  text-align: center;
  color: var(--text-secondary);
  font-size: 13px;
  padding: 16px 0;
}

.dt-empty {
  text-align: center;
  padding: 12px 0;
}

.dt-empty-icon {
  font-size: 36px;
  opacity: 0.5;
  display: block;
  margin-bottom: 8px;
}

.dt-empty p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.dt-empty-sub {
  font-size: 12px !important;
  margin-top: 4px !important;
  opacity: 0.6;
}

/* ===== 搜索栏 ===== */
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}
.search-input { flex: 1; max-width: 400px; }
.type-select { width: 140px; }

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

.scroll-status {
  text-align: center;
  padding: 24px 0;
  color: var(--text-secondary);
  font-size: 13px;
}

.empty-state .sub { font-size: 13px; margin-top: 4px; }

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .hero-banner {
    flex-direction: column;
    padding: 24px;
  }

  .hero-title { font-size: 24px; }
  .hero-right { width: 100%; }

  .daily-top-card {
    min-height: auto;
  }
}

@media (max-width: 640px) {
  .search-bar { flex-wrap: wrap; }
  .search-input { max-width: 100%; }
  .type-select { width: 120px; }

  .hero-title { font-size: 20px; }
  .hero-subtitle { font-size: 15px; }
  .hero-desc { font-size: 13px; }
}
</style>
