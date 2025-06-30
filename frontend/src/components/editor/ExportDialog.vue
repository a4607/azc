<template>
  <el-dialog v-model="dialogVisible" title="导出文档" width="500px" :close-on-click-modal="false">
    <div class="export-dialog-content">
      <el-form :model="exportForm" label-width="100px">
        <el-form-item label="导出格式">
          <el-radio-group v-model="exportForm.format">
            <el-radio :value="'pdf'">PDF</el-radio>
            <el-radio :value="'docx'">Word</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="文件名">
          <el-input v-model="exportForm.filename" placeholder="请输入文件名" />
        </el-form-item>

        <template v-if="exportForm.format === 'pdf'">
          <el-form-item label="页面大小">
            <el-select v-model="exportForm.pageSize">
              <el-option label="A4" value="a4" />
              <el-option label="A3" value="a3" />
              <el-option label="Letter" value="letter" />
            </el-select>
          </el-form-item>

          <el-form-item label="页面方向">
            <el-radio-group v-model="exportForm.orientation">
              <el-radio :value="'portrait'">纵向</el-radio>
              <el-radio :value="'landscape'">横向</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="页边距">
            <el-input-number v-model="exportForm.margin" :min="0" :max="50" :step="5" />
            <span class="unit">mm</span>
          </el-form-item>
        </template>

        <template v-if="exportForm.format === 'docx'">
          <el-form-item label="包含图片">
            <el-switch v-model="exportForm.includeImages" />
          </el-form-item>

          <el-form-item label="包含图表">
            <el-switch v-model="exportForm.includeCharts" />
          </el-form-item>
        </template>
      </el-form>
    </div>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleExport" :loading="exporting">
          导出
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import html2pdf from 'html2pdf.js'
import { Document, Packer, Paragraph, TextRun } from 'docx'
import { saveAs } from 'file-saver'

export default {
  name: 'ExportDialog',
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
    content: {
      type: String,
      required: true,
    },
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const dialogVisible = ref(props.modelValue)
    const exporting = ref(false)

    const exportForm = ref({
      format: 'pdf',
      filename: '文档',
      pageSize: 'a4',
      orientation: 'portrait',
      margin: 20,
      includeImages: true,
      includeCharts: true,
    })

    watch(() => props.modelValue, (val) => {
      dialogVisible.value = val
    })

    watch(dialogVisible, (val) => {
      emit('update:modelValue', val)
    })

    const handleExport = async () => {
      if (!exportForm.value.filename) {
        ElMessage.warning('请输入文件名')
        return
      }

      exporting.value = true
      try {
        if (exportForm.value.format === 'pdf') {
          await exportToPDF()
        } else {
          await exportToWord()
        }
        ElMessage.success('导出成功')
        dialogVisible.value = false
      } catch (error) {
        ElMessage.error('导出失败：' + error.message)
      } finally {
        exporting.value = false
      }
    }

    const exportToPDF = async () => {
      const element = document.createElement('div')
      element.innerHTML = props.content

      const opt = {
        margin: exportForm.value.margin,
        filename: `${exportForm.value.filename}.pdf`,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2 },
        jsPDF: {
          unit: 'mm',
          format: exportForm.value.pageSize,
          orientation: exportForm.value.orientation,
        },
      }

      await html2pdf().set(opt).from(element).save()
    }

    const exportToWord = async () => {
      const doc = new Document({
        sections: [{
          properties: {},
          children: [
            new Paragraph({
              children: [
                new TextRun(props.content),
              ],
            }),
          ],
        }],
      })

      const blob = await Packer.toBlob(doc)
      saveAs(blob, `${exportForm.value.filename}.docx`)
    }

    return {
      dialogVisible,
      exporting,
      exportForm,
      handleExport,
    }
  },
}
</script>

<style lang="scss" scoped>
.export-dialog-content {
  .unit {
    margin-left: 8px;
    color: #666;
  }
}
</style>