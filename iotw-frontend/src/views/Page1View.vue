<script setup lang="ts">
// Page 1 component
</script>

<template>
  <div class="page1-container">
    <h2>页面1</h2>
    <div class="content">
      <p>页面1内容</p>
    </div>
  </div>
</template>

<style scoped lang="less">
.page1-container {
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