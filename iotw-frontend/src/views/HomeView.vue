<script setup lang="ts">
// Home page component
</script>

<template>
  <div class="home-container">
    <h2>首页</h2>
    <div class="content">
      <p>欢迎来到IOTW系统</p>
    </div>
  </div>
</template>

<style scoped lang="less">
.home-container {
  padding: 20px;
  
  h2 {
    margin-bottom: 20px;
    color: var(--el-text-color-primary);
  }

  .content {
    background-color: var(--el-bg-color);
    padding: 20px;
    border-radius: 4px;
    box-shadow: var(--el-box-shadow-light);
  }
}
</style> 