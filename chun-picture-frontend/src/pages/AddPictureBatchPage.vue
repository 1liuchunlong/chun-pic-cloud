<template>
  <div id="addPictureBatchPage">
    <h2 style="margin-bottom: 16px">批量创建</h2>
    <!-- 图片信息表单 -->
    <a-form name="formData" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item name="searchText" label="关键词">
        <a-input v-model:value="formData.searchText" placeholder="请输入关键词" allow-clear />
      </a-form-item>
      <a-form-item name="count" label="抓取数量">
        <a-input-number
          v-model:value="formData.count"
          placeholder="请输入数量"
          style="min-width: 180px"
          :min="1"
          :max="30"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="namePrefix" label="名称前缀">
        <a-input
          v-model:value="formData.namePrefix"
          placeholder="请输入名称前缀，会自动补充序号"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="category" label="分类">
        <a-radio-group v-model:value="formData.category">
          <a-radio
            v-for="category in categoryList"
            :key="category"
            :value="category"
          >
            {{ category }}
          </a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item name="tags" label="标签">
        <a-select
          v-model:value="formData.tags"
          mode="tags"
          placeholder="请输入标签，按回车添加"
          style="width: 100%"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          执行任务
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'

import { useRouter } from 'vue-router'
import { uploadPictureByBatchUsingPost, listPictureTagCategoryUsingGet } from '@/api/pictureController'

const formData = reactive<API.PictureUploadByBatchRequest>({
  count: 10,
})

// 分类列表
const categoryList = ref<string[]>([])

// 获取分类列表
const getCategoryList = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    categoryList.value = res.data.data.categoryList ?? []
  } else {
    message.error('获取分类列表失败，' + res.data.message)
  }
}

// 提交任务状态
const loading = ref(false)

const router = useRouter()

/**
 * 提交表单
 */
const handleSubmit = async () => {
  loading.value = true
  const res = await uploadPictureByBatchUsingPost({
    ...formData,
  })
  // 操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success(`创建成功，共 ${res.data.data} 条`)
    // 跳转到主页
    router.push({
      path: `/`,
    })
  } else {
    message.error('创建失败，' + res.data.message)
  }
  loading.value = false
}

onMounted(() => {
  getCategoryList()
})
</script>

<style scoped>
#addPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
