/**
 * Vite 构建配置。
 *
 * Vite 是新一代前端构建工具，相比 Webpack：
 * - 开发阶段使用原生 ES Module，启动速度极快
 * - 生产构建使用 Rollup，输出优化后的静态文件
 *
 * 核心配置：
 * - @vitejs/plugin-vue：Vue 3 SFC（单文件组件）编译插件
 * - server.proxy：开发环境 API 代理，解决前后端分离的跨域问题
 */
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],

  /** 开发服务器配置 */
  server: {
    port: 5173,  // 前端开发服务器端口

    /**
     * API 代理配置。
     *
     * 原理：Vite 开发服务器接收前端请求 → 符合 /api 前缀的请求
     * → 转发到 localhost:8080（后端） → 返回结果给前端。
     *
     * 这样前端的 axios baseURL 只需写 '/api'，无需关心端口号，
     * 浏览器也只和 5173 端口通信，避免了跨域（CORS）问题。
     */
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 后端地址
        changeOrigin: true                 // 修改请求头中的 Origin
      }
    }
  }
})
