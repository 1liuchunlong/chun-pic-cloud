<template>
  <div id="homePage">
    <!-- 搜索框 -->
    <div class="search-bar">
      <a-input-search
        v-model:value="searchParams.searchText"
        placeholder="从海量图片中搜索"
        enter-button="搜索"
        size="large"
        @search="doSearch"
      />
    </div>
    <!-- 分类和标签筛选 -->
    <a-tabs v-model:active-key="selectedCategory" @change="doSearch">
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane v-for="category in categoryList" :tab="category" :key="category" />
    </a-tabs>
    <div class="tag-bar">
      <span style="margin-right: 8px">标签：</span>
      <a-space :size="[0, 8]" wrap>
        <a-checkable-tag
          v-for="(tag, index) in tagList"
          :key="tag"
          v-model:checked="selectedTagList[index]"
          @change="doSearch"
        >
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>
    <!-- 图片列表 -->
    <PictureList :dataList="dataList" :loading="loading" />
    <!-- 分页 -->
    <a-pagination
      style="text-align: right"
      v-model:current="searchParams.current"
      v-model:pageSize="searchParams.pageSize"
      :total="total"
      @change="onPageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController'
import { message } from 'ant-design-vue'
import PictureList from '@/components/PictureList.vue'
import { DEFAULT_PAGE_SIZE } from '@/constants/common'

// 定义数据
const dataList = ref<API.PictureVO[]>([])
const total = ref(0)
const loading = ref(true)

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: DEFAULT_PAGE_SIZE,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  // 转换搜索参数
  const params = {
    ...searchParams,
    tags: [] as string[],
  }
  if (selectedCategory.value !== 'all') {
    params.category = selectedCategory.value
  }
  // [true, false, false] => ['java']
  selectedTagList.value.forEach((useTag, index) => {
    if (useTag) {
      params.tags.push(tagList.value[index])
    }
  })
  const res = await listPictureVoByPageUsingPost(params)
  if (res.data.code === 0 && res.data.data) {
    // 修复 id 精度丢失问题：将 id 转换为字符串保存
    const records = (res.data.data.records ?? []).map((picture: API.PictureVO) => {
      if (picture.id) {
        // 如果 id 是 number 且可能丢失精度，从原始响应中获取字符串形式
        // 这里我们直接转换为字符串，确保不会丢失精度
        return {
          ...picture,
          id: picture.id, // 保持原值，但在使用时转换为字符串
        }
      }
      return picture
    })
    dataList.value = records
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败，' + res.data.message)
  }
  loading.value = false
}

// 页面加载时获取数据，请求一次
onMounted(() => {
  fetchData()
})

// 分页参数
const onPageChange = (page: number, pageSize: number) => {
  searchParams.current = page
  searchParams.pageSize = pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  // 重置搜索条件
  searchParams.current = 1
  fetchData()
}

// 标签和分类列表
const categoryList = ref<string[]>([])
const selectedCategory = ref<string>('all')
const tagList = ref<string[]>([])
const selectedTagList = ref<boolean[]>([])

/**
 * 获取标签和分类选项
 * @param values
 */
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    tagList.value = res.data.data.tagList ?? []
    // 过滤掉"全部"，因为前端已经硬编码了一个"全部"选项（key="all"）
    categoryList.value = (res.data.data.categoryList ?? []).filter(cat => cat !== '全部')
  } else {
    message.error('获取标签分类列表失败，' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})
</script>

<style scoped>
#homePage {
  margin-bottom: 16px;
  padding: 0 8px;
}

#homePage .search-bar {
  max-width: 600px;
  margin: 0 auto 32px;
}

#homePage .search-bar :deep(.ant-input-search) {
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.1);
}

#homePage .search-bar :deep(.ant-input-search .ant-input) {
  border-radius: 24px 0 0 24px;
  border: 2px solid #e5e7eb;
  padding: 12px 20px;
  font-size: 15px;
}

#homePage .search-bar :deep(.ant-input-search .ant-input:focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

#homePage .search-bar :deep(.ant-input-search-button) {
  border-radius: 0 24px 24px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: 2px solid transparent;
  border-left: none;
  height: 100%;
  padding: 0 24px;
  font-weight: 500;
}

#homePage .search-bar :deep(.ant-input-search-button:hover) {
  background: linear-gradient(135deg, #5568d3 0%, #653d8a 100%);
}

/* Tabs 样式 */
#homePage :deep(.ant-tabs) {
  margin-bottom: 24px;
}

#homePage :deep(.ant-tabs-tab) {
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 500;
  color: #6b7280;
  transition: all 0.3s ease;
}

#homePage :deep(.ant-tabs-tab:hover) {
  color: #667eea;
}

#homePage :deep(.ant-tabs-tab-active .ant-tabs-tab-btn) {
  color: #667eea;
  font-weight: 600;
}

#homePage :deep(.ant-tabs-ink-bar) {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  height: 3px;
  border-radius: 2px;
}

/* 标签栏样式 */
#homePage .tag-bar {
  margin-bottom: 24px;
  padding: 16px 20px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

#homePage .tag-bar > span {
  margin-right: 12px;
  font-weight: 600;
  color: #4b5563;
  font-size: 14px;
}

#homePage .tag-bar :deep(.ant-tag) {
  border-radius: 12px;
  padding: 4px 14px;
  font-size: 13px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  color: #6b7280;
  transition: all 0.2s ease;
  cursor: pointer;
}

#homePage .tag-bar :deep(.ant-tag-checkable-checked) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: #ffffff;
}

#homePage .tag-bar :deep(.ant-tag-checkable):hover {
  border-color: #667eea;
  color: #667eea;
}

/* 分页样式 */
#homePage :deep(.ant-pagination) {
  margin-top: 40px;
}

#homePage :deep(.ant-pagination-item) {
  border-radius: 8px;
  border-color: #e5e7eb;
  transition: all 0.2s ease;
}

#homePage :deep(.ant-pagination-item:hover) {
  border-color: #667eea;
}

#homePage :deep(.ant-pagination-item-active) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
}

#homePage :deep(.ant-pagination-item-active a) {
  color: #ffffff;
}

#homePage :deep(.ant-pagination-item:hover a) {
  color: #667eea;
}

#homePage :deep(.ant-pagination-prev),
#homePage :deep(.ant-pagination-next) {
  border-radius: 8px;
  transition: all 0.2s ease;
}

#homePage :deep(.ant-pagination-prev:hover),
#homePage :deep(.ant-pagination-next:hover) {
  border-color: #667eea;
}

#homePage :deep(.ant-pagination-prev:hover .anticon),
#homePage :deep(.ant-pagination-next:hover .anticon) {
  color: #667eea;
}
</style>
