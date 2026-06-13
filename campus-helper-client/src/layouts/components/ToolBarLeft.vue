<template>
  <div class="tool-bar-lf">
    <CollapseIcon />
    <Breadcrumb />
  </div>
</template>
<script setup>
import CollapseIcon from "./CollapseIcon.vue"
import Breadcrumb from "./Breadcrumb.vue"
</script>
<style scoped>
.tool-bar-lf { display: flex; align-items: center; overflow: hidden; white-space: nowrap; }
</style>
