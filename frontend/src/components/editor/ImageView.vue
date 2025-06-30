<template>
  <node-view-wrapper as="div" class="image-view-wrapper" :style="wrapperStyle">
    <img :src="imageSrc" :alt="node.attrs.alt" :title="node.attrs.title" class="re-resizable-img" ref="imgRef" />
    <div v-for="handle in handles" :key="handle" :class="['resize-handle', handle]"
      @mousedown.prevent="onMouseDown(handle, $event)"></div>
  </node-view-wrapper>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { NodeViewWrapper, nodeViewProps } from '@tiptap/vue-3'

const props = defineProps(nodeViewProps)

const imageSrc = ref(props.node.attrs.src)

// Watch for src changes and update the local ref
watch(() => props.node.attrs.src, (newSrc) => {
  imageSrc.value = newSrc
})

const handles = ['top-left', 'top-right', 'bottom-left', 'bottom-right', 'top', 'bottom', 'left', 'right']

const imgRef = ref(null)
const resizing = ref(false)
const resizeState = reactive({
  handle: '',
  startX: 0,
  startY: 0,
  startWidth: 0,
  startHeight: 0,
  aspectRatio: 1,
  maxWidth: 0,
})

const wrapperStyle = computed(() => ({
  width: props.node.attrs.width ? `${props.node.attrs.width}px` : 'auto',
  height: props.node.attrs.height ? `${props.node.attrs.height}px` : 'auto',
}))

const onMouseDown = (handle, event) => {
  resizing.value = true
  resizeState.handle = handle
  resizeState.startX = event.clientX
  resizeState.startY = event.clientY

  const { width, height } = imgRef.value.getBoundingClientRect()
  resizeState.startWidth = width
  resizeState.startHeight = height
  resizeState.aspectRatio = width / height

  // Get the width of the editor's content area as the max width
  const editorContentDOM = props.editor.view.dom
  const editorRect = editorContentDOM.getBoundingClientRect()
  // Subtract some padding to prevent the image from touching the edge
  resizeState.maxWidth = editorRect.width - 20

  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
}

const onMouseMove = (event) => {
  if (!resizing.value) return

  let newWidth = resizeState.startWidth
  let newHeight = resizeState.startHeight
  const deltaX = event.clientX - resizeState.startX
  const deltaY = event.clientY - resizeState.startY

  if (resizeState.handle.includes('right')) {
    newWidth = resizeState.startWidth + deltaX
  }
  if (resizeState.handle.includes('left')) {
    newWidth = resizeState.startWidth - deltaX
  }
  if (resizeState.handle.includes('bottom')) {
    newHeight = resizeState.startHeight + deltaY
  }
  if (resizeState.handle.includes('top')) {
    newHeight = resizeState.startHeight - deltaY
  }

  // Constrain the new width to the max width
  if (newWidth > resizeState.maxWidth) {
    newWidth = resizeState.maxWidth
  }

  // maintain aspect ratio for corner handles
  if (['top-left', 'top-right', 'bottom-left', 'bottom-right'].includes(resizeState.handle)) {
    if (Math.abs(deltaX) > Math.abs(deltaY) || newWidth === resizeState.maxWidth) {
      newHeight = newWidth / resizeState.aspectRatio
    } else {
      newWidth = newHeight * resizeState.aspectRatio
    }
  }

  if (newWidth > 20 && newHeight > 20) { // Prevent image from becoming too small
    props.updateAttributes({
      width: Math.round(newWidth),
      height: Math.round(newHeight),
    })
  }
}

const onMouseUp = () => {
  resizing.value = false
  window.removeEventListener('mousemove', onMouseMove)
  window.removeEventListener('mouseup', onMouseUp)
}
</script>

<style scoped>
.image-view-wrapper {
  position: relative;
  display: inline-block;
  /* Or 'block' if you want images on their own line */
  line-height: 0;
  /* Remove space below image */
}

.image-view-wrapper:hover .resize-handle {
  display: block;
}

.re-resizable-img {
  width: 100%;
  height: 100%;
  display: block;
}

.resize-handle {
  position: absolute;
  width: 10px;
  height: 10px;
  background-color: #409eff;
  border: 1px solid white;
  border-radius: 50%;
  display: none;
  transform: translate(-50%, -50%);
  z-index: 10;
}

.resize-handle.top-left {
  top: 0;
  left: 0;
  cursor: nwse-resize;
}

.resize-handle.top-right {
  top: 0;
  right: 0;
  cursor: nesw-resize;
}

.resize-handle.bottom-left {
  bottom: 0;
  left: 0;
  cursor: nesw-resize;
}

.resize-handle.bottom-right {
  bottom: 0;
  right: 0;
  cursor: nwse-resize;
}

.resize-handle.top {
  top: 0;
  left: 50%;
  cursor: ns-resize;
}

.resize-handle.bottom {
  bottom: 0;
  left: 50%;
  cursor: ns-resize;
}

.resize-handle.left {
  top: 50%;
  left: 0;
  cursor: ew-resize;
}

.resize-handle.right {
  top: 50%;
  right: 0;
  cursor: ew-resize;
}
</style>