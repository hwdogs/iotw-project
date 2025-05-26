<script setup lang="ts">
import {logout} from "@/net"
import router from "@/router"
import CommonAside from "@/components/CommonAside.vue";
import CommonHeader from "@/components/CommonHeader.vue";

function userLogout() {
  logout(() => router.push('/'))
}

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
        <el-main class="right-main">
          <router-view></router-view>
          <div class="logout-btn">
            <el-button @click="userLogout">退出登录</el-button>
          </div>
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

.logout-btn {
  position: absolute;
  top: 20px;
  right: 20px;
}
</style>