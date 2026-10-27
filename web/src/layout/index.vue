<template>
  <el-container class="layout-wrapper" :class="{ 'is-collapsed': appStore.sidebarCollapsed }">
    <div class="sidebar-mask" @click="appStore.toggleSidebar()"></div>
    <el-aside :width="appStore.sidebarCollapsed ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <img class="logo-mark" src="@/assets/brand/logo.png" alt="JZone-Fast" />
        <span v-if="!appStore.sidebarCollapsed" class="logo-text">JZone-Fast</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="appStore.sidebarCollapsed"
        router
      >
        <template v-for="item in menuRoutes" :key="item.path">
          <el-sub-menu v-if="item.children && item.children.length" :index="item.path">
            <template #title>
              <el-icon v-if="item.meta?.icon"><component :is="item.meta.icon" /></el-icon>
              <span>{{ item.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.path"
              :index="child.path"
            >
              <el-icon v-if="child.meta?.icon"><component :is="child.meta.icon" /></el-icon>
              <span>{{ child.meta?.title }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.path">
            <el-icon v-if="item.meta?.icon"><component :is="item.meta.icon" /></el-icon>
            <span>{{ item.meta?.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container class="main-container" direction="vertical">
      <el-header class="header">
        <div class="header-left">
          <button class="toggle-btn" type="button" aria-label="切换导航" @click="appStore.toggleSidebar()">
            <el-icon>
              <Fold v-if="!appStore.sidebarCollapsed" />
              <Expand v-else />
            </el-icon>
          </button>
          <Breadcrumb />
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar class="user-avatar" :size="28" icon="UserFilled" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 多标签页 -->
      <TagsView />

      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" :key="route.path" v-if="route.meta?.keepAlive" />
          </keep-alive>
          <component :is="Component" :key="route.path" v-if="!route.meta?.keepAlive" />
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'
import { usePermissionStore } from '@/store/permission'
import { useTagsViewStore } from '@/store/tagsView'
import { resetRouter } from '@/router'
import Breadcrumb from './components/Breadcrumb.vue'
import TagsView from './components/TagsView.vue'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const tagsViewStore = useTagsViewStore()

const staticMenuRoutes = [
  { path: '/dashboard', meta: { title: '首页', icon: 'HomeFilled' }, children: [] }
]

const menuRoutes = computed(() => [
  ...staticMenuRoutes,
  ...permissionStore.routes.filter(r => !r.meta?.hidden)
])

async function handleCommand(cmd) {
  if (cmd === 'logout') {
    await userStore.logout()
    tagsViewStore.reset()
    permissionStore.reset()
    resetRouter()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.layout-wrapper {
  position: relative;
  height: 100vh;
  min-width: 0;
  background: var(--jz-bg);
}

.sidebar-mask {
  display: none;
}

.layout-wrapper.is-collapsed .sidebar .logo {
  justify-content: center;
  padding: 0;
}

.sidebar {
  position: relative;
  z-index: 2;
  background:
    linear-gradient(180deg, rgba(37, 99, 235, 0.12) 0%, rgba(22, 163, 74, 0.04) 42%, rgba(17, 24, 39, 0) 100%),
    var(--jz-sidebar);
  box-shadow: 12px 0 26px rgba(15, 23, 42, 0.08);
  transition: width 0.24s ease, transform 0.24s ease;
  overflow: hidden;

  .logo {
    height: 64px;
    display: flex;
    align-items: center;
    gap: 10px;
    justify-content: flex-start;
    padding: 0 18px;
    color: #fff;
    border-bottom: 1px solid rgba(255, 255, 255, 0.08);
    white-space: nowrap;
  }

  .logo-mark {
    width: 30px;
    height: 30px;
    flex: 0 0 30px;
    border-radius: 8px;
    box-shadow: 0 8px 18px rgba(37, 99, 235, 0.28);
  }

  .logo-text {
    font-size: 17px;
    font-weight: 750;
    letter-spacing: 0;
  }

  .el-menu {
    border-right: none;
    background: transparent;

    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      height: 44px;
      margin: 4px 10px;
      border-radius: 8px;
      color: rgba(255, 255, 255, 0.72);
      transition: background-color 0.18s ease, color 0.18s ease;
    }

    :deep(.el-menu-item:hover),
    :deep(.el-sub-menu__title:hover) {
      color: #fff;
      background: var(--jz-sidebar-hover);
    }

    :deep(.el-menu-item.is-active) {
      color: #fff;
      background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
      box-shadow: 0 8px 18px rgba(37, 99, 235, 0.22);
    }

    :deep(.el-sub-menu .el-menu) {
      background: transparent;
    }

    :deep(.el-sub-menu .el-menu-item) {
      min-width: 0;
      padding-left: 44px !important;
      background: transparent;
    }

    :deep(.el-sub-menu .el-menu-item.is-active) {
      color: #fff;
      background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
    }
  }
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  background: rgba(255, 255, 255, 0.92);
  border-bottom: 1px solid var(--jz-border);
  padding: 0 24px;
  backdrop-filter: blur(12px);

  .header-left {
    display: flex;
    min-width: 0;
    align-items: center;
    gap: 14px;
  }

  .toggle-btn {
    display: inline-flex;
    width: 36px;
    height: 36px;
    align-items: center;
    justify-content: center;
    border: 1px solid var(--jz-border);
    border-radius: 8px;
    color: var(--jz-text-secondary);
    background: #fff;
    cursor: pointer;
    transition: color 0.18s ease, border-color 0.18s ease, box-shadow 0.18s ease;

    &:hover {
      color: var(--jz-primary);
      border-color: rgba(37, 99, 235, 0.38);
      box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
    }

    &:active {
      transform: translateY(1px);
    }
  }

  .page-meta {
    min-width: 0;
  }

  .page-title {
    color: var(--jz-text);
    font-size: 17px;
    font-weight: 700;
    line-height: 1.2;
  }

  .page-path {
    margin-top: 3px;
    color: var(--jz-text-muted);
    font-size: 12px;
    line-height: 1.2;
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 6px 8px 6px 6px;
      border: 1px solid transparent;
      border-radius: 999px;
      cursor: pointer;
      transition: background-color 0.18s ease, border-color 0.18s ease;

      &:hover {
        border-color: var(--jz-border);
        background: var(--jz-surface-soft);
      }

      .username {
        max-width: 160px;
        color: var(--jz-text-secondary);
        font-size: 14px;
        font-weight: 550;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .user-avatar {
        color: var(--jz-primary);
        background: var(--jz-primary-light);
      }
    }
  }
}

.main-content {
  min-width: 0;
  background:
    radial-gradient(circle at top left, rgba(37, 99, 235, 0.08), transparent 34%),
    radial-gradient(circle at top right, rgba(22, 163, 74, 0.06), transparent 28%),
    var(--jz-bg);
  padding: 20px;
  overflow: auto;
}

@media (max-width: 900px) {
  .sidebar-mask {
    position: fixed;
    inset: 0;
    z-index: 1;
    display: block;
    background: rgba(15, 23, 42, 0.36);
    opacity: 1;
    transition: opacity 0.2s ease, visibility 0.2s ease;
  }

  .layout-wrapper.is-collapsed .sidebar-mask {
    visibility: hidden;
    opacity: 0;
    pointer-events: none;
  }

  .sidebar {
    position: fixed;
    inset: 0 auto 0 0;
    height: 100vh;
  }

  .layout-wrapper.is-collapsed .sidebar {
    transform: translateX(-64px);
  }

  .header {
    padding: 0 14px;

    .page-path {
      display: none;
    }

    .username {
      display: none;
    }
  }

  .main-content {
    padding: 14px;
  }
}
</style>
