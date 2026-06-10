<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '72px' : '240px'" class="sidebar">
      <div class="logo" :class="{ 'logo-collapsed': isCollapse }">
        <div class="logo-icon-wrapper">
          <div class="logo-icon">
            <i class="el-icon-s-platform"></i>
          </div>
          <transition name="fade-text">
            <div class="logo-text" v-if="!isCollapse">
              <h3>员工管理系统</h3>
              <span class="logo-subtitle">Management System</span>
            </div>
          </transition>
        </div>
      </div>

      <div class="menu-wrapper">
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="transparent"
          text-color="#8a92b2"
          active-text-color="#fff"
          :collapse="isCollapse"
          router
        >
          <el-menu-item index="/home">
            <div class="menu-item-inner">
              <div class="menu-icon-wrapper">
                <i class="el-icon-s-home"></i>
              </div>
              <span slot="title">首页</span>
            </div>
          </el-menu-item>
          <el-menu-item index="/user">
            <div class="menu-item-inner">
              <div class="menu-icon-wrapper">
                <i class="el-icon-user-solid"></i>
              </div>
              <span slot="title">员工管理</span>
            </div>
          </el-menu-item>
          <el-menu-item v-if="isAdmin" index="/department">
            <div class="menu-item-inner">
              <div class="menu-icon-wrapper">
                <i class="el-icon-office-building"></i>
              </div>
              <span slot="title">部门管理</span>
            </div>
          </el-menu-item>
          <el-menu-item index="/attendance">
            <div class="menu-item-inner">
              <div class="menu-icon-wrapper">
                <i class="el-icon-time"></i>
              </div>
              <span slot="title">考勤管理</span>
            </div>
          </el-menu-item>
          <el-menu-item index="/leave">
            <div class="menu-item-inner">
              <div class="menu-icon-wrapper">
                <i class="el-icon-s-claim"></i>
              </div>
              <span slot="title">请假审批</span>
            </div>
          </el-menu-item>
          <el-menu-item index="/salary">
            <div class="menu-item-inner">
              <div class="menu-icon-wrapper">
                <i class="el-icon-money"></i>
              </div>
              <span slot="title">薪资管理</span>
            </div>
          </el-menu-item>
          <el-menu-item index="/notice">
            <div class="menu-item-inner">
              <div class="menu-icon-wrapper">
                <i class="el-icon-message"></i>
              </div>
              <span slot="title">公告管理</span>
            </div>
          </el-menu-item>
          <el-menu-item v-if="isAdmin" index="/system">
            <div class="menu-item-inner">
              <div class="menu-icon-wrapper">
                <i class="el-icon-setting"></i>
              </div>
              <span slot="title">系统管理</span>
            </div>
          </el-menu-item>
          <el-menu-item index="/profile">
            <div class="menu-item-inner">
              <div class="menu-icon-wrapper">
                <i class="el-icon-user"></i>
              </div>
              <span slot="title">个人中心</span>
            </div>
          </el-menu-item>
        </el-menu>
      </div>

      <!-- 折叠按钮 -->
      <div class="collapse-btn" @click="toggleCollapse">
        <div class="collapse-btn-inner">
          <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"></i>
        </div>
      </div>
    </el-aside>

    <el-container>
      <!-- 头部 -->
      <el-header class="header" height="64px">
        <div class="header-left">
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/home' }">
              <i class="el-icon-s-home" style="margin-right: 4px;"></i>首页
            </el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta && $route.meta.title">
              {{ $route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-tooltip content="全屏" placement="bottom">
            <div class="header-icon-btn" @click="toggleFullscreen">
              <i :class="isFullscreen ? 'el-icon-copy-document' : 'el-icon-full-screen'"></i>
            </div>
          </el-tooltip>

          <el-tooltip content="系统通知" placement="bottom">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
              <div class="header-icon-btn">
                <i class="el-icon-bell"></i>
              </div>
            </el-badge>
          </el-tooltip>

          <div class="header-divider"></div>

          <el-dropdown @command="handleCommand" class="user-dropdown">
            <span class="user-info">
              <el-avatar :size="36" class="user-avatar">
                {{ currentUser ? currentUser.fnickname.charAt(0) : 'U' }}
              </el-avatar>
              <div class="user-detail">
                <span class="user-name">{{ currentUser ? currentUser.fnickname : '' }}</span>
                <span class="user-role">{{ currentUser ? currentUser.ftypename : '普通员工' }}</span>
              </div>
              <i class="el-icon-arrow-down arrow-icon"></i>
            </span>
            <el-dropdown-menu slot="dropdown" class="user-dropdown-menu">
              <div class="dropdown-user-info">
                <el-avatar :size="48" class="dropdown-avatar">
                  {{ currentUser ? currentUser.fnickname.charAt(0) : 'U' }}
                </el-avatar>
                <div class="dropdown-user-detail">
                  <strong>{{ currentUser ? currentUser.fnickname : '' }}</strong>
                  <span>{{ currentUser ? currentUser.floginname : '' }}</span>
                </div>
              </div>
              <el-dropdown-item divided command="profile">
                <i class="el-icon-user"></i> 个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout">
                <i class="el-icon-switch-button"></i> 退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <transition name="fade-transform" mode="out-in">
          <router-view />
        </transition>
      </el-main>

      <!-- 底部 -->
      <el-footer class="footer" height="40px">
        <span>© 2025 员工管理系统 All Rights Reserved</span>
      </el-footer>
    </el-container>

    <!-- 状态异常弹窗 -->
    <el-dialog
      title="系统提示"
      append-to-body
      destroy-on-close
      :visible.sync="stateDialogVisible"
      width="420px"
      :close-on-click-modal="false"
      :show-close="false"
      center
      custom-class="state-dialog"
    >
      <div class="state-dialog-content">
        <div class="warning-icon-wrapper">
          <i class="el-icon-warning warning-icon"></i>
        </div>
        <p class="state-message">{{ stateMessage }}</p>
        <p class="countdown-text">将在 <span class="countdown-num">{{ countdown }}</span> 秒后跳转到登录页</p>
      </div>
    </el-dialog>
  </el-container>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Layout',

  computed: {
    ...mapGetters(['currentUser', 'isAdmin']),

    activeMenu() {
      return this.$route.path
    }
  },

  mounted() {
    this.startUserStateCheck()
  },

  beforeDestroy() {
    this.stopUserStateCheck()
  },

  data() {
    return {
      isCollapse: false,
      isFullscreen: false,
      checkInterval: null,
      unreadCount: 0,
      stateDialogVisible: false,
      stateMessage: '',
      countdown: 5,
      countdownInterval: null
    }
  },

  methods: {
    toggleCollapse() {
      this.isCollapse = !this.isCollapse
    },

    toggleFullscreen() {
      if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen().then(() => {
          this.isFullscreen = true
        }).catch(() => {})
      } else {
        document.exitFullscreen().then(() => {
          this.isFullscreen = false
        }).catch(() => {})
      }
    },

    handleCommand(command) {
      if (command === 'logout') {
        this.handleLogout()
      } else if (command === 'profile') {
        this.$router.push('/profile')
      }
    },

    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.post('/logout').then(() => {
          this.$store.dispatch('logout')
          this.$message.success('已退出登录')
          this.$router.push('/login')
        })
      }).catch(() => {})
    },

    startUserStateCheck() {
      this.checkInterval = setInterval(() => {
        this.checkUserState()
      }, 5 * 60 * 1000)
    },

    stopUserStateCheck() {
      if (this.checkInterval) {
        clearInterval(this.checkInterval)
        this.checkInterval = null
      }
      if (this.countdownInterval) {
        clearInterval(this.countdownInterval)
        this.countdownInterval = null
      }
    },

    checkUserState() {
      this.$axios.get('/checkUserState').then(res => {
        if (!res.success) {
          this.stateMessage = res.message || '账号状态异常'
          this.stateDialogVisible = true
          this.countdown = 5
          this.countdownInterval = setInterval(() => {
            this.countdown--
            if (this.countdown <= 0) {
              this.handleForceLogout()
            }
          }, 1000)
        }
      })
    },

    handleForceLogout() {
      this.stopUserStateCheck()
      this.stateDialogVisible = false
      this.$store.dispatch('logout')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

/* ===== 侧边栏 ===== */
.sidebar {
  background: linear-gradient(195deg, #111827 0%, #1a2235 40%, #1e293b 100%);
  transition: width 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(ellipse at top left, rgba(102, 126, 234, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at bottom right, rgba(118, 75, 162, 0.06) 0%, transparent 50%);
  pointer-events: none;
}

/* Logo 区域 */
.logo {
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  min-height: 80px;
  display: flex;
  align-items: center;
  position: relative;
  z-index: 1;
}

.logo-collapsed {
  padding: 20px 12px;
  justify-content: center;
}

.logo-icon-wrapper {
  display: flex;
  align-items: center;
  gap: 14px;
}

.logo-icon {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.35);
  transition: all 0.3s;
  flex-shrink: 0;
}

.logo-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.45);
}

.logo-icon i {
  font-size: 22px;
  color: #fff;
}

.logo-text h3 {
  color: #fff;
  margin: 0;
  font-size: 17px;
  white-space: nowrap;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.logo-subtitle {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.35);
  letter-spacing: 1px;
  text-transform: uppercase;
  white-space: nowrap;
}

/* 菜单区域 */
.menu-wrapper {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 12px 0;
  position: relative;
  z-index: 1;
}

.menu-wrapper::-webkit-scrollbar {
  width: 0;
}

.sidebar-menu {
  border-right: none;
  padding: 0 8px;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 100%;
}

.sidebar-menu .el-menu-item {
  margin: 3px 0;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 46px;
  line-height: 46px;
  padding: 0 12px !important;
  position: relative;
  overflow: hidden;
}

.sidebar-menu .el-menu-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 0;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 0 3px 3px 0;
  transition: height 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.sidebar-menu .el-menu-item:hover {
  background: rgba(102, 126, 234, 0.12) !important;
}

.sidebar-menu .el-menu-item.is-active {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.25), rgba(118, 75, 162, 0.15)) !important;
  color: #fff !important;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.sidebar-menu .el-menu-item.is-active::before {
  height: 60%;
}

.menu-item-inner {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.menu-icon-wrapper {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  transition: all 0.3s;
  flex-shrink: 0;
}

.sidebar-menu .el-menu-item:hover .menu-icon-wrapper {
  background: rgba(102, 126, 234, 0.15);
}

.sidebar-menu .el-menu-item.is-active .menu-icon-wrapper {
  background: rgba(102, 126, 234, 0.2);
}

.sidebar-menu .el-menu-item i {
  font-size: 18px;
  transition: all 0.3s;
}

.sidebar-menu .el-menu-item.is-active i {
  color: #fff;
}

.sidebar-menu .el-menu-item span {
  font-size: 14px;
  letter-spacing: 0.3px;
}

/* 折叠按钮 */
.collapse-btn {
  padding: 16px;
  display: flex;
  justify-content: center;
  position: relative;
  z-index: 1;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.collapse-btn-inner {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.collapse-btn-inner:hover {
  background: rgba(102, 126, 234, 0.2);
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.collapse-btn-inner i {
  font-size: 18px;
  color: #8b9cf7;
}

/* ===== 头部 ===== */
.header {
  background-color: #fff;
  box-shadow: 0 1px 8px rgba(0, 21, 41, 0.06);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  border-bottom: 1px solid #f0f2f5;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon-btn {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #f8f9fb;
  font-size: 18px;
  color: #606266;
}

.header-icon-btn:hover {
  background: #f0f2ff;
  color: #667eea;
  transform: translateY(-1px);
}

.header-divider {
  width: 1px;
  height: 24px;
  background: #e8ecf4;
}

.notification-badge {
  cursor: pointer;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 14px 6px 6px;
  border-radius: 14px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 15px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-detail {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.user-name {
  color: #1e293b;
  font-size: 14px;
  font-weight: 600;
}

.user-role {
  color: #94a3b8;
  font-size: 11px;
}

.arrow-icon {
  color: #94a3b8;
  font-size: 12px;
  transition: transform 0.3s;
}

/* 下拉菜单 */
.user-dropdown-menu {
  padding: 0 !important;
}

.dropdown-user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 20px;
  background: linear-gradient(135deg, #f5f7ff, #f0f2ff);
  border-bottom: 1px solid #edf2f7;
}

.dropdown-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 20px;
  font-weight: 600;
  flex-shrink: 0;
}

.dropdown-user-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.dropdown-user-detail strong {
  color: #1e293b;
  font-size: 15px;
}

.dropdown-user-detail span {
  color: #94a3b8;
  font-size: 12px;
}

/* ===== 主内容区 ===== */
.main-content {
  background: linear-gradient(135deg, #f0f2f8 0%, #e8ecf4 100%);
  padding: 24px;
  overflow-y: auto;
  min-height: 0;
  position: relative;
}

/* 主内容区微妙的网格背景 */
.main-content::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(circle at 25% 25%, rgba(102, 126, 234, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 75% 75%, rgba(118, 75, 162, 0.03) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* ===== 底部 ===== */
.footer {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid #edf2f7;
  font-size: 12px;
  color: #94a3b8;
  letter-spacing: 0.5px;
}

/* ===== 动画 ===== */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-transform-enter,
.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.fade-text-enter-active,
.fade-text-leave-active {
  transition: opacity 0.2s;
}

.fade-text-enter,
.fade-text-leave-to {
  opacity: 0;
}

/* ===== 状态异常弹窗 ===== */
.state-dialog {
  border-radius: 20px;
  overflow: hidden;
}

.state-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #ff9a56, #f56c6c);
  padding: 20px 24px;
}

.state-dialog >>> .el-dialog__title {
  color: #fff;
  font-weight: 600;
}

.state-dialog-content {
  text-align: center;
  padding: 24px 0;
}

.warning-icon-wrapper {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #fff3e0, #ffe0b2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.warning-icon {
  font-size: 36px;
  color: #e6a23c;
}

.state-message {
  font-size: 16px;
  color: #303133;
  margin: 0 0 16px;
  font-weight: 500;
}

.countdown-text {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

.countdown-num {
  font-size: 22px;
  font-weight: 700;
  color: #e6a23c;
}
</style>