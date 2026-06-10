<!--
  打卡时间轴组件 — 展示打卡记录列表。

  Props:
  - items: Array (默认 []) — 打卡记录数组，每条需包含 id, username, content,
    createdAt, mood(可选), location(可选)。

  每条记录的展示内容：
  - 头像（用户名的首字母，圆形绿色背景）
  - 用户名 + 格式化时间
  - 打卡正文
  - 心情标签（开心/一般/担心）+ 地点
  - 无记录时显示"暂无打卡记录"
-->
<template>
  <div class="timeline">
    <!-- 空状态提示 -->
    <div v-if="!items.length" class="empty-hint">暂无打卡记录</div>
    <!-- 遍历打卡记录列表 -->
    <div v-for="item in items" :key="item.id" class="timeline-item">
      <!-- 头像 — 取用户名的第一个字 -->
      <div class="tl-avatar">{{ item.username?.charAt(0) || '?' }}</div>
      <div class="tl-body">
        <!-- 顶部：用户名 + 时间 -->
        <div class="tl-header">
          <strong>{{ item.username }}</strong>
          <span class="tl-time">{{ formatTime(item.createdAt) }}</span>
        </div>
        <!-- 打卡内容 -->
        <p class="tl-content">{{ item.content }}</p>
        <!-- 底部：心情标签 + 地点 -->
        <div class="tl-footer">
          <el-tag v-if="item.mood" size="small" :type="moodColor(item.mood)">
            {{ moodLabel(item.mood) }}
          </el-tag>
          <span v-if="item.location" class="tl-location">
            <el-icon><LocationFilled /></el-icon> {{ item.location }}
          </span>
          <el-button v-if="admin" text type="danger" size="small"
            class="tl-delete" @click="handleDelete(item)">
            <el-icon><Delete /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from 'element-plus'

const props = defineProps({
  items: { type: Array, default: () => [] },
  admin: { type: Boolean, default: false }
})

const emit = defineEmits(['delete'])

async function handleDelete(item) {
  try {
    await ElMessageBox.confirm(
      `确定删除 ${item.username} 的打卡记录吗？`,
      '删除确认',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' }
    )
    emit('delete', item.id)
  } catch {
    // 用户取消
  }
}

/**
 * 格式化 ISO 时间字符串为本地显示格式。
 * 示例输入："2026-06-08T10:30:00"
 * 示例输出："2026-06-08 10:30"
 */
function formatTime(str) {
  if (!str) return ''
  const d = new Date(str)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

/** 心情 → Element Plus Tag 颜色映射 */
function moodColor(m) {
  return { happy: 'success', neutral: '', worried: 'danger' }[m] || ''
}

/** 心情英文 → 中文显示映射 */
function moodLabel(m) {
  return { happy: '开心', neutral: '一般', worried: '担心' }[m] || m
}
</script>

<style scoped>
.timeline { padding: 4px 0; }
.empty-hint { text-align: center; color: var(--text-secondary); padding: 24px; font-size: 14px; }

/* 每条打卡记录 — 左头像 + 右内容 */
.timeline-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border);
}
.timeline-item:last-child { border-bottom: none; }

/* 头像 — 圆形，绿色背景，显示用户名首字 */
.tl-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--primary-bg);
  color: var(--primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.tl-body { flex: 1; min-width: 0; }
.tl-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.tl-header strong { font-size: 14px; }
.tl-time { font-size: 12px; color: var(--text-secondary); }

.tl-content { font-size: 14px; line-height: 1.6; color: var(--text); word-break: break-word; }

.tl-footer { display: flex; align-items: center; gap: 12px; margin-top: 8px; }
.tl-location { font-size: 12px; color: var(--text-secondary); display: flex; align-items: center; gap: 2px; }
.tl-delete { margin-left: auto; }
</style>
