<template>
  <div class="profile-page">
    <h2 class="profile-title">个人信息</h2>
    <el-form :model="form" ref="formRef" label-width="80px" class="profile-form">
      <el-form-item label="头像">
        <el-upload class="avatar-uploader" :action="null" :http-request="() => { }" :show-file-list="false"
          :on-change="handleAvatarChange" :auto-upload="false">
          <el-avatar :size="80" :src="userId ? `/api/users/avatar/${userId}` : defaultAvatar" />
          <el-button class="upload-btn" type="primary" plain>更新头像</el-button>
        </el-upload>
      </el-form-item>
      <el-form-item label="昵称" required>
        <el-input v-model="form.username" maxlength="20" />
      </el-form-item>
      <el-form-item label="签名">
        <el-input v-model="form.signature" maxlength="56" show-word-limit type="textarea" :rows="2"
          placeholder="简单介绍一下自己" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio label="男">男</el-radio>
          <el-radio label="女">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="出生日期">
        <el-date-picker v-model="form.birthday" type="date" placeholder="选择日期" format="YYYY-MM-DD"
          value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item>
        <el-button type="success" @click="handleSubmit">更新信息</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import defaultAvatar from '@/assets/logo.png'
import { uploadAvatar, updateUserInfo } from '@/api/user'

const store = useStore()
const formRef = ref(null)
const form = ref({
  avatar: '',
  username: '',
  signature: '',
  gender: '男',
  birthday: ''
})
const userId = ref(null)

onMounted(async () => {
  const user = store.state.user.userInfo
  if (user) {
    userId.value = user.id
    form.value.avatar = user.avatar ? `/api/users/avatar/${user.id}` : ''
    form.value.username = user.username
    form.value.signature = user.signature || ''
    form.value.gender = user.gender || '男'
    form.value.birthday = user.birthday ? user.birthday.slice(0, 10) : ''
  }
})

const handleAvatarChange = async (file) => {
  const raw = file.raw
  const res = await uploadAvatar(raw)
  if (res.code === 200) {
    form.value.avatar = res.data
    await store.dispatch('user/getUserInfo')
    ElMessage.success('头像更新成功')
  } else {
    ElMessage.error(res.message || '头像上传失败')
  }
}

const handleSubmit = async () => {
  // 只提交需要的字段
  const data = {
    username: form.value.username,
    signature: form.value.signature,
    gender: form.value.gender,
    birthday: form.value.birthday && form.value.birthday.length > 10 ? form.value.birthday.slice(0, 10) : form.value.birthday
  }
  const res = await updateUserInfo(data)
  if (res.code === 200) {
    await store.dispatch('user/getUserInfo')
    ElMessage.success('信息已更新')
  } else {
    ElMessage.error(res.message || '更新失败')
  }
}
</script>

<style scoped>
.profile-page {
  max-width: 520px;
  margin: 40px auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px 0 rgba(0, 0, 0, 0.06);
  padding: 40px 40px 24px 40px;
}

.profile-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 32px;
  color: #222;
}

.profile-form {
  .avatar-uploader {
    display: flex;
    align-items: center;
    gap: 20px;

    .upload-btn {
      margin-left: 16px;
    }
  }
}
</style>
