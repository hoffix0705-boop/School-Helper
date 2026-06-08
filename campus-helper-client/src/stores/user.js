import { defineStore } from "pinia"
import { ref, computed } from "vue"
import { getToken, setToken, removeToken } from "@/utils/storage"

export const useUserStore = defineStore("user", () => {
  const token = ref(getToken())
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)

  function setUserToken(val) {
    token.value = val
    setToken(val)
  }

  function setUserInfo(info) {
    userInfo.value = info
  }

  function logout() {
    token.value = null
    userInfo.value = null
    removeToken()
  }

  return { token, userInfo, isLoggedIn, setUserToken, setUserInfo, logout }
})
