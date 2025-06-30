<template>
  <div class="icon-stats-bar">
    <el-popover v-for="item in statItems" :key="item.key" placement="bottom" width="180" trigger="click">
      <template #reference>
        <el-button circle :icon="item.icon" @click="updateStats" class="stat-icon-btn">
          <span class="stat-label">{{ item.label }}</span>
        </el-button>
      </template>
      <div class="popover-content">
        <div class="popover-title">{{ item.label }}</div>
        <div class="popover-value">{{ stats[item.key] }}</div>
      </div>
    </el-popover>
    <el-button type="default" icon="el-icon-refresh" @click="updateStats" :loading="loading" circle
      class="stat-icon-btn" title="刷新" />
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { Document, EditPen, Edit, Tickets, Collection, Refresh } from '@element-plus/icons-vue'

export default {
  name: 'DocumentStats',
  props: {
    editor: {
      type: Object,
      required: true
    }
  },
  setup(props) {
    const loading = ref(false)
    const stats = ref({
      charCount: 0,
      chineseCharCount: 0,
      englishCharCount: 0,
      numberCount: 0,
      punctuationCount: 0
    })

    const statItems = [
      { key: 'charCount', label: '字数', icon: Document },
      { key: 'chineseCharCount', label: '中文', icon: EditPen },
      { key: 'englishCharCount', label: '英文', icon: Edit },
      { key: 'numberCount', label: '数字', icon: Tickets },
      { key: 'punctuationCount', label: '标点', icon: Collection },
    ]

    const updateStats = () => {
      loading.value = true
      const content = props.editor.getText()

      // 统计中文字数
      const chineseChars = content.match(/[\u4e00-\u9fa5]/g) || []
      const chineseCharCount = chineseChars.length

      // 统计英文字数
      const englishChars = content.match(/[a-zA-Z]/g) || []
      const englishCharCount = englishChars.length

      // 统计数字
      const numbers = content.match(/[0-9]/g) || []
      const numberCount = numbers.length

      // 统计标点符号
      const punctuation = content.match(/[^\u4e00-\u9fa5a-zA-Z0-9\s]/g) || []
      const punctuationCount = punctuation.length

      // 总字数（包括空格）
      const charCount = content.length

      stats.value = {
        charCount,
        chineseCharCount,
        englishCharCount,
        numberCount,
        punctuationCount
      }

      loading.value = false
    }

    // 监听编辑器内容变化
    watch(() => props.editor.state.doc.content, () => {
      updateStats()
    }, { deep: true })

    onMounted(() => {
      updateStats()
    })

    return {
      loading,
      stats,
      updateStats,
      statItems
    }
  }
}
</script>

<style scoped>
.icon-stats-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0 12px 0;
  justify-content: flex-start;
  background: #fff;
  border-left: 1px solid #dcdfe6;
  border-bottom: 1px solid #ebeef5;
}

.stat-icon-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  font-size: 20px;
  background: #f5f7fa;
  border: none;
}

.stat-label {
  font-size: 12px;
  color: #606266;
  margin-top: 2px;
}

.popover-content {
  text-align: center;
}

.popover-title {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
}

.popover-value {
  font-size: 24px;
  color: #409eff;
  font-weight: bold;
}
</style>