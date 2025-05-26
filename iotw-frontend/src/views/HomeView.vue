<script setup lang="ts">
import {ref} from 'vue'

const getImageUrl = (user) => {
  return new URL(`../assets/images/${user}.jpg`, import.meta.url).href;
}

const tableData = ref([
  {
    name: "手表",
    todayBuy: 100,
    monthlyBuy: 200,
    totalBuy: 300,
  },
  {
    name: "RTX5090",
    todayBuy: 100,
    monthlyBuy: 200,
    totalBuy: 300,
  }
])

const tableLabel = ref({
  name: "货物",
  todayBuy: "今天购物",
  monthlyBuy: "本月购买",
  totalBuy: "总购买",
})

</script>

<template>
  <div class="home-container">
    <h2>首页</h2>
    <div class="content">
      <p>欢迎来到IOTW系统</p>
    </div>
  </div>
  <el-row class="home" :gutter="20">
    <el-col :span="8" style="margin-top: 20px">
      <el-card shadow="hover">
        <div class="user">
          <img :src="getImageUrl('avatar')" class="user" alt="Description of image"/>
          <div class="user-info">
            <p class="user-info-name">Admin</p>
            <p class="user-info-rule">超级管理员</p>
          </div>
        </div>
        <div class="login-info">
          <p>上次登录时间: <span>2025-5-26</span></p>
          <p>上次登录地点: <span>北京</span></p>
        </div>
      </el-card>


        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped lang="less">
.home {
  height: 100%;
  overflow: hidden;

  .user {
    display: flex;
    align-items: center;
    border-bottom: 1px solid var(--el-border-color-hover);
    margin-bottom: 20px;
    gap: 10px;

    img {
      width: 150px;
      height: 150px;
      border-radius: 50%;
      margin-right: 20px;
    }

    .user-info {
      flex: 1;
      min-width: 200px;

      .user-info-rule {
        color: var(--el-text-color-secondary);
      }

      .user-info-name {
        font-size: 24px;
        color: var(--el-text-color-primary);
      }
    }
  }

  .login-info {
    p {
      line-height: 30px;
      font-size: 14px;
      color: var(--el-text-color-secondary);

      span {
        color: var(--el-text-color-primary);
        margin-left: 60px;
      }
    }
  }
}
</style> 