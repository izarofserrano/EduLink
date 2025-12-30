import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import LandingPage from './pages/LandingPage.vue'
import LoginPage from './pages/LoginPage.vue'
import ProfilePage from './pages/ProfilePage.vue'
import SearchMaterials from './pages/SearchMaterials.vue'
import UploadDocument from './pages/UploadDocument.vue'
import CourseForum from './pages/CourseForum.vue'
import StudentActivities from './pages/StudentActivities.vue'

// Admin Pages
import ValidateDocumentsPage from './pages/ValidateDocumentsPage.vue'
import ManageUsersPage from './pages/ManageUsersPage.vue'

// ✅ Teacher Pages
import TeacherDashboard from './pages/TeacherDashboard.vue'
import TeacherCourseDetail from './pages/TeacherCourseDetail.vue'
import TeacherUploadMaterial from './pages/TeacherUploadMaterial.vue'

import './style.css'

const routes = [
  { path: '/', component: LandingPage, name: 'landing' },
  { path: '/login', component: LoginPage, name: 'login' },
  { path: '/search', component: SearchMaterials, name: 'search' },
  { path: '/upload', component: UploadDocument, name: 'upload' },
  { path: '/forums', component: CourseForum, name: 'forums' },
  { path: '/activities', component: StudentActivities, name: 'activities' },
  { path: '/profile', component: ProfilePage, name: 'profile' },
  
  // Admin Routes
  { 
    path: '/admin/validate', 
    component: ValidateDocumentsPage, 
    name: 'validate',
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  { 
    path: '/admin/manage', 
    component: ManageUsersPage, 
    name: 'manage',
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  
  // ✅ Teacher Routes
  { 
    path: '/teacher/dashboard', 
    component: TeacherDashboard, 
    name: 'TeacherDashboard',
    meta: { requiresAuth: true, requiresTeacher: true }
  },
  { 
    path: '/teacher/courses/:courseId', 
    component: TeacherCourseDetail, 
    name: 'TeacherCourseDetail',
    meta: { requiresAuth: true, requiresTeacher: true }
  },
  { 
    path: '/teacher/upload', 
    component: TeacherUploadMaterial, 
    name: 'TeacherUploadMaterial',
    meta: { requiresAuth: true, requiresTeacher: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ✅ Navigation Guard - Proteger rutas
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('edulink_user') || '{}')
  
  // Check if route requires authentication
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  // Check if route requires admin
  if (to.meta.requiresAdmin && user.role !== 'ADMIN') {
    alert('Access denied. Admin privileges required.')
    next('/')
    return
  }
  
  // Check if route requires teacher
  if (to.meta.requiresTeacher && user.role !== 'TEACHER') {
    alert('Access denied. Teacher privileges required.')
    next('/')
    return
  }
  
  next()
})

const app = createApp(App)
app.use(router)
app.mount('#app')