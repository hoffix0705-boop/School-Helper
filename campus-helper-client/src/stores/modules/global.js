import { defineStore } from "pinia"

export const useGlobalStore = defineStore("global", {
  state: () => ({
    layout: "vertical",
    assemblySize: "default",
    primary: "#409eff",
    isDark: false,
    isCollapse: false,
    accordion: true,
    breadcrumb: true,
    tabs: false,
    footer: false
  }),
  actions: {
    setGlobalState(key, value) {
      this[key] = value
    }
  },
  persist: false
})
