/**
 * Axios 请求封装 — 统一的 HTTP 客户端。
 * <p>
 * 核心功能：
 * <ol>
 *   <li>baseURL 统一前缀 /api（Vite 代理到 localhost:8080）</li>
 *   <li>请求拦截器自动添加 JWT Authorization 头</li>
 *   <li>响应拦截器统一处理 code !== 200 的业务错误</li>
 *   <li>401 自动清除登录状态并跳转登录页</li>
 * </ol>
 * <p>
 * 数据流：前端 → /api/xxx → Vite 代理 → localhost:8080/api/xxx → 后端
 * <p>
 * 后端返回格式：{ code: 200, message: "操作成功", data: {...} }
 * 本拦截器将 code === 200 的正常响应解包，直接返回 data 字段给调用方。
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建 Axios 实例（隔离的配置副本，不影响全局 axios）
const api = axios.create({
  baseURL: '/api',      // 所有请求自动加上 /api 前缀
  timeout: 15000        // 15 秒超时（图片上传可能较慢）
})

/**
 * 请求拦截器：在发送请求前自动添加 Authorization 头。
 * <p>
 * 执行时机：每次 HTTP 请求发出前（在 then/catch 之前)
 * 注意：必须 return config，否则请求会被阻止
 */
api.interceptors.request.use(config => {
  // 从 localStorage 读取 JWT token（由登录成功后存入）
  const token = localStorage.getItem('token')
  if (token) {
    // 标准 Bearer 认证格式：Authorization: Bearer <jwt_token>
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

/**
 * 响应拦截器：统一处理成功/失败响应。
 * <p>
 * 两个回调：
 * - 第一个：HTTP 2xx 响应（包括 code !== 200 的业务错误）
 * - 第二个：HTTP 非 2xx 响应（网络错误、401、500 等）
 */
api.interceptors.response.use(
  // 成功回调 — HTTP 状态码 2xx
  response => {
    const data = response.data
    // 后端返回的统一格式：{ code, message, data }
    if (data.code === 200) {
      // 成功 → 返回 data 字段（调用方直接用 .then(data => ...)）
      return data
    }
    // 业务错误（如 code=409 用户名已存在）→ 弹窗提示
    ElMessage.error(data.message || '请求失败')
    return Promise.reject(new Error(data.message))
  },

  // 失败回调 — HTTP 状态码非 2xx（网络错误、401、500 等）
  error => {
    // 401：未认证（token 过期或无效）
    if (error.response?.status === 401) {
      // 清除本地存储的登录状态
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      // 强制跳转登录页（用 location.href 而非 router.push，
      // 因为此处不在 Vue 组件上下文中）
      window.location.href = '/login'
      return Promise.reject(error)
    }
    // 其他错误 → 提示错误信息
    const msg = error.response?.data?.message || '网络错误'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default api
