<script setup>
import { ref, computed, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { getTaskDetail, cancelTask, acceptTask, completeTask } from "@/api/task"
import { useUserStore } from "@/stores/user"
import { ElMessage, ElMessageBox } from "element-plus"

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
  try {
    task.value = await getTaskDetail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function handleAccept() {
  try {
    await acceptTask(route.params.id)
    ElMessage.success("接单成功")
    fetchDetail()
  } catch (e) {
    // already handled by interceptor
  }
}

async function handleComplete() {
  try {
    await completeTask(route.params.id)
    ElMessage.success("任务已完成")
    fetchDetail()
  } catch (e) {
    // already handled
  }
}

async function handleCancel() {
  try {
    await ElMessageBox.confirm("确认取消该任务？", "提示", { type: "warning" })
    await cancelTask(route.params.id)
    ElMessage.success("已取消")
    fetchDetail()
  } catch {
    // cancelled
  }
}

function goEdit() {
  router.push(`/tasks/${route.params.id}/edit`)
}

function formatTime(t) {
  if (!t) return "-"
  return t.substring(0, 16).replace("T", " ")
}

onMounted(fetchDetail)
</script>

<template>
  <el-card v-loading="loading" v-if="task">
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>任务详情</span>
        <div>
          <el-button v-if="isPublisher && task.status === 0" type="primary" @click="goEdit">编辑</el-button>
          <el-button v-if="isPublisher && task.status === 0" type="danger" @click="handleCancel">取消任务</el-button>
          <el-button v-if="!isPublisher && task.status === 0" type="success" @click="handleAccept">接单</el-button>
          <el-button v-if="isAcceptor && task.status === 1" type="success" @click="handleComplete">确认完成</el-button>
          <el-button @click="router.push('/tasks')">返回</el-button>
        </div>
      </div>
    </template>

    <el-descriptions :column="2" border>
      <el-descriptions-item label="标题" :span="2">{{ task.title }}</el-descriptions-item>
      <el-descriptions-item label="分类">{{ task.categoryName || "-" }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusTypeMap[task.status]" size="small">{{ statusMap[task.status] }}</el-tag>
        <el-tag v-if="task.urgent" type="danger" size="small" style="margin-left: 4px">加急</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="报酬">{{ task.price }} 元</el-descriptions-item>
      <el-descriptions-item label="发布人">{{ task.publisherNickname || "-" }}</el-descriptions-item>
      <el-descriptions-item label="联系电话">{{ task.contactPhone || "-" }}</el-descriptions-item>
      <el-descriptions-item label="地点">{{ task.address || "-" }}</el-descriptions-item>
      <el-descriptions-item label="截止时间">{{ formatTime(task.deadline) }}</el-descriptions-item>
      <el-descriptions-item label="浏览次数">{{ task.viewCount }}</el-descriptions-item>
      <el-descriptions-item label="发布时间">{{ formatTime(task.createTime) }}</el-descriptions-item>
      <!-- 接单人信息 -->
      <el-descriptions-item v-if="task.acceptorId" label="接单人">{{ task.acceptorNickname }}</el-descriptions-item>
      <el-descriptions-item v-if="task.acceptTime" label="接单时间">{{ formatTime(task.acceptTime) }}</el-descriptions-item>
      <el-descriptions-item v-if="task.completeTime" label="完成时间">{{ formatTime(task.completeTime) }}</el-descriptions-item>
      <el-descriptions-item label="描述" :span="2">
        <pre style="white-space: pre-wrap; margin: 0">{{ task.description || "暂无描述" }}</pre>
      </el-descriptions-item>
    </el-descriptions>
  </el-card>
</template>
