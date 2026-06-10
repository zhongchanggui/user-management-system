<template>
  <div class="login-container">
    <div class="bg-animation">
      <div
        v-for="(bubble, index) in bubbles"
        :key="'bubble-' + index"
        class="bubble"
        :style="bubble.style"
      ></div>
    </div>

    <div class="particles-container">
      <div
        v-for="(particle, index) in particles"
        :key="'particle-' + index"
        class="particle"
        :style="particle.style"
      ></div>
    </div>

    <div class="glow-effect glow-1"></div>
    <div class="glow-effect glow-2"></div>

    <div class="login-box">
      <div class="login-header">
        <div class="logo-icon">
          <i class="el-icon-office-building"></i>
        </div>
        <h2>员工管理系统</h2>
        <p>Enterprise Employee Management System</p>
      </div>

      <!-- 登录方式标签页切换（借鉴期末作业） -->
      <div class="login-tabs">
        <div
          class="login-tab"
          :class="{ active: activeTab === 'password' }"
          @click="switchTab('password')"
        >
          <i class="el-icon-lock"></i> 密码登录
        </div>
        <div
          class="login-tab"
          :class="{ active: activeTab === 'sms' }"
          @click="switchTab('sms')"
        >
          <i class="el-icon-chat-dot-round"></i> 短信登录
        </div>
      </div>

      <!-- 密码登录表单 -->
      <el-form
        v-show="activeTab === 'password'"
        ref="loginForm"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter.native="handleLogin"
      >
        <el-form-item prop="floginname">
          <el-input
            v-model="loginForm.floginname"
            placeholder="工号 / 邮箱 / 用户名"
            prefix-icon="el-icon-user"
            clearable
            size="large"
          />
        </el-form-item>

        <el-form-item prop="fpassword">
          <el-input
            v-model="loginForm.fpassword"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            show-password
            clearable
            size="large"
          />
        </el-form-item>

        <el-form-item prop="verifyCode" class="verify-code-item">
          <div class="verify-code-wrapper">
            <el-input
              v-model="loginForm.verifyCode"
              placeholder="请输入验证码"
              prefix-icon="el-icon-key"
              clearable
              size="large"
              class="verify-input"
              maxlength="4"
            />
            <div class="verify-canvas-box" @click="refreshVerifyCode">
              <verify-code ref="verifyCode" class="verify-canvas" @refresh="onCodeRefresh" />
            </div>
          </div>
          <div class="verify-hint">
            <span class="hint-text">看不清？点击图片换一张</span>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            <i class="el-icon-right" v-if="!loading"></i>
            <span>{{ loading ? '身 份 验 证 中...' : '登 录 系 统' }}</span>
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 短信登录表单 -->
      <el-form
        v-show="activeTab === 'sms'"
        ref="smsForm"
        :model="smsForm"
        :rules="smsRules"
        class="login-form"
        @keyup.enter.native="handleSmsLogin"
      >
        <el-form-item prop="phone">
          <el-input
            v-model="smsForm.phone"
            placeholder="请输入手机号"
            prefix-icon="el-icon-mobile-phone"
            clearable
            size="large"
            maxlength="11"
          />
        </el-form-item>

        <el-form-item prop="smsCode">
          <div class="sms-code-wrapper">
            <el-input
              v-model="smsForm.smsCode"
              placeholder="请输入验证码"
              prefix-icon="el-icon-message"
              clearable
              size="large"
              class="sms-input"
              maxlength="6"
            />
            <el-button
              class="sms-send-btn"
              :disabled="smsCooldown > 0"
              @click="sendSmsCode"
            >
              {{ smsCooldown > 0 ? smsCooldown + 's 后重发' : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleSmsLogin"
          >
            <i class="el-icon-right" v-if="!loading"></i>
            <span>{{ loading ? '身 份 验 证 中...' : '登 录 系 统' }}</span>
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 协议复选框（借鉴期末作业） -->
      <div class="agreement-section">
        <label class="agree-checkbox" @click.prevent="toggleAgreement">
          <input type="checkbox" v-model="agreed" />
          <span class="checkmark" :class="{ checked: agreed }">
            <i class="el-icon-check" v-if="agreed"></i>
          </span>
          <span class="agree-text">
            同意
            <a href="javascript:void(0)" class="agree-link" @click.stop>《用户协议》</a>
            和
            <a href="javascript:void(0)" class="agree-link" @click.stop>《隐私条款》</a>
          </span>
        </label>
      </div>

      <!-- 底部操作链接 -->
      <div class="login-options">
        <span class="action-link" @click="goToRegister">
          <i class="el-icon-edit-outline"></i> 立即注册
        </span>
        <span class="action-link forgot-link" @click="goToForgotPassword">
          <i class="el-icon-question"></i> 忘记密码
        </span>
      </div>

      <!-- 第三方登录（借鉴期末作业） -->
      <div class="third-party-login">
        <div class="divider-line">
          <span class="divider-text">其它方式登录</span>
        </div>
        <div class="third-party-icons">
          <div class="icon-circle" title="微信登录">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="icon-circle" title="QQ登录">
            <i class="el-icon-s-custom"></i>
          </div>
          <div class="icon-circle" title="微博登录">
            <i class="el-icon-star-off"></i>
          </div>
        </div>
      </div>

      <div class="login-footer">
        <p>© 2025 员工管理系统 | 安全加密 · 智能办公</p>
      </div>
    </div>

    <!-- 协议弹窗（借鉴期末作业） -->
    <div class="modal-overlay" v-if="showAgreementModal" @click.self="showAgreementModal = false">
      <div class="modal-content">
        <div class="modal-icon">
          <i class="el-icon-warning"></i>
        </div>
        <p class="modal-text">请阅读并同意《用户协议》和《隐私条款》</p>
        <div class="modal-buttons">
          <el-button type="primary" class="modal-btn-agree" @click="agreeAndContinue">同意</el-button>
          <el-button class="modal-btn-cancel" @click="showAgreementModal = false">取消</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import VerifyCode from '../components/VerifyCode.vue'

export default {
  name: 'Login',

  components: {
    VerifyCode
  },

  data() {
    const validateVerifyCode = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入验证码'))
      } else if (!this.$refs.verifyCode.validate(value)) {
        callback(new Error('验证码错误，请重新输入'))
        if (this.$refs.verifyCode) this.$refs.verifyCode.refreshCode()
        this.loginForm.verifyCode = ''
      } else {
        callback()
      }
    }

    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('手机号格式不正确'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      bubbles: [],
      particles: [],
      activeTab: 'password',
      agreed: false,
      showAgreementModal: false,
      pendingAction: null,
      smsCooldown: 0,
      smsCooldownTimer: null,
      loginForm: {
        floginname: '',
        fpassword: '',
        verifyCode: ''
      },
      smsForm: {
        phone: '',
        smsCode: ''
      },
      loginRules: {
        floginname: [
          { required: true, message: '请输入用户名/工号', trigger: 'blur' },
          { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
        ],
        fpassword: [
          { required: true, message: '请输入登录密码', trigger: 'blur' },
          { min: 3, max: 20, message: '密码长度 3~20 位', trigger: 'blur' }
        ],
        verifyCode: [
          { required: true, validator: validateVerifyCode, trigger: 'blur' }
        ]
      },
      smsRules: {
        phone: [
          { required: true, validator: validatePhone, trigger: 'blur' }
        ],
        smsCode: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          { len: 6, message: '验证码为6位数字', trigger: 'blur' }
        ]
      }
    }
  },

  created() {
    const { username, password } = this.$route.query
    if (username) this.loginForm.floginname = username
    if (password) this.loginForm.fpassword = password

    this.initBackgroundAnimations()
  },

  mounted() {
    this.$nextTick(() => {
      if (this.$refs.verifyCode) {
        this.$refs.verifyCode.refreshCode()
      }
    })
  },

  beforeDestroy() {
    if (this.smsCooldownTimer) {
      clearInterval(this.smsCooldownTimer)
    }
  },

  methods: {
    initBackgroundAnimations() {
      const bubbleCount = 20
      for (let i = 0; i < bubbleCount; i++) {
        const size = Math.random() * 60 + 20
        this.bubbles.push({
          style: {
            width: `${size}px`,
            height: `${size}px`,
            left: `${Math.random() * 100}%`,
            animationDelay: `${Math.random() * 10}s`,
            animationDuration: `${Math.random() * 10 + 10}s`,
            opacity: Math.random() * 0.4 + 0.1
          }
        })
      }

      const particleCount = 30
      for (let i = 0; i < particleCount; i++) {
        const size = Math.random() * 5 + 2
        this.particles.push({
          style: {
            width: `${size}px`,
            height: `${size}px`,
            left: `${Math.random() * 100}%`,
            top: `${Math.random() * 100}%`,
            animationDuration: `${Math.random() * 15 + 10}s`,
            animationDelay: `${Math.random() * 5}s`,
            background: `rgba(255, 255, 255, ${Math.random() * 0.6 + 0.2})`
          }
        })
      }
    },

    switchTab(tab) {
      this.activeTab = tab
      if (tab === 'password') {
        this.$nextTick(() => {
          if (this.$refs.verifyCode) {
            this.$refs.verifyCode.refreshCode()
          }
        })
      }
    },

    toggleAgreement() {
      this.agreed = !this.agreed
    },

    checkAgreement(action) {
      if (!this.agreed) {
        this.pendingAction = action
        this.showAgreementModal = true
        return false
      }
      return true
    },

    agreeAndContinue() {
      this.agreed = true
      this.showAgreementModal = false
      if (this.pendingAction) {
        this.pendingAction()
        this.pendingAction = null
      }
    },

    onCodeRefresh() {},

    refreshVerifyCode() {
      if (this.$refs.verifyCode) {
        this.$refs.verifyCode.refreshCode()
      }
    },

    sendSmsCode() {
      if (!this.smsForm.phone) {
        this.$message.warning('请先输入手机号')
        return
      }
      if (!/^1[3-9]\d{9}$/.test(this.smsForm.phone)) {
        this.$message.warning('手机号格式不正确')
        return
      }
      if (!this.checkAgreement(() => this.sendSmsCode())) {
        return
      }

      this.smsCooldown = 60
      this.$message.success('验证码已发送至您的手机')
      this.smsCooldownTimer = setInterval(() => {
        this.smsCooldown--
        if (this.smsCooldown <= 0) {
          clearInterval(this.smsCooldownTimer)
          this.smsCooldownTimer = null
        }
      }, 1000)
    },

    handleLogin() {
      if (!this.checkAgreement(() => this.handleLogin())) {
        return
      }

      if (!this.loginForm.verifyCode) {
        this.$message.warning({ message: '请输入验证码', duration: 2000 })
        return
      }
      if (this.$refs.verifyCode && !this.$refs.verifyCode.validate(this.loginForm.verifyCode)) {
        this.$message.error({ message: '验证码输入错误，请重新输入', duration: 2500 })
        if (this.$refs.verifyCode) this.$refs.verifyCode.refreshCode()
        this.loginForm.verifyCode = ''
        return
      }

      this.$refs.loginForm.validate((valid, errorFields) => {
        if (!valid) {
          if (errorFields && errorFields.verifyCode) {
            this.$message.warning({ message: '请先输入验证码', duration: 2000 })
          } else if (errorFields && errorFields.floginname) {
            this.$message.warning({ message: '请输入用户名/工号', duration: 2000 })
          } else if (errorFields && errorFields.fpassword) {
            this.$message.warning({ message: '请输入登录密码', duration: 2000 })
          } else {
            this.$message.warning({ message: '请完善登录信息', duration: 2000 })
          }
          return
        }

        this.loading = true
        this.$axios.post('/login', {
          floginname: this.loginForm.floginname,
          fpassword: this.loginForm.fpassword
        }, { silent: true }).then(res => {
          if (res.success) {
            sessionStorage.removeItem('registerFormData')
            this.$message.success({
              message: res.message || '登录成功，欢迎回来！',
              duration: 1500,
              showClose: true
            })
            this.$store.dispatch('login', res.data.user)
            setTimeout(() => {
              this.$router.push('/home')
            }, 500)
          } else {
            const errMsg = res.message || '登录失败'
            if (errMsg === '用户不存在') {
              this.$message.error({ message: '该用户不存在，请检查用户名', duration: 3000 })
            } else if (errMsg.includes('密码')) {
              this.$message.error({ message: '账号或密码错误，请重试', duration: 3000 })
            } else if (errMsg.includes('禁用') || errMsg.includes('冻结')) {
              this.$message.warning({ message: errMsg + '，请联系管理员', duration: 3000 })
            } else {
              this.$message.error({ message: errMsg, duration: 3000 })
            }
          }
        }).catch(() => {
          this.$message.error({ message: '网络错误，请稍后重试', duration: 3000 })
        }).finally(() => {
          this.loading = false
        })
      })
    },

    handleSmsLogin() {
      if (!this.checkAgreement(() => this.handleSmsLogin())) {
        return
      }

      this.$refs.smsForm.validate((valid) => {
        if (!valid) return

        this.loading = true
        this.$axios.post('/login', {
          floginname: this.smsForm.phone,
          fpassword: this.smsForm.smsCode
        }, { silent: true }).then(res => {
          if (res.success) {
            sessionStorage.removeItem('registerFormData')
            this.$message.success({
              message: res.message || '登录成功，欢迎回来！',
              duration: 1500
            })
            this.$store.dispatch('login', res.data.user)
            setTimeout(() => {
              this.$router.push('/home')
            }, 500)
          } else {
            this.$message.error({ message: res.message || '登录失败', duration: 3000 })
          }
        }).catch(() => {
          this.$message.error({ message: '网络错误，请稍后重试', duration: 3000 })
        }).finally(() => {
          this.loading = false
        })
      })
    },

    goToRegister() {
      this.$router.push('/register')
    },

    goToForgotPassword() {
      this.$router.push('/forgot-password')
    }
  }
}
</script>

<style scoped>
/* 基础容器 */
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: radial-gradient(circle at top left, #667eea, #764ba2, #6B8DD6);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  position: relative;
  overflow: hidden;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 动态背景层 */
.bg-animation, .particles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;
  pointer-events: none;
}

/* 气泡动画 */
.bubble {
  position: absolute;
  bottom: -100px;
  background: radial-gradient(circle at 30% 30%, rgba(255,255,255,0.4), rgba(255,255,255,0.05));
  border-radius: 50%;
  backdrop-filter: blur(2px);
  border: 1px solid rgba(255,255,255,0.2);
  animation: rise linear infinite;
  will-change: transform;
}

@keyframes rise {
  0% { transform: translateY(0) scale(1) rotate(0deg); opacity: 1; }
  100% { transform: translateY(-110vh) scale(1.2) rotate(360deg); opacity: 0; }
}

/* 漂浮粒子 */
.particle {
  position: absolute;
  border-radius: 50%;
  box-shadow: 0 0 10px rgba(255,255,255,0.3);
  animation: floatParticle linear infinite alternate;
}

@keyframes floatParticle {
  0% { transform: translate(0, 0); }
  100% { transform: translate(30px, -50px); }
}

/* 静态光晕 */
.glow-effect {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  pointer-events: none;
}

.glow-1 {
  width: 350px;
  height: 350px;
  top: -100px;
  left: -100px;
  background: rgba(142, 104, 255, 0.4);
}

.glow-2 {
  width: 450px;
  height: 450px;
  bottom: -100px;
  right: -50px;
  background: rgba(100, 200, 255, 0.3);
}

/* 核心登录面板 */
.login-box {
  width: 460px;
  padding: 45px 40px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow:
    0 25px 50px rgba(0, 0, 0, 0.15),
    inset 0 0 0 1px rgba(255,255,255,0.4),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  position: relative;
  z-index: 10;
  transition: transform 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275), box-shadow 0.5s ease;
  animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.login-box::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.8), transparent);
  border-radius: 30px 30px 0 0;
}

.login-box:hover {
  transform: translateY(-10px) scale(1.01);
  box-shadow:
    0 40px 70px rgba(0, 0, 0, 0.2),
    inset 0 0 0 1px rgba(255,255,255,0.5),
    0 0 80px rgba(102, 126, 234, 0.1);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(50px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 头部样式 */
.login-header {
  text-align: center;
  margin-bottom: 25px;
}

.logo-icon {
  width: 76px;
  height: 76px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  box-shadow: 0 12px 24px rgba(118, 75, 162, 0.35);
  animation: floatLogo 4s ease-in-out infinite alternate;
}

@keyframes floatLogo {
  0% { transform: translateY(0px) scale(1); }
  100% { transform: translateY(-8px) scale(1.02); }
}

.logo-icon i {
  font-size: 38px;
  color: #fff;
}

.login-header h2 {
  font-size: 26px;
  color: #2c3e50;
  margin-bottom: 6px;
  font-weight: 700;
  letter-spacing: 1px;
}

.login-header p {
  font-size: 12px;
  color: #8392a5;
  letter-spacing: 1px;
  font-weight: 500;
  text-transform: uppercase;
}

/* 登录方式标签页（借鉴期末作业） */
.login-tabs {
  display: flex;
  margin-bottom: 25px;
  border-bottom: 1px solid rgba(238, 238, 238, 0.6);
  position: relative;
}

.login-tab {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  cursor: pointer;
  color: #8392a5;
  font-size: 15px;
  font-weight: 500;
  position: relative;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.login-tab:hover {
  color: #667eea;
}

.login-tab.active {
  color: #667eea;
  font-weight: 600;
}

.login-tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  height: 3px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-radius: 3px 3px 0 0;
}

.login-tab i {
  font-size: 16px;
}

/* 表单输入框 */
.login-form .el-form-item {
  margin-bottom: 22px;
}

.login-form .el-input >>> .el-input__inner {
  height: 54px;
  line-height: 54px;
  border-radius: 16px;
  border: 1.5px solid rgba(220, 223, 230, 0.8);
  background-color: rgba(255, 255, 255, 0.8);
  font-size: 15px;
  padding-left: 48px;
  color: #303133;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.login-form .el-input >>> .el-input__inner:focus {
  border-color: #667eea;
  background-color: #fff;
  box-shadow:
    0 0 0 4px rgba(102, 126, 234, 0.15),
    0 8px 24px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.login-form .el-input >>> .el-input__prefix {
  left: 18px;
  font-size: 20px;
  line-height: 54px;
  color: #a0a6b1;
  transition: color 0.3s;
}

.login-form .el-input:focus-within >>> .el-input__prefix {
  color: #667eea;
}

/* 验证码特殊布局 */
.verify-code-wrapper {
  display: flex;
  gap: 14px;
  align-items: center;
}

.verify-input {
  flex: 1;
}

.verify-canvas-box {
  height: 46px;
  min-width: 130px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  flex-shrink: 0;
}

.verify-canvas-box:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.2);
}

.verify-canvas-box:active {
  transform: scale(0.97);
}

/* 提示文字 */
.verify-hint {
  display: flex;
  justify-content: flex-end;
  margin-top: 6px;
  padding-right: 4px;
}

.hint-text {
  font-size: 12px;
  color: #a0a6b1;
  transition: color 0.2s;
  cursor: default;
  user-select: none;
}

.verify-code-item:hover .hint-text {
  color: #667eea;
}

/* 短信验证码布局 */
.sms-code-wrapper {
  display: flex;
  gap: 14px;
  align-items: center;
}

.sms-input {
  flex: 1;
}

.sms-send-btn {
  height: 54px;
  min-width: 120px;
  border-radius: 16px;
  font-size: 14px;
  font-weight: 500;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
  white-space: nowrap;
  transition: all 0.3s ease;
}

.sms-send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(118, 75, 162, 0.3);
}

.sms-send-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  height: 54px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  letter-spacing: 2px;
  color: #fff;
  box-shadow: 0 10px 20px -5px rgba(118, 75, 162, 0.5);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.25), transparent);
  transition: left 0.6s ease;
}

.login-button:hover::before {
  left: 100%;
}

.login-button:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 20px 40px -10px rgba(118, 75, 162, 0.6);
  filter: brightness(1.05);
}

.login-button:active {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 8px 16px -4px rgba(118, 75, 162, 0.5);
}

.login-button i {
  margin-right: 8px;
  font-size: 18px;
}

/* 协议复选框区域（借鉴期末作业） */
.agreement-section {
  margin: 15px 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.agree-checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
  font-size: 13px;
  color: #606266;
  user-select: none;
}

.agree-checkbox input[type="checkbox"] {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  border: 2px solid #dcdfe6;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.checkmark.checked {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-color: #667eea;
}

.checkmark i {
  color: #fff;
  font-size: 12px;
  font-weight: bold;
}

.agree-text {
  line-height: 1.4;
}

.agree-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.agree-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 底部操作链接 */
.login-options {
  display: flex;
  justify-content: space-between;
  margin: 15px 0 0;
  padding: 0 5px;
}

.action-link {
  font-size: 14px;
  color: #5c6bc0;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.2s, transform 0.2s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.action-link:hover {
  color: #3949ab;
  transform: scale(1.05);
}

.forgot-link {
  color: #909399;
}

.forgot-link:hover {
  color: #667eea;
}

/* 第三方登录区域（借鉴期末作业） */
.third-party-login {
  margin-top: 25px;
  text-align: center;
}

.divider-line {
  display: flex;
  align-items: center;
  margin-bottom: 18px;
}

.divider-line::before,
.divider-line::after {
  content: '';
  flex: 1;
  height: 1px;
  background: rgba(220, 223, 230, 0.6);
}

.divider-text {
  padding: 0 15px;
  font-size: 13px;
  color: #a0a6b1;
  white-space: nowrap;
}

.third-party-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.icon-circle {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(220, 223, 230, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.icon-circle:hover {
  background: rgba(102, 126, 234, 0.1);
  border-color: #667eea;
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.15);
}

.icon-circle i {
  font-size: 20px;
  color: #667eea;
}

/* 版权信息 */
.login-footer {
  text-align: center;
  margin-top: 20px;
}

.login-footer p {
  font-size: 12px;
  color: rgba(131, 146, 165, 0.8);
  letter-spacing: 0.5px;
}

/* 协议弹窗（借鉴期末作业） */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  background: #fff;
  border-radius: 20px;
  padding: 35px 40px;
  width: 90%;
  max-width: 380px;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  animation: modalSlideIn 0.3s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes modalSlideIn {
  from { opacity: 0; transform: translateY(20px) scale(0.95); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.modal-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 18px;
  background: linear-gradient(135deg, #ffecd2, #fcb69f);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-icon i {
  font-size: 30px;
  color: #e6a23c;
}

.modal-text {
  font-size: 15px;
  color: #303133;
  line-height: 1.6;
  margin-bottom: 25px;
}

.modal-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.modal-btn-agree {
  flex: 1;
  height: 44px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: #fff;
}

.modal-btn-agree:hover {
  filter: brightness(1.1);
}

.modal-btn-cancel {
  flex: 1;
  height: 44px;
  border-radius: 12px;
  font-size: 15px;
  font-weight: 500;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  color: #606266;
}

.modal-btn-cancel:hover {
  background: #eef1f6;
}

/* 响应式适配 */
@media (max-width: 550px) {
  .login-box {
    width: 92%;
    padding: 35px 28px;
  }
  .logo-icon {
    width: 64px;
    height: 64px;
    border-radius: 20px;
  }
  .logo-icon i {
    font-size: 32px;
  }
  .login-header h2 {
    font-size: 22px;
  }
  .login-tab {
    font-size: 14px;
  }
  .sms-send-btn {
    min-width: 100px;
    font-size: 13px;
  }
}
</style>
