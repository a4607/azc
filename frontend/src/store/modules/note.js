import { getNotes, getNoteById, createNote, updateNote, deleteNote, searchNotes, getNotesByTag, getPublicNotes, getNoteByShareCode, updateNotePublicStatus } from '@/api/note'

const state = {
  notes: [],
  currentNote: null,
  total: 0,
  loading: false,
  publicNotes: [],
  publicTotal: 0
}

const mutations = {
  SET_NOTES: (state, notes) => {
    state.notes = notes
  },
  SET_CURRENT_NOTE: (state, note) => {
    state.currentNote = note
  },
  SET_TOTAL: (state, total) => {
    state.total = total
  },
  SET_LOADING: (state, loading) => {
    state.loading = loading
  },
  SET_PUBLIC_NOTES: (state, notes) => {
    state.publicNotes = notes
  },
  SET_PUBLIC_TOTAL: (state, total) => {
    state.publicTotal = total
  }
}

const actions = {
  // 获取笔记列表
  async getNotes({ commit }, params) {
    try {
      const res = await getNotes(params)
      if (res.code === 200) {
        const notesData = res.data
        const totalData = res.total
        console.log('Before committing to store: notesData=', notesData, 'totalData=', totalData);
        commit('SET_NOTES', notesData)
        commit('SET_TOTAL', totalData)
      }
      return res
    } catch (error) {
      console.error('获取笔记列表失败:', error)
      throw error
    }
  },

  // 获取笔记详情
  async getNoteById(_, id) {
    try {
      const res = await getNoteById(id)
      return res
    } catch (error) {
      console.error('获取笔记详情失败:', error)
      throw error
    }
  },

  // 创建笔记
  async createNote(_, data) {
    try {
      const res = await createNote(data)
      return res
    } catch (error) {
      console.error('创建笔记失败:', error)
      throw error
    }
  },

  // 更新笔记
  async updateNote(_, { id, data }) {
    try {
      const res = await updateNote(id, data)
      return res
    } catch (error) {
      console.error('更新笔记失败:', error)
      throw error
    }
  },

  // 删除笔记
  async deleteNote(_, id) {
    try {
      const res = await deleteNote(id)
      return res
    } catch (error) {
      console.error('删除笔记失败:', error)
      throw error
    }
  },

  // 搜索笔记
  searchNotes({ commit }, keyword) {
    return new Promise((resolve, reject) => {
      searchNotes(keyword)
        .then(response => {
          const { data } = response
          commit('SET_NOTES', data.items)
          commit('SET_TOTAL', data.total)
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 按标签获取笔记
  getNotesByTag({ commit }, tagId) {
    return new Promise((resolve, reject) => {
      getNotesByTag(tagId)
        .then(response => {
          const { data } = response
          commit('SET_NOTES', data.items)
          commit('SET_TOTAL', data.total)
          resolve(data)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 获取公开笔记列表
  getPublicNotes({ commit }, params) {
    return new Promise((resolve, reject) => {
      getPublicNotes(params).then(response => {
        if (response.code === 200) {
          commit('SET_PUBLIC_NOTES', response.data || [])
          commit('SET_PUBLIC_TOTAL', response.total || 0)
          resolve(response)
        } else {
          reject(new Error(response.message || '获取公开笔记列表失败'))
        }
      }).catch(error => {
        console.error('获取公开笔记列表失败:', error)
        reject(error)
      })
    })
  },

  // 通过分享码获取笔记
  getNoteByShareCode({ commit }, shareCode) {
    return new Promise((resolve, reject) => {
      getNoteByShareCode(shareCode)
        .then(response => {
          if (response.code === 200) {
            commit('SET_CURRENT_NOTE', response.data)
            resolve(response)
          } else {
            reject(new Error(response.message || '获取笔记失败'))
          }
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 更新笔记公开状态
  updateNotePublicStatus(_, { id, isPublic }) {
    return new Promise((resolve, reject) => {
      updateNotePublicStatus(id, isPublic)
        .then(response => {
          if (response.code === 200) {
            resolve(response.data)
          } else {
            reject(new Error(response.message || '更新笔记公开状态失败'))
          }
        })
        .catch(error => {
          reject(error)
        })
    })
  }
}

const getters = {
  notes: state => state.notes,
  currentNote: state => state.currentNote,
  total: state => state.total,
  loading: state => state.loading,
  publicNotes: state => state.publicNotes,
  publicTotal: state => state.publicTotal
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
