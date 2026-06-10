<template>
  <div class="register-container">
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

    <div class="register-box">
      <div class="register-header">
        <div class="logo-icon">
          <i class="el-icon-edit-outline"></i>
        </div>
        <h2>员工注册</h2>
        <p>Create Your Account</p>
      </div>

      <el-form
        ref="registerForm"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
        label-position="top"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="用户名" prop="floginname">
              <el-input
                v-model="registerForm.floginname"
                placeholder="请输入用户名"
                maxlength="50"
                clearable
                prefix-icon="el-icon-user"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="fnickname">
              <el-input
                v-model="registerForm.fnickname"
                placeholder="请输入昵称"
                maxlength="50"
                clearable
                prefix-icon="el-icon-postcard"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="密码" prop="fpassword">
              <el-input
                v-model="registerForm.fpassword"
                type="password"
                placeholder="英文+数字，至少6位"
                maxlength="50"
                show-password
                clearable
                prefix-icon="el-icon-lock"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                maxlength="50"
                show-password
                clearable
                prefix-icon="el-icon-lock"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="fbirthday">
              <el-date-picker
                v-model="registerForm.fbirthday"
                type="date"
                placeholder="选择出生日期"
                value-format="yyyy-MM-dd"
                :picker-options="datePickerOptions"
                style="width: 100%"
                prefix-icon="el-icon-date"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话号码" prop="fphonenumber">
              <el-input
                v-model="registerForm.fphonenumber"
                placeholder="请输入电话号码"
                maxlength="20"
                clearable
                prefix-icon="el-icon-mobile-phone"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="电子邮箱" prop="femail">
          <el-input
            v-model="registerForm.femail"
            placeholder="请输入企业或个人电子邮箱"
            maxlength="50"
            clearable
            prefix-icon="el-icon-message"
          />
        </el-form-item>

        <el-form-item class="button-item">
          <el-button
            type="primary"
            :loading="loading"
            class="register-button"
            @click="handleRegister"
          >
            <i class="el-icon-check" v-if="!loading"></i>
            <span>{{ loading ? '提 交 注 册 中...' : '立 即 注 册' }}</span>
          </el-button>
          <el-button class="back-button" @click="goToLogin">
            <i class="el-icon-back"></i> 返回登录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 协议提示（借鉴期末作业） -->
      <div class="terms-section">
        <p class="terms-text">
          注册即表示您同意我们的
          <a href="javascript:void(0)" class="terms-link">《用户协议》</a>
          和
          <a href="javascript:void(0)" class="terms-link">《隐私条款》</a>
        </p>
      </div>

      <!-- 底部链接区域 -->
      <div class="register-footer-links">
        <span class="footer-link" @click="goToLogin">
          <i class="el-icon-d-arrow-left"></i> 已有账号？立即登录
        </span>
        <span class="footer-link forgot-link" @click="goToForgotPassword">
          <i class="el-icon-question"></i> 忘记密码
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Register',

  data() {
    const validatePassword = (rule, value, callback) => {
      const pattern = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,50}$/
      if (!value) {
        callback(new Error('请输入密码'))
      } else if (!pattern.test(value)) {
        callback(new Error('密码必须包含英文和数字，且长度6-50位'))
      } else {
        callback()
      }
    }

    const validateConfirmPassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.fpassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    const validatePhone = (rule, value, callback) => {
      if (!value) return callback()
      const pattern = /^1[3-9]\d{9}$/
      if (!pattern.test(value)) {
        callback(new Error('手机号格式不正确'))
      } else {
        callback()
      }
    }

    const validateEmail = (rule, value, callback) => {
      if (!value) return callback()
      const pattern = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
      if (!pattern.test(value)) {
        callback(new Error('邮箱格式不正确'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      bubbles: [],
      particles: [],
      datePickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      },
      registerForm: {
        floginname: '',
        fpassword: '',
        confirmPassword: '',
        fnickname: '',
        fbirthday: '',
        fphonenumber: '',
        femail: ''
      },
      registerRules: {
        floginname: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { max: 50, message: '不能超过50个字符', trigger: 'blur' }
        ],
        fpassword: [{ required: true, validator: validatePassword, trigger: 'blur' }],
        confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
        fnickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { max: 50, message: '不能超过50个字符', trigger: 'blur' }
        ],
        fbirthday: [{ required: true, message: '请选择出生日期', trigger: 'change' }],
        fphonenumber: [{ validator: validatePhone, trigger: 'blur' }],
        femail: [
          { max: 50, message: '不能超过50个字符', trigger: 'blur' },
          { validator: validateEmail, trigger: 'blur' }
        ]
      }
    }
  },

  watch: {
    registerForm: {
      deep: true,
      handler() {
        this.saveFormData()
      }
    }
  },

  created() {
    this.initBackgroundAnimations()
  },

  mounted() {
    this.loadFormData()
    window.addEventListener('beforeunload', this.clearSavedFormData)
  },

  beforeDestroy() {
    window.removeEventListener('beforeunload', this.clearSavedFormData)
  },

  methods: {
    initBackgroundAnimations() {
      for (let i = 0; i < 15; i++) {
        const size = Math.random() * 50 + 20
        this.bubbles.push({
          style: {
            width: `${size}px`,
            height: `${size}px`,
            left: `${Math.random() * 100}%`,
            animationDelay: `${Math.random() * 10}s`,
            animationDuration: `${Math.random() * 12 + 8}s`,
            opacity: Math.random() * 0.3 + 0.1
          }
        })
      }
      for (let i = 0; i < 25; i++) {
        const size = Math.random() * 4 + 2
        this.particles.push({
          style: {
            width: `${size}px`,
            height: `${size}px`,
            left: `${Math.random() * 100}%`,
            top: `${Math.random() * 100}%`,
            animationDuration: `${Math.random() * 15 + 10}s`,
            animationDelay: `${Math.random() * 5}s`,
            background: `rgba(255, 255, 255, ${Math.random() * 0.5 + 0.2})`
          }
        })
      }
    },

    saveFormData() {
      const dataToSave = {
        floginname: this.registerForm.floginname,
        fpassword: this.registerForm.fpassword,
        confirmPassword: this.registerForm.confirmPassword,
        fnickname: this.registerForm.fnickname,
        fbirthday: this.registerForm.fbirthday,
        fphonenumber: this.registerForm.fphonenumber,
        femail: this.registerForm.femail
      }
      sessionStorage.setItem('registerFormData', JSON.stringify(dataToSave))
    },

    loadFormData() {
      const savedData = sessionStorage.getItem('registerFormData')
      if (savedData) {
        try {
          const parsedData = JSON.parse(savedData)
          this.registerForm.floginname = parsedData.floginname || ''
          this.registerForm.fpassword = parsedData.fpassword || ''
          this.registerForm.confirmPassword = parsedData.confirmPassword || ''
          this.registerForm.fnickname = parsedData.fnickname || ''
          this.registerForm.fbirthday = parsedData.fbirthday || ''
          this.registerForm.fphonenumber = parsedData.fphonenumber || ''
          this.registerForm.femail = parsedData.femail || ''
        } catch (e) {
          console.error('读取缓存失败', e)
        }
      }
    },

    clearSavedFormData() {
      sessionStorage.removeItem('registerFormData')
    },

    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          const submitData = { ...this.registerForm }
          delete submitData.confirmPassword

          this.$axios.post('/register', submitData, { silent: true }).then(res => {
            if (res.success) {
              this.clearSavedFormData()
              this.$message.success({
                message: '注册成功，即将跳转到登录页...',
                duration: 2000
              })
              setTimeout(() => {
                this.$router.push({
                  path: '/login',
                  query: {
                    username: this.registerForm.floginname,
                    password: this.registerForm.fpassword
                  }
                })
              }, 1500)
            } else {
              this.$message.error(res.message || '注册失败，请重试')
            }
          }).catch(() => {
            this.$message.error('网络错误，请稍后重试')
          }).finally(() => {
            this.loading = false
          })
        } else {
          this.$message.warning('请正确填写所有的必填信息')
        }
      })
    },

    goToLogin() {
      this.$router.push({
        path: '/login',
        query: {
          username: this.registerForm.floginname,
          password: this.registerForm.fpassword
        }
      })
    },

    goToForgotPassword() {
      this.$router.push('/forgot-password')
    }
  }
}
</script>

<style scoped>
/* 基础容器 */
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: radial-gradient(circle at 10% 30%, rgba(102, 126, 234, 0.9), rgba(118, 75, 162, 0.95)),
              linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  background-size: 200% 200%;
  animation: gradientShift 15s ease infinite;
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 动画背景层 */
.bg-animation, .particles-container {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  overflow: hidden;
  z-index: 1;
  pointer-events: none;
}

.bubble {
  position: absolute;
  bottom: -100px;
  background: radial-gradient(circle at 30% 30%, rgba(255,255,255,0.4), rgba(255,255,255,0.05));
  border-radius: 50%;
  backdrop-filter: blur(2px);
  border: 1px solid rgba(255,255,255,0.2);
  animation: rise linear infinite;
}

@keyframes rise {
  0% { transform: translateY(0) scale(1) rotate(0deg); opacity: 1; }
  100% { transform: translateY(-120vh) scale(1.2) rotate(360deg); opacity: 0; }
}

.particle {
  position: absolute;
  border-radius: 50%;
  box-shadow: 0 0 10px rgba(255,255,255,0.3);
  animation: floatParticle linear infinite alternate;
}

@keyframes floatParticle {
  0% { transform: translate(0, 0); }
  100% { transform: translate(40px, -60px); }
}

/* 炫彩光晕效果 */
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
  background: radial-gradient(circle, rgba(156, 126, 245, 0.3), transparent 70%);
}

.glow-2 {
  width: 450px;
  height: 450px;
  bottom: -80px;
  right: -50px;
  background: radial-gradient(circle, rgba(210, 140, 255, 0.25), transparent 70%);
}

/* 高级毛玻璃卡片 */
.register-box {
  width: 720px;
  padding: 45px 50px 35px;
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15), inset 0 0 0 1px rgba(255,255,255,0.4);
  position: relative;
  z-index: 10;
  transition: transform 0.4s ease, box-shadow 0.4s ease;
  animation: fadeInUp 0.7s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}

.register-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 35px 60px rgba(0, 0, 0, 0.2), inset 0 0 0 1px rgba(255,255,255,0.5);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(40px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 头部样式 */
.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  width: 76px;
  height: 76px;
  background: linear-gradient(135deg, #5f7bef, #7c52b3);
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  box-shadow: 0 12px 24px rgba(94, 97, 208, 0.35);
  animation: floatLogo 4s ease-in-out infinite alternate;
}

@keyframes floatLogo {
  0% { transform: translateY(0px) scale(1); }
  100% { transform: translateY(-8px) scale(1.02); }
}

.logo-icon i {
  font-size: 36px;
  color: #fff;
}

.register-header h2 {
  font-size: 26px;
  background: linear-gradient(135deg, #3f2b6d, #5e3b9e);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 8px;
  font-weight: 700;
  letter-spacing: 1px;
}

.register-header p {
  font-size: 12px;
  color: #8392a5;
  letter-spacing: 1px;
  font-weight: 500;
  text-transform: uppercase;
}

/* 表单输入框 */
.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-form >>> .el-form-item__label {
  padding-bottom: 4px;
  font-weight: 600;
  color: #475569;
  font-size: 13px;
  line-height: 1.2;
}

.register-form .el-input >>> .el-input__inner {
  height: 50px;
  line-height: 50px;
  border-radius: 14px;
  border: 1px solid rgba(220, 223, 230, 0.8);
  background-color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  padding-left: 42px;
}

.register-form .el-input >>> .el-input__inner:focus {
  border-color: #8b6ef0;
  background-color: #fff;
  box-shadow: 0 0 0 4px rgba(107, 93, 219, 0.15);
}

.register-form .el-input >>> .el-input__prefix {
  left: 14px;
  font-size: 18px;
  line-height: 50px;
  color: #a0a6b1;
  transition: color 0.3s;
}

.register-form .el-input:focus-within >>> .el-input__prefix {
  color: #8b6ef0;
}

.register-form .el-date-editor >>> .el-input__prefix {
  left: 14px;
}

/* 按钮组 */
.button-item {
  margin-top: 30px;
  margin-bottom: 0;
}

.button-item >>> .el-form-item__content {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.register-button, .back-button {
  height: 52px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  letter-spacing: 1px;
  transition: all 0.3s ease;
}

.register-button {
  flex: 3;
  background: linear-gradient(95deg, #6c5ce7, #a463f5);
  border: none;
  color: #fff;
  box-shadow: 0 10px 20px -5px rgba(108, 92, 231, 0.5);
}

.register-button:hover {
  background: linear-gradient(95deg, #5a4ad1, #9452e3);
  transform: translateY(-2px);
  box-shadow: 0 15px 25px -5px rgba(108, 92, 231, 0.6);
}

.register-button:active {
  transform: translateY(1px);
}

.back-button {
  flex: 2;
  background: rgba(108, 92, 231, 0.05);
  border: 1px solid rgba(108, 92, 231, 0.3);
  color: #6c5ce7;
}

.back-button:hover {
  background: #fff;
  border-color: #6c5ce7;
  color: #6c5ce7;
  transform: translateY(-2px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.05);
}

.register-button i, .back-button i {
  margin-right: 6px;
  font-size: 18px;
}

/* 协议提示（借鉴期末作业） */
.terms-section {
  text-align: center;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid rgba(238, 238, 238, 0.5);
}

.terms-text {
  font-size: 13px;
  color: #909399;
  margin: 0;
  line-height: 1.6;
}

.terms-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.terms-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 底部链接区域 */
.register-footer-links {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding: 0 5px;
}

.footer-link {
  font-size: 14px;
  color: #5c6bc0;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.2s, transform 0.2s;
  display: flex;
  align-items: center;
  gap: 5px;
}

.footer-link:hover {
  color: #3949ab;
  transform: scale(1.05);
}

.forgot-link {
  color: #909399;
}

.forgot-link:hover {
  color: #667eea;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .register-box {
    width: 95%;
    padding: 35px 25px 25px;
  }
  .button-item >>> .el-form-item__content {
    flex-direction: column;
    gap: 15px;
  }
  .register-button, .back-button {
    width: 100%;
    flex: none;
  }
  .register-footer-links {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
</style>
