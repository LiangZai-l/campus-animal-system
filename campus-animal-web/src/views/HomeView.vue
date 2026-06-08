<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="query.name"
        placeholder="搜索动物名称..."
        clearable
        size="large"
        class="search-input"
        @keyup.enter="fetchList"
        @clear="fetchList"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="query.type" placeholder="全部类型" clearable size="large" class="type-select" @change="fetchList">
        <el-option v-for="t in types" :key="t" :label="t" :value="t" />
      </el-select>
      <el-button size="large" type="primary" @click="fetchList">
        <el-icon><Search /></el-icon>
      </el-button>
    </div>

    <!-- 卡片列表 -->
    <div v-if="animals.length" class="card-grid">
      <AnimalCard v-for="a in animals" :key="a.id" :animal="a" />
    </div>
    <div v-else-if="!loading" class="empty-state">
      <div class="icon">🐾</div>
      <p>还没有动物档案</p>
      <p class="sub">管理员添加后这里会显示动物卡片</p>
    </div>

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-wrap">
      <el-pagination
        v-model:current-page="query.page"
        :page-size="query.size"
        :total="total"
        background
        layout="prev, pager, next"
        @current-change="fetchList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import api from '../api'
import AnimalCard from '../components/AnimalCard.vue'

const animals = ref([])
const types = ref([])
const total = ref(0)
const loading = ref(false)

const query = reactive({ name: '', type: '', page: 1, size: 12 })

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

async function fetchTypes() {
  const res = await api.get('/animals/types')
  types.value = res.data
}

onMounted(() => {
  fetchList()
  fetchTypes()
})
</script>

<style scoped>
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

.empty-state .sub {
  font-size: 13px;
  margin-top: 4px;
}

@media (max-width: 640px) {
  .search-bar { flex-wrap: wrap; }
  .search-input { max-width: 100%; }
  .type-select { width: 120px; }
}
</style>
