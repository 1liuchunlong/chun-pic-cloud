<template>
  <a-modal class="image-out-painting" v-model:visible="visible" title="AI 扩图" :footer="false" :width="'90%'"
    :style="{ maxWidth: '1400px' }" @cancel="closeModal">
    <a-row gutter="24">
      <a-col span="12">
        <div class="image-wrapper">
          <h4 class="image-title">原始图片</h4>
          <div class="image-container">
            <img :src="picture?.url" :alt="picture?.name" class="preview-img" />
          </div>
        </div>
      </a-col>
      <a-col span="12">
        <div class="image-wrapper">
          <h4 class="image-title">扩图结果</h4>
          <div class="image-container">
            <img v-if="resultImageUrl" :src="resultImageUrl" :alt="picture?.name" class="preview-img" />
            <div v-else class="placeholder-image">
              <span>等待生成结果...</span>
            </div>
          </div>
        </div>
      </a-col>
    </a-row>
    <div class="button-container">
      <a-button class="action-button generate-button" type="primary" :loading="!!taskId" ghost @click="createTask">
        生成图片
      </a-button>
      <a-button v-if="resultImageUrl" class="action-button apply-button" type="primary" :loading="uploadLoading"
        @click="handleUpload">
        应用结果
      </a-button>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import {
  createPictureOutPaintingTaskUsingPost,
  getPictureOutPaintingTaskUsingGet,
  uploadPictureByUrlUsingPost,
} from '@/api/pictureController'
import { message } from 'ant-design-vue'

interface Props {
  picture?: API.PictureVO
  spaceId?: number
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

const resultImageUrl = ref<string>('')

// 任务 id
const taskId = ref<string>()

/**
 * 创建任务
 */
const createTask = async () => {
  if (!props.picture?.id) {
    return
  }
  const res = await createPictureOutPaintingTaskUsingPost({
    pictureId: props.picture.id,
    // 根据需要设置扩图参数
    parameters: {
      xScale: 2,
      yScale: 2,
    },
  })
  if (res.data.code === 0 && res.data.data) {
    message.success('创建任务成功，请耐心等待，不要退出界面')
    console.log(res.data.data.output.taskId)
    taskId.value = res.data.data.output.taskId
    // 开启轮询
    startPolling()
  } else {
    message.error('图片任务失败，' + res.data.message)
  }
}

// 轮询定时器
let pollingTimer: NodeJS.Timeout = null

// 开始轮询
const startPolling = () => {
  if (!taskId.value) {
    return
  }

  pollingTimer = setInterval(async () => {
    try {
      const res = await getPictureOutPaintingTaskUsingGet({
        taskId: taskId.value,
      })
      if (res.data.code === 0 && res.data.data) {
        const taskResult = res.data.data.output
        if (taskResult.taskStatus === 'SUCCEEDED') {
          message.success('扩图任务执行成功')
          resultImageUrl.value = taskResult.outputImageUrl
          // 清理轮询
          clearPolling()
        } else if (taskResult.taskStatus === 'FAILED') {
          message.error('扩图任务执行失败')
          // 清理轮询
          clearPolling()
        }
      }
    } catch (error) {
      console.error('扩图任务轮询失败', error)
      message.error('扩图任务轮询失败，' + error.message)
      // 清理轮询
      clearPolling()
    }
  }, 3000) // 每 3 秒轮询一次
}

// 清理轮询
const clearPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
    taskId.value = null
  }
}

// 是否正在上传
const uploadLoading = ref(false)

/**
 * 上传图片
 * @param file
 */
const handleUpload = async () => {
  uploadLoading.value = true
  try {
    const params: API.PictureUploadRequest = {
      fileUrl: resultImageUrl.value,
      spaceId: props.spaceId,
    }
    if (props.picture) {
      params.id = props.picture.id
    }
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('图片上传成功')
      // 将上传成功的图片信息传递给父组件
      props.onSuccess?.(res.data.data)
      // 关闭弹窗
      closeModal()
    } else {
      message.error('图片上传失败，' + res.data.message)
    }
  } catch (error) {
    console.error('图片上传失败', error)
    message.error('图片上传失败，' + error.message)
  }
  uploadLoading.value = false
}

// 是否可见
const visible = ref(false)

// 打开弹窗
const openModal = () => {
  visible.value = true
}

// 关闭弹窗
const closeModal = () => {
  visible.value = false
}

// 暴露函数给父组件
defineExpose({
  openModal,
})
</script>

<style scoped>
.image-out-painting :deep(.ant-modal-content) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.image-out-painting :deep(.ant-modal-header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  padding: 20px 24px;
}

.image-out-painting :deep(.ant-modal-title) {
  color: #ffffff;
  font-size: 18px;
  font-weight: 700;
}

.image-out-painting :deep(.ant-modal-close) {
  color: #ffffff;
}

.image-out-painting :deep(.ant-modal-close:hover) {
  color: rgba(255, 255, 255, 0.8);
}

.image-out-painting :deep(.ant-modal-body) {
  padding: 32px;
  background: #fafafa;
}

.image-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.image-title {
  margin-bottom: 16px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.image-container {
  flex: 1;
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  transition: all 0.3s ease;
}

.image-container:hover {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.preview-img {
  max-width: 100%;
  max-height: 500px;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.preview-img:hover {
  transform: scale(1.02);
}

.placeholder-image {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  width: 100%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 8px;
  border: 2px dashed #d1d5db;
  color: #9ca3af;
  font-size: 14px;
}

.button-container {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.action-button {
  height: 44px;
  padding: 0 32px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 12px;
  transition: all 0.3s ease;
  border: none;
}

.generate-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  color: #ffffff;
}

.generate-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #5568d3 0%, #653d8a 100%);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.generate-button:active:not(:disabled) {
  transform: translateY(0);
}

.apply-button {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  color: #ffffff;
}

.apply-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #059669 0%, #047857 100%);
  box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
  transform: translateY(-2px);
}

.apply-button:active:not(:disabled) {
  transform: translateY(0);
}

/* 响应式 */
@media (max-width: 768px) {
  .image-out-painting :deep(.ant-modal-body) {
    padding: 20px;
  }

  .image-container {
    min-height: 300px;
  }

  .preview-img {
    max-height: 300px;
  }

  .button-container {
    flex-direction: column;
  }

  .action-button {
    width: 100%;
  }
}
</style>
