<template>
  <div class="picture-list">
    <!-- 图片列表 -->
    <a-list
      :grid="{ gutter: [24, 24], xs: 1, sm: 2, md: 3, lg: 4, xl: 4, xxl: 5 }"
      :data-source="dataList"
      :loading="loading"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0">
          <!-- 单张图片 -->
          <a-card
            hoverable
            class="picture-card"
            @click="doClickPicture(picture)"
          >
            <template #cover>
              <div class="picture-cover">
                <img
                  :alt="picture.name"
                  :src="picture.thumbnailUrl ?? picture.url"
                  class="picture-image"
                />
                <div class="picture-overlay" @click.stop>
                  <div class="picture-actions">
                    <a-tooltip :title="isLiked(picture.id) ? '取消点赞' : '点赞'">
                      <span class="like-icon-wrapper">
                        <component
                          :is="isLiked(picture.id) ? LikeFilled : LikeOutlined"
                          :class="{ 'like-active': isLiked(picture.id) }"
                          @click="(e) => doLike(picture, e)"
                        />
                      </span>
                    </a-tooltip>
                    <a-tooltip title="分享">
                      <ShareAltOutlined @click="(e) => doShare(picture, e)" />
                    </a-tooltip>
                    <a-tooltip title="以图搜图">
                      <SearchOutlined @click="(e) => doSearch(picture, e)" />
                    </a-tooltip>
                    <a-tooltip v-if="canEdit" title="编辑">
                      <EditOutlined @click="(e) => doEdit(picture, e)" />
                    </a-tooltip>
                    <a-tooltip v-if="canDelete" title="删除">
                      <DeleteOutlined @click="(e) => doDelete(picture, e)" />
                    </a-tooltip>
                  </div>
                </div>
              </div>
            </template>
            <a-card-meta class="picture-meta">
              <template #description>
                <div class="picture-tags">
                  <a-tag class="category-tag">
                    {{ picture.category ?? '默认' }}
                  </a-tag>
                  <a-tag
                    v-for="tag in (picture.tags || [])"
                    :key="tag"
                    class="tag-item"
                  >
                    {{ tag }}
                  </a-tag>
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
    <ShareModal ref="shareModalRef" :link="shareLink" />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  DeleteOutlined,
  EditOutlined,
  LikeFilled,
  LikeOutlined,
  SearchOutlined,
  ShareAltOutlined,
} from '@ant-design/icons-vue'
import { deletePictureUsingPost } from '@/api/pictureController'
import { message } from 'ant-design-vue'
import ShareModal from '@/components/ShareModal.vue'
import { ref, reactive, watch } from 'vue'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  showOp?: boolean
  canEdit?: boolean
  canDelete?: boolean
  onReload?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false,
  showOp: true,
  canEdit: false,
  canDelete: false,
})

const router = useRouter()
// 跳转至图片详情页
const doClickPicture = (picture: API.PictureVO) => {
  if (!picture.id) {
    return
  }
  // 使用字符串形式传递 id，避免大整数精度丢失
  router.push({
    path: `/picture/${String(picture.id)}`,
  })
}

// 搜索
const doSearch = (picture, e) => {
  // 阻止冒泡
  e.stopPropagation()
  if (!picture.id) {
    return
  }
  // 使用字符串形式传递 id，避免大整数精度丢失
  window.open(`/search_picture?pictureId=${String(picture.id)}`)
}

// 编辑
const doEdit = (picture, e) => {
  // 阻止冒泡
  e.stopPropagation()
  // 跳转时一定要携带 spaceId
  router.push({
    path: '/add_picture',
    query: {
      id: picture.id,
      spaceId: picture.spaceId,
    },
  })
}

// 删除数据
const doDelete = async (picture, e) => {
  // 阻止冒泡
  e.stopPropagation()
  const id = picture.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    props.onReload?.()
  } else {
    message.error('删除失败')
  }
}

// ----- 点赞操作 ----
// 点赞状态管理：Map<pictureId, { liked: boolean, count: number }>
const likeMap = reactive<Map<number, { liked: boolean; count: number }>>(new Map())

// 初始化点赞状态（如果后端有API，可以在这里调用获取初始状态）
const initLikeState = (picture: API.PictureVO) => {
  if (picture.id && !likeMap.has(picture.id)) {
    // 初始状态：未点赞，点赞数为0（如果后端有API，可以从picture对象中获取）
    likeMap.set(picture.id, { liked: false, count: 0 })
  }
}

// 检查是否已点赞
const isLiked = (pictureId?: number): boolean => {
  if (!pictureId) return false
  return likeMap.get(pictureId)?.liked ?? false
}

// 点赞/取消点赞
const doLike = async (picture: API.PictureVO, e: Event) => {
  // 阻止冒泡
  e.stopPropagation()

  if (!picture.id) {
    return
  }

  // 初始化状态
  if (!likeMap.has(picture.id)) {
    initLikeState(picture)
  }

  const likeState = likeMap.get(picture.id)!
  const wasLiked = likeState.liked

  // 切换点赞状态
  likeState.liked = !wasLiked
  likeState.count += wasLiked ? -1 : 1

  // TODO: 如果后端有API，可以在这里调用点赞接口
  // 例如：
  // try {
  //   const res = await likePictureUsingPost({ pictureId: picture.id, liked: !wasLiked })
  //   if (res.data.code !== 0) {
  //     // 如果失败，回滚状态
  //     likeState.liked = wasLiked
  //     likeState.count += wasLiked ? 1 : -1
  //     message.error(res.data.message || '操作失败')
  //   }
  // } catch (error) {
  //   // 如果失败，回滚状态
  //   likeState.liked = wasLiked
  //   likeState.count += wasLiked ? 1 : -1
  //   message.error('操作失败')
  // }

  // 显示反馈消息（可选）
  // message.success(likeState.liked ? '已点赞' : '已取消点赞')
}

// 监听数据列表变化，初始化点赞状态
watch(
  () => props.dataList,
  (newList) => {
    newList.forEach((picture) => {
      if (picture.id) {
        initLikeState(picture)
      }
    })
  },
  { immediate: true, deep: true }
)

// ----- 分享操作 ----
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()
// 分享
const doShare = (picture, e) => {
  // 阻止冒泡
  e.stopPropagation()
  if (!picture.id) {
    return
  }
  // 使用字符串形式传递 id，避免大整数精度丢失
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${String(picture.id)}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}
</script>

<style scoped>
.picture-list {
  padding: 8px 0;
}

.picture-card {
  border-radius: 16px;
  overflow: hidden;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: #ffffff;
}

.picture-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(99, 102, 241, 0.15);
}

.picture-cover {
  position: relative;
  width: 100%;
  padding-top: 75%;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.picture-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.picture-card:hover .picture-image {
  transform: scale(1.05);
}

.picture-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.picture-card:hover .picture-overlay {
  opacity: 1;
}

.picture-actions {
  display: flex;
  gap: 20px;
  align-items: center;
}

.picture-actions :deep(.anticon) {
  color: #ffffff;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.picture-actions :deep(.anticon):hover {
  transform: scale(1.15);
  background: rgba(255, 255, 255, 0.3);
}

.picture-meta {
  padding: 16px;
}

.picture-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-tag {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  border: none;
  border-radius: 12px;
  padding: 2px 12px;
  font-size: 12px;
  font-weight: 500;
}

.tag-item {
  background: #f3f4f6;
  color: #6b7280;
  border: none;
  border-radius: 12px;
  padding: 2px 12px;
  font-size: 12px;
  transition: all 0.2s ease;
}

.tag-item:hover {
  background: #e5e7eb;
  color: #4b5563;
}

/* 点赞图标特殊样式 */
.like-icon-wrapper {
  display: inline-block;
  cursor: pointer;
}

.like-icon-wrapper :deep(*) {
  font-size: 20px;
  transition: color 0.3s, transform 0.2s;
  cursor: pointer;
}

.like-icon-wrapper :deep(*):hover {
  transform: scale(1.15);
}

.like-icon-wrapper :deep(*):active {
  transform: scale(0.95);
}

.like-active {
  color: #ff4d4f !important;
}

.picture-actions .like-icon-wrapper :deep(*) {
  color: #ffffff;
}

.picture-actions .like-icon-wrapper .like-active {
  color: #ff4d4f !important;
}
</style>
