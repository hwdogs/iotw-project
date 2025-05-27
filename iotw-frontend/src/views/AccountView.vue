<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import {get} from '@/net'

const tableData = ref([
  {
    name: '2016-05-03',
    role: 1,
    birth: 'California',
    sexLabel: 1,
    address: 'No. 189, Grove St, Los Angeles',
  },
  {
    name: '2016-05-03',
    role: 2,
    birth: 'California',
    sexLabel: 1,
    address: 'No. 189, Grove St, Los Angeles',
  },
  {
    name: '2016-05-03',
    role: 1,
    birth: 'California',
    sexLabel: 2,
    address: 'No. 189, Grove St, Los Angeles',
  }
])

const tableLabel = reactive([
  {
    prop: 'name',
    label: '名字',
  },
  {
    prop: 'role',
    label: '角色',
  },
  {
    prop: 'birth',
    label: '出生日期',
  },
  {
    prop: 'sexLabel',
    label: '性别',
  },
  {
    prop: 'address',
    label: '地址',
    width: '50px',
  }

])

const handleClick = () => {
  console.log('click')
}

const getAccountData = () => {
  get('/api/account/getAccountData', (responseData) => {
    if (responseData) {
      tableData.value = responseData.data.list.map((item) => ({
        ...item,
        role: item.role === 1 ? '用户' : '供应链经理',
        sexLabel: item.sex === 1 ? '男' : '女',
      }));
    }
  })
}
// onMounted(() => {
//   getAccountData()
// })

</script>

<template>
  <div class="user-header">
    <el-button type="primary" plain>新增</el-button>
    <el-form :inline="true">
      <el-form-item label="请输入">
        <el-input placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" plain>搜索</el-button>
      </el-form-item>
    </el-form>
  </div>
  <div class="table">
    <el-table :data="tableData" style="width: 100%">
      <el-table-column
          v-for="item in tableLabel"
          :key="item.prop"
          :width="item.width ? item.with : 125"
          :prop="item.prop"
          :label="item.label"
      />
      <el-table-column fixed="right" label="Operations" min-width="120">
        <template #default>
          <el-button type="primary" size="small" plain @click="handleClick">
            编辑
          </el-button>
          <el-button type="danger" size="small" plain>删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style scoped lang="less">
.user-header {
  display: flex;
  justify-content: space-between;
  padding: 20px;

  h2 {
    margin-bottom: 20px;
    color: var(--el-text-color-primary);
  }

  .content {
    background-color: var(--el-bg-color);
    padding: 20px;
    border-radius: 4px;
    box-shadow: var(--el-box-shadow-light);
  }
}
</style> 