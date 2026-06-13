import { defineStore } from "pinia"

export const useKeepAliveStore = defineStore("keepAlive", {
  state: () => ({
    keepAliveName: []
  }),
  actions: {
    addKeepAliveName(name) {
      if (!this.keepAliveName.includes(name)) {
        this.keepAliveName.push(name)
      }
    },
    removeKeepAliveName(name) {
      this.keepAliveName = this.keepAliveName.filter(n => n !== name)
    }
  }
})
