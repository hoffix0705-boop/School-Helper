<script setup>
import { ref, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from "@/api/category"

const loading = ref(false)
const treeData = ref([])
const dialogVisible = ref(false)
const formTitle = ref("")
const editing = ref(false)
const form = ref({ id: null, name: "", parentId: 0, sort: 0, icon: "", status: 1 })
const parentOptions = ref([{ id: 0, name: "顶级分类" }])

async function fetchTree() {
  loading.value = true
  try {
    treeData.value = await getCategoryTree()
  } finally {
    loading.value = false
  }
}

function flattenTree(list, depth = 0) {
  let result = []
  for (const item of list) {
    result.push({ ...item, _depth: depth })
    if (item.children?.length) {
      result = result.concat(flattenTree(item.children, depth + 1))
    }
  }
  return result
}

function buildParentOptions() {
  parentOptions.value = [{ id: 0, name: "顶级分类" }]
  function walk(list, depth) {
    for (const item of list) {
      parentOptions.value.push({
        id: item.id,
        name: `${"  ".repeat(depth)}${item.name}`,
        disabled: item.id === form.value.id,
      })
      if (item.children?.length) {
        walk(item.children, depth + 1)
      }
    }
  }
  walk(treeData.value, 0)
}

function openCreate(parentId = 0) {
  editing.value = false
  formTitle.value = "新增分类"
  form.value = { id: null, name: "", parentId, sort: 0, icon: "", status: 1 }
  buildParentOptions()
  dialogVisible.value = true
}

function openEdit(row) {
  editing.value = true
  formTitle.value = "编辑分类"
  form.value = { id: row.id, name: row.name, parentId: row.parentId || 0, sort: row.sort || 0, icon: row.icon || "", status: row.status ?? 1 }
  buildParentOptions()
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    if (editing.value) {
      await updateCategory(form.value.id, form.value)
      ElMessage.success("更新成功")
    } else {
      await createCategory(form.value)
      ElMessage.success("新增成功")
    }
    dialogVisible.value = false
    fetchTree()
  } catch {
    // handled
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm("确认删除该分类？", "提示", { type: "warning" })
    await deleteCategory(id)
    ElMessage.success("删除成功")
    fetchTree()
  } catch {
    // cancelled
  }
}

onMounted(fetchTree)
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span>分类管理</span>
          <el-button type="primary" @click="openCreate(0)">新增分类</el-button>
        </div>
      </template>

      <el-table :data="flattenTree(treeData)" v-loading="loading" row-key="id" stripe default-expand-all>
        <el-table-column label="分类名称" min-width="250">
          <template #default="{ row }">
            <span :style="{ marginLeft: row._depth * 24 + 'px' }">
              {{ row._depth > 0 ? "📁 " : "📂 " }}{{ row.name }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status ? 'success' : 'info'" size="small">{{ row.status ? "启用" : "禁用" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ row.createTime?.substring(0, 16).replace("T", " ") || "-" }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openCreate(row.id)">新增子分类</el-button>
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="formTitle" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="父分类">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="{ label: 'name', value: 'id', children: 'children', disabled: 'disabled' }"
            placeholder="选择父分类"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" required>
          <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="32" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 200px" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="图标类名或emoji" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>
