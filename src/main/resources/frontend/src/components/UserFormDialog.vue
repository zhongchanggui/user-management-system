<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="visible"
    width="600px"
    :close-on-click-modal="false"
    @close="handleClose"
    custom-class="custom-dialog"
    append-to-body
    destroy-on-close
  >
    <el-form
      ref="form"
      :model="form"
      :rules="rules"
      label-width="90px"
    >
      <!-- 基础信息区 -->
      <div class="form-section">
        <div class="section-title"><i class="el-icon-user"></i> 基础信息</div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="floginname">
              <el-input v-model="form.floginname" placeholder="请输入用户名" maxlength="50" clearable :disabled="!isAdd || isAdminEditSelf" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="密码" prop="fpassword">
              <el-input v-model="form.fpassword" :type="showPassword ? 'text' : 'password'" :placeholder="isAdd ? '请输入密码' : '留空则不修改'" maxlength="50" clearable>
                <i slot="suffix" :class="showPassword ? 'el-icon-view' : 'el-icon-hide'" style="cursor:pointer; color:#909399; font-size:16px; line-height:36px; padding-right:8px;" @click="showPassword = !showPassword"></i>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="昵称" prop="fnickname">
              <el-input v-model="form.fnickname" placeholder="请输入昵称" maxlength="50" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <div class="gender-selector">
                <div class="gender-option" :class="{ active: form.fsex === 0 }" @click="form.fsex = 0">
                  <span class="gender-text">保密</span>
                </div>
                <div class="gender-option" :class="{ active: form.fsex === 1, male: form.fsex === 1 }" @click="form.fsex = 1">
                  <span class="gender-text">男</span>
                </div>
                <div class="gender-option" :class="{ active: form.fsex === 2, female: form.fsex === 2 }" @click="form.fsex = 2">
                  <span class="gender-text">女</span>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 联系信息区 -->
      <div class="form-section">
        <div class="section-title"><i class="el-icon-phone"></i> 联系信息</div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电话号码" prop="fphonenumber">
              <el-input v-model="form.fphonenumber" placeholder="请输入电话号码" maxlength="20" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电子邮箱" prop="femail">
              <el-input v-model="form.femail" placeholder="请输入电子邮箱" maxlength="50" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="家庭住址">
          <el-input v-model="form.faddress" placeholder="请输入家庭住址" maxlength="200" clearable />
        </el-form-item>
      </div>

      <!-- 个人信息区 -->
      <div class="form-section">
        <div class="section-title"><i class="el-icon-postcard"></i> 个人信息</div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="出生日期" prop="fbirthday">
              <el-date-picker v-model="form.fbirthday" type="date" placeholder="选择出生日期" value-format="yyyy-MM-dd" :picker-options="datePickerOptions" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号">
              <el-input v-model="form.fidcard" placeholder="请输入身份证号" maxlength="20" clearable />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入职日期">
              <el-date-picker v-model="form.fentrydate" type="date" placeholder="选择入职日期" value-format="yyyy-MM-dd" :picker-options="datePickerOptions" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职位">
              <el-input v-model="form.fposition" placeholder="请输入职位" maxlength="50" clearable />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 管理员操作区 -->
      <div class="form-section admin-section" v-if="isAdmin">
        <div class="section-title admin-title"><i class="el-icon-s-check"></i> 管理员操作</div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户状态" prop="fstate">
              <el-select v-model="form.fstate" placeholder="请选择状态" style="width:100%" :disabled="isAdminEditSelf">
                <el-option label="禁用" :value="0" />
                <el-option label="正常" :value="1" />
                <el-option label="冻结" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户类型" prop="ftype">
              <el-select v-model="form.ftype" placeholder="请选择类型" style="width:100%">
                <el-option label="普通员工" :value="0" />
                <el-option label="部门主管" :value="1" />
                <el-option label="系统管理员" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属部门">
              <el-select v-model="form.fdeptid" placeholder="请选择部门" clearable style="width:100%">
                <el-option v-for="d in deptList" :key="d.fid" :label="d.fname" :value="d.fid" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="直属上级">
              <el-select v-model="form.fsuperiorid" placeholder="请选择上级" clearable style="width:100%">
                <el-option v-for="u in userList" :key="u.fid" :label="u.fnickname" :value="u.fid" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>
    
    <div slot="footer">
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        {{ isAdd ? '新 增' : '保 存' }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'UserFormDialog',
  
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    type: {
      type: String,
      default: 'add'
    },
    data: {
      type: Object,
      default: null
    }
  },
  
  computed: {
    isAdd() {
      return this.type === 'add'
    },
    dialogTitle() {
      return this.isAdd ? '新增用户' : '编辑用户'
    },
    isAdmin() {
      try {
        const user = this.$store.getters.currentUser
        // ftype >= 1 表示管理员或部门主管可以管理用户
        return user && user.ftype >= 1
      } catch (e) {
        return false
      }
    },
    // 判断是否是管理员编辑自己
    isAdminEditSelf() {
      if (this.isAdd || !this.data) return false
      const currentUser = this.$store.getters.currentUser
      return currentUser && this.data && currentUser.fid === this.data.fid && this.isAdmin
    }
  },
  
  data() {
    const validatePassword = (rule, value, callback) => {
      if (this.isAdd && !value) {
        callback(new Error('请输入密码'))
        return
      }
      if (value) {
        const pattern = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{6,50}$/
        if (!pattern.test(value)) {
          callback(new Error('密码必须包含英文和数字，且长度至少6位'))
          return
        }
      }
      callback()
    }
    
    const validatePhone = (rule, value, callback) => {
      if (!value) { callback(); return }
      const pattern = /^1[3-9]\d{9}$/
      if (!pattern.test(value)) {
        callback(new Error('手机号格式不正确'))
      } else {
        callback()
      }
    }
    
    const validateEmail = (rule, value, callback) => {
      if (!value) { callback(); return }
      const pattern = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/
      if (!pattern.test(value)) {
        callback(new Error('邮箱格式不正确'))
      } else {
        callback()
      }
    }
    
    return {
      submitting: false,
      showPassword: false,
      deptList: [],
      userList: [],
      datePickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      },
      form: {
        fid: null, floginname: '', fpassword: '', fnickname: '', fstate: 1, ftype: 0,
        fsex: 0, faddress: '', fidcard: '', fentrydate: '',
        fdeptid: null, fposition: '', fsuperiorid: null,
        fbirthday: '', fphonenumber: '', femail: ''
      },
      rules: {
        floginname: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        fpassword: [{ validator: validatePassword, trigger: 'blur' }],
        fnickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        fphonenumber: [{ validator: validatePhone, trigger: 'blur' }],
        femail: [{ validator: validateEmail, trigger: 'blur' }]
      }
    }
  },
  
  watch: {
    visible(val) {
      if (val) {
        this.initForm()
        this.loadSupportData()
      }
    }
  },
  
  methods: {
    loadSupportData() {
      // 静默加载部门列表（401时使用本地数据）
      this.$axios.get('/department/list', { silent: true }).then(res => {
        if (res.success && res.data) this.deptList = res.data
      }).catch(() => {
        this.deptList = [
          {fid:1,fname:'总裁办'},{fid:2,fname:'技术部'},{fid:3,fname:'市场部'},
          {fid:4,fname:'人事部'},{fid:5,fname:'财务部'},{fid:6,fname:'前端组'},
          {fid:7,fname:'后端组'},{fid:8,fname:'测试组'},{fid:9,fname:'销售组'},{fid:10,fname:'运营组'}
        ]
      })
      // 静默加载用户列表（401时使用空数据）
      this.$axios.get('/user/query', { silent: true }).then(res => {
        if (res.success && res.data) this.userList = res.data
      }).catch(() => {
        this.userList = []
      })
    },
    
    initForm() {
      this.showPassword = false
      if (this.isAdd) {
        this.form = {
          fid: null, floginname: '', fpassword: '', fnickname: '', fstate: 1, ftype: 0,
          fsex: 0, faddress: '', fidcard: '', fentrydate: '',
          fdeptid: null, fposition: '', fsuperiorid: null,
          fbirthday: '', fphonenumber: '', femail: ''
        }
        this.$nextTick(() => {
          if (this.$refs.form) this.$refs.form.clearValidate()
        })
      } else if (this.data) {
        // 编辑时从后端获取用户详情（包含密码）
        this.$axios.get(`/user/detail/${this.data.fid}`, { silent: true }).then(res => {
          if (res.success && res.data) {
            const user = res.data
            this.form = {
              fid: user.fid, floginname: user.floginname, fpassword: user.fpassword || '',
              fnickname: user.fnickname, fstate: user.fstate, ftype: user.ftype || 0,
              fsex: user.fsex || 0, faddress: user.faddress || '',
              fidcard: user.fidcard || '', fentrydate: user.fentrydate || '',
              fdeptid: user.fdeptid || null, fposition: user.fposition || '',
              fsuperiorid: user.fsuperiorid || null,
              fbirthday: user.fbirthday, fphonenumber: user.fphonenumber, femail: user.femail
            }
          } else {
            // 接口失败时使用传入的数据
            this.form = {
              fid: this.data.fid, floginname: this.data.floginname, fpassword: '',
              fnickname: this.data.fnickname, fstate: this.data.fstate, ftype: this.data.ftype || 0,
              fsex: this.data.fsex || 0, faddress: this.data.faddress || '',
              fidcard: this.data.fidcard || '', fentrydate: this.data.fentrydate || '',
              fdeptid: this.data.fdeptid || null, fposition: this.data.fposition || '',
              fsuperiorid: this.data.fsuperiorid || null,
              fbirthday: this.data.fbirthday, fphonenumber: this.data.fphonenumber, femail: this.data.femail
            }
          }
          this.$nextTick(() => {
            if (this.$refs.form) this.$refs.form.clearValidate()
          })
        }).catch(() => {
          // 接口失败时使用传入的数据
          this.form = {
            fid: this.data.fid, floginname: this.data.floginname, fpassword: '',
            fnickname: this.data.fnickname, fstate: this.data.fstate, ftype: this.data.ftype || 0,
            fsex: this.data.fsex || 0, faddress: this.data.faddress || '',
            fidcard: this.data.fidcard || '', fentrydate: this.data.fentrydate || '',
            fdeptid: this.data.fdeptid || null, fposition: this.data.fposition || '',
            fsuperiorid: this.data.fsuperiorid || null,
            fbirthday: this.data.fbirthday, fphonenumber: this.data.fphonenumber, femail: this.data.femail
          }
          this.$nextTick(() => {
            if (this.$refs.form) this.$refs.form.clearValidate()
          })
        })
      }
    },
    
    handleClose() {
      this.$emit('update:visible', false)
    },
    
    handleSubmit() {
      if (!this.$refs.form) return
      this.$refs.form.validate(valid => {
        if (valid) {
          // 额外校验：编辑时状态和生日必须有值
          if (!this.isAdd && this.form.fstate == null) {
            this.$message.warning('请选择用户状态')
            return
          }
          if (!this.form.fbirthday) {
            this.$message.warning('请选择出生日期')
            return
          }
          this.submitting = true
          const url = this.isAdd ? '/user/add' : '/user/edit'
          const data = { ...this.form }
          if (!this.isAdd && !data.fpassword) {
            delete data.fpassword
          }
          // 新增用户时设置创建人为当前用户
          if (this.isAdd) {
            const currentUser = this.$store.getters.currentUser
            if (currentUser) {
              data.fcreateuserid = currentUser.fid
            }
          }
          this.$axios.post(url, data)
            .then(res => {
              if (res.success) {
                this.$message.success(this.isAdd ? '新增成功' : '修改成功')
                this.$emit('success')
                this.handleClose()
              }
            })
            .catch(() => {
              this.$message.error('操作失败，请重试')
            })
            .finally(() => {
              this.submitting = false
            })
        }
      })
    }
  }
}
</script>

<style scoped>
.custom-dialog {
  border-radius: 24px;
}

.custom-dialog >>> .el-dialog__header {
  background: linear-gradient(145deg, #5f7bef, #7c52b3);
  padding: 20px 24px;
  border-radius: 24px 24px 0 0;
}

.custom-dialog >>> .el-dialog__title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.custom-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: rgba(255, 255, 255, 0.8);
  font-size: 20px;
}

.custom-dialog >>> .el-dialog__headerbtn:hover .el-dialog__close {
  color: #fff;
}

.custom-dialog >>> .el-dialog__body {
  padding: 20px 24px 8px;
  max-height: 60vh;
  overflow-y: auto;
}

.custom-dialog >>> .el-dialog__footer {
  padding: 16px 24px 24px;
  border-top: 1px solid #f0f2f5;
}

/* 分组标题 */
.form-section {
  margin-bottom: 18px;
  padding-bottom: 6px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 14px;
  padding: 8px 14px;
  background: linear-gradient(135deg, rgba(102,126,234,0.08), rgba(118,75,162,0.05));
  border-radius: 10px;
  border-left: 3px solid #667eea;
}

.section-title i {
  color: #667eea;
  margin-right: 6px;
}

.admin-section {
  background: rgba(255, 248, 230, 0.6);
  border-radius: 16px;
  padding: 14px;
  border: 1px dashed #e6a23c;
}

.admin-title {
  background: linear-gradient(135deg, rgba(230,162,60,0.1), rgba(240,199,138,0.08));
  border-left-color: #e6a23c;
}

.admin-title i {
  color: #e6a23c;
}

/* 表单输入美化 */
.custom-dialog .el-input >>> .el-input__inner {
  border-radius: 12px;
  border: 1.5px solid #e9eef3;
  background-color: #fafcff;
  height: 36px;
  line-height: 36px;
  transition: all 0.25s;
}

.custom-dialog .el-input >>> .el-input__inner:focus {
  border-color: #8b6ef0;
  box-shadow: 0 0 0 3px rgba(107, 93, 219, 0.15);
  background-color: #fff;
}

.custom-dialog .el-select >>> .el-input__inner {
  border-radius: 12px;
  border: 1.5px solid #e9eef3;
  background-color: #fafcff;
  height: 36px;
  line-height: 36px;
}

.custom-dialog .el-date-editor >>> .el-input__inner {
  border-radius: 12px;
  border: 1.5px solid #e9eef3;
  background-color: #fafcff;
  height: 36px;
  line-height: 36px;
}

.custom-dialog >>> .el-form-item__label {
  font-weight: 500;
  color: #475569;
  font-size: 13px;
}

.custom-dialog .el-button {
  border-radius: 14px;
  padding: 10px 28px;
  font-weight: 500;
  transition: all 0.3s;
}

.custom-dialog .el-button--primary {
  background: linear-gradient(95deg, #6c5ce7, #a463f5);
  border: none;
  box-shadow: 0 8px 20px rgba(108, 92, 231, 0.4);
}

.custom-dialog .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(108, 92, 231, 0.55);
}

.custom-dialog .el-button--default {
  border: 1.5px solid #e9eef3;
}

.custom-dialog .el-button--default:hover {
  border-color: #8b6ef0;
  color: #6c5ce7;
}

/* 性别选择器 - 美化 */
.gender-selector {
  display: flex;
  gap: 8px;
}

.gender-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 6px 14px;
  border-radius: 12px;
  border: 1.5px solid #e9eef3;
  background: #fafcff;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  min-width: 60px;
}

.gender-option:hover {
  border-color: #c4b5fd;
  background: #f5f3ff;
  transform: translateY(-2px);
}

.gender-option.active {
  border-color: #8b5cf6;
  background: linear-gradient(135deg, rgba(139, 92, 246, 0.1), rgba(167, 139, 250, 0.08));
  box-shadow: 0 4px 12px rgba(139, 92, 246, 0.2);
}

.gender-option.active.male {
  border-color: #3b82f6;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1), rgba(96, 165, 250, 0.08));
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
}

.gender-option.active.female {
  border-color: #ec4899;
  background: linear-gradient(135deg, rgba(236, 72, 153, 0.1), rgba(244, 114, 182, 0.08));
  box-shadow: 0 4px 12px rgba(236, 72, 153, 0.2);
}

.gender-text {
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
}

.gender-option.active .gender-text {
  color: #6c5ce7;
}

.gender-option.active.male .gender-text {
  color: #3b82f6;
}

.gender-option.active.female .gender-text {
  color: #ec4899;
}
</style>