<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { EditPen, Picture, Tickets, Plus, ArrowLeft } from '@element-plus/icons-vue'
import { post, get } from '@/net'
import type { FormInstance, UploadProps } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 步骤条当前位置
const currentStep = ref(0)

// 表单数据
const goodForm = reactive({
  goodId: null as number | null,
  goodName: '',
  warehouseId: null as number | null,
  category: null as number | null,
  price: 0,
  standard: '',
  image: '',
  description: ''
})

// 仓库列表
const warehouseList = ref<{ warehouseId: number, warehouseName: string }[]>([])

// 商品类别列表
const categoryList = ref<{ categoryId: number, categoryName: string }[]>([])

// 获取商品类别列表
const getCategoryList = () => {
  post('/api/category/query', {
    pageNum: 1,
    pageSize: 100,
    sortField: 'category_id',
    sortAsc: true
  }, (res) => {
    categoryList.value = res.records.map((item: any) => ({
      categoryId: item.categoryId,
      categoryName: item.categoryName
    }))
  }, (message) => {
    ElMessage.error('获取商品类别列表失败：' + message)
  })
}

// 图片上传相关
const imageUrl = ref('')

const handleAvatarSuccess: UploadProps['onSuccess'] = (
  response,
  uploadFile
) => {
  // 使用相对路径，因为已经配置了baseURL
  goodForm.image = `/api/file/download?name=${response}`

  imageUrl.value = goodForm.image
  ElMessage.success('图片上传成功')
}

const handleAvatarError = (error: any) => {
  console.error('Image load error:', error)
  ElMessage.error('图片加载失败')
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/png') {
    ElMessage.error('图片必须是 JPG 或 PNG 格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 自定义上传方法
const customUpload = async (options: any) => {
  const { file, onSuccess, onError } = options
  const formData = new FormData()
  formData.append('file', file)

  try {
    // 使用封装的post方法
    post('/api/file/upload', formData, 
      (response) => {
        // 直接传递文件名给成功回调
        onSuccess(response)
      },
      (error) => {
        ElMessage.error('图片上传失败：' + error)
        onError(new Error(error || '上传失败'))
      }
    )
  } catch (error) {
    ElMessage.error('图片上传失败：' + error)
    onError(error)
  }
}

// 获取仓库列表
const getWarehouseList = () => {
  post('/api/warehouse/query', {
    pageNum: 1,
    pageSize: 100,
    sortField: 'warehouse_id',
    sortAsc: true
  }, (res) => {
    warehouseList.value = res.records
  }, (message) => {
    ElMessage.error('获取仓库列表失败：' + message)
  })
}

// 获取商品信息
const getGoodInfo = (goodId: number) => {
  post('/api/good/query', {
    goodId,
    pageNum: 1,
    pageSize: 5
  }, (res) => {
    if (res.records.length > 0) {
      const good = res.records[0]
      Object.assign(goodForm, good)
      if (good.image) {
        imageUrl.value = good.image
      }
    }
  }, (message) => {
    ElMessage.error('获取商品信息失败：' + message)
  })
}

// 表单验证规则
const rules = {
  goodName: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  warehouseId: [
    { required: true, message: '请选择仓库', trigger: 'change' }
  ],
  category: [
    { required: true, message: '请选择商品类别', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' }
  ]
}

// 保存商品
const saveGood = async () => {
  const isEdit = route.query.type === 'edit'
  const apiUrl = isEdit ? '/api/good/update' : '/api/good/add'
  
  try {
    const response = await post(apiUrl, goodForm)
    ElMessage.success(isEdit ? '更新成功' : '添加成功')
    router.push('/index/good')
  } catch (error) {
    ElMessage.error(isEdit ? '更新失败' : '添加失败')
  }
}

// 返回列表
const handleBack = () => {
  router.push('/index/good')
}

onMounted(() => {
  getWarehouseList()
  getCategoryList()
  if (route.query.type === 'edit' && route.query.id) {
    getGoodInfo(Number(route.query.id))
  }
})
</script>

<template>
  <div class="good-add-or-update">
    <div class="header">
      <el-button :icon="ArrowLeft" @click="handleBack">返回列表</el-button>
      <h2>{{ route.query.type === 'edit' ? '编辑商品' : '新增商品' }}</h2>
    </div>

    <el-card class="box-card">
      <template #header>
        <el-steps
            class="mb-4"
            :space="200"
            :active="currentStep"
            simple
        >
          <el-step title="基本信息" :icon="EditPen" @click="currentStep = 0"/>
          <el-step title="商品封面" :icon="Picture" @click="currentStep = 1"/>
          <el-step title="商品描述" :icon="Tickets" @click="currentStep = 2"/>
        </el-steps>
      </template>

      <el-form :model="goodForm" :rules="rules" label-width="120px" size="large">
        <!-- 基本信息 -->
        <div v-show="currentStep === 0">
          <el-form-item label="商品名称" prop="goodName">
            <el-input v-model="goodForm.goodName" placeholder="请输入商品名称" maxlength="50" show-word-limit/>
          </el-form-item>

          <el-form-item label="仓库" prop="warehouseId">
            <el-select v-model="goodForm.warehouseId" placeholder="请选择仓库" style="width: 100%">
              <el-option
                  v-for="warehouse in warehouseList"
                  :key="warehouse.warehouseId"
                  :label="warehouse.warehouseName"
                  :value="warehouse.warehouseId"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="分类" prop="category">
            <el-select v-model="goodForm.category" placeholder="请选择分类" style="width: 100%">
              <el-option
                  v-for="item in categoryList"
                  :key="item.categoryId"
                  :label="item.categoryName"
                  :value="item.categoryId"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="规格" prop="standard">
            <el-input v-model="goodForm.standard" placeholder="请输入规格"/>
          </el-form-item>

          <el-form-item label="价格" prop="price">
            <el-input-number 
                v-model="goodForm.price" 
                placeholder="请输入价格" 
                :precision="2" 
                :step="0.1" 
                :min="0"
                style="width: 100%"
            />
          </el-form-item>
        </div>

        <!-- 商品封面 -->
        <div v-show="currentStep === 1">
          <el-form-item label="展示图片">
            <el-upload
                class="avatar-uploader"
                :http-request="customUpload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                accept=".jpg,.jpeg,.png"
            >
              <img v-if="imageUrl" :src="imageUrl" class="avatar"/>
              <el-icon v-else class="avatar-uploader-icon"><Plus/></el-icon>
            </el-upload>
          </el-form-item>
        </div>

        <!-- 商品描述 -->
        <div v-show="currentStep === 2">
          <el-form-item label="商品描述" prop="description">
            <el-input
                v-model="goodForm.description"
                type="textarea"
                :rows="4"
                placeholder="请输入商品描述"
            />
          </el-form-item>
        </div>

        <div class="form-bottom-btn">
          <el-button v-if="currentStep > 0" @click="currentStep--">上一步</el-button>
          <el-button v-if="currentStep < 2" type="primary" @click="currentStep++">下一步</el-button>
          <el-button v-if="currentStep === 2" type="primary" @click="saveGood">保存</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped lang="less">
.good-add-or-update {
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

  .box-card {
    width: auto;
    margin-top: 17px;
  }

  .el-step {
    cursor: pointer;
  }

  .form-bottom-btn {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 20px;
    gap: 16px;
  }

  .el-form {
    padding: 0 180px 0 80px;
  }
}

.avatar-uploader {
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }

  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);

    &:hover {
      border-color: var(--el-color-primary);
    }
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
    line-height: 178px;
  }
}
</style>