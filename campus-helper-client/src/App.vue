<script setup>
import { useUserStore } from "@/stores/user"
import { useAppStore } from "@/stores/app"
</script>

<template>
  <router-view />
</template>

<style>
html, body, #app { margin: 0; padding: 0; height: 100%; }
</style>
