import request from '@/utils/request'

export const pageDictType  = (params) => request.get('/sys/dict/type/page', { params })
export const addDictType   = (data)   => request.post('/sys/dict/type', data)
export const editDictType  = (data)   => request.put('/sys/dict/type', data)
export const deleteDictType= (id)     => request.delete(`/sys/dict/type/${id}`)

export const listDictItems = (dictType) => request.get('/sys/dict/item/list', { params: { dictType } })
export const addDictItem   = (data)     => request.post('/sys/dict/item', data)
export const editDictItem  = (data)     => request.put('/sys/dict/item', data)
export const deleteDictItem= (id)       => request.delete(`/sys/dict/item/${id}`)
