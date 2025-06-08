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
        },
        {
            path: '/index',
            name: 'index',
            redirect: { name: 'home' },
            component: () => import('@/views/IndexView.vue'),
            children: [
                {
                    path: 'home',
                    name: 'home',
                    component: () => import('@/views/HomeView.vue')
                },
                {
                    path: 'good',
                    name: 'good',
                    component: () => import('@/views/GoodView.vue')
                },
                {
                    path: 'good_add_or_update',
                    name: 'good_add_or_update',
                    component: () => import('@/views/GoodAddOrUpdateView.vue')
                },
                {
                    path: 'supply',
                    name: 'supply',
                    component: () => import('@/views/SupplyView.vue')
                },
                {
                    path: 'sell',
                    name: 'sell',
                    component: () => import('@/views/SellView.vue')
                },
                {
                    path: 'goods/:goodId/good-add-edit',
                    name: 'good-add-edit',
                    component: () => import('@/views/goods/AddGoodView.vue'),
                    props: true
                },
                {
                    path: 'warehouse',
                    name: 'warehouse',
                    component: () => import('@/views/WarehouseView.vue')
                },
                {
                    path: 'warehouse_add_or_update',
                    name: 'warehouse_add_or_update',
                    component: () => import('@/views/WarehouseAddOrUpdateView.vue')
                },
                {
                    path: 'manage',
                    name: 'manage',
                    component: () => import('@/views/ManageView.vue')
                },
                {
                    path: 'insider',
                    name: 'insider',
                    component: () => import('@/views/AccountView.vue')
                },
                {
                    path: 'supplier',
                    name: 'supplier',
                    component: () => import('@/views/SupplierView.vue')
                },
                {
                    path: 'customer',
                    name: 'customer',
                    component: () => import('@/views/CustomerView.vue')
                },
                {
                    path: 'category',
                    name: 'category',
                    component: () => import('@/views/CategoryView.vue')
                },
                {
                    path: 'roles/:roleId/alloc-menus',
                    name: 'alloc-menus',
                    component: () => import('@/views/roles/AllocMenus.vue'),
                    props: true,
                }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const isUnauthorized = unauthorize()
    if (to.name && to.name.startsWith('welcome-') && !isUnauthorized) {
        next('/index')
    } else if (to.fullPath.startsWith('/index') && isUnauthorized) {
        next('/')
    } else {
        next()
    }
})

export default router