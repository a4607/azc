<template>
  <el-container class="app-wrapper">
    <el-aside width="200px" class="sidebar-container">
      <div class="logo">
        <img src="@/assets/logo.png" alt="Logo">
        <span>笔记系统</span>
      </div>
      <el-menu :default-active="activeMenu" class="el-menu-vertical" :collapse="isCollapse" background-color="#304156"
        text-color="#bfcbd9" active-text-color="#409EFF" router>
        <el-menu-item index="/notes">
          <el-icon>
            <Document />
          </el-icon>
          <template #title>我的笔记</template>
        </el-menu-item>
        <el-menu-item index="/notes/public">
          <el-icon>
            <Share />
          </el-icon>
          <template #title>公开笔记</template>
        </el-menu-item>
        <el-menu-item index="/notes/create">
          <el-icon>
            <Connection />
          </el-icon>
          <template #title>新建笔记</template>
        </el-menu-item>
        <el-menu-item index="/tags">
          <el-icon>
            <Collection />
          </el-icon>
          <template #title>标签管理</template>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon>
            <User />
          </el-icon>
          <template #title>个人中心</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="app-header">
        <div class="header-left">
          <el-icon class="toggle-sidebar" @click="toggleSidebar">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <div class="header-center">
          <SearchBar />
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-dropdown-trigger" v-if="userInfo">
              <el-avatar :size="40"
                :src="userInfo && userInfo.id ? `/api/users/avatar/${userInfo.id}` : defaultAvatar" />
              <div class="user-info">
                <span class="username">{{ userInfo.username }}</span>
                <span class="signature">{{ userInfo.signature }}</span>
              </div>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon>
                    <User />
                  </el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon>
                    <Setting />
                  </el-icon>设置
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon>
                    <SwitchButton />
                  </el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <div class="header-actions">
            <el-icon class="action-icon" @click="toggleLanguage" title="切换语言">
              <TranslateIcon />
            </el-icon>
            <el-icon class="action-icon" @click="toggleDark()" title="切换主题">
              <Moon v-if="isDark" />
              <Sunny v-else />
            </el-icon>
          </div>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useDark, useToggle } from '@vueuse/core'
import {
  Document,
  Share,
  Connection,
  Collection,
  User,
  Fold,
  Expand,
  CaretBottom,
  Setting,
  SwitchButton,
  Switch,
  Sunny,
  Moon,
  Sort
} from '@element-plus/icons-vue'
import defaultAvatar from '@/assets/logo.png';
import TranslateIcon from '@/assets/icons/TranslateIcon.vue';
import SearchBar from '@/components/SearchBar.vue'

const store = useStore()
const router = useRouter()
const route = useRoute()

const isCollapse = ref(false)
const userInfo = computed(() => store.state.user.userInfo)

const activeMenu = computed(() => route.path)

const isDark = useDark()
const toggleDark = useToggle(isDark)

const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

const toggleLanguage = () => {
  ElMessage.info('语言切换功能正在开发中...');
}

const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'settings') {
    router.push('/profile') // Temporarily point to profile
  } else if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await store.dispatch('user/logout')
      router.push('/login')
    } catch (error) {
      // User clicked cancel or closed the dialog
      console.log('Logout cancelled');
    }
  }
}
</script>

<style lang="scss" scoped>
.app-wrapper {
  height: 100vh;
  display: flex;
  flex-direction: row;

  .sidebar-container {
    background-color: #304156;
    transition: width 0.3s;
    height: 100vh;

    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      padding: 0 20px;
      color: #fff;
      font-size: 18px;

      img {
        width: 32px;
        height: 32px;
        margin-right: 12px;
      }
    }

    .el-menu {
      border: none;
    }
  }

  .el-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100vh;
    overflow: auto;
  }

  .app-header {
    background-color: #fff;
    border-bottom: 1px solid #e6e6e6;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;

    .header-left {
      .toggle-sidebar {
        font-size: 20px;
        cursor: pointer;

        &:hover {
          color: #409EFF;
        }
      }
    }

    .header-center {
      flex: 1;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .header-right {
      display: flex;
      align-items: center;

      .user-dropdown-trigger {
        display: flex;
        align-items: center;
        cursor: pointer;

        .user-info {
          display: flex;
          flex-direction: column;
          align-items: flex-start;
          margin-left: 10px;

          .username {
            font-weight: 600;
            color: #333;
            line-height: 1.2;
          }

          .signature {
            font-size: 12px;
            color: #999;
            max-width: 150px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
          }
        }
      }

      .header-actions {
        display: flex;
        align-items: center;
        margin-left: 20px;

        .action-icon {
          font-size: 20px;
          cursor: pointer;
          color: #5a5e66;
          margin-left: 15px;

          &:hover {
            color: #409EFF;
          }
        }
      }
    }
  }

  .el-main {
    background-color: #fff !important;
    padding: 20px;
    flex: 1;
    min-height: 0;
    overflow: auto;
    display: flex;
    flex-direction: column;
  }
}
</style>
