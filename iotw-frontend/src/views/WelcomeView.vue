<script setup lang="ts">

</script>

<template>
  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">
    <div style="flex: 1;background-color: black">
      <el-image style="width: 100%;height: 100%" fit="cover"
                src="https://pic.rmb.bdstatic.com/bjh/240418/news/7e1008037a60291a9611f91878855ef67469.jpeg"/>
    </div>
    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到物联网智能仓库管理平台</div>
      <div style="margin-top: 10px">您在这里可以实时追踪库存动态与环境参数，借助物联网设备实现精准高效的仓储监控</div>
      <div style="margin-top: 5px">您在这里可以预判市场需求趋势，利用数据驱动决策提升供应链响应速度与运营效益</div>
    </div>
    <div class="right-card">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<style scoped>
.welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}

.right-card {
  width: 400px;
  z-index: 1;
  background-color: white;
}
</style>