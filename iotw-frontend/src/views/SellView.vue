<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Plus, Edit, Delete, RefreshLeft } from '@element-plus/icons-vue'
import { post, get } from '@/net'

// 类型定义
interface Sell {
  sellId: number
  customerId: number
  goodId: number
  sellNumber: number
  createTime: string
  updateTime: string
}

// 添加列配置类型
interface TableColumnConfig {
  prop: keyof Sell | string
  label: string
  width?: string | number
  showOverflowTooltip?: boolean
}

// 响应式数据
const tableData = ref<Sell[]>([])
const loading = ref(false)
const total = ref(0)

// 添加顾客和商品的远程搜索数据
const customerOptions = ref<{ id: number, name: string }[]>([])
const goodOptions = ref<{ id: number, name: string }[]>([])
const customerLoading = ref(false)
const goodLoading = ref(false)

// 添加搜索关键词
const customerSearchKeyword = ref('')
const goodSearchKeyword = ref('')

const tableLabel = reactive<TableColumnConfig[]>([
  { prop: 'sellId', label: '出库ID', width: '95' },
  { prop: 'customerId', label: '顾客ID' },
  { prop: 'goodId', label: '商品ID' },
  { prop: 'sellNumber', label: '出库数量' },
  { prop: 'createTime', label: '出库时间' },
  { prop: 'updateTime', label: '更新时间' }
])

const SORT_OPTIONS = [
  { value: 'update_time', label: '默认排序' },
  { value: 'create_time', label: '出库时间' },
  { value: 'sell_id', label: '出库ID' }
]

// 查询表单数据
const queryForm = ref<FormInstance>()
const conditionForm = reactive({
  pageNum: 1,
  pageSize: 10,
  sellId: undefined,
  customerId: undefined,
  goodId: undefined,
  startSellNumber: undefined,
  endSellNumber: undefined,
  startCreateTime: undefined,
  endCreateTime: undefined,
  startUpdateTime: undefined,
  endUpdateTime: undefined,
  sortField: 'update_time',
  sortAsc: false
})

// 查询列表数据
const queryList = async () => {
  loading.value = true
  // 创建请求数据的副本
  const requestData = { ...conditionForm }

  post('/api/sell/query', requestData,
    (data) => {
      tableData.value = data.records
      total.value = data.total
      loading.value = false
    },
    (message) => {
      ElMessage.error(message || '查询失败')
      loading.value = false
    }
  )
}

// 重置查询
const resetQuery = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  
  // 重置表单字段
  formEl.resetFields()
  
  // 手动重置所有条件字段
  Object.assign(conditionForm, {
    pageNum: 1,
    pageSize: 10,
    sellId: undefined,
    customerId: undefined,
    goodId: undefined,
    startSellNumber: undefined,
    endSellNumber: undefined,
    startCreateTime: undefined,
    endCreateTime: undefined,
    startUpdateTime: undefined,
    endUpdateTime: undefined,
    sortField: 'create_time',
    sortAsc: false
  })
  
  // 重新获取数据
  queryList()
}

// 分页处理
const handlePageChange = (page: number) => {
  conditionForm.pageNum = page
  queryList()
}

const handleSizeChange = (size: number) => {
  conditionForm.pageSize = size
  queryList()
}

// 对话框控制
const dialogVisible = ref(false)
const dialogTitle = ref('')
const dialogType = ref<'add' | 'update'>('add')

// 表单数据
const formRef = ref<FormInstance>()
const form = ref({
  sellId: undefined,
  customerId: undefined,
  goodId: undefined,
  sellNumber: undefined
})

// 表单验证规则
const rules = ref<FormRules>({
  customerId: [{ required: true, message: '请选择顾客', trigger: 'change' }],
  goodId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  sellNumber: [{ required: true, message: '请输入出库数量', trigger: 'blur' }]
})

// 远程搜索顾客
const remoteCustomerSearch = () => {
  if (customerSearchKeyword.value) {
    try {
      customerLoading.value = true
      // 判断输入是否为数字
      const isNumber = !isNaN(Number(customerSearchKeyword.value))
      post('/api/customer/query', {
        pageNum: 1,
        pageSize: 10,
        // 如果是数字，则按ID搜索，否则按名称搜索
        ...(isNumber ? { customerId: Number(customerSearchKeyword.value) } : { name: customerSearchKeyword.value })
      }, (res) => {
        if (res && res.records) {
          customerOptions.value = res.records.map((item: any) => ({
            id: item.customerId,
            name: item.username
          }))
        }
      }, (err) => {
        ElMessage.error('获取顾客列表失败' + err)
      })
    } catch (error) {
      ElMessage.error('获取顾客列表失败' + error)
    } finally {
      customerLoading.value = false
    }
  } else {
    customerOptions.value = []
  }
}

// 远程搜索商品
const remoteGoodSearch = () => {
  if (goodSearchKeyword.value) {
    try {
      goodLoading.value = true
      // 判断输入是否为数字
      const isNumber = !isNaN(Number(goodSearchKeyword.value))
      post('/api/good/query', {
        pageNum: 1,
        pageSize: 10,
        // 如果是数字，则按ID搜索，否则按名称搜索
        ...(isNumber ? { goodId: Number(goodSearchKeyword.value) } : { goodName: goodSearchKeyword.value })
      }, (res) => {
        if (res && res.records) {
          goodOptions.value = res.records.map((item: any) => ({
            id: item.goodId,
            name: item.goodName
          }))
        }
      }, (err) => {
        ElMessage.error('获取商品列表失败' + err)
      })
    } catch (error) {
      ElMessage.error('获取商品列表失败' + error)
    } finally {
      goodLoading.value = false
    }
  } else {
    goodOptions.value = []
  }
}

// 打开添加对话框
const handleAdd = () => {
  dialogType.value = 'add'
  dialogTitle.value = '添加出库记录'
  form.value = {
    sellId: undefined,
    customerId: undefined,
    goodId: undefined,
    sellNumber: undefined
  }
  // 清空搜索关键词和选项
  customerSearchKeyword.value = ''
  goodSearchKeyword.value = ''
  customerOptions.value = []
  goodOptions.value = []
  dialogVisible.value = true
}

// 打开编辑对话框
const handleEdit = (row: Sell) => {
  dialogType.value = 'update'
  dialogTitle.value = '编辑出库记录'
  form.value = { ...row }
  // 清空搜索关键词和选项
  customerSearchKeyword.value = ''
  goodSearchKeyword.value = ''
  customerOptions.value = []
  goodOptions.value = []
  dialogVisible.value = true
}

// 删除记录
const handleDelete = async (row: Sell) => {
  try {
    await ElMessageBox.confirm('确认删除该出库记录吗？', '提示', {
      type: 'warning'
    })
    get(`/api/sell/delete?id=${row.sellId}`,
      () => {
        ElMessage.success('删除成功')
        queryList()
      },
      (message) => {
        ElMessage.error(message || '删除失败')
      }
    )
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      const url = dialogType.value === 'add' ? '/api/sell/add' : '/api/sell/update'
      post(url, form.value,
        () => {
          ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
          dialogVisible.value = false
          queryList()
        },
        (message) => {
          ElMessage.error(message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
        }
      )
    }
  })
}

// 关闭对话框时重置表单
const handleClose = () => {
  formRef.value?.resetFields()
  form.value = {
    sellId: undefined,
    customerId: undefined,
    goodId: undefined,
    sellNumber: undefined
  }
  // 清空搜索关键词和选项
  customerSearchKeyword.value = ''
  goodSearchKeyword.value = ''
  customerOptions.value = []
  goodOptions.value = []
}

onMounted(() => {
  queryList()
})
</script>

<template>
  <div class="sell-management" style="width: auto;">
    <!-- 搜索表单 -->
    <el-form
      ref="queryForm"
      :inline="true"
      :model="conditionForm"
      class="search-form"
    >
      <!-- 第一行：基本信息 -->
      <div class="form-row">
        <el-form-item label="出库ID" prop="sellId">
          <el-input
            v-model="conditionForm.sellId"
            placeholder="输入出库ID"
            clearable
          />
        </el-form-item>

        <el-form-item label="顾客ID" prop="customerId">
          <el-input
            v-model="conditionForm.customerId"
            placeholder="输入顾客ID"
            clearable
          />
        </el-form-item>

        <el-form-item label="商品ID" prop="goodId">
          <el-input
            v-model="conditionForm.goodId"
            placeholder="输入商品ID"
            clearable
          />
        </el-form-item>

        <el-form-item label="出库数量">
          <el-input-number
            v-model="conditionForm.startSellNumber"
            placeholder="最小值"
          />
          <span style="margin: 0 8px;">—</span>
          <el-input-number
            v-model="conditionForm.endSellNumber"
            placeholder="最大值"
          />
        </el-form-item>
      </div>

      <!-- 第二行：时间相关 -->
      <div class="form-row">
        <el-form-item label="出库时间">
          <el-date-picker
            v-model="conditionForm.startCreateTime"
            type="datetime"
            placeholder="开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
          <span style="margin: 0 8px;">—</span>
          <el-date-picker
            v-model="conditionForm.endCreateTime"
            type="datetime"
            placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>

        <el-form-item label="更新时间">
          <el-date-picker
            v-model="conditionForm.startUpdateTime"
            type="datetime"
            placeholder="开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
          <span style="margin: 0 8px;">—</span>
          <el-date-picker
            v-model="conditionForm.endUpdateTime"
            type="datetime"
            placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
          />
        </el-form-item>
      </div>

      <!-- 第三行：排序和操作按钮 -->
      <div class="form-row">
        <el-form-item label="排序方式">
          <el-select
            v-model="conditionForm.sortField"
            placeholder="选择排序方式"
            style="width: 120px"
            @change="queryList"
          >
            <el-option
              v-for="item in SORT_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="排序方向">
          <el-button-group>
            <el-button
              :type="conditionForm.sortAsc ? 'primary' : ''"
              @click="conditionForm.sortAsc = true; queryList()"
            >
              升序
            </el-button>
            <el-button
              :type="!conditionForm.sortAsc ? 'primary' : ''"
              @click="conditionForm.sortAsc = false; queryList()"
            >
              降序
            </el-button>
          </el-button-group>
        </el-form-item>

        <el-form-item>
          <el-button size="large" type="primary" :icon="Search" @click="queryList" round>搜索</el-button>
          <el-button size="large" :icon="RefreshLeft" @click="resetQuery(queryForm)" round>重置</el-button>
        </el-form-item>
      </div>
    </el-form>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" :icon="Plus" plain @click="handleAdd">新增出库</el-button>
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
              @click="handleDelete(row)"
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
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    />

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      draggable
      @close="handleClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="顾客" prop="customerId">
          <div style="display: flex; gap: 10px;">
            <el-input
              v-model="customerSearchKeyword"
              placeholder="请输入顾客ID或名称搜索"
              style="flex: 1;"
            />
            <el-button
              type="primary"
              :icon="Search"
              :loading="customerLoading"
              @click="remoteCustomerSearch"
            >
              搜索
            </el-button>
          </div>
          <el-select
            v-model="form.customerId"
            filterable
            placeholder="请先搜索并选择顾客"
            style="width: 100%; margin-top: 10px;"
          >
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span>ID: {{ item.id }} - 名称: {{ item.name }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="商品" prop="goodId">
          <div style="display: flex; gap: 10px;">
            <el-input
              v-model="goodSearchKeyword"
              placeholder="请输入商品ID或名称搜索"
              style="flex: 1;"
            />
            <el-button
              type="primary"
              :icon="Search"
              :loading="goodLoading"
              @click="remoteGoodSearch"
            >
              搜索
            </el-button>
          </div>
          <el-select
            v-model="form.goodId"
            filterable
            placeholder="请先搜索并选择商品"
            style="width: 100%; margin-top: 10px;"
          >
            <el-option
              v-for="item in goodOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <span>ID: {{ item.id }} - 名称: {{ item.name }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="出库数量" prop="sellNumber">
          <el-input-number v-model="form.sellNumber" :min="1" placeholder="请输入出库数量" style="width: 100%" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="less">
.sell-management {
  padding: 20px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: var(--el-box-shadow-light);
  width: 100%;

  .search-form {
    margin-bottom: 8px;

    .form-row {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      margin-bottom: 15px;
      align-items: center;

      .el-form-item {
        margin-right: 0;
        margin-bottom: 0;
        min-width: 200px;
      }

      .el-date-editor {
        width: 220px !important;
      }

      .el-select {
        width: 120px !important;
      }
    }
  }

  .action-buttons {
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