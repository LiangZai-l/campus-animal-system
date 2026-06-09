/**
 * Vue Router 路由配置 + 导航守卫。
 * <p>
 * 路由表设计：
 * <ul>
 *   <li>{@code meta.guest: true} — 游客页面（登录/注册），已登录用户自动跳首页</li>
 *   <li>{@code meta.auth: true} — 需要登录的页面，未登录跳 /login</li>
 *   <li>{@code meta.admin: true} — 管理员页面，非 ADMIN 角色跳首页</li>
 * </ul>
 * <p>
 * 所有页面组件使用<b>动态导入</b>（() => import(...)），
 * 实现代码分割和按需加载，减小首屏打包体积。
 * <p>
 * 导航守卫执行顺序：
 * <ol>
 *   <li>检查 meta.auth — 需要登录但 token 不存在 → /login</li>
 *   <li>检查 meta.admin — 需要管理员但角色不是 ADMIN → /</li>
 *   <li>检查 meta.guest — 已登录用户访问登录/注册页 → /</li>
 * </ol>
 */
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  // ===== 游客页面（已登录用户无法访问） =====
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),  // 懒加载
    meta: { guest: true }  // 标记：仅游客可访问
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { guest: true }
  },

  // ===== 需要登录的页面 =====
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue'),
    meta: { auth: true }  // 标记：需登录
  },
  {
    path: '/animals/:id',           // :id 为动态路由参数
    name: 'AnimalDetail',
    component: () => import('../views/AnimalDetailView.vue'),
    meta: { auth: true }
  },
  {
    path: '/checkin/create',
    name: 'CheckInCreate',
    component: () => import('../views/CheckInCreateView.vue'),
    meta: { auth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/ProfileView.vue'),
    meta: { auth: true }
  },

  // ===== 管理员页面（需 ADMIN 角色） =====
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboard.vue'),
    meta: { admin: true }  // 标记：需 ADMIN 角色
  },
  {
    path: '/admin/animals/new',
    name: 'AnimalCreate',
    component: () => import('../views/AnimalFormView.vue'),  // 新增/编辑复用同一组件
    meta: { admin: true }
  },
  {
    path: '/admin/animals/:id/edit',
    name: 'AnimalEdit',
    component: () => import('../views/AnimalFormView.vue'),  // 与新增复用同一组件
    meta: { admin: true }
  }
]

const router = createRouter({
  // createWebHistory：使用 HTML5 History 模式（URL 无 # 号）
  // 生产环境需配合 Nginx 的 try_files 配置
  history: createWebHistory(),
  routes
})

/**
 * 全局前置守卫 — 每次路由跳转前执行。
 * <p>
 * 三个参数：
 * <ul>
 *   <li>to：目标路由对象</li>
 *   <li>from：来源路由对象</li>
 *   <li>next：放行函数（next() 放行，next('/path') 重定向）</li>
 * </ul>
 */
router.beforeEach((to, from, next) => {
  // 从 Pinia store 获取认证状态（Pinia 必须在 createApp 时已注册）
  const auth = useAuthStore()

  // 需要登录但无 token → 重定向到登录页
  if (to.meta.auth && !auth.token) {
    return next('/login')
  }

  // 需要管理员但角色不是 ADMIN → 重定向到首页
  if (to.meta.admin && auth.user?.role !== 'ADMIN') {
    return next('/')
  }

  // 已登录用户访问登录/注册页 → 重定向到首页
  if (to.meta.guest && auth.token) {
    return next('/')
  }

  next()
})

export default router
