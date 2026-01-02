<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>My Profile</h1>
      </div>
    </div>

    <div class="container">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading profile...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <p class="error-message">{{ error }}</p>
        <button @click="loadUserData" class="btn btn-primary">Retry</button>
      </div>

      <!-- Profile Content -->
      <div v-else>
        <div class="profile-grid">
          <!-- Profile Card -->
          <div class="card">
            <div class="profile-avatar">
              <div class="avatar-circle">
                {{ userInitials }}
              </div>
              <div class="user-role-badge" :class="'role-' + userRole.toLowerCase()">
                {{ userRole }}
              </div>
            </div>
            
            <div class="profile-info">
              <h2>{{ userName }}</h2>
              <p class="user-email">{{ userEmail }}</p>
              
              <!-- Student Stats -->
              <div v-if="userRole === 'STUDENT'" class="user-stats">
                <div class="stat">
                  <span class="stat-value">{{ reputationPoints.toFixed(1) }}</span>
                  <span class="stat-label">Reputation Points</span>
                </div>
                <div class="stat">
                  <span class="stat-value">{{ uploadedDocs }}</span>
                  <span class="stat-label">Documents Shared</span>
                </div>
                <div class="stat">
                  <span class="stat-value">{{ activities }}</span>
                  <span class="stat-label">Activities</span>
                </div>
              </div>

              <!-- Teacher/Admin Stats -->
              <div v-else class="user-stats">
                <div class="stat">
                  <span class="stat-value">{{ uploadedDocs }}</span>
                  <span class="stat-label">Documents Shared</span>
                </div>
                <div class="stat">
                  <span class="stat-value">{{ activities }}</span>
                  <span class="stat-label">Activities Posted</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Account Settings -->
          <div class="card">
            <h3 class="card-title">Account Information</h3>
            
            <div class="info-section">
              <div class="info-item">
                <label class="info-label">User ID</label>
                <p class="info-value">{{ userId }}</p>
              </div>

              <div class="info-item">
                <label class="info-label">Username</label>
                <p class="info-value">{{ userName }}</p>
              </div>

              <div class="info-item">
                <label class="info-label">Email</label>
                <p class="info-value">{{ userEmail }}</p>
              </div>

              <div class="info-item">
                <label class="info-label">Account Type</label>
                <p class="info-value">
                  <span class="role-badge" :class="'role-' + userRole.toLowerCase()">
                    {{ userRole }}
                  </span>
                </p>
              </div>

              <div v-if="userRole === 'STUDENT'" class="info-item">
                <label class="info-label">Reputation Points</label>
                <p class="info-value">{{ reputationPoints.toFixed(2) }}</p>
              </div>
            </div>

            <div class="action-buttons">
              <button @click="logout" class="btn btn-secondary">
                Logout
              </button>
            </div>
          </div>
        </div>

        <!-- My Contributions -->
        <div class="card mt-3">
          <div class="card-header-with-action">
            <h3 class="card-title">My Recent Documents</h3>
            <router-link to="/upload" class="btn btn-primary btn-sm">
              Upload More Documents
            </router-link>
          </div>
          
          <div v-if="myDocuments.length === 0" class="empty-state">
            <div class="empty-icon">📄</div>
            <p>You haven't uploaded any documents yet.</p>
            <p class="empty-subtitle">Start sharing your knowledge with the community!</p>
            <router-link to="/upload" class="btn btn-primary">
              Upload Your First Document
            </router-link>
          </div>

          <div v-else class="contributions-list">
            <div class="contribution-item" v-for="doc in myDocuments" :key="doc.documentId">
              <div class="contribution-content">
                <h4>{{ doc.docTitle }}</h4>
                <p>{{ doc.docDescription || 'No description' }}</p>
                <div class="document-info-row">
                  <span class="contribution-date">
                    <b>Uploaded:</b> {{ formatDate(doc.uploadedAt) }}
                  </span>
                  <span class="course-name">
                    <b>Course:</b> {{ doc.courseName || 'Unknown' }}
                  </span>
                </div>
                <div class="document-meta">
                  <span class="meta-badge" :class="doc.isApproved ? 'approved' : 'pending'">
                    {{ doc.isApproved ? '✓ Approved' : '⏳ Pending' }}
                  </span>
                  <span class="meta-badge type">
                    {{ formatDocType(doc.documentType) }}
                  </span>
                </div>
              </div>
              <div class="contribution-stats">
                <div class="stat-row">
                  <span class="stat-number">{{ doc.downloadCount }}</span>
                  <span class="stat-text">downloads</span>
                </div>
                <div class="stat-row" v-if="doc.rating">
                  <span class="stat-icon">⭐</span>
                  <span class="stat-number">{{ doc.rating }}</span>
                  <span class="stat-text">({{ doc.totalRatings || 0 }})</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- My Activities (if any) -->
        <div v-if="myActivities.length > 0" class="card mt-3">
          <h3 class="card-title">My Activities</h3>
          
          <div class="contributions-list">
            <div class="contribution-item" v-for="activity in myActivities" :key="activity.activityId">
              <div class="contribution-icon"></div>
              <div class="contribution-content">
                <h4>{{ activity.title }}</h4>
                <p>{{ activity.description }}</p>
                <span class="contribution-date">
                  Created: {{ formatDate(activity.activityDate) }}
                </span>
                <div class="document-meta">
                  <span class="meta-badge type">{{ activity.activityType }}</span>
                </div>
              </div>
              <div class="contribution-stats">
                <span><b>{{ activity.attendance || 0 }}</b> interested</span>
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
  name: 'ProfilePage',
  data() {
    return {
      loading: true,
      error: null,
      userId: null,
      userName: '',
      userEmail: '',
      userRole: '',
      reputationPoints: 0,
      uploadedDocs: 0,
      activities: 0,
      myDocuments: [],
      myActivities: []
    }
  },
  computed: {
    userInitials() {
      return this.userName
        .split(' ')
        .map(n => n[0])
        .join('')
        .toUpperCase() || 'U'
    }
  },
  mounted() {
    this.loadUserData()
  },
  methods: {
    async loadUserData() {
      this.loading = true
      this.error = null

      try {
        const token = localStorage.getItem('token')
        
        // Verificar token primero
        if (!token) {
          console.log('No token found, redirecting to login')
          this.$router.push('/login')
          return
        }

        // Obtener info del usuario desde el backend
        const response = await axios.get('http://localhost:8080/api/auth/me', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })

        console.log('User data from backend:', response.data)

        // Guardar datos del usuario
        this.userId = response.data.userId
        this.userName = response.data.username
        this.userEmail = response.data.email || ''
        this.userRole = response.data.role

        // Cargar datos adicionales
        await this.loadUserDetails()
        await this.loadMyDocuments()
        await this.loadMyActivities()

      } catch (error) {
        console.error('Error loading profile:', error)
        
        if (error.response?.status === 401) {
          console.log('Token expired or invalid')
          localStorage.removeItem('token')
          localStorage.removeItem('edulink_user')
          this.$router.push('/login')
        } else {
          this.error = 'Failed to load profile. Please try again.'
        }
      } finally {
        this.loading = false
      }
    },

    async loadUserDetails() {
      try {
        const token = localStorage.getItem('token')
        
        const response = await axios.get(
          `http://localhost:8080/api/users/${this.userId}`,
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )

        if (this.userRole === 'STUDENT' && response.data.reputationPoints !== undefined) {
          this.reputationPoints = response.data.reputationPoints
        }

      } catch (error) {
        console.error('Error loading user details:', error)
      }
    },

    async loadMyDocuments() {
      try {
        const token = localStorage.getItem('token')
        
        const response = await axios.get(
          'http://localhost:8080/api/documents',
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )

        // Filtrar por uploaderId
        this.myDocuments = response.data.filter(doc => 
          doc.uploaderId === this.userId
        )
        
        // Formatear rating
        this.myDocuments = this.myDocuments.map(doc => ({
          ...doc,
          rating: doc.averageRating ? doc.averageRating.toFixed(1) : null
        }))
        
        this.uploadedDocs = this.myDocuments.length

        console.log('My documents loaded:', this.myDocuments.length)

      } catch (error) {
        console.error('Error loading documents:', error)
        this.myDocuments = []
        this.uploadedDocs = 0
      }
    },

    async loadMyActivities() {
      try {
        const token = localStorage.getItem('token')
        
        const response = await axios.get(
          'http://localhost:8080/api/activities',
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )

        this.myActivities = response.data.filter(
          activity => activity.organizerId === this.userId
        )
        this.activities = this.myActivities.length

        console.log('My activities loaded:', this.myActivities.length)

      } catch (error) {
        console.error('Error loading activities:', error)
        this.myActivities = []
        this.activities = 0
      }
    },

    formatDate(dateString) {
      if (!dateString) return 'Unknown'
      
      const date = new Date(dateString)
      const now = new Date()
      const diffTime = Math.abs(now - date)
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))

      if (diffDays === 0) return 'Today'
            
      return date.toLocaleDateString()
    },

    formatDocType(type) {
      const types = {
        'exam': 'Exam',
        'notes': 'Notes',
        'lab_report': 'Lab Report',
        'summary': 'Summary',
        'assignment': 'Assignment',
        'project': 'Project'
      }
      return types[type] || 'Document'
    },

    logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('edulink_user')
      
      window.dispatchEvent(new Event('storage'))
      
      this.$router.push('/login')
    }
  }
}
</script>







<style scoped>
.loading-state {
  text-align: center;
  padding: 4rem 2rem;
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

.error-state {
  text-align: center;
  padding: 4rem 2rem;
}

.error-message {
  color: #e74c3c;
  font-size: 1.125rem;
  margin-bottom: 1rem;
}

.profile-grid {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

.profile-avatar {
  text-align: center;
  padding: 2rem 0;
}

.avatar-circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  font-weight: 700;
  margin: 0 auto 1rem;
}

.user-role-badge {
  display: inline-block;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 600;
  margin-top: 1rem;
}

.user-role-badge.role-student {
  background: #e3f2fd;
  color: #1976d2;
}

.user-role-badge.role-teacher {
  background: #f3e5f5;
  color: #7b1fa2;
}

.user-role-badge.role-admin {
  background: #fce4ec;
  color: #c2185b;
}

.profile-info {
  text-align: center;
  padding: 0 2rem 2rem;
}

.profile-info h2 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.user-email {
  color: #7f8c8d;
  margin-bottom: 2rem;
  word-break: break-word;
}

.user-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  padding-top: 2rem;
  border-top: 1px solid #e9ecef;
}

.stat {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 2rem;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  display: block;
  font-size: 0.875rem;
  color: #7f8c8d;
  margin-top: 0.25rem;
}

.info-section {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.info-label {
  font-weight: 600;
  color: #7f8c8d;
  font-size: 0.875rem;
  text-transform: uppercase;
}

.info-value {
  font-size: 1.125rem;
  color: #2c3e50;
  margin: 0;
}

.role-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.875rem;
  font-weight: 600;
}

.role-badge.role-student {
  background: #e3f2fd;
  color: #1976d2;
}

.role-badge.role-teacher {
  background: #f3e5f5;
  color: #7b1fa2;
}

.role-badge.role-admin {
  background: #fce4ec;
  color: #c2185b;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e9ecef;
}

.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  color: #7f8c8d;
}

.empty-state p {
  margin-bottom: 0.5rem;
  font-size: 1.125rem;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
  opacity: 0.6;
}

.empty-subtitle {
  font-size: 0.875rem !important;
  color: #95a5a6;
  margin-bottom: 1.5rem !important;
}

.card-header-with-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.document-info-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 0.5rem;
  flex-wrap: wrap;
}

.course-name {
  font-size: 0.875rem;
  color: #667eea;
  font-weight: 500;
}

.contributions-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.contribution-item {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.contribution-item:hover {
  background: white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.contribution-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.contribution-content {
  flex: 1;
}

.contribution-content h4 {
  font-size: 1.125rem;
  margin-bottom: 0.25rem;
  color: #2c3e50;
}

.contribution-content p {
  color: #7f8c8d;
  font-size: 0.875rem;
  margin-bottom: 0.5rem;
}

.contribution-date {
  font-size: 0.875rem;
  color: #95a5a6;
}

.document-meta {
  margin-top: 0.5rem;
}

.meta-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  margin-right: 0.5rem;
}

.meta-badge.approved {
  background: #d4edda;
  color: #155724;
}

.meta-badge.pending {
  background: #fff3cd;
  color: #856404;
}

.meta-badge.type {
  background: #e3f2fd;
  color: #1976d2;
}

.contribution-stats {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  text-align: right;
  color: #7f8c8d;
  font-size: 0.875rem;
  flex-shrink: 0;
  min-width: 120px;
}

.stat-row {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.25rem;
}

.stat-icon {
  font-size: 1rem;
}

.stat-number {
  font-weight: 600;
  color: #2c3e50;
}

.stat-text {
  color: #95a5a6;
  font-size: 0.8125rem;
}

.mt-3 {
  margin-top: 2rem;
}

@media (max-width: 768px) {
  .profile-grid {
    grid-template-columns: 1fr;
  }

  .user-stats {
    grid-template-columns: repeat(2, 1fr);
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>