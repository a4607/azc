<template>
  <div class="edit-layout">
    <div class="main-content">
      <editor-toolbar v-if="editor" :editor="editor" @save="handleSave" />
      <input v-model="noteForm.title" class="title-input" placeholder="请输入标题" />
      <input v-model="noteForm.tags" class="tag-input" placeholder="请输入标签，多个标签用逗号分隔" />
      <div class="editor-container">
        <prose-editor v-if="editor" :editor="editor" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useEditor, EditorContent, VueNodeViewRenderer } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import Table from '@tiptap/extension-table'
import TableRow from '@tiptap/extension-table-row'
import TableCell from '@tiptap/extension-table-cell'
import TableHeader from '@tiptap/extension-table-header'
import CodeBlockLowlight from '@tiptap/extension-code-block-lowlight'
import Link from '@tiptap/extension-link'
import Placeholder from '@tiptap/extension-placeholder'
import TaskList from '@tiptap/extension-task-list'
import TaskItem from '@tiptap/extension-task-item'
import TextAlign from '@tiptap/extension-text-align'
import TextStyle from '@tiptap/extension-text-style'
import Color from '@tiptap/extension-color'
import Highlight from '@tiptap/extension-highlight'
import Underline from '@tiptap/extension-underline'
import { createLowlight } from 'lowlight'
import EditorToolbar from '@/components/editor/EditorToolbar.vue'
import ProseEditor from '@/components/editor/ProseEditor.vue'
import OutlineView from '@/components/editor/OutlineView.vue'
import DocumentStats from '@/components/editor/DocumentStats.vue'
import ImageView from '@/components/editor/ImageView.vue'
import { debounce } from 'lodash'

const store = useStore()
const route = useRoute()
const router = useRouter()

const saving = ref(false)
const autoSaveTimer = ref(null)
const noteForm = ref({
  id: undefined,
  title: '',
  content: '',
  tags: '',
  isPublic: 0
})

const lastSaved = ref(null)
const originalContent = ref('')
const hasChanges = ref(false)
const isSettingContent = ref(false)
const justSaved = ref(false)

const rules = {
  title: [
    { required: true, message: '请输入笔记标题', trigger: 'blur' },
    { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入笔记内容', trigger: 'blur' }
  ]
}

const editor = useEditor({
  content: noteForm.value.content,
  extensions: [
    StarterKit.configure({
      codeBlock: false,
      paragraph: {
        addKeyboardShortcuts() {
          return {
            Backspace: () => {
              const { state, dispatch } = this.editor.view;
              const { $from, empty } = state.selection;
              if (!empty) {
                return false;
              }
              if ($from.parentOffset > 0) {
                return false;
              }
              return this.editor.commands.joinBackward();
            },
          };
        },
      },
    }),
    Image.extend({
      addAttributes() {
        return {
          ...this.parent?.(),
          width: {
            default: 400,
            parseHTML: element => element.getAttribute('width'),
            renderHTML: attributes => {
              if (!attributes.width) {
                return {}
              }
              return { width: attributes.width }
            },
          },
          height: {
            default: null,
            parseHTML: element => element.getAttribute('height'),
            renderHTML: attributes => {
              if (!attributes.height) {
                return {}
              }
              return { height: attributes.height }
            },
          },
        };
      },
      addNodeView() {
        return VueNodeViewRenderer(ImageView);
      },
    }).configure({
      HTMLAttributes: {
        class: 'editor-image',
      },
    }),
    Table.configure({ resizable: true }),
    TableRow,
    TableCell,
    TableHeader,
    CodeBlockLowlight.configure({ lowlight: createLowlight() }),
    Link,
    Placeholder.configure({ placeholder: '开始写作...' }),
    TaskList,
    TaskItem,
    TextAlign.configure({ types: ['heading', 'paragraph'] }),
    TextStyle,
    Color,
    Highlight,
    Underline,
  ],
  editorProps: {
    handlePaste(view, event) {
      const items = event.clipboardData && event.clipboardData.items
      if (items) {
        for (let i = 0; i < items.length; i++) {
          const item = items[i]
          if (item.type.indexOf('image') !== -1) {
            const file = item.getAsFile()
            const reader = new FileReader()
            reader.onload = (evt) => {
              const src = evt.target.result
              if (editor && editor.value) {
                editor.value.chain().focus().setImage({ src }).run()
              }
            }
            reader.readAsDataURL(file)
            event.preventDefault()
            return true
          }
        }
      }
      return false
    }
  },
  onUpdate: ({ editor }) => {
    if (isSettingContent.value) {
      return
    }
    noteForm.value.content = editor.getHTML()
    checkForChanges()
  }
})

// 检查内容是否有变化
const checkForChanges = () => {
  const currentContent = JSON.stringify({
    title: noteForm.value.title,
    content: noteForm.value.content,
    tags: noteForm.value.tags
  })
  hasChanges.value = currentContent !== originalContent.value
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 保存笔记 - 简化为默认私密状态
const handleSave = debounce(async () => {
  if (saving.value) {
    console.warn('A save operation is already in progress. Skipping.')
    return
  }
  saving.value = true

  try {
    if (!noteForm.value.title.trim()) {
      ElMessage.warning('请输入笔记标题')
      return
    }

    if (!noteForm.value.content.trim()) {
      ElMessage.warning('请输入笔记内容')
      return
    }

    const isNewNote = !noteForm.value.id

    // 处理标签：将字符串转换为 tagId 数组
    const tagNames = typeof noteForm.value.tags === 'string' && noteForm.value.tags
      ? noteForm.value.tags.split(',').map(name => name.trim()).filter(Boolean)
      : []

    const tagIds = []
    const allTags = store.getters.tags || []

    for (const name of tagNames) {
      const existingTag = allTags.find(t => t.name === name)
      if (existingTag) {
        tagIds.push(existingTag.id)
      } else {
        // 创建新标签
        try {
          const result = await store.dispatch('tag/createTag', { name })
          if (result && result.code === 200 && result.data) {
            const newTag = result.data
            tagIds.push(newTag.id)
            // 可选：将新标签添加到本地store，避免重复创建
            store.commit('tag/ADD_TAG', newTag)
          }
        } catch (error) {
          console.error(`创建标签'${name}'失败:`, error)
        }
      }
    }

    const saveData = {
      title: noteForm.value.title.trim(),
      content: noteForm.value.content,
      tagIds: tagIds,
      isPublic: noteForm.value.isPublic || 0
    }

    let result
    if (isNewNote) {
      // 创建新笔记
      result = await store.dispatch('note/createNote', saveData)
    } else {
      // 更新现有笔记
      result = await store.dispatch('note/updateNote', {
        id: noteForm.value.id,
        data: saveData
      })
    }

    if (result.code === 200) {
      ElMessage.success(isNewNote ? '创建成功' : '保存成功')

      if (isNewNote && result.data && result.data.id) {
        noteForm.value.id = result.data.id
        // Set a flag to prevent the watcher from re-fetching the note right after creation.
        // The editor content is already the most up-to-date version.
        justSaved.value = true
        // After creation, update the URL to include the new note's ID
        // This prevents the creation of a new note on subsequent saves.
        router.replace({ params: { id: result.data.id } })
      }

      if (editor.value) {
        originalContent.value = editor.value.getHTML()
        hasChanges.value = false
      }

      lastSaved.value = new Date()
      // The call to getNote after saving is removed.
      // The editor's state is the source of truth after a successful save.
      // Reloading from the server was causing the image rendering bug.

    } else {
      ElMessage.error(result.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}, 500)

const getTags = async () => {
  try {
    await store.dispatch('tag/getTags', userInfo.value?.username)
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

const userInfo = computed(() => store.getters.userInfo)
const tags = computed(() => store.getters.tags)
const currentNote = computed(() => store.getters.currentNote)

watch(userInfo, (val) => {
  if (val && val.username) {
    getTags()
  }
}, { immediate: true })

// 监听表单变化
watch([() => noteForm.value.title, () => noteForm.value.tags], () => {
  checkForChanges()
})

const getNote = async (id) => {
  try {
    const result = await store.dispatch('note/getNoteById', id)

    if (result.code === 200 && result.data) {
      const note = result.data
      console.log('Received note data:', note)
      console.log('Note content length:', note.content ? note.content.length : 0)
      console.log('Note content preview:', note.content ? note.content.substring(0, 200) + '...' : 'null')

      noteForm.value = {
        id: note.id,
        title: note.title,
        content: note.content || '',
        tags: Array.isArray(note.tags) ? note.tags.map(tag => tag.name).join(',') : '',
        isPublic: note.isPublic
      }

      await nextTick()
      if (editor && editor.value) {
        const content = note.content || ''

        console.log('Setting content to editor, length:', content.length)
        console.log('Content contains img tag:', content.includes('<img'))

        isSettingContent.value = true

        // Clear content first
        editor.value.commands.clearContent(false)

        // Try a different approach: manually parse and insert content
        if (content.includes('<img')) {
          console.log('Content contains images, using manual parsing approach')

          // Create a temporary div to parse the HTML
          const tempDiv = document.createElement('div')
          tempDiv.innerHTML = content

          // Insert content node by node
          const nodes = Array.from(tempDiv.childNodes)
          for (const node of nodes) {
            if (node.nodeType === Node.TEXT_NODE) {
              // Insert text
              editor.value.commands.insertContent(node.textContent)
            } else if (node.nodeType === Node.ELEMENT_NODE) {
              if (node.tagName === 'IMG') {
                // Insert image using editor's image command
                const src = node.getAttribute('src')
                const width = node.getAttribute('width')
                const height = node.getAttribute('height')
                const alt = node.getAttribute('alt') || ''
                const title = node.getAttribute('title') || ''

                console.log('Inserting image:', { src, width, height, alt, title })
                editor.value.chain().focus().setImage({
                  src,
                  width: width ? parseInt(width) : 400,
                  height: height ? parseInt(height) : null,
                  alt,
                  title
                }).run()
              } else {
                // Insert other HTML elements
                editor.value.commands.insertContent(node.outerHTML)
              }
            }
          }
        } else {
          console.log('Content without images, using setContent method')
          // For regular content, use setContent
          editor.value.commands.setContent(content, false)
        }

        // Update originalContent after setting content
        originalContent.value = editor.value.getHTML()
        checkForChanges()
        isSettingContent.value = false

        console.log('Editor content after setting:', editor.value.getHTML().substring(0, 200) + '...')
      }
    }
  } catch (error) {
    console.error('获取笔记详情失败:', error)
  }
}

const focusEditor = (e) => {
  if (editor && editor.commands) {
    editor.commands.focus()
  }
}

const handleEditorContentMouseDown = (e) => {
  if (editor && editor.value) {
    if (editor.value.isEmpty) {
      editor.value.commands.setContent('<p></p>')
    }
    editor.value.commands.focus()
    e.preventDefault()
  }
}

watch(() => route.params.id, (newId) => {
  // If the route changed because we just saved a new note,
  // the editor content is already up-to-date. Don't re-fetch.
  if (justSaved.value) {
    justSaved.value = false // Reset the flag
    return
  }

  if (newId) {
    // Ensure the editor is ready before setting content
    const checkEditor = () => {
      if (editor.value) {
        getNote(newId);
      } else {
        setTimeout(checkEditor, 50);
      }
    };
    checkEditor();
  } else {
    // Logic for new note (URL has no ID)
    noteForm.value = {
      id: undefined,
      title: '',
      content: '',
      tags: '',
      isPublic: 0
    };
    if (editor.value) {
      editor.value.commands.clearContent();
    }
  }
}, { immediate: true });

onMounted(() => {
  autoSaveTimer.value = setInterval(() => {
    if (saving.value) return
    if (hasChanges.value && noteForm.value.title.trim() && noteForm.value.content.trim()) {
      console.log('自动保存中...')
      handleSave()
    }
  }, 30000)
})

onUnmounted(() => {
  if (editor.value) {
    editor.value.destroy()
  }
  if (autoSaveTimer.value) {
    clearInterval(autoSaveTimer.value)
  }
})
</script>

<style lang="scss">
.edit-layout {
  display: flex;
  height: calc(100vh - 50px);
  background-color: #f9f9f9;
}

.main-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  margin: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  /* Prevent content from spilling out */
}

.title-input,
.tag-input {
  border: none;
  outline: none;
  font-size: 2em;
  font-weight: bold;
  padding: 15px 25px;
  width: 100%;
  box-sizing: border-box;
}

.tag-input {
  font-size: 1em;
  font-weight: normal;
  color: #555;
  padding-top: 0;
}

.editor-container {
  flex-grow: 1;
  padding: 0 25px 25px;
  overflow-y: auto;
  /* Make editor content scrollable */

  .ProseMirror {
    outline: none;
    min-height: 400px;

    /* 
      Forcefully break long words (like long strings of numbers/letters)
      that don't have spaces. This is the ultimate fix for the wrapping issue.
    */
    overflow-wrap: break-word;
    word-break: break-all;
    /* More aggressive wrapping */
    line-break: anywhere;
    /* Modern property for CJK/Western text wrapping */

    p {
      margin: 0;
    }
  }
}
</style>