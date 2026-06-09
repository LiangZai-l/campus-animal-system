<!--
  注册页面。

  布局与登录页一致：居中卡片（400px）。
  字段：用户名（3-50 字，必填）、密码（6 位以上，必填）、
        真实姓名（选填）、手机号（选填）。

  注册流程：
  1. 用户填写信息
  2. 调用 authStore.register() → POST /api/auth/register
  3. 成功后弹窗提示"注册成功，请登录"，跳转到登录页
  4. 失败时（如用户名已存在）Axios 拦截器弹出错误提示
-->
<template>
  <div class="auth-page">
    <div class="auth-card">
      <!-- 顶部：Logo + 标题 -->
      <div class="auth-header">
        <img :src="logoSvg" alt="logo" class="auth-logo" />
        <h2>创建账号</h2>
        <p class="subtitle">加入校园动物图鉴社区</p>
      </div>

      <!-- 注册表单 -->
      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleRegister">
        <!-- 用户名（3-50 字符，必填） -->
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（3-50位）">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>

        <!-- 密码（6 位以上，必填） -->
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（6位以上）" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>

        <!-- 真实姓名（选填） -->
        <el-form-item prop="realName">
          <el-input v-model="form.realName" placeholder="真实姓名（选填）">
            <template #prefix><el-icon><UserFilled /></el-icon></template>
          </el-input>
        </el-form-item>

        <!-- 手机号（选填） -->
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号（选填）">
            <template #prefix><el-icon><Phone /></el-icon></template>
          </el-input>
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" class="submit-btn">
            注册
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部：登录链接 -->
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

// 表单数据（reactive：深度响应式）
const form = reactive({ username: '', password: '', realName: '', phone: '' })

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度 3-50', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度 6-100', trigger: 'blur' }
  ]
  // realName 和 phone 无校验规则（选填项）
}

/** 处理注册提交 */
async function handleRegister() {
  loading.value = true
  try {
    // 调用 Pinia store 的 register 方法（{...form} 展开为独立对象）
    await auth.register({ ...form })
    ElMessage.success('注册成功，请登录')
    // 注册成功跳转到登录页（不自动登录）
    router.push('/login')
  } catch {
    // 错误由 Axios 拦截器处理
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
