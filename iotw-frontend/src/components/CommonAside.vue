<script setup lang="ts">
import {ref, computed} from 'vue';
import {useRouter} from 'vue-router';
import {useAllDataStore} from '@/stores'
import {Options} from "element-plus";
import {
  House,
  Box,
  User,
  More,
  Setting
} from '@element-plus/icons-vue'

// 类型定义
interface MenuItem {
  path: string;
  name: string;
  label: string;
  icon: Options;
  url?: string;
  children?: MenuItem[];
}

const router = useRouter();

const list = ref<MenuItem[]>([
  {
    path: '/index/home',
    name: 'home',
    label: '首页',
    icon: House,
    url: 'Home',
  },
  {
    path: '/index/mall',
    name: 'mall',
    label: '商品管理',
    icon: Box,
    url: 'Mall',
  },
  {
    path: '/index/user',
    name: 'user',
    label: '账号管理',
    icon: User,
    url: 'User',
  },
  {
    path: '/index/other',
    name: 'other',
    label: '其他',
    icon: More,
    children: [
      {
        path: '/index/page1',
        name: 'page1',
        label: '页面1',
        icon: Setting,
        url: 'Page1',
      },
      {
        path: '/index/page2',
        name: 'page2',
        label: '页面2',
        icon: Setting,
        url: 'Page2',
      },
    ]
  }
])

const noChildren = computed(() =>
    list.value.filter(item => !item.children))

const hasChildren = computed(() =>
    list.value.filter(item => item.children))

const clickMenu = (item: MenuItem) => {
  router.push(item.path)
}

const store = useAllDataStore();

const isCollapse = computed(() => store.state.isCollapse)
// width
const width = computed(() => store.state.isCollapse ? '64px' : '192px')
</script>

<template>
  <el-aside :width="width">
    <el-menu
        :collapse="isCollapse"
        :collapse-transition="false"
    >
      <h3 v-show="!isCollapse" key="full">IOTW</h3>
      <h3 v-show="isCollapse" key="collapse">W</h3>
      <el-menu-item
          v-for="item in noChildren"
          :index="item.path"
          :key="item.path"
          @click="clickMenu(item)"
      >
        <el-icon>
          <component :is="item.icon"/>
        </el-icon>
        <span>{{ item.label }}</span>
      </el-menu-item>
      <el-sub-menu
          v-for="item in hasChildren"
          :index="item.path"
          :key="item.path"
      >
        <template #title>
          <el-icon>
            <component :is="item.icon"/>
          </el-icon>
          <span>{{ item.label }}</span>
        </template>
        <el-menu-item-group>
          <el-menu-item
              v-for="(subItem,subIndex) in item.children"
              :index="subItem.path"
              :key="subItem.path"
              @click="clickMenu(subItem)"
          >
            <el-icon>
              <component :is="subItem.icon"/>
            </el-icon>
            <span>{{ subItem.label }}</span>
          </el-menu-item>
        </el-menu-item-group>
      </el-sub-menu>
    </el-menu>
  </el-aside>
</template>

<style scoped lang="less">
.icons {
  width: 18px;
  height: 18px;
  margin-right: 5px;
}

.el-menu {
  h3 {
    line-height: 48px;
    text-align: center;
  }
}

.el-aside {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 强制菜单高度 */
::v-deep(.el-menu) {
  flex: 1; /* 撑满剩余空间 */
  overflow-y: auto; /* 允许滚动 */
}
</style>