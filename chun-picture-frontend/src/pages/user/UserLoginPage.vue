<template>
  <div id="userLoginPage">
    <h2 class="title">纯纯云图库 -- 用户登录</h2>
    <div class="desc">企业级智能协同云图库</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码不能小于8位' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder ="请输入密码"/>
      </a-form-item>

      <div class = "tips">
        没有账号?
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>

      <a-form-item :wrapper-col="{ offset: 8, span: 16 }">
        <a-button type="primary" html-type="submit" style="width: 100%">Submit</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script lang="ts" setup>
import { reactive } from 'vue'
import {useRouter} from "vue-router";
import {useLoginUserStore} from "@/stores/useLoginUserStore";
import {userLoginUsingPost} from "@/api/userController";
import {message} from "ant-design-vue";


const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const router = useRouter()
const loginUserStore = useLoginUserStore()

/**
 * 提交表单
 * @param values  values 是在校验通过后由 Form 自动生成并传入的。 form表单里 name的字段 组合成对象
 */
const handleSubmit = async (values: any) => {
  const res = await userLoginUsingPost(values)
  // 登录成功，把登录态保存到全局状态中
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败，' + res.data.message)
  }
}



</script>
<style scoped>
#userLoginPage {
  max-width: 420px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}

</style>
