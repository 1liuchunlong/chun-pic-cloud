<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <router-link to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.svg" alt="logo" />
            <div class="title">纯纯云图库</div>
          </div>
        </router-link>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <!-- 用户信息展示栏 -->
      <a-col flex="120px">
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item>
                    <router-link to="/my_space">
                      <UserOutlined />
                      我的空间
                    </router-link>
                  </a-menu-item>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import { HomeOutlined, LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { userLogoutUsingPost } from '@/api/userController'

const loginUserStore = useLoginUserStore()

// 未经过滤的菜单项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/add_picture',
    label: '创建图片',
    title: '创建图片',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/pictureManage',
    label: '图片管理',
    title: '图片管理',
  },
  {
    key: '/admin/spaceManage',
    label: '空间管理',
    title: '空间管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://www.codefather.cn', target: '_blank' }, '编程导航'),
    title: '编程导航',
  },
]

// 根据权限过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    // 管理员才能看到 /admin 开头的菜单
    if (menu?.key?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const items = computed(() => filterMenus(originItems))

const router = useRouter()
// 当前要高亮的菜单项
const current = ref<string[]>([])
// 监听路由变化，更新高亮菜单项
router.afterEach((to, from, next) => {
  current.value = [to.path]
})

// 路由跳转事件
const doMenuClick = ({ key }) => {
  router.push({
    path: key,
  })
}

// 用户注销
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
#globalHeader {
  height: 100%;
}

#globalHeader .title-bar {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: opacity 0.2s ease;
  padding: 8px 0;
}

#globalHeader .title-bar:hover {
  opacity: 0.8;
}

.title {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-size: 20px;
  font-weight: 700;
  margin-left: 12px;
  letter-spacing: 0.5px;
}

.logo {
  height: 40px;
  width: 40px;
  transition: transform 0.3s ease;
}

#globalHeader .title-bar:hover .logo {
  transform: scale(1.05) rotate(5deg);
}

/* 菜单样式 */
#globalHeader :deep(.ant-menu) {
  background: transparent;
  border-bottom: none;
  line-height: 64px;
}

#globalHeader :deep(.ant-menu-item) {
  margin: 0 8px;
  border-radius: 8px;
  transition: all 0.3s ease;
  color: #4b5563;
  font-weight: 500;
}

#globalHeader :deep(.ant-menu-item:hover) {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

#globalHeader :deep(.ant-menu-item-selected) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  color: #667eea;
  border-bottom: none;
}

#globalHeader :deep(.ant-menu-item-selected::after) {
  display: none;
}

/* 用户信息区域 */
.user-login-status {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  height: 100%;
}

.user-login-status :deep(.ant-space) {
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.user-login-status :deep(.ant-space):hover {
  background: rgba(102, 126, 234, 0.1);
}

.user-login-status :deep(.ant-avatar) {
  border: 2px solid #e5e7eb;
  transition: border-color 0.3s ease;
}

.user-login-status :deep(.ant-space:hover .ant-avatar) {
  border-color: #667eea;
}

/* 登录按钮 */
.user-login-status :deep(.ant-btn-primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 12px;
  padding: 8px 24px;
  height: auto;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.user-login-status :deep(.ant-btn-primary:hover) {
  background: linear-gradient(135deg, #5568d3 0%, #653d8a 100%);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.user-login-status :deep(.ant-btn-primary:active) {
  transform: translateY(0);
}

/* 下拉菜单 */
.user-login-status :deep(.ant-dropdown-menu) {
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.user-login-status :deep(.ant-dropdown-menu-item) {
  border-radius: 8px;
  margin: 4px 0;
  padding: 10px 16px;
  transition: all 0.2s ease;
}

.user-login-status :deep(.ant-dropdown-menu-item:hover) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
}

.user-login-status :deep(.ant-dropdown-menu-item a) {
  color: inherit;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
