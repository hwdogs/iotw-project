<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import {post, get} from '@/net'
import {ElMessage, ElMessageBox} from "element-plus";
import type {FormInstance} from 'element-plus'
import {Plus, Search, RefreshLeft} from '@element-plus/icons-vue'
import router from '@/router';


//类型定义
interface Account {
  id: string
  name: string
  role: number
  sex: number
  birth: string
  email: string
  address: string
}

interface QueryParams {
  pageNum: number
  pageSize: number
  username?: string
  role?: number | null
  sex?: number | null
  startBirth?: string
  endBirth?: string
  sortField?: 'birth' | 'role' | 'register_time' | 'id'
  sortAsc?: boolean

}

// 添加列配置类型
interface TableColumnConfig {
  prop: keyof Account | string
  label: string
  width?: string | number
  showOverflowTooltip?: boolean
}

//响应式数据
const tableData = ref<Account[]>([])
const loading = ref<boolean>(false);
const totalSearch = ref(0);

const tableLabel = reactive<TableColumnConfig[]>([
  {prop: 'name', label: '用户名'},
  {prop: 'role', label: '角色'},
  {prop: 'birth', label: '出生日期'},
  {prop: 'sexLabel', label: '性别'},
  {prop: 'email', label: '邮箱', showOverflowTooltip: true},
  {prop: 'address', label: '地址', showOverflowTooltip: true}
])

// 映射配置
const ROLE_OPTIONS = [
  {value: -1, label: '超级管理员'},
  {value: 0, label: '管理员'},
  {value: 1, label: '普通用户'},
  {value: 2, label: '供应链经理'}
]

const SEX_OPTIONS = [
  {value: 0, label: '女'},
  {value: 1, label: '男'}
]

const SORT_OPTIONS = [
  {value: 'id', label: '默认排序'},
  {value: 'birth', label: '出生日期'},
  {value: 'role', label: '用户角色'},
  {value: 'register_time', label: '注册时间'}
]

const conditionForm = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  role: null as number | null,
  sex: null as number | null,
  startBirth: '',
  endBirth: '',
  email: '',
  sortField: 'id',
  sortAsc: false,
})
// 添加表单引用类型
const queryForm = ref<FormInstance>()

const getAccountData = () => {
  try {
    loading.value = true
    post('/api/account/query', conditionForm, (res) => {
      if (res) {
        const {records, total} = res;

        tableData.value = records.map((item: Account) => ({
          ...item,
          role: ROLE_OPTIONS.find(r => r.value === item.role)?.label || '未知角色',
          sexLabel: SEX_OPTIONS.find(s => s.value === item.sex)?.label || '未知'
        }))

        totalSearch.value = total
      }
    }, (err) => {
      console.log(err)
      ElMessage.error(err)
    })
  } catch (error) {
    ElMessage.error('table数据加载失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handlePageChange = (page: number) => {
  conditionForm.pageNum = page
  getAccountData()
}

const handleSizeChange = (size: number) => {
  conditionForm.pageSize = size
  getAccountData()
}

// 搜索重置
const resetQuery = (formEl: FormInstance | undefined) => {
  formEl?.resetFields()
  if (!formEl) return
  formEl.resetFields()
  conditionForm.pageNum = 1
  getAccountData()
}

// 删除操作
const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认删除该用户？', '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    get(`/api/account/delete/${id}`, () => {
      ElMessage.success('删除成功')
      getAccountData()
    })
  }).catch(() => {
  })
}

const handleAlloc = (id: number) => {
  router.push({
    name: 'alloc-menus',
    params: {
      roleId: id,
    }
  })
}

onMounted(() => {
  getAccountData()
})
</script>

<template>
  <div class="user-management">
    <!--搜索表单-->
    <el-form
        ref="queryForm"
        :inline="true"
        :model="conditionForm"
        class="search-form">
      <el-form-item label="用户名" prop="username">
        <el-input
            v-model="conditionForm.username"
            placeholder="输入用户名"
            clearable
        />
      </el-form-item>

      <el-form-item label="角色" prop="role">
        <el-select
            v-model.number="conditionForm.role"
            placeholder="请选择角色"
            clearable
            value-key="value"
            style="width: 120px"
        >
          <el-option
              v-for="item in ROLE_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="性别" prop="sex">
        <el-select
            v-model.number="conditionForm.sex"
            placeholder="选择性别"
            clearable
            value-key="value"
            style="width: 110px;"
        >
          <el-option
              v-for="item in SEX_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="出生日期">
        <el-date-picker
            v-model="conditionForm.startBirth"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
        />
        <span style="margin: 0 8px;">—</span>
        <el-date-picker
            v-model="conditionForm.endBirth"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
        />
      </el-form-item>
      <div>
        <el-form-item label="排序方式">
          <el-select
              v-model="conditionForm.sortField"
              placeholder="选择排序方式"
              style="width: 120px"
              @change="getAccountData"
          >
            <el-option
                v-for="item in SORT_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <!-- 排序方向切换 -->
        <el-form-item label="排序方向">
          <el-button-group>
            <el-button
                :type="conditionForm.sortAsc ? 'primary' : ''"
                @click="conditionForm.sortAsc = true; getAccountData()"
            >
              升序
            </el-button>
            <el-button
                :type="!conditionForm.sortAsc ? 'primary' : ''"
                @click="conditionForm.sortAsc = false; getAccountData()"
            >
              降序
            </el-button>
          </el-button-group>
        </el-form-item>

      </div>

      <el-form-item>
        <el-button size="large" type="primary" :icon="Search" @click="getAccountData" round>搜索</el-button>
        <el-button size="large" :icon="RefreshLeft" @click="resetQuery(queryForm)" round>重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" :icon="Plus" plain>新增用户</el-button>
    </div>

    <!-- 数据表格 -->
    <el-table
        :data="tableData"
        v-loading="loading"
        style="width: 100%"
        stripe
        border
        highlight-current-row
    >
      <el-table-column
          v-for="col in tableLabel"
          :key="col.prop"
          :prop="col.prop"
          :label="col.label"
          :width="col.width"
          :show-overflow-tooltip="col.showOverflowTooltip ?? false"
      />

      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <el-button
              type="primary"
              size="default"
              text
              bg
          >
            编辑
          </el-button>
          <el-button
              type="danger"
              size="default"
              text
              bg
              @click="handleDelete(row.id)"
          >
            删除
          </el-button>
          <el-button
              type="warning"
              size="default"
              text
              bg
              @click="handleAlloc(row.id)"
          >
            分配权限
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        background
        class="pagination"
        :current-page="conditionForm.pageNum"
        :page-size="conditionForm.pageSize"
        :page-sizes="[10, 30, 50]"
        :total="totalSearch"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
    />

  </div>
</template>

<style scoped lang="less">
.user-management {
  padding: 20px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: var(--el-box-shadow-light);
  width: 100%;

  .search-form {
    margin-bottom: 20px;

    .el-form-item {
      margin-right: 15px;
    }
  }

  .action-button {
    margin-bottom: 15px;
  }

  .pagination {
    margin-top: 20px;
    justify-content: flex-end;
  }
}

.el-table {
  margin-top: 15px;
  width: 100% !important;

  :deep(.el-table__cell) {
    padding: 8px 0;
  }

  :deep(.el-table__body) {
    tr {
      height: 40px;
    }
  }
}

:deep(.el-table) {
  --el-table-border-color: var(--el-border-color);
  --el-table-header-bg-color: var(--el-bg-color);
  --el-table-row-hover-bg-color: var(--el-fill-color-light);
  --el-table-current-row-bg-color: var(--el-fill-color);
}
</style> 