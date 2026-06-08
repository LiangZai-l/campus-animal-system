import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/RegisterView.vue'),
    meta: { guest: true }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/animals/:id',
    name: 'AnimalDetail',
    component: () => import('../views/AnimalDetailView.vue')
  },
  {
    path: '/checkin/create',
    name: 'CheckInCreate',
    component: () => import('../views/CheckInCreateView.vue')
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboard.vue'),
    meta: { admin: true }
  },
  {
    path: '/admin/animals/new',
    name: 'AnimalCreate',
    component: () => import('../views/AnimalFormView.vue'),
    meta: { admin: true }
  },
  {
    path: '/admin/animals/:id/edit',
    name: 'AnimalEdit',
    component: () => import('../views/AnimalFormView.vue'),
    meta: { admin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const auth = useAuthStore()

  if (to.meta.admin && auth.user?.role !== 'ADMIN') {
    return next('/')
  }
  if (to.meta.guest && auth.token) {
    return next('/')
  }

  next()
})

export default router
