<!--
  动物卡片组件 — 用于首页的网格列表展示。

  Props:
  - animal: Object (required) — 动物信息对象，需包含 id, name, type, area,
    coverImage, status, description 字段。

  功能：
  - 有封面图时展示图片，无封面图时展示类型 emoji 占位符（🐱🐕🐦🐾）
  - 右上角标签显示"在校"或"已离校"状态
  - 标题、类型标签、区域、描述（最多 2 行）
  - 点击跳转到动物详情页
  - hover 时卡片上移 4px（微交互）
-->
<template>
  <!-- shadow="hover" 表示 hover 时才显示阴影 -->
  <el-card class="animal-card" shadow="hover" @click="$router.push(`/animals/${animal.id}`)">
    <!-- 封面图区域 -->
    <div class="card-cover">
      <!-- 有封面图时显示图片 -->
      <img v-if="animal.coverImage" :src="animal.coverImage" :alt="animal.name" />
      <!-- 无封面图时显示类型 emoji 占位符 -->
      <div v-else class="cover-placeholder">
        <span class="placeholder-icon">{{ typeIcon }}</span>
      </div>
      <!-- 状态标签 — 绝对定位在右上角 -->
      <el-tag :type="statusType" size="small" class="status-tag">
        {{ animal.status === 1 ? '在校' : '已离校' }}
      </el-tag>
    </div>

    <!-- 卡片正文 -->
    <div class="card-body">
      <h3 class="card-title">{{ animal.name }}</h3>
      <div class="card-meta">
        <!-- 类型标签：不同颜色（猫=default, 狗=warning, 鸟=success） -->
        <el-tag size="small" :type="typeColor">{{ animal.type }}</el-tag>
        <!-- 常驻区域 -->
        <span class="card-area">
          <el-icon><LocationFilled /></el-icon> {{ animal.area }}
        </span>
      </div>
      <!-- 描述文字：最多显示 2 行（CSS -webkit-line-clamp） -->
      <p class="card-desc" v-if="animal.description">{{ animal.description }}</p>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'

// 定义组件 props（Vue 3 Composition API 语法）
const props = defineProps({
  animal: { type: Object, required: true }
})

// 动物类型 → emoji 图标映射
const typeIcons = { '猫': '🐱', '狗': '🐕', '鸟': '🐦' }
/** 根据动物类型返回对应的 emoji，未知类型用 🐾 */
const typeIcon = computed(() => typeIcons[props.animal.type] || '🐾')

/** 根据动物类型返回 Element Plus Tag 组件的 type（颜色） */
const typeColor = computed(() => {
  const map = { '猫': '', '狗': 'warning', '鸟': 'success' }
  return map[props.animal.type] || 'info'
})

/** 状态标签颜色：在校=绿色(success)，离校=灰色(info) */
const statusType = computed(() => props.animal.status === 1 ? 'success' : 'info')
</script>

<style scoped>
/* 卡片整体 — 点击手型 + hover 上移动画 */
.animal-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  overflow: hidden;
}
.animal-card:hover {
  transform: translateY(-4px);  /* hover 上移 4px */
}

/* 封面区域 — 固定高度 180px */
.card-cover {
  height: 180px;
  background: var(--primary-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  /* 负 margin 使封面撑满卡片圆角区域 */
  margin: -20px -20px 16px;
  border-radius: var(--radius) var(--radius) 0 0;
  overflow: hidden;
}
/* 图片填满容器并保持比例裁剪 */
.card-cover img { width: 100%; height: 100%; object-fit: cover; }
.cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
.placeholder-icon { font-size: 56px; opacity: 0.6; }
.status-tag { position: absolute; top: 10px; right: 10px; }

.card-title { font-size: 17px; font-weight: 600; margin-bottom: 8px; color: var(--text); }
.card-meta { display: flex; align-items: center; gap: 12px; margin-bottom: 6px; }
.card-area { font-size: 13px; color: var(--text-secondary); display: flex; align-items: center; gap: 2px; }

/* 描述文字 — 最多显示 2 行（超出部分省略号） */
.card-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;       /* 限制显示 2 行 */
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}
</style>
