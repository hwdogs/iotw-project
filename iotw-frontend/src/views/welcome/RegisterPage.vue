<script setup lang="ts">

import {computed, reactive, ref} from "vue";
import {Lock, User, Message, Edit} from "@element-plus/icons-vue";
import router from "@/router";
import {get} from "@/net";
import {ElMessage} from "element-plus";

const  form = reactive({
  username: '',
  password: '',
  passwordConfirm: '',
  email: '',
  code: ''
})

const rule = {
const coldTime = ref(0)

function askCode() {
  if (isEmailValid) {
    let timer = null;
    coldTime.value = 60

    get(`/api/auth/ask-code?email=${form.email}&type=register`, () => {
      ElMessage.success(`验证码已发送到邮箱${form.email}, 请注意查收`)

      // 先清除可能存在的旧计时器
      if (timer) clearInterval(timer);

      // 启动新计时器并保存ID
      timer = setInterval(() => {
        coldTime.value--;

        // 当倒计时结束时清除计时器[2](@ref)
        if (coldTime.value <= 0) {
          clearInterval(timer);
          coldTime.value = 0; // 可选：重置显示值
        }
      }, 1000);

    }, (message) => {
      ElMessage.warning(message)
      coldTime.value = 0
      clearInterval(timer);
    })
  } else {
    ElMessage.warning('请输入正确的电子邮件!')
  }
}

const isEmailValid = computed(() => /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(form.email))


</script>

<template>
  <div style="text-align: center;margin: 0 20px">
    <div style="margin-top: 100px">
      <div style="font-size: 25px;font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color: grey">欢迎注册我们的学习平台，请在下方填写相关信息</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rule">
        <el-form-item>
          <el-input v-model="form.username" maxlength="10" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.passwordConfirm" maxlength="20" type="password" placeholder="确认密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
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
        <el-button style="width: 270px" type="primary" plain>立即注册</el-button>
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