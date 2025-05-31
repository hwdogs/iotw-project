<script setup lang="ts">
import {computed, ref} from 'vue';
import {useRoute} from "vue-router";
import {useAllDataStore} from '@/stores'
import router from '@/router';

const route = useRoute();

const store = useAllDataStore();

const tags = computed(() => store.state.tags);

const handleMenu = (tag) => {
  router.push(tag.name)
  store.selectMenu(tag)
}

const handleClose = (tag: any, index: number) => {
  // 通过pinia管理
  store.updateTags(tag);
  // 如果点击关闭的tag不是对应的View 直接删除
  if (tag.name !== route.name) {
    return;
  }
  // 如果点击的是最后一个
  if (index === store.state.tags.length) {
    store.selectMenu(tags.value[index - 1])
    router.push(tags.value[index - 1].name) //路由到前一个
  } else {
    store.selectMenu(tags.value[index])
    router.push(tags.value[index].name)
  }
}
</script>

<template>
  <div class="tags">
    <el-tag
        v-for="(tag, index) in tags"
        :key="tag.name"
        :closable="tag.name != 'home'"
        :effect="route.name === tag.name? 'dark' : 'plain'"
        @click="handleMenu(tag)"
        @close="handleClose(tag, index)"
        class="tag-item"
    >
      {{ tag.label }}
    </el-tag>
  </div>
</template>

<style scoped lang="less">
.tags {
  margin: 3px 0 10px 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  margin-right: 8px;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px); /* 悬停效果 */
    box-shadow: 0 2px 5px rgba(0,0,0,0.1); /* 悬停阴影 */
  }
}
</style>