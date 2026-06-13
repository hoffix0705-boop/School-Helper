<script setup>
import { ref, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { getUserList, setUserStatus } from "@/api/user"
import { Search } from "@element-plus/icons-vue"
import dayjs from "dayjs"

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ page: 1, pageSize: 10, keyword: "", userType: "", status: "" })
const genderMap = { 0: "未知", 1: "男", 2: "女" }

async function fetchData() {
  loading.value = true
  try {
    const p = { page: query.value.page, pageSize: query.value.pageSize }
    if (query.value.keyword) p.keyword = query.value.keyword
    if (query.value.userType !== "" && query.value.userType !== null) p.userType = query.value.userType
    if (query.value.status !== "" && query.value.status !== null) p.status = query.value.status

    const r = await getUserList(p)
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

function handleSearch() {
  query.value.page = 1
  fetchData()
}

function handlePageChange(page) {
  query.value.page = page
  fetchData()
}

async function toggleStatus(r) {
  const ns = r.status === 0 ? 1 : 0
  const action = ns === 0 ? "启用" : "禁用"
  try {
    await ElMessageBox.confirm("确认" + action + "用户「" + (r.nickname || r.username) + "」？", "提示", { type: "warning" })
    await setUserStatus(r.id, ns)
    ElMessage.success(action + "成功")
    fetchData()
  } catch {
    // user cancelled
  }
}

function fmt(t) {
  return t ? dayjs(t).format("YYYY-MM-DD HH:mm") : "-"
}

onMounted(fetchData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">用户管理</h2>
      <span class="page-subtitle">共 {{ total }} 个用户</span>
    </div>
    <div class="card">
      <div class="search-bar">
        <el-input
          v-model="query.keyword"
          placeholder="搜索用户名/昵称/手机号"
          clearable
          style="width:260px"
          @keyup.enter="handleSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-select v-model="query.userType" placeholder="类型" clearable style="width:110px" @change="handleSearch">
          <el-option label="全部" :value="null" />
          <el-option label="管理员" :value="0" />
          <el-option label="普通用户" :value="1" />
        </el-select>
        <el-select v-model="query.status" placeholder="状态" clearable style="width:110px" @change="handleSearch">
          <el-option label="全部" :value="null" />
          <el-option label="正常" :value="0" />
          <el-option label="禁用" :value="1" />
        </el-select>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="query.value={page:1,pageSize:10,keyword:'',userType:null,status:null};fetchData()">重置</el-button>
      </div>
      <el-table :data="list" v-loading="loading" stripe style="width:100%">
        <el-table-column prop="username" label="用户名" width="110" />
        <el-table-column prop="nickname" label="昵称" width="110" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="150" show-overflow-tooltip />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.userType===0?'danger':'primary'" size="small">{{ row.userType===0?"管理员":"用户" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status===0?'success':'info'" size="small">{{ row.status===0?"正常":"禁用" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creditScore" label="信用分" width="70" />
        <el-table-column label="时间" width="150">
          <template #default="{ row }">{{ fmt(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button :type="row.status===0?'warning':'success'" link @click="toggleStatus(row)">
              {{ row.status===0?"禁用":"启用" }}
            </el-button>
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
.page-header { display: flex; align-items: center; gap: 16px; margin-bottom: 20px; }
.page-title { margin: 0; font-size: 22px; font-weight: 700; color: #1a1a2e; }
.page-subtitle { font-size: 13px; color: #909399; }
.search-bar { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
