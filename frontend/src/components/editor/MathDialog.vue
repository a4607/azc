<template>
  <el-dialog v-model="dialogVisible" title="插入数学公式" width="600px" :close-on-click-modal="false" @close="handleClose">
    <div class="math-dialog-content">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="行内公式" name="inline">
          <el-input v-model="inlineMath" type="textarea" :rows="3" placeholder="输入行内公式，例如：E = mc^2"
            @input="updateInlinePreview" />
          <div class="preview" v-html="inlinePreview"></div>
        </el-tab-pane>
        <el-tab-pane label="块级公式" name="block">
          <el-input v-model="blockMath" type="textarea" :rows="5" placeholder="输入块级公式，例如：\frac{d}{dx}e^x = e^x"
            @input="updateBlockPreview" />
          <div class="preview" v-html="blockPreview"></div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleInsert">插入</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { ref, watch } from 'vue'
import katex from 'katex'
import { ElMessage } from 'element-plus'

export default {
  name: 'MathDialog',
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['update:modelValue', 'insert'],
  setup(props, { emit }) {
    const dialogVisible = ref(props.modelValue)
    const activeTab = ref('inline')
    const inlineMath = ref('')
    const blockMath = ref('')
    const inlinePreview = ref('')
    const blockPreview = ref('')

    watch(() => props.modelValue, (val) => {
      dialogVisible.value = val
      if (val) {
        inlineMath.value = ''
        blockMath.value = ''
        inlinePreview.value = ''
        blockPreview.value = ''
      }
    })

    watch(dialogVisible, (val) => {
      emit('update:modelValue', val)
    })

    const updateInlinePreview = () => {
      try {
        inlinePreview.value = katex.renderToString(inlineMath.value, {
          displayMode: false,
          throwOnError: false,
        })
      } catch (error) {
        inlinePreview.value = '公式预览错误'
      }
    }

    const updateBlockPreview = () => {
      try {
        blockPreview.value = katex.renderToString(blockMath.value, {
          displayMode: true,
          throwOnError: false,
        })
      } catch (error) {
        blockPreview.value = '公式预览错误'
      }
    }

    const handleInsert = () => {
      if (activeTab.value === 'inline') {
        if (!inlineMath.value) {
          ElMessage.warning('请输入行内公式')
          return
        }
        emit('insert', {
          type: 'inline',
          content: inlineMath.value,
        })
      } else {
        if (!blockMath.value) {
          ElMessage.warning('请输入块级公式')
          return
        }
        emit('insert', {
          type: 'block',
          content: blockMath.value,
        })
      }
      handleClose()
    }

    const handleClose = () => {
      dialogVisible.value = false
    }

    return {
      dialogVisible,
      activeTab,
      inlineMath,
      blockMath,
      inlinePreview,
      blockPreview,
      updateInlinePreview,
      updateBlockPreview,
      handleInsert,
      handleClose,
    }
  },
}
</script>

<style lang="scss" scoped>
.math-dialog-content {
  .preview {
    margin-top: 1em;
    padding: 1em;
    background-color: #f8f9fa;
    border-radius: 4px;
    min-height: 2em;
  }
}
</style>