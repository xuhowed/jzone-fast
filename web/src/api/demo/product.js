import request from '@/utils/request'

export const pageProduct       = (params) => request.get('/demo/product/page', { params })
export const getProductById    = (id)     => request.get(`/demo/product/${id}`)
export const addProduct        = (data)   => request.post('/demo/product', data)
export const editProduct       = (data)   => request.put('/demo/product', data)
export const deleteProduct     = (id)     => request.delete(`/demo/product/${id}`)
export const exportProduct     = (params) => request.get('/demo/product/export', { params, responseType: 'blob' })
export const importProduct     = (formData) => request.post('/demo/product/import', formData, {
  headers: { 'Content-Type': 'multipart/form-data' },
  timeout: 60000
})
export const downloadTemplate  = ()       => request.get('/demo/product/template', { responseType: 'blob' })
