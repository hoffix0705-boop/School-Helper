<template>
  <el-breadcrumb separator-icon="ArrowRight">
    <el-breadcrumb-item :to="{ path: '/dashboard' }">
      <el-icon><HomeFilled /></el-icon>
    </el-breadcrumb-item>
    <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
      <el-icon v-if="item.meta?.icon"><component :is="item.meta.icon" /></el-icon>
      {{ item.meta?.title || item.name }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
import { computed } from "vue"
import { useRoute } from "vue-router"

const route = useRoute()
const breadcrumbs = computed(() => {
  const matched = route.matched.filter(r => r.path)
  return matched.slice(1)
})
</script>
