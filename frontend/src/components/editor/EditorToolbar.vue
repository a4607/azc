<template>
  <div v-if="editor" class="toolbar-wrapper">
    <div class="toolbar">
      <el-button-group>
        <el-button @click="$emit('save')" title="保存">
          <el-icon>
            <Finished />
          </el-icon>
        </el-button>
      </el-button-group>

      <el-button-group>
        <el-button @click="editor.chain().focus().undo().run()" :disabled="!editor.can().undo()" title="撤销 (Ctrl+Z)">
          <el-icon>
            <ArrowLeft />
          </el-icon>
        </el-button>
        <el-button @click="editor.chain().focus().redo().run()" :disabled="!editor.can().redo()" title="重做 (Ctrl+Y)">
          <el-icon>
            <ArrowRight />
          </el-icon>
        </el-button>
        <el-button @click="showHistoryDialog = true" title="历史记录">
          <el-icon>
            <Clock />
          </el-icon>
        </el-button>
      </el-button-group>

      <el-button-group>
        <el-dropdown>
          <el-button>
            <el-icon>
              <CaretBottom />
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="editor.chain().focus().toggleHeading({ level: 1 }).run()">标题
                1</el-dropdown-item>
              <el-dropdown-item @click="editor.chain().focus().toggleHeading({ level: 2 }).run()">标题
                2</el-dropdown-item>
              <el-dropdown-item @click="editor.chain().focus().toggleHeading({ level: 3 }).run()">标题
                3</el-dropdown-item>
              <el-dropdown-item @click="editor.chain().focus().setParagraph().run()">正文</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <el-button @click="editor.chain().focus().toggleBold().run()" :class="{ 'is-active': editor.isActive('bold') }"
          title="加粗 (Ctrl+B)">
          <b>B</b>
        </el-button>
        <el-button @click="editor.chain().focus().toggleItalic().run()"
          :class="{ 'is-active': editor.isActive('italic') }" title="斜体 (Ctrl+I)">
          <i>I</i>
        </el-button>
        <el-button @click="editor.chain().focus().toggleUnderline().run()"
          :class="{ 'is-active': editor.isActive('underline') }" title="下划线 (Ctrl+U)">
          <u>U</u>
        </el-button>
        <el-button @click="editor.chain().focus().toggleStrike().run()"
          :class="{ 'is-active': editor.isActive('strike') }" title="删除线">
          <el-icon>
            <Minus />
          </el-icon>
        </el-button>
        <el-button @click="editor.chain().focus().unsetAllMarks().clearNodes().run()" title="清除格式">
          <Icon icon="mdi:format-clear" width="20" />
        </el-button>
      </el-button-group>

      <el-button-group>
        <el-button @click="editor.chain().focus().toggleBulletList().run()"
          :class="{ 'is-active': editor.isActive('bulletList') }" title="无序列表">
          <el-icon>
            <Expand />
          </el-icon>
        </el-button>
        <el-button @click="editor.chain().focus().toggleOrderedList().run()"
          :class="{ 'is-active': editor.isActive('orderedList') }" title="有序列表">
          <el-icon>
            <Tickets />
          </el-icon>
        </el-button>
        <el-button @click="editor.chain().focus().toggleBlockquote().run()"
          :class="{ 'is-active': editor.isActive('blockquote') }" title="引用">
          <el-icon>
            <ChatLineSquare />
          </el-icon>
        </el-button>
        <el-button @click="editor.chain().focus().toggleCodeBlock().run()"
          :class="{ 'is-active': editor.isActive('codeBlock') }" title="代码块">
          <el-icon>
            <Monitor />
          </el-icon>
        </el-button>
      </el-button-group>

      <el-button-group>
        <el-button @click="editor.chain().focus().setTextAlign('left').run()"
          :class="{ 'is-active': editor.isActive({ textAlign: 'left' }) }" title="左对齐">
          <Icon icon="material-symbols:format-align-left" width="20" />
        </el-button>
        <el-button @click="editor.chain().focus().setTextAlign('center').run()"
          :class="{ 'is-active': editor.isActive({ textAlign: 'center' }) }" title="居中对齐">
          <Icon icon="material-symbols:format-align-center" width="20" />
        </el-button>
        <el-button @click="editor.chain().focus().setTextAlign('right').run()"
          :class="{ 'is-active': editor.isActive({ textAlign: 'right' }) }" title="右对齐">
          <Icon icon="material-symbols:format-align-right" width="20" />
        </el-button>
        <el-button @click="editor.chain().focus().setTextAlign('justify').run()"
          :class="{ 'is-active': editor.isActive({ textAlign: 'justify' }) }" title="两端对齐">
          <Icon icon="material-symbols:format-align-justify" width="20" />
        </el-button>
      </el-button-group>

      <el-button-group>
        <el-button @click="addImage" title="插入图片">
          <el-icon>
            <Picture />
          </el-icon>
        </el-button>
        <el-button @click="showMathDialog = true" title="插入数学公式">
          <el-icon>
            <Plus />
          </el-icon>
        </el-button>
        <el-button @click="showChartDialog = true" title="插入图表">
          <el-icon>
            <PieChart />
          </el-icon>
        </el-button>
      </el-button-group>

      <el-button-group>
        <el-button @click="showExportDialog = true" title="导出">
          <el-icon>
            <Download />
          </el-icon>
        </el-button>
        <el-button @click="showShortcutDialog = true" title="快捷键">
          <el-icon>
            <QuestionFilled />
          </el-icon>
        </el-button>
        <el-popover placement="bottom" width="340" trigger="click">
          <template #reference>
            <el-button title="大纲与统计" circle>
              <el-icon>
                <List />
              </el-icon>
            </el-button>
          </template>
          <div style="max-height: 400px; overflow-y: auto; min-width: 300px;">
            <OutlineView :editor="editor" />
            <div style="margin-top: 12px; border-top: 1px solid #eee; padding-top: 12px;">
              <div style="font-weight: bold; margin-bottom: 8px;">文档统计</div>
              <div class="stats-row">
                <span class="stats-icon"><el-icon>
                    <Document />
                  </el-icon>字数：{{ stats.charCount }}</span>
                <span class="stats-icon"><el-icon>
                    <EditPen />
                  </el-icon>中文：{{ stats.chineseCharCount }}</span>
                <span class="stats-icon"><el-icon>
                    <Edit />
                  </el-icon>英文：{{ stats.englishCharCount }}</span>
                <span class="stats-icon"><el-icon>
                    <Tickets />
                  </el-icon>数字：{{ stats.numberCount }}</span>
                <span class="stats-icon"><el-icon>
                    <Collection />
                  </el-icon>标点：{{ stats.punctuationCount }}</span>
              </div>
            </div>
          </div>
        </el-popover>
      </el-button-group>
    </div>

    <input type="file" ref="fileInput" @change="handleFileChange" accept="image/*" style="display: none" />

    <!-- Dialogs -->
    <HistoryDialog v-model="showHistoryDialog" :editor="editor" />
    <MathDialog v-model="showMathDialog" :editor="editor" />
    <ChartDialog v-model="showChartDialog" :editor="editor" />
    <ExportDialog v-model="showExportDialog" :content="editor.getHTML()" />
    <ShortcutDialog v-model="showShortcutDialog" />
  </div>
</template>

<script setup>
import { defineProps, ref, defineEmits, computed } from 'vue';
import {
  ArrowLeft, ArrowRight, Clock, CaretBottom, Minus, Expand, Tickets,
  ChatLineSquare, Monitor, Picture, Plus, PieChart, Download, QuestionFilled, Finished, List, Document, EditPen, Edit, Collection,
} from '@element-plus/icons-vue';
import HistoryDialog from './HistoryDialog.vue';
import MathDialog from './MathDialog.vue';
import ChartDialog from './ChartDialog.vue';
import ExportDialog from './ExportDialog.vue';
import ShortcutDialog from './ShortcutDialog.vue';
import OutlineView from './OutlineView.vue';
import { Icon } from '@iconify/vue';


const props = defineProps({
  editor: {
    type: Object,
    required: true,
  },
});

const emit = defineEmits(['save']);

const fileInput = ref(null);

const showHistoryDialog = ref(false);
const showMathDialog = ref(false);
const showChartDialog = ref(false);
const showExportDialog = ref(false);
const showShortcutDialog = ref(false);

const addImage = () => {
  fileInput.value.click();
};

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file && file.type.startsWith('image/')) {
    const reader = new FileReader();
    reader.onload = (e) => {
      const src = e.target.result;
      props.editor.chain().focus().setImage({ src }).run();
    };
    reader.readAsDataURL(file);
  }
};

const stats = computed(() => {
  if (!props.editor) return {
    charCount: 0,
    chineseCharCount: 0,
    englishCharCount: 0,
    numberCount: 0,
    punctuationCount: 0
  };
  const content = props.editor.getText();
  return {
    charCount: content.length,
    chineseCharCount: (content.match(/[\u4e00-\u9fa5]/g) || []).length,
    englishCharCount: (content.match(/[a-zA-Z]/g) || []).length,
    numberCount: (content.match(/[0-9]/g) || []).length,
    punctuationCount: (content.match(/[^\u4e00-\u9fa5a-zA-Z0-9\s]/g) || []).length
  };
});

</script>

<style lang="scss" scoped>
.toolbar-wrapper {
  border-bottom: 1px solid #dcdfe6;
  padding: 0.5rem;
}

.toolbar {
  display: flex;
  flex-wrap: nowrap;
  gap: 0.5rem;
  overflow-x: auto;
  padding-bottom: 0.5rem;
  /* for scrollbar */
}

.el-button-group {
  flex-shrink: 0;
}

.el-button.is-active {
  background-color: #ecf5ff;
  color: #409eff;
}

b,
i,
u {
  font-style: normal;
  font-weight: normal;
  width: 24px;
  text-align: center;
}

b {
  font-weight: bold;
}

i {
  font-style: italic;
}

u {
  text-decoration: underline;
}

.stats-row {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 4px;
}

.stats-icon {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
  gap: 4px;
}
</style>