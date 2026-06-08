<template>
  <div class="page-container">
    <div class="form-card">
      <h2>发布打卡</h2>
      <p class="form-sub">记录你与校园动物的偶遇时刻</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large" @submit.prevent="submit">
        <el-form-item label="选择动物" prop="animalId">
          <el-select v-model="form.animalId" placeholder="搜索并选择动物..." filterable class="full-width">
            <el-option v-for="a in animals" :key="a.id" :label="`${a.name} (${a.type} · ${a.area})`" :value="a.id" />
          </el-select>
        </el-form-item>

        <el-form-item label="打卡内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="描述一下你看到它时的情景吧..." />
        </el-form-item>

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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const router = useRouter()
const animals = ref([])
const loading = ref(false)

const formRef = ref(null)
const form = reactive({ animalId: null, content: '', mood: '', location: '' })
const rules = {
  animalId: [{ required: true, message: '请选择动物', trigger: 'change' }],
  content: [{ required: true, message: '请输入打卡内容', trigger: 'blur' }]
}

async function submit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await api.post('/checkins', { ...form })
    ElMessage.success('打卡成功')
    router.push(`/animals/${res.data.animalId}`)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  const res = await api.get('/animals', { params: { page: 1, size: 100 } })
  animals.value = res.data.records
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
</style>
