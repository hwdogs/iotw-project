<script setup lang="ts">
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { EditPen, Picture, Tickets, Plus } from '@element-plus/icons-vue'
import { ref } from 'vue'

const router = useRouter()

const props = defineProps({
  goodId: {
    type: String,
    required: true,
  }
})

const handleBack = () => {
  router.back()
}

// 步骤条当前位置
const currentStep = ref(0)

// 表单数据
const GoodForm = ref({
  //基本信息
  goodName: '',
  warehouseId: '',
  category: '',
  price: 0,
  standard: '',
  // 货物封面
  image: '',
  // 货物描述
  description: ''
})

import type { UploadProps } from 'element-plus'

const imageUrl = ref('')

// 回调：图片上传成功后
const handleAvatarSuccess: UploadProps['onSuccess'] = (
  response,
  uploadFile
) => {
  imageUrl.value = URL.createObjectURL(uploadFile.raw!)
}

//回调： 图片上传之前-检查
const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.type !== 'image/jpeg') {
    ElMessage.error('Avatar picture must be JPG format!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('Avatar picture size can not exceed 2MB!')
    return false
  }
  return true
}

const authItemName = "access_token";

function getAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName);
    if (!str) return null
    const authObj = JSON.parse(str)
    return authObj.token
}
</script>

<template>
  <div aria-label="A complete example of page header">
    <el-page-header @back="handleBack">
      <template #content>
        <div class="flex items-center">
          <span class="text-large font-600 mr-3"> {{ props.goodId === 'new' ? '新增商品' : '编辑商品' }} </span>
        </div>
      </template>
      <template #extra>
        <div class="flex items-center">
          <el-button type="primary" class="ml-2">保存</el-button>
        </div>
      </template>
    </el-page-header>

    <el-card class="box-card">
      <template #header>
        <el-steps
            class="mb-4"
            :space="200"
            :active="currentStep"
            simple
        >
          <el-step title="基本信息" :icon="EditPen" @click="currentStep = 0"/>
          <el-step title="货物封面" :icon="Picture" @click="currentStep = 1"/>
          <el-step title="货物描述" :icon="Tickets" @click="currentStep = 2"/>
        </el-steps>
      </template>
      <el-form :model="GoodForm" label-width="120px" size="large">
        <!-- 基本信息 -->
        <div v-show="currentStep === 0">
          <el-form-item label="货物名称">
            <el-input v-model="GoodForm.goodName" placeholder="请输入货物名称" maxlength="50" show-word-limit/>
          </el-form-item>
          <el-form-item label="仓库">
            <el-select v-model="GoodForm.warehouseId" placeholder="请选择仓库"></el-select>
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="GoodForm.category" placeholder="请选择分类"></el-select>
          </el-form-item>
          <el-form-item label="规格">
            <el-input v-model="GoodForm.standard" placeholder="请输入规格" />
          </el-form-item>
          <el-form-item label="价格">
            <el-input-number v-model="GoodForm.price" placeholder="请输入价格" :precision="2" :step="1" :min='0'/>
          </el-form-item>
        </div>
        <!-- 货物封面 -->
        <div v-show="currentStep === 1">
          <el-form-item label="展示图片">
            <el-upload
              class="avatar-uploader"
              action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15" 
              :headers="getAccessToken()"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
            >
              <img v-if="imageUrl" :src="imageUrl" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>
        </div>
        <!-- 货物描述 -->
        <div v-show="currentStep === 2"><h1>货物描述</h1></div>
        <div class="form-bottom-btn">
          <el-button type="primary" v-show="currentStep !== 0" plain @click="currentStep = currentStep - 1">上一步</el-button>
          <el-button type="primary" v-show="currentStep !== 2" plain @click="currentStep = currentStep + 1">下一步</el-button>
          <el-button type="primary" v-show="currentStep === 2" plain @click="">保存</el-button>
        </div>
      </el-form>
    </el-card>

  </div>
</template>

<style scoped lang="less">
.flex {
  display: flex;
}

.items-center {
  align-items: center;
}

.mr-3 {
  margin-right: 12px;
}

.ml-2 {
  margin-left: 8px;
}

.text-large {
  font-size: 18px;
}

.font-600 {
  font-weight: 600;
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
  margin-top: 10px;
}
.el-form{
  padding: 0 180px 0 80px;
}

.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>