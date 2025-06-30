import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue').catch(err => {
      console.error('Failed to load login component:', err)
      return import('../views/error/index.vue')
    }),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/register/index.vue').catch(err => {
      console.error('Failed to load register component:', err)
      return import('../views/error/index.vue')
    }),
    meta: { title: '注册' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/profile/index.vue').catch(err => {
      console.error('Failed to load profile component:', err)
      return import('../views/error/index.vue')
    }),
    meta: { title: '个人资料', requiresAuth: true }
  },
  {
    path: '/',
    component: () => import('../layout/index.vue').catch(err => {
      console.error('Failed to load layout component:', err)
      return import('../views/error/index.vue')
    }),
    redirect: '/notes',
    children: [
      {
        path: 'notes',
        name: 'Notes',
        component: () => import('../views/notes/index.vue').catch(err => {
          console.error('Failed to load notes component:', err)
          return import('../views/error/index.vue')
        }),
        meta: { title: '笔记列表', requiresAuth: true }
      },
      {
        path: 'notes/create',
        name: 'NoteCreate',
        component: () => import('../views/notes/edit.vue'),
        meta: { title: '新建笔记', requiresAuth: true }
      },
      {
        path: 'notes/edit/:id',
        name: 'NoteEdit',
        component: () => import('../views/notes/edit.vue'),
        meta: { title: '编辑笔记', requiresAuth: true }
      },
      {
        path: 'tags',
        name: 'Tags',
        component: () => import('../views/tag/index.vue'),
        meta: { title: '标签管理', requiresAuth: true }
      },
      {
        path: 'notes/public',
        name: 'PublicNotes',
        component: () => import('../views/notes/public.vue'),
        meta: { title: '公开笔记', requiresAuth: true }
      },
      {
        path: 'notes/share/:shareCode',
        name: 'ShareNote',
        component: () => import('../views/notes/share.vue'),
        meta: { title: '分享笔记' }
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('../views/AboutView.vue'),
        meta: { title: '关于我们', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 笔记系统` : '笔记系统'

  // 检查是否需要登录权限
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const token = store.getters.token
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
      return
    }

    try {
      // 检查用户信息是否已加载
      const userInfo = store.getters.userInfo
      if (!userInfo) {
        // 获取用户信息
        await store.dispatch('user/getUserInfo')
      }
      next()
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 获取用户信息失败，可能是token过期
      await store.dispatch('user/resetToken')
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    }
  } else {
    next()
  }
})

export default router
