<template>
  <div class="tags-view-container">
    <div class="tags-view-wrapper">
      <router-link
        v-for="tag in tagsViewStore.visitedViews"
        :key="tag.path"
        :to="tag.path"
        class="tags-view-item"
        :class="{ active: isActive(tag) }"
      >
        <span class="tag-dot" v-if="isActive(tag)" />
        <span class="tag-title">{{ tag.title }}</span>
        <el-icon
          v-if="!tag.affix"
          class="tag-close-icon"
          @click.prevent.stop="closeSelectedTag(tag)"
        >
          <Close />
        </el-icon>
      </router-link>
    </div>

    <div class="tags-action">
      <el-dropdown trigger="click" @command="handleActionCommand">
        <button type="button" class="action-btn" title="更多操作">
          <el-icon><ArrowDown /></el-icon>
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="refresh">
              <el-icon><Refresh /></el-icon>刷新页面
            </el-dropdown-item>
            <el-dropdown-item command="closeOthers">
              <el-icon><CircleClose /></el-icon>关闭其他
            </el-dropdown-item>
            <el-dropdown-item command="closeAll">
              <el-icon><FolderDelete /></el-icon>关闭所有
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { watch, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTagsViewStore } from '@/store/tagsView'
import { Close, ArrowDown, Refresh, CircleClose, FolderDelete } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const tagsViewStore = useTagsViewStore()

function isActive(tag) {
  return tag.path === route.path
}

function addTags() {
  if (route.name) {
    tagsViewStore.addView(route)
  }
}

function closeSelectedTag(view) {
  const views = tagsViewStore.delView(view)
  if (isActive(view)) {
    toLastView(views)
  }
}

function toLastView(views) {
  const latestView = views.slice(-1)[0]
  if (latestView) {
    router.push(latestView.path)
  } else {
    router.push('/')
  }
}

function handleActionCommand(cmd) {
  const currentView = {
    path: route.path,
    fullPath: route.fullPath,
    title: route.meta?.title || '页面',
    affix: route.path === '/dashboard'
  }

  if (cmd === 'refresh') {
    // 刷新当前页面
    router.replace({ path: '/redirect' + route.fullPath }).catch(() => {
      // 备用刷新逻辑
      window.location.reload()
    })
  } else if (cmd === 'closeOthers') {
    tagsViewStore.delOthersViews(currentView)
    if (!isActive(currentView)) {
      router.push(currentView.path)
    }
  } else if (cmd === 'closeAll') {
    const views = tagsViewStore.delAllViews()
    toLastView(views)
  }
}

watch(
  () => route.path,
  () => {
    addTags()
  }
)

onMounted(() => {
  addTags()
})
</script>

<style scoped lang="scss">
.tags-view-container {
  height: 38px;
  width: 100%;
  background: #ffffff;
  border-bottom: 1px solid var(--jz-border, #e2e8f0);
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.04);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  user-select: none;

  .tags-view-wrapper {
    display: flex;
    align-items: center;
    gap: 6px;
    overflow-x: auto;
    overflow-y: hidden;
    white-space: nowrap;
    scrollbar-width: none;
    &::-webkit-scrollbar {
      display: none;
    }
  }

  .tags-view-item {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    height: 26px;
    line-height: 26px;
    padding: 0 10px;
    border-radius: 4px;
    font-size: 12px;
    color: var(--jz-text-secondary, #475569);
    background: #f1f5f9;
    border: 1px solid transparent;
    text-decoration: none;
    transition: all 0.18s ease;

    &:hover {
      background: #e2e8f0;
      color: var(--jz-text, #1e293b);
    }

    &.active {
      background: var(--el-color-primary-light-9, #eff6ff);
      color: var(--jz-primary, #2563eb);
      border-color: rgba(37, 99, 235, 0.25);
      font-weight: 550;

      .tag-dot {
        display: inline-block;
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background-color: var(--jz-primary, #2563eb);
      }
    }

    .tag-title {
      max-width: 120px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .tag-close-icon {
      font-size: 11px;
      width: 14px;
      height: 14px;
      border-radius: 50%;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      transition: all 0.15s ease;

      &:hover {
        background-color: rgba(0, 0, 0, 0.12);
        color: #ef4444;
      }
    }
  }

  .tags-action {
    flex-shrink: 0;
    margin-left: 8px;

    .action-btn {
      width: 26px;
      height: 26px;
      border-radius: 4px;
      border: 1px solid var(--jz-border, #e2e8f0);
      background: #f8fafc;
      color: var(--jz-text-secondary, #64748b);
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.18s ease;

      &:hover {
        background: #fff;
        color: var(--jz-primary, #2563eb);
        border-color: rgba(37, 99, 235, 0.3);
      }
    }
  }
}
</style>
