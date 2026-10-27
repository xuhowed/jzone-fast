import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { usePermissionStore } from '@/store/permission'

const staticRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true }
  },
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/error/403.vue'),
    meta: { hidden: true }
  },
  {
    path: '/500',
    name: '500',
    component: () => import('@/views/error/500.vue'),
    meta: { hidden: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

let dynamicRoutesAdded = false
let dynamicRouteRemovers = []

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const permissionStore = usePermissionStore()

  const whiteList = ['/login', '/403', '/500']
  if (!userStore.token) {
    if (whiteList.includes(to.path)) return next()
    return next('/login')
  }

  if (to.path === '/login') return next('/')

  if (!dynamicRoutesAdded) {
    try {
      permissionStore.setPermissions(userStore.userInfo?.permissions || [])
      const dynamicRoutes = await permissionStore.generateRoutes()
      const flatRoutes = permissionStore.flattenRoutes(dynamicRoutes)
      flatRoutes.forEach(route => dynamicRouteRemovers.push(router.addRoute('Layout', route)))

      // 在动态路由注册完成后添加 404 路由
      dynamicRouteRemovers.push(router.addRoute({
        path: '/:pathMatch(.*)*',
        name: '404',
        component: () => import('@/views/404.vue'),
        meta: { hidden: true }
      }))

      dynamicRoutesAdded = true
      return next({ ...to, replace: true })
    } catch (error) {
      await userStore.logout()
      return next('/login')
    }
  }

  next()
})

export function resetRouter() {
  dynamicRouteRemovers.forEach(removeRoute => removeRoute())
  dynamicRouteRemovers = []
  if (router.hasRoute('404')) {
    router.removeRoute('404')
  }
  dynamicRoutesAdded = false
}

export default router
