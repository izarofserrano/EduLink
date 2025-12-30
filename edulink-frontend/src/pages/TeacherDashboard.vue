<template>
  <div class="teacher-dashboard">
    <!-- Header -->
    <div class="page-header">
      <div class="container">
        <h1>Teacher Dashboard</h1>
        <p class="subtitle">Welcome, {{ currentUser?.username }}</p>
      </div>
    </div>

    <div class="container">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading your courses...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <p class="error-message">{{ error }}</p>
        <button @click="loadCourses" class="btn btn-primary">Retry</button>
      </div>

      <!-- Content -->
      <div v-else>
        <!-- Quick Actions -->
        <div class="quick-actions">
          <router-link to="/teacher/upload" class="action-card upload">
            <h3>Upload Material</h3>
            <p>Share resources with students</p>
          </router-link>
          
          <router-link to="/forums" class="action-card forum">
            <h3>Forum Questions</h3>
            <p>Answer student questions</p>
          </router-link>
        </div>

        <!-- My Courses -->
        <div class="section">
          <div class="section-header">
            <h2>My Courses ({{ courses.length }})</h2>
          </div>

          <div v-if="courses.length === 0" class="empty-state">
            <p>No courses assigned yet</p>
            <p class="debug-info">Username: {{ currentUser?.username }}</p>
          </div>

          <div v-else class="courses-grid">
            <div 
              v-for="course in courses" 
              :key="course.courseId"
              class="course-card"
              @click="goToCourseDetail(course.courseId)"
            >
              <div class="course-header">
                <div class="course-code">{{ course.code }}</div>
                <div class="semester-badge">Semester {{ course.semester }}</div>
              </div>
              
              <h3 class="course-name">{{ course.courseName }}</h3>
              
              <div class="course-stats">
                <div class="stat-item">
                  <span class="stat-icon">📄</span>
                  <span class="stat-value">{{ course.documentCount }}</span>
                  <span class="stat-label">Documents</span>
                </div>
                <div class="stat-item">
                  <span class="stat-icon">👥</span>
                  <span class="stat-value">{{ course.studentCount }}</span>
                  <span class="stat-label">Students</span>
                </div>
              </div>

              <div class="course-footer">
                <button class="btn-view">View Details →</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'TeacherDashboard',
  data() {
    return {
      loading: true,
      error: null,
      courses: []
    }
  },
  computed: {
    currentUser() {
      const user = localStorage.getItem('edulink_user')
      return user ? JSON.parse(user) : null
    },
    isTeacher() {
      return this.currentUser?.role === 'TEACHER'
    }
  },
  mounted() {
    this.checkTeacherAccess()
    this.loadCourses()
  },
  methods: {
    checkTeacherAccess() {
      if (!this.isTeacher) {
        this.$router.push('/')
      }
    },

    async loadCourses() {
      this.loading = true
      this.error = null

      try {
        const token = localStorage.getItem('token')
        const username = this.currentUser?.username

        console.log('=== LOADING COURSES ===')
        console.log('Token:', token ? 'Present' : 'Missing')
        console.log('Username:', username)

        const response = await axios.get(
          'http://localhost:8080/api/teacher/courses',
          {
            headers: { 'Authorization': `Bearer ${token}` }
          }
        )

        console.log('Courses response:', response.data)
        this.courses = response.data

      } catch (error) {
        console.error('Error loading courses:', error)
        console.error('Error response:', error.response)
        
        if (error.response?.status === 403) {
          this.error = 'Access denied. Teacher privileges required.'
        } else {
          this.error = 'Failed to load courses. Please try again.'
        }
      } finally {
        this.loading = false
      }
    },

    goToCourseDetail(courseId) {
      this.$router.push(`/teacher/courses/${courseId}`)
    }
  }
}
</script>


<style scoped>
.teacher-dashboard {
  min-height: 100vh;
  background: #ffffff;
  padding-bottom: 3rem;
}

.page-header {
  background: rgb(95, 179, 84);
  backdrop-filter: blur(10px);
  padding: 2rem 0;
  margin-bottom: 2rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.page-header h1 {
  color: white;
  margin: 0;
  font-size: 2.5rem;
}

.page-header .subtitle {
  color: rgba(255, 255, 255, 0.9);
  margin: 0.5rem 0 0 0;
  font-size: 1.1rem;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

/* Quick Actions */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.action-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid var(--accent-blue);
  text-decoration: none;
  color: inherit;
  display: block;
}

.action-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.action-card.upload:hover {
  border-color: #667eea;
}

.action-card.forum:hover {
  border-color: #764ba2;
}

.action-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.action-card h3 {
  margin: 0.5rem 0;
  color: #333;
}

.action-card p {
  color: #666;
  margin: 0;
}

/* Courses Grid */
.section {
  background: whitesmoke;
  border-radius: 12px;
  padding: 2rem;
  margin-bottom: 2rem;
}

.section-header {
  margin-bottom: 1.5rem;
}

.section-header h2 {
  margin: 0;
  color: #333;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.course-card {
  background: var(--accent-blue);
  border-radius: 12px;
  padding: 1.5rem;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.course-code {
  font-weight: bold;
  font-size: 1.2rem;
}

.semester-badge {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
}

.course-name {
  margin: 0 0 1.5rem 0;
  font-size: 1.3rem;
}

.course-stats {
  display: flex;
  gap: 2rem;
  margin-bottom: 1.5rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
}

.stat-icon {
  font-size: 1.5rem;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
}

.stat-label {
  font-size: 0.85rem;
  opacity: 0.9;
}

.course-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding-top: 1rem;
}

.btn-view {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  width: 100%;
  transition: all 0.3s ease;
}

.btn-view:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 1rem;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #999;
  line-height: 1;
}

.modal-header {
  padding: 2rem;
  border-bottom: 1px solid #eee;
}

.modal-header h2 {
  margin: 0 0 0.5rem 0;
}

.modal-header p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.modal-body {
  padding: 2rem;
}

/* Form Styles */
.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.form-label span {
  color: #e74c3c;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-hint {
  margin: 0.5rem 0 0 0;
  font-size: 0.85rem;
  color: #666;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  flex: 1;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.btn-secondary {
  background: #e0e0e0;
  color: #333;
}

.btn-secondary:hover {
  background: #d0d0d0;
}

/* Progress */
.upload-progress {
  margin-top: 1rem;
}

.progress-bar {
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
}

.upload-progress p {
  text-align: center;
  font-size: 0.9rem;
  color: #666;
  margin: 0;
}

/* Alerts */
.alert {
  padding: 1rem;
  border-radius: 8px;
  margin-top: 1rem;
}

.alert-success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.alert-error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

/* Loading & Empty States */
.loading-state,
.error-state,
.empty-state {
  text-align: center;
  padding: 3rem;
  background: white;
  border-radius: 12px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

.debug-info {
  margin-top: 1rem;
  font-size: 0.85rem;
  color: #666;
  font-family: monospace;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  color: #e74c3c;
  margin-bottom: 1rem;
}

@media (max-width: 768px) {
  .courses-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>