<template>
  <template v-for="item in menuList" :key="item.path">
    <el-menu-item v-if="!item.children?.length" :index="item.path">
      <el-icon v-if="item.meta?.icon"><component :is="item.meta.icon" /></el-icon>
      <template #title>
        <span>{{ item.meta?.title || item.name }}</span>
      </template>
    </el-menu-item>
    <el-sub-menu v-else :index="item.path">
      <template #title>
        <el-icon v-if="item.meta?.icon"><component :is="item.meta.icon" /></el-icon>
        <span>{{ item.meta?.title || item.name }}</span>
      </template>
      <SubMenu :menuList="item.children" />
    </el-sub-menu>
  </template>
</template>

<script setup>
defineProps({ menuList: { type: Array, default: () => [] } })
</script>
