<script setup lang="ts">
import CommonAside from "@/components/CommonAside.vue";
import CommonHeader from "@/components/CommonHeader.vue";
import CommonTag from "@/components/CommonTag.vue";

</script>

<template>
  <div class="common-layout">
    <el-container class="lay-container">
      <!-- 自定义左侧组件-->
      <common-aside/>
      <el-container>
        <el-header class="el-header">
          <!--自定义头部组件-->
          <common-header/>
        </el-header>
        <common-tag/>
        <el-main class="right-main">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>

  </div>

</template>

<style scoped lang="less">
.common-layout, .launch-layout {
  height: 100%;
}

.el-header {
  background-color: var(--el-header-bg);
}

.lay-container {
  height: 100vh;
  display: flex;
}

.right-main {
  padding: 20px;
  position: relative;
}
</style>