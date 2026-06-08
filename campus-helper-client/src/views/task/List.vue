<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { ElMessage, ElMessageBox } from "element-plus"
import { getTaskList, cancelTask } from "@/api/task"
import { getActiveCategoryTree } from "@/api/category"
import { useUserStore } from "@/stores/user"

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const categoryOptions = ref([])

const query = ref({ page: 1, pageSize: 10, keyword: "", status: "", categoryId: "" })
const activeTab = ref("all")

const statusOptions = [
  { value: "", label: "全部" },
  { value: 0, label: "待接单" },
  { value: 1, label: "进行中" },
  { value: 2, label: "已完成" },
  { value: 3, label: "已取消" },
]

const statusMap = { 0: "待接单", 1: "进行中", 2: "已完成", 3: "已取消" }
const statusTypeMap = { 0: "warning", 1: "primary", 2: "success", 3: "info" }

function flattenOptions(list, depth = 0) {
  let result = [{ value: "", label: "全部" }]
  for (const item of list) {
    result.push({ value: item.id, label: `${"　".repeat(depth)}${item.name}` })
    if (item.children?.length) {
      result = result.concat(flattenOptions(item.children, depth + 1))
    }
  }
  return result
}

async function loadCategories() {
  try {
    const tree = await getActiveCategoryTree()
    categoryOptions.value = flattenOptions(tree)
  } catch {
    // fallback
  }
}

function handleTabChange(tab) {
  activeTab.value = tab
  delete query.value.userId
  if (tab === "published") {
    query.value.userId = userStore.userInfo?.id
  }
  query.value.page = 1
  fetchData()
}

async function fetchData() {
  loading.value = true
  try {
    const params = { ...query.value }
    if (params.status === "") delete params.status
    if (params.categoryId === "") delete params.categoryId
    const res = await getTaskList(params)
    list.value = res.records
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.value.page = 1
  fetchData()
}

function handlePageChange(page) {
  query.value.page = page
  fetchData()
}

function goDetail(id) {
  router.push(`/tasks/${id}`)
}

function goCreate() {
  router.push("/tasks/create")
}

async function handleCancel(id) {
  try {
    await ElMessageBox.confirm("确认取消该任务？", "提示", { type: "warning" })
    await cancelTask(id)
    ElMessage.success("已取消")
    fetchData()
  } catch {
    // cancelled
  }
}

function formatTime(t) {
  if (!t) return "-"
  return t.substring(0, 16).replace("T", " ")
}

onMounted(() => {
  loadCategories()
  fetchData()
})
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>任务管理</span>
          <el-button type="primary" @click="goCreate">发布任务</el-button>
        </div>
      </template>

      <el-tabs :model-value="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部任务" name="all" />
        <el-tab-pane label="我的发布" name="published" />
      </el-tabs>

      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="任务标题" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width: 160px">
            <el-option v-for="o in categoryOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="o in statusOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="query = { page: 1, pageSize: 10, keyword: '', status: '', categoryId: '' }; handleTabChange('all')">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="price" label="报酬(元)" width="100">
          <template #default="{ row }">{{ row.price || 0 }} 元</template>
        </el-table-column>
        <el-table-column prop="publisherNickname" label="发布人" width="120" />
        <el-table-column label="接单人" width="120">
          <template #default="{ row }">{{ row.acceptorNickname || "-" }}</template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTypeMap[row.status]" size="small">{{ statusMap[row.status] }}</el-tag>
            <el-tag v-if="row.urgent" type="danger" size="small" style="margin-left: 4px">加急</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
            <el-button v-if="row.status === 0 && userStore.userInfo?.id === row.userId" link type="danger" @click="handleCancel(row.id)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 16px; text-align: right">
        <el-pagination
          v-model:current-page="query.page"
          :page-size="query.pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
  </div>
</template>
