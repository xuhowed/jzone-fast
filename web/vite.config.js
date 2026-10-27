import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler'
      }
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/sys/': {
        target: 'http://localhost:8086',
        changeOrigin: true,
        bypass(req) {
          if (req.headers.accept?.includes('text/html')) {
            return '/index.html'
          }
        }
      },
      '/demo/': {
        target: 'http://localhost:8086',
        changeOrigin: true,
        bypass(req) {
          if (req.headers.accept?.includes('text/html')) {
            return '/index.html'
          }
        }
      }
    }
  },
  build: {
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('@element-plus/icons-vue')) {
              return 'vendor-element-icons'
            }
            if (id.includes('element-plus')) {
              return 'vendor-element'
            }
            if (id.includes('echarts') || id.includes('zrender')) {
              return 'vendor-echarts'
            }
            if (id.includes('vue') || id.includes('pinia') || id.includes('vue-router') || id.includes('@vue')) {
              return 'vendor-vue'
            }
            if (id.includes('axios')) {
              return 'vendor-utils'
            }
          }
        }
      }
    }
  }
})
