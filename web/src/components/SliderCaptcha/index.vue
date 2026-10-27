<template>
  <el-dialog
    v-model="visible"
    title="安全验证"
    width="360px"
    align-center
    append-to-body
    :close-on-click-modal="false"
    class="slider-captcha-dialog"
    @closed="handleClosed"
  >
    <div v-loading="loading" class="slider-captcha-body">
      <div class="captcha-img-wrap" :class="{ 'shake-anim': isError }">
        <img
          v-if="captchaData.backgroundImage"
          :src="captchaData.backgroundImage"
          class="bg-img"
          alt="captcha background"
          draggable="false"
        />
        <img
          v-if="captchaData.sliderImage"
          :src="captchaData.sliderImage"
          class="slider-img"
          alt="captcha piece"
          draggable="false"
          :style="{ transform: `translateX(${sliderX}px)` }"
        />
        <button
          type="button"
          class="refresh-btn"
          title="换一张"
          :disabled="loading"
          @click="fetchCaptcha"
        >
          <el-icon><Refresh /></el-icon>
        </button>
      </div>

      <div ref="trackRef" class="slider-track" :class="{ dragging: isDragging }">
        <div class="slider-track-bar" :style="{ width: `${sliderX + 20}px` }" />
        <span class="slider-track-text">{{ isDragging ? '滑动以对齐缺口' : '按住滑块向右拖动完成拼图' }}</span>
        <div
          class="slider-handle"
          :style="{ transform: `translateX(${sliderX}px)` }"
          @mousedown="onMouseDown"
          @touchstart="onTouchStart"
        >
          <el-icon><DArrowRight /></el-icon>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onUnmounted } from 'vue'
import { Refresh, DArrowRight } from '@element-plus/icons-vue'
import { getSliderCaptcha } from '@/api/system/login'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const loading = ref(false)
const isDragging = ref(false)
const isError = ref(false)
const sliderX = ref(0)
const startClientX = ref(0)
const startSliderX = ref(0)
const trackRef = ref(null)

const maxMove = 320 - 44 // 276px

const captchaData = ref({
  token: '',
  enabled: true,
  backgroundImage: '',
  sliderImage: '',
  y: 0
})

watch(
  () => props.modelValue,
  (val) => {
    visible.value = val
    if (val) {
      sliderX.value = 0
      isError.value = false
      fetchCaptcha()
    }
  }
)

watch(visible, (val) => {
  emit('update:modelValue', val)
})

async function fetchCaptcha() {
  loading.value = true
  sliderX.value = 0
  isError.value = false
  try {
    const data = await getSliderCaptcha()
    if (!data.enabled) {
      // 验证码未开启，直接返回
      emit('success', { captchaToken: null, captchaX: null })
      visible.value = false
      return
    }
    captchaData.value = data
  } catch (err) {
    ElMessage.error('获取验证码失败，请重试')
  } finally {
    loading.value = false
  }
}

function onMouseDown(e) {
  if (loading.value) return
  isDragging.value = true
  startClientX.value = e.clientX
  startSliderX.value = sliderX.value
  window.addEventListener('mousemove', onMouseMove)
  window.addEventListener('mouseup', onMouseUp)
}

function onMouseMove(e) {
  if (!isDragging.value) return
  const delta = e.clientX - startClientX.value
  let nextX = startSliderX.value + delta
  if (nextX < 0) nextX = 0
  if (nextX > maxMove) nextX = maxMove
  sliderX.value = nextX
}

function onMouseUp() {
  if (!isDragging.value) return
  isDragging.value = false
  window.removeEventListener('mousemove', onMouseMove)
  window.removeEventListener('mouseup', onMouseUp)
  handleSubmit()
}

function onTouchStart(e) {
  if (loading.value || !e.touches[0]) return
  isDragging.value = true
  startClientX.value = e.touches[0].clientX
  startSliderX.value = sliderX.value
  window.addEventListener('touchmove', onTouchMove)
  window.addEventListener('touchend', onTouchEnd)
}

function onTouchMove(e) {
  if (!isDragging.value || !e.touches[0]) return
  const delta = e.touches[0].clientX - startClientX.value
  let nextX = startSliderX.value + delta
  if (nextX < 0) nextX = 0
  if (nextX > maxMove) nextX = maxMove
  sliderX.value = nextX
}

function onTouchEnd() {
  if (!isDragging.value) return
  isDragging.value = false
  window.removeEventListener('touchmove', onTouchMove)
  window.removeEventListener('touchend', onTouchEnd)
  handleSubmit()
}

function handleSubmit() {
  emit('success', {
    captchaToken: captchaData.value.token,
    captchaX: Math.round(sliderX.value)
  })
}

function failAndRefresh() {
  isError.value = true
  setTimeout(() => {
    isError.value = false
    fetchCaptcha()
  }, 600)
}

function handleClosed() {
  sliderX.value = 0
  isDragging.value = false
  isError.value = false
}

defineExpose({
  failAndRefresh,
  close: () => {
    visible.value = false
  }
})

onUnmounted(() => {
  window.removeEventListener('mousemove', onMouseMove)
  window.removeEventListener('mouseup', onMouseUp)
  window.removeEventListener('touchmove', onTouchMove)
  window.removeEventListener('touchend', onTouchEnd)
})
</script>

<style scoped lang="scss">
.slider-captcha-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  user-select: none;
}

.captcha-img-wrap {
  width: 320px;
  height: 160px;
  position: relative;
  overflow: hidden;
  border-radius: 6px;
  border: 1px solid var(--el-border-color-lighter);
  background-color: #f1f5f9;

  .bg-img,
  .slider-img {
    width: 320px;
    height: 160px;
    display: block;
    position: absolute;
    top: 0;
    left: 0;
    pointer-events: none;
  }

  .slider-img {
    z-index: 2;
  }

  .refresh-btn {
    position: absolute;
    top: 8px;
    right: 8px;
    z-index: 3;
    width: 28px;
    height: 28px;
    border-radius: 50%;
    border: none;
    background: rgba(0, 0, 0, 0.45);
    color: #fff;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: background-color 0.2s;

    &:hover {
      background: rgba(0, 0, 0, 0.7);
    }
  }
}

.slider-track {
  width: 320px;
  height: 40px;
  background-color: #f1f5f9;
  border: 1px solid var(--el-border-color);
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  .slider-track-bar {
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    background: #e0f2fe;
    pointer-events: none;
  }

  .slider-track-text {
    font-size: 13px;
    color: #64748b;
    pointer-events: none;
    z-index: 1;
  }

  .slider-handle {
    position: absolute;
    left: 0;
    top: 0;
    width: 40px;
    height: 38px;
    background: #ffffff;
    border-radius: 20px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
    border: 1px solid var(--el-border-color);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: grab;
    z-index: 2;
    transition: background-color 0.2s, box-shadow 0.2s;

    &:hover {
      background-color: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
    }

    &:active {
      cursor: grabbing;
      box-shadow: 0 2px 8px rgba(37, 99, 235, 0.3);
    }
  }
}

.shake-anim {
  animation: shake 0.5s ease;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-8px); }
  40%, 80% { transform: translateX(8px); }
}
</style>
