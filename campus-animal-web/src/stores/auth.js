/**
 * Pinia 认证状态管理 — 管理用户登录状态。
 * <p>
 * Pinia 是 Vue 3 官方状态管理库（替代 Vuex），
 * 使用 Composition API 风格（defineStore 的 setup 语法）。
 * <p>
 * 持久化策略：
 * <ul>
 *   <li>token 和 user 存入 localStorage（浏览器本地存储）</li>
 *   <li>刷新页面后从 localStorage 恢复登录状态</li>
 *   <li>注销时清除 localStorage 中的 token 和 user</li>
 * </ul>
 * <p>
 * 安全考量：JWT 存入 localStorage 存在 XSS 攻击风险。
 * 生产环境可考虑 httpOnly Cookie（需后端配合），
 * 但学习项目中 localStorage 方案更为简单直观。
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '../api'

// defineStore 第一个参数 'auth' 是 store 的唯一标识
export const useAuthStore = defineStore('auth', () => {
  // ===== 状态（state） =====

  /** JWT 令牌，从 localStorage 读取初始值（刷新后保持登录状态） */
  const token = ref(localStorage.getItem('token') || '')

  /**
   * 安全解析 localStorage 中的 user JSON。
   * 用 try-catch 包裹防止 JSON 解析异常导致应用崩溃。
   */
  function safeParseUser() {
    try {
      return JSON.parse(localStorage.getItem('user') || 'null')
    } catch {
      // JSON 格式损坏时清除脏数据
      localStorage.removeItem('user')
      return null
    }
  }
  /** 当前用户信息 */
  const user = ref(safeParseUser())

  // ===== 计算属性（getters） =====

  /** 是否已登录 */
  const isLoggedIn = computed(() => !!token.value)

  /** 是否是管理员 */
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // ===== 方法（actions） =====

  /**
   * 登录 — 调用 API → 存储 token 和 user。
   * <p>
   * 流程：
   * 1. POST /api/auth/login → 获取 { token, userId, username, realName, role }
   * 2. 存入 Pinia 状态（响应式）
   * 3. 持久化到 localStorage（刷新不丢失）
   *
   * @param {string} username 用户名
   * @param {string} password 密码
   * @returns {Promise} API 响应
   */
  async function login(username, password) {
    const res = await api.post('/auth/login', { username, password })
    // 更新响应式状态
    token.value = res.data.token
    user.value = {
      id: res.data.userId,
      username: res.data.username,
      realName: res.data.realName,
      role: res.data.role
    }
    // 持久化到 localStorage
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
    return res
  }

  /**
   * 注册 — 调用 API 创建新用户。
   * <p>
   * 注意：注册成功后不自动登录，需要跳转到登录页让用户手动登录。
   *
   * @param {object} data 注册信息 { username, password, realName, phone }
   */
  async function register(data) {
    await api.post('/auth/register', data)
  }

  /**
   * 注销 — 清除所有状态和本地存储。
   * <p>
   * 注意：不需要调用后端接口（JWT 是无状态的），
   * 只需清除客户端存储的 token 即可实现"注销"。后端不做 token 失效处理，
   * 这是 JWT 的固有特性（生产环境可用黑名单机制解决）。
   */
  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 暴露给组件使用的状态和方法
  return { token, user, isLoggedIn, isAdmin, login, register, logout }
})
