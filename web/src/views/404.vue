<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">404</div>
      <div class="error-title">抱歉，您访问的页面不存在</div>
      <div class="error-desc">请检查您输入的网址是否正确，或者该页面已被移动或删除。</div>
      <div class="error-actions">
        <el-button type="primary" size="large" @click="goHome">返回首页</el-button>
        <el-button size="large" @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goHome() {
  router.push('/')
}

function goBack() {
  router.back()
}
</script>

<style scoped lang="scss">
.error-page {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;

  .error-content {
    text-align: center;
    max-width: 480px;

    .error-code {
      font-size: 110px;
      font-weight: 900;
      line-height: 1;
      background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      margin-bottom: 20px;
      letter-spacing: -2px;
    }

    .error-title {
      font-size: 24px;
      font-weight: 700;
      color: var(--jz-text, #1e293b);
      margin-bottom: 12px;
    }

    .error-desc {
      font-size: 14px;
      color: var(--jz-text-secondary, #64748b);
      line-height: 1.6;
      margin-bottom: 32px;
    }

    .error-actions {
      display: flex;
      justify-content: center;
      gap: 16px;
    }
  }
}
</style>
