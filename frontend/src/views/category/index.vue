<template>
  <div>
    <el-card>
      <div class="category-container">
        <!-- 操作栏 -->
        <div class="filter-container">
          <el-button type="primary" icon="el-icon-plus" @click="handleCreate">
            新建分类
          </el-button>
        </div>

        <!-- 分类列表 -->
        <el-table v-loading="loading" :data="categories" style="width: 100%; border-radius: 12px; overflow: hidden;">
          <el-table-column prop="name" label="分类名称" min-width="200" />
          <el-table-column prop="noteCount" label="笔记数量" width="120" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" align="center" width="150" class-name="small-padding fixed-width">
            <template slot-scope="{row}">
              <el-button type="primary" size="mini" @click="handleUpdate(row)">
                编辑
              </el-button>
              <el-button size="mini" type="danger" @click="handleDelete(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分类表单对话框 -->
        <el-dialog v-if="dialogVisible" :title="dialogTitle" :visible.sync="dialogVisible" width="500px"
          :destroy-on-close="true" @closed="handleDialogClosed">
          <el-form ref="form" :model="categoryForm" :rules="rules" label-width="80px">
            <el-form-item label="分类名称" prop="name">
              <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="handleCancel">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
          </div>
        </el-dialog>
      </div>
    </el-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Category',
  data() {
    return {
      loading: false,
      dialogVisible: false,
      dialogTitle: '',
      categoryForm: {
        id: undefined,
        name: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters([
      'categories'
    ])
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        await this.$store.dispatch('category/getCategories')
      } catch (error) {
        console.error('获取分类列表失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleCreate() {
      this.dialogTitle = '新建分类'
      this.categoryForm = {
        id: undefined,
        name: ''
      }
      this.dialogVisible = true
    },
    handleUpdate(row) {
      this.dialogTitle = '编辑分类'
      this.categoryForm = {
        id: row.id,
        name: row.name
      }
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确认删除该分类?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await this.$store.dispatch('category/deleteCategory', row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (error) {
          console.error('删除失败:', error)
        }
      }).catch(() => { })
    },
    handleSubmit() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          try {
            if (this.categoryForm.id) {
              await this.$store.dispatch('category/updateCategory', {
                id: this.categoryForm.id,
                data: this.categoryForm
              })
              this.$message.success('更新成功')
            } else {
              await this.$store.dispatch('category/createCategory', this.categoryForm)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.getList()
          } catch (error) {
            console.error('保存分类失败:', error)
          }
        }
      })
    },
    handleCancel() {
      this.dialogVisible = false
    },
    handleDialogClosed() {
      this.$nextTick(() => {
        if (this.$refs.form) {
          this.$refs.form.resetFields()
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.el-card {
  margin-top: 0;
}

.category-container {
  padding: 20px;

  .filter-container {
    padding-bottom: 10px;
  }

  .dialog-footer {
    text-align: right;
  }
}
</style>
