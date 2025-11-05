import request from './request'

export const login = (data) => request.post('/account/login', data)
export const register = (data) => request.post('/account/register', data)
export const refresh = () => request.post('/account/refresh')