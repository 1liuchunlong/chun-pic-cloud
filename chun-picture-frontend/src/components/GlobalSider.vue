<template>
  <div id="globalSider">
    <a-layout-sider v-if="loginUserStore.loginUser.id" class="sider"
                    width="200" breakpoint="lg" collapsed-width="0">
      <a-menu
        mode="inline"
        v-model:selectedKeys="current"
        :items="menuItems"
        @click="doMenuClick"
      />
    </a-layout-sider>
  </div>
</template>
<script lang="ts" setup>
// 菜单列表
import { useRouter } from 'vue-router'
import { computed, h, ref, watchEffect } from 'vue'
import { PictureOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'

const loginUserStore = useLoginUserStore()
const menuItems = [
  {
    key: '/',
    label: '公共图库',
    icon: () => h(PictureOutlined),
  },
  {
    key: '/my_space',
    label: '我的空间',
    icon: () => h(UserOutlined),
  },
]

const router = useRouter()

// 当前选中菜单
const current = ref<string[]>([])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, failure) => {
  current.value = [to.path]
})

// 路由跳转事件
const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}
</script>

<style scoped>
#globalSider :deep(.ant-layout-sider) {
  background: linear-gradient(180deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  border-right: 1px solid rgba(102, 126, 234, 0.1);
}

#globalSider :deep(.ant-menu) {
  background: transparent;
  border-right: none;
  padding: 16px 12px;
}

#globalSider :deep(.ant-menu-item) {
  margin: 8px 0;
  border-radius: 12px;
  height: 48px;
  line-height: 48px;
  padding-left: 20px !important;
  transition: all 0.3s ease;
  color: #4b5563;
  font-weight: 500;
}

#globalSider :deep(.ant-menu-item:hover) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  transform: translateX(4px);
}

#globalSider :deep(.ant-menu-item-selected) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #ffffff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

#globalSider :deep(.ant-menu-item-selected::after) {
  display: none;
}

#globalSider :deep(.ant-menu-item-icon) {
  font-size: 18px;
  margin-right: 12px;
}

#globalSider :deep(.ant-menu-item-selected .ant-menu-item-icon) {
  color: #ffffff;
}
</style>
