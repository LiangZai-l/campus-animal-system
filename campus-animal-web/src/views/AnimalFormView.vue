<template>
  <div class="page-container">
    <div class="form-card">
      <h2>{{ isEdit ? '编辑动物档案' : '新增动物档案' }}</h2>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large" @submit.prevent="submit" v-loading="loading">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="给动物取个名字" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="类型" prop="type">
              <el-input v-model="form.type" placeholder="猫/狗/鸟/其他" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" class="full-width">
                <el-option label="在校" :value="1" />
                <el-option label="已离校" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="常驻区域" prop="area">
          <el-input v-model="form.area" placeholder="如：图书馆附近" />
        </el-form-item>
        <el-form-item label="特征描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="描述它的外貌特征、性格等..." />
        </el-form-item>
        <el-form-item label="封面图片 URL">
          <el-input v-model="form.coverImage" placeholder="可以先通过文件上传获取URL" />
          <div class="form-hint">
            先到
            <router-link to="/admin">管理页</router-link>
            使用上传功能获取图片链接，或在任何页面通过 API 上传后填入
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="saving" class="submit-btn">
            {{ isEdit ? '保存修改' : '创建档案' }}
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const saving = ref(false)
const loading = ref(false)

const form = reactive({
  name: '', type: '', area: '', description: '', coverImage: '', status: 1
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请输入类型', trigger: 'blur' }],
  area: [{ required: true, message: '请输入区域', trigger: 'blur' }]
}

async function submit() {
  saving.value = true
  try {
    if (isEdit.value) {
      await api.put(`/animals/${route.params.id}`, { ...form })
      ElMessage.success('修改成功')
    } else {
      await api.post('/animals', { ...form })
      ElMessage.success('创建成功')
    }
    router.push('/admin')
  } catch {
    // handled
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  if (isEdit.value) {
    loading.value = true
    const res = await api.get(`/animals/${route.params.id}`)
    Object.assign(form, {
      name: res.data.name,
      type: res.data.type,
      area: res.data.area,
      description: res.data.description || '',
      coverImage: res.data.coverImage || '',
      status: res.data.status
    })
    loading.value = false
  }
})
</script>

<style scoped>
.form-card {
  max-width: 640px;
  margin: 20px auto;
  background: white;
  border-radius: var(--radius);
  padding: 36px;
  box-shadow: 0 2px 16px rgba(0,0,0,0.05);
}
.form-card h2 { font-size: 22px; margin-bottom: 28px; }
.full-width { width: 100%; }
.submit-btn { margin-right: 12px; }
.form-hint { font-size: 12px; color: var(--text-secondary); margin-top: 4px; }
</style>
