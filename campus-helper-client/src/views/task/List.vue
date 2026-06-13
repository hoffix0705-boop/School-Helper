<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { ElMessage, ElMessageBox } from "element-plus"
import { getTaskList, cancelTask } from "@/api/task"
import { getActiveCategoryTree } from "@/api/category"
import { useUserStore } from "@/stores/user"
import { Search } from "@element-plus/icons-vue"
import dayjs from "dayjs"

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const list = ref([])
const total = ref(0)
const categoryOptions = ref([])
const query = ref({ page: 1, pageSize: 10, keyword: "", status: "", categoryId: "" })
const activeTab = ref("all")
const statusMap = { 0: "待接单", 1: "进行中", 2: "已完成", 3: "已取消" }
const statusTypeMap = { 0: "warning", 1: "primary", 2: "success", 3: "info" }
const statusOpts = [
  { value: "", label: "全部" },
  { value: 0, label: "待接单" },
  { value: 1, label: "进行中" },
  { value: 2, label: "已完成" }
]

function flattenOpts(list, d) {
  const r = []
  for (const i of list) {
    r.push({ value: i.id, label: (d ? "\u3000".repeat(d) : "") + i.name })
    if (i.children && i.children.length) r.push(...flattenOpts(i.children, d + 1))
  }
  return r
}

function handleTab(t) {
  activeTab.value = t
  if (t !== "published") delete query.value.userId
  else query.value.userId = userStore.userInfo?.id
  query.value.page = 1
  nextTickFetch()
}

function nextTickFetch() {
  query.value.page = 1
  fetchData()
}

async function fetchData() {
  loading.value = true
  try {
    const p = { page: query.value.page, pageSize: query.value.pageSize }
    if (query.value.keyword) p.keyword = query.value.keyword
    if (query.value.status !== "" && query.value.status !== null) p.status = query.value.status
    if (query.value.categoryId) p.categoryId = query.value.categoryId
    if (query.value.userId) p.userId = query.value.userId

    const r = await getTaskList(p)
    list.value = Array.isArray(r?.records) ? r.records : []
    total.value = r?.total ?? 0
  } catch (e) {
    console.warn("fetchData error:", e)
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function search() {
  query.value.page = 1
  fetchData()
}

function goDetail(id) {
  router.push("/tasks/" + id)
}

function goCreate() {
  router.push("/tasks/create")
}

function handlePageChange(page) {
  query.value.page = page
  fetchData()
}

async function handleCancel(id) {
  try {
    await ElMessageBox.confirm("确认取消？", "提示", { type: "warning" })
    await cancelTask(id)
    ElMessage.success("已取消")
    fetchData()
  } catch {
    // user cancelled the confirm dialog
  }
}

function fmt(t) {
  return t ? dayjs(t).format("YYYY-MM-DD HH:mm") : "-"
}

async function loadCats() {
  try {
    const t = await getActiveCategoryTree()
    categoryOptions.value = [{ value: "", label: "全部分类" }, ...flattenOpts(t || [], 0)]
  } catch {
    categoryOptions.value = [{ value: "", label: "全部分类" }]
  }
}

onMounted(() => {
  loadCats()
  fetchData()
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">任务管理</h2>
      <el-button type="primary" @click="goCreate">
        <el-icon><Plus /></el-icon>发布任务
      </el-button>
    </div>
    <div class="card">
      <el-tabs :model-value="activeTab" @tab-change="handleTab">
        <el-tab-pane label="全部任务" name="all" />
        <el-tab-pane label="我的发布" name="published" />
      </el-tabs>
      <div class="search-bar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索标题"
          clearable
          style="width:200px"
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width:150px">
          <el-option
            v-for="o in categoryOptions"
            :key="o.value + o.label"
            :label="o.label"
            :value="o.value"
          />
        </el-select>
        <el-select v-model="query.status" placeholder="全部状态" clearable style="width:130px">
          <el-option
            v-for="o in statusOpts"
            :key="String(o.value)"
            :label="o.label"
            :value="o.value"
          />
        </el-select>
        <el-button type="primary" @click="search">搜索</el-button>
        <el-button @click="query={page:1,pageSize:10,keyword:'',status:'',categoryId:''};activeTab='all';delete query.value.userId;fetchData()">重置</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe style="width:100%">
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="90" />
        <el-table-column label="报酬" width="80">
          <template #default="{ row }">{{ row.price ?? 0 }}元</template>
        </el-table-column>
        <el-table-column prop="publisherNickname" label="发布人" width="100" />
        <el-table-column label="接单人" width="100">
          <template #default="{ row }">{{ row.acceptorNickname || "-" }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTypeMap[row.status]" size="small">{{ statusMap[row.status] }}</el-tag>
            <el-tag v-if="row.urgent" type="danger" size="small" style="margin-left:4px">加急</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="时间" width="150">
          <template #default="{ row }">{{ fmt(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
            <el-button
              v-if="row.status === 0 && userStore.userInfo?.id === row.userId"
              link
              type="danger"
              @click="handleCancel(row.id)"
            >取消</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          :page-size="query.pageSize"
          :total="total"
          layout="total, prev, pager, next"
          background
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.page-container { max-width: 1400px; margin: 0 auto; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.page-title { margin: 0; font-size: 22px; font-weight: 700; color: #1a1a2e; }
.search-bar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 20px; }
:deep(.el-tabs) { margin-bottom: 16px; }
</style>
