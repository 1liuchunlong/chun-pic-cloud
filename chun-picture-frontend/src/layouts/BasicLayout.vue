<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout>
        <GlobalSider class="sider" />
        <a-layout-content class="content">
          <router-view />
        </a-layout-content>
      </a-layout>
      <a-layout-footer class="footer">
        <a href="http://www.chunchun.icu" target="_blank"> 纯纯小站 by 程序员小纯</a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalSider from "@/components/GlobalSider.vue";
</script>

<style scoped>
#basicLayout .header {
  padding-inline: 24px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.98) 100%);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  border-bottom: 1px solid rgba(102, 126, 234, 0.1);
  height: 64px;
  position: sticky;
  top: 0;
  z-index: 100;
}

#basicLayout .sider {
  background: transparent;
  padding-top: 24px;
}

#basicLayout :deep(.ant-menu-root) {
  border-bottom: none !important;
  border-inline-end: none !important;
}

#basicLayout .content {
  padding: 32px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.02) 0%, rgba(255, 255, 255, 1) 50%, rgba(118, 75, 162, 0.02) 100%);
  min-height: calc(100vh - 64px - 60px);
  margin-bottom: 60px;
}

#basicLayout .footer {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  border-top: 1px solid rgba(102, 126, 234, 0.1);
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
  backdrop-filter: blur(10px);
  z-index: 99;
}

#basicLayout .footer a {
  color: #6b7280;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
}

#basicLayout .footer a:hover {
  color: #667eea;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
</style>
