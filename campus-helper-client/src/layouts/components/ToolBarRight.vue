<template>
  <div class="tool-bar-ri">
    <div class="header-icon">
      <el-tooltip effect="dark" content="全屏" placement="bottom">
        <el-icon class="icon-style" @click="toggleFullscreen">
          <FullScreen />
        </el-icon>
      </el-tooltip>
    </div>
    <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
    <el-dropdown trigger="click" @command="handleCommand">
      <div class="avatar">
        <el-avatar :size="36" icon="UserFilled" />
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile">
            <el-icon><User /></el-icon>个人信息
          </el-dropdown-item>
          <el-dropdown-item command="logout" divided>
            <el-icon><SwitchButton /></el-icon>退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router"
import { useUserStore } from "@/stores/user"
import { ElMessageBox, ElMessage } from "element-plus"

const router = useRouter()
const userStore = useUserStore()

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleCommand = (cmd) => {
  if (cmd === "logout") {
    ElMessageBox.confirm("确定退出登录吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    }).then(() => {
      userStore.logout()
      router.push("/login")
      ElMessage.success("退出成功")
    }).catch(() => {})
  }
}
</script>

<style scoped>
.tool-bar-ri { display: flex; align-items: center; padding-right: 10px; }
.header-icon { display: flex; align-items: center; }
.icon-style {
  margin-left: 21px;
  font-size: 20px;
  color: var(--el-header-text-color);
  cursor: pointer;
  transition: all 0.3s;
}
.icon-style:hover { color: var(--el-color-primary); }
.username { margin: 0 15px; font-size: 14px; color: var(--el-header-text-color); }
.avatar { cursor: pointer; }
</style>
