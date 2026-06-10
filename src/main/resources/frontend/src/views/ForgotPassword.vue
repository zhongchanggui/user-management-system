<template>
  <div class="forgot-container">
    <div class="bg-animation">
      <div v-for="(bubble, index) in bubbles" :key="'b-' + index" class="bubble" :style="bubble.style"></div>
    </div>
    <div class="glow-effect glow-1"></div>
    <div class="glow-effect glow-2"></div>

    <div class="forgot-box">
      <div class="forgot-header">
        <div class="logo-icon">
          <i class="el-icon-key"></i>
        </div>
        <h2>找回密码</h2>
        <p>Reset Your Password</p>
      </div>

      <!-- 步骤条 -->
      <el-steps :active="step" finish-status="success" align-center class="steps-bar">
        <el-step title="验证身份" icon="el-icon-user"></el-step>
        <el-step title="重置密码" icon="el-icon-edit"></el-step>
        <el-step title="完成" icon="el-icon-success"></el-step>
      </el-steps>

      <!-- 步骤1: 验证身份 -->
      <div v-if="step === 0" class="step-content">
        <el-form :model="verifyForm" :rules="verifyRules" ref="verifyForm" class="forgot-form">
          <el-form-item prop="floginname">
            <el-input v-model="verifyForm.floginname" placeholder="请输入用户名/工号" prefix-icon="el-icon-user" size="large" clearable />
          </el-form-item>
          <el-form-item prop="femail">
            <el-input v-model="verifyForm.femail" placeholder="请输入注册邮箱" prefix-icon="el-icon-message" size="large" clearable />
          </el-form-item>
          <el-form-item prop="verifyCode">
            <div class="verify-row">
              <el-input v-model="verifyForm.verifyCode" placeholder="邮箱验证码" prefix-icon="el-icon-key" size="large" clearable class="verify-input" />
              <el-button type="primary" size="large" :disabled="codeCooldown > 0" @click="sendCode" class="code-btn">
                {{ codeCooldown > 0 ? codeCooldown + 's后重发' : '发送验证码' }}
              </el-button>
            </div>
          </el-form-item>
          <el-button type="primary" class="submit-btn" @click="handleVerify">下一步</el-button>
        </el-form>
      </div>

      <!-- 步骤2: 重置密码 -->
      <div v-if="step === 1" class="step-content">
        <el-form :model="resetForm" :rules="resetRules" ref="resetForm" class="forgot-form">
          <el-form-item prop="newPassword">
            <el-input v-model="resetForm.newPassword" type="password" placeholder="请输入新密码" prefix-icon="el-icon-lock" show-password size="large" />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="resetForm.confirmPassword" type="password" placeholder="请确认新密码" prefix-icon="el-icon-lock" show-password size="large" />
          </el-form-item>
          <div class="password-strength">
            <span class="strength-label">密码强度：</span>
            <div class="strength-bars">
              <div class="strength-bar" :class="{ active: passwordStrength >= 1, weak: passwordStrength === 1, medium: passwordStrength === 2, strong: passwordStrength >= 3 }"></div>
              <div class="strength-bar" :class="{ active: passwordStrength >= 2, medium: passwordStrength === 2, strong: passwordStrength >= 3 }"></div>
              <div class="strength-bar" :class="{ active: passwordStrength >= 3, strong: passwordStrength >= 3 }"></div>
            </div>
            <span class="strength-text" :class="{ weak: passwordStrength === 1, medium: passwordStrength === 2, strong: passwordStrength >= 3 }">
              {{ ['', '弱', '中', '强'][passwordStrength] }}
            </span>
          </div>
          <el-button type="primary" class="submit-btn" @click="handleReset">重置密码</el-button>
        </el-form>
      </div>

      <!-- 步骤3: 完成 -->
      <div v-if="step === 2" class="step-content success-content">
        <div class="success-icon">
          <i class="el-icon-success"></i>
        </div>
        <h3>密码重置成功！</h3>
        <p>您的密码已成功修改，请使用新密码登录。</p>
        <el-button type="primary" class="submit-btn" @click="goToLogin">返回登录</el-button>
      </div>

      <div class="back-link" @click="goToLogin">
        <i class="el-icon-back"></i> 返回登录
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ForgotPassword',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.resetForm.newPassword) {
        callback(new Error('两次密码输入不一致'))
      } else {
        callback()
      }
    }
    return {
      step: 0,
      codeCooldown: 0,
      bubbles: [],
      verifyForm: { floginname: '', femail: '', verifyCode: '' },
      verifyRules: {
        floginname: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        femail: [{ required: true, message: '请输入邮箱', trigger: 'blur' }, { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
        verifyCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      },
      resetForm: { newPassword: '', confirmPassword: '' },
      resetRules: {
        newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
        confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
      }
    }
  },
  computed: {
    passwordStrength() {
      const pwd = this.resetForm.newPassword
      if (!pwd) return 0
      let score = 0
      if (pwd.length >= 6) score++
      if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) score++
      if (/\d/.test(pwd) && /[^a-zA-Z0-9]/.test(pwd)) score++
      return score
    }
  },
  created() {
    this.initBubbles()
  },
  methods: {
    initBubbles() {
      for (let i = 0; i < 15; i++) {
        const size = Math.random() * 50 + 20
        this.bubbles.push({ style: { width: `${size}px`, height: `${size}px`, left: `${Math.random() * 100}%`, animationDelay: `${Math.random() * 10}s`, animationDuration: `${Math.random() * 10 + 10}s`, opacity: Math.random() * 0.3 + 0.1 } })
      }
    },
    sendCode() {
      if (!this.verifyForm.femail) {
        this.$message.warning('请先输入邮箱')
        return
      }
      this.codeCooldown = 60
      const timer = setInterval(() => {
        this.codeCooldown--
        if (this.codeCooldown <= 0) clearInterval(timer)
      }, 1000)
      this.$message.success('验证码已发送至您的邮箱')
    },
    handleVerify() {
      this.$refs.verifyForm.validate(valid => {
        if (valid) {
          this.step = 1
        }
      })
    },
    handleReset() {
      this.$refs.resetForm.validate(valid => {
        if (valid) {
          this.step = 2
          this.$message.success('密码重置成功')
        }
      })
    },
    goToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.forgot-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: radial-gradient(circle at top left, #667eea, #764ba2, #6B8DD6);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  position: relative;
  overflow: hidden;
}
@keyframes gradientShift { 0% { background-position: 0% 50%; } 50% { background-position: 100% 50%; } 100% { background-position: 0% 50%; } }

.bg-animation { position: absolute; top: 0; left: 0; width: 100%; height: 100%; overflow: hidden; z-index: 1; pointer-events: none; }
.bubble { position: absolute; bottom: -100px; background: radial-gradient(circle at 30% 30%, rgba(255,255,255,0.3), rgba(255,255,255,0.05)); border-radius: 50%; animation: rise linear infinite; }
@keyframes rise { 0% { transform: translateY(0) scale(1); opacity: 0.8; } 100% { transform: translateY(-110vh) scale(0.5); opacity: 0; } }

.glow-effect { position: absolute; border-radius: 50%; filter: blur(80px); z-index: 0; pointer-events: none; }
.glow-1 { width: 300px; height: 300px; top: -80px; left: -80px; background: rgba(142, 104, 255, 0.4); }
.glow-2 { width: 400px; height: 400px; bottom: -80px; right: -40px; background: rgba(100, 200, 255, 0.3); }

.forgot-box {
  width: 460px;
  padding: 40px 40px 30px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 30px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 10;
  animation: fadeInUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
@keyframes fadeInUp { from { opacity: 0; transform: translateY(50px); } to { opacity: 1; transform: translateY(0); } }

.forgot-header { text-align: center; margin-bottom: 30px; }
.logo-icon { width: 70px; height: 70px; background: linear-gradient(135deg, #667eea, #764ba2); border-radius: 22px; display: flex; align-items: center; justify-content: center; margin: 0 auto 16px; box-shadow: 0 12px 24px rgba(118, 75, 162, 0.35); }
.logo-icon i { font-size: 34px; color: #fff; }
.forgot-header h2 { font-size: 24px; color: #2c3e50; margin-bottom: 6px; font-weight: 700; }
.forgot-header p { font-size: 12px; color: #8392a5; letter-spacing: 1px; text-transform: uppercase; }

.steps-bar { margin-bottom: 30px; }

.step-content { animation: fadeIn 0.3s ease; }
@keyframes fadeIn { from { opacity: 0; } to { opacity: 1; } }

.forgot-form .el-form-item { margin-bottom: 22px; }
.forgot-form .el-input >>> .el-input__inner { height: 50px; border-radius: 14px; border: 1px solid rgba(220, 223, 230, 0.8); background: rgba(255,255,255,0.8); font-size: 15px; padding-left: 45px; }
.forgot-form .el-input >>> .el-input__inner:focus { border-color: #667eea; box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15); }
.forgot-form .el-input >>> .el-input__prefix { left: 15px; font-size: 18px; color: #a0a6b1; }

.verify-row { display: flex; gap: 12px; }
.verify-input { flex: 1; }
.code-btn { border-radius: 14px; white-space: nowrap; }

.password-strength { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; padding: 0 4px; }
.strength-label { font-size: 13px; color: #94a3b8; }
.strength-bars { display: flex; gap: 4px; }
.strength-bar { width: 40px; height: 6px; background: #e9eef3; border-radius: 3px; transition: all 0.3s; }
.strength-bar.active.weak { background: #f56c6c; }
.strength-bar.active.medium { background: #e6a23c; }
.strength-bar.active.strong { background: #67c23a; }
.strength-text { font-size: 12px; font-weight: 600; }
.strength-text.weak { color: #f56c6c; }
.strength-text.medium { color: #e6a23c; }
.strength-text.strong { color: #67c23a; }

.submit-btn { width: 100%; height: 50px; font-size: 16px; font-weight: 600; border-radius: 14px; background: linear-gradient(135deg, #667eea, #764ba2); border: none; letter-spacing: 2px; box-shadow: 0 10px 20px -5px rgba(118, 75, 162, 0.5); margin-top: 8px; }
.submit-btn:hover { transform: translateY(-2px); box-shadow: 0 14px 28px -5px rgba(118, 75, 162, 0.6); }

.success-content { text-align: center; padding: 20px 0; }
.success-icon { width: 80px; height: 80px; background: linear-gradient(135deg, #67c23a, #85ce61); border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 20px; }
.success-icon i { font-size: 40px; color: #fff; }
.success-content h3 { font-size: 22px; color: #1e293b; margin-bottom: 10px; }
.success-content p { font-size: 14px; color: #94a3b8; margin-bottom: 30px; }

.back-link { text-align: center; margin-top: 24px; font-size: 14px; color: #5c6bc0; cursor: pointer; transition: all 0.2s; }
.back-link:hover { color: #3949ab; transform: translateX(-3px); }
.back-link i { margin-right: 4px; }

@media (max-width: 520px) { .forgot-box { width: 90%; padding: 30px 24px 20px; } }
</style>