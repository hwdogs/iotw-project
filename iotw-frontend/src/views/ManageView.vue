<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import {post, get} from '@/net'
import {ElMessage, ElMessageBox} from "element-plus";
import type {FormInstance} from 'element-plus'
import {Plus, Search, RefreshLeft} from '@element-plus/icons-vue'

// 类型定义
interface Manage {
  manageId: number
  warehouseId: number
  accountId: number
  createTime: string
  updateTime: string
}

// 添加列配置类型
interface TableColumnConfig {
  prop: keyof Manage | string
  label: string
  width?: string | number
  showOverflowTooltip?: boolean
}

// 响应式数据
const tableData = ref<Manage[]>([])
const loading = ref<boolean>(false);
const totalSearch = ref(0);

// 添加账户和仓库的远程搜索数据
const accountOptions = ref<{ id: number, name: string }[]>([])
const warehouseOptions = ref<{ id: number, name: string }[]>([])
const accountLoading = ref(false)
const warehouseLoading = ref(false)

// 添加搜索关键词
const warehouseSearchKeyword = ref('')
const accountSearchKeyword = ref('')

// 添加排序选项
const SORT_OPTIONS = [
  {value: 'create_time', label: '创建时间'},
  {value: 'update_time', label: '更新时间'},
  {value: 'warehouse_id', label: '仓库ID'},
  {value: 'account_id', label: '账户ID'}
]

const tableLabel = reactive<TableColumnConfig[]>([
  {prop: 'manageId', label: '管理ID'},
  {prop: 'warehouseId', label: '仓库ID'},
  {prop: 'accountId', label: '账户ID'},
  {prop: 'createTime', label: '创建时间'},
  {prop: 'updateTime', label: '更新时间'}
])

const conditionForm = reactive({
  pageNum: 1,
  pageSize: 10,
  warehouseId: null as number | null,
  accountId: null as number | null,
  startCreateTime: '',
  endCreateTime: '',
  startUpdateTime: '',
  endUpdateTime: '',
  sortField: 'create_time',
  sortAsc: false,
})

// 添加表单引用类型
const queryForm = ref<FormInstance>()

// 远程搜索仓库
const remoteWarehouseSearch = () => {
  if (warehouseSearchKeyword.value) {
    try {
      warehouseLoading.value = true
      // 判断输入是否为数字
      const isNumber = !isNaN(Number(warehouseSearchKeyword.value))
      post('/api/warehouse/query', {
        pageNum: 1,
        pageSize: 10,
        // 如果是数字，则按ID搜索，否则按名称搜索
        ...(isNumber ? { warehouseId: Number(warehouseSearchKeyword.value) } : { name: warehouseSearchKeyword.value })
      }, (res) => {
        console.log('仓库搜索结果:', res)
        if (res && res.records) {
          warehouseOptions.value = res.records.map((item: any) => {
            console.log('处理仓库数据:', item)
            return {
              id: item.warehouseId,
              name: item.warehouseName
            }
          })
          console.log('处理后的仓库选项:', warehouseOptions.value)
        }
      }, (err) => {
        ElMessage.error('获取仓库列表失败' + err)
      })
    } catch (error) {
      ElMessage.error('获取仓库列表失败' + error)
    } finally {
      warehouseLoading.value = false
    }
  } else {
    warehouseOptions.value = []
  }
}

// 远程搜索账户
const remoteAccountSearch = () => {
  if (accountSearchKeyword.value) {
    try {
      accountLoading.value = true
      // 判断输入是否为数字
      const isNumber = !isNaN(Number(accountSearchKeyword.value))
      post('/api/account/query', {
        pageNum: 1,
        pageSize: 10,
        // 如果是数字，则按ID搜索，否则按名称搜索
        ...(isNumber ? { id: Number(accountSearchKeyword.value) } : { username: accountSearchKeyword.value })
      }, (res) => {
        console.log('账户搜索结果:', res)
        if (res && res.records) {
          accountOptions.value = res.records.map((item: any) => {
            console.log('处理账户数据:', item)
            return {
              id: item.id,
              name: item.name
            }
          })
          console.log('处理后的账户选项:', accountOptions.value)
        }
      }, (err) => {
        ElMessage.error('获取账户列表失败' + err)
      })
    } catch (error) {
      ElMessage.error('获取账户列表失败' + error)
    } finally {
      accountLoading.value = false
    }
  } else {
    accountOptions.value = []
  }
}

const getManageData = () => {
  try {
    loading.value = true
    post('/api/manage/query', conditionForm, (res) => {
      if (res) {
        const {records, total} = res;
        tableData.value = records
        totalSearch.value = total
      }
    }, (err) => {
      console.log(err)
      ElMessage.error('table数据加载失败' + err)
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
  getManageData()
}

const handleSizeChange = (size: number) => {
  conditionForm.pageSize = size
  getManageData()
}

// 搜索重置
const resetQuery = (formEl: FormInstance | undefined) => {
  formEl?.resetFields()
  if (!formEl) return
  formEl.resetFields()
  conditionForm.pageNum = 1
  getManageData()
}

// 编辑对话框相关
const dialogVisible = ref(false)
const editForm = ref<FormInstance>()
const editFormData = reactive({
  manageId: null as number | null,
  warehouseId: null as number | null,
  accountId: null as number | null,
})

// 添加表单验证规则
const rules = {
  warehouseId: [
    {required: true, message: '请选择仓库', trigger: 'change'}
  ],
  accountId: [
    {required: true, message: '请选择账户', trigger: 'change'}
  ]
}

// 添加管理记录操作
const handleAdd = () => {
  dialogVisible.value = true
  // Reset form data
  editFormData.manageId = null
  editFormData.warehouseId = null
  editFormData.accountId = null
}

// 提交添加表单
const submitAddForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
      const requestData = {
        warehouseId: editFormData.warehouseId,
        accountId: editFormData.accountId
      }

      post('/api/manage/add', requestData, () => {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        getManageData()
      }, (message) => {
        ElMessage.error(message)
      })
    }
  })
}

// 更新操作
const handleUpdate = (id: number) => {
  const currentRow = tableData.value.find(item => Number(item.manageId) === id)
  if (currentRow) {
    editFormData.manageId = id
    editFormData.warehouseId = currentRow.warehouseId
    editFormData.accountId = currentRow.accountId
    dialogVisible.value = true
  }
}

// 提交编辑表单
const submitEditForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
      const requestData = {
        manageId: editFormData.manageId,
        warehouseId: editFormData.warehouseId,
        accountId: editFormData.accountId
      }

      post('/api/manage/update', requestData, () => {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        getManageData()
      }, (message) => {
        ElMessage.error(message)
      })
    }
  })
}

// 关闭对话框时重置表单
const handleClose = () => {
  editForm.value?.resetFields()
  editFormData.manageId = null
  editFormData.warehouseId = null
  editFormData.accountId = null
}

// 删除操作
const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认删除该管理记录？', '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    get(`/api/manage/delete?id=${id}`, () => {
      ElMessage.success('删除成功')
      getManageData()
    }, (message) => {
      ElMessage.error(message)
    })
  }).catch(() => {
  })
}

onMounted(() => {
  getManageData()
})
</script>

<template>
  <div class="manage-management" style="width: auto;">
    <!--搜索表单-->
    <el-form
        ref="queryForm"
        :inline="true"
        :model="conditionForm"
        class="search-form">
      <el-form-item label="仓库ID" prop="warehouseId">
        <el-input
            v-model.number="conditionForm.warehouseId"
            placeholder="输入仓库ID"
            clearable
        />
      </el-form-item>

      <el-form-item label="账户ID" prop="accountId">
        <el-input
            v-model.number="conditionForm.accountId"
            placeholder="输入账户ID"
            clearable
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
              @change="getManageData"
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
                @click="conditionForm.sortAsc = true; getManageData()"
            >
              升序
            </el-button>
            <el-button
                :type="!conditionForm.sortAsc ? 'primary' : ''"
                @click="conditionForm.sortAsc = false; getManageData()"
            >
              降序
            </el-button>
          </el-button-group>
        </el-form-item>
      </div>

      <el-form-item>
        <el-button size="large" type="primary" :icon="Search" @click="getManageData" round>搜索</el-button>
        <el-button size="large" :icon="RefreshLeft" @click="resetQuery(queryForm)" round>重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" :icon="Plus" plain @click="handleAdd">新增管理记录</el-button>
    </div>

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

        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
                type="primary"
                size="default"
                text
                bg
                @click="handleUpdate(row.manageId)"
            >
              编辑
            </el-button>
            <el-button
                type="danger"
                size="default"
                text
                bg
                @click="handleDelete(row.manageId)"
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

    <!-- 编辑/添加对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="editFormData.manageId ? '编辑管理记录' : '添加新管理记录'"
        width="500px"
        draggable
        @close="handleClose"
    >
      <el-form
          ref="editForm"
          :model="editFormData"
          :rules="rules"
          label-width="100px"
      >
        <el-form-item label="仓库" prop="warehouseId">
          <div style="display: flex; gap: 10px;">
            <el-input
                v-model="warehouseSearchKeyword"
                placeholder="请输入仓库ID或名称搜索"
                style="flex: 1;"
            />
            <el-button
                type="primary"
                :icon="Search"
                :loading="warehouseLoading"
                @click="remoteWarehouseSearch"
            >
              搜索
            </el-button>
          </div>
          <el-select
              v-model="editFormData.warehouseId"
              filterable
              placeholder="请先搜索并选择仓库"
              style="width: 100%; margin-top: 10px;"
          >
            <el-option
                v-for="item in warehouseOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            >
              <span>ID: {{ item.id }} - 名称: {{ item.name }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="账户" prop="accountId">
          <div style="display: flex; gap: 10px;">
            <el-input
                v-model="accountSearchKeyword"
                placeholder="请输入账户ID或名称搜索"
                style="flex: 1;"
            />
            <el-button
                type="primary"
                :icon="Search"
                :loading="accountLoading"
                @click="remoteAccountSearch"
            >
              搜索
            </el-button>
          </div>
          <el-select
              v-model="editFormData.accountId"
              filterable
              placeholder="请先搜索并选择账户"
              style="width: 100%; margin-top: 10px;"
          >
            <el-option
                v-for="item in accountOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
            >
              <span>ID: {{ item.id }} - 名称: {{ item.name }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="editFormData.manageId ? submitEditForm(editForm) : submitAddForm(editForm)">
            {{ editFormData.manageId ? '确认编辑' : '确认添加' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<style scoped lang="less">
.manage-management {
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