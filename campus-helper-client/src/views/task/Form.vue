<script setup>
import { ref, onMounted } from "vue"
import { useRouter, useRoute } from "vue-router"
import { ElMessage } from "element-plus"
import { createTask, getTaskDetail, updateTask } from "@/api/task"
import { getActiveCategoryTree } from "@/api/category"

const router = useRouter()
const route = useRoute()
const isEdit = !!route.params.id
const loading = ref(false)
const submitting = ref(false)
const categoryTree = ref([])

const form = ref({
  title: "",
  description: "",
  categoryId: null,
  price: null,
  contactPhone: "",
  address: "",
  urgent: 0,
  deadline: null,
})

async function loadCategories() {
  try {
    categoryTree.value = await getActiveCategoryTree()
  } catch {
    // fallback
  }
}

function flattenOptions(list, depth = 0) {
  let result = []
  for (const item of list) {
    result.push({ ...item, _label: `${"　".repeat(depth)}${item.name}`, _depth: depth })
    if (item.children?.length) {
      result = result.concat(flattenOptions(item.children, depth + 1))
    }
  }
  return result
}

async function fetchDetail() {
  if (!isEdit) return
  loading.value = true
  try {
    const data = await getTaskDetail(route.params.id)
    form.value = {
      title: data.title,
      description: data.description,
      categoryId: data.categoryId,
      price: data.price,
      contactPhone: data.contactPhone,
      address: data.address,
      urgent: data.urgent,
      deadline: data.deadline,
    }
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  submitting.value = true
  try {
    if (isEdit) {
      await updateTask(route.params.id, form.value)
      ElMessage.success("更新成功")
    } else {
      await createTask(form.value)
      ElMessage.success("发布成功")
    }
    router.push("/tasks")
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadCategories()
  fetchDetail()
})
</script>

<template>
  <el-card v-loading="loading">
    <template #header>{{ isEdit ? "编辑任务" : "发布任务" }}</template>
    <el-form :model="form" label-width="100px" style="max-width: 700px">
      <el-form-item label="标题" required>
        <el-input v-model="form.title" placeholder="任务标题" maxlength="128" show-word-limit />
      </el-form-item>
      <el-form-item label="分类">
        <el-tree-select
          v-model="form.categoryId"
          :data="categoryTree"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="选择任务分类"
          check-strictly
          clearable
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="描述">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="任务详情描述" />
      </el-form-item>
      <el-form-item label="报酬(元)" required>
        <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 200px" />
      </el-form-item>
      <el-form-item label="联系电话">
        <el-input v-model="form.contactPhone" placeholder="手机号" />
      </el-form-item>
      <el-form-item label="地点">
        <el-input v-model="form.address" placeholder="任务地点" />
      </el-form-item>
      <el-form-item label="加急">
        <el-switch v-model="form.urgent" :active-value="1" :inactive-value="0" />
      </el-form-item>
      <el-form-item label="截止时间">
        <el-date-picker v-model="form.deadline" type="datetime" placeholder="选择截止时间" style="width: 100%" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">{{ isEdit ? "保存" : "发布" }}</el-button>
        <el-button @click="router.push('/tasks')">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
