import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserRoutes } from '@/api/system/menu'

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref([])
  const permissions = ref([])

  function setPermissions(perms) {
    permissions.value = perms || []
  }

  function hasPermission(perm) {
    return permissions.value.includes(perm)
  }

  async function generateRoutes() {
    const data = await getUserRoutes()
    const dynamicRoutes = buildRoutes(data || [])
    routes.value = dynamicRoutes
    return dynamicRoutes
  }

  function buildRoutes(routeList) {
    return routeList.map(item => ({
      path: item.path,
      name: item.name,
      component: item.component ? loadComponent(item.component) : undefined,
      meta: item.meta || {},
      children: item.children ? buildRoutes(item.children) : []
    }))
  }

  /**
   * 将嵌套路由扁平化：提取所有有 component 的页面路由，
   * 注册到系统顶层路由，避免 Layout 多层递归嵌套。
   */
  function flattenRoutes(routeList) {
    const result = []
    for (const route of routeList) {
      if (route.component) {
        result.push({ ...route })
      }
      if (route.children && route.children.length) {
        result.push(...flattenRoutes(route.children))
      }
    }
    return result
  }

  function loadComponent(component) {
    const modules = import.meta.glob('/src/views/**/*.vue')
    const key = `/src/views/${component}.vue`
    return modules[key] ? () => modules[key]() : () => import('@/views/404.vue')
  }

  function reset() {
    routes.value = []
    permissions.value = []
  }

  return { routes, permissions, setPermissions, hasPermission, generateRoutes, flattenRoutes, reset }
})
