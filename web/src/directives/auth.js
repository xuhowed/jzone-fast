import { usePermissionStore } from '@/store/permission'

export default {
  mounted(el, binding) {
    const perms = Array.isArray(binding.value) ? binding.value : [binding.value]
    if (!perms || !perms.length || !perms[0]) return
    const permissionStore = usePermissionStore()
    const hasAny = perms.some(p => permissionStore.hasPermission(p))
    if (!hasAny) {
      el.parentNode?.removeChild(el)
    }
  }
}
