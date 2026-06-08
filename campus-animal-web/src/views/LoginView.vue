<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <img :src="logoSvg" alt="logo" class="auth-logo" />
        <h2>欢迎回来</h2>
        <p class="subtitle">登录你的校园动物图鉴账号</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="auth-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
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

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  loading.value = true
  try {
    await auth.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    // error handled by interceptor
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
