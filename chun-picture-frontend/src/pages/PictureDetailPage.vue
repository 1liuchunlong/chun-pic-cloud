<template>
  <div id="pictureDetailPage">
    <a-spin :spinning="loading">
      <a-row :gutter="[24, 24]" align="stretch">
        <!-- 图片预览 -->
        <a-col :sm="24" :md="14" :xl="15">
          <a-card class="preview-card" title="图片预览">
            <template #title>
              <span class="card-title">图片预览</span>
            </template>
            <div class="image-container">
              <a-image v-if="picture.url" :src="picture.url" class="preview-image" :preview="true" />
              <a-empty v-else description="暂无图片" />
            </div>
          </a-card>
        </a-col>
        <!-- 图片信息区域 -->
        <a-col :sm="24" :md="10" :xl="9">
          <a-card class="info-card" title="图片信息">
            <template #title>
              <span class="card-title">图片信息</span>
            </template>
            <a-descriptions :column="1" class="picture-descriptions">
              <a-descriptions-item label="作者" class="desc-item">
                <a-space class="author-info">
                  <a-avatar :size="32" :src="picture.user?.userAvatar" class="author-avatar" />
                  <span class="author-name">{{ picture.user?.userName }}</span>
                </a-space>
              </a-descriptions-item>
              <a-descriptions-item label="名称" class="desc-item">
                <span class="info-value">{{ picture.name ?? '未命名' }}</span>
              </a-descriptions-item>
              <a-descriptions-item label="简介" class="desc-item">
                <span class="info-value">{{ picture.introduction ?? '-' }}</span>
              </a-descriptions-item>
              <a-descriptions-item label="分类" class="desc-item">
                <a-tag class="category-tag">
                  {{ picture.category ?? '默认' }}
                </a-tag>
              </a-descriptions-item>
              <a-descriptions-item label="标签" class="desc-item">
                <div class="tags-container">
                  <a-tag v-for="tag in picture.tags" :key="tag" class="tag-item">
                    {{ tag }}
                  </a-tag>
                </div>
              </a-descriptions-item>
              <a-descriptions-item label="格式" class="desc-item">
                <span class="info-value">{{ picture.picFormat ?? '-' }}</span>
              </a-descriptions-item>
              <a-descriptions-item label="宽度" class="desc-item">
                <span class="info-value">{{ picture.picWidth ?? '-' }} px</span>
              </a-descriptions-item>
              <a-descriptions-item label="高度" class="desc-item">
                <span class="info-value">{{ picture.picHeight ?? '-' }} px</span>
              </a-descriptions-item>
              <a-descriptions-item label="宽高比" class="desc-item">
                <span class="info-value">{{ picture.picScale ?? '-' }}</span>
              </a-descriptions-item>
              <a-descriptions-item label="大小" class="desc-item">
                <span class="info-value">{{ formatSize(picture.picSize) }}</span>
              </a-descriptions-item>
              <a-descriptions-item label="主色调" class="desc-item">
                <a-space class="color-info">
                  <span class="info-value">{{ picture.picColor ?? '-' }}</span>
                  <div v-if="picture.picColor" class="color-box" :style="{
                    backgroundColor: toHexColor(picture.picColor),
                  }" />
                </a-space>
              </a-descriptions-item>
            </a-descriptions>
            <!-- 图片操作 -->
            <div class="action-buttons">
              <a-button type="primary" class="action-btn download-btn" @click="doDownload">
                <template #icon>
                  <DownloadOutlined />
                </template>
                免费下载
              </a-button>
              <a-button class="action-btn share-btn" :icon="h(ShareAltOutlined)" @click="doShare">
                分享
              </a-button>
              <a-button v-if="true" class="action-btn edit-btn" :icon="h(EditOutlined)" @click="doEdit">
                编辑
              </a-button>
              <a-button v-if="true" class="action-btn delete-btn" :icon="h(DeleteOutlined)" @click="doDelete">
                删除
              </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>
    <ShareModal ref="shareModalRef" title="分享图片" :link="shareLink || 'https://www.codefather.cn'" />
  </div>
</template>

<script setup lang="ts">
import { h, onMounted, ref, watch } from 'vue'
import { deletePictureUsingPost, getPictureVoByIdUsingGet } from '@/api/pictureController'
import { message } from 'ant-design-vue'
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  ShareAltOutlined,
} from '@ant-design/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { downloadImage, formatSize, toHexColor } from '@/utils'
import ShareModal from '@/components/ShareModal.vue'

interface Props {
  id?: string | number
}

const props = defineProps<Props>()
const route = useRoute()
const picture = ref<API.PictureVO>({})
const loading = ref(false)

// 获取图片详情
const fetchPictureDetail = async () => {
  // 从 props 或 route.params 获取 id
  let idParam = props.id
  if (!idParam) {
    const routeId = route.params.id
    idParam = Array.isArray(routeId) ? routeId[0] : routeId
  }

  if (!idParam) {
    message.error('图片ID不存在')
    return
  }

  try {
    loading.value = true
    // 保持 id 为字符串形式，避免大整数精度丢失
    const idValue = typeof idParam === 'string' ? idParam : String(idParam)

    // 检查是否为有效数字字符串
    if (!/^\d+$/.test(idValue)) {
      message.error('无效的图片ID')
      return
    }

    // 关键修复：检查 id 是否超过安全整数范围
    // JavaScript Number.MAX_SAFE_INTEGER = 9007199254740991 (16位)
    // 如果 id 超过这个范围，不能转换为数字，否则会丢失精度
    // axios 的 params 可以接受字符串，会被正确序列化为查询参数

    // 检查 id 是否超过安全整数范围（16位数字）
    const isBigInt = idValue.length > 16 || (idValue.length === 16 && idValue > '9007199254740991')

    // 关键修复：如果 id 超过安全整数范围，直接传递字符串给 axios
    // axios 的 params 序列化支持字符串，会被正确转换为查询参数
    // 使用类型断言绕过 TypeScript 类型检查（因为类型定义要求 number，但实际运行时字符串也可以）
    const finalId: number | string = isBigInt ? idValue : Number.parseInt(idValue, 10)
    const res = await getPictureVoByIdUsingGet({
      id: finalId as number,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else {
      message.error('获取图片详情失败，' + res.data.message)
    }
  } catch (e: unknown) {
    const errorMessage = e instanceof Error ? e.message : '未知错误'
    message.error('获取图片详情失败：' + errorMessage)
  } finally {
    loading.value = false
  }
}

// 监听路由参数变化
watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      fetchPictureDetail()
    }
  },
  { immediate: false },
)

// 监听 props.id 变化
watch(
  () => props.id,
  (newId) => {
    if (newId) {
      fetchPictureDetail()
    }
  },
  { immediate: false },
)

onMounted(() => {
  fetchPictureDetail()
})

const router = useRouter()

// 编辑
const doEdit = () => {
  if (!picture.value.id) {
    message.error('图片ID不存在')
    return
  }
  // 使用字符串形式传递 id，避免大整数精度丢失
  router.push({
    path: '/add_picture',
    query: {
      id: String(picture.value.id),
    },
  })
}

// 删除数据
const doDelete = async () => {
  const id = picture.value.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
  } else {
    message.error('删除失败')
  }
}

// 下载图片
const doDownload = () => {
  if (!picture.value.url) {
    message.error('图片地址不存在')
    return
  }
  downloadImage(picture.value.url)
}

// ----- 分享操作 ----
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()
// 分享
const doShare = () => {
  if (!picture.value.id) {
    return
  }
  // 使用字符串形式传递 id，避免大整数精度丢失
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${String(picture.value.id)}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}
</script>

<style scoped>
#pictureDetailPage {
  margin-bottom: 16px;
}

/* 卡片通用样式 */
.preview-card,
.info-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  background: #ffffff;
  overflow: hidden;
  height: 100%;
  max-height: 900px;
  display: flex;
  flex-direction: column;
}

.preview-card :deep(.ant-card-body),
.info-card :deep(.ant-card-body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0;
}

.card-title {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 18px;
  font-weight: 700;
  display: inline-block;
}

.preview-card :deep(.ant-card-head),
.info-card :deep(.ant-card-head) {
  border-bottom: 2px solid rgba(102, 126, 234, 0.1);
  padding: 16px 24px;
}

.preview-card :deep(.ant-card-body),
.info-card :deep(.ant-card-body) {
  padding: 24px;
}

/* 图片预览区域 */
.image-container {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  min-height: 600px;
  max-height: 800px;
  overflow: hidden;
  flex: 1;
}

.preview-image {
  width: 100%;
  height: 100%;
}

.preview-image :deep(.ant-image) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image :deep(.ant-image-img) {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.preview-image :deep(.ant-image-mask) {
  border-radius: 8px;
}

.preview-image :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 8px;
}

/* 描述列表样式 */
.picture-descriptions {
  margin-bottom: 24px;
  flex: 1;
}

.picture-descriptions :deep(.ant-descriptions-item-label) {
  font-weight: 600;
  color: #6b7280;
  width: 90px;
  padding: 10px 0;
  font-size: 14px;
}

.picture-descriptions :deep(.ant-descriptions-item-content) {
  color: #1f2937;
  padding: 10px 0;
  font-size: 14px;
  word-break: break-word;
}

.desc-item {
  border-bottom: 1px solid #f3f4f6;
}

.desc-item:last-child {
  border-bottom: none;
}

/* 作者信息 */
.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  border: 2px solid #e5e7eb;
  transition: border-color 0.3s ease;
}

.author-info:hover .author-avatar {
  border-color: #667eea;
}

.author-name {
  font-weight: 500;
  color: #1f2937;
  font-size: 15px;
}

/* 信息值样式 */
.info-value {
  color: #1f2937;
  font-size: 15px;
}

/* 分类标签 */
.category-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  padding: 4px 14px;
  font-size: 13px;
  font-weight: 500;
}

/* 标签容器 */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  background: #f3f4f6;
  color: #6b7280;
  border: none;
  border-radius: 12px;
  padding: 4px 12px;
  font-size: 13px;
  transition: all 0.2s ease;
}

.tag-item:hover {
  background: #e5e7eb;
  color: #4b5563;
}

/* 颜色信息 */
.color-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-box {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  border: 2px solid #e5e7eb;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 操作按钮区域 */
.action-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  padding-top: 24px;
  margin-top: auto;
  border-top: 1px solid #f3f4f6;
}

.action-btn {
  border-radius: 12px;
  height: 40px;
  padding: 0 20px;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
}

/* 下载按钮 - 渐变主题色 */
.download-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.download-btn:hover {
  background: linear-gradient(135deg, #5568d3 0%, #653d8a 100%);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.download-btn:active {
  transform: translateY(0);
}

/* 分享按钮 - 渐变边框 */
.share-btn {
  background: #ffffff;
  border: 2px solid transparent;
  background-image:
    linear-gradient(#ffffff, #ffffff), linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background-origin: border-box;
  background-clip: padding-box, border-box;
  color: #667eea;
}

.share-btn:hover {
  background-image:
    linear-gradient(#f9fafb, #f9fafb), linear-gradient(135deg, #5568d3 0%, #653d8a 100%);
  color: #5568d3;
  transform: translateY(-2px);
}

.share-btn:active {
  transform: translateY(0);
}

/* 编辑按钮 */
.edit-btn {
  background: #f3f4f6;
  color: #4b5563;
}

.edit-btn:hover {
  background: #e5e7eb;
  color: #1f2937;
  transform: translateY(-2px);
}

.edit-btn:active {
  transform: translateY(0);
}

/* 删除按钮 */
.delete-btn {
  background: #fee2e2;
  color: #dc2626;
}

.delete-btn:hover {
  background: #fecaca;
  color: #b91c1c;
  transform: translateY(-2px);
}

.delete-btn:active {
  transform: translateY(0);
}

/* 响应式优化 */
@media (max-width: 1200px) {

  .preview-card,
  .info-card {
    max-height: 750px;
  }

  .image-container {
    min-height: 500px;
    max-height: 650px;
    padding: 20px;
  }
}

@media (max-width: 768px) {

  .preview-card,
  .info-card {
    max-height: 600px;
  }

  .image-container {
    min-height: 400px;
    max-height: 500px;
    padding: 16px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-btn {
    width: 100%;
  }

  .picture-descriptions :deep(.ant-descriptions-item-label) {
    width: 80px;
  }

  .preview-card :deep(.ant-card-body),
  .info-card :deep(.ant-card-body) {
    padding: 16px;
  }
}

@media (min-width: 1400px) {

  .preview-card,
  .info-card {
    max-height: 1000px;
  }

  .image-container {
    min-height: 650px;
    max-height: 850px;
    padding: 28px;
  }
}
</style>
