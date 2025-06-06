<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import {post, get} from '@/net'
import {ElMessage, ElMessageBox} from "element-plus";
import type {FormInstance} from 'element-plus'
import {Search, RefreshLeft, Plus} from '@element-plus/icons-vue'
import router from '@/router'

//类型定义
interface Warehouse {
  warehouseId: number
  warehouseName: string
  area: number
  description: string
  createTime: string
  updateTime: string
  accountIds: number[]
}

// 添加列配置类型
interface TableColumnConfig {
  prop: keyof Warehouse | string
  label: string
  width?: string | number
  showOverflowTooltip?: boolean
}

//响应式数据
const tableData = ref<Warehouse[]>([])
const loading = ref<boolean>(false);
const totalSearch = ref(0);
const formRef = ref<FormInstance>();

const tableLabel = reactive<TableColumnConfig[]>([
  {prop: 'warehouseId', label: '仓库ID'},
  {prop: 'warehouseName', label: '仓库名称'},
  {prop: 'accountIds', label: '仓库管理者'},
  {prop: 'area', label: '面积(平方米)'},
  {prop: 'description', label: '描述', showOverflowTooltip: true},
  {prop: 'createTime', label: '创建时间', showOverflowTooltip: true},
  {prop: 'updateTime', label: '更新时间', showOverflowTooltip: true}
])

const SORT_OPTIONS = [
  {value: 'warehouseId', label: '默认ID排序'},
  {value: 'area', label: '面积'},
  {value: 'createTime', label: '创建时间'},
  {value: 'updateTime', label: '更新时间'}
]

const conditionForm = reactive({
  pageNum: 1,
  pageSize: 10,
  warehouseName: '',
  startArea: null as number | null,
  endArea: null as number | null,
  startCreateTime: '',
  endCreateTime: '',
  startUpdateTime: '',
  endUpdateTime: '',
  sortField: 'warehouseId',
  sortAsc: false,
})

// 添加表单引用类型
const queryForm = ref<FormInstance>()

const getWarehouseData = () => {
  try {
    loading.value = true
    // 创建一个新对象来存储过滤后的数据
    const filteredData: {
      pageNum: number;
      pageSize: number;
      sortField: string;
      sortAsc: boolean;
      warehouseName?: string;
      startArea?: number;
      endArea?: number;
      startCreateTime?: string;
      endCreateTime?: string;
      startUpdateTime?: string;
      endUpdateTime?: string;
    } = {
      pageNum: conditionForm.pageNum,
      pageSize: conditionForm.pageSize,
      sortField: conditionForm.sortField,
      sortAsc: conditionForm.sortAsc
    }

    // 只添加有值的条件
    if (conditionForm.warehouseName) {
      filteredData.warehouseName = conditionForm.warehouseName
    }
    if (conditionForm.startArea !== null) {
      filteredData.startArea = conditionForm.startArea
    }
    if (conditionForm.endArea !== null) {
      filteredData.endArea = conditionForm.endArea
    }
    if (conditionForm.startCreateTime) {
      filteredData.startCreateTime = conditionForm.startCreateTime
    }
    if (conditionForm.endCreateTime) {
      filteredData.endCreateTime = conditionForm.endCreateTime
    }
    if (conditionForm.startUpdateTime) {
      filteredData.startUpdateTime = conditionForm.startUpdateTime
    }
    if (conditionForm.endUpdateTime) {
      filteredData.endUpdateTime = conditionForm.endUpdateTime
    }

    post('/api/warehouse/query', filteredData, (res) => {
      if (res) {
        const {records, total} = res;
        tableData.value = records
        totalSearch.value = total
      }
    }, (err) => {
      console.log(err)
      ElMessage.error('仓库数据加载失败' + err)
    })
  } catch (error) {
    ElMessage.error('仓库数据加载失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handlePageChange = (page: number) => {
  conditionForm.pageNum = page
  getWarehouseData()
}

const handleSizeChange = (size: number) => {
  conditionForm.pageSize = size
  getWarehouseData()
}

// 搜索重置
const resetQuery = (formEl: FormInstance | undefined) => {
  formEl?.resetFields()
  if (!formEl) return
  formEl.resetFields()
  conditionForm.pageNum = 1
  getWarehouseData()
}

const handleAdd = () => {
  router.push({
    name: 'warehouse_add_or_update',
    query: { type: 'add' }
  })
}

const handleEdit = (warehouse: Warehouse) => {
  router.push({
    name: 'warehouse_add_or_update',
    query: { 
      type: 'edit',
      id: warehouse.warehouseId
    }
  })
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定要删除这个仓库吗？此操作不可撤销。', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    get(`/api/warehouse/delete?id=${id}`, () => {
      ElMessage.success('仓库删除成功')
      getWarehouseData()
    }, (message) => {
      ElMessage.error('删除失败: ' + message)
    })
  }).catch(() => {
    // 用户取消操作
  })
}


onMounted(() => {
  getWarehouseData()
})
</script>

<template>
  <div class="warehouse-management" style="width: auto;">
    <!--搜索表单-->
    <el-form
        ref="queryForm"
        :inline="true"
        :model="conditionForm"
        class="search-form">
      <el-form-item label="仓库名称" prop="warehouseName">
        <el-input
            v-model="conditionForm.warehouseName"
            placeholder="输入仓库名称"
            clearable
        />
      </el-form-item>

      <el-form-item label="面积范围">
        <el-input-number
            v-model="conditionForm.startArea"
            :min="0"
            placeholder="最小面积"
            style="width: 130px"
        />
        <span style="margin: 0 8px;">—</span>
        <el-input-number
            v-model="conditionForm.endArea"
            :min="0"
            placeholder="最大面积"
            style="width: 130px"
        />
      </el-form-item>

      <el-form-item label="创建时间">
        <el-date-picker
            v-model="conditionForm.startCreateTime"
            type="datetime"
            placeholder="开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
        />
        <span style="margin: 0 8px;">—</span>
        <el-date-picker
            v-model="conditionForm.endCreateTime"
            type="datetime"
            placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>

      <el-form-item label="更新时间">
        <el-date-picker
            v-model="conditionForm.startUpdateTime"
            type="datetime"
            placeholder="开始时间"
            value-format="YYYY-MM-DD HH:mm:ss"
        />
        <span style="margin: 0 8px;">—</span>
        <el-date-picker
            v-model="conditionForm.endUpdateTime"
            type="datetime"
            placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>

      <div>
        <el-form-item label="排序方式">
          <el-select
              v-model="conditionForm.sortField"
              placeholder="选择排序方式"
              style="width: 120px"
              @change="getWarehouseData"
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
                @click="conditionForm.sortAsc = true; getWarehouseData()"
            >
              升序
            </el-button>
            <el-button
                :type="!conditionForm.sortAsc ? 'primary' : ''"
                @click="conditionForm.sortAsc = false; getWarehouseData()"
            >
              降序
            </el-button>
          </el-button-group>
        </el-form-item>
      </div>

      <el-form-item>
        <el-button size="large" type="primary" :icon="Search" @click="getWarehouseData" round>搜索</el-button>
        <el-button size="large" :icon="RefreshLeft" @click="resetQuery(queryForm)" round>重置</el-button>
      </el-form-item>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" :icon="Plus" plain @click="handleAdd">新增仓库</el-button>
      </div>
    </el-form>

    <div class="table-container">
      <el-table
          :data="tableData"
          v-loading="loading"
          style="width: 100%"
          stripe
          border
      >
        <el-table-column
            v-for="col in tableLabel"
            :key="col.prop"
            :prop="col.prop"
            :label="col.label"
            :width="col.width"
            :show-overflow-tooltip="col.showOverflowTooltip ?? false"
        >

          <template #default="scope" v-if="col.prop === 'accountIds'">
            <div class="account-tags">
              <template v-if="scope.row.accountIds && scope.row.accountIds.length">
                <el-tag
                    v-for="(id, index) in scope.row.accountIds"
                    :key="index"
                    class="account-tag"
                    size="small"
                >
                  #{{ id }}
                </el-tag>
              </template>
              <span v-else class="empty-message">无管理者</span>
            </div>
          </template>

          <template #default="scope" v-else-if="col.prop === 'description'">
            <div class="description-cell">
              {{ scope.row.description || '无描述' }}
            </div>
          </template>

        </el-table-column>
        >

        <el-table-column label="操作" width="155" fixed="right">
          <template #default="{ row }">
            <el-button
                type="primary"
                size="default"
                text
                bg
                @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
                type="danger"
                size="default"
                text
                bg
                @click="handleDelete(row.goodId)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>

      </el-table>
    </div>

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
.warehouse-management {
  padding: 20px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: var(--el-box-shadow-light);
  width: 100%;

  .search-form {
    margin-bottom: 8px;

    .el-form-item {
      margin-right: 15px;
    }
  }

  .action-button {
    margin-bottom: 8px;
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

.account-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.account-tag {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  cursor: default;
}

.empty-message {
  color: var(--el-text-color-secondary);
  font-style: italic;
  font-size: 14px;
}

:deep(.el-table) {
  --el-table-border-color: var(--el-border-color);
  --el-table-header-bg-color: var(--el-bg-color);
  --el-table-row-hover-bg-color: var(--el-fill-color-light);
  --el-table-current-row-bg-color: var(--el-fill-color);
}
</style>