<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Plus, Edit, Delete, RefreshLeft } from '@element-plus/icons-vue'
import { post, get } from '@/net'

// 类型定义
interface Supply {
  supplyId: number
  supplierId: number
  goodId: number
  supplyNumber: number
  status: number
  createTime: string
  updateTime: string
}

// 添加列配置类型
interface TableColumnConfig {
  prop: keyof Supply | string
  label: string
  width?: string | number
  showOverflowTooltip?: boolean
}

// 响应式数据
const tableData = ref<Supply[]>([])
const loading = ref(false)
const total = ref(0)

const tableLabel = reactive<TableColumnConfig[]>([
  { prop: 'supplyId', label: '入库ID', width: '95' },
  { prop: 'supplierId', label: '供应商ID'},
  { prop: 'goodId', label: '商品ID' },
  { prop: 'supplyNumber', label: '入库数量' },
  { prop: 'statusLabel', label: '状态' },
  { prop: 'createTime', label: '入库时间' },
  { prop: 'updateTime', label: '更新时间' }
])

const STATUS_OPTIONS = [
  { value: 0, label: '待审批', type: 'info' },
  { value: 1, label: '审批通过', type: 'success' },
  { value: -1, label: '审批失败', type: 'danger' }
]

const SORT_OPTIONS = [
  { value: 'update_time', label: '默认排序' },
  { value: 'create_time', label: '入库时间' },
  { value: 'supply_id', label: '入库ID' }
]

// 查询表单数据
const queryForm = ref<FormInstance>()
const conditionForm = reactive({
  pageNum: 1,
  pageSize: 10,
  supplyId: undefined,
  supplierId: undefined,
  goodId: undefined,
  status: undefined,
  startSupplyNumber: undefined,
  endSupplyNumber: undefined,
  startCreateTime: undefined,
  endCreateTime: undefined,
  startUpdateTime: undefined,
  endUpdateTime: undefined,
  sortField: 'create_time',
  sortAsc: false
})

// 查询列表数据
const queryList = async () => {
  loading.value = true
  // 创建请求数据的副本
  const requestData = { ...conditionForm }
  

  post('/api/supply/query', requestData,
    (data) => {
      tableData.value = data.records.map((item: Supply) => ({
        ...item,
        statusLabel: STATUS_OPTIONS.find(s => s.value === item.status)?.label || '未知',
        statusType: STATUS_OPTIONS.find(s => s.value === item.status)?.type || 'info'
      }))
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
    supplyId: undefined,
    supplierId: undefined,
    goodId: undefined,
    status: undefined,
    startSupplyNumber: undefined,
    endSupplyNumber: undefined,
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
  supplyId: undefined,
  supplierId: undefined,
  goodId: undefined,
  supplyNumber: undefined,
  status: undefined
})

// 表单验证规则
const rules = ref<FormRules>({
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  goodId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  supplyNumber: [{ required: true, message: '请输入入库数量', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

// 打开添加对话框
const handleAdd = () => {
  dialogType.value = 'add'
  dialogTitle.value = '添加入库记录'
  form.value = {
    supplyId: undefined,
    supplierId: undefined,
    goodId: undefined,
    supplyNumber: undefined,
    status: 0  // 默认状态为待审批
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const handleEdit = (row: Supply) => {
  dialogType.value = 'update'
  dialogTitle.value = '编辑入库记录'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除记录
const handleDelete = async (row: Supply) => {
  try {
    await ElMessageBox.confirm('确认删除该入库记录吗？', '提示', {
      type: 'warning'
    })
    get(`/api/supply/delete?id=${row.supplyId}`,
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
      const url = dialogType.value === 'add' ? '/api/supply/add' : '/api/supply/update'
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
    supplyId: undefined,
    supplierId: undefined,
    goodId: undefined,
    supplyNumber: undefined,
    status: undefined
  }
}

onMounted(() => {
  queryList()
})
</script>

<template>
  <div class="supply-management" style="width: auto;">
    <!-- 搜索表单 -->
    <el-form
      ref="queryForm"
      :inline="true"
      :model="conditionForm"
      class="search-form"
    >
      <!-- 第一行：基本信息 -->
      <div class="form-row">
        <el-form-item label="入库ID" prop="supplyId">
          <el-input
            v-model="conditionForm.supplyId"
            placeholder="输入入库ID"
            clearable
          />
        </el-form-item>

        <el-form-item label="供应商ID" prop="supplierId">
          <el-input
            v-model="conditionForm.supplierId"
            placeholder="输入供应商ID"
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

        <el-form-item label="状态" prop="status">
          <el-select
            v-model="conditionForm.status"
            placeholder="选择状态"
            clearable
            style="width: 110px;"
          >
            <el-option
              v-for="item in STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="入库数量">
          <el-input-number
            v-model="conditionForm.startSupplyNumber"
            placeholder="最小值"
          />
          <span style="margin: 0 8px;">—</span>
          <el-input-number
            v-model="conditionForm.endSupplyNumber"
            placeholder="最大值"
          />
        </el-form-item>
      </div>

      <!-- 第二行：时间相关 -->
      <div class="form-row">
        <el-form-item label="入库时间">
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
      <el-button type="primary" :icon="Plus" plain @click="handleAdd">新增入库</el-button>
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
        >
          <template #default="{ row }" v-if="col.prop === 'statusLabel'">
            <el-tag :type="row.statusType">{{ row.statusLabel }}</el-tag>
          </template>
        </el-table-column>

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
        <el-form-item label="供应商ID" prop="supplierId">
          <el-input v-model="form.supplierId" placeholder="请输入供应商ID" />
        </el-form-item>
        <el-form-item label="商品ID" prop="goodId">
          <el-input v-model="form.goodId" placeholder="请输入商品ID" />
        </el-form-item>
        <el-form-item label="入库数量" prop="supplyNumber">
          <el-input-number v-model="form.supplyNumber" :min="1" placeholder="请输入入库数量" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option
              v-for="item in STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
.supply-management {
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