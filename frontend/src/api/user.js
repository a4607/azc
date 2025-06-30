import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/users/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/users/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/users/current',
    method: 'get'
  })
}

export function updateUserInfo(data) {
  return request({
    url: '/users/current',
    method: 'put',
    data
  })
}

export function changePassword(data) {
  return request({
    url: '/users/current/password',
    method: 'put',
    data
  })
}

export function uploadAvatar(file) {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: '/users/avatar',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
