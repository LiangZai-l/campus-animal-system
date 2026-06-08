<template>
  <header class="app-header">
    <div class="header-inner">
      <router-link to="/" class="logo-link">
        <img :src="logoSvg" alt="logo" class="logo-img" />
        <span class="logo-text">校园动物图鉴</span>
      </router-link>

      <nav class="nav-links">
        <router-link to="/" class="nav-item">首页</router-link>
        <template v-if="auth.isLoggedIn">
          <router-link to="/checkin/create" class="nav-item">打卡</router-link>
          <router-link v-if="auth.isAdmin" to="/admin" class="nav-item nav-admin">管理</router-link>
        </template>
      </nav>

      <div class="user-area">
        <template v-if="auth.isLoggedIn">
          <span class="user-name">{{ auth.user?.realName || auth.user?.username }}</span>
          <el-button text size="small" @click="handleLogout">退出</el-button>
        </template>
        <template v-else>
          <el-button text size="small" @click="$router.push('/login')">登录</el-button>
          <el-button size="small" type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import logoSvg from '../assets/logo.svg'

const auth = useAuthStore()
const router = useRouter()

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-header {
  background: white;
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 8px rgba(0,0,0,0.04);
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 32px;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text);
  flex-shrink: 0;
}
.logo-img {
  width: 32px;
  height: 32px;
}
.logo-text {
  font-size: 17px;
  font-weight: 700;
  color: var(--primary);
  white-space: nowrap;
}

.nav-links {
  display: flex;
  gap: 8px;
  flex: 1;
}
.nav-item {
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 14px;
  color: var(--text-secondary);
  transition: all 0.2s;
}
.nav-item:hover,
.nav-item.router-link-active {
  color: var(--primary);
  background: var(--primary-bg);
}
.nav-admin {
  color: var(--amber) !important;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
.user-name {
  font-size: 14px;
  color: var(--text-secondary);
}

@media (max-width: 640px) {
  .logo-text { display: none; }
  .nav-links { gap: 0; }
  .nav-item { padding: 6px 8px; font-size: 13px; }
}
</style>
