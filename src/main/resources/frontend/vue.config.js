const { defineConfig } = require('@vue/cli-service')
const path = require('path')

const isProd = process.env.NODE_ENV === 'production'

module.exports = defineConfig({
  transpileDependencies: false, // 关闭依赖转译，大幅加速启动
  lintOnSave: false,            // 关闭保存时 lint 检查

  // 开发服务器配置
  devServer: {
    port: 8081,
    hot: true,                   // 启用热更新
    open: false,                 // 不自动打开浏览器（由脚本控制）
    compress: true,              // 启用 gzip 压缩
    client: {
      overlay: {
        errors: true,
        warnings: false,         // 不显示警告 overlay
      },
      progress: true,
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: { '^/api': '/api' }
      }
    }
  },

  // 生产环境打包配置
  outputDir: path.resolve(__dirname, '../../static'),
  assetsDir: '',
  indexPath: path.resolve(__dirname, '../../templates/index.html'),
  publicPath: '/',

  // 配置路径别名
  configureWebpack: {
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src')
      },
      // 减少解析范围，加速模块查找
      extensions: ['.js', '.vue', '.json'],
      symlinks: false,
    },
    cache: {
      type: 'filesystem',  // 启用文件系统缓存，二次构建大幅加速
      buildDependencies: {
        config: [__filename],
      },
    },
  },

  chainWebpack: config => {
    // 开发环境优化
    if (!isProd) {
      // 使用轻量级 source map（大幅加速编译）
      config.devtool('eval-cheap-module-source-map')

      // 移除 prefetch 和 preload 插件（减少网络请求）
      config.plugins.delete('prefetch')
      config.plugins.delete('preload')
    }

    // 生产环境关闭 source map
    if (isProd) {
      config.devtool(false)

      // 生产环境移除 console.log
      config.optimization.minimizer('terser').tap(args => {
        args[0].terserOptions.compress.drop_console = true
        args[0].terserOptions.compress.drop_debugger = true
        return args
      })

      // 代码分割优化
      config.optimization.splitChunks({
        chunks: 'all',
        cacheGroups: {
          vendor: {
            name: 'chunk-vendors',
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: 'initial',
          },
          elementUI: {
            name: 'chunk-elementUI',
            priority: 20,
            test: /[\\/]node_modules[\\/]element-ui[\\/]/,
          },
        },
      })
    }
  },
})
