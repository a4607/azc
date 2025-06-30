<template>
  <el-dialog v-model="dialogVisible" title="版本历史" width="800px" :close-on-click-modal="false">
    <div class="history-dialog-content">
      <div class="toolbar">
        <el-input v-model="searchKeyword" placeholder="搜索版本..." prefix-icon="el-icon-search" clearable />
        <el-select v-model="sortBy" placeholder="排序方式">
          <el-option label="按时间降序" value="time-desc" />
          <el-option label="按时间升序" value="time-asc" />
        </el-select>
      </div>

      <el-timeline>
        <el-timeline-item v-for="version in filteredVersions" :key="version.id"
          :timestamp="formatTime(version.timestamp)" :type="version.id === currentVersionId ? 'primary' : ''">
          <div class="version-item">
            <div class="version-header">
              <span class="version-title">{{ version.title }}</span>
              <div class="version-actions">
                <el-button v-if="version.id !== currentVersionId" type="primary" size="small"
                  @click="handleRestore(version)">
                  恢复此版本
                </el-button>
                <el-button type="danger" size="small" @click="handleDelete(version)">
                  删除
                </el-button>
              </div>
            </div>
            <div class="version-preview" v-html="version.preview"></div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </div>
  </el-dialog>
</template>

<script>
import { ref, computed, watch } from 'vue'
import { ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

export default {
  name: 'HistoryDialog',
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
    versions: {
      type: Array,
      default: () => [],
    },
    currentVersionId: {
      type: String,
      default: '',
    },
  },
  emits: ['update:modelValue', 'restore', 'delete'],
  setup(props, { emit }) {
    const dialogVisible = ref(props.modelValue)
    const searchKeyword = ref('')
    const sortBy = ref('time-desc')

    const filteredVersions = computed(() => {
      let result = [...props.versions]

      // 搜索过滤
      if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase()
        result = result.filter(version =>
          version.title.toLowerCase().includes(keyword) ||
          version.preview.toLowerCase().includes(keyword)
        )
      }

      // 排序
      result.sort((a, b) => {
        const timeA = new Date(a.timestamp).getTime()
        const timeB = new Date(b.timestamp).getTime()
        return sortBy.value === 'time-desc' ? timeB - timeA : timeA - timeB
      })

      return result
    })

    const formatTime = (timestamp) => {
      return dayjs(timestamp).format('YYYY-MM-DD HH:mm:ss')
    }

    const handleRestore = (version) => {
      ElMessageBox.confirm(
        '确定要恢复到此版本吗？当前未保存的更改将会丢失。',
        '恢复确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(() => {
        emit('restore', version)
        dialogVisible.value = false
      })
    }

    const handleDelete = (version) => {
      ElMessageBox.confirm(
        '确定要删除此版本吗？此操作不可恢复。',
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        }
      ).then(() => {
        emit('delete', version)
      })
    }

    watch(() => props.modelValue, (val) => {
      dialogVisible.value = val
    })

    watch(dialogVisible, (val) => {
      emit('update:modelValue', val)
    })

    return {
      dialogVisible,
      searchKeyword,
      sortBy,
      filteredVersions,
      formatTime,
      handleRestore,
      handleDelete,
    }
  },
}
</script>

<style lang="scss" scoped>
.history-dialog-content {
  .toolbar {
    display: flex;
    gap: 1em;
    margin-bottom: 1em;

    .el-input {
      flex: 1;
    }

    .el-select {
      width: 150px;
    }
  }

  .version-item {
    .version-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 0.5em;

      .version-title {
        font-weight: bold;
      }

      .version-actions {
        display: flex;
        gap: 0.5em;
      }
    }

    .version-preview {
      padding: 0.5em;
      background-color: #f8f9fa;
      border-radius: 4px;
      max-height: 200px;
      overflow-y: auto;
      font-size: 0.9em;
      color: #666;
    }
  }
}
</style>