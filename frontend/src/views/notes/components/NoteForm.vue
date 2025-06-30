<template>
  <el-form ref="form" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="标题" prop="title">
      <el-input v-model="form.title" placeholder="请输入笔记标题"></el-input>
    </el-form-item>

    <el-form-item label="分类" prop="categoryId">
      <el-select v-model="form.categoryId" filterable allow-create placeholder="请选择或输入分类"
        @change="handleCategoryChange">
        <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id"></el-option>
      </el-select>
    </el-form-item>

    <el-form-item label="标签" prop="tags">
      <el-select v-model="form.tags" multiple filterable allow-create clearable placeholder="请选择或输入标签"
        @change="handleTagChange">
        <el-option v-for="item in tags" :key="item.id" :label="item.name" :value="item.id"></el-option>
      </el-select>
    </el-form-item>

    <el-form-item label="内容" prop="content">
      <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入笔记内容"></el-input>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="submitForm">保存</el-button>
      <el-button @click="cancel">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import { mapState, mapActions } from 'vuex'

export default {
  name: 'NoteForm',
  props: {
    note: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      form: {
        title: '',
        content: '',
        categoryId: '',
        tags: []
      },
      rules: {
        title: [
          { required: true, message: '请输入笔记标题', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入笔记内容', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择或输入分类', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ...mapState({
      categories: state => state.category.categories,
      tags: state => state.tag.tags
    })
  },
  watch: {
    note: {
      handler(val) {
        if (val.id) {
          this.form = {
            ...val,
            tags: val.tags ? val.tags.map(tag => tag.id) : []
          }
        }
      },
      immediate: true
    }
  },
  created() {
    this.fetchCategories().then(() => {
      this.$nextTick(() => {
        // 尝试获取 el-select 内部的输入框，并使其获得焦点
        const categoryInput = this.$refs.categorySelect ? this.$refs.categorySelect.$el.querySelector('.el-input__inner') : null;
        if (categoryInput) {
          categoryInput.removeAttribute('readonly'); // 再次尝试移除 readonly 属性
          categoryInput.focus(); // 尝试使其获得焦点
        }
      });
    });
    this.fetchTags()
  },
  methods: {
    ...mapActions({
      fetchCategories: 'category/getCategories',
      fetchTags: 'tag/getTags',
      createCategory: 'category/createCategory',
      createTag: 'tag/createTag'
    }),
    async handleCategoryChange(value) {
      // 如果是新创建的分类
      if (typeof value === 'string') {
        try {
          const response = await this.createCategory({ name: value })
          this.form.categoryId = response.id
          this.$message.success('创建分类成功')
        } catch (error) {
          this.$message.error('创建分类失败')
          // 重置为 null，避免保存无效的分类 ID
          this.form.categoryId = null
        }
      } else {
        // 如果是选择已有分类，则直接赋值
        this.form.categoryId = value;
      }
    },
    async handleTagChange(value) {
      // 获取新添加的标签
      const newTag = value[value.length - 1]
      // 如果是新创建的标签
      if (typeof newTag === 'string') {
        try {
          const response = await this.createTag({ name: newTag })
          // 将新创建的标签 ID 替换到选中值中
          this.form.tags = value.map(tag =>
            typeof tag === 'string' ? response.id : tag
          )
          this.$message.success('创建标签成功')
        } catch (error) {
          this.$message.error('创建标签失败')
          // 移除新创建的标签
          this.form.tags = value.filter(tag => typeof tag !== 'string')
        }
      }
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.$emit('submit', this.form)
        }
      })
    },
    cancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.el-select {
  width: 100%;
}
</style>