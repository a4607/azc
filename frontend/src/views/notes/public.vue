<template>
  <div class="public-notes">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>公开笔记</span>
        </div>
      </template>
      <div class="note-list">
        <div v-for="note in notes" :key="note.id" class="note-card">
          <div class="note-card-header">
            <el-avatar :size="40" :src="note.userId ? `/api/users/avatar/${note.userId}` : defaultAvatar" />
            <div class="note-author-info">
              <span class="note-author">{{ note.author || note.username }}</span>
              <span class="note-time">{{ formatDate(note.createTime) }}</span>
            </div>
          </div>
          <router-link :to="'/notes/share/' + note.shareCode" class="note-card-main-link">
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
          </router-link>
          <div class="note-card-footer">
            <div class="note-meta">
              <el-icon>
                <View />
              </el-icon>
              <span>{{ note.viewCount || 0 }}</span>
              <el-icon>
                <Star />
              </el-icon>
              <span>{{ note.likeCount || 0 }}</span>
              <el-icon>
                <CollectionTag />
              </el-icon>
              <span>{{ note.favoriteCount || 0 }}</span>
              <el-icon>
                <ChatDotRound />
              </el-icon>
              <span>{{ note.commentCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="pagination-container">
        <el-pagination v-model:current-page="listQuery.page" v-model:page-size="listQuery.limit" :total="total"
          :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import dayjs from 'dayjs'
import { View, Star, CollectionTag, ChatDotRound } from '@element-plus/icons-vue'
import defaultAvatar from '@/assets/logo.png'

const store = useStore()
const loading = ref(false)
const notes = ref([])
const total = ref(0)
const listQuery = ref({
  page: 1,
  limit: 10
})

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

const getList = async () => {
  loading.value = true
  try {
    const res = await store.dispatch('note/getPublicNotes', listQuery.value)
    notes.value = res.data
    total.value = res.total
  } catch (error) {
    console.error('获取公开笔记失败:', error)
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

// Generate a clean, single-line abstract from HTML content
const generateAbstract = (html) => {
  if (!html) return ''
  // 1. Replace HTML tags with a space.
  // 2. Replace multiple whitespace chars (including newlines) with a single space.
  // 3. Trim leading/trailing whitespace.
  return html.replace(/<[^>]+>/g, ' ').replace(/\s+/g, ' ').trim()
}

// 提取 HTML 字符串中的第一张图片 src
function extractFirstImageSrc(html) {
  if (!html) return null;
  const match = html.match(/<img[^>]+src=["']([^"'>]+)["'][^>]*>/i);
  return match ? match[1] : null;
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.public-notes {
  .box-card {
    margin-bottom: 20px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .note-list {
    display: flex;
    flex-wrap: wrap;
    gap: 32px 24px;
    margin: 24px 0 0 0;
  }

  .note-card {
    width: 420px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.06);
    padding: 24px 24px 16px 24px;
    margin-bottom: 12px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    transition: box-shadow 0.2s;

    &:hover {
      box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.12);
    }

    .note-card-header {
      display: flex;
      align-items: center;
      margin-bottom: 12px;
    }

    .note-card-content {
      flex: 1;
      min-width: 0;
      padding-top: 0;
      margin-top: 0;
    }

    .note-card-image {
      margin-left: 16px;
      width: 90px;
      height: 90px;
      display: flex;
      align-items: flex-start;
      justify-content: center;

      img {
        max-width: 90px;
        max-height: 90px;
        border-radius: 8px;
        object-fit: cover;
        margin-top: 0;
        display: block;
      }
    }
  }

  .note-card-header {
    display: flex;
    align-items: center;
    margin-bottom: 12px;

    .note-author-info {
      display: flex;
      flex-direction: column;
      margin-left: 12px;

      .note-author {
        font-weight: 600;
        color: #222;
      }

      .note-time {
        font-size: 12px;
        color: #aaa;
      }
    }
  }

  .note-title {
    font-size: 1.1rem;
    color: #222;
    margin-bottom: 8px;
    text-decoration: none;
    display: block;

    &:hover {
      color: #409EFF;
    }
  }

  .note-abstract {
    color: #666;
    font-size: 15px;
    margin-bottom: 16px;
    line-height: 1.6;
    max-height: calc(1.6em * 3);
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    /* Force break long words to wrap correctly within the clamped lines */
    overflow-wrap: break-word;
    word-break: break-all;
    line-break: anywhere;
  }

  .note-card-footer {
    margin-top: 8px;

    .note-meta {
      display: flex;
      align-items: center;
      gap: 18px;
      color: #888;
      font-size: 15px;

      .el-icon {
        font-size: 18px;
        margin-right: 4px;
        vertical-align: middle;
      }

      span {
        margin-right: 8px;
      }
    }
  }

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }

  .note-card-main {
    display: flex;
    flex-direction: row;
    align-items: flex-start;
  }
}
</style>