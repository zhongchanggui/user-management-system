import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { public: true }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/ForgotPassword.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '首页', icon: 'el-icon-s-home' }
      },
      {
        path: '/user',
        name: 'User',
        component: () => import('../views/User.vue'),
        meta: { title: '员工管理', icon: 'el-icon-user-solid' }
      },
      {
        path: '/department',
        name: 'Department',
        component: () => import('../views/Department.vue'),
        meta: { title: '部门管理', icon: 'el-icon-office-building' }
      },
      {
        path: '/attendance',
        name: 'Attendance',
        component: () => import('../views/Attendance.vue'),
        meta: { title: '考勤管理', icon: 'el-icon-time' }
      },
      {
        path: '/leave',
        name: 'Leave',
        component: () => import('../views/Leave.vue'),
        meta: { title: '请假审批', icon: 'el-icon-s-claim' }
      },
      {
        path: '/salary',
        name: 'Salary',
        component: () => import('../views/Salary.vue'),
        meta: { title: '薪资管理', icon: 'el-icon-money' }
      },
      {
        path: '/notice',
        name: 'Notice',
        component: () => import('../views/Notice.vue'),
        meta: { title: '公告管理', icon: 'el-icon-message' }
      },
      {
        path: '/system',
        name: 'System',
        component: () => import('../views/System.vue'),
        meta: { title: '系统管理', icon: 'el-icon-setting' }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人中心', icon: 'el-icon-user' }
      }
    ]
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { public: true }
  },
  {
    path: '*',
    redirect: '/404'
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 全局捕获路由错误，防止 Navigation cancelled 报错
router.onError(err => {
  // 忽略导航取消/中止错误
  if (err && (err.name === 'NavigationCancelled' || err.name === 'NavigationAborted' || err.name === 'NavigationDuplicated')) {
    return
  }
  const msg = err.message || ''
  if (msg.includes('Navigation cancelled') || msg.includes('Navigation aborted') || msg.includes('Navigation duplicated')) {
    return
  }
  console.error('Router error:', err)
})

// 路由守卫
let isNavigating = false

router.beforeEach((to, from, next) => {
  // 如果已经在导航中，直接放行以避免冲突
  if (isNavigating) {
    next()
    return
  }

  const isLoggedIn = store.getters.isLoggedIn

  if (to.meta && to.meta.public) {
    // 公开页面，直接访问
    if (isLoggedIn && (to.path === '/login' || to.path === '/register')) {
      isNavigating = true
      next('/home')
      setTimeout(() => { isNavigating = false }, 100)
    } else {
      next()
    }
  } else {
    // 需要登录的页面
    if (isLoggedIn) {
      next()
    } else {
      isNavigating = true
      next('/login')
      setTimeout(() => { isNavigating = false }, 100)
    }
  }
})

export default router