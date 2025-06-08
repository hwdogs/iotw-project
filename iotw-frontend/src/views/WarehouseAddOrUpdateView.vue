<script setup lang="ts">
import {ref, onMounted, reactive} from 'vue'
import {post, get} from '@/net'
import {ElMessage} from "element-plus"
import type {FormInstance} from 'element-plus'
import {useRoute, useRouter} from 'vue-router'
import {ArrowLeft} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 类型定义
interface Account {
  id: number
  name: string
  role: number
}

// 步骤条相关
const activeStep = ref(0)
const steps = [
  {title: '基本信息', description: '填写仓库基本信息'},
  {title: '管理者设置', description: '选择仓库管理者'},
  {title: '确认信息', description: '确认所有信息'}
]

// 表单数据
const formRef = ref<FormInstance>()
const warehouseForm = reactive({
  warehouseId: null as number | null,
  warehouseName: '',
  area: null as number | null,
  description: '',
  accountIds: [] as number[]
})

// 账户列表
const accountList = ref<Account[]>([])
const loading = ref(false)

// 远程搜索相关
const searchLoading = ref(false)
const searchTimeout = ref<number | null>(null)

// 表单验证规则
const rules = {
  warehouseName: [
    {required: true, message: '请输入仓库名称', trigger: 'blur'},
    {min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur'}
  ],
  area: [
    {required: true, message: '请输入仓库面积', trigger: 'blur'}
  ]
}

// 获取当前步骤需要验证的字段
const getCurrentStepFields = () => {
  return ['warehouseName', 'area']
}

// 远程搜索方法
const remoteSearch = (query: string) => {
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  if (query) {
    searchLoading.value = true
    searchTimeout.value = window.setTimeout(() => {
      post('/api/account/query', {
        pageNum: 1,
        pageSize: 10,
        username: query,
        sortField: 'id',
        sortAsc: true
      }, (res) => {
        accountList.value = res.records.map((item: any) => ({
          id: item.id,
          name: item.name,
          role: item.role
        }))
        searchLoading.value = false
      }, (message) => {
        ElMessage.error('搜索账户失败：' + message)
        searchLoading.value = false
      })
    }, 300)
  } else {
    accountList.value = []
  }
}

// 如果是编辑模式，获取仓库信息
const getWarehouseInfo = (warehouseId: number) => {
  loading.value = true
  try {
    post(`/api/warehouse/query`, {warehouseId}, (res) => {
      Object.assign(warehouseForm, res.records[0])
    }, (message) => {
      ElMessage.error('获取仓库信息失败：' + message)
    })
  } catch (error) {
    ElMessage.error('获取仓库信息失败：' + error)
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    // 提交时验证所有字段
    await formRef.value.validate()
    const isEdit = route.query.type === 'edit'
    const apiUrl = isEdit ? '/api/warehouse/update' : '/api/warehouse/add'
    
    const formData = isEdit ? {
      warehouseId: warehouseForm.warehouseId,
      ...warehouseForm
    } : warehouseForm

    post(apiUrl, formData, () => {
      ElMessage.success(isEdit ? '更新成功' : '添加成功')
      router.push('/index/warehouse')
    }, (err) => {
      ElMessage.error('操作失败：' + err)
    })
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 下一步
const nextStep = async () => {
  if (!formRef.value) return
  
  try {
    // 只验证仓库名称和面积
    const fields = getCurrentStepFields()
    for (const field of fields) {
      await formRef.value.validateField(field)
    }
    if (activeStep.value < steps.length - 1) {
      activeStep.value++
    } else {
      await submitForm()
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 上一步
const prevStep = () => {
  if (activeStep.value > 0) {
    activeStep.value--
  }
}

// 返回列表
const goBack = () => {
  router.push('/index/warehouse')
}

onMounted(() => {
  if (route.query.type === 'edit' && route.query.id) {
    getWarehouseInfo(Number(route.query.id))
  }
})
</script>

<template>
  <div class="warehouse-manage">
    <div class="header">
      <el-button :icon="ArrowLeft" @click="goBack">返回列表</el-button>
      <h2>{{ route.query.type === 'edit' ? '编辑仓库' : '新增仓库' }}</h2>
    </div>

    <el-steps :active="activeStep" finish-status="success" class="steps">
      <el-step
          v-for="(step, index) in steps"
          :key="index"
          :title="step.title"
          :description="step.description"
      />
    </el-steps>

    <el-form
        ref="formRef"
        :model="warehouseForm"
        :rules="rules"
        label-width="100px"
        class="form"
    >
      <!-- 步骤1：基本信息 -->
      <div v-show="activeStep === 0">
        <el-form-item label="仓库名称" prop="warehouseName">
          <el-input
              v-model="warehouseForm.warehouseName"
              placeholder="请输入仓库名称"
          />
        </el-form-item>

        <el-form-item label="面积(㎡)" prop="area">
          <el-input-number
              v-model="warehouseForm.area"
              :min="1"
              :step="10"
              controls-position="right"
              style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="仓库描述" prop="description">
          <el-input
              v-model="warehouseForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入仓库描述信息"
          />
        </el-form-item>
      </div>

      <!-- 步骤2：管理者设置 -->
      <div v-show="activeStep === 1">
        <el-form-item label="管理者" prop="accountIds">
          <el-select
              v-model="warehouseForm.accountIds"
              multiple
              filterable
              remote
              :remote-method="remoteSearch"
              :loading="searchLoading"
              placeholder="请输入用户名搜索并选择仓库管理者"
              style="width: 100%"
          >
            <el-option
                v-for="account in accountList"
                :key="account.id"
                :label="account.name"
                :value="account.id"
            >
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>{{ account.name }}</span>
                <span style="color: #8492a6; font-size: 13px">
                  ID: {{ account.id }}
                </span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </div>

      <!-- 步骤3：确认信息 -->
      <div v-show="activeStep === 2">
        <el-descriptions title="仓库信息" :column="1" border>
          <el-descriptions-item label="仓库名称">
            {{ warehouseForm.warehouseName }}
          </el-descriptions-item>
          <el-descriptions-item label="面积">
            {{ warehouseForm.area }} 平方米
          </el-descriptions-item>
          <el-descriptions-item label="描述">
            {{ warehouseForm.description || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="管理者">
            <el-tag
                v-for="id in warehouseForm.accountIds"
                :key="id"
                class="mx-1"
                style="margin-right: 8px"
            >
              {{ accountList.find(a => a.id === id)?.name || `ID: ${id}` }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 步骤控制按钮 -->
      <div class="steps-action">
        <el-button v-if="activeStep > 0" @click="prevStep">上一步</el-button>
        <el-button type="primary" @click="nextStep">
          {{ activeStep === steps.length - 1 ? '提交' : '下一步' }}
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<style scoped lang="less">
.warehouse-manage {
  padding: 20px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
  box-shadow: var(--el-box-shadow-light);

  .header {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
    gap: 16px;

    h2 {
      margin: 0;
    }
  }

  .steps {
    margin-bottom: 30px;
  }

  .form {
    max-width: 600px;
    margin: 0 auto;
  }

  .steps-action {
    margin-top: 24px;
    display: flex;
    justify-content: center;
    gap: 16px;
  }
}
</style>