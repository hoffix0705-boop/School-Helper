<template>
  <div class="layout-wrapper">
    <div class="sidebar" :style="{ width: isCollapse ? '64px' : '220px' }">
      <div class="logo-box">
        <img class="logo-img" src="@/assets/images/logo.svg" alt="logo" />
        <span v-show="!isCollapse" class="logo-text">校园帮</span>
      </div>
      <div class="nav-list">
        <template v-for="item in visibleMenuList" :key="item.path">
          <div
            v-if="!item.children?.length"
            class="nav-item"
            :class="{ active: route.path === item.path }"
            @click="router.push(item.path)"
          >
            <el-icon v-if="item.meta?.icon" :size="18"><component :is="item.meta.icon" /></el-icon>
            <span v-show="!isCollapse" class="nav-label">{{ item.meta?.title }}</span>
          </div>
        </template>
      </div>
    </div>
    <div class="main-area">
      <div class="topbar">
        <div class="topbar-left">
          <el-icon class="toggle-btn" @click="toggleCollapse"><Fold /></el-icon>
        </div>
        <div class="topbar-right">
          <el-icon class="topbar-icon" @click="toggleFullscreen"><FullScreen /></el-icon>
          <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '' }}</span>
          <el-dropdown @command="handleCommand">
            <span class="avatar-trigger"><el-avatar :size="32" icon="UserFilled" /></span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout"><el-icon><SwitchButton /></el-icon>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      <div class="content-area">
        <router-view :key="route.fullPath" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useGlobalStore } from "@/stores/modules/global"
import { useUserStore } from "@/stores/user"
import { MenuList } from "@/router/index"
import { ElMessageBox, ElMessage } from "element-plus"

const route = useRoute()
const router = useRouter()
const globalStore = useGlobalStore()
const userStore = useUserStore()
const isCollapse = computed(() => globalStore.isCollapse)

const visibleMenuList = computed(() => {
  return MenuList.filter(item => {
    if (item.meta?.hidden) return false
    if (item.meta?.roles && !item.meta.roles.includes(userStore.userInfo?.userType)) return false
    return true
  })
})

function toggleCollapse() { globalStore.setGlobalState("isCollapse", !globalStore.isCollapse) }

function toggleFullscreen() {
  if (!document.fullscreenElement) document.documentElement.requestFullscreen()
  else document.exitFullscreen()
}

function handleCommand(cmd) {
  if (cmd === "logout") {
    ElMessageBox.confirm("确定退出登录？","提示",{confirmButtonText:"确定",cancelButtonText:"取消",type:"warning"}).then(()=>{
      userStore.logout()
      router.push("/login")
      ElMessage.success("退出成功")
    }).catch(()=>{})
  }
}
</script>

<style scoped lang="scss">
.layout-wrapper { display: flex; width: 100%; height: 100vh; background: #f5f7fa; }
.sidebar {
  display: flex; flex-direction: column; height: 100%;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  transition: width 0.3s ease; overflow: hidden; flex-shrink: 0;
}
.logo-box {
  display: flex; align-items: center; gap: 10px; height: 60px; padding: 0 16px;
  .logo-img { width: 32px; height: 32px; flex-shrink: 0; }
  .logo-text { font-size: 20px; font-weight: 700; color: #fff; white-space: nowrap; letter-spacing: 2px; }
}
.nav-list { flex: 1; overflow-y: auto; padding: 8px 0; }
.nav-item {
  display: flex; align-items: center; gap: 10px;
  height: 44px; margin: 2px 8px; padding: 0 12px;
  border-radius: 8px; cursor: pointer;
  color: rgba(255,255,255,0.7); font-size: 14px;
  transition: all 0.2s;
  user-select: none;
  white-space: nowrap;
  &:hover { background: rgba(255,255,255,0.1); color: #fff; }
  &.active {
    background: linear-gradient(135deg, #409eff 0%, #637bfe 100%);
    color: #fff; font-weight: 600;
    box-shadow: 0 4px 12px rgba(64,158,255,0.3);
  }
  .nav-label { overflow: hidden; text-overflow: ellipsis; }
}
.main-area { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.topbar {
  display: flex; align-items: center; justify-content: space-between;
  height: 56px; padding: 0 20px;
  background: #fff; border-bottom: 1px solid #ebeef5;
  box-shadow: 0 1px 4px rgba(0,0,0,0.04); flex-shrink: 0;
}
.topbar-left { display: flex; align-items: center; gap: 16px; }
.topbar-right { display: flex; align-items: center; gap: 16px; }
.toggle-btn, .topbar-icon { font-size: 20px; cursor: pointer; color: #606266; &:hover { color: #409eff; } }
.username { font-size: 14px; color: #606266; }
.avatar-trigger { cursor: pointer; display: flex; }
.content-area { flex: 1; padding: 20px; overflow-y: auto; background: #f5f7fa; }
</style>
