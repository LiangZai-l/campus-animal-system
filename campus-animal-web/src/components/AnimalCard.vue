<template>
  <el-card class="animal-card" shadow="hover" @click="$router.push(`/animals/${animal.id}`)">
    <div class="card-cover">
      <img v-if="animal.coverImage" :src="animal.coverImage" :alt="animal.name" />
      <div v-else class="cover-placeholder">
        <span class="placeholder-icon">{{ typeIcon }}</span>
      </div>
      <el-tag :type="statusType" size="small" class="status-tag">
        {{ animal.status === 1 ? '在校' : '已离校' }}
      </el-tag>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ animal.name }}</h3>
      <div class="card-meta">
        <el-tag size="small" :type="typeColor">{{ animal.type }}</el-tag>
        <span class="card-area">
          <el-icon><LocationFilled /></el-icon> {{ animal.area }}
        </span>
      </div>
      <p class="card-desc" v-if="animal.description">{{ animal.description }}</p>
    </div>
  </el-card>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  animal: { type: Object, required: true }
})

const typeIcons = { '猫': '🐱', '狗': '🐕', '鸟': '🐦' }
const typeIcon = computed(() => typeIcons[props.animal.type] || '🐾')

const typeColor = computed(() => {
  const map = { '猫': '', '狗': 'warning', '鸟': 'success' }
  return map[props.animal.type] || 'info'
})

const statusType = computed(() => props.animal.status === 1 ? 'success' : 'info')
</script>

<style scoped>
.animal-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  overflow: hidden;
}
.animal-card:hover {
  transform: translateY(-4px);
}

.card-cover {
  height: 180px;
  background: var(--primary-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  margin: -20px -20px 16px;
  border-radius: var(--radius) var(--radius) 0 0;
  overflow: hidden;
}
.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
.placeholder-icon {
  font-size: 56px;
  opacity: 0.6;
}
.status-tag {
  position: absolute;
  top: 10px;
  right: 10px;
}

.card-title {
  font-size: 17px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text);
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}
.card-area {
  font-size: 13px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 2px;
}

.card-desc {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}
</style>
