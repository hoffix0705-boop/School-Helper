<script setup>
import { ref, computed, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { getTaskDetail, cancelTask, acceptTask, completeTask } from "@/api/task"
import { useUserStore } from "@/stores/user"
import { ElMessage, ElMessageBox } from "element-plus"
import dayjs from "dayjs"

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const task = ref(null)

const statusMap = { 0: "待接单", 1: "进行中", 2: "已完成", 3: "已取消" }
const statusTypeMap = { 0: "warning", 1: "primary", 2: "success", 3: "info" }

const isPublisher = computed(() => task.value && userStore.userInfo?.id === task.value.userId)
const isAcceptor = computed(() => task.value && userStore.userInfo?.id === task.value.acceptorId)

async function fetchDetail() {
  loading.value = true
  try { task.value = await getTaskDetail(route.params.id) }
  finally { loading.value = false }
}
async function handleAccept() { try { await acceptTask(route.params.id); ElMessage.success("接单成功"); fetchDetail() } catch {} }
async function handleComplete() { try { await completeTask(route.params.id); ElMessage.success("任务已完成"); fetchDetail() } catch {} }
async function handleCancel() { try { await ElMessageBox.confirm("确认取消该任务？","提示",{type:"warning"}); await cancelTask(route.params.id); ElMessage.success("已取消"); fetchDetail() } catch {} }
function goEdit() { router.push(`/tasks/${route.params.id}/edit`) }
function fmt(t) { return t ? dayjs(t).format("YYYY-MM-DD HH:mm") : "-" }
onMounted(fetchDetail)
</script>
<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">任务详情</h2>
      <div class="header-actions">
        <el-button v-if="isPublisher && task?.status === 0" type="primary" @click="goEdit"><el-icon><Edit /></el-icon>编辑</el-button>
        <el-button v-if="isPublisher && task?.status === 0" type="danger" @click="handleCancel"><el-icon><Close /></el-icon>取消任务</el-button>
        <el-button v-if="!isPublisher && task?.status === 0" type="success" @click="handleAccept"><el-icon><Check /></el-icon>接单</el-button>
        <el-button v-if="isAcceptor && task?.status === 1" type="success" @click="handleComplete"><el-icon><Select /></el-icon>确认完成</el-button>
        <el-button @click="router.push('/tasks')">返回列表</el-button>
      </div>
    </div>
    <div class="card" v-loading="loading">
      <template v-if="task">
        <div class="detail-header">
          <h3>{{ task.title }}</h3>
          <div class="tags">
            <el-tag :type="statusTypeMap[task.status]" size="default">{{ statusMap[task.status] }}</el-tag>
            <el-tag v-if="task.urgent" type="danger" size="default">加急</el-tag>
          </div>
        </div>
        <el-descriptions :column="3" border class="detail-table">
          <el-descriptions-item label="分类">{{ task.categoryName || "-" }}</el-descriptions-item>
          <el-descriptions-item label="报酬">{{ task.price }} 元</el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ task.viewCount }}</el-descriptions-item>
          <el-descriptions-item label="发布人">{{ task.publisherNickname || "-" }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ task.contactPhone || "-" }}</el-descriptions-item>
          <el-descriptions-item label="地点">{{ task.address || "-" }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ fmt(task.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="截止时间">{{ fmt(task.deadline) }}</el-descriptions-item>
          <el-descriptions-item label="接单人" v-if="task.acceptorId">{{ task.acceptorNickname }}</el-descriptions-item>
          <el-descriptions-item label="接单时间" v-if="task.acceptTime">{{ fmt(task.acceptTime) }}</el-descriptions-item>
          <el-descriptions-item label="完成时间" v-if="task.completeTime">{{ fmt(task.completeTime) }}</el-descriptions-item>
          <el-descriptions-item label="描述" :span="3">
            <pre style="white-space: pre-wrap; margin: 0; font-size: 14px; line-height: 1.6;">{{ task.description || "暂无描述" }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </div>
  </div>
</template>
<style scoped lang="scss">
.page-container { max-width: 1000px; margin: 0 auto; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; flex-wrap: wrap; gap: 12px; }
.page-title { margin: 0; font-size: 22px; font-weight: 700; color: #1a1a2e; }
.header-actions { display: flex; gap: 8px; flex-wrap: wrap; }
.detail-header { display: flex; align-items: center; gap: 12px; margin-bottom: 24px; h3 { margin: 0; font-size: 20px; font-weight: 600; color: #1a1a2e; } }
.tags { display: flex; gap: 6px; }
.detail-table { :deep(td) { padding: 12px 16px; } }
</style>
