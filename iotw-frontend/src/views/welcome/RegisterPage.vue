<script setup lang="ts">

import {computed, reactive, ref} from "vue";
import {Lock, User, Message, Edit} from "@element-plus/icons-vue";
import router from "@/router";
import {get, post} from "@/net";
import {ElMessage} from "element-plus";

const form = reactive({
  username: '',
  password: '',
  passwordConfirm: '',
  email: '',
  code: ''
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error("请输入用户名"));
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error("用户名不能包含特殊字符, 只能是中/英文"));
  } else {
    callback();
  }

}
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}


const rule = {
  username: [
    {validator: validateUsername, trigger: ['blur', 'change']},
    {min: 2, max: 10, message: '用户名长度必须在2-8个字符之间', trigger: ['blur', 'change']},
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 16, message: '密码的长度必须在6-16个字符之间', trigger: ['blur', 'change']}
  ],
  passwordConfirm: [
    {validator: validatePassword, trigger: ['blur', 'change']},
  ],
  email: [
    {required: true, message: '请输入邮件地址', trigger: 'blur'},
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入获取的验证码', trigger: 'blur'},
  ]
}

const coldTime = ref(0)
const formRef = ref()
const timer = ref(null);

function askCode() {
  if (isEmailValid) {
    coldTime.value = 60
    // 先清除可能存在的旧计时器
    if (timer.value) clearInterval(timer.value);

    get(`/api/auth/ask-code?email=${form.email}&type=register`, () => {
      ElMessage.success(`验证码已发送到邮箱: ${form.email}, 请注意查收`)

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

const isEmailValid = computed(() => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(form.email))

function register() {
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      post(`/api/auth/register`, {...form}, () => {
        ElMessage.success('注册成功，欢迎加入IOTW')
        router.push('/')
      }, (message) => {
        ElMessage.warning(message)
      })
    } else {
      ElMessage.warning('请完整填写注册表单内容')
    }
  })
}

</script>

<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 100px">
      <div style="font-size: 25px;font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color: grey">欢迎注册我们的学习平台，请在下方填写相关信息</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rule" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" maxlength="15" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="passwordConfirm">
          <el-input v-model="form.passwordConfirm" maxlength="20" type="password" placeholder="确认密码">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" minlength="4" type="text" placeholder="邮箱">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row gutter="10" style="width: 100%">
            <el-col :span="17">
              <el-input v-model="form.code" maxlength="6" type="text" placeholder="请输入验证码">
                <template #prefix>
                  <el-icon>
                    <Edit/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="5">
              <el-button type="success" plain @click="askCode"
                         :disabled="!isEmailValid || coldTime > 0">
                {{ coldTime > 0 ? '请稍后 ' + coldTime + ' 秒' : '获取验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
      </el-form>
      <div style="margin-top: 80px">
        <el-button @click="register" style="width: 270px" type="primary" plain>立即注册</el-button>
      </div>
      <div style="margin-top: 20px">
        <span style="font-size: 14px;line-height: 15px;color: grey">已有账号？</span>
        <el-link style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>