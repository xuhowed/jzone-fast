<template>
  <div class="login-container">
    <div class="login-brand">
      <img class="brand-mark" src="@/assets/brand/logo.png" alt="JZone-Fast" />
      <div>
        <h1>JZone-Fast</h1>
        <p>企业级后台快速开发平台</p>
      </div>
    </div>
    <div class="login-box">
      <h2 class="title">账号登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password @keyup.enter="handleLoginClick" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" :loading="loading" :disabled="loading" @click="handleLoginClick">登 录</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 滑块验证码弹窗 -->
    <SliderCaptcha
      ref="sliderCaptchaRef"
      v-model="captchaVisible"
      @success="handleCaptchaSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { usePermissionStore } from '@/store/permission'
import SliderCaptcha from '@/components/SliderCaptcha/index.vue'

const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const formRef = ref()
const sliderCaptchaRef = ref()
const loading = ref(false)
const captchaVisible = ref(false)

const form = reactive({ username: 'admin', password: 'admin' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLoginClick() {
  if (loading.value || captchaVisible.value) return
  await formRef.value.validate()
  captchaVisible.value = true
}

async function handleCaptchaSuccess({ captchaToken, captchaX }) {
  if (loading.value) return
  loading.value = true
  try {
    const res = await userStore.login({
      ...form,
      captchaToken,
      captchaX
    })
    captchaVisible.value = false
    permissionStore.setPermissions(res.permissions)
    router.push('/')
  } catch (err) {
    if (captchaToken) {
      if (isCaptchaError(err)) {
        sliderCaptchaRef.value?.failAndRefresh()
      } else {
        captchaVisible.value = false
      }
    }
  } finally {
    loading.value = false
  }
}

function isCaptchaError(error) {
  return /验证码|滑块/.test(error?.message || '')
}
</script>

<style scoped lang="scss">
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 72px;
  padding: 32px;
  background:
    radial-gradient(circle at 16% 20%, rgba(37, 99, 235, 0.18), transparent 30%),
    radial-gradient(circle at 84% 78%, rgba(22, 163, 74, 0.16), transparent 28%),
    #f6f8fb;
}

.login-brand {
  display: flex;
  max-width: 430px;
  align-items: center;
  gap: 18px;

  .brand-mark {
    width: 64px;
    height: 64px;
    flex: 0 0 64px;
    border-radius: 8px;
    box-shadow: 0 16px 34px rgba(37, 99, 235, 0.24);
  }

  h1 {
    margin: 0;
    color: var(--jz-text);
    font-size: 36px;
    font-weight: 800;
    line-height: 1.15;
  }

  p {
    margin: 10px 0 0;
    color: var(--jz-text-secondary);
    font-size: 16px;
  }
}

.login-box {
  width: 380px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid var(--jz-border);
  box-shadow: var(--jz-shadow-md);
  .title {
    margin: 0 0 28px;
    text-align: left;
    color: var(--jz-text);
    font-size: 22px;
    font-weight: 750;
  }
}

@media (max-width: 900px) {
  .login-container {
    flex-direction: column;
    gap: 28px;
    align-items: stretch;
  }

  .login-brand {
    max-width: none;

    h1 {
      font-size: 28px;
    }
  }

  .login-box {
    width: 100%;
    max-width: 420px;
    margin: 0 auto;
    padding: 28px;
  }
}
</style>
