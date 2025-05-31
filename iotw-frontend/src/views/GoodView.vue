<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import {post, get} from '@/net'
import {ElMessage, ElMessageBox} from "element-plus";
import type {FormInstance} from 'element-plus'
import router from '@/router';
import {Plus, Search, RefreshLeft} from '@element-plus/icons-vue'

// 类型定义
interface Good {
  goodId: number
  goodName: string
  warehouseId: number
  category: number
  price: number
  standard: string
  description: string
  image: string
  createTime: string
}

interface Warehouse {
  warehouseId: number
  warehouseName: string

}

// 添加列配置类型
interface TableColumnConfig {
  prop: keyof Good | string
  label: string
  width?: string | number
  showOverflowTooltip?: boolean
}

// 响应式数据
const tableData = ref<Good[]>([])
const loading = ref<boolean>(false)
const totalSearch = ref(0)
const warehouseList = ref<Warehouse[]>([])

// 表格列配置
const tableLabel = reactive<TableColumnConfig[]>([
  {prop: 'goodName', label: '商品名称'},
  {prop: 'warehouseId', label: '仓库ID'},
  {prop: 'categoryLabel', label: '商品类别'},
  {prop: 'price', label: '价格'},
  {prop: 'standard', label: '规格'},
  {prop: 'description', label: '描述', showOverflowTooltip: true},
  {prop: 'createTime', label: '创建时间'}
])

// 映射配置
const CATEGORY_OPTIONS = [
  {value: 1, label: '农业产品'},
  {value: 2, label: '林业产品'},
  {value: 3, label: '饲养动物及其产品'},
  {value: 4, label: '渔业产品'},
  {value: 5, label: '3C产品'},
  {value: 6, label: '饮料、酒水等饮品'},
  {value: 7, label: '服饰、服装'}
]

const SORT_OPTIONS = [
  {value: 'good_id', label: '默认排序'},
  {value: 'create_time', label: '创建时间'},
  {value: 'warehouse_id', label: '仓库ID'},
  {value: 'category', label: '商品类别'},
  {value: 'price', label: '价格'}
]

// 搜索表单数据
const conditionForm = reactive({
  pageNum: 1,
  pageSize: 10,
  goodName: '',
  warehouseId: null as number | null,
  categoryId: null as number | null,
  startPrice: null as number | null,
  endPrice: null as number | null,
  startCreateTime: '',
  endCreateTime: '',
  sortField: 'create_time',
  sortAsc: false
})

// 表单引用
const queryForm = ref<FormInstance>()

// 获取商品数据
const getGoodData = () => {
  try {
    loading.value = true
    post('/api/good/query', conditionForm, (res) => {
      if (res) {
        const {records, total} = res
        tableData.value = records.map((item: Good) => ({
          ...item,
          categoryLabel: CATEGORY_OPTIONS.find(c => c.value === item.category)?.label || '未知类别'
        }))
        totalSearch.value = total
      }
    }, (err) => {
      console.log(err)
      ElMessage.error('数据加载失败' + err)
    })
  } catch (error) {
    ElMessage.error('数据加载失败')
  } finally {
    loading.value = false
  }
}

//仓库数据
const getWarehouseData = () => {

}

// 分页处理
const handlePageChange = (page: number) => {
  conditionForm.pageNum = page
  getGoodData()
}

const handleSizeChange = (size: number) => {
  conditionForm.pageSize = size
  getGoodData()
}

// 搜索重置
const resetQuery = (formEl: FormInstance | undefined) => {
  formEl?.resetFields()
  if (!formEl) return
  formEl.resetFields()
  conditionForm.pageNum = 1
  getGoodData()
}


// 添加商品操作
const handleAddOrEdit = (id: number | null) => {
  router.push({
    name: 'good-add-edit',
    params: {
      goodId: id || 'new'
    }
  })
}

// 删除商品操作
const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认删除该商品？', '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    get(`/api/good/delete?id=${id}`, () => {
      ElMessage.success('删除成功')
      getGoodData()
    }, (message) => {
      ElMessage.error(message)
    })
  }).catch(() => {
  })
}

onMounted(() => {
  getGoodData()
})
</script>

<template>
  <div class="good-management" style="width: auto;">
    <!-- 搜索表单 -->
    <el-form
        ref="queryForm"
        :inline="true"
        :model="conditionForm"
        class="search-form">
      <el-form-item label="商品名称" prop="goodName">
        <el-input
            v-model="conditionForm.goodName"
            placeholder="输入商品名称"
            clearable
        />
      </el-form-item>

      <el-form-item label="仓库ID" prop="warehouseId">
        <el-input-number
            v-model="conditionForm.warehouseId"
            placeholder="输入仓库ID"
            clearable
            :min="1"
        />
      </el-form-item>

      <el-form-item label="商品类别" prop="categoryId">
        <el-select
            v-model="conditionForm.categoryId"
            placeholder="请选择类别"
            clearable
            value-key="value"
            style="width: 120px"
        >
          <el-option
              v-for="item in CATEGORY_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="价格范围">
        <el-input-number
            v-model="conditionForm.startPrice"
            placeholder="最低价"
            :precision="2"
            :step="0.1"
            :min="0"
        />
        <span style="margin: 0 8px;">—</span>
        <el-input-number
            v-model="conditionForm.endPrice"
            placeholder="最高价"
            :precision="2"
            :step="0.1"
            :min="0"
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

      <div>
        <el-form-item label="排序方式">
          <el-select
              v-model="conditionForm.sortField"
              placeholder="选择排序方式"
              style="width: 120px"
              @change="getGoodData"
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
                @click="conditionForm.sortAsc = true; getGoodData()"
            >
              升序
            </el-button>
            <el-button
                :type="!conditionForm.sortAsc ? 'primary' : ''"
                @click="conditionForm.sortAsc = false; getGoodData()"
            >
              降序
            </el-button>
          </el-button-group>
        </el-form-item>
      </div>

      <el-form-item>
        <el-button size="large" type="primary" :icon="Search" @click="getGoodData" round>搜索</el-button>
        <el-button size="large" :icon="RefreshLeft" @click="resetQuery(queryForm)" round>重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" :icon="Plus" plain @click="handleAddOrEdit(null)">新增商品</el-button>
    </div>

    <!-- 数据表格 -->
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
        />

        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
                type="primary"
                size="default"
                text
                bg
                @click="handleAddOrEdit(row.goodId)"
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
.good-management {
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

:deep(.el-table) {
  --el-table-border-color: var(--el-border-color);
  --el-table-header-bg-color: var(--el-bg-color);
  --el-table-row-hover-bg-color: var(--el-fill-color-light);
  --el-table-current-row-bg-color: var(--el-fill-color);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 