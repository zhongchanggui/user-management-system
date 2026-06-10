<template>
  <div class="dept-container">
    <!-- 页面标题 -->
    <el-card class="page-card" shadow="never">
      <div slot="header" class="card-header">
        <span><i class="el-icon-office-building"></i> 部门管理</span>
        <el-button v-if="isAdmin" type="primary" size="small" icon="el-icon-plus" @click="handleAdd(0)">新增顶级部门</el-button>
      </div>

      <el-table
        :data="deptTree"
        row-key="fid"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        class="data-table"
      >
        <el-table-column prop="fname" label="部门名称" min-width="200">
          <template slot-scope="scope">
            <i class="el-icon-office-building" style="color:#667eea;margin-right:6px"></i>
            <strong>{{ scope.row.fname }}</strong>
          </template>
        </el-table-column>
        <el-table-column prop="fmanager" label="负责人" width="120" align="center" />
        <el-table-column prop="fdescription" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="fstate" label="状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.fstate === 1 ? 'success' : 'danger'" effect="dark" size="small">
              {{ scope.row.fstate === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fsort" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="250" align="center">
          <template slot-scope="scope">
            <div class="action-group">
              <el-button v-if="isAdmin" type="text" size="small" class="action-btn action-add" @click="handleAdd(scope.row.fid)">
                <i class="el-icon-plus"></i> 新增
              </el-button>
              <el-button v-if="isAdmin" type="text" size="small" class="action-btn action-edit" @click="handleEdit(scope.row)">
                <i class="el-icon-edit"></i> 编辑
              </el-button>
              <el-button v-if="isAdmin" type="text" size="small" class="action-btn action-delete" @click="handleDelete(scope.row)">
                <i class="el-icon-delete"></i> 删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" class="form-dialog" append-to-body destroy-on-close>
      <el-form :model="form" :rules="rules" ref="deptForm" label-width="100px">
        <el-form-item label="部门名称" prop="fname">
          <el-input v-model="form.fname" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="上级部门">
          <el-select v-model="form.fparent_id" placeholder="无（顶级部门）" clearable style="width:100%">
            <el-option :value="0" label="无（顶级部门）" />
            <el-option v-for="d in flatDepts" :key="d.fid" :label="d.fname" :value="d.fid" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.fmanager" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.fdescription" type="textarea" :rows="3" placeholder="请输入部门描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.fsort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.fstate">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Department',
  data() {
    return {
      deptList: [],
      dialogVisible: false,
      isEdit: false,
      form: { fid: null, fname: '', fparent_id: 0, fmanager: '', fdescription: '', fsort: 0, fstate: 1 },
      rules: {
        fname: [{ required: true, message: '请输入部门名称', trigger: 'blur' }]
      }
    }
  },
  computed: {
    isAdmin() {
      return this.$store.getters.isAdmin
    },
    deptTree() {
      const map = {}
      const roots = []
      this.deptList.forEach(d => { map[d.fid] = { ...d, children: [] } })
      this.deptList.forEach(d => {
        if (d.fparent_id && map[d.fparent_id]) {
          map[d.fparent_id].children.push(map[d.fid])
        } else {
          roots.push(map[d.fid])
        }
      })
      return roots
    },
    flatDepts() {
      return this.deptList.map(d => ({ fid: d.fid, fname: d.fname }))
    }
  },
  mounted() {
    this.loadDepts()
  },
  methods: {
    loadDepts() {
      this.$axios.get('/department/list', { silent: true }).then(res => {
        if (res.success) this.deptList = res.data
      }).catch(() => {
        this.deptList = [
          {fid:1,fname:'总裁办',fparent_id:0,fmanager:'admin',fdescription:'公司最高管理机构',fstate:1,fsort:1},
          {fid:2,fname:'技术部',fparent_id:0,fmanager:'张三',fdescription:'负责技术研发',fstate:1,fsort:2},
          {fid:3,fname:'市场部',fparent_id:0,fmanager:'王五',fdescription:'负责市场推广',fstate:1,fsort:3},
          {fid:4,fname:'人事部',fparent_id:0,fmanager:'莉莉',fdescription:'负责人力资源',fstate:1,fsort:4},
          {fid:5,fname:'财务部',fparent_id:0,fmanager:'汤姆',fdescription:'负责财务管理',fstate:1,fsort:5},
          {fid:6,fname:'前端组',fparent_id:2,fmanager:'孙七',fdescription:'负责前端开发',fstate:1,fsort:1},
          {fid:7,fname:'后端组',fparent_id:2,fmanager:'周八',fdescription:'负责后端开发',fstate:1,fsort:2},
          {fid:8,fname:'测试组',fparent_id:2,fmanager:'郑十',fdescription:'负责质量测试',fstate:1,fsort:3},
          {fid:9,fname:'销售组',fparent_id:3,fmanager:'露西',fdescription:'负责产品销售',fstate:1,fsort:1},
          {fid:10,fname:'运营组',fparent_id:3,fmanager:'杰克',fdescription:'负责运营推广',fstate:1,fsort:2}
        ]
      })
    },
    handleAdd(parentId) {
      this.isEdit = false
      this.form = { fid: null, fname: '', fparent_id: parentId, fmanager: '', fdescription: '', fsort: 0, fstate: 1 }
      this.$nextTick(() => { this.$refs.deptForm && this.$refs.deptForm.clearValidate() })
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.isEdit = true
      this.form = { ...row }
      this.$nextTick(() => { this.$refs.deptForm && this.$refs.deptForm.clearValidate() })
      this.dialogVisible = true
    },
    handleSubmit() {
      this.$refs.deptForm.validate(valid => {
        if (!valid) return
        const url = this.isEdit ? '/department/edit' : '/department/add'
        this.$axios.post(url, this.form).then(res => {
          if (res.success) {
            this.$message.success(this.isEdit ? '修改成功' : '添加成功')
            this.dialogVisible = false
            this.loadDepts()
          }
        }).catch(() => {
          this.$message.error(this.isEdit ? '修改失败，请重试' : '添加失败，请重试')
        })
      })
    }
  }
}
</script>

<style scoped>
.dept-container { padding: 0; }
.page-card { border: none; border-radius: 20px; overflow: hidden; animation: fadeInUp 0.6s ease-out; }
@keyframes fadeInUp { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }
.page-card >>> .el-card__header { background: linear-gradient(135deg, rgba(102,126,234,0.08), rgba(118,75,162,0.05)); border-bottom: 1px solid #edf2f7; padding: 16px 24px; }
.card-header { display: flex; justify-content: space-between; align-items: center; font-weight: 600; color: #1e293b; }
.card-header i { color: #667eea; font-size: 18px; margin-right: 8px; }
.card-header .el-button { border-radius: 24px; background: linear-gradient(95deg, #6c5ce7, #a463f5); border: none; box-shadow: 0 8px 20px rgba(108,92,231,0.4); transition: all 0.3s; }
.card-header .el-button:hover { transform: translateY(-2px); box-shadow: 0 12px 28px rgba(108,92,231,0.55); }
.data-table { border-radius: 16px; overflow: hidden; }
.data-table >>> .el-table__header th { background: linear-gradient(135deg, rgba(102,126,234,0.06), rgba(118,75,162,0.04)); color: #334155; font-weight: 600; font-size: 13px; border-bottom: 2px solid #e8ecf4; }
.data-table >>> .el-table__row:hover > td { background: linear-gradient(135deg, rgba(102,126,234,0.04), rgba(118,75,162,0.02)) !important; }
.data-table .el-tag { border-radius: 20px; padding: 2px 14px; font-weight: 500; border: none; letter-spacing: 0.5px; }
.data-table .el-tag--dark.el-tag--success { background: linear-gradient(135deg, #67c23a, #85ce61); box-shadow: 0 2px 8px rgba(103,194,58,0.3); }
.data-table .el-tag--dark.el-tag--danger { background: linear-gradient(135deg, #f56c6c, #f89898); box-shadow: 0 2px 8px rgba(245,108,108,0.3); }
.action-group { display: flex; align-items: center; justify-content: center; gap: 4px; flex-wrap: nowrap; white-space: nowrap; }
.action-btn { padding: 4px 10px; border-radius: 8px; font-size: 13px; font-weight: 500; transition: all 0.3s; margin: 0 2px; }
.action-btn i { margin-right: 3px; }
.action-add { color: #667eea; }
.action-add:hover { background: rgba(102, 126, 234, 0.1); color: #4f46e5; }
.action-edit { color: #e6a23c; }
.action-edit:hover { background: rgba(230, 162, 60, 0.1); color: #d48806; }
.action-delete { color: #f56c6c; }
.action-delete:hover { background: rgba(245, 108, 108, 0.1); color: #e03e3e; }
.form-dialog >>> .el-dialog { border-radius: 24px; overflow: hidden; }
.form-dialog >>> .el-dialog__header { background: linear-gradient(145deg, #5f7bef, #7c52b3); padding: 20px 24px; }
.form-dialog >>> .el-dialog__title { color: #fff; font-size: 18px; font-weight: 600; letter-spacing: 0.5px; }
.form-dialog >>> .el-dialog__headerbtn .el-dialog__close { color: rgba(255,255,255,0.8); font-size: 20px; }
.form-dialog .el-input >>> .el-input__inner { border-radius: 18px; border: 1.5px solid #e9eef3; background-color: #fafcff; transition: all 0.25s; }
.form-dialog .el-input >>> .el-input__inner:focus { border-color: #8b6ef0; box-shadow: 0 0 0 3px rgba(107,93,219,0.2); background-color: #fff; }
.form-dialog .el-textarea >>> .el-textarea__inner { border-radius: 18px; border: 1.5px solid #e9eef3; background-color: #fafcff; transition: all 0.25s; }
.form-dialog .el-textarea >>> .el-textarea__inner:focus { border-color: #8b6ef0; box-shadow: 0 0 0 3px rgba(107,93,219,0.2); }
.form-dialog .el-button--primary { background: linear-gradient(95deg, #6c5ce7, #a463f5); border: none; border-radius: 28px; box-shadow: 0 8px 20px rgba(108,92,231,0.4); }
.form-dialog .el-button--primary:hover { transform: translateY(-2px); box-shadow: 0 12px 28px rgba(108,92,231,0.55); }
</style>
