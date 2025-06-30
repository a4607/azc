import { createStore } from 'vuex'
import user from './modules/user'
import note from './modules/note'
import tag from './modules/tag'
import getters from './getters'

export default createStore({
  modules: {
    user,
    note,
    tag
  },
  getters
})
