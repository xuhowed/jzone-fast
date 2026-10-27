<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-code">500</div>
      <div class="error-title">抱歉，系统发生未知异常</div>
      <div class="error-desc">服务器内部发生错误，开发人员已接收到告警，请稍后刷新重试或联系技术支持。</div>
      <div class="error-actions">
        <el-button type="primary" size="large" @click="handleRefresh">刷新重试</el-button>
        <el-button size="large" @click="goHome">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function handleRefresh() {
  window.location.reload()
}

function goHome() {
  router.push('/')
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
      background: linear-gradient(135deg, #ef4444 0%, #b91c1c 100%);
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
