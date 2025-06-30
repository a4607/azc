<template>
  <el-dialog v-model="dialogVisible" title="插入图表" width="800px" :close-on-click-modal="false" @close="handleClose">
    <div class="chart-dialog-content">
      <el-form :model="chartForm" label-width="100px">
        <el-form-item label="图表类型">
          <el-select v-model="chartForm.type" @change="handleTypeChange">
            <el-option label="折线图" value="line" />
            <el-option label="柱状图" value="bar" />
            <el-option label="饼图" value="pie" />
            <el-option label="散点图" value="scatter" />
          </el-select>
        </el-form-item>

        <el-form-item label="标题">
          <el-input v-model="chartForm.title" />
        </el-form-item>

        <el-form-item label="数据">
          <el-input v-model="chartForm.data" type="textarea" :rows="10" placeholder="输入JSON格式的数据，例如：
{
  'xAxis': ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  'series': [
    {
      'name': '数据1',
      'data': [150, 230, 224, 218, 135, 147, 260]
    }
  ]
}" />
        </el-form-item>
      </el-form>

      <div class="preview">
        <div ref="chartContainer" style="width: 100%; height: 400px;"></div>
      </div>
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
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

export default {
  name: 'ChartDialog',
  props: {
    modelValue: {
      type: Boolean,
      default: false,
    },
  },
  emits: ['update:modelValue', 'insert'],
  setup(props, { emit }) {
    const dialogVisible = ref(props.modelValue)
    const chartContainer = ref(null)
    let chart = null

    const chartForm = ref({
      type: 'line',
      title: '',
      data: JSON.stringify({
        xAxis: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        series: [
          {
            name: '数据1',
            data: [150, 230, 224, 218, 135, 147, 260],
          },
        ],
      }, null, 2),
    })

    watch(() => props.modelValue, (val) => {
      dialogVisible.value = val
      if (val) {
        nextTick(() => {
          initChart()
        })
      }
    })

    watch(dialogVisible, (val) => {
      emit('update:modelValue', val)
    })

    const initChart = () => {
      if (chart) {
        chart.dispose()
      }
      if (chartContainer.value) {
        chart = echarts.init(chartContainer.value)
        updateChart()
      }
    }

    const updateChart = () => {
      if (!chart) return

      try {
        const data = JSON.parse(chartForm.value.data)
        const option = {
          title: {
            text: chartForm.value.title,
          },
          tooltip: {
            trigger: 'axis',
          },
          xAxis: {
            type: 'category',
            data: data.xAxis,
          },
          yAxis: {
            type: 'value',
          },
          series: data.series.map(series => ({
            name: series.name,
            type: chartForm.value.type,
            data: series.data,
          })),
        }

        chart.setOption(option)
      } catch (error) {
        ElMessage.error('数据格式错误')
      }
    }

    const handleTypeChange = () => {
      updateChart()
    }

    const handleInsert = () => {
      try {
        const data = JSON.parse(chartForm.value.data)
        emit('insert', {
          type: chartForm.value.type,
          title: chartForm.value.title,
          data,
        })
        handleClose()
      } catch (error) {
        ElMessage.error('数据格式错误')
      }
    }

    const handleClose = () => {
      dialogVisible.value = false
      if (chart) {
        chart.dispose()
        chart = null
      }
    }

    onMounted(() => {
      if (dialogVisible.value) {
        nextTick(() => {
          initChart()
        })
      }
    })

    onUnmounted(() => {
      if (chart) {
        chart.dispose()
        chart = null
      }
    })

    return {
      dialogVisible,
      chartContainer,
      chartForm,
      handleTypeChange,
      handleInsert,
      handleClose,
    }
  },
}
</script>

<style lang="scss" scoped>
.chart-dialog-content {
  .preview {
    margin-top: 1em;
    padding: 1em;
    background-color: #f8f9fa;
    border-radius: 4px;
  }
}
</style>