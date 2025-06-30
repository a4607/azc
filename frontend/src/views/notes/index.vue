<template>
  <div class="notes-container">
    <div class="toolbar">
      <el-button class="filter-item" type="primary" @click="handleCreate">
        <el-icon>
          <Plus />
        </el-icon>
        新建笔记
      </el-button>
    </div>

    <div v-loading="loading" class="note-list">
      <div v-for="note in notes" :key="note.id" class="note-card">
        <div class="note-card-header">
          <span class="note-time">{{ formatDate(note.createTime) }}</span>
          <el-dropdown trigger="click" @command="(command) => handleCommand(command, note)">
            <span class="el-dropdown-link">
              <el-icon>
                <More />
              </el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit">编辑</el-dropdown-item>
                <el-dropdown-item command="delete">删除</el-dropdown-item>
                <el-dropdown-item command="share">{{ note.isPublic === 1 ? '设为私密' : '设为公开' }}</el-dropdown-item>
                <el-dropdown-item v-if="note.isPublic === 1" command="copy">复制分享链接</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div class="note-card-main-link" @click="handleUpdate(note)">
          <div class="note-card-main">
            <div class="note-card-content">
              <div class="note-title">
                <b>{{ note.title }}</b>
              </div>
              <div class="note-abstract">{{ generateAbstract(note.content) }}</div>
            </div>
            <div class="note-card-image" v-if="extractFirstImageSrc(note.content)">
              <img :src="extractFirstImageSrc(note.content)" alt="笔记图片" />
            </div>
          </div>
        </div>

      </div>
      <div v-if="notes && notes.length === 0 && !loading" class="empty-notes">
        <p>暂无笔记，快去写一篇吧~</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination v-model:current-page="listQuery.page" v-model:page-size="listQuery.limit" :total="total"
        :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, More } from '@element-plus/icons-vue'
import dayjs from 'dayjs'

const store = useStore()
const router = useRouter()

const listQuery = ref({
  page: 1,
  limit: 10
})

const loading = ref(false)
const notes = computed(() => store.getters.notes)
const total = computed(() => store.getters.total)

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const generateAbstract = (html) => {
  if (!html) return ''
  return html.replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

function extractFirstImageSrc(html) {
  if (!html) return null;
  const match = html.match(/<img[^>]+src=["']([^"'>]+)["'][^>]*>/i);
  return match ? match[1] : null;
}

const getList = async () => {
  loading.value = true
  try {
    await store.dispatch('note/getNotes', listQuery.value)
  } catch (error) {
    console.error('获取笔记列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSizeChange = (val) => {
  listQuery.value.limit = val
  getList()
}

const handleCurrentChange = (val) => {
  listQuery.value.page = val
  getList()
}

const handleCreate = () => {
  router.push('/notes/create')
}

const handleUpdate = (row) => {
  router.push(`/notes/edit/${row.id}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该笔记?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await store.dispatch('note/deleteNote', row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => { })
}

const copyShareLink = (shareCode) => {
  const shareLink = `${window.location.origin}/notes/share/${shareCode}`
  navigator.clipboard.writeText(shareLink).then(() => {
    ElMessage.success('分享链接已复制到剪贴板！')
  }).catch(err => {
    console.error('复制失败:', err)
    ElMessage.error('复制分享链接失败，请手动复制。')
  })
}

const handlePublicChange = (row) => {
  const newIsPublic = row.isPublic === 1 ? 0 : 1
  store.dispatch('note/updateNotePublicStatus', {
    id: row.id,
    isPublic: newIsPublic
  }).then(() => {
    ElMessage.success(newIsPublic === 1 ? '已设置为公开' : '已设置为私密')
    getList()
  }).catch(() => {
    ElMessage.error('设置失败')
  })
}

const handleCommand = (command, note) => {
  switch (command) {
    case 'edit':
      handleUpdate(note)
      break
    case 'delete':
      handleDelete(note)
      break
    case 'share':
      handlePublicChange(note)
      break
    case 'copy':
      copyShareLink(note.shareCode)
      break
  }
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.notes-container {
  padding: 20px;

  .toolbar {
    margin-bottom: 20px;
  }
}

.note-list {
  display: flex;
  flex-wrap: wrap;
  gap: 32px 24px;
}

.note-card {
  width: 420px;
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.06);
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.2s;
  cursor: pointer;

  &:hover {
    box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.12);
  }
}

.note-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .note-time {
    font-size: 13px;
    color: #aaa;
  }

  .el-dropdown-link {
    cursor: pointer;
    font-size: 18px;
    color: #999;

    &:hover {
      color: #409EFF;
    }
  }
}

.note-card-main-link {
  text-decoration: none;
  color: inherit;
}

.note-card-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.note-card-content {
  flex: 1;
  min-width: 0;
}

.note-title {
  font-size: 1.1rem;
  color: #222;
  margin-bottom: 8px;
}

.note-abstract {
  color: #666;
  font-size: 15px;
  margin-bottom: 16px;
  line-height: 1.6;
  /* Clamp to 3 lines */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
  /* Force break long words */
  overflow-wrap: break-word;
  word-break: break-all;
  line-break: anywhere;
}

.note-card-image {
  margin-left: 16px;
  width: 90px;
  height: 90px;
  flex-shrink: 0;

  img {
    width: 100%;
    height: 100%;
    border-radius: 8px;
    object-fit: cover;
  }
}

.empty-notes {
  width: 100%;
  text-align: center;
  color: #999;
  margin-top: 40px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>