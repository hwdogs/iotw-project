<script setup lang="ts">
import {ref, onMounted} from 'vue'
import {get} from "@/net";
import {ElMessage} from "element-plus";

// 定义用户信息接口
interface AccountInfo {
  id: number
  name: string
  role: number
  birth: string
  sex: number
  email: string
  address: string
}

// 角色映射
const ROLE_MAP = {
  '-1': '超级管理员',
  '0': '管理员',
  '1': '普通用户',
  '2': '供应链经理'
}

// 性别映射
const SEX_MAP = {
  '0': '女',
  '1': '男'
}

const accountInfo = ref<AccountInfo | null>(null)
const loading = ref(false)

const getImageUrl = (user) => {
  return new URL(`../assets/images/${user}.jpg`, import.meta.url).href;
}

const tableData = ref([
  {
    name: "Garmin Forerrunner255",
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

// 获取用户信息
const getAccountInfo = () => {
  loading.value = true
  get('/api/account/info', (data) => {
    if (data) {
      accountInfo.value = data
    }
    loading.value = false
  }, (message) => {
    ElMessage.error(message);
    loading.value = false
  })
}



onMounted(() => {
  getAccountInfo()
})
</script>

<template>
  <el-row class="home" :gutter="20">
    <el-col :span="8" style="margin-top: 20px">
      <el-card shadow="hover" v-loading="loading">
        <div class="user">
          <img :src="getImageUrl('avatar')" class="user" alt="用户头像"/>
          <div class="user-info">
            <p class="user-info-name">{{ accountInfo?.name || '无' }}</p>
            <p class="user-info-rule">{{ accountInfo ? ROLE_MAP[accountInfo.role] : '无' }}</p>
          </div>
        </div>
        <div class="login-info">
          <p>用户ID: <span>{{ accountInfo?.id || '无' }}</span></p>
          <p>邮箱: <span>{{ accountInfo?.email || '无' }}</span></p>
          <p>性别: <span>{{ accountInfo ? SEX_MAP[accountInfo.sex] : '无' }}</span></p>
          <p>出生日期: <span>{{ accountInfo?.birth || '无' }}</span></p>
          <p>地址: <span>{{ accountInfo?.address || '无' }}</span></p>
        </div>
      </el-card>

      <el-card shadow="hover" class="user-table">
        <el-table :data="tableData">
          <el-table-column
              v-for="(val,key) in tableLabel"
              :key="key"
              :prop="String(key)"
              :label="val"
          >
          </el-table-column>
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped lang="less">
.home {
  height: 100%;
  overflow-y: auto;
  padding: 20px;

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
      display: flex;
      justify-content: space-between;
      align-items: center;

      span {
        color: var(--el-text-color-primary);
        text-align: justify;
        min-width: 200px;
      }
    }
  }
}

.user-table {
  margin-top: 20px;
}
</style> 