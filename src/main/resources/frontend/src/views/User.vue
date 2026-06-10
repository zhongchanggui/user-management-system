<template>
  <div class="user-container">
    <!-- 搜索筛选区 -->
    <el-card class="search-card">
      <div slot="header" class="card-header">
        <span><i class="el-icon-search"></i> 筛选条件</span>
        <el-button type="text" @click="showAdvanced = !showAdvanced">
          {{ showAdvanced ? '收起' : '展开' }}高级筛选
          <i :class="showAdvanced ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
        </el-button>
      </div>
      
      <el-form :model="searchForm" label-width="80px" size="small">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="用户名">
              <el-input v-model="searchForm.floginname" placeholder="请输入用户名" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="昵称">
              <el-input v-model="searchForm.fnickname" placeholder="请输入昵称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态">
              <el-select v-model="searchForm.fstate" placeholder="全部状态" clearable style="width: 100%">
                <el-option label="禁用" :value="0" />
                <el-option label="正常" :value="1" />
                <el-option label="冻结" :value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="类型">
              <el-select v-model="searchForm.ftype" placeholder="全部类型" clearable style="width: 100%">
                <el-option label="注册" :value="0" />
                <el-option label="添加" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-collapse-transition>
          <div v-show="showAdvanced">
            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="电话号码">
                  <el-input v-model="searchForm.fphonenumber" placeholder="请输入电话号码" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="电子邮箱">
                  <el-input v-model="searchForm.femail" placeholder="请输入电子邮箱" clearable />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="创建人">
                  <el-select v-model="searchForm.fcreateuserid" placeholder="全部创建人" clearable style="width: 100%">
                    <el-option
                      v-for="creator in creatorList"
                      :key="creator.fid"
                      :label="creator.fnickname"
                      :value="creator.fid"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="快速筛选">
                  <el-input v-model="searchForm.fcondition" placeholder="用户名/昵称/电话/邮箱" clearable />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="出生日期">
                  <el-date-picker
                    v-model="birthdayRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd"
                    :picker-options="birthdayPickerOptions"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="创建时间">
                  <el-date-picker
                    v-model="createTimeRange"
                    type="daterange"
                    range-separator="至"
                    start-placeholder="开始日期"
                    end-placeholder="结束日期"
                    value-format="yyyy-MM-dd"
                    :picker-options="createTimePickerOptions"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-collapse-transition>
        
        <el-row>
          <el-col :span="24" style="text-align: right">
            <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
            <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
    
    <!-- 操作按钮区 -->
    <el-card class="table-card">
      <div slot="header" class="card-header">
        <span><i class="el-icon-tickets"></i> 员工列表</span>
        <div class="header-buttons">
          <el-button
            type="primary"
            icon="el-icon-plus"
            size="small"
            @click="handleAdd"
          >
            新增员工
          </el-button>
          <el-button
            type="success"
            icon="el-icon-download"
            size="small"
            @click="handleExport"
          >
            导出数据
          </el-button>
        </div>
      </div>
      
      <!-- 批量操作栏 -->
      <div class="batch-bar" v-if="selectedUsers.length > 0">
        <span class="batch-info">已选择 <strong>{{ selectedUsers.length }}</strong> 项</span>
        <el-button
          v-if="selectedUsers.length === 1"
          type="primary"
          size="small"
          icon="el-icon-edit"
          @click="handleBatchEdit"
        >
          编辑
        </el-button>
        <el-button type="danger" size="small" icon="el-icon-delete" @click="handleBatchDelete">
          批量删除
        </el-button>
        <el-button size="small" icon="el-icon-refresh" @click="clearSelection">取消选择</el-button>
      </div>
      
      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="userList"
        border
        stripe
        style="width: 100%"
        class="data-table"
        @selection-change="handleSelectionChange"
        ref="userTable"
      >
        <el-table-column type="selection" width="45" align="center" />
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column prop="floginname" label="用户名" min-width="100" show-overflow-tooltip />
        <el-table-column prop="fnickname" label="昵称" min-width="100" show-overflow-tooltip />
        <el-table-column prop="fstatename" label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStateType(scope.row.fstate)" effect="dark" size="small">
              {{ getStateText(scope.row.fstate) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fbirthday" label="出生日期" width="110" align="center" />
        <el-table-column prop="fphonenumber" label="电话号码" width="130" />
        <el-table-column prop="femail" label="电子邮箱" min-width="150" show-overflow-tooltip />
        <el-table-column prop="ftypename" label="类型" width="90" align="center">
          <template slot-scope="scope">
            <el-tag type="info" size="small" effect="plain">{{ scope.row.ftypename }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fcreatedatetime" label="创建时间" width="120" align="center">
          <template slot-scope="scope">
            {{ scope.row.fcreatedatetime ? scope.row.fcreatedatetime.substring(0, 10) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="fcreateusername" label="创建人" min-width="90" show-overflow-tooltip />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template slot-scope="scope">
            <div class="action-group">
              <el-button
                type="text"
                size="small"
                class="action-btn action-view"
                @click="handleDetail(scope.row)"
              >
                <i class="el-icon-view"></i> 详情
              </el-button>
              <el-button
                v-if="isAdmin || isCurrentUser(scope.row)"
                type="text"
                size="small"
                class="action-btn action-edit"
                :disabled="isAdmin && scope.row.floginname === 'admin' && !isCurrentUser(scope.row)"
                @click="handleEdit(scope.row)"
              >
                <i class="el-icon-edit"></i> 编辑
              </el-button>
              <el-button
                v-if="isAdmin && !isCurrentUser(scope.row)"
                type="text"
                size="small"
                class="action-btn action-delete"
                :disabled="scope.row.floginname === 'admin'"
                @click="handleDelete(scope.row)"
              >
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pagination.pagenum"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pagination.pagesize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
        />
      </div>
    </el-card>
    
    <!-- 用户详情弹窗 -->
    <el-dialog
      title="用户详情"
      :visible.sync="detailVisible"
      width="500px"
      class="detail-dialog"
      append-to-body
      destroy-on-close
    >
      <el-descriptions :column="1" border v-if="detailUser">
        <el-descriptions-item label="用户ID">{{ detailUser.fid }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailUser.floginname }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ detailUser.fnickname }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStateType(detailUser.fstate)" effect="dark" size="small">
            {{ getStateText(detailUser.fstate) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag type="info" effect="plain" size="small">
            {{ detailUser.ftypename }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ detailUser.fbirthday || '-' }}</el-descriptions-item>
        <el-descriptions-item label="电话号码">{{ detailUser.fphonenumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="电子邮箱">{{ detailUser.femail || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailUser.fcreatedatetime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detailUser.fcreateusername || '系统' }}</el-descriptions-item>
      </el-descriptions>
      <span slot="footer">
        <el-button @click="detailVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
    
    <!-- 新增/编辑弹窗 -->
    <user-form-dialog
      :visible.sync="dialogVisible"
      :type="dialogType"
      :data="currentRow"
      @success="handleSearch"
    />
  </div>
</template>

<script>
import UserFormDialog from '../components/UserFormDialog.vue'

export default {
  name: 'User',
  
  components: {
    UserFormDialog
  },
  
  computed: {
    isAdmin() {
      return this.$store.getters.isAdmin
    }
  },
  data() {
    return {
      loading: false,
      showAdvanced: false,
      userList: [],
      creatorList: [],
      birthdayRange: [],
      createTimeRange: [],
      selectedUsers: [],
      detailVisible: false,
      detailUser: null,
      birthdayPickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      },
      createTimePickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        }
      },
      searchForm: {
        floginname: '',
        fnickname: '',
        fstate: '',
        ftype: '',
        fphonenumber: '',
        femail: '',
        fcreateuserid: '',
        fcondition: ''
      },
      pagination: {
        pagenum: 1,
        pagesize: 10,
        total: 0
      },
      dialogVisible: false,
      dialogType: 'add',
      currentRow: null
    }
  },
  
  mounted() {
    // 从路由参数中获取状态筛选条件
    this.applyRouteParams()
    this.loadUserList()
    this.loadCreatorList()
  },

  watch: {
    // 监听路由参数变化
    '$route.query.state': {
      handler(newVal) {
        if (newVal !== undefined && newVal !== '') {
          this.searchForm.fstate = Number(newVal)
        } else {
          this.searchForm.fstate = ''
        }
        this.loadUserList()
      },
      immediate: false
    }
  },

  methods: {
    // 应用路由参数
    applyRouteParams() {
      const stateParam = this.$route.query.state
      if (stateParam !== undefined && stateParam !== '') {
        this.searchForm.fstate = Number(stateParam)
      }
    },

    // 判断是否是当前登录用户
    isCurrentUser(row) {
      const currentUser = this.$store.getters.currentUser
      return currentUser && row.fid === currentUser.fid
    },

    // 批量编辑（单条）- 保留勾选状态
    handleBatchEdit() {
      if (this.selectedUsers.length !== 1) return
      const row = this.selectedUsers[0]
      this.handleEdit(row)
      // 不取消选择，编辑完成后保留
    },

    // 加载用户列表
    loadUserList() {
      this.loading = true

      const params = {
        ...this.searchForm,
        pagenum: this.pagination.pagenum,
        pagesize: this.pagination.pagesize
      }

      if (this.birthdayRange && this.birthdayRange.length === 2) {
        params.fbirthdaybegin = this.birthdayRange[0]
        params.fbirthdayend = this.birthdayRange[1]
      }

      if (this.createTimeRange && this.createTimeRange.length === 2) {
        // 确保创建时间格式为 yyyy-MM-dd HH:mm:ss
        params.fcreatedatetimebegin = this.formatDateTime(this.createTimeRange[0])
        params.fcreatedatetimeend = this.formatDateTime(this.createTimeRange[1])
      }

      // 保存当前选中的ID列表
      const selectedIds = this.selectedUsers.map(u => u.fid)

      this.$axios.get('/user/query/page', { params })
        .then(res => {
          if (res.success) {
            this.userList = res.data.list
            this.pagination.total = res.data.total

            // 恢复选中状态
            if (selectedIds.length > 0) {
              this.$nextTick(() => {
                this.userList.forEach(row => {
                  if (selectedIds.includes(row.fid)) {
                    this.$refs.userTable.toggleRowSelection(row, true)
                  }
                })
              })
            }
          }
        })
        .finally(() => {
          this.loading = false
        })
    },
    
    // 加载创建人列表
    loadCreatorList() {
      this.$axios.get('/user/creators').then(res => {
        if (res.success) {
          this.creatorList = res.data
        }
      })
    },

    // 格式化日期时间
    formatDateTime(dateStr) {
      if (!dateStr) return ''
      // 如果已经是 yyyy-MM-dd HH:mm:ss 格式，直接返回
      if (dateStr.length === 19) return dateStr
      // 如果是 yyyy-MM-dd 格式，补全时间部分
      if (dateStr.length === 10) return dateStr + ' 00:00:00'
      return dateStr
    },
    
    // 查询
    handleSearch() {
      this.pagination.pagenum = 1
      this.loadUserList()
    },
    
    // 重置
    handleReset() {
      this.searchForm = {
        floginname: '',
        fnickname: '',
        fstate: '',
        ftype: '',
        fphonenumber: '',
        femail: '',
        fcreateuserid: '',
        fcondition: ''
      }
      this.birthdayRange = []
      this.createTimeRange = []
      this.pagination.pagenum = 1
      this.loadUserList()
    },
    
    // 新增
    handleAdd() {
      this.dialogType = 'add'
      this.currentRow = null
      this.dialogVisible = true
    },
    
    // 编辑
    handleEdit(row) {
      if (row.floginname === 'admin' && !this.isCurrentUser(row)) {
        this.$message.warning('系统管理员不允许编辑')
        return
      }
      this.dialogType = 'edit'
      this.currentRow = row
      this.dialogVisible = true
    },
    
    // 详情
    handleDetail(row) {
      this.detailUser = row
      this.detailVisible = true
    },
    
    // 删除
    handleDelete(row) {
      if (row.floginname === 'admin') {
        this.$message.warning('系统管理员不允许删除')
        return
      }
      
      this.$confirm(`确定要删除员工 "${row.fnickname}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$axios.post('/user/delete', { fid: row.fid }).then(res => {
          if (res.success) {
            this.$message.success('删除成功')
            this.loadUserList()
          }
        })
      }).catch(() => {})
    },
    
    // 表格多选变化
    handleSelectionChange(selection) {
      this.selectedUsers = selection
    },
    
    // 清除选择
    clearSelection() {
      this.$refs.userTable.clearSelection()
      this.selectedUsers = []
    },
    
    // 批量删除
    handleBatchDelete() {
      const adminRow = this.selectedUsers.find(u => u.floginname === 'admin')
      if (adminRow) {
        this.$message.warning('批量删除中包含系统管理员，已跳过')
        return
      }
      
      const names = this.selectedUsers.map(u => u.fnickname).join('、')
      this.$confirm(`确定要删除以下员工吗？${names}`, '批量删除确认', {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let successCount = 0
        let total = this.selectedUsers.length
        let completed = 0
        this.selectedUsers.forEach(user => {
          this.$axios.post('/user/delete', { fid: user.fid }).then(res => {
            completed++
            if (res.success) successCount++
            if (completed === total) {
              this.$message.success(`成功删除 ${successCount} 名员工`)
              this.selectedUsers = []
              this.loadUserList()
            }
          }).catch(() => {
            completed++
            if (completed === total) {
              this.$message.success(`成功删除 ${successCount} 名员工`)
              this.selectedUsers = []
              this.loadUserList()
            }
          })
        })
      }).catch(() => {})
    },
    
    // 导出数据
    handleExport() {
      if (this.userList.length === 0) {
        this.$message.warning('没有可导出的数据')
        return
      }
      
      const headers = ['序号', '用户名', '昵称', '状态', '出生日期', '电话号码', '电子邮箱', '类型', '创建时间', '创建人']
      const rows = this.userList.map((user, index) => [
        index + 1,
        user.floginname,
        user.fnickname,
        this.getStateText(user.fstate),
        user.fbirthday || '',
        user.fphonenumber || '',
        user.femail || '',
        user.ftypename || '',
        user.fcreatedatetime || '',
        user.fcreateusername || '系统'
      ])
      
      // 添加BOM头解决中文乱码
      let csvContent = '\uFEFF' + headers.join(',') + '\n'
      rows.forEach(row => {
        csvContent += row.map(cell => `"${cell}"`).join(',') + '\n'
      })
      
      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.setAttribute('href', url)
      link.setAttribute('download', `员工数据_${new Date().toISOString().slice(0,10)}.csv`)
      link.style.visibility = 'hidden'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      this.$message.success('导出成功')
    },
    
    // 分页大小变化
    handleSizeChange(val) {
      this.pagination.pagesize = val
      this.loadUserList()
    },
    
    // 页码变化
    handleCurrentChange(val) {
      this.pagination.pagenum = val
      this.loadUserList()
    },
    
    // 获取状态标签类型
    getStateType(state) {
      switch (state) {
        case 0: return 'danger'
        case 1: return 'success'
        case 2: return 'warning'
        default: return 'info'
      }
    },
    
    // 获取状态文字
    getStateText(state) {
      switch (state) {
        case 0: return '禁用'
        case 1: return '正常'
        case 2: return '冻结'
        default: return '未知'
      }
    }
  }
}
</script>

<style scoped>
.user-container {
  padding: 0;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 24px;
  border: none;
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(102, 126, 234, 0.06);
}

.search-card:hover {
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.1);
  border-color: rgba(102, 126, 234, 0.12);
}

.search-card >>> .el-card__header {
  background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  border-bottom: 1px solid #edf2f7;
  padding: 16px 24px;
}

/* 表格卡片 */
.table-card {
  margin-bottom: 24px;
  border: none;
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(102, 126, 234, 0.06);
}

.table-card:hover {
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.08);
}

.table-card >>> .el-card__header {
  background: linear-gradient(135deg, #fafbfc 0%, #f5f7fa 100%);
  border-bottom: 1px solid #edf2f7;
  padding: 16px 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #1e293b;
}

.card-header span i {
  color: #667eea;
  font-size: 18px;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.header-buttons .el-button {
  border-radius: 12px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
  transition: all 0.3s;
}

.header-buttons .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.35);
}

/* 批量操作栏 */
.batch-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #e8f4fd, #e3f2fd);
  border-radius: 12px;
  border: 1px solid #bbdefb;
}

.batch-info {
  font-size: 14px;
  color: #1565c0;
}

.batch-info strong {
  color: #0d47a1;
  font-size: 16px;
}

/* 搜索表单美化 */
.search-card .el-form-item {
  margin-bottom: 16px;
}

.search-card .el-input__inner,
.search-card .el-select .el-input__inner {
  border-radius: 12px;
  border: 1.5px solid #e9eef3;
  background: #fafcff;
  transition: all 0.25s;
}

.search-card .el-input__inner:focus,
.search-card .el-select .el-input__inner:focus {
  border-color: #8b6ef0;
  box-shadow: 0 0 0 3px rgba(107, 93, 219, 0.15);
}

.search-card .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  padding: 10px 24px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s;
}

.search-card .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.search-card .el-button--default {
  border-radius: 12px;
  padding: 10px 24px;
  border: 1.5px solid #e9eef3;
  transition: all 0.3s;
}

.search-card .el-button--default:hover {
  border-color: #667eea;
  color: #667eea;
}

/* 数据表格美化 */
.data-table {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(102, 126, 234, 0.08);
}

.data-table >>> .el-table__header th {
  background: linear-gradient(135deg, #f5f7fe 0%, #f0f2fc 100%);
  color: #334155;
  font-weight: 600;
  font-size: 13px;
  letter-spacing: 0.3px;
  border-bottom: 2px solid #e8ecf4;
}

.data-table >>> .el-table__body td {
  padding: 14px 0;
  color: #475569;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.data-table >>> .el-table__row {
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.data-table >>> .el-table__row:hover > td {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.06), rgba(118, 75, 162, 0.04)) !important;
  transform: scale(1.002);
}

.data-table >>> .el-table__row--striped > td {
  background: #fafbfc;
}

.data-table >>> .el-table__row--striped:hover > td {
  background: linear-gradient(135deg, #f5f7ff 0%, #f0f2ff 100%) !important;
}

/* 状态标签美化 */
.data-table .el-tag {
  border-radius: 20px;
  padding: 2px 12px;
  font-weight: 500;
  letter-spacing: 0.5px;
  border: none;
}

.data-table .el-tag--dark.el-tag--success {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
}

.data-table .el-tag--dark.el-tag--danger {
  background: linear-gradient(135deg, #f56c6c, #f89898);
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
}

.data-table .el-tag--dark.el-tag--warning {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
  box-shadow: 0 2px 8px rgba(230, 162, 60, 0.3);
}

.data-table .el-tag--info {
  background: #f0f2f8;
  color: #667eea;
  border: 1px solid #e0e4f0;
}

/* 操作按钮美化 */
.action-group {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  flex-wrap: nowrap;
  white-space: nowrap;
}

.action-btn {
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
  margin: 0 2px;
}

.action-btn i {
  margin-right: 3px;
}

.action-view {
  color: #667eea;
}

.action-view:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #4f46e5;
}

.action-edit {
  color: #e6a23c;
}

.action-edit:hover {
  background: rgba(230, 162, 60, 0.1);
  color: #d48806;
}

.action-delete {
  color: #f56c6c;
}

.action-delete:hover {
  background: rgba(245, 108, 108, 0.1);
  color: #e03e3e;
}

.action-btn.is-disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* 详情弹窗美化 */
.detail-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px 24px;
}

.detail-dialog >>> .el-dialog__title {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.detail-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: rgba(255, 255, 255, 0.8);
}

.detail-dialog >>> .el-descriptions-item__label {
  background: #f8fafc;
  font-weight: 500;
  color: #475569;
  width: 100px;
}

/* 分页美化 */
.pagination-wrapper {
  margin-top: 24px;
  text-align: right;
  padding: 12px 16px;
  background: linear-gradient(135deg, rgba(245, 247, 254, 0.5), rgba(240, 242, 252, 0.5));
  border-radius: 16px;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.35);
  border-radius: 10px;
  transform: scale(1.05);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li {
  border-radius: 10px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  font-weight: 500;
}

.pagination-wrapper >>> .el-pagination.is-background .el-pager li:hover {
  color: #667eea;
  background: rgba(102, 126, 234, 0.08);
  transform: translateY(-2px);
}

.pagination-wrapper >>> .el-pagination.is-background .btn-prev,
.pagination-wrapper >>> .el-pagination.is-background .btn-next {
  border-radius: 10px;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
}

.pagination-wrapper >>> .el-pagination.is-background .btn-prev:hover,
.pagination-wrapper >>> .el-pagination.is-background .btn-next:hover {
  color: #667eea;
  transform: translateY(-2px);
}

.pagination-wrapper >>> .el-pagination .el-select .el-input .el-input__inner {
  border-radius: 10px;
}

.pagination-wrapper >>> .el-pagination .el-input__inner {
  border-radius: 10px;
}
</style>