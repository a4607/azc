import { getTags, createTag as createTagApi, updateTag, deleteTag } from '@/api/tag'

const state = {
  tags: [],
  loading: false
}

const mutations = {
  SET_TAGS: (state, tags) => {
    state.tags = tags
  },
  SET_LOADING: (state, loading) => {
    state.loading = loading
  },
  ADD_TAG: (state, tag) => {
    state.tags.push(tag)
  }
}

const actions = {
  // 兼容旧用法，提供 getTags action
  async getTags({ commit }, params) {
    try {
      const res = await getTags(params)
      if (res.code === 200) {
        commit('SET_TAGS', res.data)
      }
      return res
    } catch (error) {
      console.error('获取标签失败:', error)
      throw error
    }
  },

  // 获取标签列表
  async getTagList(_, params) {
    try {
      const res = await getTags(params)
      return res
    } catch (error) {
      console.error('获取标签列表失败:', error)
      throw error
    }
  },

  // 创建标签
  async createTag(_, data) {
    try {
      const res = await createTagApi(data)
      return res
    } catch (error) {
      console.error('创建标签失败:', error)
      throw error
    }
  },

  // 更新标签
  updateTag(_, { id, tagData }) {
    return new Promise((resolve, reject) => {
      updateTag(id, tagData)
        .then(response => {
          resolve(response)
        })
        .catch(error => {
          reject(error)
        })
    })
  },

  // 删除标签
  deleteTag(_, id) {
    return new Promise((resolve, reject) => {
      deleteTag(id)
        .then(response => {
          resolve(response)
        })
        .catch(error => {
          reject(error)
        })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
