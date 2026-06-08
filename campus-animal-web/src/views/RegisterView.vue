<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <img :src="logoSvg" alt="logo" class="auth-logo" />
        <h2>创建账号</h2>
        <p class="subtitle">加入校园动物图鉴社区</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（3-50位）" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（6位以上）" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="realName">
          <el-input v-model="form.realName" placeholder="真实姓名（选填）" prefix-icon="UserFilled" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号（选填）" prefix-icon="Phone" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">
        已有账号？<router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { ElMessage } from 'element-plus'
import logoSvg from '../assets/logo.svg'

const auth = useAuthStore()
const router = useRouter()
const loading = ref(false)

const form = reactive({ username: '', password: '', realName: '', phone: '' })
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度 3-50', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度 6-100', trigger: 'blur' }
  ]
}

async function handleRegister() {
  loading.value = true
  try {
    await auth.register({ ...form })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}
.auth-card {
  width: 400px;
  max-width: 100%;
  background: white;
  border-radius: 16px;
  padding: 48px 36px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.06);
}
.auth-header { text-align: center; margin-bottom: 36px; }
.auth-logo { width: 56px; height: 56px; margin-bottom: 16px; }
.auth-header h2 { font-size: 22px; color: var(--text); margin-bottom: 6px; }
.subtitle { font-size: 14px; color: var(--text-secondary); }
.submit-btn { width: 100%; }
.auth-footer { text-align: center; font-size: 14px; color: var(--text-secondary); margin-top: 20px; }
</style>
