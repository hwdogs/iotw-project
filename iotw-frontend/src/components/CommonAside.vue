<script setup lang="ts">
import {ref, computed} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import {useAllDataStore} from '@/stores'
import {markRaw} from 'vue'
import type {Component} from 'vue'
import {
  House,
  Box,
  User,
  More,
  Avatar,
  UserFilled,
  School,
  ShoppingTrolley,
  Sell,
  HomeFilled,
  Management,
  Files,
} from '@element-plus/icons-vue'

// 类型定义
interface MenuItem {
  path: string;
  name: string;
  label: string;
  icon: Component;
  url?: string;
  children?: MenuItem[];
}

const router = useRouter();
const route = useRoute();

const list = ref<MenuItem[]>([
  {
    path: '/index/home',
    name: 'home',
    label: '首页',
    icon: markRaw(House),
    url: 'Home',
  },
  {
    path: '/index/goods',
    name: 'goods',
    label: '商品管理',
    icon: markRaw(Box),
    children: [
      {
        path: '/index/good',
        name: 'good',
        label: '商品列表',
        icon: markRaw(Box),
        url: 'Good',
      },
      {
        path: '/index/supply',
        name: 'supply',
        label: '供应管理',
        icon: markRaw(Sell),
        url: 'Supply',
      },
      {
        path: '/index/sell',
        name: 'sell',
        label: '销售管理',
        icon: markRaw(ShoppingTrolley),
        url: 'Sell',
      }
    ]
  },
  {
    path: '/index/warehouses',
    name: 'warehouses',
    label: '仓库管理',
    icon: markRaw(School),
    children: [
      {
        path: '/index/warehouse',
        name: 'warehouse',
        label: '仓库列表',
        icon: markRaw(School),
        url: 'Warehouse',
      },
      {
        path: '/index/manage',
        name: 'manage',
        label: '仓库管理',
        icon: markRaw(HomeFilled),
        url: 'Manage',
      }
    ]
  },
  {
    path: '/index/user',
    name: 'user',
    label: '人员管理',
    icon: markRaw(User),
    children: [
      {
        path: '/index/insider',
        name: 'insider',
        label: '内部人员',
        icon: markRaw(Avatar),
        url: 'Insider',
      },
      {
        path: '/index/supplier',
        name: 'supplier',
        label: '供应商',
        icon: markRaw(UserFilled),
        url: 'Supplier',
      },
      {
        path: '/index/customer',
        name: 'customer',
        label: '顾客人员',
        icon: markRaw(User),
        url: 'Customer'
      },
    ]
  },
  {
    path: '/index/other',
    name: 'other',
    label: '其他',
    icon: markRaw(More),
    children: [
      {
        path: '/index/category',
        name: 'category',
        label: '类别列表',
        icon: markRaw(Files),
        url: 'Category',
      },
      {
        path: '/index/categoryManage',
        name: 'categoryManage',
        label: '类别管理',
        icon: markRaw(Management),
        url: 'CategoryManage',
      }
    ]
  }
])

const noChildren = computed(() =>
    list.value.filter(item => !item.children))

const hasChildren = computed(() =>
    list.value.filter(item => item.children))

const clickMenu = (item: MenuItem) => {
  router.push(item.path)
  store.selectMenu(item)
}

const store = useAllDataStore();

const isCollapse = computed(() => store.state.isCollapse)
// width
const width = computed(() => store.state.isCollapse ? '64px' : '192px')

const activeMenu = computed(() => route.path)

</script>

<template>
  <el-aside :width="width">
    <el-menu
        :collapse="isCollapse"
        :collapse-transition="false"
        :default-active="activeMenu"
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