<template>
  <div class="system-container">
    <el-tabs v-model="activeTab" class="system-tabs">
      <!-- 角色管理 -->
      <el-tab-pane label="角色管理" name="roles">
        <el-card class="tab-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-user-solid"></i> 角色管理</span>
            <el-button type="primary" icon="el-icon-plus" size="small" @click="handleAddRole">新增角色</el-button>
          </div>
          <el-table :data="roles" border stripe class="data-table">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="name" label="角色名称" width="150" />
            <el-table-column prop="code" label="角色标识" width="150" />
            <el-table-column prop="description" label="描述" min-width="200" />
            <el-table-column prop="userCount" label="用户数" width="80" align="center" />
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === '启用' ? 'success' : 'danger'" effect="dark" size="small">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleEditRole(scope.row)">编辑</el-button>
                <el-button type="text" size="small" @click="handlePermission(scope.row)">权限</el-button>
                <el-button type="text" size="small" style="color: #f56c6c;" @click="handleDeleteRole(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 权限管理 -->
      <el-tab-pane label="权限管理" name="permissions">
        <el-card class="tab-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-lock"></i> 权限管理</span>
          </div>
          <el-tree
            :data="permissionTree"
            show-checkbox
            node-key="id"
            default-expand-all
            :props="{ children: 'children', label: 'label' }"
            class="permission-tree"
          />
        </el-card>
      </el-tab-pane>

      <!-- 操作日志 -->
      <el-tab-pane label="操作日志" name="logs">
        <el-card class="tab-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-document"></i> 操作日志</span>
            <div class="header-actions">
              <el-date-picker v-model="logDateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" size="small" />
              <el-input v-model="logSearch" placeholder="搜索操作内容" prefix-icon="el-icon-search" size="small" clearable style="width: 200px;" />
              <el-button type="primary" icon="el-icon-search" size="small">查询</el-button>
            </div>
          </div>
          <el-table :data="logs" border stripe class="data-table">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="operator" label="操作人" width="100" />
            <el-table-column prop="module" label="模块" width="100" align="center" />
            <el-table-column prop="action" label="操作" width="100" align="center">
              <template slot-scope="scope">
                <el-tag size="small" :type="getLogType(scope.row.action)">{{ scope.row.action }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="detail" label="操作详情" min-width="250" show-overflow-tooltip />
            <el-table-column prop="ip" label="IP地址" width="130" />
            <el-table-column prop="time" label="操作时间" width="160" align="center" />
            <el-table-column prop="result" label="结果" width="80" align="center">
              <template slot-scope="scope">
                <el-tag :type="scope.row.result === '成功' ? 'success' : 'danger'" effect="dark" size="small">{{ scope.row.result }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrapper">
            <el-pagination background :current-page="1" :page-sizes="[10, 20, 50]" :page-size="10" layout="total, sizes, prev, pager, next" :total="logs.length" />
          </div>
        </el-card>
      </el-tab-pane>

      <!-- 数据字典 -->
      <el-tab-pane label="数据字典" name="dict">
        <el-card class="tab-card" shadow="hover">
          <div slot="header" class="card-header">
            <span><i class="el-icon-collection"></i> 数据字典</span>
            <el-button type="primary" icon="el-icon-plus" size="small">新增字典</el-button>
          </div>
          <el-table :data="dictList" border stripe class="data-table">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="name" label="字典名称" width="150" />
            <el-table-column prop="code" label="字典编码" width="150" />
            <el-table-column prop="description" label="描述" min-width="200" />
            <el-table-column prop="itemCount" label="项数" width="80" align="center" />
            <el-table-column label="操作" width="150" align="center">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handleDictItems(scope.row)">字典项</el-button>
                <el-button type="text" size="small">编辑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
export default {
  name: 'System',
  data() {
    return {
      activeTab: 'roles',
      logDateRange: [],
      logSearch: '',
      roles: [
        { name: '超级管理员', code: 'SUPER_ADMIN', description: '拥有系统所有权限', userCount: 1, status: '启用' },
        { name: '系统管理员', code: 'ADMIN', description: '管理系统用户和配置', userCount: 3, status: '启用' },
        { name: '部门主管', code: 'DEPT_MANAGER', description: '管理部门员工和审批', userCount: 8, status: '启用' },
        { name: '普通员工', code: 'EMPLOYEE', description: '基础操作权限', userCount: 45, status: '启用' },
        { name: 'HR专员', code: 'HR', description: '人事管理和薪资管理', userCount: 5, status: '启用' },
        { name: '访客', code: 'GUEST', description: '只读权限', userCount: 0, status: '禁用' }
      ],
      permissionTree: [
        { id: 1, label: '系统管理', children: [
          { id: 11, label: '用户管理', children: [{ id: 111, label: '查看' }, { id: 112, label: '新增' }, { id: 113, label: '编辑' }, { id: 114, label: '删除' }] },
          { id: 12, label: '角色管理', children: [{ id: 121, label: '查看' }, { id: 122, label: '新增' }, { id: 123, label: '编辑' }, { id: 124, label: '删除' }] },
          { id: 13, label: '权限管理', children: [{ id: 131, label: '查看' }, { id: 132, label: '配置' }] }
        ]},
        { id: 2, label: '员工管理', children: [
          { id: 21, label: '员工信息', children: [{ id: 211, label: '查看' }, { id: 212, label: '新增' }, { id: 213, label: '编辑' }, { id: 214, label: '删除' }, { id: 215, label: '导出' }] },
          { id: 22, label: '部门管理', children: [{ id: 221, label: '查看' }, { id: 222, label: '编辑' }] }
        ]},
        { id: 3, label: '考勤管理', children: [
          { id: 31, label: '考勤记录', children: [{ id: 311, label: '查看' }, { id: 312, label: '导出' }] },
          { id: 32, label: '考勤规则', children: [{ id: 321, label: '查看' }, { id: 322, label: '编辑' }] }
        ]},
        { id: 4, label: '薪资管理', children: [
          { id: 41, label: '工资条', children: [{ id: 411, label: '查看' }, { id: 412, label: '编辑' }, { id: 413, label: '导出' }] }
        ]},
        { id: 5, label: '公告管理', children: [
          { id: 51, label: '公告', children: [{ id: 511, label: '查看' }, { id: 512, label: '发布' }, { id: 513, label: '编辑' }, { id: 514, label: '删除' }] }
        ]}
      ],
      logs: [
        { operator: 'admin', module: '用户管理', action: '新增', detail: '新增员工：王小明', ip: '192.168.1.100', time: '2025-06-03 08:15:00', result: '成功' },
        { operator: 'admin', module: '用户管理', action: '编辑', detail: '修改员工：李华 - 更新手机号', ip: '192.168.1.100', time: '2025-06-03 08:20:00', result: '成功' },
        { operator: 'admin', module: '用户管理', action: '删除', detail: '删除员工：张三', ip: '192.168.1.100', time: '2025-06-03 09:00:00', result: '成功' },
        { operator: 'user01', module: '系统', action: '登录', detail: '用户登录系统', ip: '192.168.1.105', time: '2025-06-03 09:30:00', result: '成功' },
        { operator: 'user02', module: '系统', action: '登录', detail: '密码错误登录失败', ip: '192.168.1.106', time: '2025-06-03 09:35:00', result: '失败' },
        { operator: 'admin', module: '公告管理', action: '发布', detail: '发布公告：系统升级通知', ip: '192.168.1.100', time: '2025-06-02 14:00:00', result: '成功' }
      ],
      dictList: [
        { name: '用户状态', code: 'user_state', description: '用户账号状态', itemCount: 3 },
        { name: '用户类型', code: 'user_type', description: '用户角色类型', itemCount: 3 },
        { name: '请假类型', code: 'leave_type', description: '请假申请类型', itemCount: 7 },
        { name: '审批状态', code: 'approval_status', description: '审批流程状态', itemCount: 4 },
        { name: '考勤状态', code: 'attendance_status', description: '考勤打卡状态', itemCount: 5 }
      ]
    }
  },
  methods: {
    handleAddRole() { this.$message.info('新增角色功能') },
    handleEditRole(row) { this.$message.info(`编辑角色：${row.name}`) },
    handlePermission(row) { this.$message.info(`配置权限：${row.name}`) },
    handleDeleteRole(row) {
      this.$confirm(`确定删除角色"${row.name}"吗？`, '提示', { type: 'warning' }).then(() => {
        this.$message.success('删除成功')
      }).catch(() => {})
    },
    getLogType(action) {
      const map = { '新增': 'success', '编辑': '', '删除': 'danger', '登录': 'info', '发布': 'warning' }
      return map[action] || 'info'
    },
    handleDictItems(row) { this.$message.info(`查看字典项：${row.name}`) }
  }
}
</script>

<style scoped>
.system-container { padding: 0; }

.system-tabs >>> .el-tabs__header { margin-bottom: 0; }
.system-tabs >>> .el-tabs__nav-wrap::after { display: none; }
.system-tabs >>> .el-tabs__item { height: 50px; line-height: 50px; font-size: 15px; font-weight: 600; }
.system-tabs >>> .el-tabs__item.is-active { color: #667eea; }
.system-tabs >>> .el-tabs__active-bar { background: linear-gradient(135deg, #667eea, #764ba2); height: 3px; border-radius: 3px; }

.tab-card { border: none; border-radius: 0 0 20px 20px; margin-bottom: 24px; }
.card-header { display: flex; align-items: center; justify-content: space-between; font-weight: 600; color: #1e293b; }
.card-header i { color: #667eea; margin-right: 8px; font-size: 18px; }
.header-actions { display: flex; gap: 10px; align-items: center; }

.data-table { border-radius: 12px; overflow: hidden; }
.data-table >>> .el-table__header th { background: linear-gradient(135deg, #f5f7fe, #f0f2fc); color: #334155; font-weight: 600; font-size: 13px; }
.data-table >>> .el-table__row:hover > td { background: linear-gradient(135deg, #f5f7ff, #f0f2ff) !important; }
.data-table .el-tag { border-radius: 20px; padding: 2px 12px; font-weight: 500; border: none; }

.permission-tree { padding: 16px; }
.permission-tree >>> .el-tree-node__content { height: 36px; border-radius: 8px; margin: 2px 0; }
.permission-tree >>> .el-tree-node__content:hover { background: #f0f2ff; }

.pagination-wrapper { margin-top: 20px; text-align: right; }
.pagination-wrapper >>> .el-pagination.is-background .el-pager li:not(.disabled).active { background: linear-gradient(135deg, #667eea, #764ba2); border-radius: 8px; }
</style>