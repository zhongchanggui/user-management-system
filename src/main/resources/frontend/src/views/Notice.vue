<template>
  <div class="notice-container">
    <el-card class="page-card" shadow="never">
      <div slot="header" class="card-header">
        <span><i class="el-icon-message"></i> 公告管理</span>
        <el-button v-if="isAdmin" type="primary" size="small" icon="el-icon-plus" @click="handleAdd">发布公告</el-button>
      </div>

      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索公告标题..." clearable prefix-icon="el-icon-search" style="width:300px" @keyup.enter.native="loadNotices" />
        <el-select v-model="filterType" placeholder="全部类型" clearable style="width:120px;margin-left:12px" @change="loadNotices">
          <el-option label="普通" :value="0" />
          <el-option label="重要" :value="1" />
          <el-option label="紧急" :value="2" />
        </el-select>
      </div>

      <!-- 公告列表 -->
      <div class="notice-list">
        <div class="notice-item" v-for="item in noticeList" :key="item.fid" @click="handleView(item)">
          <div class="notice-left">
            <div class="notice-type-badge" :class="getTypeClass(item.ftype)">
              {{ getTypeName(item.ftype) }}
            </div>
            <div class="notice-info">
              <h4 class="notice-title">{{ item.ftitle }}</h4>
              <p class="notice-meta">
                <span><i class="el-icon-user"></i> {{ item.fcreateusername || '系统' }}</span>
                <span><i class="el-icon-time"></i> {{ item.fcreatedatetime }}</span>
              </p>
            </div>
          </div>
          <div class="notice-right">
            <el-tag :type="getStateType(item.fstate)" size="small" effect="dark">
              {{ getStateName(item.fstate) }}
            </el-tag>
            <el-dropdown v-if="isAdmin" trigger="click" @command="handleCommand($event, item)" style="margin-left:12px">
              <el-button icon="el-icon-more" size="mini" circle />
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="edit" icon="el-icon-edit">编辑</el-dropdown-item>
                <el-dropdown-item command="delete" icon="el-icon-delete" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
        <div class="notice-empty" v-if="noticeList.length === 0">
          <i class="el-icon-bell"></i>
          <p>暂无公告</p>
        </div>
      </div>
    </el-card>

    <!-- 公告详情弹窗 -->
    <el-dialog :title="currentNotice.ftitle" :visible.sync="viewVisible" width="600px" class="view-dialog" append-to-body destroy-on-close>
      <div class="notice-detail-meta">
        <span><i class="el-icon-user"></i> {{ currentNotice.fcreateusername || '系统' }}</span>
        <span><i class="el-icon-time"></i> {{ currentNotice.fcreatedatetime }}</span>
        <el-tag :type="getTypeTagType(currentNotice.ftype)" size="small">{{ getTypeName(currentNotice.ftype) }}</el-tag>
      </div>
      <el-divider></el-divider>
      <div class="notice-detail-content">{{ currentNotice.fcontent }}</div>
      <span slot="footer">
        <el-button @click="viewVisible = false">关 闭</el-button>
      </span>
    </el-dialog>

    <!-- 发布/编辑公告弹窗 -->
    <el-dialog :title="isEdit ? '编辑公告' : '发布公告'" :visible.sync="editVisible" width="600px" class="form-dialog" append-to-body destroy-on-close>
      <el-form :model="form" :rules="formRules" ref="noticeForm" label-width="80px">
        <el-form-item label="标题" prop="ftitle">
          <el-input v-model="form.ftitle" placeholder="请输入公告标题" maxlength="200" />
        </el-form-item>
        <el-form-item label="类型" prop="ftype">
          <el-radio-group v-model="form.ftype">
            <el-radio :label="0">普通</el-radio>
            <el-radio :label="1">重要</el-radio>
            <el-radio :label="2">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="fcontent">
          <el-input v-model="form.fcontent" type="textarea" :rows="8" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="editVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存修改' : '发布公告' }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Notice',
  data() {
    return {
      noticeList: [],
      keyword: '',
      filterType: '',
      viewVisible: false,
      editVisible: false,
      isEdit: false,
      currentNotice: {},
      form: { fid: null, ftitle: '', fcontent: '', ftype: 0, fstate: 1 },
      formRules: {
        ftitle: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
        fcontent: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isAdmin() {
      return this.$store.getters.isAdmin
    }
  },
  mounted() {
    this.loadNotices()
  },
  methods: {
    loadNotices() {
      const params = {}
      if (this.keyword) params.keyword = this.keyword
      if (this.filterType !== '') params.ftype = this.filterType
      this.$axios.get('/notice/list', { params, silent: true }).then(res => {
        if (res.success) this.noticeList = res.data
      }).catch(() => {
        this.noticeList = [
          { fid: 1, ftitle: '欢迎使用员工管理系统V2.0', fcontent: '各位同事好，员工管理系统V2.0已正式上线，包含员工管理、部门管理、考勤管理等功能。请各部门积极配合使用。', ftype: 1, fstate: 1, fcreateuserid: 1, fcreateusername: '系统管理员', fcreatedatetime: '2026-05-28 11:05:11' },
          { fid: 2, ftitle: '请及时更新个人资料信息', fcontent: '请各位员工登录系统后及时更新个人资料，包括联系方式、紧急联系人等信息，确保信息准确完整。', ftype: 0, fstate: 1, fcreateuserid: 1, fcreateusername: '系统管理员', fcreatedatetime: '2026-05-28 11:05:11' },
          { fid: 3, ftitle: '系统将于本周六进行维护升级', fcontent: '为提升系统性能和安全性，计划于本周六凌晨2:00-6:00进行系统维护升级，届时系统将暂停服务，请提前做好工作安排。', ftype: 2, fstate: 1, fcreateuserid: 1, fcreateusername: '系统管理员', fcreatedatetime: '2026-05-28 11:05:11' },
          { fid: 4, ftitle: '关于2024年度绩效考核的通知', fcontent: '2024年度绩效考核将于下月启动，请各部门负责人提前做好考核准备工作，具体安排另行通知。', ftype: 1, fstate: 1, fcreateuserid: 1, fcreateusername: '系统管理员', fcreatedatetime: '2026-05-28 11:05:11' },
          { fid: 5, ftitle: '新员工入职培训安排', fcontent: '本月新入职员工培训将于下周一上午9:00在三楼会议室举行，请相关人员准时参加。', ftype: 0, fstate: 1, fcreateuserid: 4, fcreateusername: '莉莉', fcreatedatetime: '2026-05-28 11:05:11' }
        ]
      })
    },
    handleView(item) {
      this.currentNotice = item
      this.viewVisible = true
    },
    handleAdd() {
      this.isEdit = false
      this.form = { fid: null, ftitle: '', fcontent: '', ftype: 0, fstate: 1 }
      this.$nextTick(() => { this.$refs.noticeForm && this.$refs.noticeForm.clearValidate() })
      this.editVisible = true
    },
    handleEdit(item) {
      this.isEdit = true
      this.form = { fid: item.fid, ftitle: item.ftitle, fcontent: item.fcontent, ftype: item.ftype, fstate: item.fstate }
      this.$nextTick(() => { this.$refs.noticeForm && this.$refs.noticeForm.clearValidate() })
      this.editVisible = true
    },
    handleDelete(item) {
      this.$confirm(`确定要删除公告"${item.ftitle}"吗？`, '提示', {
        type: 'warning'
      }).then(() => {
        this.$axios.post('/notice/delete', { fid: item.fid }).then(res => {
          if (res.success) { this.$message.success('删除成功'); this.loadNotices() }
        }).catch(() => { this.$message.error('删除失败，请重试') })
      }).catch(() => {})
    },
    handleCommand(cmd, item) {
      if (cmd === 'edit') this.handleEdit(item)
      else if (cmd === 'delete') this.handleDelete(item)
    },
    handleSubmit() {
      this.$refs.noticeForm.validate(valid => {
        if (!valid) return
        const url = this.isEdit ? '/notice/edit' : '/notice/add'
        this.$axios.post(url, this.form).then(res => {
          if (res.success) {
            this.$message.success(this.isEdit ? '修改成功' : '发布成功')
            this.editVisible = false
            this.loadNotices()
          }
        }).catch(() => {
          this.$message.error(this.isEdit ? '修改失败，请重试' : '发布失败，请重试')
        })
      })
    },
    getTypeName(type) {
      return { 0: '普通', 1: '重要', 2: '紧急' }[type] || '普通'
    },
    getTypeClass(type) {
      return { 0: 'type-normal', 1: 'type-important', 2: 'type-urgent' }[type] || 'type-normal'
    },
    getTypeTagType(type) {
      return { 0: 'info', 1: 'warning', 2: 'danger' }[type] || 'info'
    },
    getStateType(state) {
      return { 0: 'info', 1: 'success', 2: 'warning' }[state] || 'info'
    },
    getStateName(state) {
      return { 0: '草稿', 1: '已发布', 2: '已撤回' }[state] || '未知'
    }
  }
}
</script>

<style scoped>
.notice-container { padding: 0; }
.page-card { border: none; border-radius: 20px; overflow: hidden; animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
.page-card >>> .el-card__header { background: linear-gradient(135deg, rgba(102,126,234,0.08), rgba(118,75,162,0.05)); border-bottom: 1px solid #edf2f7; padding: 16px 24px; }
.card-header { display: flex; justify-content: space-between; align-items: center; font-weight: 600; color: #1e293b; }
.card-header i { color: #667eea; font-size: 18px; margin-right: 8px; }
.card-header .el-button { border-radius: 24px; background: linear-gradient(95deg, #6c5ce7, #a463f5); border: none; box-shadow: 0 8px 20px rgba(108,92,231,0.4); transition: all 0.3s; }
.card-header .el-button:hover { transform: translateY(-2px); box-shadow: 0 12px 28px rgba(108,92,231,0.55); }
.search-bar { display: flex; align-items: center; margin-bottom: 20px; }
.search-bar .el-input >>> .el-input__inner { border-radius: 18px; border: 1.5px solid #e9eef3; background-color: #fafcff; transition: all 0.25s; }
.search-bar .el-input >>> .el-input__inner:focus { border-color: #8b6ef0; box-shadow: 0 0 0 3px rgba(107,93,219,0.2); background-color: #fff; }
.search-bar .el-select >>> .el-input__inner { border-radius: 18px; border: 1.5px solid #e9eef3; }
.notice-list { min-height: 300px; }
.notice-item { display: flex; justify-content: space-between; align-items: center; padding: 18px 20px; margin-bottom: 12px; background: rgba(255,255,255,0.95); border-radius: 16px; cursor: pointer; transition: all 0.3s; border: 1px solid #edf2f7; }
.notice-item:hover { transform: translateY(-3px); box-shadow: 0 12px 28px rgba(102,126,234,0.12); border-color: rgba(102,126,234,0.2); }
.notice-left { display: flex; align-items: center; gap: 16px; flex: 1; }
.notice-type-badge { padding: 5px 14px; border-radius: 10px; font-size: 12px; font-weight: 600; min-width: 40px; text-align: center; color: #fff; letter-spacing: 0.5px; }
.type-normal { background: linear-gradient(135deg, #667eea, #8b9cf7); }
.type-important { background: linear-gradient(135deg, #e6a23c, #f0c78a); }
.type-urgent { background: linear-gradient(135deg, #f56c6c, #f89898); box-shadow: 0 4px 12px rgba(245,108,108,0.3); }
.notice-info { flex: 1; }
.notice-title { font-size: 15px; font-weight: 600; color: #1e293b; margin: 0 0 6px; }
.notice-meta { font-size: 12px; color: #94a3b8; margin: 0; }
.notice-meta span { margin-right: 16px; }
.notice-meta i { margin-right: 4px; }
.notice-right { display: flex; align-items: center; }
.notice-empty { text-align: center; padding: 60px; color: #94a3b8; }
.notice-empty i { font-size: 64px; display: block; margin-bottom: 16px; }
.view-dialog >>> .el-dialog { border-radius: 24px; overflow: hidden; }
.view-dialog >>> .el-dialog__header { background: linear-gradient(145deg, #5f7bef, #7c52b3); padding: 20px 24px; }
.view-dialog >>> .el-dialog__title { color: #fff; font-size: 18px; font-weight: 600; letter-spacing: 0.5px; }
.view-dialog >>> .el-dialog__headerbtn .el-dialog__close { color: rgba(255,255,255,0.8); }
.notice-detail-meta { display: flex; gap: 16px; align-items: center; font-size: 13px; color: #94a3b8; }
.notice-detail-meta i { margin-right: 4px; }
.notice-detail-content { font-size: 15px; line-height: 1.8; color: #334155; white-space: pre-wrap; padding: 16px 0; }
.form-dialog >>> .el-dialog { border-radius: 24px; overflow: hidden; }
.form-dialog >>> .el-dialog__header { background: linear-gradient(145deg, #5f7bef, #7c52b3); padding: 20px 24px; }
.form-dialog >>> .el-dialog__title { color: #fff; font-size: 18px; font-weight: 600; letter-spacing: 0.5px; }
.form-dialog >>> .el-dialog__headerbtn .el-dialog__close { color: rgba(255,255,255,0.8); }
.form-dialog .el-input >>> .el-input__inner { border-radius: 18px; border: 1.5px solid #e9eef3; background-color: #fafcff; transition: all 0.25s; }
.form-dialog .el-input >>> .el-input__inner:focus { border-color: #8b6ef0; box-shadow: 0 0 0 3px rgba(107,93,219,0.2); background-color: #fff; }
.form-dialog .el-textarea >>> .el-textarea__inner { border-radius: 18px; border: 1.5px solid #e9eef3; background-color: #fafcff; transition: all 0.25s; }
.form-dialog .el-textarea >>> .el-textarea__inner:focus { border-color: #8b6ef0; box-shadow: 0 0 0 3px rgba(107,93,219,0.2); }
.form-dialog .el-button--primary { background: linear-gradient(95deg, #6c5ce7, #a463f5); border: none; border-radius: 28px; box-shadow: 0 8px 20px rgba(108,92,231,0.4); }
.form-dialog .el-button--primary:hover { transform: translateY(-2px); box-shadow: 0 12px 28px rgba(108,92,231,0.55); }
</style>
