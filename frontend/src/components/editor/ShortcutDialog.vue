<template>
  <el-dialog v-model="dialogVisible" title="快捷键" width="600px" :close-on-click-modal="false" @close="handleClose">
    <div class="shortcut-list">
      <div v-for="(group, index) in shortcuts" :key="index" class="shortcut-group">
        <h3>{{ group.title }}</h3>
        <div class="shortcut-items">
          <div v-for="(item, idx) in group.items" :key="idx" class="shortcut-item">
            <span class="description">{{ item.description }}</span>
            <span class="keys">
              <template v-for="(key, keyIndex) in item.keys" :key="keyIndex">
                <kbd>{{ key }}</kbd>
                <span v-if="keyIndex < item.keys.length - 1">+</span>
              </template>
            </span>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'

const dialogVisible = ref(false)

const shortcuts = [
  {
    title: '基础操作',
    items: [
      { description: '保存', keys: ['Ctrl', 'S'] },
      { description: '撤销', keys: ['Ctrl', 'Z'] },
      { description: '重做', keys: ['Ctrl', 'Y'] },
      { description: '复制', keys: ['Ctrl', 'C'] },
      { description: '粘贴', keys: ['Ctrl', 'V'] },
      { description: '剪切', keys: ['Ctrl', 'X'] },
      { description: '全选', keys: ['Ctrl', 'A'] }
    ]
  },
  {
    title: '文本格式化',
    items: [
      { description: '加粗', keys: ['Ctrl', 'B'] },
      { description: '斜体', keys: ['Ctrl', 'I'] },
      { description: '下划线', keys: ['Ctrl', 'U'] },
      { description: '删除线', keys: ['Ctrl', 'Shift', 'S'] },
      { description: '标题 1', keys: ['Ctrl', '1'] },
      { description: '标题 2', keys: ['Ctrl', '2'] },
      { description: '标题 3', keys: ['Ctrl', '3'] }
    ]
  },
  {
    title: '列表操作',
    items: [
      { description: '无序列表', keys: ['Ctrl', 'Shift', '8'] },
      { description: '有序列表', keys: ['Ctrl', 'Shift', '7'] },
      { description: '任务列表', keys: ['Ctrl', 'Shift', '9'] }
    ]
  },
  {
    title: '其他操作',
    items: [
      { description: '插入链接', keys: ['Ctrl', 'K'] },
      { description: '插入图片', keys: ['Ctrl', 'Shift', 'I'] },
      { description: '插入表格', keys: ['Ctrl', 'Shift', 'T'] },
      { description: '插入代码块', keys: ['Ctrl', 'Shift', 'C'] }
    ]
  }
]

const handleClose = () => {
  dialogVisible.value = false
}

defineExpose({
  dialogVisible
})
</script>

<style lang="scss" scoped>
.shortcut-list {
  .shortcut-group {
    margin-bottom: 20px;

    h3 {
      margin: 0 0 10px;
      font-size: 16px;
      color: #303133;
    }

    .shortcut-items {
      .shortcut-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 0;
        border-bottom: 1px solid #EBEEF5;

        &:last-child {
          border-bottom: none;
        }

        .description {
          color: #606266;
        }

        .keys {
          display: flex;
          align-items: center;
          gap: 4px;

          kbd {
            display: inline-block;
            padding: 2px 6px;
            font-size: 12px;
            line-height: 1;
            color: #444d56;
            background-color: #fafbfc;
            border: 1px solid #d1d5da;
            border-radius: 3px;
            box-shadow: inset 0 -1px 0 #d1d5da;
          }
        }
      }
    }
  }
}
</style>