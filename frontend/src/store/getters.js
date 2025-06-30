const getters = {
  token: state => state.user.token,
  userInfo: state => state.user.userInfo,
  total: state => {
    console.log('Getter total accessed:', state.note.total)
    return state.note.total
  },
  notes: state => {
    console.log('Getter notes accessed:', state.note.notes)
    return state.note.notes
  },
  currentNote: state => state.note.currentNote,
  categories: state => state.category.categories,
  tags: state => state.tag.tags
}

export default getters 