<template>
  <div class="error-container">
    <el-result icon="error" title="页面加载失败" sub-title="抱歉，页面加载过程中出现错误">
      <template #extra>
        <el-button type="primary" @click="handleRetry">重试</el-button>
        <el-button @click="handleBack">返回</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const handleRetry = () => {
  window.location.reload()
}

const handleBack = () => {
  router.back()
}
</script>

<style lang="scss" scoped>
.error-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f0f2f5;
}
</style>