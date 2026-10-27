import request from '@/utils/request'

export const pageRole      = (params) => request.get('/sys/role/page', { params })
export const listRole      = ()       => request.get('/sys/role/list')
export const getRoleById   = (id)     => request.get(`/sys/role/${id}`)
export const addRole       = (data)   => request.post('/sys/role', data)
export const editRole      = (data)   => request.put('/sys/role', data)
export const deleteRole    = (id)     => request.delete(`/sys/role/${id}`)
export const assignMenu    = (roleId, menuIds) => {
  const id = typeof roleId === 'object' ? roleId.roleId : roleId
  const menus = typeof roleId === 'object' ? roleId.menuIds : menuIds
  return request.put(`/sys/role/${id}/menus`, { menuIds: menus })
}
export const getRoleMenuIds= (id)     => request.get(`/sys/role/${id}/menus`)
