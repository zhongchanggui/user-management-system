<template>
  <div class="leave-container">
    <!-- 顶部统计 -->
    <el-row :gutter="24" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-1" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-time"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.pending }}</p>
              <p class="stat-label">待审批</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-2" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-success"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.approved }}</p>
              <p class="stat-label">已通过</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-3" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-error"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.rejected }}</p>
              <p class="stat-label">已驳回</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-4" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-date"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.remaining }}</p>
              <p class="stat-label">剩余年假</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 申请表单 -->
    <el-card class="form-card" shadow="hover">
      <div slot="header" class="card-header">
        <span><i class="el-icon-edit-outline"></i> 请假申请</span>
      </div>
      <el-form :model="leaveForm" :rules="leaveRules" ref="leaveForm" label-width="100px" class="leave-form">
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="请假类型" prop="type">
              <el-select v-model="leaveForm.type" placeholder="请选择请假类型" style="width: 100%">
                <el-option label="事假" value="personal" />
                <el-option label="病假" value="sick" />
                <el-option label="年假" value="annual" />
                <el-option label="婚假" value="marriage" />
                <el-option label="产假" value="maternity" />
                <el-option label="陪产假" value="paternity" />
                <el-option label="丧假" value="bereavement" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="leaveForm.startDate" type="datetime" placeholder="选择开始时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="leaveForm.endDate" type="datetime" placeholder="选择结束时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="16">
            <el-form-item label="请假事由" prop="reason">
              <el-input v-model="leaveForm.reason" type="textarea" :rows="3" placeholder="请输入请假事由" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="紧急联系人">
              <el-input v-model="leaveForm.emergencyContact" placeholder="紧急联系人姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" icon="el-icon-s-promotion" @click="handleSubmit">提交申请</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 标签页：我发起的 / 待我审批 / 我已审批 -->
    <el-card class="list-card" shadow="hover">
      <el-tabs v-model="activeTab" class="leave-tabs">
        <el-tab-pane label="我的申请" name="mine">
          <el-table :data="myLeaves" border stripe class="data-table">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="type" label="类型" width="80" align="center">
              <template slot-scope="scope">
                <el-tag size="small" effect="plain">{{ getTypeName(scope.row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startDate" label="开始时间" width="160" align="center" />
            <el-table-column prop="endDate" label="结束时间" width="160" align="center" />
            <el-table-column prop="days" label="天数" width="70" align="center" />
            <el-table-column prop="reason" label="事由" min-width="200" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="90" align="center">
              <template slot-scope="scope">
                <el-tag :type="getStatusType(scope.row.status)" effect="dark" size="small">
                  {{ getStatusName(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="approver" label="审批人" width="100" align="center" />
            <el-table-column label="操作" width="140" align="center">
              <template slot-scope="scope">
                <div class="action-group">
                  <el-button v-if="scope.row.status === 'pending'" type="text" size="small" class="action-btn action-delete" @click="handleCancel(scope.row)">
                    <i class="el-icon-refresh-left"></i> 撤回
                  </el-button>
                  <el-button type="text" size="small" class="action-btn action-view" @click="handleViewDetail(scope.row)">
                    <i class="el-icon-view"></i> 详情
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="待我审批" name="approve">
          <el-table :data="pendingApprovals" border stripe class="data-table">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="applicant" label="申请人" width="100" align="center" />
            <el-table-column prop="department" label="部门" width="100" align="center" />
            <el-table-column prop="type" label="类型" width="80" align="center">
              <template slot-scope="scope">
                <el-tag size="small" effect="plain">{{ getTypeName(scope.row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="startDate" label="开始时间" width="160" align="center" />
            <el-table-column prop="endDate" label="结束时间" width="160" align="center" />
            <el-table-column prop="days" label="天数" width="70" align="center" />
            <el-table-column prop="reason" label="事由" min-width="180" show-overflow-tooltip />
            <el-table-column label="操作" width="160" align="center">
              <template slot-scope="scope">
                <div class="action-group">
                  <el-button type="text" size="small" class="action-btn" style="color:#67c23a" @click="handleApprove(scope.row)">
                    <i class="el-icon-check"></i> 通过
                  </el-button>
                  <el-button type="text" size="small" class="action-btn" style="color:#f56c6c" @click="handleReject(scope.row)">
                    <i class="el-icon-close"></i> 驳回
                  </el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="审批历史" name="history">
          <el-table :data="approvalHistory" border stripe class="data-table">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="applicant" label="申请人" width="100" align="center" />
            <el-table-column prop="type" label="类型" width="80" align="center">
              <template slot-scope="scope"><el-tag size="small" effect="plain">{{ getTypeName(scope.row.type) }}</el-tag></template>
            </el-table-column>
            <el-table-column prop="days" label="天数" width="70" align="center" />
            <el-table-column prop="result" label="审批结果" width="100" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.result === 'approved' ? 'success' : 'danger'" size="small" effect="dark">
                  {{ scope.row.result === 'approved' ? '已通过' : '已驳回' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="审批意见" min-width="200" show-overflow-tooltip />
            <el-table-column prop="time" label="审批时间" width="160" align="center" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog title="请假详情" :visible.sync="detailVisible" width="500px" append-to-body destroy-on-close>
      <el-descriptions :column="1" border v-if="detailItem">
        <el-descriptions-item label="请假类型">{{ getTypeName(detailItem.type) }}</el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ detailItem.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ detailItem.endDate }}</el-descriptions-item>
        <el-descriptions-item label="请假天数">{{ detailItem.days }}天</el-descriptions-item>
        <el-descriptions-item label="请假事由">{{ detailItem.reason }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="getStatusType(detailItem.status)" effect="dark">{{ getStatusName(detailItem.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detailItem.approver || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批意见">{{ detailItem.comment || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <span slot="footer"><el-button @click="detailVisible = false">关 闭</el-button></span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Leave',
  data() {
    return {
      activeTab: 'mine',
      detailVisible: false,
      detailItem: null,
      stats: { pending: 2, approved: 15, rejected: 1, remaining: 8 },
      approvalHistory: [
        { applicant: '李华', type: 'sick', days: 2, result: 'approved', comment: '同意，注意休息', time: '2026-05-18 14:30' },
        { applicant: '张伟', type: 'personal', days: 1, result: 'rejected', comment: '近期项目紧张，请改期', time: '2026-05-15 09:20' }
      ],
      leaveForm: { type: '', startDate: '', endDate: '', reason: '', emergencyContact: '' },
      leaveRules: {
        type: [{ required: true, message: '请选择请假类型', trigger: 'change' }],
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
        reason: [{ required: true, message: '请输入请假事由', trigger: 'blur' }]
      },
      myLeaves: [
        { type: 'personal', startDate: '2025-06-05 09:00', endDate: '2025-06-05 18:00', days: 1, reason: '家中有事需要处理', status: 'pending', approver: '张经理', comment: '' },
        { type: 'sick', startDate: '2025-05-20 09:00', endDate: '2025-05-22 18:00', days: 3, reason: '身体不适，需要休息', status: 'approved', approver: '张经理', comment: '同意，注意休息' },
        { type: 'annual', startDate: '2025-04-10 09:00', endDate: '2025-04-14 18:00', days: 5, reason: '清明节假期出行', status: 'approved', approver: '李主管', comment: '' },
        { type: 'personal', startDate: '2025-03-15 09:00', endDate: '2025-03-15 18:00', days: 1, reason: '搬家', status: 'rejected', approver: '张经理', comment: '近期工作繁忙，请改期' }
      ],
      pendingApprovals: [
        { applicant: '王小明', department: '技术部', type: 'sick', startDate: '2025-06-03 09:00', endDate: '2025-06-04 18:00', days: 2, reason: '感冒发烧需要就医' },
        { applicant: '李丽华', department: '市场部', type: 'personal', startDate: '2025-06-06 09:00', endDate: '2025-06-06 18:00', days: 1, reason: '办理个人证件' }
      ]
    }
  },
  methods: {
    getTypeName(type) {
      const map = { personal: '事假', sick: '病假', annual: '年假', marriage: '婚假', maternity: '产假', paternity: '陪产假', bereavement: '丧假' }
      return map[type] || type
    },
    getStatusType(status) {
      const map = { pending: 'warning', approved: 'success', rejected: 'danger', cancelled: 'info' }
      return map[status] || 'info'
    },
    getStatusName(status) {
      const map = { pending: '待审批', approved: '已通过', rejected: '已驳回', cancelled: '已撤回' }
      return map[status] || status
    },
    handleSubmit() {
      this.$refs.leaveForm.validate(valid => {
        if (valid) {
          this.$message.success('请假申请已提交')
          this.handleReset()
        }
      })
    },
    handleReset() {
      this.$refs.leaveForm && this.$refs.leaveForm.resetFields()
    },
    handleCancel(row) {
      this.$confirm('确定要撤回该请假申请吗？', '提示', { type: 'warning' }).then(() => {
        row.status = 'cancelled'
        this.$message.success('已撤回')
      }).catch(() => {})
    },
    handleViewDetail(row) {
      this.detailItem = row
      this.detailVisible = true
    },
    handleApprove(row) {
      this.$prompt('请输入审批意见', '审批通过', { inputPlaceholder: '同意', defaultValue: '同意' }).then(({ value }) => {
        const idx = this.pendingApprovals.indexOf(row)
        if (idx > -1) this.pendingApprovals.splice(idx, 1)
        this.stats.approved++
        this.$message.success('已通过')
      }).catch(() => {})
    },
    handleReject(row) {
      this.$prompt('请输入驳回原因', '驳回申请', { inputPlaceholder: '请填写驳回原因' }).then(({ value }) => {
        const idx = this.pendingApprovals.indexOf(row)
        if (idx > -1) this.pendingApprovals.splice(idx, 1)
        this.stats.rejected++
        this.$message.warning('已驳回')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.leave-container { padding: 0; }

.stat-row { margin-bottom: 24px; }
.stat-card { border: none; border-radius: 20px; cursor: pointer; transition: all 0.35s; overflow: hidden; }
.stat-card:hover { transform: translateY(-6px); box-shadow: 0 12px 28px rgba(0,0,0,0.12); }
.stat-content { display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 56px; height: 56px; border-radius: 16px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-icon i { font-size: 26px; color: #fff; }
.stat-card-1 .stat-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
.stat-card-2 .stat-icon { background: linear-gradient(135deg, #11998e, #38ef7d); }
.stat-card-3 .stat-icon { background: linear-gradient(135deg, #eb3349, #f45c43); }
.stat-card-4 .stat-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-value { font-size: 28px; font-weight: 800; color: #1e293b; line-height: 1.2; margin-bottom: 4px; }
.stat-label { font-size: 13px; color: #94a3b8; }

.form-card { border: none; border-radius: 20px; margin-bottom: 24px; }
.form-card >>> .el-card__header { background: linear-gradient(135deg, #fafbfc, #f5f7fa); border-bottom: 1px solid #edf2f7; }
.card-header { display: flex; align-items: center; justify-content: space-between; font-weight: 600; color: #1e293b; }
.card-header i { color: #667eea; margin-right: 8px; font-size: 18px; }

.leave-form .el-input__inner, .leave-form .el-textarea__inner { border-radius: 12px; border: 1.5px solid #e9eef3; }
.leave-form .el-input__inner:focus, .leave-form .el-textarea__inner:focus { border-color: #667eea; box-shadow: 0 0 0 3px rgba(102,126,234,0.15); }

.list-card { border: none; border-radius: 20px; margin-bottom: 24px; }
.list-card >>> .el-card__header { padding: 0; border: none; }
.list-card >>> .el-tabs__header { padding: 0 24px; background: linear-gradient(135deg, #fafbfc, #f5f7fa); margin: 0; }
.list-card >>> .el-tabs__nav-wrap::after { display: none; }
.list-card >>> .el-tabs__item { height: 56px; line-height: 56px; font-weight: 600; }
.list-card >>> .el-tabs__item.is-active { color: #667eea; }
.list-card >>> .el-tabs__active-bar { background: linear-gradient(135deg, #667eea, #764ba2); }
.list-card >>> .el-card__body { padding: 20px 24px; }

.data-table { border-radius: 12px; overflow: hidden; }
.data-table >>> .el-table__header th { background: linear-gradient(135deg, #f5f7fe, #f0f2fc); color: #334155; font-weight: 600; font-size: 13px; }
.data-table >>> .el-table__row:hover > td { background: linear-gradient(135deg, #f5f7ff, #f0f2ff) !important; }
.data-table .el-tag { border-radius: 20px; padding: 2px 12px; font-weight: 500; border: none; }
.action-group { display: flex; align-items: center; justify-content: center; gap: 4px; flex-wrap: nowrap; white-space: nowrap; }
.action-btn { padding: 4px 10px; border-radius: 8px; font-size: 13px; font-weight: 500; transition: all 0.3s; margin: 0 2px; }
.action-btn i { margin-right: 3px; }
.action-view { color: #667eea; }
.action-view:hover { background: rgba(102, 126, 234, 0.1); color: #4f46e5; }
.action-delete { color: #f56c6c; }
.action-delete:hover { background: rgba(245, 108, 108, 0.1); color: #e03e3e; }
</style>