<script setup>
import { ref, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { getUserList, setUserStatus } from "@/api/user"

const loading = ref(false)
const list = ref([])
const total = ref(0)
const query = ref({ page: 1, pageSize: 10, keyword: "", userType: "", status: "", college: "" })

const userTypeOptions = [
  { value: "", label: "全部" },
  { value: 0, label: "管理员" },
  { value: 1, label: "普通用户" },
]

const genderMap = { 0: "未知", 1: "男", 2: "女" }

async function fetchData() {
  loading.value = true
  try {
    const params = { ...query.value }
    if (params.userType === "") delete params.userType
    if (params.status === "") delete params.status
    if (params.college === "") delete params.college
    const res = await getUserList(params)
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

async function handleToggleStatus(row) {
  const newStatus = row.status === 0 ? 1 : 0
  const action = newStatus === 0 ? "启用" : "禁用"
  try {
    await ElMessageBox.confirm(`确认${action}用户「${row.nickname || row.username}」？`, "提示", { type: "warning" })
    await setUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchData()
  } catch {
    // cancelled
  }
}

function formatTime(t) {
  if (!t) return "-"
  return t.substring(0, 16).replace("T", " ")
}

onMounted(fetchData)
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <span>用户管理</span>
      </template>

      <!-- 搜索 -->
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="用户名/昵称/手机号" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.userType" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="o in userTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="全部" :value="" />
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="query = { page: 1, pageSize: 10, keyword: '', userType: '', status: '', college: '' }; fetchData()">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" width="160" show-overflow-tooltip />
        <el-table-column label="性别" width="60">
          <template #default="{ row }">{{ genderMap[row.gender] || "-" }}</template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.userType === 0 ? 'danger' : 'primary'" size="small">
              {{ row.userType === 0 ? "管理员" : "用户" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'info'" size="small">
              {{ row.status === 0 ? "正常" : "禁用" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="college" label="学院" width="150" show-overflow-tooltip />
        <el-table-column prop="creditScore" label="信用分" width="80" />
        <el-table-column label="注册时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              :type="row.status === 0 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 0 ? "禁用" : "启用" }}
            </el-button>
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
