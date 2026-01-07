
import 'ant-design-vue/dist/reset.css';
import "@/access";

import {createI18n} from "vue-i18n";
import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue';
import VueCropper from 'vue-cropper';
import 'vue-cropper/dist/index.css'


const  i18n = createI18n({
  legacy: false,
  locale: 'en',
  fallbackLng: 'zh',
  messages: {
    zh : {

    }
  }
})

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)
app.use(VueCropper)
app.mount('#app')

