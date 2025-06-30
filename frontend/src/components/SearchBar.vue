<template>
  <div class="search-bar-wrapper">
    <div class="search-input-container">
      <el-input v-model="keyword" placeholder="搜索教程" class="search-input" @focus="showPanel = true" @input="onInput"
        @keyup.enter.native="handleSearch" clearable @clear="handleClear" ref="inputRef">
        <template #prefix>
          <el-icon>
            <Search />
          </el-icon>
        </template>
      </el-input>
      <div v-if="showPanel" class="search-dropdown-panel" @mousedown.prevent>
        <template v-if="!keyword">
          <div class="search-history-header">
            <span>搜索历史</span>
            <span class="clear-btn" @click="clearHistory">清空</span>
          </div>
          <div class="search-history-list">
            <el-tag v-for="item in historyList" :key="item" class="history-tag" @click="selectHistory(item)"
              effect="plain" size="large" round>
              {{ item.length > 10 ? item.slice(0, 10) + '...' : item }}
            </el-tag>
          </div>
        </template>
        <template v-else>
          <div class="search-group">
            <div class="search-group-title">
              <el-icon>
                <Clock />
              </el-icon>
              <span>我的笔记</span>
            </div>
            <div v-if="loadingMine" class="search-loading">加载中...</div>
            <div v-else-if="mineResults.length === 0" class="search-empty">暂无结果</div>
            <div v-else>
              <div v-for="note in mineResults" :key="note.id" class="search-result-item"
                @click="goToNote(note, 'mine')">
                <div class="result-title">{{ note.title }}</div>
                <el-button type="primary" link size="small" class="jump-btn"
                  @click.stop="goToNote(note, 'mine')">跳转</el-button>
              </div>
            </div>
          </div>
          <div class="search-group">
            <div class="search-group-title">
              <el-icon>
                <EditPen />
              </el-icon>
              <span>公开列表</span>
            </div>
            <div v-if="loadingPublic" class="search-loading">加载中...</div>
            <div v-else-if="publicResults.length === 0" class="search-empty">暂无结果</div>
            <div v-else>
              <div v-for="note in publicResults" :key="note.id" class="search-result-item"
                @click="goToNote(note, 'public')">
                <div class="result-title">{{ note.title }}</div>
                <el-button type="primary" link size="small" class="jump-btn"
                  @click.stop="goToNote(note, 'public')">跳转</el-button>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Clock, EditPen } from '@element-plus/icons-vue'

const store = useStore()
const router = useRouter()
const keyword = ref('')
const showPanel = ref(false)
const inputRef = ref(null)
const historyList = ref([])
const mineResults = ref([])
const publicResults = ref([])
const loadingMine = ref(false)
const loadingPublic = ref(false)
const HISTORY_KEY = 'note_search_history'

const loadHistory = () => {
  const raw = localStorage.getItem(HISTORY_KEY)
  historyList.value = raw ? JSON.parse(raw) : []
}

const saveHistory = (kw) => {
  if (!kw.trim()) return
  let arr = historyList.value.filter(item => item !== kw)
  arr.unshift(kw)
  if (arr.length > 15) arr = arr.slice(0, 15)
  historyList.value = arr
  localStorage.setItem(HISTORY_KEY, JSON.stringify(arr))
}

const clearHistory = () => {
  historyList.value = []
  localStorage.removeItem(HISTORY_KEY)
}

const selectHistory = (item) => {
  keyword.value = item
  showPanel.value = true
  nextTick(() => {
    handleSearch()
  })
}

const handleSearch = () => {
  if (!keyword.value.trim()) return
  saveHistory(keyword.value)
  showPanel.value = false
  // 可选：这里可触发页面跳转或聚合搜索逻辑
  ElMessage.success('搜索：' + keyword.value)
}

const handleClear = () => {
  keyword.value = ''
}

const onInput = () => {
  showPanel.value = true
  if (keyword.value.trim()) {
    fetchResults()
  }
}

const fetchResults = async () => {
  loadingMine.value = true
  loadingPublic.value = true
  try {
    const [mineRes, publicRes] = await Promise.all([
      store.dispatch('note/getNotes', { page: 1, limit: 10, keyword: keyword.value }),
      store.dispatch('note/getPublicNotes', { page: 1, limit: 10, keyword: keyword.value })
    ])
    mineResults.value = mineRes.data || []
    publicResults.value = publicRes.data || []
  } catch (e) {
    mineResults.value = []
    publicResults.value = []
  } finally {
    loadingMine.value = false
    loadingPublic.value = false
  }
}

const goToNote = (note, type) => {
  showPanel.value = false
  if (type === 'mine') {
    router.push(`/notes/edit/${note.id}`)
  } else {
    router.push(`/notes/share/${note.shareCode}`)
  }
}

onMounted(() => {
  loadHistory()
  document.addEventListener('click', onDocClick)
})

const onDocClick = (e) => {
  if (!inputRef.value || !inputRef.value.$el) return
  if (!inputRef.value.$el.contains(e.target)) {
    showPanel.value = false
  }
}

watch(keyword, (val) => {
  if (!val) {
    showPanel.value = true
  } else {
    fetchResults()
  }
})
</script>

<style scoped>
.search-bar-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.search-input-container {
  position: relative;
  width: 340px;
}

.search-input {
  width: 100%;
  border-radius: 8px;
  font-size: 16px;
  background: #f5f6f7;
}

.search-dropdown-panel {
  position: absolute;
  left: 0;
  top: 44px;
  width: 100%;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.10);
  z-index: 99;
  padding: 18px 18px 10px 18px;
  min-height: 60px;
  animation: fadeIn .18s;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-8px);
  }

  to {
    opacity: 1;
    transform: none;
  }
}

.search-history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 15px;
  color: #888;
  margin-bottom: 10px;
}

.clear-btn {
  color: #bcbcbc;
  font-size: 13px;
  cursor: pointer;
  transition: color 0.2s;
}

.clear-btn:hover {
  color: #f56c6c;
}

.search-history-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 10px;
}

.history-tag {
  cursor: pointer;
  font-size: 14px;
  background: #f5f6f7;
  color: #666;
  border: none;
  transition: background 0.2s, color 0.2s;
}

.history-tag:hover {
  background: #e6e8eb;
  color: #222;
}

.search-group {
  margin-bottom: 10px;
}

.search-group-title {
  display: flex;
  align-items: center;
  font-size: 15px;
  color: #888;
  margin-bottom: 10px;
}

.search-loading {
  text-align: center;
  color: #888;
  font-size: 14px;
  margin-bottom: 10px;
}

.search-empty {
  text-align: center;
  color: #888;
  font-size: 14px;
  margin-bottom: 10px;
}

.search-result-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.search-result-item:hover {
  background: #f5f6f7;
}

.result-title {
  font-size: 14px;
  color: #666;
}

.jump-btn {
  padding: 0;
  margin-left: 10px;
  background: none;
  border: none;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s;
}

.jump-btn:hover {
  color: #222;
}
</style>
