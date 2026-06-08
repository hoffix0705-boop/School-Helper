import axios from "axios"
import { ElMessage } from "element-plus"
import { useUserStore } from "@/stores/user"
import router from "@/router"

const request = axios.create({
  baseURL: "/api",
  timeout: 15000,
})

request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers.token = userStore.token
  }
  return config
})

request.interceptors.response.use(
  (res) => {
    const { code, msg, data } = res.data
    if (code === 200) return data
    ElMessage.error(msg || "请求失败")
    if (code === 401) {
      useUserStore().logout()
      router.push("/login")
    }
    return Promise.reject(new Error(msg))
  },
  (err) => {
    if (err.response?.status === 401) {
      useUserStore().logout()
      router.push("/login")
    }
    ElMessage.error(err.message || "网络异常")
    return Promise.reject(err)
  },
)

export default request
