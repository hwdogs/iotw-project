<script setup lang="ts">
import {Menu} from '@element-plus/icons-vue'
import {useAllDataStore} from '@/stores'
import {logout} from '@/net'
import router from '@/router'
import {ElMessageBox} from "element-plus";
import {computed} from "vue";

function userLogout() {
  ElMessageBox.confirm('您确定要退出登录吗?').then((result) => {
    if (result) {
      logout(() => router.push('/'))
    }
  })
}

const getImageUrl = (user) => {
  return new URL(`../assets/images/${user}.jpg`, import.meta.url).href;
}

const store = useAllDataStore();

const handleCollapse = () => {
  store.state.isCollapse = !store.state.isCollapse
}

const current = computed(() => store.state.currentMenu)
</script>

<template>
  <div class="header">
    <div class="l-content">
      <el-button size="default" :icon="Menu" @click="handleCollapse">
      </el-button>
      <el-breadcrumb separator="/" class="bread">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item v-if="current" :to="current.path">{{ current.label }}</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="r-content">
      <el-dropdown>
        <span class="el-dropdown-link">
<!--          <img :src="getImageUrl('avatar')" class="user" alt=""/>-->
          <el-avatar
              src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
          />
         </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item>个人中心</el-dropdown-item>
            <el-dropdown-item @click="userLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped lang="less">
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: var(--el-bg-color);
}

.icons {
  width: 20px;
  height: 20px;
}

.l-content {
  display: flex;
  align-items: center;

  .el-button {
    margin-right: 10px;
  }
}

.r-content {
  .user {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }
}

:deep(.bread span) {
  cursor: pointer !important;
}
</style>