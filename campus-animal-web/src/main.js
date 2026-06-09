/**
 * Vue 3 应用入口文件。
 * <p>
 * 启动流程：
 * <ol>
 *   <li>createApp(App) — 创建 Vue 应用实例</li>
 *   <li>app.use(pinia) — 注册 Pinia 状态管理</li>
 *   <li>app.use(router) — 注册 Vue Router 路由</li>
 *   <li>app.use(ElementPlus) — 注册 Element Plus UI 组件库（中文语言包）</li>
 *   <li>全局注册 Element Plus 图标组件</li>
 *   <li>app.mount('#app') — 挂载到 index.html 的 #app 元素</li>
 * </ol>
 */
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
// Element Plus 样式必须手动导入，否则所有组件为裸 HTML（无样式）
import 'element-plus/dist/index.css'
// 中文语言包：日历、分页、对话框等组件使用中文
import zhCn from 'element-plus/es/locale/lang/zh-cn'
// 导入所有 Element Plus 图标（全局注册后可在模板中直接使用）
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
// 全局主题样式（CSS 变量 + Element Plus 主题覆盖）
import './style.css'

const app = createApp(App)

// Pinia：Vue 3 官方状态管理库（替代 Vuex），支持 Composition API 风格
app.use(createPinia())
// Vue Router：前端路由（单页应用页面切换）
app.use(router)
// Element Plus：企业级 UI 组件库，配置中文语言包
app.use(ElementPlus, { locale: zhCn })

// 全局注册所有 Element Plus 图标组件
// 注册后可在任何 Vue 模板中直接使用 <el-icon><User /></el-icon> 而无需导入
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 挂载到 index.html 中 id="app" 的 div 元素
app.mount('#app')
