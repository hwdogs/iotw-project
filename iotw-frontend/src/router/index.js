import {createRouter, createWebHistory} from "vue-router";
import {unauthorize} from "@/net/index.js";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/views/welcome/LoginPage.vue')
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/views/welcome/RegisterPage.vue')
                },
                {
                    path: 'reset',
                    name: 'welcome-reset',
                    component: () => import('@/views/welcome/ResetPage.vue')
                }
            ]
        }, {
            path: '/index',
            name: 'index',
            redirect: {name: 'home'},
            component: () => import('@/views/IndexView.vue'),
            children: [
                {
                    path: 'home',
                    name: 'home',
                    component: () => import('@/views/HomeView.vue')
                },
                {
                    path: 'mall',
                    name: 'mall',
                    component: () => import('@/views/MallView.vue')
                },
                {
                    path: 'user',
                    name: 'user',
                    component: () => import('@/views/UserView.vue')
                },
                {
                    path: 'page1',
                    name: 'page1',
                    component: () => import('@/views/Page1View.vue')
                },
                {
                    path: 'page2',
                    name: 'page2',
                    component: () => import('@/views/Page2View.vue')
                }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const isUnauthorized = unauthorize()
    if (to.name.startsWith('welcome-') && !isUnauthorized) {
        next('/index')
    } else if (to.fullPath.startsWith('/index') && isUnauthorized) {
        next('/')
    } else {
        next()
    }
})

export default router