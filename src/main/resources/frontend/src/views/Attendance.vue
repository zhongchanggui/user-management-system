<template>
  <div class="attendance-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="24" class="stat-row">
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-1" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-time"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.total }}</p>
              <p class="stat-label">今日打卡</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-2" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-success"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.normal }}</p>
              <p class="stat-label">正常出勤</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-3" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-warning"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.late }}</p>
              <p class="stat-label">迟到/早退</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card stat-card-4" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon"><i class="el-icon-error"></i></div>
            <div class="stat-info">
              <p class="stat-value">{{ stats.absent }}</p>
              <p class="stat-label">旷工</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 每日打卡 + 月度统计 + 假期余额 -->
    <el-row :gutter="24" class="checkin-row">
      <el-col :xs="24" :sm="24" :md="8" :lg="8">
        <el-card class="checkin-card" shadow="hover">
          <div class="checkin-content">
            <div class="clock-display">
              <div class="clock-icon-ring"><i class="el-icon-alarm-clock"></i></div>
              <p class="clock-time">{{ currentTime }}</p>
              <p class="clock-date">{{ currentDate }}</p>
              <div class="location-badge" :class="locationClass">
                <i :class="locationIcon"></i>
                <span>{{ locationText }}</span>
              </div>
            </div>
            <div class="checkin-tips" v-if="userDistance !== null && !userInRange">
              <el-alert type="warning" :closable="false" show-icon>
                <span slot="title">您当前不在打卡范围内，请前往学校附近打卡</span>
              </el-alert>
            </div>
            <div class="checkin-buttons">
              <el-button type="primary" class="checkin-btn morning-btn" :disabled="!canMorningCheckin" @click="handleCheckin('morning')">
                <i class="el-icon-sunrise"></i>
                <span>{{ todayRecord.morningTime ? '已打卡 ' + todayRecord.morningTime : '上 班 打 卡' }}</span>
              </el-button>
              <el-button type="success" class="checkin-btn evening-btn" :disabled="!canEveningCheckin" @click="handleCheckin('evening')">
                <i class="el-icon-moon"></i>
                <span>{{ todayRecord.eveningTime ? '已打卡 ' + todayRecord.eveningTime : '下 班 打 卡' }}</span>
              </el-button>
              <el-button class="checkin-btn field-btn" @click="handleFieldCheckin" v-if="userDistance !== null && !userInRange">
                <i class="el-icon-position"></i>
                <span>外 勤 打 卡</span>
              </el-button>
            </div>
            <div class="checkin-rules">
              <div class="rule-item"><i class="el-icon-office-building"></i><span>黑龙江外国语学院</span></div>
              <div class="rule-item"><i class="el-icon-time"></i><span>工作时间 {{ ruleConfig.workStart }} - {{ ruleConfig.workEnd }}</span></div>
              <div class="rule-item"><i class="el-icon-location-outline"></i><span>打卡范围 {{ ruleConfig.rangeDistance }}米</span></div>
              <div class="rule-item" v-if="userDistance !== null"><i class="el-icon-position"></i><span>当前距离 {{ userDistance }}米</span></div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="16" :lg="16">
        <!-- 月度统计 + 加班统计 合并卡片 -->
        <el-card class="monthly-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-data-analysis"></i> 月度统计</span>
            <el-button v-if="isAdmin" size="mini" type="text" @click="ruleDrawer = true"><i class="el-icon-setting"></i> 规则</el-button>
          </div>
          <div class="monthly-stats">
            <div class="monthly-item item-work">
              <div class="monthly-icon"><i class="el-icon-s-flag"></i></div>
              <div class="monthly-detail">
                <span class="monthly-value">{{ monthlyStats.workDays }}</span>
                <span class="monthly-label">出勤天数</span>
              </div>
            </div>
            <div class="monthly-item item-late">
              <div class="monthly-icon"><i class="el-icon-alarm-clock"></i></div>
              <div class="monthly-detail">
                <span class="monthly-value">{{ monthlyStats.lateCount }}</span>
                <span class="monthly-label">迟到次数</span>
              </div>
            </div>
            <div class="monthly-item item-early">
              <div class="monthly-icon"><i class="el-icon-time"></i></div>
              <div class="monthly-detail">
                <span class="monthly-value">{{ monthlyStats.earlyCount }}</span>
                <span class="monthly-label">早退次数</span>
              </div>
            </div>
            <div class="monthly-item item-overtime">
              <div class="monthly-icon"><i class="el-icon-s-cooperation"></i></div>
              <div class="monthly-detail">
                <span class="monthly-value">{{ monthlyStats.overtimeHours }}h</span>
                <span class="monthly-label">加班时长</span>
              </div>
            </div>
          </div>
          <!-- 加班统计内嵌 -->
          <div class="overtime-inline">
            <div class="overtime-inline-item">
              <i class="el-icon-time"></i>
              <span class="overtime-label">本月累计</span>
              <span class="overtime-value">{{ monthlyStats.overtimeHours }}h</span>
            </div>
            <div class="overtime-inline-item">
              <i class="el-icon-refresh-right"></i>
              <span class="overtime-label">可调休</span>
              <span class="overtime-value">{{ Math.floor(monthlyStats.overtimeHours * 0.5) }}h</span>
            </div>
            <div class="overtime-inline-item">
              <i class="el-icon-money"></i>
              <span class="overtime-label">加班补贴</span>
              <span class="overtime-value">¥{{ (monthlyStats.overtimeHours * 50).toFixed(0) }}</span>
            </div>
          </div>
          <div class="overtime-warning" v-if="monthlyStats.overtimeHours > 20">
            <el-alert type="info" :closable="false" show-icon>
              <span slot="title">本月已累计加班 {{ monthlyStats.overtimeHours }} 小时，注意休息~</span>
            </el-alert>
          </div>
        </el-card>

        <!-- 假期余额 -->
        <el-card class="leave-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-document"></i> 假期余额</span>
            <el-button size="mini" type="text" @click="$router.push('/leave')"><i class="el-icon-s-promotion"></i> 申请</el-button>
          </div>
          <div class="leave-content">
            <div class="leave-stats">
              <div class="leave-stat-item" v-for="item in leaveTypes" :key="item.value">
                <div class="leave-dot" :style="{background: item.color}"></div>
                <span class="leave-label">{{ item.label }}</span>
                <span class="leave-value">{{ leaveBalance[item.value] || 0 }}</span>
                <span class="leave-unit">天</span>
              </div>
            </div>
            <div class="leave-right">
              <div class="pending-leave" v-if="pendingLeaves.length">
                <el-tag size="small" type="warning" effect="dark">待审批 {{ pendingLeaves.length }} 条</el-tag>
              </div>
              <div class="leave-records">
                <div class="leave-record-item" v-for="(leave, idx) in recentLeaves" :key="idx">
                  <el-tag :type="getLeaveStatusType(leave.status)" size="mini">{{ getLeaveStatusName(leave.status) }}</el-tag>
                  <span class="leave-record-text">{{ leave.typeName }} {{ leave.days }}天</span>
                  <span class="leave-record-time">{{ leave.startDate }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 考勤日历 + 打卡地图 并排 -->
    <el-row :gutter="24" class="calendar-map-row">
      <el-col :xs="24" :sm="24" :md="13" :lg="13">
        <el-card class="calendar-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-date"></i> 考勤日历</span>
            <div class="header-actions">
              <el-button-group size="small" class="month-nav-group">
                <el-button icon="el-icon-arrow-left" @click="prevMonth">上月</el-button>
                <el-button @click="goToday" class="month-display">{{ currentYear }}年{{ currentMonthNum }}月</el-button>
                <el-button @click="nextMonth">下月<i class="el-icon-arrow-right"></i></el-button>
              </el-button-group>
            </div>
          </div>
          <div class="calendar-grid">
            <div class="calendar-header">
              <div v-for="day in weekDays" :key="day" class="calendar-weekday">{{ day }}</div>
            </div>
            <div class="calendar-body">
              <el-tooltip v-for="(item, index) in calendarData" :key="index" :content="getCellTooltip(item)" placement="top" effect="dark" :disabled="!item.day">
                <div class="calendar-cell" :class="getCellClass(item)">
                  <span class="cell-date">{{ item.day }}</span>
                  <span v-if="item.status" class="cell-status">{{ getStatusText(item.status) }}</span>
                  <span v-if="item.checkIn" class="cell-time">{{ item.checkIn }}</span>
                </div>
              </el-tooltip>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="24" :md="11" :lg="11">
        <el-card class="map-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-location"></i> 打卡范围地图</span>
            <div class="header-actions">
              <el-tag size="small" :type="userInRange ? 'success' : 'danger'" effect="dark" v-if="userDistance !== null">{{ userInRange ? '在打卡范围内' : '超出打卡范围' }}</el-tag>
              <el-tag size="small" effect="plain" v-if="currentAddress">{{ currentAddress }}</el-tag>
            </div>
          </div>
          <div v-loading="mapLoading" element-loading-text="地图加载中..." element-loading-spinner="el-icon-loading">
            <div id="amap-container" class="map-container"></div>
          </div>
          <div class="map-legend">
            <div class="legend-item"><span class="legend-dot" style="background:#667eea"></span> 打卡范围 {{ ruleConfig.rangeDistance }}米</div>
            <div class="legend-item"><span class="legend-dot" style="background:#67c23a"></span> 范围内可打卡</div>
            <div class="legend-item"><span class="legend-dot" style="background:#f56c6c"></span> 超出范围</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 打卡记录表格 -->
    <el-card class="record-card" shadow="hover">
      <div slot="header" class="card-header">
        <span><i class="el-icon-tickets"></i> 打卡记录</span>
        <div class="header-actions">
          <el-radio-group v-model="recordFilter" size="small" @change="filterRecords" class="filter-radio-group">
            <el-radio-button label="all">全部</el-radio-button>
            <el-radio-button label="normal">正常</el-radio-button>
            <el-radio-button label="late">迟到</el-radio-button>
            <el-radio-button label="early">早退</el-radio-button>
          </el-radio-group>
          <el-button type="primary" icon="el-icon-download" size="small" @click="exportDialog = true">导出报表</el-button>
        </div>
      </div>
      <el-table :data="records" border stripe class="data-table" v-loading="tableLoading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="date" label="日期" width="120" align="center" />
        <el-table-column prop="weekday" label="星期" width="80" align="center" />
        <el-table-column prop="morningTime" label="上班打卡" width="120" align="center">
          <template slot-scope="scope"><span :class="{ 'late-text': scope.row.morningLate }">{{ scope.row.morningTime || '未打卡' }}</span></template>
        </el-table-column>
        <el-table-column prop="eveningTime" label="下班打卡" width="120" align="center">
          <template slot-scope="scope"><span :class="{ 'early-text': scope.row.eveningEarly }">{{ scope.row.eveningTime || '未打卡' }}</span></template>
        </el-table-column>
        <el-table-column prop="workHours" label="工作时长" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getRecordStatusType(scope.row.status)" effect="dark" size="small">{{ getRecordStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination background :current-page="1" :page-sizes="[10, 20, 30]" :page-size="10" layout="total, sizes, prev, pager, next" :total="records.length" />
      </div>
    </el-card>

    <!-- 考勤规则设置抽屉（管理员） -->
    <el-drawer title="考勤规则设置" :visible.sync="ruleDrawer" size="460px" :before-close="handleRuleClose" append-to-body custom-class="rule-drawer" :modal="true" :modal-append-to-body="true" :lock-scroll="true" direction="rtl">
      <div class="rule-drawer-content">
        <el-form label-width="120px" size="small">
          <el-divider content-position="left"><i class="el-icon-time"></i> 工作时间</el-divider>
          <el-form-item label="上班时间">
            <el-time-picker v-model="ruleConfig.workStart" format="HH:mm" value-format="HH:mm" style="width:100%"></el-time-picker>
          </el-form-item>
          <el-form-item label="下班时间">
            <el-time-picker v-model="ruleConfig.workEnd" format="HH:mm" value-format="HH:mm" style="width:100%"></el-time-picker>
          </el-form-item>
          <el-divider content-position="left"><i class="el-icon-setting"></i> 打卡规则</el-divider>
          <el-form-item label="迟到阈值">
            <el-input-number v-model="ruleConfig.lateThreshold" :min="0" :max="60"></el-input-number>
            <span style="margin-left:8px;color:#94a3b8">分钟</span>
          </el-form-item>
          <el-form-item label="旷工阈值">
            <el-input-number v-model="ruleConfig.absentThreshold" :min="30" :max="240"></el-input-number>
            <span style="margin-left:8px;color:#94a3b8">分钟</span>
          </el-form-item>
          <el-form-item label="打卡范围">
            <el-input-number v-model="ruleConfig.rangeDistance" :min="100" :max="2000" :step="100"></el-input-number>
            <span style="margin-left:8px;color:#94a3b8">米</span>
          </el-form-item>
          <el-divider content-position="left"><i class="el-icon-date"></i> 工作日设置</el-divider>
          <el-form-item label="工作日">
            <el-checkbox-group v-model="ruleConfig.workdays">
              <el-checkbox v-for="d in ['周一','周二','周三','周四','周五','周六','周日']" :key="d" :label="d">{{ d }}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-divider content-position="left"><i class="el-icon-position"></i> 外勤设置</el-divider>
          <el-form-item label="允许外勤">
            <el-switch v-model="ruleConfig.fieldWorkEnabled"></el-switch>
          </el-form-item>
          <el-form-item label="需拍照">
            <el-switch v-model="ruleConfig.fieldRequirePhoto" :disabled="!ruleConfig.fieldWorkEnabled"></el-switch>
          </el-form-item>
        </el-form>
        <div class="drawer-footer">
          <el-button @click="ruleDrawer = false" round>取 消</el-button>
          <el-button type="primary" @click="saveRules" round><i class="el-icon-check"></i> 保 存</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 导出报表弹窗 -->
    <el-dialog title="导出考勤报表" :visible.sync="exportDialog" width="520px" append-to-body destroy-on-close custom-class="export-dialog">
      <div class="export-preview">
        <i class="el-icon-download"></i>
        <span>选择导出参数后点击导出</span>
      </div>
      <el-form label-width="100px" class="export-form">
        <el-form-item label="报表类型">
          <el-radio-group v-model="exportType">
            <el-radio-button label="daily">日报表</el-radio-button>
            <el-radio-button label="monthly">月报表</el-radio-button>
            <el-radio-button label="personal">个人统计</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择月份">
          <el-date-picker v-model="exportMonth" type="month" placeholder="选择月份" style="width: 100%"></el-date-picker>
        </el-form-item>
        <el-form-item label="导出格式">
          <el-radio-group v-model="exportFormat">
            <el-radio label="excel">Excel</el-radio>
            <el-radio label="csv">CSV</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="exportDialog = false">取 消</el-button>
        <el-button type="primary" icon="el-icon-download" @click="handleExportReport">导 出</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Attendance',
  data() {
    const now = new Date()
    return {
      currentYear: now.getFullYear(), currentMonthNum: now.getMonth() + 1,
      currentTime: '', currentDate: '', timer: null,
      map: null, currentAddress: '', userDistance: null, userInRange: true, userPosition: null,
      tableLoading: true, mapLoading: true, recordFilter: 'all',
      ruleDrawer: false,
      exportDialog: false, exportType: 'monthly', exportMonth: '', exportFormat: 'excel',
      ruleConfig: { workStart: '09:00', workEnd: '18:00', lateThreshold: 10, absentThreshold: 60, rangeDistance: 500, workdays: ['周一','周二','周三','周四','周五'], fieldWorkEnabled: true, fieldRequirePhoto: false },
      weekDays: ['日', '一', '二', '三', '四', '五', '六'],
      stats: { total: 42, normal: 38, late: 3, absent: 1 },
      monthlyStats: { workDays: 22, lateCount: 2, earlyCount: 1, overtimeHours: 8 },
      todayRecord: { morningTime: '', eveningTime: '' },
      calendarData: [],
      leaveTypes: [
        { value: 'annual', label: '年假', color: '#667eea' },
        { value: 'sick', label: '病假', color: '#67c23a' },
        { value: 'personal', label: '事假', color: '#e6a23c' }
      ],
      leaveBalance: { annual: 12, sick: 10, personal: 5 },
      pendingLeaves: [
        { type: 'annual', days: 2, status: 'pending', startDate: '2026-06-10' }
      ],
      recentLeaves: [
        { typeName: '病假', days: 3, status: 'approved', startDate: '2026-05-20' },
        { typeName: '事假', days: 1, status: 'rejected', startDate: '2026-05-15' }
      ],
      allRecords: [
        { date: '2026-06-02', weekday: '周一', morningTime: '08:55', eveningTime: '18:05', workHours: '9h10m', status: 'normal', morningLate: false, eveningEarly: false, remark: '' },
        { date: '2026-06-01', weekday: '周日', morningTime: '', eveningTime: '', workHours: '-', status: 'rest', morningLate: false, eveningEarly: false, remark: '休息日' },
        { date: '2026-05-31', weekday: '周六', morningTime: '', eveningTime: '', workHours: '-', status: 'rest', morningLate: false, eveningEarly: false, remark: '休息日' },
        { date: '2026-05-30', weekday: '周五', morningTime: '09:12', eveningTime: '18:30', workHours: '9h18m', status: 'late', morningLate: true, eveningEarly: false, remark: '迟到12分钟' },
        { date: '2026-05-29', weekday: '周四', morningTime: '08:45', eveningTime: '17:50', workHours: '9h05m', status: 'early', morningLate: false, eveningEarly: true, remark: '早退10分钟' },
        { date: '2026-05-28', weekday: '周三', morningTime: '08:50', eveningTime: '18:10', workHours: '9h20m', status: 'normal', morningLate: false, eveningEarly: false, remark: '' },
        { date: '2026-05-27', weekday: '周二', morningTime: '08:30', eveningTime: '20:30', workHours: '12h00m', status: 'overtime', morningLate: false, eveningEarly: false, remark: '加班2.5小时' }
      ],
      records: []
    }
  },
  computed: {
    ...mapGetters(['isAdmin']),
    canMorningCheckin() { return !this.todayRecord.morningTime },
    canEveningCheckin() { return this.todayRecord.morningTime && !this.todayRecord.eveningTime },
    locationClass() { if (this.userDistance === null) return 'locating'; return this.userInRange ? 'in-range' : 'out-range' },
    locationIcon() { if (this.userDistance === null) return 'el-icon-loading'; return this.userInRange ? 'el-icon-circle-check' : 'el-icon-circle-close' },
    locationText() { if (this.userDistance === null) return '正在定位...'; return this.userInRange ? '已在打卡范围内' : '超出打卡范围' }
  },
  created() { this.generateCalendar(); this.startClock(); this.records = [...this.allRecords]; this.tableLoading = false },
  mounted() { this.$nextTick(() => { this.initMap() }) },
  beforeDestroy() { if (this.timer) clearInterval(this.timer); if (this.map) { this.map.destroy(); this.map = null } },
  methods: {
    prevMonth() { if (this.currentMonthNum === 1) { this.currentYear--; this.currentMonthNum = 12 } else { this.currentMonthNum-- }; this.generateCalendar() },
    nextMonth() { if (this.currentMonthNum === 12) { this.currentYear++; this.currentMonthNum = 1 } else { this.currentMonthNum++ }; this.generateCalendar() },
    goToday() { const n = new Date(); this.currentYear = n.getFullYear(); this.currentMonthNum = n.getMonth() + 1; this.generateCalendar() },
    startClock() { this.updateTime(); this.timer = setInterval(this.updateTime, 1000) },
    updateTime() { const n = new Date(); this.currentTime = n.toLocaleTimeString('zh-CN', { hour12: false }); this.currentDate = `${n.getFullYear()}年${n.getMonth()+1}月${n.getDate()}日 星期${'日一二三四五六'[n.getDay()]}` },
    generateCalendar() {
      const now = new Date(), year = this.currentYear, month = this.currentMonthNum - 1
      const firstDay = new Date(year, month, 1).getDay(), daysInMonth = new Date(year, month+1, 0).getDate()
      const data = []
      for (let i = 0; i < firstDay; i++) data.push({ day: '', status: '', checkIn: '' })
      for (let d = 1; d <= daysInMonth; d++) {
        const date = new Date(year, month, d), dow = date.getDay()
        const isWeekend = dow === 0 || dow === 6
        const isPast = date < new Date(now.getFullYear(), now.getMonth(), now.getDate())
        const isToday = d === now.getDate() && month === now.getMonth() && year === now.getFullYear()
        let status = '', checkIn = ''
        if (isToday) { status = 'today'; if (this.todayRecord.morningTime) checkIn = this.todayRecord.morningTime }
        else if (isWeekend) status = 'rest'
        else if (isPast) { const s = (year*10000+(month+1)*100+d)%100; status = s > 12 ? 'normal' : 'late'; checkIn = status==='normal' ? '08:'+String(20+(s%30)).padStart(2,'0') : '09:'+String(1+(s%12)).padStart(2,'0') }
        data.push({ day: d, status, checkIn })
      }
      this.calendarData = data; this.updateMonthlyStats(data)
    },
    updateMonthlyStats(data) { const n = data.filter(d => d.status === 'normal'), l = data.filter(d => d.status === 'late'); this.monthlyStats.workDays = n.length + l.length; this.monthlyStats.lateCount = l.length },
    getCellClass(item) { return { 'cell-empty': !item.day, 'cell-normal': item.status==='normal', 'cell-late': item.status==='late', 'cell-rest': item.status==='rest', 'cell-today': item.status==='today' } },
    getCellTooltip(item) { if (!item.day) return ''; const map = { normal: `正常出勤 ${item.checkIn}`, late: `迟到 ${item.checkIn}`, rest: '休息日', today: '今天' }; return map[item.status] || `${item.day}日` },
    getStatusText(s) { return { normal:'正常', late:'迟到', rest:'休息', absent:'旷工', today:'今天' }[s] || '' },
    getRecordStatusType(s) { return { normal:'success', late:'warning', early:'warning', absent:'danger', rest:'info', overtime:'' }[s]||'info' },
    getRecordStatusText(s) { return { normal:'正常', late:'迟到', early:'早退', absent:'旷工', rest:'休息', overtime:'加班' }[s]||'未知' },
    getLeaveStatusType(s) { return { pending:'warning', approved:'success', rejected:'danger' }[s]||'info' },
    getLeaveStatusName(s) { return { pending:'待审批', approved:'已通过', rejected:'已驳回' }[s]||s },
    handleCheckin(type) {
      if (this.userDistance !== null && this.userDistance > this.ruleConfig.rangeDistance) { this.$message.error({ message: `打卡失败！距学校${this.userDistance}米，超出${this.ruleConfig.rangeDistance}米范围`, duration: 3000, showClose: true }); return }
      const t = new Date().toLocaleTimeString('zh-CN', { hour12: false, hour:'2-digit', minute:'2-digit', second:'2-digit' })
      const fullTime = new Date().toLocaleString('zh-CN')
      if (type === 'morning') { this.todayRecord.morningTime = t; this.$notify({ title: '打卡成功', message: `上班打卡时间：${fullTime}`, type: 'success', duration: 3000, position: 'top-right' }) }
      else {
        let wh = ''
        if (this.todayRecord.morningTime) { const m = new Date('2000/01/01 ' + this.todayRecord.morningTime), e = new Date('2000/01/01 ' + t); wh = ((e - m) / 1000 / 3600).toFixed(1) }
        this.todayRecord.eveningTime = t
        this.$notify({ title: '打卡成功', message: wh ? `下班打卡时间：${fullTime}<br>今日工作时长：${wh}小时` : `下班打卡时间：${fullTime}`, type: 'success', duration: 4000, dangerouslyUseHTMLString: true })
      }
      this.generateCalendar()
    },
    handleFieldCheckin() {
      this.$confirm('确认进行外勤打卡？将记录当前位置信息', '外勤打卡', { confirmButtonText: '确认打卡', cancelButtonText: '取消', type: 'info' }).then(() => {
        const t = new Date().toLocaleTimeString('zh-CN', { hour12: false, hour:'2-digit', minute:'2-digit' })
        this.todayRecord.morningTime = t
        this.$notify({ title: '外勤打卡成功', message: `打卡时间：${t}<br>打卡类型：外勤`, type: 'warning', duration: 3000, dangerouslyUseHTMLString: true })
        this.generateCalendar()
      }).catch(() => {})
    },
    filterRecords() { if (this.recordFilter === 'all') { this.records = [...this.allRecords] } else { this.records = this.allRecords.filter(r => r.status === this.recordFilter) } },
    handleExportReport() { this.$message.success(`已导出${this.exportType === 'daily' ? '日报表' : this.exportType === 'monthly' ? '月报表' : '个人统计'}（${this.exportFormat.toUpperCase()}）`); this.exportDialog = false },
    saveRules() { this.$message.success('考勤规则已保存'); this.ruleDrawer = false },
    handleRuleClose(done) { done() },
    initMap() {
      try { if (window.AMap) { this.createMap(); return }; this.mapLoading = true
        const s = document.createElement('script'); s.src = 'https://webapi.amap.com/maps?v=2.0&key=cf4b4c3055d3946390a1cc41e4f94955&plugin=AMap.Geolocation,AMap.Scale,AMap.ToolBar'; s.crossOrigin = 'anonymous'
        s.onerror = () => { this.mapLoading = false; this.showFallbackMap() }; s.onload = () => { try { this.createMap() } catch(e) { this.mapLoading = false; this.showFallbackMap() } }; document.head.appendChild(s)
      } catch(e) { this.mapLoading = false; this.showFallbackMap() }
    },
    createMap() {
      try {
        const lng = 126.5500, lat = 45.8580
        const container = document.getElementById('amap-container')
        if (!container) { this.mapLoading = false; this.showFallbackMap(); return }
        this.map = new AMap.Map('amap-container', { zoom: 15, center: [lng, lat], mapStyle: 'amap://styles/whitesmoke', resizeEnable: true })
        if (!this.map) { this.mapLoading = false; this.showFallbackMap(); return }
        this.map.addControl(new AMap.Scale()); this.map.addControl(new AMap.ToolBar({ position: 'RT' }))
        this.map.add(new AMap.Marker({ position: [lng, lat], title: '黑龙江外国语学院', animation: 'AMAP_ANIMATION_DROP' }))
        this.map.add(new AMap.Circle({ center: [lng, lat], radius: this.ruleConfig.rangeDistance, strokeColor: '#667eea', strokeWeight: 2, strokeOpacity: 0.8, fillColor: '#667eea', fillOpacity: 0.1, strokeStyle: 'dashed' }))
        const iw = new AMap.InfoWindow({ content: '<div style="padding:8px;font-family:Microsoft YaHei,sans-serif"><h4 style="margin:0 0 6px;color:#333;font-size:14px">📍 黑龙江外国语学院</h4><p style="margin:0;color:#666;font-size:12px">哈尔滨市利民开发区师大南路1号</p><p style="margin:4px 0 0;color:#667eea;font-size:12px">打卡范围：500米 · 工作时间：09:00 - 18:00</p></div>', offset: new AMap.Pixel(0,-36) }); iw.open(this.map, [lng, lat])
        this.locateUser(lng, lat); this.currentAddress = '黑龙江外国语学院'; this.mapLoading = false
      } catch(e) { this.mapLoading = false; this.showFallbackMap() }
    },
    locateUser(clng, clat) {
      if (!this.map || !AMap.Geolocation) return
      try {
        const geo = new AMap.Geolocation({ enableHighAccuracy: true, timeout: 8000, buttonPosition: 'RB', showCircle: true, showMarker: true, panToLocation: false, zoomToAccuracy: false })
        if (!this.map) return
        this.map.addControl(geo)
        geo.getCurrentPosition((s, r) => {
          if (s === 'complete' && this.map) {
            const p = r.position; this.userPosition = [p.lng, p.lat]; const d = Math.round(this.calcDistance(p.lng, p.lat, clng, clat)); this.userDistance = d; this.userInRange = d <= this.ruleConfig.rangeDistance
            try {
              this.map.add(new AMap.Marker({ position: [p.lng, p.lat], icon: 'https://webapi.amap.com/theme/v1.3/markers/n/loc.png', title: '我的位置' }))
              this.map.add(new AMap.Polyline({ path: [[clng,clat],[p.lng,p.lat]], strokeColor: this.userInRange?'#67c23a':'#f56c6c', strokeWeight: 2, strokeStyle: 'dashed' }))
            } catch (mapErr) { console.warn('地图标注失败:', mapErr) }
            this.currentAddress = `距学校${d}米`; if (!this.userInRange) this.$message.warning(`距学校${d}米，超出${this.ruleConfig.rangeDistance}米打卡范围`)
          }
        })
      } catch(e) { console.warn('定位失败:', e) }
    },
    calcDistance(lng1, lat1, lng2, lat2) { const r = Math.PI/180, R = 6371000; const dLat = (lat2-lat1)*r, dLng = (lng2-lng1)*r; const a = Math.sin(dLat/2)**2 + Math.cos(lat1*r)*Math.cos(lat2*r)*Math.sin(dLng/2)**2; return R*2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a)) },
    showFallbackMap() { const c = document.getElementById('amap-container'); if (c) c.innerHTML = '<div style="display:flex;flex-direction:column;align-items:center;justify-content:center;height:100%;background:linear-gradient(135deg,#f5f7fe,#eef1fb);border-radius:16px;text-align:center;padding:20px"><i class="el-icon-location" style="font-size:48px;color:#667eea;margin-bottom:16px"></i><h3 style="color:#334155;margin:0 0 8px">📍 黑龙江外国语学院</h3><p style="color:#94a3b8;margin:0 0 6px">哈尔滨市利民开发区师大南路1号 · 打卡范围 500 米</p><p style="color:#667eea;font-size:13px;margin:0">工作时间：09:00 - 18:00</p><p style="color:#94a3b8;font-size:12px;margin-top:12px">地图服务加载中，请确保网络连接正常</p></div>' }
  }
}
</script>

<style scoped>
/* ===== 页面容器 ===== */
.attendance-container {
  padding: 24px;
  width: 100%;
  box-sizing: border-box;
  animation: fadeInUp 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

/* ===== 统计卡片 ===== */
.stat-row {
  margin-bottom: 24px;
}

.stat-card {
  border: none;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  border: 1px solid rgba(102, 126, 234, 0.08);
  position: relative;
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
  opacity: 0.12;
  transform: scale(1.8);
}

.stat-card-1::before { background: radial-gradient(circle, #667eea, transparent 70%); }
.stat-card-2::before { background: radial-gradient(circle, #11998e, transparent 70%); }
.stat-card-3::before { background: radial-gradient(circle, #f093fb, transparent 70%); }
.stat-card-4::before { background: radial-gradient(circle, #eb3349, transparent 70%); }

.stat-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 24px 48px -12px rgba(102, 126, 234, 0.2);
  border-color: rgba(102, 126, 234, 0.2);
}

.stat-card >>> .el-card__body {
  padding: 20px 24px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.stat-card:hover .stat-icon {
  transform: rotate(-8deg) scale(1.1);
}

.stat-icon i {
  font-size: 28px;
  color: #fff;
}

.stat-card-1 .stat-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.35);
}

.stat-card-2 .stat-icon {
  background: linear-gradient(135deg, #11998e, #38ef7d);
  box-shadow: 0 8px 20px rgba(17, 153, 142, 0.35);
}

.stat-card-3 .stat-icon {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  box-shadow: 0 8px 20px rgba(240, 147, 251, 0.35);
}

.stat-card-4 .stat-icon {
  background: linear-gradient(135deg, #eb3349, #f45c43);
  box-shadow: 0 8px 20px rgba(235, 51, 73, 0.35);
}

.stat-value {
  font-size: 30px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.2;
  margin-bottom: 4px;
  animation: countUp 0.8s ease-out;
}

@keyframes countUp {
  from { opacity: 0; transform: scale(0.7) translateY(10px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
  font-weight: 500;
}

/* ===== 卡片头部通用 ===== */
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-weight: 600;
  color: #1e293b;
  flex-wrap: wrap;
  gap: 10px;
}

.card-header i {
  color: #667eea;
  margin-right: 8px;
  font-size: 18px;
  transition: all 0.3s;
}

.card-header:hover i {
  transform: scale(1.1) rotate(-5deg);
  color: #764ba2;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

/* ===== 打卡区域 ===== */
.checkin-row {
  margin-bottom: 24px;
}

.checkin-row .el-col {
  display: flex;
}

.checkin-row .el-card {
  flex: 1;
}

.checkin-card {
  border: none;
  border-radius: 24px;
  background: linear-gradient(145deg, #ffffff, #f8faff);
  display: flex;
  flex-direction: column;
  border: 1px solid rgba(102, 126, 234, 0.08);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.checkin-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
  opacity: 0;
  transition: opacity 0.3s;
}

.checkin-card:hover::before {
  opacity: 1;
}

.checkin-card:hover {
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.12);
  border-color: rgba(102, 126, 234, 0.15);
}

.checkin-card >>> .el-card__header {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04), rgba(118, 75, 162, 0.03));
  border-bottom: 1px solid rgba(102, 126, 234, 0.08);
}

.checkin-content {
  text-align: center;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

/* 时钟显示区 */
.clock-display {
  margin-bottom: 16px;
  padding: 24px 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.06), rgba(118, 75, 162, 0.04));
  border-radius: 24px;
  position: relative;
  overflow: hidden;
}

.clock-display::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle at 30% 30%, rgba(102, 126, 234, 0.05) 0%, transparent 50%);
  animation: clockGlow 8s ease-in-out infinite alternate;
  pointer-events: none;
}

@keyframes clockGlow {
  0% { transform: translate(0, 0); }
  100% { transform: translate(10%, 10%); }
}

.clock-icon-ring {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.35);
  position: relative;
  z-index: 1;
  animation: ringPulse 3s ease-in-out infinite;
}

@keyframes ringPulse {
  0%, 100% { box-shadow: 0 12px 32px rgba(102, 126, 234, 0.35); }
  50% { box-shadow: 0 16px 40px rgba(102, 126, 234, 0.5); }
}

.clock-icon-ring i {
  font-size: 30px;
  color: #fff;
}

.clock-time {
  font-size: 42px;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea, #764ba2, #f093fb);
  background-size: 200% 200%;
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 6px;
  letter-spacing: 4px;
  position: relative;
  z-index: 1;
  animation: gradientText 5s ease-in-out infinite;
}

@keyframes gradientText {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.clock-date {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
  position: relative;
  z-index: 1;
}

.location-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  margin-top: 12px;
  position: relative;
  z-index: 1;
  transition: all 0.3s;
}

.location-badge.in-range {
  background: rgba(103, 194, 58, 0.12);
  color: #67c23a;
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.15);
}

.location-badge.out-range {
  background: rgba(245, 108, 108, 0.12);
  color: #f56c6c;
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.15);
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-4px); }
  75% { transform: translateX(4px); }
}

.location-badge.locating {
  background: rgba(230, 162, 60, 0.12);
  color: #e6a23c;
}

.checkin-tips {
  margin-bottom: 12px;
}

.checkin-tips >>> .el-alert {
  border-radius: 12px;
  border: none;
}

/* 打卡按钮 */
.checkin-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 20px 0;
}

.checkin-btn {
  width: 100%;
  height: 58px;
  border-radius: 18px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 3px;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.checkin-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s ease;
}

.checkin-btn:hover::before {
  left: 100%;
}

.checkin-btn::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: width 0.6s, height 0.6s;
}

.checkin-btn:active::after {
  width: 400px;
  height: 400px;
}

.morning-btn {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  box-shadow: 0 10px 24px rgba(102, 126, 234, 0.35);
}

.morning-btn:hover:not(:disabled) {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 18px 36px rgba(102, 126, 234, 0.5);
}

.morning-btn:active:not(:disabled) {
  transform: translateY(-1px) scale(0.98);
}

.evening-btn {
  background: linear-gradient(135deg, #11998e, #38ef7d);
  border: none;
  box-shadow: 0 10px 24px rgba(17, 153, 142, 0.35);
}

.evening-btn:hover:not(:disabled) {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 18px 36px rgba(17, 153, 142, 0.5);
}

.evening-btn:active:not(:disabled) {
  transform: translateY(-1px) scale(0.98);
}

.field-btn {
  background: linear-gradient(135deg, #e6a23c, #f0c78a);
  border: none;
  box-shadow: 0 10px 24px rgba(230, 162, 60, 0.35);
  color: #fff;
}

.field-btn:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 18px 36px rgba(230, 162, 60, 0.5);
}

.checkin-btn:disabled {
  opacity: 0.35;
  box-shadow: none;
  cursor: not-allowed;
  filter: grayscale(0.4);
}

.checkin-btn:disabled::before,
.checkin-btn:disabled::after {
  display: none;
}

/* 打卡规则 */
.checkin-rules {
  text-align: left;
  padding: 16px;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  border: 1px solid rgba(102, 126, 234, 0.06);
}

.rule-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #64748b;
  transition: all 0.2s;
}

.rule-item:hover {
  color: #334155;
  transform: translateX(4px);
}

.rule-item i {
  color: #667eea;
  font-size: 14px;
  width: 16px;
  text-align: center;
  flex-shrink: 0;
}

/* ===== 月度统计卡片 ===== */
.monthly-card {
  border: none;
  border-radius: 24px;
  border: 1px solid rgba(102, 126, 234, 0.08);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.monthly-card:hover {
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.15);
  transform: translateY(-4px);
}

.monthly-card >>> .el-card__header,
.leave-card >>> .el-card__header {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04), rgba(118, 75, 162, 0.03));
  border-bottom: 1px solid rgba(102, 126, 234, 0.08);
}

.monthly-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.monthly-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 14px;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-radius: 16px;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.monthly-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.08);
  border-color: rgba(102, 126, 234, 0.1);
}

.monthly-icon {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all 0.35s;
}

.monthly-item:hover .monthly-icon {
  transform: rotate(-8deg) scale(1.1);
}

.monthly-icon i {
  font-size: 22px;
  color: #fff;
}

.item-work .monthly-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
}

.item-late .monthly-icon {
  background: linear-gradient(135deg, #e6a23c, #f0c78a);
  box-shadow: 0 6px 16px rgba(230, 162, 60, 0.3);
}

.item-early .monthly-icon {
  background: linear-gradient(135deg, #f56c6c, #f89898);
  box-shadow: 0 6px 16px rgba(245, 108, 108, 0.3);
}

.item-overtime .monthly-icon {
  background: linear-gradient(135deg, #11998e, #38ef7d);
  box-shadow: 0 6px 16px rgba(17, 153, 142, 0.3);
}

.monthly-detail {
  display: flex;
  flex-direction: column;
}

.monthly-value {
  font-size: 24px;
  font-weight: 800;
  color: #1e293b;
  line-height: 1.2;
}

.monthly-label {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
  font-weight: 500;
}

.overtime-warning {
  margin-top: 14px;
}

.overtime-warning >>> .el-alert {
  border-radius: 12px;
  border: none;
}

/* 加班统计内嵌样式 */
.overtime-inline {
  display: flex;
  gap: 16px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed rgba(102, 126, 234, 0.15);
}

.overtime-inline-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04), rgba(118, 75, 162, 0.03));
  border-radius: 12px;
  transition: all 0.3s;
}

.overtime-inline-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08), rgba(118, 75, 162, 0.06));
  transform: translateY(-2px);
}

.overtime-inline-item i {
  font-size: 20px;
  color: #667eea;
}

.overtime-label {
  font-size: 12px;
  color: #94a3b8;
  flex: 1;
}

.overtime-value {
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

/* ===== 假期余额 ===== */
.leave-card {
  border: none;
  border-radius: 24px;
  margin-top: 24px;
  border: 1px solid rgba(102, 126, 234, 0.08);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}

.leave-card:hover {
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.15);
  transform: translateY(-4px);
}

.leave-content {
  display: flex;
  gap: 24px;
}

.leave-stats {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.leave-right {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.leave-stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  background: linear-gradient(135deg, #f8fafc, #f1f5f9);
  border-radius: 14px;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.leave-stat-item:hover {
  transform: translateX(6px);
  border-color: rgba(102, 126, 234, 0.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
}

.leave-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.leave-label {
  font-size: 13px;
  color: #64748b;
  flex: 1;
  font-weight: 500;
}

.leave-value {
  font-size: 22px;
  font-weight: 800;
  color: #1e293b;
}

.leave-unit {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
}

.pending-leave {
  margin-top: 14px;
}

.pending-leave >>> .el-tag {
  border-radius: 20px;
  padding: 4px 14px;
}

.leave-records {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.leave-record-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #64748b;
  padding: 8px 12px;
  background: #f8fafc;
  border-radius: 10px;
  transition: all 0.2s;
}

.leave-record-item:hover {
  background: #f0f2ff;
}

.leave-record-item >>> .el-tag {
  border-radius: 16px;
  padding: 2px 10px;
  font-size: 11px;
}

.leave-record-text {
  flex: 1;
  font-weight: 500;
}

.leave-record-time {
  font-size: 11px;
  color: #b0b7c9;
}

/* ===== 考勤日历 + 打卡地图 并排 ===== */
.calendar-map-row {
  margin-bottom: 24px;
}

.calendar-map-row .el-col {
  display: flex;
}

.calendar-map-row .el-card {
  flex: 1;
}

.calendar-card {
  border: none;
  border-radius: 24px;
  border: 1px solid rgba(102, 126, 234, 0.08);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.calendar-card:hover {
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.1);
}

.calendar-card >>> .el-card__header {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04), rgba(118, 75, 162, 0.03));
  border-bottom: 1px solid rgba(102, 126, 234, 0.08);
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
  margin-bottom: 10px;
}

.calendar-weekday {
  text-align: center;
  font-size: 13px;
  font-weight: 700;
  color: #667eea;
  padding: 10px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.06), rgba(118, 75, 162, 0.04));
  border-radius: 10px;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
}

.calendar-cell {
  min-height: 80px;
  padding: 10px 8px;
  border-radius: 16px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid transparent;
}

.calendar-cell:hover {
  background: #eef0ff;
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.12);
  border-color: rgba(102, 126, 234, 0.15);
}

.cell-date {
  font-size: 16px;
  font-weight: 700;
  color: #334155;
}

.cell-status {
  font-size: 11px;
  padding: 2px 10px;
  border-radius: 12px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.cell-time {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 500;
}

.cell-empty {
  background: transparent;
  border: none;
}

.cell-empty:hover {
  background: transparent;
  transform: none;
  box-shadow: none;
}

.cell-normal .cell-status {
  background: rgba(103, 194, 58, 0.12);
  color: #67c23a;
}

.cell-late .cell-status {
  background: rgba(230, 162, 60, 0.12);
  color: #e6a23c;
}

.cell-rest {
  background: #f5f5f5;
}

.cell-rest .cell-status {
  background: rgba(144, 147, 153, 0.12);
  color: #909399;
}

.cell-today {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.12), rgba(118, 75, 162, 0.08));
  border: 2px solid #667eea;
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2);
  animation: todayPulse 2.5s ease-in-out infinite alternate;
  position: relative;
}

.cell-today::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  border-radius: 18px;
  background: linear-gradient(135deg, #667eea, #764ba2, #f093fb);
  opacity: 0.15;
  z-index: -1;
  animation: todayGlow 3s ease-in-out infinite alternate;
}

@keyframes todayPulse {
  0% { box-shadow: 0 6px 20px rgba(102, 126, 234, 0.2); }
  100% { box-shadow: 0 8px 28px rgba(102, 126, 234, 0.35); }
}

@keyframes todayGlow {
  0% { opacity: 0.1; }
  100% { opacity: 0.2; }
}

.cell-today .cell-status {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
}

.cell-today .cell-date {
  color: #667eea;
  font-weight: 800;
}

/* ===== 地图卡片 ===== */
.map-card {
  border: none;
  border-radius: 24px;
  border: 1px solid rgba(102, 126, 234, 0.08);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.map-card:hover {
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.1);
}

.map-card >>> .el-card__header {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04), rgba(118, 75, 162, 0.03));
  border-bottom: 1px solid rgba(102, 126, 234, 0.08);
}

/* 月份导航按钮组 */
.month-nav-group {
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.month-nav-group .el-button {
  border-color: rgba(102, 126, 234, 0.2);
  transition: all 0.3s;
  font-weight: 500;
}

.month-nav-group .el-button:hover {
  color: #667eea;
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.06);
}

.month-display {
  min-width: 130px;
  font-weight: 600 !important;
  color: #667eea !important;
}

/* 导出按钮 */
.header-actions .el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
  font-weight: 500;
}

.header-actions .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(102, 126, 234, 0.45);
}

/* 卡片头部按钮组 */
.card-header .el-button-group {
  border-radius: 12px;
  overflow: hidden;
}

.card-header .el-button-group .el-button {
  border-color: rgba(102, 126, 234, 0.2);
  transition: all 0.3s;
}

.card-header .el-button-group .el-button:hover {
  color: #667eea;
  border-color: #667eea;
}

.map-container {
  height: 420px;
  border-radius: 18px;
  overflow: hidden;
  background: linear-gradient(135deg, #eef1fb, #f5f7fe);
  margin: 0 16px;
  border: 1px solid rgba(102, 126, 234, 0.06);
}

.map-legend {
  display: flex;
  gap: 28px;
  justify-content: center;
  padding: 18px 0 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
  transition: all 0.2s;
}

.legend-item:hover {
  color: #334155;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* ===== 打卡记录表格 ===== */
.record-card {
  border: none;
  border-radius: 24px;
  margin-bottom: 24px;
  border: 1px solid rgba(102, 126, 234, 0.08);
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.record-card:hover {
  box-shadow: 0 16px 40px rgba(102, 126, 234, 0.08);
}

.record-card >>> .el-card__header {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04), rgba(118, 75, 162, 0.03));
  border-bottom: 1px solid rgba(102, 126, 234, 0.08);
}

/* 筛选按钮组 */
.filter-radio-group {
  margin-right: 12px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
}

.filter-radio-group >>> .el-radio-button__inner {
  border-color: rgba(102, 126, 234, 0.15);
  transition: all 0.3s;
  font-weight: 500;
  padding: 8px 16px;
}

.filter-radio-group >>> .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.35);
}

.filter-radio-group >>> .el-radio-button__inner:hover {
  color: #667eea;
  border-color: #667eea;
}

.record-card >>> .el-radio-group .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.record-card >>> .el-radio-button__inner {
  border-radius: 10px;
  transition: all 0.3s;
}

.record-card >>> .el-radio-button__inner:hover {
  color: #667eea;
}

.data-table {
  border-radius: 14px;
  overflow: hidden;
  border: 1px solid rgba(102, 126, 234, 0.06);
}

.data-table >>> .el-table__header th {
  background: linear-gradient(135deg, #f5f7fe, #f0f2fc);
  color: #334155;
  font-weight: 600;
  font-size: 13px;
  letter-spacing: 0.3px;
  border-bottom: 2px solid rgba(102, 126, 234, 0.08);
}

.data-table >>> .el-table__body td {
  padding: 14px 0;
  transition: all 0.25s;
}

.data-table >>> .el-table__row {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.data-table >>> .el-table__row:hover > td {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.04), rgba(118, 75, 162, 0.03)) !important;
}

.data-table .el-tag {
  border-radius: 20px;
  padding: 2px 14px;
  font-weight: 600;
  border: none;
  letter-spacing: 0.3px;
}

.data-table .el-tag--dark.el-tag--success {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  box-shadow: 0 3px 10px rgba(103, 194, 58, 0.3);
}

.data-table .el-tag--dark.el-tag--warning {
  background: linear-gradient(135deg, #e6a23c, #f0c78a);
  box-shadow: 0 3px 10px rgba(230, 162, 60, 0.3);
}

.data-table .el-tag--dark.el-tag--danger {
  background: linear-gradient(135deg, #f56c6c, #f89898);
  box-shadow: 0 3px 10px rgba(245, 108, 108, 0.3);
}

.data-table .el-tag--dark.el-tag--info {
  background: linear-gradient(135deg, #909399, #b0b7c9);
  box-shadow: 0 3px 10px rgba(144, 147, 153, 0.3);
}

.late-text {
  color: #e6a23c;
  font-weight: 700;
}

.early-text {
  color: #f56c6c;
  font-weight: 700;
}

.pagination-wrapper {
  margin-top: 20px;
  text-align: right;
  padding: 12px 16px;
  background: linear-gradient(135deg, rgba(245, 247, 254, 0.5), rgba(240, 242, 252, 0.5));
  border-radius: 16px;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.35);
  border-radius: 10px;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li {
  border-radius: 10px;
  transition: all 0.25s;
  font-weight: 500;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:hover {
  color: #667eea;
  transform: translateY(-2px);
}

/* ===== 抽屉样式 ===== */
.rule-drawer >>> .el-drawer__header {
  background: linear-gradient(135deg, #667eea, #764ba2);
  padding: 20px 24px;
  margin: 0;
  border-bottom: none;
}

.rule-drawer >>> .el-drawer__header span {
  color: #fff;
  font-weight: 700;
  font-size: 16px;
  letter-spacing: 1px;
}

.rule-drawer >>> .el-drawer__header .el-drawer__close-btn {
  color: rgba(255, 255, 255, 0.8);
  font-size: 20px;
  transition: all 0.3s;
}

.rule-drawer >>> .el-drawer__header .el-drawer__close-btn:hover {
  color: #fff;
  transform: rotate(90deg);
}

.rule-drawer >>> .el-drawer__body {
  padding: 0;
  background: linear-gradient(180deg, #f8faff, #f0f2fc);
}

.rule-drawer-content {
  padding: 8px 28px 28px;
}

.rule-drawer-content >>> .el-divider {
  margin: 24px 0 18px;
}

.rule-drawer-content >>> .el-divider__text {
  font-weight: 700;
  color: #667eea;
  font-size: 14px;
  background: transparent;
  padding: 0 12px 0 0;
}

.rule-drawer-content >>> .el-divider__text i {
  margin-right: 6px;
  font-size: 15px;
}

.rule-drawer-content >>> .el-form-item {
  margin-bottom: 20px;
}

.rule-drawer-content >>> .el-form-item__label {
  color: #475569;
  font-weight: 500;
  font-size: 13px;
}

.rule-drawer-content >>> .el-input__inner,
.rule-drawer-content >>> .el-input-number {
  border-radius: 12px;
}

.rule-drawer-content >>> .el-input__inner {
  border: 1.5px solid #e2e8f0;
  transition: all 0.3s;
}

.rule-drawer-content >>> .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.12);
}

.rule-drawer-content >>> .el-checkbox {
  margin-right: 10px;
  margin-bottom: 6px;
}

.rule-drawer-content >>> .el-checkbox__label {
  font-size: 13px;
  color: #475569;
}

.rule-drawer-content >>> .el-checkbox__input.is-checked .el-checkbox__inner {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-color: #667eea;
}

.rule-drawer-content >>> .el-checkbox__input.is-checked + .el-checkbox__label {
  color: #667eea;
  font-weight: 600;
}

.rule-drawer-content >>> .el-switch.is-checked .el-switch__core {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-color: #667eea;
}

.drawer-footer {
  text-align: right;
  padding: 24px 0 16px;
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.drawer-footer .el-button {
  padding: 10px 28px;
  font-weight: 600;
  letter-spacing: 1px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.drawer-footer .el-button--default {
  border-radius: 12px;
  border: 1.5px solid #e2e8f0;
  color: #64748b;
}

.drawer-footer .el-button--default:hover {
  border-color: #667eea;
  color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.drawer-footer .el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.35);
}

.drawer-footer .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 28px rgba(102, 126, 234, 0.5);
}

/* ===== 导出弹窗美化 ===== */
.export-dialog {
  border-radius: 24px;
  overflow: hidden;
}

.export-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #667eea, #764ba2);
  padding: 22px 24px;
}

.export-dialog >>> .el-dialog__title {
  color: #fff;
  font-weight: 600;
  letter-spacing: 0.5px;
  font-size: 16px;
}

.export-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: rgba(255, 255, 255, 0.8);
  font-size: 20px;
}

.export-dialog >>> .el-dialog__headerbtn:hover .el-dialog__close {
  color: #fff;
}

.export-dialog >>> .el-dialog__body {
  padding: 24px;
}

.export-dialog >>> .el-dialog__footer {
  padding: 16px 24px 24px;
  border-top: 1px solid rgba(102, 126, 234, 0.08);
}

.export-dialog >>> .el-dialog__footer .el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.export-dialog >>> .el-dialog__footer .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(102, 126, 234, 0.45);
}

.export-preview {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.06), rgba(118, 75, 162, 0.04));
  border-radius: 14px;
  margin-bottom: 20px;
  font-size: 13px;
  color: #64748b;
}

.export-preview i {
  font-size: 20px;
  color: #667eea;
}

.export-form .el-radio-button__inner {
  border-radius: 10px;
  transition: all 0.3s;
}

.export-form .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.export-form .el-input__inner {
  border-radius: 12px;
  border: 1.5px solid #e9eef3;
}

.export-form .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

.attendance-container >>> .el-dialog {
  border-radius: 24px;
  overflow: hidden;
}

.attendance-container >>> .el-dialog__header {
  background: linear-gradient(135deg, #667eea, #764ba2);
  padding: 20px 24px;
}

.attendance-container >>> .el-dialog__title {
  color: #fff;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.attendance-container >>> .el-dialog__headerbtn .el-dialog__close {
  color: rgba(255, 255, 255, 0.8);
}

.attendance-container >>> .el-dialog__headerbtn:hover .el-dialog__close {
  color: #fff;
}

.attendance-container >>> .el-dialog__footer .el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .attendance-container {
    padding: 12px;
  }

  .stat-value {
    font-size: 24px;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
  }

  .stat-icon i {
    font-size: 22px;
  }

  .clock-time {
    font-size: 32px;
    letter-spacing: 2px;
  }

  .clock-icon-ring {
    width: 52px;
    height: 52px;
  }

  .clock-icon-ring i {
    font-size: 24px;
  }

  .map-container {
    height: 280px;
  }

  .monthly-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .overtime-summary {
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
  }

  .summary-value {
    font-size: 22px;
  }

  .calendar-cell {
    min-height: 64px;
    padding: 8px 4px;
  }

  .cell-date {
    font-size: 14px;
  }

  .cell-status {
    font-size: 10px;
    padding: 1px 6px;
  }

  .checkin-btn {
    height: 50px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .calendar-weekday {
    font-size: 11px;
    padding: 6px;
  }

  .cell-time {
    display: none;
  }
}
</style>
