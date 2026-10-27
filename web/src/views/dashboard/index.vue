<template>
  <div class="dashboard">
    <section class="dashboard-hero">
      <div>
        <div class="eyebrow">JZone-Fast Admin</div>
        <h1>欢迎回来，{{ displayName }}</h1>
        <p>统一管理系统用户、角色、菜单、字典与演示业务数据。</p>
      </div>
      <div class="hero-status">
        <span class="status-dot"></span>
        <span>系统运行正常</span>
      </div>
    </section>

    <el-row :gutter="16" class="metric-grid">
      <el-col :xs="24" :sm="12" :lg="6" v-for="card in cards" :key="card.title">
        <div class="metric-card" :style="{ '--accent': card.color, '--accent-soft': card.softColor }">
          <div class="metric-icon">
            <el-icon><component :is="card.icon" /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-title">{{ card.title }}</div>
            <div class="metric-desc">{{ card.desc }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="workbench-row">
      <el-col :xs="24" :lg="15">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>工作概览</span>
              <el-tag type="success">已登录</el-tag>
            </div>
          </template>
          <div class="profile-list">
            <div class="profile-item">
              <span>当前用户</span>
              <strong>{{ displayName }}</strong>
            </div>
            <div class="profile-item">
              <span>角色</span>
              <strong>{{ roleText }}</strong>
            </div>
            <div class="profile-item">
              <span>权限范围</span>
              <strong>按角色授权</strong>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="9">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>设计规范</span>
            </div>
          </template>
          <div class="principle-list">
            <div v-for="item in principles" :key="item">{{ item }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
defineOptions({ name: 'Dashboard' })
import { computed } from 'vue'
import { useUserStore } from '@/store/user'
const userStore = useUserStore()

const displayName = computed(() => userStore.userInfo?.nickname || userStore.userInfo?.username || '管理员')
const roleText = computed(() => userStore.userInfo?.roles?.length ? userStore.userInfo.roles.join(', ') : '暂无角色')

const cards = [
  { title: '用户管理', desc: '账号状态与基础资料', icon: 'User', color: '#2563eb', softColor: '#e8f0ff' },
  { title: '角色管理', desc: '角色权限与职责边界', icon: 'UserFilled', color: '#16a34a', softColor: '#eaf7ef' },
  { title: '菜单管理', desc: '导航结构与按钮权限', icon: 'Menu', color: '#d97706', softColor: '#fff4df' },
  { title: '字典管理', desc: '枚举配置与业务标签', icon: 'Collection', color: '#dc2626', softColor: '#feecec' }
]

const principles = [
  '清晰的信息层级',
  '一致的组件状态',
  '可读优先的内容密度',
  '面向后台操作的响应式布局'
]
</script>

<style scoped lang="scss">
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dashboard-hero {
  display: flex;
  min-height: 148px;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 28px 32px;
  border: 1px solid rgba(37, 99, 235, 0.12);
  border-radius: 8px;
  background:
    linear-gradient(135deg, rgba(37, 99, 235, 0.08), rgba(22, 163, 74, 0.05)),
    #fff;
  box-shadow: var(--jz-shadow-sm);

  .eyebrow {
    margin-bottom: 8px;
    color: var(--jz-primary);
    font-size: 12px;
    font-weight: 750;
    letter-spacing: 0;
    text-transform: uppercase;
  }

  h1 {
    margin: 0;
    color: var(--jz-text);
    font-size: 26px;
    font-weight: 750;
    line-height: 1.25;
  }

  p {
    max-width: 560px;
    margin: 10px 0 0;
    color: var(--jz-text-secondary);
  }
}

.hero-status {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #c8ecd6;
  border-radius: 999px;
  color: #0f7a36;
  background: #effaf3;
  font-weight: 650;
  white-space: nowrap;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--jz-success);
  box-shadow: 0 0 0 4px rgba(22, 163, 74, 0.16);
}

.metric-grid {
  row-gap: 16px;
}

.metric-card {
  display: flex;
  min-height: 118px;
  align-items: center;
  gap: 14px;
  padding: 20px;
  border: 1px solid var(--jz-border);
  border-radius: 8px;
  background: #fff;
  box-shadow: var(--jz-shadow-sm);
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;

  &:hover {
    transform: translateY(-2px);
    border-color: var(--accent);
    box-shadow: var(--jz-shadow-md);
  }
}

.metric-icon {
  display: inline-flex;
  width: 46px;
  height: 46px;
  align-items: center;
  justify-content: center;
  flex: 0 0 46px;
  border-radius: 8px;
  color: var(--accent);
  background: var(--accent-soft);
  font-size: 24px;
}

.metric-title {
  color: var(--jz-text);
  font-size: 16px;
  font-weight: 700;
}

.metric-desc {
  margin-top: 4px;
  color: var(--jz-text-muted);
  font-size: 13px;
}

.workbench-row {
  row-gap: 16px;
}

.profile-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.profile-item {
  min-height: 78px;
  padding: 16px;
  border: 1px solid var(--jz-border);
  border-radius: 8px;
  background: var(--jz-surface-soft);

  span {
    display: block;
    color: var(--jz-text-muted);
    font-size: 12px;
  }

  strong {
    display: block;
    margin-top: 8px;
    color: var(--jz-text);
    font-size: 15px;
    font-weight: 700;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.principle-list {
  display: grid;
  gap: 10px;

  div {
    padding: 12px 14px;
    border-left: 3px solid var(--jz-primary);
    border-radius: 6px;
    color: var(--jz-text-secondary);
    background: var(--jz-surface-soft);
    font-weight: 550;
  }
}

@media (max-width: 900px) {
  .dashboard-hero {
    align-items: flex-start;
    flex-direction: column;
    padding: 22px;

    h1 {
      font-size: 22px;
    }
  }

  .profile-list {
    grid-template-columns: 1fr;
  }
}
</style>
