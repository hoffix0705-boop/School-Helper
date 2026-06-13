<template>
  <el-icon class="collapse-icon" @click="toggleCollapse">
    <component :is="globalStore.isCollapse ? 'Expand' : 'Fold'" />
  </el-icon>
</template>

<script setup>
import { useGlobalStore } from "@/stores/modules/global"
const globalStore = useGlobalStore()
const toggleCollapse = () => globalStore.setGlobalState("isCollapse", !globalStore.isCollapse)
</script>

<style scoped>
.collapse-icon {
  margin-right: 20px;
  font-size: 22px;
  color: var(--el-header-text-color);
  cursor: pointer;
  transition: all 0.3s;
}
.collapse-icon:hover { color: var(--el-color-primary); }
</style>
