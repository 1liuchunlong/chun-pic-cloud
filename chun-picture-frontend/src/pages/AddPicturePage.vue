<template>
  <div id="addPicturePage">
    <div class="page-header">
      <h2 class="page-title">
        {{ route.query?.id ? '修改图片' : '创建图片' }}
      </h2>
      <a-typography-paragraph v-if="spaceId" class="space-info" type="secondary">
        保存至空间：<a :href="`/space/${spaceId}`" target="_blank" class="space-link">{{ spaceId }}</a>
      </a-typography-paragraph>
    </div>

    <!-- 选择上传方式 -->
    <div class="upload-section">
      <a-tabs v-model:activeKey="uploadType" class="upload-tabs">
        <a-tab-pane key="file" tab="文件上传">
          <!-- 图片上传组件 -->
          <PictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
        </a-tab-pane>
        <a-tab-pane key="url" tab="URL 上传" force-render>
          <!-- URL 图片上传组件 -->
          <UrlPictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- 图片编辑 -->
    <div v-if="picture" class="edit-bar">
      <a-space size="middle">
        <a-button class="edit-button" :icon="h(EditOutlined)" @click="doEditPicture">编辑图片</a-button>
        <a-button class="ai-button" type="primary" ghost :icon="h(FullscreenOutlined)" @click="doImagePainting">
          AI 扩图
        </a-button>
      </a-space>
      <ImageCropper ref="imageCropperRef" :imageUrl="picture?.url" :picture="picture" :spaceId="spaceId"
        :onSuccess="onCropSuccess" />
      <ImageOutPainting ref="imageOutPaintingRef" :picture="picture" :spaceId="spaceId"
        :onSuccess="onImageOutPaintingSuccess" />
    </div>

    <!-- 图片信息表单 -->
    <div v-if="picture" class="form-section">
      <a-form name="pictureForm" layout="vertical" :model="pictureForm" @finish="handleSubmit" class="picture-form">
        <a-form-item name="name" label="名称">
          <a-input v-model:value="pictureForm.name" placeholder="请输入名称" allow-clear />
        </a-form-item>
        <a-form-item name="introduction" label="简介">
          <a-textarea v-model:value="pictureForm.introduction" placeholder="请输入简介"
            :auto-size="{ minRows: 2, maxRows: 5 }" allow-clear />
        </a-form-item>
        <a-form-item name="category" label="分类">
          <a-auto-complete v-model:value="pictureForm.category" placeholder="请输入分类" :options="categoryOptions"
            allow-clear />
        </a-form-item>
        <a-form-item name="tags" label="标签">
          <a-select v-model:value="pictureForm.tags" mode="tags" placeholder="请输入标签" :options="tagOptions"
            allow-clear />
        </a-form-item>
        <a-form-item>
          <a-button class="submit-button" type="primary" html-type="submit" style="width: 100%">
            {{ route.query?.id ? '保存' : '创建' }}
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import PictureUpload from '@/components/PictureUpload.vue'
import { computed, h, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  editPictureUsingPost,
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet,
} from '@/api/pictureController'
import { useRoute, useRouter } from 'vue-router'
import UrlPictureUpload from '@/components/UrlPictureUpload.vue'
import ImageCropper from '@/components/ImageCropper.vue'
import { EditOutlined, FullscreenOutlined } from '@ant-design/icons-vue'
import ImageOutPainting from '@/components/ImageOutPainting.vue'

const router = useRouter()
const route = useRoute()

const picture = ref<API.PictureVO>()
const pictureForm = reactive<API.PictureEditRequest>({})
const uploadType = ref<'file' | 'url'>('file')
// 空间 id
const spaceId = computed(() => {
  return route.query?.spaceId
})

/**
 * 图片上传成功
 * @param newPicture
 */
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  console.log(values)
  const pictureId = picture.value.id
  if (!pictureId) {
    return
  }
  const res = await editPictureUsingPost({
    id: pictureId,
    spaceId: spaceId.value,
    ...values,
  })
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success('创建成功')
    // 跳转到图片详情页
    router.push({
      path: `/picture/${pictureId}`,
    })
  } else {
    message.error('创建失败，' + res.data.message)
  }
}

const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])

/**
 * 获取标签和分类选项
 * @param values
 */
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('获取标签分类列表失败，' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})

// 获取老数据
const getOldPicture = async () => {
  // 获取到 id
  const id = route.query?.id
  if (id) {
    const res = await getPictureVoByIdUsingGet({
      id,
    })
    if (res.data.code === 0 && res.data.data) {
      const data = res.data.data
      picture.value = data
      pictureForm.name = data.name
      pictureForm.introduction = data.introduction
      pictureForm.category = data.category
      pictureForm.tags = data.tags
    }
  }
}

onMounted(() => {
  getOldPicture()
})

// ----- 图片编辑器引用 ------
const imageCropperRef = ref()

// 编辑图片
const doEditPicture = async () => {
  imageCropperRef.value?.openModal()
}

// 编辑成功事件
const onCropSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}

// ----- AI 扩图引用 -----
const imageOutPaintingRef = ref()

// 打开 AI 扩图弹窗
const doImagePainting = async () => {
  imageOutPaintingRef.value?.openModal()
}

// AI 扩图保存事件
const onImageOutPaintingSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
}
</script>

<style scoped>
#addPicturePage {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 2px solid rgba(102, 126, 234, 0.1);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.space-info {
  margin: 0;
  font-size: 14px;
}

.space-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.space-link:hover {
  color: #764ba2;
  text-decoration: underline;
}

.upload-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease;
}

.upload-section:hover {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.1);
}

.upload-tabs :deep(.ant-tabs-tab) {
  font-weight: 500;
  font-size: 15px;
}

.upload-tabs :deep(.ant-tabs-tab-active) {
  color: #667eea;
}

.upload-tabs :deep(.ant-tabs-ink-bar) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.edit-bar {
  background: #ffffff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease;
}

.edit-bar:hover {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.1);
}

.edit-button,
.ai-button {
  height: 40px;
  padding: 0 24px;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.edit-button {
  background: #f3f4f6;
  color: #4b5563;
  border: none;
}

.edit-button:hover {
  background: #e5e7eb;
  color: #1f2937;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.ai-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.ai-button:hover {
  background: linear-gradient(135deg, #5568d3 0%, #653d8a 100%);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.ai-button:active {
  transform: translateY(0);
}

.form-section {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease;
}

.form-section:hover {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.1);
}

.picture-form :deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.picture-form :deep(.ant-input),
.picture-form :deep(.ant-input-affix-wrapper),
.picture-form :deep(.ant-select-selector),
.picture-form :deep(.ant-picker) {
  border-radius: 8px;
  border-color: #d1d5db;
  transition: all 0.3s ease;
}

.picture-form :deep(.ant-input):focus,
.picture-form :deep(.ant-input-affix-wrapper):focus,
.picture-form :deep(.ant-select-selector):focus,
.picture-form :deep(.ant-picker):focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

.submit-button {
  height: 44px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.submit-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #5568d3 0%, #653d8a 100%);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.submit-button:active:not(:disabled) {
  transform: translateY(0);
}

/* 响应式 */
@media (max-width: 768px) {
  #addPicturePage {
    padding: 16px;
  }

  .page-title {
    font-size: 24px;
  }

  .upload-section,
  .edit-bar,
  .form-section {
    padding: 20px;
  }

  .edit-bar {
    padding: 20px 16px;
  }

  .edit-button,
  .ai-button {
    width: 100%;
    margin-bottom: 12px;
  }

  .edit-bar :deep(.ant-space) {
    width: 100%;
    flex-direction: column;
  }
}
</style>
