<template>
  <div class="timeline">
    <div v-if="!items.length" class="empty-hint">暂无打卡记录</div>
    <div v-for="item in items" :key="item.id" class="timeline-item">
      <div class="tl-avatar">{{ item.username?.charAt(0) || '?' }}</div>
      <div class="tl-body">
        <div class="tl-header">
          <strong>{{ item.username }}</strong>
          <span class="tl-time">{{ formatTime(item.createdAt) }}</span>
        </div>
        <p class="tl-content">{{ item.content }}</p>
        <div class="tl-footer">
          <el-tag v-if="item.mood" size="small" :type="moodColor(item.mood)">
            {{ moodLabel(item.mood) }}
          </el-tag>
          <span v-if="item.location" class="tl-location">
            <el-icon><LocationFilled /></el-icon> {{ item.location }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  items: { type: Array, default: () => [] }
})

function formatTime(str) {
  if (!str) return ''
  const d = new Date(str)
  const pad = n => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function moodColor(m) {
  return { happy: 'success', neutral: '', worried: 'danger' }[m] || ''
}

function moodLabel(m) {
  return { happy: '开心', neutral: '一般', worried: '担心' }[m] || m
}
</script>

<style scoped>
.timeline { padding: 4px 0; }
.empty-hint { text-align: center; color: var(--text-secondary); padding: 24px; font-size: 14px; }

.timeline-item {
  display: flex;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--border);
}
.timeline-item:last-child { border-bottom: none; }

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
</style>
