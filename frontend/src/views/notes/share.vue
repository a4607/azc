<template>
  <div class="note-share-container" v-if="note">
    <div class="note-wrapper">
      <h1 class="note-title">{{ note.title }}</h1>

      <div class="note-tags" v-if="note.tags && note.tags.length">
        <el-tag v-for="tag in note.tags" :key="tag.id" class="tag-item">{{ tag.name }}</el-tag>
      </div>

      <p class="author-signature" v-if="note.author">{{ note.author.signature }}</p>

      <div class="separator"></div>

      <div class="note-body" v-html="note.content"></div>

      <!-- Comment Section -->
      <div class="comment-section">
        <h3 class="comment-title">评论</h3>
        <div class="comment-input-area">
          <el-avatar :size="48" :src="currentUserAvatar" class="comment-avatar" />
          <div class="comment-input-wrapper">
            <el-input type="textarea" :rows="3" :placeholder="newCommentPlaceholder" v-model="newComment"
              class="comment-textarea" :disabled="!isLogin" />
            <div class="comment-actions">
              <div class="action-icons">
                <el-button circle title="表情" :disabled="!isLogin">
                  <Icon icon="mdi:emoticon-happy-outline" width="20" />
                </el-button>
                <el-button circle title="@用户" :disabled="!isLogin">
                  <Icon icon="mdi:at" width="20" />
                </el-button>
                <el-button circle title="图片" :disabled="!isLogin">
                  <Icon icon="mdi:image-outline" width="20" />
                </el-button>
              </div>
              <el-button type="primary" @click="postComment" :disabled="!isLogin">发布</el-button>
            </div>
          </div>
        </div>

        <!-- Comment List -->
        <div class="comment-list">
          <div v-if="comments.length === 0" class="empty-comment">暂无评论，快来抢沙发吧~</div>
          <template v-for="item in commentTree" :key="item.id">
            <div class="comment-item">
              <el-avatar :size="32" :src="getCommenterAvatarUrl(item)" class="comment-list-avatar" />
              <div class="comment-content">
                <div class="comment-user">{{ item.username || '用户' + item.userId }}</div>
                <div class="comment-text" v-html="renderWithMentions(item.content)"></div>
                <div class="comment-time">{{ item.createTime }}</div>
                <el-button size="small" text type="primary" @click="setReplyTo(item)" v-if="isLogin">回复</el-button>
                <el-button size="small" text type="danger" @click="handleDeleteComment(item)"
                  v-if="canDelete(item)">删除</el-button>
              </div>
            </div>
            <!-- 专属回复输入框（父评论） -->
            <div v-if="replyTo && replyTo.id === item.id" class="reply-box">
              <el-avatar :size="40" :src="currentUserAvatar" class="reply-avatar" />
              <div class="reply-main">
                <div class="reply-at">回复 @{{ item.username || ('用户' + item.userId) }}：</div>
                <el-input type="textarea" :rows="2" v-model="replyContent" placeholder="请输入回复内容..."
                  class="reply-textarea" />
                <div class="reply-actions">
                  <el-button circle title="表情" class="reply-action-btn">
                    <Icon icon="mdi:emoticon-happy-outline" width="20" />
                  </el-button>
                  <el-button circle title="@用户" class="reply-action-btn">
                    <Icon icon="mdi:at" width="20" />
                  </el-button>
                  <el-button circle title="图片" class="reply-action-btn">
                    <Icon icon="mdi:image-outline" width="20" />
                  </el-button>
                  <el-button size="small" type="primary" @click="postReply">发布</el-button>
                  <el-button size="small" @click="cancelReply">取消</el-button>
                </div>
              </div>
            </div>
            <div v-if="item.children && item.children.length" class="comment-children">
              <div v-for="child in item.children" :key="child.id" class="comment-item child">
                <el-avatar :size="28" :src="getCommenterAvatarUrl(child)" class="comment-list-avatar" />
                <div class="comment-content">
                  <div class="comment-user">{{ child.username || '用户' + child.userId }}</div>
                  <div class="comment-text" v-html="renderWithMentions(child.content)"></div>
                  <div class="comment-time">{{ child.createTime }}</div>
                  <el-button size="small" text type="primary" @click="setReplyTo(child)" v-if="isLogin">回复</el-button>
                  <el-button size="small" text type="danger" @click="handleDeleteComment(child)"
                    v-if="canDelete(child)">删除</el-button>
                </div>
              </div>
              <!-- 专属回复输入框（子评论） -->
              <div v-if="replyTo && item.children.some(child => child.id === replyTo.id)" class="reply-box child-reply">
                <el-avatar :size="36" :src="currentUserAvatar" class="reply-avatar" />
                <div class="reply-main">
                  <div class="reply-at">回复 @{{ replyTo.username || ('用户' + replyTo.userId) }}：</div>
                  <el-input type="textarea" :rows="2" v-model="replyContent" placeholder="请输入回复内容..."
                    class="reply-textarea" />
                  <div class="reply-actions">
                    <el-button circle title="表情" class="reply-action-btn">
                      <Icon icon="mdi:emoticon-happy-outline" width="20" />
                    </el-button>
                    <el-button circle title="@用户" class="reply-action-btn">
                      <Icon icon="mdi:at" width="20" />
                    </el-button>
                    <el-button circle title="图片" class="reply-action-btn">
                      <Icon icon="mdi:image-outline" width="20" />
                    </el-button>
                    <el-button size="small" type="primary" @click="postReply">发布</el-button>
                    <el-button size="small" @click="cancelReply">取消</el-button>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>
  </div>
  <div v-else-if="!loading" class="empty-note">
    <el-empty description="笔记不存在、是私密的或已被删除" />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElTag, ElAvatar, ElInput, ElButton, ElMessageBox } from 'element-plus'
import { Icon } from '@iconify/vue'
import { addComment, getCommentsByNoteId, deleteComment as apiDeleteComment } from '@/api/comment'

const route = useRoute()
const store = useStore()
const loading = ref(false)
const note = ref(null)
const newComment = ref('')
const comments = ref([])
const total = ref(0)

const userInfo = computed(() => store.getters.userInfo)
const isLogin = computed(() => !!userInfo.value && !!userInfo.value.id)
const isAdmin = computed(() => userInfo.value && userInfo.value.role === 'admin')

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const currentUserAvatar = computed(() => {
  if (userInfo.value && userInfo.value.id) {
    return `/api/users/avatar/${userInfo.value.id}?t=${new Date().getTime()}`;
  }
  return defaultAvatar;
});

const getCommenterAvatarUrl = (comment) => {
  if (comment && comment.userId) {
    return `/api/users/avatar/${comment.userId}?t=${new Date().getTime()}`;
  }
  return defaultAvatar;
}

let replyTo = ref(null)
let replyContent = ref('')

const newCommentPlaceholder = computed(() => {
  if (!isLogin.value) return '请先登录后再评论...'
  if (replyTo.value) return `回复 @${replyTo.value.username || ('用户' + replyTo.value.userId)}：`
  return '快来评论吧！'
})

const getNote = async () => {
  const shareCode = route.params.shareCode
  if (!shareCode) return

  loading.value = true
  try {
    const res = await store.dispatch('note/getNoteByShareCode', shareCode)
    if (res.code === 200 && res.data) {
      note.value = res.data
    } else {
      note.value = null
      ElMessage.error(res.message || '无法加载笔记')
    }
  } catch (error) {
    note.value = null
    console.error('获取分享笔记失败:', error)
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  if (!note.value || !note.value.id) return
  const res = await getCommentsByNoteId(note.value.id)
  if (res.code === 200 && res.data) {
    comments.value = res.data
    total.value = res.data.length
  } else {
    comments.value = []
    total.value = 0
  }
}

const postComment = async () => {
  if (!isLogin.value) {
    ElMessage.warning('请先登录后再评论！')
    return
  }
  if (!newComment.value.trim()) {
    ElMessage.warning('评论内容不能为空')
    return
  }
  const data = {
    noteId: note.value.id,
    userId: userInfo.value.id,
    username: userInfo.value.username,
    avatar: userInfo.value.avatar,
    content: newComment.value,
    parentId: replyTo.value ? replyTo.value.id : null
  }
  const res = await addComment(data)
  if (res.code === 200) {
    ElMessage.success('评论成功！')
    newComment.value = ''
    if (!replyTo || typeof replyTo.value === 'undefined') { replyTo = ref(null) }
    replyTo.value = null
    loadComments()
  } else {
    ElMessage.error(res.message || '评论失败')
  }
}

const canDelete = (comment) => {
  return isAdmin.value || (userInfo.value && comment.userId === userInfo.value.id)
}

const handleDeleteComment = (comment) => {
  ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const res = await apiDeleteComment(comment.id, userInfo.value.id, isAdmin.value)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadComments()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  }).catch(() => { })
}

// 递归分组评论为树结构
function buildCommentTree(list) {
  const map = {}
  const roots = []
  list.forEach(item => {
    item.children = []
    map[item.id] = item
  })
  list.forEach(item => {
    if (item.parentId) {
      if (map[item.parentId]) {
        map[item.parentId].children.push(item)
      }
    } else {
      roots.push(item)
    }
  })
  return roots
}

const commentTree = computed(() => buildCommentTree(comments.value))

const renderWithMentions = (text) => {
  if (!text) return '';
  const mentionRegex = /@(\S+)/g;
  return text.replace(mentionRegex, '<a href="#" class="mention-link">@$1</a>');
}

function setReplyTo(val) {
  if (!replyTo || typeof replyTo.value === 'undefined') {
    replyTo = ref(null)
  }
  replyTo.value = val
  replyContent.value = ''
}

function cancelReply() {
  replyTo.value = null
  replyContent.value = ''
}

async function postReply() {
  if (!isLogin.value) {
    ElMessage.warning('请先登录后再评论！')
    return
  }
  if (!replyContent.value.trim()) {
    ElMessage.warning('回复内容不能为空')
    return
  }
  const data = {
    noteId: note.value.id,
    userId: userInfo.value.id,
    username: userInfo.value.username,
    avatar: userInfo.value.avatar,
    content: replyContent.value,
    parentId: replyTo.value ? replyTo.value.id : null
  }
  const res = await addComment(data)
  if (res.code === 200) {
    ElMessage.success('回复成功！')
    replyContent.value = ''
    replyTo.value = null
    loadComments()
  } else {
    ElMessage.error(res.message || '回复失败')
  }
}

onMounted(async () => {
  await getNote()
  await loadComments()
})
</script>

<style lang="scss" scoped>
.note-share-container {
  display: flex;
  justify-content: center;
  padding: 40px 20px;
  background-color: #f7f7f7;
}

.note-wrapper {
  max-width: 800px;
  width: 100%;
  background-color: #fff;
  padding: 40px 60px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.note-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 24px;
  color: #2c3e50;
  line-height: 1.3;
}

.note-tags {
  margin-bottom: 24px;

  .tag-item {
    margin-right: 8px;
    background-color: #ecf5ff;
    border-color: #d9ecff;
    color: #409eff;
  }
}

.author-signature {
  font-size: 1rem;
  color: #888;
  margin-bottom: 24px;
  padding-left: 4px;
  border-left: 3px solid #eee;
}

.separator {
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 32px;
}

.note-body {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #34495e;
  overflow-wrap: break-word;
  word-break: break-all;
  line-break: anywhere;

  // Tiptap 内容样式
  :deep(h1),
  :deep(h2),
  :deep(h3) {
    margin-top: 1.5em;
    margin-bottom: 0.8em;
    font-weight: 600;
  }

  :deep(p) {
    margin-bottom: 1.2em;
    overflow-wrap: break-word;
    word-break: break-all;
    line-break: anywhere;
  }

  :deep(ul),
  :deep(ol) {
    padding-left: 2em;
    margin-bottom: 1.2em;
  }

  :deep(blockquote) {
    border-left: 4px solid #ccc;
    padding-left: 1em;
    margin-left: 0;
    color: #666;
  }

  :deep(pre) {
    background: #f5f5f5;
    padding: 1em;
    border-radius: 4px;
    white-space: pre-wrap;
    word-break: break-all;
  }

  :deep(code) {
    font-family: 'Courier New', Courier, monospace;
    background-color: #f0f0f0;
    padding: 2px 4px;
    border-radius: 3px;
    font-size: 0.9em;
  }

  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 4px;
    margin: 1em 0;
  }
}

.empty-note {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
}

/* Comment Section Styles */
.comment-section {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.comment-title {
  font-size: 1.4rem;
  font-weight: 600;
  margin-bottom: 24px;
}

.comment-input-area {
  display: flex;
}

.comment-avatar {
  margin-right: 16px;
  flex-shrink: 0;
}

.comment-input-wrapper {
  width: 100%;
}

.comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.action-icons {
  display: flex;
  gap: 8px;
}

.empty-comment {
  color: #aaa;
  text-align: center;
  margin: 24px 0;
}

.comment-list {
  margin-top: 32px;
}

.comment-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 24px;
}

.comment-list-avatar {
  margin-right: 12px;
}

.comment-content {
  background: #f7f7f7;
  border-radius: 8px;
  padding: 12px 16px;
  min-width: 0;
  flex: 1;
}

.comment-user {
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.comment-text {
  margin-bottom: 6px;
  word-break: break-all;

  :deep(.mention-link) {
    color: #409eff;
    font-weight: 500;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.comment-time {
  font-size: 12px;
  color: #aaa;
}

.comment-children {
  margin-left: 48px;
  border-left: 2px solid #f0f0f0;
  padding-left: 16px;
}

.comment-item.child {
  margin-bottom: 12px;
}

.reply-box {
  display: flex;
  align-items: flex-start;
  background: #fafbfc;
  border-radius: 10px;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.04);
  padding: 16px 20px 12px 16px;
  margin: 12px 0 18px 44px;

  .reply-avatar {
    margin-right: 14px;
    flex-shrink: 0;
  }

  .reply-main {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 6px;

    .reply-at {
      font-size: 14px;
      color: #888;
      margin-bottom: 2px;
    }

    .reply-textarea {
      width: 100%;
      border-radius: 6px;
      background: #fff;
      font-size: 15px;
    }

    .reply-actions {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-top: 4px;

      .reply-action-btn {
        background: #f4f4f4;
        border: none;
        color: #888;

        &:hover {
          color: #409EFF;
          background: #e6f0fa;
        }
      }
    }
  }
}

.child-reply {
  margin-left: 48px;

  .reply-avatar {
    margin-right: 10px;
  }
}
</style>