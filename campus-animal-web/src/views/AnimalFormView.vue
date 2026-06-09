<!--
  动物档案表单页 — 新增/编辑共用同一组件。

  路由：
  - /admin/animals/new → 新增模式（isEdit = false）
  - /admin/animals/:id/edit → 编辑模式（isEdit = true）

  判断逻辑：通过 computed(() => !!route.params.id) 判断是否有路径参数。

  新增模式：
  - 表单初始为空
  - 提交 POST /api/animals → 创建成功跳转到 /admin

  编辑模式：
  - onMounted 时调用 GET /api/animals/:id 预填数据
  - 提交 PUT /api/animals/:id → 修改成功跳转到 /admin
-->
<template>
  <div class="page-container">
    <!-- 表单卡片（最大宽度 640px，居中） -->
    <div class="form-card">
      <!-- 标题根据模式动态切换 -->
      <h2>{{ isEdit ? '编辑动物档案' : '新增动物档案' }}</h2>

      <!-- label-position="top"：标签在输入框上方 -->
      <el-form ref="formRef" :model="form" :rules="rules"
        label-position="top" size="large" @submit.prevent="submit" v-loading="loading">

        <!-- 名称 -->
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="给动物取个名字" />
        </el-form-item>

        <!-- 类型 + 状态（并排两列） -->
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

        <!-- 常驻区域 -->
        <el-form-item label="常驻区域" prop="area">
          <el-input v-model="form.area" placeholder="如：图书馆附近" />
        </el-form-item>

        <!-- 特征描述（多行文本域） -->
        <el-form-item label="特征描述">
          <el-input v-model="form.description" type="textarea" :rows="3"
            placeholder="描述它的外貌特征、性格等..." />
        </el-form-item>

        <!-- 封面图片上传 -->
        <el-form-item label="封面图片">
          <div class="cover-upload">
            <el-upload
              :http-request="handleUpload"
              :before-upload="beforeUpload"
              :show-file-list="false"
              accept="image/*"
              class="cover-uploader"
            >
              <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
              <div v-else class="upload-placeholder" :class="{ 'is-uploading': uploading }">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <span>{{ uploading ? '上传中...' : '点击上传封面图片' }}</span>
              </div>
            </el-upload>
            <el-button v-if="form.coverImage" text type="danger" size="small"
              @click="form.coverImage = ''">
              移除图片
            </el-button>
          </div>
        </el-form-item>

        <!-- 按钮区域 -->
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="saving" class="submit-btn">
            {{ isEdit ? '保存修改' : '创建档案' }}
          </el-button>
          <!-- $router.back() 返回上一页 -->
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
import { Plus } from '@element-plus/icons-vue'
import api from '../api'

const route = useRoute()
const router = useRouter()

/** 是否有路径参数 id → 编辑模式（新增模式 id 为 undefined） */
const isEdit = computed(() => !!route.params.id)

const saving = ref(false)   // 提交按钮 loading
const loading = ref(false)  // 页面 loading（编辑模式预填数据时）
const uploading = ref(false) // 封面上传 loading

// 表单数据（reactive：双向绑定）
const form = reactive({
  name: '', type: '', area: '', description: '', coverImage: '', status: 1
})

// 表单校验规则
const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  type: [{ required: true, message: '请输入类型', trigger: 'blur' }],
  area: [{ required: true, message: '请输入区域', trigger: 'blur' }]
}

/** 上传前校验：类型必须为图片，大小不超过 10MB */
function beforeUpload(file) {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (file.size / 1024 / 1024 > 10) {
    ElMessage.error('图片大小不能超过 10MB')
    return false
  }
  return true
}

/** 自定义上传：手动构建 FormData，调用后端 /files/upload */
async function handleUpload({ file, onSuccess, onError }) {
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await api.post('/files/upload', formData)
    form.coverImage = res.data
    ElMessage.success('封面上传成功')
    onSuccess()
  } catch (err) {
    onError(err)
  } finally {
    uploading.value = false
  }
}

/** 提交表单 — 根据模式调用新增或修改接口 */
async function submit() {
  saving.value = true
  try {
    if (isEdit.value) {
      // 编辑模式 → PUT /api/animals/:id
      await api.put(`/animals/${route.params.id}`, { ...form })
      ElMessage.success('修改成功')
    } else {
      // 新增模式 → POST /api/animals
      await api.post('/animals', { ...form })
      ElMessage.success('创建成功')
    }
    router.push('/admin')  // 成功后跳转到管理页
  } catch {
    // 错误由 Axios 拦截器处理
  } finally {
    saving.value = false
  }
}

/** 编辑模式时预填现有数据 */
onMounted(async () => {
  if (isEdit.value) {
    loading.value = true
    // 获取动物详情
    const res = await api.get(`/animals/${route.params.id}`)
    // Object.assign：将响应数据复制到 form（保持响应式）
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
.cover-upload {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}
.cover-uploader {
  width: 100%;
}
.cover-uploader :deep(.el-upload) {
  display: block;
  width: 100%;
}
.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 140px;
  border: 2px dashed var(--el-border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s;
  color: var(--text-secondary);
  font-size: 13px;
}
.upload-placeholder:hover {
  border-color: var(--el-color-primary);
}
.upload-placeholder.is-uploading {
  cursor: wait;
  opacity: 0.6;
}
.upload-icon {
  font-size: 28px;
  margin-bottom: 6px;
}
.cover-preview {
  display: block;
  width: 100%;
  max-height: 240px;
  object-fit: cover;
  border-radius: 8px;
  cursor: pointer;
}
</style>
