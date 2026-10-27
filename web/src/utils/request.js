import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

const request = axios.create({
  timeout: 10000
})

request.interceptors.request.use(config => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers['Authorization'] = userStore.token
  }
  return config
})

request.interceptors.response.use(
  response => {
    // 文件下载（blob 响应）直接返回原始数据
    if (response.config.responseType === 'blob') {
      return response
    }
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    if (res.code === 401) {
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
      return Promise.reject(new Error(res.msg))
    }
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg))
  },
  error => {
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default request
