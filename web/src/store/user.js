import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/system/login'
import { usePermissionStore } from '@/store/permission'
import { resetRouter } from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  async function login(data) {
    const res = await loginApi(data)
    token.value = res.token
    userInfo.value = res
    localStorage.setItem('token', res.token)
    localStorage.setItem('userInfo', JSON.stringify(res))
    return res
  }

  async function logout() {
    try { await logoutApi() } catch {}
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    const permissionStore = usePermissionStore()
    permissionStore.reset()
    resetRouter()
  }

  return { token, userInfo, login, logout }
})
