<template>
  <el-breadcrumb separator="/" class="app-breadcrumb">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path || item.title">
        <span
          v-if="index === breadcrumbs.length - 1 || !item.redirect && !item.path"
          class="no-redirect"
        >
          {{ item.title }}
        </span>
        <a v-else class="redirect-link" @click.prevent="handleLink(item)">
          {{ item.title }}
        </a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePermissionStore } from '@/store/permission'

const route = useRoute()
const router = useRouter()
const permissionStore = usePermissionStore()
const breadcrumbs = ref([])

function getBreadcrumb() {
  const list = [{ path: '/dashboard', title: '首页' }]

  if (route.path === '/dashboard') {
    breadcrumbs.value = list
    return
  }

  // 从 permissionStore.routes 查找父级菜单分类
  let parentTitle = ''
  for (const group of permissionStore.routes) {
    if (group.children && group.children.length) {
      const found = group.children.find(c => c.path === route.path)
      if (found) {
        parentTitle = group.meta?.title || ''
        break
      }
    }
  }

  if (parentTitle) {
    list.push({ path: '', title: parentTitle })
  }

  if (route.meta?.title) {
    list.push({ path: route.path, title: route.meta.title })
  }

  breadcrumbs.value = list
}

function handleLink(item) {
  if (item.path) {
    router.push(item.path)
  }
}

watch(
  () => route.path,
  () => {
    getBreadcrumb()
  },
  { immediate: true }
)
</script>

<style scoped lang="scss">
.app-breadcrumb {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  line-height: 1;

  .no-redirect {
    color: var(--jz-text-secondary, #475569);
    font-weight: 600;
    cursor: default;
  }

  .redirect-link {
    color: var(--jz-text-muted, #94a3b8);
    font-weight: 500;
    cursor: pointer;
    text-decoration: none;
    transition: color 0.18s ease;

    &:hover {
      color: var(--jz-primary, #2563eb);
    }
  }

  :deep(.el-breadcrumb__separator) {
    color: #cbd5e1;
    margin: 0 8px;
  }
}

.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.25s ease;
}

.breadcrumb-enter-from,
.breadcrumb-leave-to {
  opacity: 0;
  transform: translateX(10px);
}
</style>
