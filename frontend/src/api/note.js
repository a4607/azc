import request from '@/utils/request'

export function getNotes(params) {
  return request({
    url: '/notes/list',
    method: 'get',
    params
  })
}

export function getNoteById(id) {
  return request({
    url: `/notes/${id}`,
    method: 'get'
  })
}

export function createNote(data) {
  return request({
    url: '/notes',
    method: 'post',
    data
  })
}

export function updateNote(id, data) {
  return request({
    url: `/notes/${id}`,
    method: 'put',
    data
  })
}

export function deleteNote(id) {
  return request({
    url: `/notes/${id}`,
    method: 'delete'
  })
}

export function searchNotes(keyword) {
  return request({
    url: '/notes/search',
    method: 'get',
    params: { keyword }
  })
}

export function getNotesByTag(tagId) {
  return request({
    url: '/notes/tag',
    method: 'get',
    params: { tagId }
  })
}

// 获取公开笔记列表
export function getPublicNotes(params) {
  return request({
    url: '/notes/public',
    method: 'get',
    params
  })
}

// 通过分享码获取笔记
export function getNoteByShareCode(shareCode) {
  return request({
    url: `/notes/share/${shareCode}`,
    method: 'get'
  })
}

// 更新笔记公开状态
export function updateNotePublicStatus(id, isPublic) {
  return request({
    url: `/notes/${id}/public`,
    method: 'put',
    params: { isPublic }
  })
}
