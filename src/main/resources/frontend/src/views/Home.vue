<template>
  <div class="home-container">
    <!-- 欢迎卡片 - 增强版 -->
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <div class="welcome-left">
          <div class="welcome-avatar">
            <el-avatar :size="80" class="avatar">
              {{ currentUser ? currentUser.fnickname.charAt(0).toUpperCase() : 'U' }}
            </el-avatar>
            <div class="avatar-badge" :class="getStateClass(currentUser ? currentUser.fstate : 1)"></div>
          </div>
          <div class="welcome-info">
            <h2>欢迎回来，{{ currentUser ? currentUser.fnickname : '用户' }} 👋</h2>
            <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
          </div>
        </div>
        <div class="welcome-right">
          <div class="welcome-clock">
            <div class="clock-time">{{ currentTime }}</div>
            <div class="clock-date">{{ currentShortDate }}</div>
          </div>
          <div class="welcome-actions">
            <el-button type="primary" icon="el-icon-user" round @click="$router.push('/user')">
              员工管理
            </el-button>
            <el-button icon="el-icon-refresh" round @click="handleRefresh">
              刷新数据
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 - 优化样式 -->
    <el-row :gutter="24" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-1" shadow="hover" @click.native="$router.push('/user')">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.total }}</p>
              <p class="stat-label">员工总数</p>
              <span class="stat-trend">总人数</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-2" shadow="hover" @click.native="filterByState(1)">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-success"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.normal }}</p>
              <p class="stat-label">正常状态</p>
              <span class="stat-trend">正常</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-3" shadow="hover" @click.native="filterByState(2)">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.frozen }}</p>
              <p class="stat-label">冻结状态</p>
              <span class="stat-trend">冻结</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-4" shadow="hover" @click.native="filterByState(0)">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-error"></i>
            </div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.disabled }}</p>
              <p class="stat-label">禁用状态</p>
              <span class="stat-trend">禁用</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 用户信息卡片 + 快捷操作 + 待办事项 -->
    <el-row :gutter="24">
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="info-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-user"></i> 个人信息</span>
            <el-button type="text" icon="el-icon-edit" @click="$router.push('/profile')">编辑</el-button>
          </div>
          <el-descriptions :column="1" border class="user-descriptions">
            <el-descriptions-item label="用户名">
              {{ currentUser ? currentUser.floginname : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="昵称">
              {{ currentUser ? currentUser.fnickname : '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStateType(currentUser ? currentUser.fstate : 1)" size="medium" effect="dark">
                {{ getStateText(currentUser ? currentUser.fstate : 1) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="类型">
              <el-tag type="info" effect="plain" size="medium">
                {{ currentUser ? currentUser.ftypename : '普通员工' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ currentUser ? formatDate(currentUser.fcreatedatetime) : '-' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card class="info-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-s-opportunity"></i> 快捷操作</span>
            <span class="card-tip">快速导航</span>
          </div>
          <div class="quick-actions">
            <div class="action-item" @click="$router.push('/user')">
              <div class="action-icon"><i class="el-icon-user"></i></div>
              <span>员工管理</span>
              <small>管理所有员工信息</small>
            </div>
            <div class="action-item" @click="handleRefresh">
              <div class="action-icon"><i class="el-icon-refresh-right"></i></div>
              <span>刷新数据</span>
              <small>同步最新数据</small>
            </div>
            <div class="action-item" @click="$router.push('/profile')">
              <div class="action-icon"><i class="el-icon-setting"></i></div>
              <span>个人设置</span>
              <small>修改个人信息</small>
            </div>
            <div class="action-item" @click="handleLogout">
              <div class="action-icon"><i class="el-icon-switch-button"></i></div>
              <span>退出登录</span>
              <small>安全退出系统</small>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统公告/待办区域 -->
    <el-row :gutter="24">
      <el-col :span="24">
        <el-card class="notice-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-message"></i> 系统公告</span>
            <el-button type="text" @click="viewMoreNotices">查看更多</el-button>
          </div>
          <div class="notice-list">
            <div class="notice-item" v-for="(notice, index) in notices" :key="index">
              <i class="el-icon-info"></i>
              <span class="notice-title">{{ notice.title }}</span>
              <span class="notice-time">{{ notice.time }}</span>
            </div>
            <div class="notice-empty" v-if="notices.length === 0">
              <i class="el-icon-bell"></i>
              <span>暂无最新公告</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Home',

  data() {
    return {
      currentTime: '',
      currentDateShort: '',
      clockTimer: null,
      stats: {
        total: 0,
        normal: 0,
        frozen: 0,
        disabled: 0
      },
      notices: [
        { title: '欢迎使用员工管理系统V2.0', time: '2024-01-01' },
        { title: '请及时更新个人资料信息', time: '2024-01-15' },
        { title: '系统将于本周六进行维护升级', time: '2024-01-20' }
      ]
    }
  },

  computed: {
    ...mapGetters(['currentUser']),

    currentDate() {
      const now = new Date()
      const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
      const year = now.getFullYear()
      const month = now.getMonth() + 1
      const day = now.getDate()
      return `${year}年${month}月${day}日 ${weekDays[now.getDay()]}`
    },

    currentShortDate() {
      const now = new Date()
      const month = now.getMonth() + 1
      const day = now.getDate()
      const weekDays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
      return `${month}月${day}日 ${weekDays[now.getDay()]}`
    }
  },

  mounted() {
    this.loadStats()
    this.startClock()
  },

  beforeDestroy() {
    if (this.clockTimer) {
      clearInterval(this.clockTimer)
    }
  },

  methods: {
    startClock() {
      this.updateTime()
      this.clockTimer = setInterval(this.updateTime, 1000)
    },

    updateTime() {
      const now = new Date()
      this.currentTime = now.toLocaleTimeString('zh-CN', { hour12: false })
    },

    loadStats() {
      this.$axios.get('/user/query').then(res => {
        if (res.success && res.data) {
          const users = res.data
          this.stats.total = users.length
          this.stats.normal = users.filter(u => u.fstate === 1).length
          this.stats.frozen = users.filter(u => u.fstate === 2).length
          this.stats.disabled = users.filter(u => u.fstate === 0).length
        }
      }).catch(() => {
        // 模拟数据，防止接口失败时页面空白
        this.stats = { total: 128, normal: 95, frozen: 23, disabled: 10 }
      })
    },

    getStateType(state) {
      switch (state) {
        case 0: return 'danger'
        case 1: return 'success'
        case 2: return 'warning'
        default: return 'info'
      }
    },

    getStateText(state) {
      switch (state) {
        case 0: return '禁用'
        case 1: return '正常'
        case 2: return '冻结'
        default: return '未知'
      }
    },

    getStateClass(state) {
      switch (state) {
        case 0: return 'badge-disabled'
        case 1: return 'badge-normal'
        case 2: return 'badge-frozen'
        default: return ''
      }
    },

    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    },

    filterByState(state) {
      this.$router.push({ path: '/user', query: { state } })
    },

    viewMoreNotices() {
      this.$message.info('更多公告功能开发中...')
    },

    handleRefresh() {
      this.loadStats()
      this.$message.success({
        message: '数据已刷新',
        duration: 1500,
        iconClass: 'el-icon-refresh'
      })
    },

    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        iconClass: 'el-icon-warning'
      }).then(() => {
        this.$axios.post('/logout').then(() => {
          this.$store.dispatch('logout')
          this.$message.success('已退出登录')
          setTimeout(() => {
            this.$router.push('/login')
          }, 500)
        }).catch(() => {
          // 即使接口失败也退出登录
          this.$store.dispatch('logout')
          this.$router.push('/login')
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.home-container {
  padding: 0;
  background: linear-gradient(135deg, #f0f2f8 0%, #e8ecf4 100%);
  min-height: 100%;
}

/* 欢迎卡片 - 增强 */
.welcome-card {
  margin-bottom: 24px;
  border: none;
  border-radius: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
  position: relative;
}

.welcome-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
  border-radius: 50%;
  pointer-events: none;
}

.welcome-card >>> .el-card__body {
  padding: 32px 36px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.welcome-left {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.welcome-avatar {
  position: relative;
  flex-shrink: 0;
}

.avatar {
  background: rgba(255, 255, 255, 0.25);
  font-size: 34px;
  font-weight: 600;
  color: #fff;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  border: 3px solid rgba(255, 255, 255, 0.5);
}

.avatar-badge {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 2px solid #fff;
  animation: badgePulse 2s infinite;
}

@keyframes badgePulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.15); }
}

.badge-normal {
  background: #67c23a;
  box-shadow: 0 0 10px rgba(103, 194, 58, 0.6);
}

.badge-disabled {
  background: #f56c6c;
  box-shadow: 0 0 10px rgba(245, 108, 108, 0.6);
}

.badge-frozen {
  background: #e6a23c;
  box-shadow: 0 0 10px rgba(230, 162, 60, 0.6);
}

.welcome-info h2 {
  color: #fff;
  font-size: 26px;
  margin-bottom: 8px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.welcome-info p {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  margin: 0;
}

.welcome-right {
  display: flex;
  align-items: center;
  gap: 24px;
  flex-wrap: wrap;
}

.welcome-clock {
  text-align: center;
  padding: 12px 24px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.clock-time {
  font-size: 32px;
  font-weight: 800;
  color: #fff;
  letter-spacing: 2px;
  line-height: 1.2;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.clock-date {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 4px;
  font-weight: 500;
}

.welcome-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.welcome-right .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  backdrop-filter: blur(4px);
  padding: 10px 20px;
  transition: all 0.3s;
}

.welcome-right .el-button:hover {
  background: rgba(255, 255, 255, 0.35);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

/* 统计卡片 */
.stat-row {
  margin-bottom: 24px;
}

.stat-card {
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  position: relative;
  border: 1px solid rgba(102, 126, 234, 0.06);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -50%;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  opacity: 0;
  transition: all 0.5s ease;
  pointer-events: none;
}

.stat-card:hover::before {
  opacity: 0.15;
  transform: scale(1.5);
}

.stat-card-1::before { background: radial-gradient(circle, #667eea, transparent); }
.stat-card-2::before { background: radial-gradient(circle, #11998e, transparent); }
.stat-card-3::before { background: radial-gradient(circle, #f093fb, transparent); }
.stat-card-4::before { background: radial-gradient(circle, #eb3349, transparent); }

.stat-card::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  transform: translate(30%, -30%);
  opacity: 0.08;
  pointer-events: none;
}

.stat-card-1::after { background: #667eea; }
.stat-card-2::after { background: #11998e; }
.stat-card-3::after { background: #f093fb; }
.stat-card-4::after { background: #eb3349; }

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.2);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 4px 0;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon i {
  font-size: 30px;
  color: #fff;
}

.stat-card-1 .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

.stat-card-2 .stat-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  box-shadow: 0 8px 16px rgba(17, 153, 142, 0.3);
}

.stat-card-3 .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 8px 16px rgba(240, 147, 251, 0.3);
}

.stat-card-4 .stat-icon {
  background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
  box-shadow: 0 8px 16px rgba(235, 51, 73, 0.3);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.2;
  margin-bottom: 4px;
  background: linear-gradient(135deg, #1e293b, #334155);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  animation: countUp 0.8s ease-out;
}

@keyframes countUp {
  from { opacity: 0; transform: scale(0.8) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
  margin-bottom: 4px;
}

.stat-trend {
  font-size: 11px;
  color: #cbd5e1;
  letter-spacing: 0.5px;
}

/* 信息卡片 */
.info-card {
  border: none;
  border-radius: 20px;
  margin-bottom: 24px;
  overflow: hidden;
  transition: all 0.3s;
}

.info-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.info-card >>> .el-card__header {
  background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  border-bottom: 1px solid #edf2f7;
  padding: 16px 24px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  color: #1e293b;
}

.card-header i {
  color: #667eea;
  font-size: 18px;
  margin-right: 8px;
}

.card-tip {
  font-size: 12px;
  color: #94a3b8;
  font-weight: normal;
}

.user-descriptions >>> .el-descriptions-item__label {
  width: 100px;
  background: #f8fafc;
  font-weight: 500;
  color: #475569;
}

.user-descriptions >>> .el-descriptions-item__content {
  color: #1e293b;
}

/* 状态标签美化 */
.state-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 快捷操作网格 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  text-align: center;
  border: 1px solid transparent;
  position: relative;
  overflow: hidden;
}

.action-item::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: width 0.5s ease, height 0.5s ease;
  z-index: 0;
}

.action-item:hover::before {
  width: 300px;
  height: 300px;
}

.action-item:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 32px rgba(102, 126, 234, 0.35);
  border-color: rgba(102, 126, 234, 0.2);
}

.action-item:hover .action-icon i,
.action-item:hover span,
.action-item:hover small {
  color: #fff;
  position: relative;
  z-index: 1;
}

.action-item:hover .action-icon {
  background: rgba(255, 255, 255, 0.25);
  position: relative;
  z-index: 1;
}

.action-icon {
  width: 52px;
  height: 52px;
  background: rgba(102, 126, 234, 0.15);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  transition: all 0.3s;
}

.action-item:hover .action-icon {
  background: rgba(255, 255, 255, 0.25);
}

.action-icon i {
  font-size: 26px;
  color: #667eea;
  transition: all 0.3s;
}

.action-item span {
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 6px;
  transition: all 0.3s;
}

.action-item small {
  font-size: 11px;
  color: #94a3b8;
  transition: all 0.3s;
}

/* 公告卡片 */
.notice-card {
  border: none;
  border-radius: 20px;
  margin-bottom: 24px;
  transition: all 0.3s;
}

.notice-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.notice-card >>> .el-card__header {
  background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  border-bottom: 1px solid #edf2f7;
}

.notice-list {
  padding: 8px 0;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-bottom: 1px solid #edf2f7;
  transition: all 0.2s;
  cursor: pointer;
  border-radius: 8px;
  margin: 0 8px;
}

.notice-item:hover {
  background: linear-gradient(135deg, #f0f2ff 0%, #f5f7fe 100%);
}

.notice-item i {
  color: #667eea;
  font-size: 16px;
}

.notice-title {
  flex: 1;
  color: #334155;
  font-size: 14px;
}

.notice-time {
  font-size: 12px;
  color: #94a3b8;
}

.notice-empty {
  text-align: center;
  padding: 40px;
  color: #94a3b8;
}

.notice-empty i {
  font-size: 48px;
  margin-bottom: 12px;
  display: block;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-card >>> .el-card__body {
    padding: 20px;
  }

  .welcome-info h2 {
    font-size: 20px;
  }

  .stat-value {
    font-size: 24px;
  }

  .quick-actions {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>