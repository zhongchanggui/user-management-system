<template>
  <div class="verify-code-container" @click="refreshCode" :title="'点击刷新验证码'">
    <canvas ref="verifyCanvas" :width="width" :height="height"></canvas>
    <div class="refresh-overlay">
      <i class="el-icon-refresh"></i>
    </div>
  </div>
</template>

<script>
export default {
  name: 'VerifyCode',

  props: {
    width: {
      type: Number,
      default: 130
    },
    height: {
      type: Number,
      default: 46
    },
    length: {
      type: Number,
      default: 4
    }
  },

  data() {
    return {
      code: ''
    }
  },

  mounted() {
    this.drawCode()
  },

  methods: {
    // 生成主题色系随机颜色
    randomColor(min, max) {
      const r = Math.floor(Math.random() * (max - min) + min)
      const g = Math.floor(Math.random() * (max - min) + min)
      const b = Math.floor(Math.random() * (max - min) + min)
      return `rgb(${r},${g},${b})`
    },

    // 生成主题色（紫蓝系）
    themeColor(opacity) {
      const colors = [
        [102, 126, 234],
        [118, 75, 162],
        [86, 115, 235],
        [142, 104, 255],
        [99, 102, 241]
      ]
      const c = colors[Math.floor(Math.random() * colors.length)]
      return `rgba(${c[0]},${c[1]},${c[2]},${opacity})`
    },

    // 生成随机验证码
    generateCode() {
      const chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhkmnpqrstwxyz23456789'
      let code = ''
      for (let i = 0; i < this.length; i++) {
        code += chars.charAt(Math.floor(Math.random() * chars.length))
      }
      return code
    },

    // 绘制验证码
    drawCode() {
      const canvas = this.$refs.verifyCanvas
      if (!canvas) return
      const ctx = canvas.getContext('2d')

      // 生成验证码
      this.code = this.generateCode()

      // 绘制渐变背景
      const bgGradient = ctx.createLinearGradient(0, 0, this.width, this.height)
      bgGradient.addColorStop(0, '#f0f2ff')
      bgGradient.addColorStop(0.5, '#f5f3ff')
      bgGradient.addColorStop(1, '#eef2ff')
      ctx.fillStyle = bgGradient
      ctx.fillRect(0, 0, this.width, this.height)

      // 绘制细密网格背景
      ctx.strokeStyle = 'rgba(102, 126, 234, 0.06)'
      ctx.lineWidth = 0.5
      for (let x = 0; x < this.width; x += 8) {
        ctx.beginPath()
        ctx.moveTo(x, 0)
        ctx.lineTo(x, this.height)
        ctx.stroke()
      }
      for (let y = 0; y < this.height; y += 8) {
        ctx.beginPath()
        ctx.moveTo(0, y)
        ctx.lineTo(this.width, y)
        ctx.stroke()
      }

      // 绘制贝塞尔曲线干扰线（3条）
      for (let i = 0; i < 3; i++) {
        ctx.strokeStyle = this.themeColor(0.3 + Math.random() * 0.2)
        ctx.lineWidth = 1 + Math.random()
        ctx.beginPath()
        ctx.moveTo(Math.random() * this.width * 0.2, Math.random() * this.height)
        ctx.bezierCurveTo(
          Math.random() * this.width * 0.4, Math.random() * this.height,
          Math.random() * this.width * 0.6, Math.random() * this.height,
          this.width * (0.8 + Math.random() * 0.2), Math.random() * this.height
        )
        ctx.stroke()
      }

      // 绘制干扰噪点（40个）
      for (let i = 0; i < 40; i++) {
        ctx.fillStyle = this.themeColor(0.15 + Math.random() * 0.25)
        ctx.beginPath()
        ctx.arc(
          Math.random() * this.width,
          Math.random() * this.height,
          0.8 + Math.random() * 1.5,
          0,
          2 * Math.PI
        )
        ctx.fill()
      }

      // 绘制验证码文字
      const fontSize = Math.floor(this.height * 0.6)
      ctx.font = `bold ${fontSize}px 'Courier New', Consolas, monospace`
      ctx.textBaseline = 'middle'

      const textWidth = this.width / (this.length + 1)
      for (let i = 0; i < this.length; i++) {
        const x = textWidth * (i + 0.8)
        const y = this.height / 2 + (Math.random() - 0.5) * 4

        // 随机旋转角度
        const angle = (Math.random() - 0.5) * 0.35

        ctx.save()
        ctx.translate(x, y)
        ctx.rotate(angle)

        // 文字阴影
        ctx.shadowColor = this.themeColor(0.2)
        ctx.shadowBlur = 3
        ctx.shadowOffsetX = 1
        ctx.shadowOffsetY = 1

        // 文字颜色（主题色系深色）
        ctx.fillStyle = this.themeColor(0.7 + Math.random() * 0.3)
        ctx.fillText(this.code[i], -fontSize / 3, 0)
        ctx.restore()
      }
    },

    // 刷新验证码
    refreshCode() {
      this.drawCode()
      this.$emit('refresh')
    },

    // 验证输入的验证码是否正确
    validate(inputCode) {
      return inputCode.toLowerCase() === this.code.toLowerCase()
    }
  }
}
</script>

<style scoped>
.verify-code-container {
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid rgba(102, 126, 234, 0.15);
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.08);
}

.verify-code-container:hover {
  border-color: rgba(102, 126, 234, 0.4);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.18);
  transform: scale(1.03);
}

.verify-code-container:active {
  transform: scale(0.97);
}

.verify-code-container canvas {
  display: block;
  width: 100%;
  height: 100%;
}

/* 悬浮刷新图标 */
.refresh-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(102, 126, 234, 0.08);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.verify-code-container:hover .refresh-overlay {
  opacity: 1;
}

.refresh-overlay i {
  font-size: 22px;
  color: rgba(102, 126, 234, 0.7);
  animation: spin 0.6s ease-in-out;
  animation-play-state: paused;
}

.verify-code-container:hover .refresh-overlay i {
  animation-play-state: running;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
