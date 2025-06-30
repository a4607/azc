<template>
  <div>
    <el-card>
      <div class="tag-container">
        <!-- 操作栏 -->
        <div class="filter-container">
          <el-button type="primary" @click="handleCreate">
            <el-icon>
              <Plus />
            </el-icon>
            新建标签
          </el-button>
          <el-button type="warning" @click="toggleManage" style="margin-left: 12px;">
            管理
          </el-button>
          <el-button type="danger" v-if="manageMode && multipleSelection.length" @click="handleBatchDelete"
            style="margin-left: 12px;">
            批量删除
          </el-button>
        </div>

        <!-- 标签列表 -->
        <el-table v-loading="loading" :data="tags" style="width: 100%; border-radius: 12px; overflow: hidden;"
          :row-key="row => row.id" @selection-change="handleSelectionChange" @row-click="handleRowClick"
          :expand-row-keys="expandRowKeys">
          <el-table-column type="selection" width="55" v-if="manageMode" />
          <el-table-column type="expand">
            <template #default="{ row }">
              <div v-if="tagNotes[row.id] && tagNotes[row.id].length">
                <el-table :data="tagNotes[row.id]" size="small" style="width: 100%;">
                  <el-table-column prop="title" label="笔记标题" min-width="200" />
                  <el-table-column prop="createTime" label="创建时间" width="180" />
                </el-table>
              </div>
              <div v-else style="color: #aaa; padding: 12px 0;">暂无笔记</div>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="标签名称" min-width="200" />
          <el-table-column prop="noteCount" label="笔记数量" width="120" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handleUpdate(row)">
                编辑
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 标签表单对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
          <el-form ref="formRef" :model="tagForm" :rules="rules" label-width="80px">
            <el-form-item label="标签名称" prop="name">
              <el-input v-model="tagForm.name" placeholder="请输入标签名称" />
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button type="primary" @click="handleSubmit">确定</el-button>
            </div>
          </template>
        </el-dialog>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const store = useStore()
const formRef = ref(null)

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tagForm = ref({
  id: undefined,
  name: ''
})

const rules = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ]
}

const manageMode = ref(false)
const multipleSelection = ref([])
const tagNotes = ref({})
const expandRowKeys = ref([])

const getList = async () => {
  if (!userInfo.value || !userInfo.value.username) {
    console.warn('用户信息未加载完成')
    return
  }
  loading.value = true
  try {
    await store.dispatch('tag/getTags', userInfo.value.username)
  } catch (error) {
    console.error('获取标签列表失败:', error)
    ElMessage.error('获取标签列表失败')
  } finally {
    loading.value = false
  }
}

const tags = computed(() => store.getters.tags)
const userInfo = computed(() => store.getters.userInfo)

watch(userInfo, (val) => {
  if (val && val.username) {
    getList()
  }
}, { immediate: true })

const handleCreate = () => {
  dialogTitle.value = '新建标签'
  tagForm.value = {
    id: undefined,
    name: ''
  }
  dialogVisible.value = true
}

const handleUpdate = (row) => {
  dialogTitle.value = '编辑标签'
  tagForm.value = {
    id: row.id,
    name: row.name
  }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该标签?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await store.dispatch('tag/deleteTag', row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => { })
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (tagForm.value.id) {
          await store.dispatch('tag/updateTag', {
            id: tagForm.value.id,
            data: tagForm.value
          })
          ElMessage.success('更新成功')
        } else {
          await store.dispatch('tag/createTag', tagForm.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        getList()
      } catch (error) {
        console.error('保存标签失败:', error)
        ElMessage.error('保存标签失败')
      }
    }
  })
}

const toggleManage = () => {
  manageMode.value = !manageMode.value
  multipleSelection.value = []
}

const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

const handleBatchDelete = () => {
  if (!multipleSelection.value.length) return
  ElMessageBox.confirm(`确认批量删除选中的${multipleSelection.value.length}个标签？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      for (const tag of multipleSelection.value) {
        await store.dispatch('tag/deleteTag', tag.id)
      }
      ElMessage.success('批量删除成功')
      getList()
      multipleSelection.value = []
      manageMode.value = false
    } catch (error) {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }).catch(() => { })
}

const handleRowClick = async (row) => {
  if (!row.noteCount) return
  // 展开/收起逻辑
  const idx = expandRowKeys.value.indexOf(row.id)
  if (idx > -1) {
    expandRowKeys.value.splice(idx, 1)
    return
  }
  expandRowKeys.value = [row.id]
  if (!tagNotes.value[row.id]) {
    // 假设有API: store.dispatch('note/getNotesByTag', tagId)
    try {
      const res = await store.dispatch('note/getNotesByTag', row.id)
      tagNotes.value[row.id] = (res.data && res.data.items) || []
    } catch (e) {
      tagNotes.value[row.id] = []
    }
  }
}
</script>

<style lang="scss" scoped>
.el-card {
  margin-top: 0;
}

.tag-container {
  padding: 20px;

  .filter-container {
    padding-bottom: 10px;
  }

  .dialog-footer {
    text-align: right;
  }
}
</style>