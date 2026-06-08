<script setup>
import { useRouter, useRoute } from "vue-router"
import { useUserStore } from "@/stores/user"
import { useAppStore } from "@/stores/app"

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()

function handleLogout() {
  userStore.logout()
  router.push("/login")
}
</script>

<template>
  <el-container style="height: 100vh">
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'">
      <el-menu
        :collapse="appStore.sidebarCollapsed"
        :default-active="route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        style="height: 100%; border-right: none"
      >
        <div class="sidebar-title">
          <span v-if="!appStore.sidebarCollapsed">🏫 校园帮</span>
          <span v-else>🏫</span>
        </div>
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>控制台</template>
        </el-menu-item>
        <el-menu-item index="/tasks">
          <el-icon><List /></el-icon>
          <template #title>任务管理</template>
        </el-menu-item>
        <el-menu-item index="/categories">
          <el-icon><Collection /></el-icon>
          <template #title>分类管理</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <el-button :icon="appStore.sidebarCollapsed ? 'Expand' : 'Fold'" text @click="appStore.toggleSidebar()" />
        <el-dropdown @command="handleLogout">
          <span style="cursor: pointer">
            {{ userStore.userInfo?.nickname || "管理员" }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.sidebar-title {
  height: 56px;
  line-height: 56px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
}
</style>
