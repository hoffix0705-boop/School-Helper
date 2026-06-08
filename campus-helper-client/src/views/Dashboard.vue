<script setup>
</script>

<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-num">0</div>
            <div class="stat-label">进行中的任务</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-num">0</div>
            <div class="stat-label">待审核</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-num">0</div>
            <div class="stat-label">注册用户</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-item">
            <div class="stat-num">0</div>
            <div class="stat-label">完成任务</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-card style="margin-top: 20px">
      <template #header>欢迎回来</template>
      <p>校园帮管理控制台，在这里你可以管理用户、任务和系统配置。</p>
    </el-card>
  </div>
</template>

<style scoped>
.stat-item { text-align: center; padding: 10px 0; }
.stat-num { font-size: 32px; font-weight: bold; color: #409eff; }
.stat-label { margin-top: 8px; color: #909399; font-size: 14px; }
</style>
