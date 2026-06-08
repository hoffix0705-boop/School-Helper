<script setup>
import { ref } from "vue"
import { useRouter } from "vue-router"
import { ElMessage } from "element-plus"
import { useUserStore } from "@/stores/user"

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({ username: "", password: "" })
const loading = ref(false)

function handleLogin() {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning("请输入账号和密码")
    return
  }
  loading.value = true
  // TODO: 联调后端 auth/login 接口
  setTimeout(() => {
    userStore.setUserToken("mock-token")
    router.push("/")
    loading.value = false
  }, 500)
}
</script>

<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">校园帮 · 管理端</h2>
      <el-form :model="loginForm" @keyup.enter="handleLogin">
        <el-form-item>
          <el-input
            v-model="loginForm.username"
            placeholder="用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            prefix-icon="Lock"
            size="large"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}
.login-title {
  text-align: center;
  margin-bottom: 32px;
  color: #303133;
  font-size: 24px;
}
</style>
