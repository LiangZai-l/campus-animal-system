<template>
  <div id="campus-app">
    <AppHeader />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import AppHeader from './components/AppHeader.vue'
</script>

<style scoped>
.main-content {
  min-height: calc(100vh - 60px);
  padding-top: 4px;
}
</style>
