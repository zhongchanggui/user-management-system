<template>
  <div class="salary-container">
    <!-- 顶部统计 -->
    <el-row :gutter="24" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-1" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-money"></i></div>
            <div class="stat-info">
              <p class="stat-value">¥{{ currentSalary.totalSalary }}</p>
              <p class="stat-label">本月工资</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-2" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-bank-card"></i></div>
            <div class="stat-info">
              <p class="stat-value">¥{{ currentSalary.afterTax }}</p>
              <p class="stat-label">实发工资</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-3" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-discount"></i></div>
            <div class="stat-info">
              <p class="stat-value">¥{{ currentSalary.tax }}</p>
              <p class="stat-label">个人所得税</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-4" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-present"></i></div>
            <div class="stat-info">
              <p class="stat-value">¥{{ currentSalary.bonus }}</p>
              <p class="stat-label">绩效奖金</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 工资条详情 -->
    <el-row :gutter="24">
      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <el-card class="detail-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-document"></i> 本月工资条</span>
            <div class="header-actions">
              <el-select v-model="selectedMonth" size="small" style="width: 120px;" @change="loadSalary">
                <el-option v-for="m in months" :key="m" :label="m" :value="m" />
              </el-select>
              <el-button type="primary" icon="el-icon-download" size="small" @click="handleExport">导出工资条</el-button>
            </div>
          </div>
          <el-descriptions :column="2" border class="salary-descriptions">
            <el-descriptions-item label="基本工资">¥{{ currentSalary.baseSalary }}</el-descriptions-item>
            <el-descriptions-item label="岗位工资">¥{{ currentSalary.positionSalary }}</el-descriptions-item>
            <el-descriptions-item label="绩效奖金">¥{{ currentSalary.bonus }}</el-descriptions-item>
            <el-descriptions-item label="加班工资">¥{{ currentSalary.overtimePay }}</el-descriptions-item>
            <el-descriptions-item label="交通补贴">¥{{ currentSalary.transportAllowance }}</el-descriptions-item>
            <el-descriptions-item label="餐饮补贴">¥{{ currentSalary.mealAllowance }}</el-descriptions-item>
            <el-descriptions-item label="住房补贴">¥{{ currentSalary.housingAllowance }}</el-descriptions-item>
            <el-descriptions-item label="全勤奖">¥{{ currentSalary.attendanceBonus }}</el-descriptions-item>
            <el-descriptions-item label="养老保险">-¥{{ currentSalary.pension }}</el-descriptions-item>
            <el-descriptions-item label="医疗保险">-¥{{ currentSalary.medical }}</el-descriptions-item>
            <el-descriptions-item label="失业保险">-¥{{ currentSalary.unemployment }}</el-descriptions-item>
            <el-descriptions-item label="住房公积金">-¥{{ currentSalary.housingFund }}</el-descriptions-item>
            <el-descriptions-item label="个人所得税">-¥{{ currentSalary.tax }}</el-descriptions-item>
            <el-descriptions-item label="其他扣款">-¥{{ currentSalary.otherDeduction }}</el-descriptions-item>
          </el-descriptions>
          <div class="salary-summary">
            <div class="summary-item">
              <span>应发合计</span>
              <strong class="total-amount">¥{{ currentSalary.totalSalary }}</strong>
            </div>
            <div class="summary-item">
              <span>扣款合计</span>
              <strong class="deduction-amount">-¥{{ currentSalary.totalDeduction }}</strong>
            </div>
            <div class="summary-item">
              <span>实发工资</span>
              <strong class="net-amount">¥{{ currentSalary.afterTax }}</strong>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <!-- 社保公积金 -->
        <el-card class="insurance-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-umbrella"></i> 社保公积金</span>
          </div>
          <div class="insurance-list">
            <div class="insurance-item">
              <span class="insurance-label">养老保险基数</span>
              <span class="insurance-value">¥{{ insuranceInfo.pensionBase }}</span>
            </div>
            <div class="insurance-item">
              <span class="insurance-label">医疗保险基数</span>
              <span class="insurance-value">¥{{ insuranceInfo.medicalBase }}</span>
            </div>
            <div class="insurance-item">
              <span class="insurance-label">公积金基数</span>
              <span class="insurance-value">¥{{ insuranceInfo.housingBase }}</span>
            </div>
            <div class="insurance-item">
              <span class="insurance-label">公积金比例</span>
              <span class="insurance-value">{{ insuranceInfo.housingRate }}%</span>
            </div>
          </div>
        </el-card>

        <!-- 工资趋势 -->
        <el-card class="trend-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-data-line"></i> 工资趋势</span>
          </div>
          <div class="trend-list">
            <div v-for="(item, index) in salaryTrend" :key="index" class="trend-item">
              <span class="trend-month">{{ item.month }}</span>
              <div class="trend-bar-wrapper">
                <div class="trend-bar" :style="{ width: (item.amount / maxTrend * 100) + '%' }"></div>
              </div>
              <span class="trend-amount">¥{{ item.amount }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 历史工资记录 -->
    <el-card class="history-card" shadow="hover">
      <div slot="header" class="card-header">
        <span><i class="el-icon-tickets"></i> 历史工资记录</span>
      </div>
      <el-table :data="salaryHistory" border stripe class="data-table">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="month" label="月份" width="100" align="center" />
        <el-table-column prop="baseSalary" label="基本工资" width="110" align="center" />
        <el-table-column prop="bonus" label="绩效奖金" width="110" align="center" />
        <el-table-column prop="allowance" label="补贴合计" width="110" align="center" />
        <el-table-column prop="deduction" label="扣款合计" width="110" align="center" />
        <el-table-column prop="tax" label="个税" width="100" align="center" />
        <el-table-column prop="netSalary" label="实发工资" width="130" align="center">
          <template slot-scope="scope">
            <span style="color: #67c23a; font-weight: 700;">¥{{ scope.row.netSalary }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === '已发放' ? 'success' : 'warning'" effect="dark" size="small">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Salary',
  data() {
    return {
      selectedMonth: '2025年06月',
      months: ['2025年06月', '2025年05月', '2025年04月', '2025年03月', '2025年02月', '2025年01月'],
      currentSalary: {
        baseSalary: '8,000.00', positionSalary: '3,000.00', bonus: '2,500.00', overtimePay: '800.00',
        transportAllowance: '300.00', mealAllowance: '500.00', housingAllowance: '1,000.00', attendanceBonus: '200.00',
        pension: '640.00', medical: '160.00', unemployment: '40.00', housingFund: '960.00',
        tax: '385.00', otherDeduction: '0.00', totalSalary: '16,300.00', totalDeduction: '2,185.00', afterTax: '14,115.00'
      },
      insuranceInfo: { pensionBase: '8,000', medicalBase: '8,000', housingBase: '12,000', housingRate: 12 },
      salaryTrend: [
        { month: '1月', amount: 12800 }, { month: '2月', amount: 13200 }, { month: '3月', amount: 13500 },
        { month: '4月', amount: 13800 }, { month: '5月', amount: 14100 }, { month: '6月', amount: 14115 }
      ],
      salaryHistory: [
        { month: '2025-05', baseSalary: '8,000', bonus: '2,000', allowance: '2,000', deduction: '2,185', tax: '350', netSalary: '13,465', status: '已发放' },
        { month: '2025-04', baseSalary: '8,000', bonus: '2,200', allowance: '2,000', deduction: '2,185', tax: '365', netSalary: '13,650', status: '已发放' },
        { month: '2025-03', baseSalary: '8,000', bonus: '1,800', allowance: '2,000', deduction: '2,185', tax: '330', netSalary: '13,285', status: '已发放' },
        { month: '2025-02', baseSalary: '8,000', bonus: '2,500', allowance: '2,000', deduction: '2,185', tax: '380', netSalary: '13,935', status: '已发放' },
        { month: '2025-01', baseSalary: '8,000', bonus: '1,500', allowance: '2,000', deduction: '2,185', tax: '310', netSalary: '13,005', status: '已发放' }
      ]
    }
  },
  computed: {
    maxTrend() {
      return Math.max(...this.salaryTrend.map(i => i.amount))
    }
  },
  methods: {
    loadSalary() { this.$message.info('加载工资数据...') },
    handleExport() { this.$message.success('工资条导出成功') },
    viewDetail(row) { this.$message.info(`查看 ${row.month} 工资详情`) }
  }
}
</script>

<style scoped>
.salary-container { padding: 0; }

.stat-row { margin-bottom: 24px; }
.stat-card { border: none; border-radius: 20px; cursor: pointer; transition: all 0.35s; overflow: hidden; }
.stat-card:hover { transform: translateY(-6px); box-shadow: 0 12px 28px rgba(0,0,0,0.12); }
.stat-content { display: flex; align-items: center; gap: 16px; }
.stat-icon { width: 56px; height: 56px; border-radius: 16px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-icon i { font-size: 26px; color: #fff; }
.stat-card-1 .stat-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-card-2 .stat-icon { background: linear-gradient(135deg, #11998e, #38ef7d); }
.stat-card-3 .stat-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
.stat-card-4 .stat-icon { background: linear-gradient(135deg, #667eea, #4ecdc4); }
.stat-value { font-size: 22px; font-weight: 800; color: #1e293b; line-height: 1.2; margin-bottom: 4px; }
.stat-label { font-size: 13px; color: #94a3b8; }

.detail-card, .insurance-card, .trend-card, .history-card { border: none; border-radius: 20px; margin-bottom: 24px; }
.detail-card >>> .el-card__header, .insurance-card >>> .el-card__header, .trend-card >>> .el-card__header, .history-card >>> .el-card__header {
  background: linear-gradient(135deg, #fafbfc, #f5f7fa); border-bottom: 1px solid #edf2f7;
}
.card-header { display: flex; align-items: center; justify-content: space-between; font-weight: 600; color: #1e293b; }
.card-header i { color: #667eea; margin-right: 8px; font-size: 18px; }
.header-actions { display: flex; gap: 10px; align-items: center; }

.salary-descriptions >>> .el-descriptions-item__label { background: #f8fafc; font-weight: 500; color: #475569; width: 120px; }
.salary-descriptions >>> .el-descriptions-item__content { color: #1e293b; font-weight: 500; }

.salary-summary { display: flex; justify-content: flex-end; gap: 32px; margin-top: 24px; padding: 20px; background: linear-gradient(135deg, #f5f7ff, #f0f2ff); border-radius: 16px; }
.summary-item { text-align: center; }
.summary-item span { display: block; font-size: 13px; color: #94a3b8; margin-bottom: 8px; }
.total-amount { font-size: 20px; font-weight: 700; color: #1e293b; }
.deduction-amount { font-size: 20px; font-weight: 700; color: #f56c6c; }
.net-amount { font-size: 24px; font-weight: 800; color: #67c23a; }

.insurance-list { display: flex; flex-direction: column; gap: 12px; }
.insurance-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; background: #f8fafc; border-radius: 12px; }
.insurance-label { font-size: 13px; color: #64748b; }
.insurance-value { font-size: 15px; font-weight: 600; color: #1e293b; }

.trend-list { display: flex; flex-direction: column; gap: 12px; }
.trend-item { display: flex; align-items: center; gap: 10px; }
.trend-month { font-size: 13px; color: #64748b; width: 30px; }
.trend-bar-wrapper { flex: 1; height: 20px; background: #f0f2f8; border-radius: 10px; overflow: hidden; }
.trend-bar { height: 100%; background: linear-gradient(135deg, #667eea, #764ba2); border-radius: 10px; transition: width 0.5s; }
.trend-amount { font-size: 13px; font-weight: 600; color: #1e293b; width: 60px; text-align: right; }

.history-card { margin-bottom: 24px; }
.data-table { border-radius: 12px; overflow: hidden; }
.data-table >>> .el-table__header th { background: linear-gradient(135deg, #f5f7fe, #f0f2fc); color: #334155; font-weight: 600; font-size: 13px; }
.data-table >>> .el-table__row:hover > td { background: linear-gradient(135deg, #f5f7ff, #f0f2ff) !important; }
.data-table .el-tag { border-radius: 20px; padding: 2px 12px; font-weight: 500; border: none; }

@media (max-width: 768px) { .stat-value { font-size: 18px; } .salary-summary { flex-direction: column; gap: 16px; } }
</style>