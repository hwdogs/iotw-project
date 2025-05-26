<script setup lang="ts">
import {ref, computed} from 'vue';
import {useRouter} from 'vue-router';
import {
  House,
  VideoPlay,
  User,
  Location,
  Setting
} from '@element-plus/icons-vue'

const router = useRouter();

const list = ref([
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
    icon: VideoPlay,
    url: 'Mall',
  },
  {
    path: '/index/user',
    name: 'user',
    label: '用户管理',
    icon: User,
    url: 'User',
  },
  {
    path: '/index/other',
    name: 'other',
    label: '其他',
    icon: Location,
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
const noChildren = computed(() => list.value.filter(item => !item.children))
const hasChildren = computed(() => list.value.filter(item => item.children))

const clickMenu = (item) => {
  router.push(item.path)
}
</script>

<template>
  <el-aside width="180px" class="border-right">
    <el-menu
    :collapse="false"
    >
      <h3>IOTW</h3>
      <el-menu-item
          v-for="item in noChildren"
          :index="item.path"
          :key="item.path"
          @click="clickMenu(item)"
      >
        <el-icon>
          <component :is="item.icon" />
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
            <component :is="item.icon" />
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
              <component :is="subItem.icon" />
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