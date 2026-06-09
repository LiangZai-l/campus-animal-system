<!--
  管理员后台仪表盘 — 动物档案管理表格。

  路由：/admin（meta.admin: true，仅 ADMIN 角色可访问）

  功能：
  - 表格展示所有动物（ID、名称、类型、区域、状态、录入时间）
  - "查看"跳转详情页、"编辑"跳转编辑页、"删除"弹出确认框
  - 顶部"新增动物"按钮
  - 分页器

  数据流：
  - mounted → fetchList() → GET /api/animals?page=&size=
  - handleDelete(id) → DELETE /api/animals/:id → 成功 → refreshList()
-->
<template>
  <div class="page-container">
    <!-- 顶部：标题 + 新增按钮 -->
    <div class="admin-header">
      <h2>动物档案管理</h2>
      <el-button type="primary" size="large" @click="$router.push('/admin/animals/new')">
        <el-icon><Plus /></el-icon> 新增动物
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-table :data="animals" stripe style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="name" label="名称" width="140" />
      <el-table-column prop="type" label="类型" width="80" />
      <el-table-column prop="area" label="区域" width="120" />

      <!-- 状态列 — 自定义渲染为 Tag -->
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '在校' : '离校' }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 录入时间列 — 格式化 ISO 日期 -->
      <el-table-column prop="createdAt" label="录入时间" width="160">
        <template #default="{ row }">
          {{ row.createdAt ? row.createdAt.split('T')[0] : '-' }}
        </template>
      </el-table-column>

      <!-- 操作列 — 固定在右侧 -->
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" size="small"
            @click="$router.push(`/animals/${row.id}`)">查看</el-button>
          <el-button text size="small"
            @click="$router.push(`/admin/animals/${row.id}/edit`)">编辑</el-button>
          <!-- el-popconfirm：点击弹出确认气泡 -->
          <el-popconfirm title="确定删除该动物档案吗？相关打卡也会被删除。"
            @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button text type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        :page-size="size"
        :total="total"
        background
        layout="prev, pager, next"
        @current-change="fetchList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const animals = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const loading = ref(false)

/** 获取动物分页列表 */
async function fetchList() {
  loading.value = true
  try {
    const res = await api.get('/animals', {
      params: { page: page.value, size: size.value }
    })
    animals.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

/** 删除动物（确认后） */
async function handleDelete(id) {
  try {
    await api.delete(`/animals/${id}`)
    ElMessage.success('删除成功')
    // 删除后刷新列表（保持当前页）
    await fetchList()
  } catch {
    ElMessage.error('删除失败，请重试')
  }
}

onMounted(fetchList)
</script>

<style scoped>
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.admin-header h2 { font-size: 22px; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>
