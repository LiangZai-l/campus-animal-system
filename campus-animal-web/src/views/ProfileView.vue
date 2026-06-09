<!--
  个人中心页 — 查看/删除打卡记录、修改密码、注销账号。

  路由：/profile（meta.auth: true，需登录）

  布局：
  1. 用户信息卡片
  2. 打卡历史列表（可删除）
  3. 修改密码对话框
  4. 注销账号（危险操作，需确认）
-->
<template>
  <div class="page-container">
    <h2 class="page-title">个人中心</h2>

    <!-- ===== 用户信息卡片 ===== -->
    <div class="info-card">
      <div class="info-avatar">
        {{ (auth.user?.realName || auth.user?.username || '?').charAt(0) }}
      </div>
      <div class="info-text">
        <h3>{{ auth.user?.realName || auth.user?.username }}</h3>
        <p>@{{ auth.user?.username }} · {{ auth.user?.role === 'ADMIN' ? '管理员' : '普通用户' }}</p>
      </div>
    </div>

    <!-- ===== 操作按钮区 ===== -->
    <div class="action-bar">
      <el-button type="primary" plain @click="passwordDialogVisible = true">
        <el-icon><Lock /></el-icon> 修改密码
      </el-button>
      <el-button type="danger" plain @click="showDeleteAccountConfirm">
        <el-icon><Delete /></el-icon> 注销账号
      </el-button>
    </div>

    <!-- ===== 我的打卡记录 ===== -->
    <div class="section">
      <h3 class="section-title">我的打卡记录</h3>
      <div v-if="checkIns.length" class="checkin-list">
        <div v-for="item in checkIns" :key="item.id" class="checkin-item">
          <div class="ci-main">
            <div class="ci-header">
              <span class="ci-animal" @click="$router.push(`/animals/${item.animalId}`)">
                🐾 查看动物
              </span>
              <span class="ci-time">{{ formatTime(item.createdAt) }}</span>
            </div>
            <p class="ci-content">{{ item.content }}</p>
            <div class="ci-footer">
              <el-tag v-if="item.mood" size="small" :type="moodColor(item.mood)">
                {{ moodLabel(item.mood) }}
              </el-tag>
              <span v-if="item.location" class="ci-location">
                <el-icon><LocationFilled /></el-icon> {{ item.location }}
              </span>
            </div>
          </div>
          <el-button type="danger" text size="small" @click="deleteCheckIn(item.id)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
      <div v-else class="empty-hint">暂无打卡记录</div>
    </div>

    <!-- ===== 修改密码对话框 ===== -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="420px" destroy-on-close>
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-position="top"
        @submit.prevent="changePassword">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="pwdLoading" @click="changePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '../api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const checkIns = ref([])
const passwordDialogVisible = ref(false)
const pwdLoading = ref(false)
const pwdFormRef = ref(null)

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (_rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

function formatTime(str) {
  if (!str) return ''
  const d = new Date(str)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function moodColor(m) {
  return { happy: 'success', neutral: '', worried: 'danger' }[m] || ''
}

function moodLabel(m) {
  return { happy: '开心', neutral: '一般', worried: '担心' }[m] || m
}

async function fetchMyCheckIns() {
  try {
    const res = await api.get('/checkins/my')
    checkIns.value = res.data
  } catch { /* 忽略 */ }
}

async function deleteCheckIn(id) {
  try {
    await ElMessageBox.confirm('确定删除这条打卡记录吗？', '提示', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return // 用户取消
  }
  try {
    await api.delete(`/checkins/${id}`)
    ElMessage.success('已删除')
    fetchMyCheckIns()
  } catch { /* API 错误已在拦截器中提示 */ }
}

async function changePassword() {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return

  pwdLoading.value = true
  try {
    await api.put('/auth/password', {
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    passwordDialogVisible.value = false
    auth.logout()
    router.push('/login')
  } catch {
    // API 错误已在响应拦截器中提示，此处仅保持对话框打开
  } finally {
    pwdLoading.value = false
  }
}

function showDeleteAccountConfirm() {
  ElMessageBox.confirm(
    '注销后账号及所有数据将被永久删除，不可恢复。确定要继续吗？',
    '注销账号',
    { confirmButtonText: '确认注销', cancelButtonText: '取消', type: 'error' }
  ).then(async () => {
    try {
      await api.delete('/auth/account')
      ElMessage.success('账号已注销')
      auth.logout()
      router.push('/login')
    } catch { /* API 错误已在拦截器中提示 */ }
  }).catch(() => {})
}

onMounted(() => {
  fetchMyCheckIns()
})
</script>

<style scoped>
.page-title {
  font-size: 22px;
  margin-bottom: 24px;
}

.info-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: white;
  border-radius: var(--radius);
  padding: 28px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  margin-bottom: 20px;
}

.info-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  flex-shrink: 0;
}

.info-text h3 { font-size: 20px; margin-bottom: 4px; }
.info-text p { font-size: 14px; color: var(--text-secondary); }

.action-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
}

.section {
  background: white;
  border-radius: var(--radius);
  padding: 24px 28px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.section-title { font-size: 17px; margin-bottom: 16px; }

.checkin-list { display: flex; flex-direction: column; }
.checkin-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border);
}
.checkin-item:last-child { border-bottom: none; }

.ci-main { flex: 1; min-width: 0; }
.ci-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.ci-animal { color: var(--primary); cursor: pointer; font-size: 13px; }
.ci-animal:hover { text-decoration: underline; }
.ci-time { font-size: 12px; color: var(--text-secondary); }
.ci-content { font-size: 14px; line-height: 1.6; word-break: break-word; }
.ci-footer { display: flex; align-items: center; gap: 12px; margin-top: 8px; }
.ci-location { font-size: 12px; color: var(--text-secondary); display: flex; align-items: center; gap: 2px; }

.empty-hint { text-align: center; color: var(--text-secondary); padding: 32px; font-size: 14px; }
</style>
