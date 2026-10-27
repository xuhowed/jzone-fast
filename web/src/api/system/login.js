import request from '@/utils/request'

export const getSliderCaptcha = () => request.get('/sys/captcha/slider')
export const login  = (data)   => request.post('/sys/login', data)
export const logout = ()       => request.post('/sys/logout')
