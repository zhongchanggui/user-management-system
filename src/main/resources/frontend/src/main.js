import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// 引入 ElementUI
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

// 引入 axios
import axios from 'axios'

Vue.use(ElementUI)

Vue.config.productionTip = false

// 全局错误处理 - 忽略axios取消请求和Element UI取消操作等无害错误
Vue.config.errorHandler = (err, vm, info) => {
  if (!err) return
  const msg = err.message || (typeof err === 'string' ? err : '')
  // 忽略axios取消的请求错误
  if (msg === 'cancel' || err.__CANCEL__) return
  // 忽略所有Vue Router导航错误
  if (err.name === 'NavigationDuplicated') return
  if (err.name === 'NavigationCancelled') return
  if (err.name === 'NavigationAborted') return
  if (msg && msg.includes('Navigation cancelled')) return
  // 忽略Element UI MessageBox/Confirm/Prompt取消
  if (msg === 'cancel' || msg.includes('cancel')) return
  // 其他错误正常打印
  console.error('Vue error:', err, info)
}

// 捕获未处理的Promise rejection
window.addEventListener('unhandledrejection', event => {
  if (!event.reason) return
  const msg = event.reason.message || (typeof event.reason === 'string' ? event.reason : '')
  if (msg === 'cancel' || event.reason.__CANCEL__) {
    event.preventDefault()
    return
  }
  // 忽略所有Vue Router导航错误
  if (event.reason.name === 'NavigationDuplicated' ||
      event.reason.name === 'NavigationCancelled' ||
      event.reason.name === 'NavigationAborted') {
    event.preventDefault()
    return
  }
  if (msg && msg.includes('Navigation cancelled')) {
    event.preventDefault()
    return
  }
  // 忽略所有Element UI确认框取消操作
  if (typeof event.reason === 'string' && event.reason === 'cancel') {
    event.preventDefault()
  }
})

// 捕获跨域脚本错误等运行时错误
window.addEventListener('error', event => {
  // 忽略外部脚本加载错误（如高德地图API）
  if (event.message && event.message === 'Script error.') {
    event.preventDefault()
  }
}, true)

// 全局修复Vue Router重复导航报错
const originalPush = router.push
router.push = function push(location) {
  return originalPush.call(this, location).catch(err => {
    if (err && (err.name === 'NavigationDuplicated' ||
                err.name === 'NavigationCancelled' ||
                err.name === 'NavigationAborted')) return
    throw err
  })
}
const originalReplace = router.replace
router.replace = function replace(location) {
  return originalReplace.call(this, location).catch(err => {
    if (err && (err.name === 'NavigationDuplicated' ||
                err.name === 'NavigationCancelled' ||
                err.name === 'NavigationAborted')) return
    throw err
  })
}

// 配置 axios 基础路径
axios.defaults.baseURL = '/api'
axios.defaults.timeout = 10000
axios.defaults.withCredentials = true

// axios 请求拦截器
axios.interceptors.request.use(
  config => {
    // 可以在这里添加 token 等
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// axios 响应拦截器
axios.interceptors.response.use(
  response => {
    const res = response.data
    if (!res.success) {
      // 业务错误提示
      if (res.message) {
        ElementUI.Message.error(res.message)
      }
      // 未登录处理
      if (res.message === '用户未登录') {
        store.dispatch('logout')
        router.push('/login').catch(() => {})
      }
    }
    return res
  },
  error => {
    // 忽略取消的请求
    if (axios.isCancel(error)) {
      return Promise.reject(error)
    }
    // 如果请求配置了 silent 模式，不显示错误提示
    const isSilent = error.config && error.config.silent
    if (error.response) {
      if (error.response.status === 401) {
        if (!isSilent) {
          ElementUI.Message.error('登录已过期，请重新登录')
          store.dispatch('logout')
          router.push('/login').catch(() => {})
        }
      } else if (!isSilent) {
        ElementUI.Message.error('网络错误，请稍后重试')
      }
    } else if (!isSilent) {
      ElementUI.Message.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

Vue.prototype.$axios = axios
Vue.prototype.$message = ElementUI.Message

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
