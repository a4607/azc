<template>
  <div class="outline-view">
    <div class="outline-header">
      <span>大纲</span>
    </div>
    <div class="outline-content">
      <div v-if="!editor || headings.length === 0" class="empty-outline">
        <el-empty description="暂无内容" />
      </div>
      <div v-else class="outline-list">
        <div v-for="(heading, index) in headings" :key="index" class="outline-item"
          :class="{ active: editor.isActive('heading', { level: heading.level }) }"
          :style="{ paddingLeft: `${(heading.level - 1) * 20}px` }" @click="onHeadingClick(heading)">
          {{ heading.text }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, defineProps } from 'vue'

const props = defineProps({
  editor: {
    type: Object,
    default: null
  }
})

const headings = ref([])

const onHeadingClick = (heading) => {
  if (props.editor) {
    props.editor.chain().focus().scrollIntoView().setTextSelection(heading.pos).run()
  }
}

watch(
  () => props.editor?.state,
  (state) => {
    if (!state) {
      headings.value = []
      return
    }
    const newHeadings = []
    state.doc.descendants((node, pos) => {
      if (node.type.name === 'heading') {
        newHeadings.push({
          level: node.attrs.level,
          text: node.textContent,
          pos
        })
      }
    })
    headings.value = newHeadings
  },
  { deep: true, immediate: true }
)
</script>

<style lang="scss" scoped>
.outline-view {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-right: 1px solid #e0e0e0;

  .outline-header {
    padding: 16px;
    font-size: 16px;
    font-weight: 500;
    border-bottom: 1px solid #e0e0e0;
  }

  .outline-content {
    flex: 1;
    overflow-y: auto;
    padding: 16px;

    .empty-outline {
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .outline-list {
      .outline-item {
        padding: 8px 0;
        cursor: pointer;
        font-size: 14px;
        color: #606266;
        transition: all 0.3s;

        &:hover {
          color: #409EFF;
        }

        &.active {
          color: #409EFF;
          font-weight: 500;
        }
      }
    }
  }
}
</style>