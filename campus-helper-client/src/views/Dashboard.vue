<template>
  <div class="dashboard">
    <div class="welcome-banner">
      <div class="banner-content">
        <h1>欢迎回来，{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}！</h1>
        <p>这是校园帮管理后台，在这里你可以管理任务、分类和用户。</p>
      </div>
      <img class="banner-img" src="@/assets/images/welcome.png" alt="welcome" />
    </div>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <div class="stat-card" :style="{ background: stat.bg }">
          <div class="stat-info">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
          <el-icon class="stat-icon" :size="48"><component :is="stat.icon" /></el-icon>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="dashboard-panels">
      <el-col :span="16">
        <div class="panel card">
          <div class="panel-header">
            <h3>最新任务</h3>
          </div>
          <el-table :data="recentTasks" v-loading="loadingTasks" stripe style="width: 100%">
            <el-table-column prop="title" label="任务标题" min-width="180" />
            <el-table-column label="赏金(元)" width="100">
              <template #default="{ row }">{{ row.price ?? 0 }}元</template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="statusTag(row.status)" size="small">{{ statusMap[row.status] || '' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="发布时间" width="160">
              <template #default="{ row }">{{ formatTime(row.createTime || row.create_time) }}</template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="panel card">
          <div class="panel-header">
            <h3>快速操作</h3>
          </div>
          <div class="quick-actions">
            <el-button type="primary" size="large" class="action-btn" @click="$router.push('/tasks/create')">
              <el-icon><Plus /></el-icon>发布任务
            </el-button>
            <el-button size="large" class="action-btn" @click="$router.push('/tasks')">
              <el-icon><List /></el-icon>任务管理
            </el-button>
            <el-button v-if="isAdmin" size="large" class="action-btn" @click="$router.push('/categories')">
              <el-icon><FolderOpened /></el-icon>分类管理
            </el-button>
            <el-button v-if="isAdmin" size="large" class="action-btn" @click="$router.push('/users')">
              <el-icon><User /></el-icon>用户管理
            </el-button>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue"
import { useUserStore } from "@/stores/user"
import { getDashboardStats, getRecentTasks } from "@/api/dashboard"
import { Plus, List, FolderOpened, User, Tickets, Check, Clock, DataBoard } from "@element-plus/icons-vue"
import dayjs from "dayjs"

const userStore = useUserStore()
const isAdmin = computed(() => userStore.userInfo?.userType === 0)

const stats = ref([
  { label: "任务总量", value: 0, icon: Tickets, bg: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)" },
  { label: "进行中", value: 0, icon: Clock, bg: "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)" },
  { label: "已完成", value: 0, icon: Check, bg: "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)" },
  { label: "用户数", value: 0, icon: DataBoard, bg: "linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)" },
])
const recentTasks = ref([])
const loadingTasks = ref(false)

const statusMap = { 0: "待接单", 1: "进行中", 2: "已完成", 3: "已取消" }
const statusTag = (s) => {
  const map = { 0: "", 1: "warning", 2: "success", 3: "danger" }
  return map[s] || ""
}
const formatTime = (t) => t ? dayjs(t).format("YYYY-MM-DD HH:mm") : ""

onMounted(async () => {
  try {
    const data = await getDashboardStats()
    if (data) {
      stats.value[0].value = data.totalTasks ?? 0
      stats.value[1].value = data.inProgressTasks ?? 0
      stats.value[2].value = data.completedTasks ?? 0
      stats.value[3].value = data.totalUsers ?? 0
    }
  } catch (_) {}

  loadingTasks.value = true
  try {
    const tasks = await getRecentTasks()
    recentTasks.value = tasks || []
  } catch (_) {
    recentTasks.value = []
  } finally {
    loadingTasks.value = false
  }
})
</script>

<style scoped lang="scss">
.dashboard { max-width: 1400px; margin: 0 auto; }
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 32px;
  margin-bottom: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #fff;
  .banner-content {
    h1 { font-size: 24px; font-weight: 700; margin: 0 0 8px; }
    p { font-size: 14px; opacity: 0.9; margin: 0; }
  }
  .banner-img { width: 140px; height: auto; }
}
.stat-cards { margin-bottom: 24px; }
.stat-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  border-radius: 12px;
  color: #fff;
  box-shadow: 0 6px 16px rgba(0,0,0,0.08);
  .stat-info { .stat-value { font-size: 32px; font-weight: 800; line-height: 1.2; } .stat-label { font-size: 14px; opacity: 0.85; margin-top: 4px; } }
  .stat-icon { opacity: 0.3; }
}
.dashboard-panels { margin-bottom: 20px; }
.panel {
  .panel-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16px;
    h3 { margin: 0; font-size: 16px; font-weight: 600; color: #1a1a2e; }
  }
}
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  .action-btn {
    width: 100%;
    height: 44px;
    justify-content: flex-start;
    font-size: 14px;
    border-radius: 8px;
  }
}
</style>
