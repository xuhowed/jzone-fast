import request from '@/utils/request'

export const getMenuTree   = ()     => request.get('/sys/menu/tree')
export const getUserRoutes = ()     => request.get('/sys/menu/routes')
export const getRouters    = getUserRoutes
export const addMenu       = (data) => request.post('/sys/menu', data)
export const editMenu      = (data) => request.put('/sys/menu', data)
export const deleteMenu    = (id)   => request.delete(`/sys/menu/${id}`)
