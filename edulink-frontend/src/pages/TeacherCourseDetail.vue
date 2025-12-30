<template>
  <div class="course-detail">
    <!-- Header -->
    <div class="page-header">
      <div class="container">
        <button @click="goBack" class="btn-back">← Back to Dashboard</button>
        <h1>{{ course?.courseName || 'Loading...' }}</h1>
        <p class="subtitle">{{ course?.code }} • Semester {{ course?.semester }}</p>
      </div>
    </div>

    <div class="container">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading course statistics...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <p class="error-message">{{ error }}</p>
        <button @click="loadStatistics" class="btn btn-primary">Retry</button>
      </div>

      <!-- Content -->
      <div v-else-if="stats">
        <!-- Statistics Overview -->
        <div class="stats-grid">
          <div class="stat-card documents">
            <div class="stat-icon"></div>
            <div class="stat-content">
              <h3>{{ stats.totalDocuments }}</h3>
              <p>Total Documents</p>
              <div class="stat-breakdown">
                <span>{{ stats.officialDocuments }} official</span>
                <span>{{ stats.studentDocuments }} from students</span>
              </div>
            </div>
          </div>

          <div class="stat-card downloads">
            <div class="stat-icon"></div>
            <div class="stat-content">
              <h3>{{ stats.totalDownloads }}</h3>
              <p>Total Downloads</p>
            </div>
          </div>

          <div class="stat-card rating">
            <div class="stat-icon"></div>
            <div class="stat-content">
              <h3>{{ stats.averageRating.toFixed(1) }}</h3>
              <p>Average Rating</p>
              <div class="stars">
                <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= Math.round(stats.averageRating) }">
                  ★
                </span>
              </div>
            </div>
          </div>

          <div class="stat-card questions">
            <div class="stat-icon"></div>
            <div class="stat-content">
              <h3>{{ stats.unansweredQuestions }}</h3>
              <p>Unanswered Questions</p>
              <router-link to="/forums" class="stat-link">Go to Forum →</router-link>
            </div>
          </div>
        </div>

        <!-- Top Documents -->
        <div class="section">
          <div class="section-header">
            <h2>Most Downloaded Documents</h2>
          </div>

          <div v-if="stats.topDocuments && stats.topDocuments.length > 0" class="top-documents">
            <div 
              v-for="(doc, index) in stats.topDocuments" 
              :key="doc.documentId"
              class="document-item"
            >
              <div class="rank">{{ index + 1 }}</div>
              <div class="document-info">
                <h4>{{ doc.title }}</h4>
                <div class="document-stats">
                  <span class="stat">{{ doc.downloads }} downloads</span>
                  <span class="stat">⭐ {{ doc.rating.toFixed(1) }} rating</span>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="empty-state">
            <p>No documents uploaded yet</p>
            <button @click="goToUpload" class="btn btn-primary">Upload First Material</button>
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="actions-section">
          <button @click="goToUpload" class="btn btn-primary btn-large">
            Upload Material for this Course
          </button>
          <router-link :to="`/search?course=${courseId}`" class="btn btn-secondary btn-large">
            View All Course Materials
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'TeacherCourseDetail',
  data() {
    return {
      loading: true,
      error: null,
      course: null,
      stats: null
    }
  },
  computed: {
    courseId() {
      return this.$route.params.courseId
    },
    isTeacher() {
      const user = JSON.parse(localStorage.getItem('edulink_user') || '{}')
      return user.role === 'TEACHER'
    }
  },
  mounted() {
    if (!this.isTeacher) {
      this.$router.push('/')
      return
    }
    this.loadStatistics()
  },
  methods: {
    async loadStatistics() {
      this.loading = true
      this.error = null

      try {
        const token = localStorage.getItem('token')
        
        const response = await axios.get(
          `http://localhost:8080/api/teacher/courses/${this.courseId}/statistics`,
          {
            headers: { 'Authorization': `Bearer ${token}` }
          }
        )

        this.stats = response.data
        this.course = {
          courseName: response.data.courseName,
          code: response.data.courseCode,
          semester: null 
        }

      } catch (error) {
        console.error('Error loading statistics:', error)
        if (error.response?.status === 403) {
          this.error = 'Access denied. This course is not assigned to you.'
        } else {
          this.error = 'Failed to load course statistics.'
        }
      } finally {
        this.loading = false
      }
    },

    goBack() {
      this.$router.push('/teacher/dashboard')
    },

    goToUpload() {
      this.$router.push({
        name: 'TeacherUploadMaterial',
        query: { courseId: this.courseId }
      })
    }
  }
}
</script>

<style scoped>
.course-detail {
  min-height: 100vh;
  background: whitesmoke;
  padding-bottom: 3rem;
}

.page-header {
  background: rgb(95, 179, 84);
  backdrop-filter: blur(10px);
  padding: 2rem 0;
  margin-bottom: 2rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.btn-back {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: rgba(255, 255, 255, 0.3);
}

.page-header h1 {
  color: white;
  margin: 0.5rem 0;
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

/* Statistics Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}


.stat-icon {
  font-size: 3rem;
}

.stat-content {
  flex: 1;
}

.stat-content h3 {
  margin: 0 0 0.25rem 0;
  font-size: 2.5rem;
  color: #333;
}

.stat-content p {
  margin: 0;
  color: #666;
  font-size: 0.9rem;
}

.stat-breakdown {
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #999;
}

.stat-breakdown span {
  display: block;
}

.stars {
  margin-top: 0.5rem;
}

.star {
  color: #ddd;
  font-size: 1.2rem;
}

.star.filled {
  color: #ffc107;
}

.stat-link {
  display: inline-block;
  margin-top: 0.5rem;
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.9rem;
}



/* Section */
.section {
  background: white;
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
  font-size: 1.5rem;
}

/* Top Documents */
.top-documents {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.document-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.5rem;
  background: #f5f7fa;
  border-radius: 8px;
  transition: none !important;
  transform: none !important;
  box-shadow: none;
}

.document-item:hover {
  transform: none !important;
  box-shadow: none !important;
  background: #f5f7fa !important;
}




.rank {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
  min-width: 50px;
  text-align: center;
}

.document-info {
  flex: 1;
}

.document-info h4 {
  margin: 0 0 0.5rem 0;
  color: #333;
}

.document-stats {
  display: flex;
  gap: 1.5rem;
  font-size: 0.9rem;
  color: #666;
}

.document-stats .stat {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

/* Actions Section */
.actions-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  text-decoration: none;
  display: inline-block;
}

.btn-large {
  padding: 1.25rem 2rem;
  font-size: 1.1rem;
}

.btn-primary {
  background: var(--accent-blue);
  color: white;
}



.btn-secondary {
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
}

.btn-secondary:hover {
  background: var(--accent-blue);
  color: white;
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

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  color: #e74c3c;
  margin-bottom: 1rem;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .actions-section {
    grid-template-columns: 1fr;
  }
}
</style>