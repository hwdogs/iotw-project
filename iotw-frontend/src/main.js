import {createApp} from 'vue'
import App from './App.vue'
import router from "@/router/index.js";
import axios from "axios";
import {createPinia} from "pinia";
import '@/api/mock.js';

import 'element-plus/theme-chalk/dark/css-vars.css'

axios.defaults.baseURL = "http://localhost:8080";

const app = createApp(App)
const pinia = createPinia()

app.use(router)
app.use(pinia)

app.mount('#app')
