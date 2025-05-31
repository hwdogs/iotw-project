<script setup lang="ts">
import {ref, onMounted, reactive, computed} from 'vue'
import {post, get} from '@/net'
import {ElMessage, ElMessageBox} from "element-plus";
import type {FormInstance} from 'element-plus'
import {Plus, Search, RefreshLeft} from '@element-plus/icons-vue'
import router from '@/router';


//类型定义
interface Account {
  id: number
  name: string
  role: number
  sex: number
  birth: string
  email: string
  address: string
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
  {prop: 'roleLabel', label: '角色'},
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
          roleLabel: ROLE_OPTIONS.find(r => r.value === item.role)?.label || '未知角色',
          sexLabel: SEX_OPTIONS.find(s => s.value === item.sex)?.label || '未知'
        }))

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

// 编辑对话框相关
const dialogVisible = ref(false)
const editForm = ref<FormInstance>()
const editFormData = reactive({
  id: null as number | null,
  username: '',
  password: '',
  passwordConfirm: '',
  email: '',
  code: '',
  role: null as number | null,
  birth: '',
  sex: null as number | null,
  address: ''
})

// 添加验证码相关变量
const coldTime = ref(0)
const timer = ref(null)

// 邮箱验证
const isEmailValid = computed(() => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(editFormData.email))

// 获取验证码
function askCode() {
  if (isEmailValid.value) {
    coldTime.value = 60
    // 先清除可能存在的旧计时器
    if (timer.value) clearInterval(timer.value);

    get(`/api/auth/ask-code?email=${editFormData.email}&type=register`, () => {
      ElMessage.success(`验证码已发送到邮箱: ${editFormData.email}, 请注意查收`)

      // 启动新计时器并保存ID
      timer.value = setInterval(() => {
        coldTime.value--;
        // 当倒计时结束时清除计时器
        if (coldTime.value <= 0) {
          clearInterval(timer.value);
          coldTime.value = 0; // 可选：重置显示值
        }
      }, 1000);

    }, (message) => {
      ElMessage.warning(message)
      coldTime.value = 0
      clearInterval(timer.value);
    })
  } else {
    ElMessage.warning('请输入正确的电子邮件!')
  }
}

// 修改表单验证规则
const rules = {
  username: [
    {pattern: /^[a-zA-Z0-9\u4e00-\u9fa5]+$/, message: '用户名只能包含字母、数字和中文', trigger: 'blur'},
    {min: 1, max: 10, message: '用户名长度在1-10个字符之间', trigger: 'blur'}
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
  ],
  passwordConfirm: [
    {validator: (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== editFormData.password) {
        callback(new Error("两次输入的密码不一致"))
      } else {
        callback()
      }
    }, trigger: ['blur', 'change']}
  ],
  email: [
    {required: true, message: '请输入邮件地址', trigger: 'blur'},
    {type: 'email' as const, message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入获取的验证码', trigger: 'blur'},
  ],
  role: [
    {required: true, message: '请选择角色', trigger: 'change'}
  ],
  sex: [
    {required: true, message: '请选择性别', trigger: 'change'}
  ]
}

//添加账户操作
const handleAdd = () => {
  dialogVisible.value = true
  // Reset form data
  for (const key in editFormData) {
    const typedKey = key as keyof typeof editFormData
    if (typedKey === 'id') {
      editFormData[typedKey] = null
    } else if (typedKey === 'role' || typedKey === 'sex') {
      editFormData[typedKey] = null
    } else {
      editFormData[typedKey] = '' as any
    }
  }
}

// 提交添加表单
const submitAddForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
      // 构建请求数据
      const requestData = {
        username: editFormData.username,
        password: editFormData.password,
        email: editFormData.email,
        code: editFormData.code,
        role: editFormData.role,
        birth: editFormData.birth,
        sex: editFormData.sex,
        address: editFormData.address
      }

      post('/api/account/register', requestData, () => {
        ElMessage.success('添加成功')
        dialogVisible.value = false
        getAccountData()
      }, (message) => {
        ElMessage.error(message)
      })
    }
  })
}

//更新操作
const handleUpdate = (id: number) => {
  // 获取当前行数据
  const currentRow = tableData.value.find(item => Number(item.id) === id)
  if (currentRow) {
    editFormData.id = id
    editFormData.username = currentRow.name
    // 从原始数据中获取role和sex的数值
    const originalData = tableData.value.find(item => Number(item.id) === id)
    if (originalData) {
      editFormData.role = originalData.role
      editFormData.sex = originalData.sex
    }
    editFormData.birth = currentRow.birth
    editFormData.email = currentRow.email
    editFormData.address = currentRow.address
    dialogVisible.value = true
  }
}

// 提交编辑表单
const submitEditForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
      // 构建请求数据，只包含非空字段
      const requestData: Record<string, any> = {}
      const originalData = tableData.value.find(item => Number(item.id) === editFormData.id)

      for (const key in editFormData) {
        const value = editFormData[key as keyof typeof editFormData]
        // 对于邮箱字段，只在值发生变化时才包含
        if (key === 'email') {
          if (value !== originalData?.email) {
            requestData[key] = value
          }
        } else if (value !== null && value !== '') {
          requestData[key] = value
        }
      }

      post('/api/account/update', requestData, () => {
        ElMessage.success('更新成功')
        dialogVisible.value = false
        getAccountData()
      }, (message) => {
        ElMessage.error(message)
      })
    }
  })
}

// 关闭对话框时重置表单
const handleClose = () => {
  editForm.value?.resetFields()
  for (const key in editFormData) {
    const typedKey = key as keyof typeof editFormData
    if (typedKey === 'id') {
      editFormData[typedKey] = null
    } else if (typedKey === 'role' || typedKey === 'sex') {
      editFormData[typedKey] = null
    } else {
      editFormData[typedKey] = '' as any
    }
  }
}

// 删除操作
const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认删除该用户？', '警告', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    get(`/api/account/delete?id=${id}`, () => {
      ElMessage.success('删除成功')
      getAccountData()
    }, (message) => {
      ElMessage.error(message)
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
  <div class="user-management" style="width: auto;">
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
      <el-button type="primary" :icon="Plus" plain @click="handleAdd">新增用户</el-button>
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

        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
                type="primary"
                size="default"
                text
                bg
                @click="handleUpdate(row.id)"
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
    </div>
    <!-- 数据表格 -->


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
        :title="editFormData.id ? '编辑用户信息' : '添加新用户'"
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editFormData.username" placeholder="请输入用户名"/>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="editFormData.password" type="password" placeholder="请输入密码"/>
        </el-form-item>

        <el-form-item label="确认密码" prop="passwordConfirm">
          <el-input v-model="editFormData.passwordConfirm" type="password" placeholder="请再次输入密码"/>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editFormData.email" placeholder="请输入邮箱"/>
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="17">
              <el-input v-model="editFormData.code" maxlength="6" type="text" placeholder="请输入验证码"/>
            </el-col>
            <el-col :span="5">
              <el-button type="success" plain @click="askCode"
                         :disabled="!isEmailValid || coldTime > 0">
                {{ coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select v-model="editFormData.role" placeholder="请选择角色" style="width: 100%">
            <el-option
                v-for="item in ROLE_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="出生日期" prop="birth">
          <el-date-picker
              v-model="editFormData.birth"
              type="date"
              placeholder="选择日期"
              style="width: 100%"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <el-select v-model="editFormData.sex" placeholder="请选择性别" style="width: 100%">
            <el-option
                v-for="item in SEX_OPTIONS"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input v-model="editFormData.address" placeholder="请输入地址"/>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="editFormData.id ? submitEditForm(editForm) : submitAddForm(editForm)">
            {{ editFormData.id ? '确认编辑' : '确认添加' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style> 