<template>
  <div class="profile-container">
    <el-row :gutter="24">
      <!-- 个人信息卡片 -->
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card class="profile-card" shadow="hover">
          <div class="profile-avatar-section">
            <el-avatar :size="100" class="profile-avatar">
              {{ currentUser ? currentUser.fnickname.charAt(0).toUpperCase() : 'U' }}
            </el-avatar>
            <h2 class="profile-name">{{ currentUser ? currentUser.fnickname : '' }}</h2>
            <el-tag :type="getStateType(currentUser ? currentUser.fstate : 1)" effect="dark" size="small">
              {{ getStateText(currentUser ? currentUser.fstate : 1) }}
            </el-tag>
          </div>
          <el-divider></el-divider>
          <div class="profile-basic-info">
            <div class="info-row"><i class="el-icon-user"></i><span>用户名：</span><strong>{{ currentUser ? currentUser.floginname : '-' }}</strong></div>
            <div class="info-row"><i class="el-icon-phone"></i><span>电话：</span><strong>{{ currentUser ? currentUser.fphonenumber || '未设置' : '-' }}</strong></div>
            <div class="info-row"><i class="el-icon-message"></i><span>邮箱：</span><strong>{{ currentUser ? currentUser.femail || '未设置' : '-' }}</strong></div>
            <div class="info-row"><i class="el-icon-date"></i><span>生日：</span><strong>{{ currentUser ? currentUser.fbirthday || '未设置' : '-' }}</strong></div>
            <div class="info-row"><i class="el-icon-time"></i><span>创建：</span><strong>{{ currentUser ? currentUser.fcreatedatetime || '-' : '-' }}</strong></div>
          </div>
          <!-- 注销账号按钮（非admin用户显示） -->
          <div class="delete-account-section" v-if="!isAdmin">
            <el-divider></el-divider>
            <el-button type="danger" plain class="delete-account-btn" @click="handleDeleteAccount">
              <i class="el-icon-delete"></i> 注销账号
            </el-button>
            <p class="delete-warning">注销后账号将永久删除，无法恢复</p>
          </div>
        </el-card>
      </el-col>

      <!-- 编辑资料 / 修改密码 -->
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card class="form-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-edit"></i> 编辑资料</span>
          </div>
          <el-form :model="profileForm" :rules="profileRules" ref="profileForm" label-width="100px">
            <el-form-item label="昵称" prop="fnickname">
              <el-input v-model="profileForm.fnickname" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="电话号码" prop="fphonenumber">
              <el-input v-model="profileForm.fphonenumber" placeholder="请输入电话号码" />
            </el-form-item>
            <el-form-item label="电子邮箱" prop="femail">
              <el-input v-model="profileForm.femail" placeholder="请输入电子邮箱" />
            </el-form-item>
            <el-form-item label="出生日期">
              <el-date-picker v-model="profileForm.fbirthday" type="date" placeholder="选择出生日期" value-format="yyyy-MM-dd" :picker-options="datePickerOptions" style="width:100%" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存修改</el-button>
              <el-button @click="resetProfile">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="form-card" shadow="hover" style="margin-top:24px">
          <div slot="header" class="card-header">
            <span><i class="el-icon-lock"></i> 修改密码</span>
          </div>
          <el-form :model="pwdForm" :rules="pwdRules" ref="pwdForm" label-width="100px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码（英文+数字，至少6位）" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="warning" @click="handleChangePassword" :loading="changingPwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Profile',
  computed: {
    ...mapGetters(['currentUser']),
    isAdmin() {
      return this.currentUser && this.currentUser.floginname === 'admin'
    }
  },
  data() {
    const validateConfirmPwd = (rule, value, callback) => {
      if (!value) return callback(new Error('请再次输入密码'))
      if (value !== this.pwdForm.newPassword) return callback(new Error('两次输入的密码不一致'))
      callback()
    }
    return {
      saving: false,
      changingPwd: false,
      datePickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      },
      profileForm: {
        fnickname: '',
        fphonenumber: '',
        femail: '',
        fbirthday: ''
      },
      profileRules: {
        fnickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
      },
      pwdForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [{ required: true, validator: validateConfirmPwd, trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.resetProfile()
  },
  methods: {
    resetProfile() {
      if (this.currentUser) {
        this.profileForm = {
          fnickname: this.currentUser.fnickname || '',
          fphonenumber: this.currentUser.fphonenumber || '',
          femail: this.currentUser.femail || '',
          fbirthday: this.currentUser.fbirthday || ''
        }
      }
    },
    handleSaveProfile() {
      this.$refs.profileForm.validate(valid => {
        if (!valid) return
        this.saving = true
        this.$axios.post('/user/edit', {
          fid: this.currentUser.fid,
          ...this.profileForm
        }).then(res => {
          if (res.success) {
            this.$message.success('保存成功')
            this.$store.dispatch('updateUser', { ...this.currentUser, ...this.profileForm })
          }
        }).catch(() => {
          this.$message.error('保存失败，请重试')
        }).finally(() => { this.saving = false })
      })
    },
    handleChangePassword() {
      // 先校验昵称是否为空
      if (!this.profileForm.fnickname || !this.profileForm.fnickname.trim()) {
        this.$message.warning('请先填写昵称，昵称不能为空')
        return
      }
      this.$refs.pwdForm.validate(valid => {
        if (!valid) return
        this.changingPwd = true
        this.$axios.post('/user/edit', {
          fid: this.currentUser.fid,
          fpassword: this.pwdForm.newPassword,
          fnickname: this.profileForm.fnickname
        }).then(res => {
          if (res.success) {
            this.$message.success('密码修改成功')
            this.pwdForm = { oldPassword: '', newPassword: '', confirmPassword: '' }
          }
        }).catch(() => {
          this.$message.error('密码修改失败，请重试')
        }).finally(() => { this.changingPwd = false })
      })
    },
    getStateType(state) {
      return { 0: 'danger', 1: 'success', 2: 'warning' }[state] || 'info'
    },
    getStateText(state) {
      return { 0: '禁用', 1: '正常', 2: '冻结' }[state] || '未知'
    },
    handleDeleteAccount() {
      this.$confirm('确定要注销账号吗？注销后您的所有信息将被永久删除，且无法恢复！', '注销确认', {
        confirmButtonText: '确定注销',
        cancelButtonText: '取消',
        type: 'warning',
        distinguishCancelAndClose: true
      }).then(() => {
        this.$axios.post('/user/delete', { fid: this.currentUser.fid }).then(res => {
          if (res.success) {
            this.$message.success('账号已注销')
            this.$store.dispatch('logout')
            this.$router.push('/login')
          }
        }).catch(() => {
          this.$message.error('注销失败，请重试')
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.profile-container { padding: 0; animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
.profile-card { border: none; border-radius: 24px; overflow: hidden; text-align: center; transition: all 0.3s; }
.profile-card:hover { transform: translateY(-4px); box-shadow: 0 20px 40px rgba(102,126,234,0.12); }
.profile-avatar-section { padding: 24px 0; }
.profile-avatar { background: linear-gradient(145deg, #5f7bef, #7c52b3); font-size: 42px; font-weight: 700; color: #fff; box-shadow: 0 15px 35px rgba(94,97,208,0.4); border: 4px solid rgba(255,255,255,0.8); animation: floatLogo 3s infinite alternate; }
@keyframes floatLogo { 0% { transform: translateY(0px); } 100% { transform: translateY(-5px); } }
.profile-name { font-size: 24px; font-weight: 700; background: linear-gradient(135deg, #3f2b6d, #5e3b9e); -webkit-background-clip: text; background-clip: text; color: transparent; margin: 16px 0 8px; }
.profile-basic-info { text-align: left; padding: 0 12px; }
.info-row { display: flex; align-items: center; gap: 8px; padding: 12px 0; border-bottom: 1px solid #f0f2f5; font-size: 14px; color: #64748b; transition: all 0.2s; }
.info-row:last-child { border-bottom: none; }
.info-row:hover { background: rgba(102,126,234,0.03); border-radius: 8px; padding-left: 8px; }
.info-row i { color: #667eea; font-size: 16px; width: 20px; text-align: center; }
.info-row strong { color: #1e293b; margin-left: auto; }
.form-card { border: none; border-radius: 24px; overflow: hidden; transition: all 0.3s; }
.form-card:hover { box-shadow: 0 12px 28px rgba(102,126,234,0.1); }
.form-card >>> .el-card__header { background: linear-gradient(135deg, rgba(102,126,234,0.08), rgba(118,75,162,0.05)); border-bottom: 1px solid #edf2f7; padding: 16px 24px; }
.card-header { display: flex; align-items: center; font-weight: 600; color: #1e293b; }
.card-header i { color: #667eea; font-size: 18px; margin-right: 8px; }
.form-card .el-input >>> .el-input__inner { border-radius: 18px; border: 1.5px solid #e9eef3; background-color: #fafcff; transition: all 0.25s; }
.form-card .el-input >>> .el-input__inner:focus { border-color: #8b6ef0; box-shadow: 0 0 0 3px rgba(107,93,219,0.2); background-color: #fff; }
.form-card .el-date-editor >>> .el-input__inner { border-radius: 18px; border: 1.5px solid #e9eef3; background-color: #fafcff; }
.form-card .el-date-editor >>> .el-input__inner:focus { border-color: #8b6ef0; box-shadow: 0 0 0 3px rgba(107,93,219,0.2); }
.form-card .el-button--primary { background: linear-gradient(95deg, #6c5ce7, #a463f5); border: none; border-radius: 28px; height: 44px; font-weight: 600; letter-spacing: 1px; box-shadow: 0 10px 24px rgba(108,92,231,0.4); transition: all 0.3s; }
.form-card .el-button--primary:hover { transform: translateY(-2px); box-shadow: 0 14px 28px rgba(108,92,231,0.55); }
.form-card .el-button--warning { background: linear-gradient(95deg, #e6a23c, #f5c175); border: none; border-radius: 28px; height: 44px; font-weight: 600; color: #fff; letter-spacing: 1px; box-shadow: 0 10px 24px rgba(230,162,60,0.4); transition: all 0.3s; }
.form-card .el-button--warning:hover { transform: translateY(-2px); box-shadow: 0 14px 28px rgba(230,162,60,0.55); }
.form-card .el-button--default { border-radius: 28px; border: 1.5px solid #e9eef3; transition: all 0.3s; }
.form-card .el-button--default:hover { border-color: #8b6ef0; color: #6c5ce7; }
/* 注销账号区域 */
.delete-account-section { padding: 0 12px 12px; }
.delete-account-btn { width: 100%; border-radius: 12px; margin-top: 8px; }
.delete-warning { font-size: 12px; color: #94a3b8; text-align: center; margin-top: 8px; }
</style>
