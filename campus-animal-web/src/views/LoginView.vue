<!--
  登录页面。

  布局：垂直水平居中卡片（400px 宽）。
  包含：Logo + 标题 + 用户名输入框 + 密码输入框 + 登录按钮 + 注册链接。

  登录流程：
  1. 用户填写用户名和密码
  2. 调用 authStore.login() → POST /api/auth/login
  3. 成功后存储 token 到 localStorage，跳转到首页
  4. 失败时 Axios 拦截器自动弹出错误提示
-->
<template>
  <!-- auth-page：flex 居中容器 -->
  <div class="auth-page">
    <!-- auth-card：白色卡片 -->
    <div class="auth-card">
      <!-- 顶部：Logo + 标题 -->
      <div class="auth-header">
        <img :src="logoSvg" alt="logo" class="auth-logo" />
        <h2>欢迎回来</h2>
        <p class="subtitle">登录你的校园动物图鉴账号</p>
      </div>

      <!-- 登录表单 -->
      <!-- @submit.prevent 阻止表单默认提交行为 -->
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleLogin">
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名">
            <!-- Element Plus slot 语法：prefix 插槽放图标 -->
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>

        <!-- 密码（show-password 显示明文切换按钮） -->
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>

        <!-- 登录按钮（loading 状态防止重复提交） -->
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部：注册链接 -->
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
/** 登录按钮 loading 状态，防止重复提交 */
const loading = ref(false)

// 表单数据（reactive 响应式对象）
const form = reactive({ username: '', password: '' })
// 表单校验规则（Element Plus 内置校验）
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

/** 处理登录提交 */
async function handleLogin() {
  loading.value = true
  try {
    // 调用 Pinia store 的 login 方法
    await auth.login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/')  // 跳转到首页
  } catch {
    // 错误由 Axios 响应拦截器统一处理（弹出 ElMessage.error）
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* 居中布局容器 */
.auth-page {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}
/* 白色卡片 */
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
