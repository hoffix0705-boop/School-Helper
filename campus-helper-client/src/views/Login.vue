<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-left">
        <div class="left-content">
          <h1 class="welcome-title">校园帮</h1>
          <p class="welcome-subtitle">Campus Helper · 校园任务接单平台</p>
          <div class="features">
            <div class="feature-item"><el-icon><Check /></el-icon> 发布任务 · 快速找人帮忙</div>
            <div class="feature-item"><el-icon><Check /></el-icon> 接单赚钱 · 空闲时间变现</div>
            <div class="feature-item"><el-icon><Check /></el-icon> 分类管理 · 一站式校园服务</div>
          </div>
        </div>
      </div>
      <div class="login-form">
        <div class="form-header">
          <img class="form-logo" src="@/assets/images/logo.svg" alt="" />
          <h2>管理后台</h2>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" class="login-btn" @click="handleLogin">
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
        <div class="form-footer">
          <span>还没有账号？<a @click="showRegister = true" class="link">立即注册</a></span>
        </div>
      </div>
    </div>

    <!-- 注册弹窗 -->
    <el-dialog v-model="showRegister" title="注册账号" width="420px" :close-on-click-modal="false">
      <el-form ref="regFormRef" :model="regForm" :rules="regRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="regForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="regForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="regForm.confirmPassword" type="password" placeholder="请确认密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegister = false">取消</el-button>
        <el-button type="primary" :loading="regLoading" @click="handleRegister">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue"
import { useRouter } from "vue-router"
import { useUserStore } from "@/stores/user"
import { ElMessage } from "element-plus"
import { User, Lock, Check } from "@element-plus/icons-vue"
import { login as loginApi, register as registerApi } from "@/api/auth"

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const regFormRef = ref(null)
const loading = ref(false)
const regLoading = ref(false)
const showRegister = ref(false)

const form = reactive({ username: "", password: "" })
const regForm = reactive({ username: "", password: "", confirmPassword: "" })

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
}
const regRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }, { min: 6, message: "密码至少6位", trigger: "blur" }],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    { validator: (_, v, cb) => v === regForm.password ? cb() : cb(new Error("两次密码不一致")), trigger: "blur" }
  ]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await loginApi(form)
    userStore.setUserToken(res.token)
    userStore.setUserInfo(res.user)
    ElMessage.success("登录成功")
    router.push("/dashboard")
  } catch (e) {
    ElMessage.error(e.message || "登录失败")
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  const valid = await regFormRef.value.validate().catch(() => false)
  if (!valid) return
  regLoading.value = true
  try {
    await registerApi({ username: regForm.username, password: regForm.password })
    ElMessage.success("注册成功，请登录")
    showRegister.value = false
  } catch (e) {
    ElMessage.error(e.message || "注册失败")
  } finally {
    regLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  overflow: hidden;
  position: relative;
}
.login-container::before {
  content: "";
  position: absolute;
  width: 600px;
  height: 600px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(64,158,255,0.15) 0%, transparent 70%);
  top: -200px;
  right: -200px;
}
.login-container::after {
  content: "";
  position: absolute;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(99,123,254,0.1) 0%, transparent 70%);
  bottom: -100px;
  left: -100px;
}
.login-card {
  position: relative;
  display: flex;
  width: 880px;
  height: 520px;
  background: rgba(255,255,255,0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
  overflow: hidden;
  z-index: 1;
}
.login-left {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px;
  color: #fff;
}
.left-content { text-align: center; }
.welcome-title {
  font-size: 36px;
  font-weight: 800;
  margin-bottom: 8px;
  letter-spacing: 4px;
}
.welcome-subtitle {
  font-size: 14px;
  opacity: 0.85;
  margin-bottom: 40px;
}
.features { text-align: left; display: inline-block; }
.feature-item {
  margin: 16px 0;
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
  .el-icon { font-size: 18px; }
}
.login-form {
  width: 400px;
  padding: 40px 36px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.form-header {
  text-align: center;
  margin-bottom: 36px;
  .form-logo { width: 48px; height: 48px; margin-bottom: 10px; }
  h2 { font-size: 24px; font-weight: 700; color: #1a1a2e; margin: 0; }
}
.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--el-color-primary) 0%, #637bfe 100%);
  border: none;
}
.form-footer {
  text-align: center;
  font-size: 13px;
  color: #999;
  .link {
    color: var(--el-color-primary);
    cursor: pointer;
    &:hover { text-decoration: underline; }
  }
}
</style>


