import axios from 'axios'
import { ElMessage } from 'element-plus'
import store from '@/store'
import router from '@/router'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',  // 使用相对路径，让 Vite 代理处理
  timeout: 5000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('Admin-Token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }

    // 打印完整的请求配置
    console.log('Request URL:', config.url)
    console.log('Request Method:', config.method)
    console.log('Request Headers:', config.headers)
    console.log('Request Data:', config.data)

    return config
  },
  error => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    console.log('Response Status:', response.status)
    console.log('Response Headers:', response.headers)
    console.log('Response Data:', response.data)

    const res = response.data

    // 如果返回的状态码不是200，说明接口请求有误
    if (res.code !== 200) {
      ElMessage({
        message: res.message || '错误',
        type: 'error',
        duration: 5 * 1000
      })

      // 401: 未登录或token过期
      if (res.code === 401) {
        store.dispatch('user/resetToken').then(() => {
          router.push('/login')
        })
      }
      return Promise.reject(new Error(res.message || '错误'))
    } else {
      return res
    }
  },
  error => {
    console.error('Response Error:', error)
    console.error('Error Config:', error.config)
    console.error('Error Response:', error.response)
    console.error('Error Message:', error.message)

    let message = '请求失败'
    if (error.response) {
      switch (error.response.status) {
        case 401:
          message = '未授权，请重新登录'
          store.dispatch('user/resetToken').then(() => {
            router.push('/login')
          })
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求错误，未找到该资源'
          break
        case 500:
          message = '服务器错误，请检查后端服务是否正常运行'
          break
        default:
          message = `连接错误${error.response.status}`
      }
    } else if (error.request) {
      message = '服务器未响应，请检查后端服务是否正常运行'
    } else {
      message = '网络连接异常'
    }

    ElMessage({
      message: message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service 