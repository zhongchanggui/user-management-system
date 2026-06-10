<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在或已被移除</p>
      <div class="actions">
        <el-button type="primary" icon="el-icon-s-home" @click="$router.push('/home')">返回首页</el-button>
        <el-button icon="el-icon-back" @click="$router.go(-1)">返回上页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default { name: 'NotFound' }
</script>

<style scoped>
.not-found-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f0f2f8, #e8ecf4);
}
.not-found-content {
  text-align: center;
  animation: fadeInUp 0.6s ease-out;
}
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}
.error-code {
  font-size: 140px;
  font-weight: 900;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  line-height: 1;
  margin-bottom: 16px;
}
.not-found-content h2 {
  font-size: 28px;
  color: #1e293b;
  margin-bottom: 12px;
}
.not-found-content p {
  font-size: 16px;
  color: #94a3b8;
  margin-bottom: 32px;
}
.actions .el-button--primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(102,126,234,0.3);
}
</style>