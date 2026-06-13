<script setup>
import { ref, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from "@/api/category"

const loading = ref(false)
const treeData = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const form = ref({ parentId: null, name: "", sort: 0, icon: "", status: 1 })

function loadData() {
  loading.value = true
  getCategoryTree().then(d => { treeData.value = d || [] }).finally(() => { loading.value = false })
}
function openAdd(parentId) {
  editingId.value = null
  form.value = { parentId, name: "", sort: 0, icon: "", status: 1 }
  dialogVisible.value = true
}
function openEdit(row) {
  editingId.value = row.id
  form.value = { parentId: row.parentId, name: row.name, sort: row.sort, icon: row.icon, status: row.status }
  dialogVisible.value = true
}
async function handleSubmit() {
  try {
    if (editingId.value) {
      await updateCategory(editingId.value, form.value)
      ElMessage.success("更新成功")
    } else {
      await createCategory(form.value)
      ElMessage.success("创建成功")
    }
    dialogVisible.value = false
    loadData()
  } catch {}
}
async function handleDelete(id) {
  try {
    await ElMessageBox.confirm("确认删除？子分类也会被删除","警告",{confirmButtonText:"删除",confirmButtonClass:"el-button--danger",type:"warning"})
    await deleteCategory(id)
    ElMessage.success("已删除")
    loadData()
  } catch {}
}
onMounted(loadData)
</script>
<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">分类管理</h2>
      <el-button type="primary" @click="openAdd(null)"><el-icon><Plus /></el-icon>添加分类</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="treeData" row-key="id" default-expand-all stripe style="width:100%">
        <el-table-column prop="name" label="名称" min-width="180">
          <template #default="{ row }"><span style="font-weight:500">{{ row.icon || "📁" }} {{ row.name }}</span></template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center"/>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?"启用":"禁用" }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openAdd(row.id)"><el-icon><Plus /></el-icon>添加子分类</el-button>
            <el-button link type="primary" size="small" @click="openEdit(row)"><el-icon><Edit /></el-icon>编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row.id)"><el-icon><Delete /></el-icon>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId?'编辑分类':'添加分类'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="父分类">
          <el-tree-select v-model="form.parentId" :data="treeData" :props="{label:'name',value:'id',children:'children'}" placeholder="顶级分类" check-strictly clearable style="width:100%"/>
        </el-form-item>
        <el-form-item label="名称" required><el-input v-model="form.name" placeholder="请输入" maxlength="32"/></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" style="width:180px"/></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="emoji 或图标"/></el-form-item>
        <el-form-item label="状态"><el-switch v-model="form.status" :active-value="1" :inactive-value="0"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible=false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<style scoped lang="scss">
.page-container { max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.page-title { margin: 0; font-size: 22px; font-weight: 700; color: #1a1a2e; }
</style>
