import request from '@/utils/request'

export const pageUser         = (params) => request.get('/sys/user/page', { params })
export const getUserById      = (id)     => request.get(`/sys/user/${id}`)
export const addUser          = (data)   => request.post('/sys/user', data)
export const editUser         = (data)   => request.put('/sys/user', data)
export const deleteUser       = (id)     => request.delete(`/sys/user/${id}`)
export const resetPassword    = (id, newPassword) => {
  const userId = typeof id === 'object' ? id.id : id
  const pwd = typeof id === 'object' ? id.newPassword : newPassword
  return request.put(`/sys/user/${userId}/password`, { newPassword: pwd })
}
export const assignRole       = (userId, roleIds) => {
  const id = typeof userId === 'object' ? userId.userId : userId
  const roles = typeof userId === 'object' ? userId.roleIds : roleIds
  return request.put(`/sys/user/${id}/roles`, { roleIds: roles })
}
