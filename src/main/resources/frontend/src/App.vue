<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
/* ===== CSS 变量定义 ===== */
:root {
  --primary-color: #667eea;
  --primary-dark: #5a67d8;
  --primary-light: #8b9cf7;
  --secondary-color: #764ba2;
  --accent-gradient: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  --accent-gradient-hover: linear-gradient(135deg, #5a67d8 0%, #6b3fa0 100%);

  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --info-color: #909399;

  --text-primary: #1e293b;
  --text-regular: #475569;
  --text-secondary: #64748b;
  --text-placeholder: #94a3b8;
  --text-disabled: #c0c4cc;

  --bg-page: #f0f2f8;
  --bg-card: #ffffff;
  --bg-hover: #f5f7ff;
  --bg-sidebar: linear-gradient(195deg, #111827 0%, #1a2235 40%, #1e293b 100%);

  --border-light: #edf2f7;
  --border-lighter: #e9eef3;
  --border-extra-light: #f0f2f5;

  --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.04);
  --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 12px 32px rgba(0, 0, 0, 0.12);
  --shadow-xl: 0 25px 50px rgba(0, 0, 0, 0.15);
  --shadow-primary: 0 8px 24px rgba(102, 126, 234, 0.25);

  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;
  --radius-2xl: 24px;

  --font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
  --transition-fast: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-normal: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  --transition-slow: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

#app {
  height: 100%;
  font-family: var(--font-family);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: var(--text-primary);
}

/* ===== 全局滚动条美化 ===== */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(180deg, rgba(102, 126, 234, 0.3), rgba(118, 75, 162, 0.3));
  border-radius: 3px;
  transition: background 0.3s;
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(180deg, rgba(102, 126, 234, 0.5), rgba(118, 75, 162, 0.5));
}

::-webkit-scrollbar-corner {
  background: transparent;
}

/* ===== 全局选中文本颜色 ===== */
::selection {
  background: rgba(102, 126, 234, 0.2);
  color: var(--text-primary);
}

/* ===== 全局 Element UI 样式增强 ===== */

/* 消息提示美化 - 浅色柔和风格 */
.el-message {
  border-radius: var(--radius-lg) !important;
  border: none !important;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12), 0 0 0 1px rgba(0,0,0,0.04) !important;
  padding: 16px 24px !important;
  backdrop-filter: blur(20px);
  min-width: 300px !important;
  background: #ffffff !important;
}

.el-message .el-message__content {
  color: #1e293b !important;
  font-weight: 600 !important;
  font-size: 14px !important;
}

.el-message .el-message__closeBtn {
  color: #94a3b8 !important;
}

.el-message .el-message__closeBtn:hover {
  color: #475569 !important;
}

/* 成功提示 - 浅绿背景深绿文字 */
.el-message--success {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%) !important;
  border-left: 4px solid #22c55e !important;
  box-shadow: 0 12px 32px rgba(34, 197, 94, 0.15), 0 0 0 1px rgba(34,197,94,0.1) !important;
}

.el-message--success .el-message__content {
  color: #15803d !important;
}

.el-message--success .el-message__icon {
  color: #22c55e !important;
}

/* 错误提示 - 浅红背景深红文字 */
.el-message--error {
  background: linear-gradient(135deg, #fef2f2 0%, #fff1f2 100%) !important;
  border-left: 4px solid #ef4444 !important;
  box-shadow: 0 12px 32px rgba(239, 68, 68, 0.15), 0 0 0 1px rgba(239,68,68,0.1) !important;
}

.el-message--error .el-message__content {
  color: #b91c1c !important;
}

.el-message--error .el-message__icon {
  color: #ef4444 !important;
}

/* 警告提示 - 浅橙背景深橙文字 */
.el-message--warning {
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%) !important;
  border-left: 4px solid #f59e0b !important;
  box-shadow: 0 12px 32px rgba(245, 158, 11, 0.15), 0 0 0 1px rgba(245,158,11,0.1) !important;
}

.el-message--warning .el-message__content {
  color: #92400e !important;
}

.el-message--warning .el-message__icon {
  color: #f59e0b !important;
}

/* 信息提示 - 浅蓝背景深蓝文字 */
.el-message--info {
  background: linear-gradient(135deg, #eff6ff 0%, #eef2ff 100%) !important;
  border-left: 4px solid #3b82f6 !important;
  box-shadow: 0 12px 32px rgba(59, 130, 246, 0.15), 0 0 0 1px rgba(59,130,246,0.1) !important;
}

.el-message--info .el-message__content {
  color: #1e40af !important;
}

.el-message--info .el-message__icon {
  color: #3b82f6 !important;
}

/* 确认弹窗美化 - 浅色柔和风格 */
.el-message-box {
  border-radius: var(--radius-2xl) !important;
  border: none !important;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(0,0,0,0.04) !important;
  overflow: hidden;
  background: #ffffff !important;
}

.el-message-box__header {
  padding: 24px 24px 12px !important;
  background: linear-gradient(135deg, #faf5ff 0%, #f5f3ff 100%) !important;
  border-bottom: 1px solid #f0edf5 !important;
}

.el-message-box__title {
  font-weight: 700 !important;
  color: #1e293b !important;
  font-size: 17px !important;
}

.el-message-box__content {
  padding: 16px 24px 20px !important;
  color: #475569 !important;
  font-size: 14px !important;
}

.el-message-box__message {
  color: #475569 !important;
}

.el-message-box__btns {
  padding: 12px 24px 24px !important;
}

.el-message-box__btns .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 12px !important;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3) !important;
}

.el-message-box__btns .el-button--default {
  background: #f8f9fc !important;
  border: 1.5px solid #e9eef3 !important;
  border-radius: 12px !important;
  color: #606266 !important;
}

/* 通知美化 */
.el-notification {
  border-radius: var(--radius-lg) !important;
  border: none !important;
  box-shadow: var(--shadow-xl) !important;
  overflow: hidden;
}

/* 全局修复：Element UI 弹窗遮罩层问题 */
.el-message-box__wrapper {
  position: fixed !important;
  top: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  left: 0 !important;
  z-index: 2001 !important;
}

.v-modal {
  position: fixed !important;
  top: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  left: 0 !important;
  z-index: 2000 !important;
}

/* 确保弹窗在遮罩层之上 */
.el-dialog__wrapper {
  position: fixed !important;
  top: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  left: 0 !important;
  overflow: auto !important;
  z-index: 2001 !important;
}

/* ===== 全局弹窗样式修复（确保颜色反差和内容可读） ===== */

/* 弹窗遮罩层 */
.el-dialog__mask {
  background-color: rgba(0, 0, 0, 0.5) !important;
  backdrop-filter: blur(4px);
}

/* 弹窗主体 */
.el-dialog {
  border-radius: 20px !important;
  overflow: hidden !important;
  background: #ffffff !important;
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.3) !important;
  border: 1px solid rgba(255, 255, 255, 0.8) !important;
}

/* 弹窗头部 - 紫色渐变背景 */
.el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  padding: 20px 24px !important;
  border-bottom: none !important;
}

.el-dialog__title {
  color: #ffffff !important;
  font-size: 18px !important;
  font-weight: 600 !important;
  letter-spacing: 0.5px !important;
}

.el-dialog__headerbtn .el-dialog__close {
  color: rgba(255, 255, 255, 0.8) !important;
  font-size: 20px !important;
}

.el-dialog__headerbtn:hover .el-dialog__close {
  color: #ffffff !important;
}

/* 弹窗内容区 - 白色背景确保可读 */
.el-dialog__body {
  background: #ffffff !important;
  color: #303133 !important;
  padding: 24px !important;
  font-size: 14px !important;
  line-height: 1.6 !important;
}

/* 弹窗底部 */
.el-dialog__footer {
  background: #f8f9fc !important;
  padding: 16px 24px !important;
  border-top: 1px solid #edf2f7 !important;
}

/* 弹窗内表单标签 */
.el-dialog .el-form-item__label {
  color: #475569 !important;
  font-weight: 500 !important;
}

/* 弹窗内输入框 */
.el-dialog .el-input__inner {
  background-color: #fafcff !important;
  border: 1.5px solid #e9eef3 !important;
  border-radius: 12px !important;
  color: #303133 !important;
  transition: all 0.25s !important;
}

.el-dialog .el-input__inner:focus {
  border-color: #8b6ef0 !important;
  box-shadow: 0 0 0 3px rgba(107, 93, 219, 0.15) !important;
  background-color: #ffffff !important;
}

.el-dialog .el-textarea__inner {
  background-color: #fafcff !important;
  border: 1.5px solid #e9eef3 !important;
  border-radius: 12px !important;
  color: #303133 !important;
}

.el-dialog .el-textarea__inner:focus {
  border-color: #8b6ef0 !important;
  box-shadow: 0 0 0 3px rgba(107, 93, 219, 0.15) !important;
}

/* 弹窗内下拉选择 */
.el-dialog .el-select .el-input__inner {
  background-color: #fafcff !important;
  border: 1.5px solid #e9eef3 !important;
  border-radius: 12px !important;
  color: #303133 !important;
}

/* 弹窗内日期选择器 */
.el-dialog .el-date-editor .el-input__inner {
  background-color: #fafcff !important;
  border: 1.5px solid #e9eef3 !important;
  border-radius: 12px !important;
}

/* 弹窗内单选框 */
.el-dialog .el-radio__label {
  color: #475569 !important;
}

/* ===== 全局按钮样式修复 ===== */

/* 主要按钮 - 紫色渐变 */
.el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  color: #ffffff !important;
  font-weight: 500 !important;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3) !important;
  transition: all 0.3s !important;
}

.el-button--primary:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 24px rgba(102, 126, 234, 0.4) !important;
  filter: brightness(1.05) !important;
}

.el-button--primary:active {
  transform: translateY(0) !important;
}

/* 成功按钮 */
.el-button--success {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%) !important;
  border: none !important;
  color: #ffffff !important;
}

/* 警告按钮 */
.el-button--warning {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%) !important;
  border: none !important;
  color: #ffffff !important;
}

/* 危险按钮 */
.el-button--danger {
  background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%) !important;
  border: none !important;
  color: #ffffff !important;
}

/* 信息按钮 */
.el-button--info {
  background: linear-gradient(135deg, #909399 0%, #b5b9c1 100%) !important;
  border: none !important;
  color: #ffffff !important;
}

/* 默认按钮 */
.el-button--default {
  background: #ffffff !important;
  border: 1.5px solid #e9eef3 !important;
  color: #606266 !important;
}

.el-button--default:hover {
  border-color: #8b6ef0 !important;
  color: #667eea !important;
  background: rgba(102, 126, 234, 0.05) !important;
}

/* 文字按钮 */
.el-button--text {
  color: #667eea !important;
  background: transparent !important;
  border: none !important;
}

.el-button--text:hover {
  color: #5a4ad1 !important;
  background: rgba(102, 126, 234, 0.08) !important;
}

/* 圆角按钮通用 */
.el-button {
  border-radius: 12px !important;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;
}

/* ===== 全局表格样式增强 ===== */
.el-table {
  border-radius: 12px !important;
  overflow: hidden !important;
}

.el-table th {
  background: linear-gradient(135deg, #f5f7fe 0%, #f0f2fc 100%) !important;
  color: #334155 !important;
  font-weight: 600 !important;
}

.el-table--striped .el-table__body tr.el-table__row--striped td {
  background: #fafbfc !important;
}

/* ===== 全局标签样式 ===== */
.el-tag {
  border-radius: 20px !important;
  padding: 2px 12px !important;
  font-weight: 500 !important;
}

/* ===== 全局分页样式 ===== */
.el-pagination.is-background .el-pager li:not(.disabled).active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3) !important;
}

/* ===== 全局卡片样式 ===== */
.el-card {
  border: none !important;
  border-radius: 20px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06) !important;
}

/* ===== 全局过渡动画 ===== */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

/* ===== 全局工具类 ===== */
.text-gradient {
  background: var(--accent-gradient);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.glass-effect {
  background: rgba(255, 255, 255, 0.75);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.6);
}
</style>
