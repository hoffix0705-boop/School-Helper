import { defineStore } from "pinia"
import { ref, computed } from "vue"
import { getToken, setToken, removeToken, getUser, setUser, removeToken as clearStorage } from "@/utils/storage"

export const useUserStore = defineStore("user", () => {
  const token = ref(getToken())
  const userInfo = ref(getUser())

  const isLoggedIn = computed(() => !!token.value)

  function setUserToken(val) {
    token.value = val
    setToken(val)
  }

  function setUserInfo(info) {
    userInfo.value = info
    setUser(info)
  }

  function logout() {
    token.value = null
    userInfo.value = null
    clearStorage()
  }

  return { token, userInfo, isLoggedIn, setUserToken, setUserInfo, logout }
})
