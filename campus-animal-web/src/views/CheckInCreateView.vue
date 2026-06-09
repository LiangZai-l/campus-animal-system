<!--
  发布打卡页面。

  路由：/checkin/create（meta.auth: true，需登录）
  可选 query 参数：?animalId=123 — 从动物详情页进入时自动预选动物

  布局：居中表单卡片（600px 宽）
  字段：
  - 选择动物（可搜索下拉，从动物详情进入时预选且不可修改）
  - 打卡内容（多行文本域，必填）
  - 心情（下拉选择：开心/一般/担心）
  - 地点（文本输入）

  提交流程：
  1. 表单校验（animalId + content 必填）
  2. POST /api/checkins → 打卡成功
  3. 跳转到动物详情页（/animals/:animalId），查看打卡时间轴

  动物列表：mounted 时获取全部动物（page=1, size=100）填充下拉选项。
-->
<template>
  <div class="page-container">
    <!-- 返回按钮：卡片外，仅从动物详情页进入时显示 -->
    <div v-if="preselectedAnimalId" class="back-bar">
      <el-button text @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回动物详情
      </el-button>
    </div>

    <div class="form-card">
      <h2>发布打卡</h2>
      <p class="form-sub">记录你与校园动物的偶遇时刻</p>

      <el-form ref="formRef" :model="form" :rules="rules"
        label-position="top" size="large" @submit.prevent="submit">

        <!-- 动物选择（filterable 支持搜索） -->
        <el-form-item label="选择动物" prop="animalId">
          <el-select v-model="form.animalId" placeholder="搜索并选择动物..."
            filterable :disabled="!!preselectedAnimalId" class="full-width">
            <el-option v-for="a in animals" :key="a.id"
              :label="`${a.name} (${a.type} · ${a.area})`" :value="a.id" />
          </el-select>
        </el-form-item>

        <!-- 打卡内容（必填） -->
        <el-form-item label="打卡内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3"
            placeholder="描述一下你看到它时的情景吧..." />
        </el-form-item>

        <!-- 心情 + 地点（并排） -->
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="心情">
              <el-select v-model="form.mood" placeholder="选择心情" clearable class="full-width">
                <el-option label="开心" value="happy" />
                <el-option label="一般" value="neutral" />
                <el-option label="担心" value="worried" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地点">
              <el-input v-model="form.location" placeholder="如：图书馆门口" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
            发布打卡
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const router = useRouter()
const route = useRoute()
const animals = ref([])
const loading = ref(false)

const preselectedAnimalId = ref(null)

const formRef = ref(null)
const form = reactive({
  animalId: null, content: '', mood: '', location: ''
})

const rules = {
  animalId: [{ required: true, message: '请选择动物', trigger: 'change' }],
  content: [{ required: true, message: '请输入打卡内容', trigger: 'blur' }]
}

function goBack() {
  router.back()
}

async function submit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await api.post('/checkins', { ...form })
    ElMessage.success('打卡成功')
    router.push(`/animals/${res.data.animalId}`)
  } catch {
    // API 错误已在响应拦截器中提示
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  // 解析 URL 中的 animalId 参数
  const animalIdFromQuery = route.query.animalId
  if (animalIdFromQuery) {
    preselectedAnimalId.value = Number(animalIdFromQuery)
  }

  const res = await api.get('/animals', { params: { page: 1, size: 100 } })
  animals.value = res.data.records

  // 如果 URL 中有 animalId，预选中对应动物
  if (preselectedAnimalId.value) {
    form.animalId = preselectedAnimalId.value
  }
})
</script>

<style scoped>
.form-card {
  max-width: 600px;
  margin: 20px auto;
  background: white;
  border-radius: var(--radius);
  padding: 36px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.05);
}
.form-card h2 { font-size: 22px; margin-bottom: 4px; }
.form-sub { font-size: 14px; color: var(--text-secondary); margin-bottom: 28px; }
.full-width { width: 100%; }
.submit-btn { width: 100%; }

.back-bar {
  margin-bottom: 16px;
}
</style>
