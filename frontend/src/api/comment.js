import request from '@/utils/request'

// 获取某个笔记的评论列表
export function getCommentsByNoteId(noteId) {
  return request({
    url: `/api/comments/note/${noteId}`,
    method: 'get',
  })
}

// 添加评论
export function addComment(data) {
  return request({
    url: '/api/comments',
    method: 'post',
    data
  })
}

// 删除评论
export function deleteComment(id, userId, isAdmin = false) {
  return request({
    url: `/api/comments/${id}`,
    method: 'delete',
    params: { userId, isAdmin }
  })
}

// 分页获取评论
export function getCommentsByNoteIdPaged(noteId, page, pageSize) {
  return request({
    url: `/api/comments/note/${noteId}/paged`,
    method: 'get',
    params: { page, pageSize }
  })
} 