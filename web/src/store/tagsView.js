import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTagsViewStore = defineStore('tagsView', () => {
  const visitedViews = ref([
    {
      path: '/dashboard',
      fullPath: '/dashboard',
      title: '首页',
      affix: true
    }
  ])

  function addView(view) {
    if (!view || !view.meta?.title || view.meta?.hidden) return
    const isExist = visitedViews.value.some(v => v.path === view.path)
    if (!isExist) {
      visitedViews.value.push({
        path: view.path,
        fullPath: view.fullPath || view.path,
        title: view.meta.title,
        affix: view.path === '/dashboard'
      })
    }
  }

  function delView(view) {
    if (view.affix) return [...visitedViews.value]
    const index = visitedViews.value.findIndex(v => v.path === view.path)
    if (index > -1) {
      visitedViews.value.splice(index, 1)
    }
    return [...visitedViews.value]
  }

  function delOthersViews(view) {
    visitedViews.value = visitedViews.value.filter(v => v.affix || v.path === view.path)
    return [...visitedViews.value]
  }

  function delAllViews() {
    visitedViews.value = visitedViews.value.filter(v => v.affix)
    return [...visitedViews.value]
  }

  function reset() {
    visitedViews.value = [
      {
        path: '/dashboard',
        fullPath: '/dashboard',
        title: '首页',
        affix: true
      }
    ]
  }

  return {
    visitedViews,
    addView,
    delView,
    delOthersViews,
    delAllViews,
    reset
  }
})
