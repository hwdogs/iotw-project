<script setup lang="ts">

import {reactive, ref} from "vue";
import {Edit, Lock, Message} from "@element-plus/icons-vue";

const active = ref(0)

const form = reactive({
  email: '',
  code: '',
  password: '',
  passwordConfirm: ''
})
</script>

<template>
  <div style="text-align: center">
    <div style="margin-top: 30px">
      <el-steps :active="active" finish-status="success" align-center>
        <el-step title="验证电子邮件"/>
        <el-step title="重新设定密码"/>
      </el-steps>
    </div>
    <div style="margin: 0 20px" v-if="active === 0">
      <div style="margin-top: 80px">
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <div style="font-size: 14px;color: grey">请输入需要重置密码的电子邮箱地址</div>
      </div>
      <div style="margin-top: 50px">
        <el-form :model="form">
          <el-form-item prop="email">
            <el-input v-model="form.email" type="text" placeholder="电子邮件">
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
                <el-button type="success">
                  获取验证码
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
        </el-form>
      </div>
      <div style="margin-top: 80px">
        <el-button @click="active++" style="width: 270px" type="primary" plain>开始重置密码</el-button>
      </div>
    </div>
    <div style="margin: 0 20px" v-if="active === 1">
      <div style="margin-top: 80px">
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <div style="font-size: 14px;color: grey">请填写新密码, 请务必牢记以防止丢失</div>
      </div>
      <div style="margin-top: 50px">
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
      </div>

      <div style="margin-top: 80px">
        <el-button @click="active++" style="width: 270px" type="warning" plain>立即重置密码</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>