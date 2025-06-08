<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Search, Plus, RefreshLeft } from '@element-plus/icons-vue'
import { post, get } from '@/net'

// 类型定义
interface Category {
  categoryId: number
  categoryName: string
}

// 添加列配置类型
interface TableColumnConfig {
  prop: keyof Category | string
  label: string
  width?: string | number
  showOverflowTooltip?: boolean
}

// 响应式数据
const tableData = ref<Category[]>([])
const loading = ref(false)
const total = ref(0)

const tableLabel = reactive<TableColumnConfig[]>([
  { prop: 'categoryId', label: '类别ID', width: '100' },
  { prop: 'categoryName', label: '类别名称', showOverflowTooltip: true }
])

const SORT_OPTIONS = [
  { value: 'category_id', label: '默认排序' }
]

// 查询表单数据
const queryForm = ref<FormInstance>()
const conditionForm = reactive({
  pageNum: 1,
  pageSize: 10,
  categoryId: undefined,
  categoryName: undefined,
  sortField: 'category_id',
  sortAsc: false
})

// 查询列表数据
const queryList = async () => {
  loading.value = true
  post('/api/category/query', conditionForm,
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
  
  formEl.resetFields()
  
  Object.assign(conditionForm, {
    pageNum: 1,
    pageSize: 10,
    categoryId: undefined,
    categoryName: undefined,
    sortField: 'category_id',
    sortAsc: false
  })
  
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
  categoryId: undefined as number | undefined,
  categoryName: undefined as string | undefined
})

// 表单验证规则
const rules = {
  categoryId: [
    { required: true, message: '请输入类别ID', trigger: 'blur' },
    { type: 'number' as const, message: '类别ID必须为数字', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value === undefined || value === null) {
          callback()
          return
        }
        const num = Number(value)
        if (num < 1 || num > 32767) {
          callback(new Error('类别ID范围在1-32767之间'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ],
  categoryName: [
    { required: true, message: '请输入类别名称', trigger: 'blur' },
    { min: 1, max: 20, message: '类别名称长度在1-20个字符之间', trigger: 'blur' }
  ]
}

// 打开添加对话框
const handleAdd = () => {
  dialogType.value = 'add'
  dialogTitle.value = '添加类别'
  form.value = {
    categoryId: undefined,
    categoryName: undefined
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const handleEdit = (row: Category) => {
  dialogType.value = 'update'
  dialogTitle.value = '编辑类别'
  form.value = { 
    categoryId: Number(row.categoryId),
    categoryName: row.categoryName
  }
  dialogVisible.value = true
}

// 删除记录
const handleDelete = async (row: Category) => {
  try {
    await ElMessageBox.confirm('确认删除该类别吗？', '提示', {
      type: 'warning'
    })
    get(`/api/category/delete?id=${row.categoryId}`,
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
      const url = dialogType.value === 'add' ? '/api/category/add' : '/api/category/update'
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
    categoryId: undefined,
    categoryName: undefined
  }
}

onMounted(() => {
  queryList()
})
</script>

<template>
  <div class="category-management" style="width: auto;">
    <!-- 搜索表单 -->
    <el-form
      ref="queryForm"
      :inline="true"
      :model="conditionForm"
      class="search-form"
    >
      <!-- 第一行：基本信息 -->
      <div class="form-row">
        <el-form-item label="类别ID" prop="categoryId">
          <el-input
            v-model="conditionForm.categoryId"
            placeholder="输入类别ID"
            clearable
          />
        </el-form-item>

        <el-form-item label="类别名称" prop="categoryName">
          <el-input
            v-model="conditionForm.categoryName"
            placeholder="输入类别名称"
            clearable
          />
        </el-form-item>
      </div>

      <!-- 第二行：排序和操作按钮 -->
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
      <el-button type="primary" :icon="Plus" plain @click="handleAdd">新增类别</el-button>
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
        <el-form-item label="类别ID" prop="categoryId">
          <el-input-number 
            v-model.number="form.categoryId" 
            :min="1" 
            :max="32767"
            placeholder="请输入类别ID"
            style="width: 100%"
            :disabled="dialogType === 'update'"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="类别名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入类别名称" />
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
.category-management {
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