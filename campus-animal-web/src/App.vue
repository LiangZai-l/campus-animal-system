<!--
  根组件 — 整个应用的布局骨架。

  结构说明：
  - AppHeader：固定在顶部的导航栏（sticky，高度 60px）
  - router-view：根据当前 URL 动态渲染对应的页面组件
  - main-content 高度 = 100vh - 60px（填满导航栏下方的所有空间）
-->
<template>
  <div id="campus-app">
    <!-- 顶部导航栏（始终显示，根据登录状态显示不同菜单） -->
    <AppHeader />
    <!-- 页面主体区域，由 Vue Router 根据当前路径动态渲染 -->
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
// <script setup> 是 Vue 3 的编译时语法糖，无需手动 return 给模板
import AppHeader from './components/AppHeader.vue'
</script>

<style scoped>
/* scoped 表示这些样式仅作用于当前组件的元素（通过 data-v-xxx 属性实现） */
.main-content {
  /* 100vh 视口高度减去导航栏 60px = 内容区最小高度 */
  min-height: calc(100vh - 60px);
  padding-top: 4px;
}
</style>
