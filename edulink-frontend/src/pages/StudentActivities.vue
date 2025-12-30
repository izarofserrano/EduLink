<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>Student Activities</h1>
      </div>
    </div>

    <div class="container">
      <div class="card mb-3">
        <h2 class="card-title">Off-Campus Activities</h2>
        
        <button 
          v-if="isLoggedIn"
          class="btn btn-primary mt-2" 
          @click="showPostActivity = true"
        >
          + Post Activity
        </button>
        <div v-else class="login-prompt">
          <p>🔒 Please <router-link to="/login" class="link-primary">login</router-link> to post activities</p>
        </div>
      </div>

      <!-- Filters Section -->
      <div class="filters-section">
        <div class="filter-group">
          <label class="filter-label">Activity Type</label>
          <select class="filter-select" v-model="filters.activityType" @change="applyFilters">
            <option value="">All Types</option>
            <option value="CLUB">Clubs & Music</option>
            <option value="SPORT">Sport</option>
            <option value="STUDY_SESSION">Study Session</option>
            <option value="EVENT">Housing/Events</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">Date Filter</label>
          <select class="filter-select" v-model="filters.dateRange" @change="applyFilters">
            <option value="">All Dates</option>
            <option value="today">Today</option>
            <option value="week">This Week</option>
            <option value="month">This Month</option>
            <option value="upcoming">Upcoming (Future)</option>
            <option value="past">Past</option>
          </select>
        </div>

        <button class="btn-clear-filters" @click="clearFilters">
          Clear Filters
        </button>
      </div>

      <!-- Results Summary -->
      <div class="results-summary">
        <p>Found <strong>{{ filteredActivities.length }}</strong> activit{{ filteredActivities.length !== 1 ? 'ies' : 'y' }}</p>
      </div>

      <!-- Post Activity Form -->
      <div v-if="showPostActivity && isLoggedIn" class="card">
        <h3 class="card-title">Post New Activity</h3>
        <form @submit.prevent="postActivity">
          <div class="form-group">
            <label class="form-label">Title <span>*</span></label>
            <input 
              type="text" 
              class="form-input"
              placeholder="e.g., Rock Band Looking for Drummer"
              v-model="newActivity.title"
              required
            />
          </div>
          
          <div class="form-group">
            <label class="form-label">Description <span>*</span></label>
            <textarea 
              class="form-textarea"
              placeholder="Describe the activity..."
              v-model="newActivity.description"
              rows="4"
              required
            ></textarea>
          </div>

          <div class="form-group">
            <label class="form-label">Activity Type <span>*</span></label>
            <select class="form-select" v-model="newActivity.type" required>
              <option value="">Select type</option>
              <option value="CLUB">Clubs & Music</option>
              <option value="SPORT">Sport</option>
              <option value="STUDY_SESSION">Study Session</option>
              <option value="EVENT">Housing/Events</option>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Activity Date <span>*</span></label>
            <input 
              type="date" 
              class="form-input"
              v-model="newActivity.date"
              :min="today"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">Location</label>
            <input 
              type="text" 
              class="form-input"
              placeholder="e.g., City Park, Library Room 203"
              v-model="newActivity.location"
            />
          </div>

          <div class="organizer-info">
            <span class="info-label">Organizer:</span>
            <span class="info-value">{{ currentUser?.username || 'You' }}</span>
          </div>

          <div style="display: flex; gap: 1rem; margin-top: 1rem;">
            <button type="submit" class="btn btn-primary">Post Activity</button>
            <button type="button" class="btn btn-secondary" @click="cancelPostActivity">
              Cancel
            </button>
          </div>
        </form>
      </div>

      <!-- Activities List -->
      <div class="mt-3">
        <div 
          v-for="activity in filteredActivities" 
          :key="activity.activityId"
          class="activity-card"
        >
          <div class="activity-header">
            <div>
              <div class="activity-type" :style="getTypeStyle(activity.activityType)">
                {{ getTypeName(activity.activityType) }}
              </div>
              <h3 class="activity-title">{{ activity.title }}</h3>
            </div>
            <div class="activity-date">{{ formatDate(activity.activityDate) }}</div>
          </div>
          
          <p class="activity-description">{{ activity.description }}</p>
          
          <div class="activity-meta">
            <div class="meta-row">
              <span class="meta-item">
                <span class="icon">📍</span>
                {{ activity.location || 'Location TBD' }}
              </span>
              <span class="meta-item">
                <span class="icon"></span>
                Organized by {{ activity.organizerUsername || 'Unknown' }}
              </span>
            </div>
          </div>

          <div class="activity-footer">
            <div class="interest-count">
              <span class="icon">❤️</span>
              <span>{{ activity.attendance || 0 }} interested</span>
            </div>
            
            <button 
              v-if="isLoggedIn"
              class="btn-interested"
              :class="{ active: isInterested(activity.activityId) }"
              @click="toggleInterested(activity)"
            >
              {{ isInterested(activity.activityId) ? '✓ Interested' : '+ Mark as Interested' }}
            </button>
            <div v-else class="login-note">
              Login to mark as interested
            </div>
          </div>
        </div>

        <div v-if="filteredActivities.length === 0" class="empty-state">
          <div class="empty-icon">🎯</div>
          <p>No activities found matching your filters.</p>
          <button 
            v-if="isLoggedIn" 
            class="btn btn-primary" 
            @click="showPostActivity = true"
          >
            Post the first activity!
          </button>
          <button 
            v-else
            class="btn btn-secondary" 
            @click="clearFilters"
          >
            Clear Filters
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'StudentActivities',
  data() {
    return {
      activities: [],
      filteredActivities: [],
      showPostActivity: false,
      newActivity: {
        title: '',
        description: '',
        type: '',
        date: '',
        location: ''
      },
      filters: {
        activityType: '',
        dateRange: ''
      },
      interestedActivities: [] // Store activity IDs user is interested in
    }
  },
  computed: {
    isLoggedIn() {
      const token = localStorage.getItem('token')
      return !!token
    },
    currentUser() {
      const user = localStorage.getItem('edulink_user')
      return user ? JSON.parse(user) : null
    },
    today() {
      return new Date().toISOString().split('T')[0]
    }
  },
  mounted() {
    this.loadActivities()
    this.loadInterestedActivities()
  },
  methods: {
    async loadActivities() {
      try {
        const response = await axios.get('http://localhost:8080/api/activities')
        this.activities = response.data
        this.filteredActivities = response.data
        this.applyFilters()
      } catch (error) {
        console.error('Error loading activities:', error)
      }
    },

    loadInterestedActivities() {
      // Load from localStorage
      const stored = localStorage.getItem('interested_activities')
      this.interestedActivities = stored ? JSON.parse(stored) : []
    },

    saveInterestedActivities() {
      // Save to localStorage
      localStorage.setItem('interested_activities', JSON.stringify(this.interestedActivities))
    },

    applyFilters() {
      let results = [...this.activities]

      // Filter by activity type
      if (this.filters.activityType) {
        results = results.filter(activity => 
          activity.activityType === this.filters.activityType
        )
      }

      // Filter by date range
      if (this.filters.dateRange) {
        const now = new Date()
        now.setHours(0, 0, 0, 0)

        results = results.filter(activity => {
          const activityDate = new Date(activity.activityDate)
          activityDate.setHours(0, 0, 0, 0)

          switch (this.filters.dateRange) {
            case 'today':
              return activityDate.getTime() === now.getTime()
            
            case 'week': {
              const weekFromNow = new Date(now)
              weekFromNow.setDate(weekFromNow.getDate() + 7)
              return activityDate >= now && activityDate <= weekFromNow
            }
            
            case 'month': {
              const monthFromNow = new Date(now)
              monthFromNow.setMonth(monthFromNow.getMonth() + 1)
              return activityDate >= now && activityDate <= monthFromNow
            }
            
            case 'upcoming':
              return activityDate >= now
            
            case 'past':
              return activityDate < now
            
            default:
              return true
          }
        })
      }

      this.filteredActivities = results
    },

    clearFilters() {
      this.filters = {
        activityType: '',
        dateRange: ''
      }
      this.filteredActivities = this.activities
    },

    async postActivity() {
    if (!this.isLoggedIn) {
      alert('Please login to post activities')
      return
    }

    try {
      const token = localStorage.getItem('token')

      // ✅ FORMATEAR LA FECHA SIN MILISEGUNDOS NI ZONA HORARIA
      const activityDate = new Date(this.newActivity.date)
      activityDate.setHours(12, 0, 0, 0) // Noon to avoid timezone issues
      
      // Formato: "2026-01-18T12:00:00"
      const dateString = activityDate.toISOString().substring(0, 19)

      const activityData = {
        title: this.newActivity.title,
        description: this.newActivity.description,
        activityType: this.newActivity.type,
        activityDate: dateString,  // ✅ Sin .000Z
        location: this.newActivity.location || 'TBD'
      }

      console.log('Sending activity data:', activityData)

      const response = await axios.post(
        'http://localhost:8080/api/activities', 
        activityData, 
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        }
      )
      
      console.log('Activity created:', response.data)
      
      await this.loadActivities()
      
      this.newActivity = { title: '', description: '', type: '', date: '', location: '' }
      this.showPostActivity = false
      
      alert('✅ Activity posted successfully!')
      
    } catch (error) {
      console.error('Error posting activity:', error)
      console.error('Error response:', error.response?.data)
      
      if (error.response?.status === 401) {
        alert('❌ Session expired. Please login again.')
        this.$router.push('/login')
      } else {
        const errorMsg = error.response?.data?.error || 'Failed to post activity'
        alert(`❌ ${errorMsg}`)
      }
    }
  },

    cancelPostActivity() {
      this.newActivity = { title: '', description: '', type: '', date: '', location: '' }
      this.showPostActivity = false
    },

    isInterested(activityId) {
      return this.interestedActivities.includes(activityId)
    },

    async toggleInterested(activity) {
      if (!this.isLoggedIn) {
        alert('Please login to mark activities as interested')
        return
      }

      const activityId = activity.activityId
      const index = this.interestedActivities.indexOf(activityId)

      if (index === -1) {
        // Mark as interested
        this.interestedActivities.push(activityId)
        activity.attendance = (activity.attendance || 0) + 1

        // In real app, this would POST to API
        // await axios.post(`/api/activities/${activityId}/interested`)
      } else {
        // Remove interest
        this.interestedActivities.splice(index, 1)
        activity.attendance = Math.max(0, (activity.attendance || 0) - 1)

        // In real app, this would DELETE from API
        // await axios.delete(`/api/activities/${activityId}/interested`)
      }

      this.saveInterestedActivities()
    },

    getTypeName(type) {
      const types = {
        'CLUB': 'Clubs & Music',
        'SPORT': 'Sport',
        'STUDY_SESSION': 'Study Session',
        'EVENT': 'Housing/Events'
      }
      return types[type] || type
    },

    getTypeStyle(type) {
      const styles = {
        'CLUB': 'background: #e3f2fd; color: #1976d2;',
        'SPORT': 'background: #e8f5e9; color: #388e3c;',
        'STUDY_SESSION': 'background: #fff3e0; color: #f57c00;',
        'EVENT': 'background: #f3e5f5; color: #7b1fa2;'
      }
      return styles[type] || 'background: #e0e0e0; color: #424242;'
    },

    formatDate(date) {
      if (!date) return 'Date TBD'
      
      const activityDate = new Date(date)
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      activityDate.setHours(0, 0, 0, 0)

      const diffTime = activityDate - today
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

      if (diffDays === 0) return 'Today'
      if (diffDays === 1) return 'Tomorrow'
      if (diffDays === -1) return 'Yesterday'
      if (diffDays > 0 && diffDays <= 7) return `In ${diffDays} days`
      if (diffDays < 0 && diffDays >= -7) return `${Math.abs(diffDays)} days ago`

      return new Date(date).toLocaleDateString('en-US', { 
        month: 'short', 
        day: 'numeric', 
        year: 'numeric' 
      })
    }
  }
}
</script>

<style scoped>
.mb-3 {
  margin-bottom: 2rem;
}

.mt-2 {
  margin-top: 1rem;
}

.mt-3 {
  margin-top: 2rem;
}

.login-prompt {
  margin-top: 1rem;
  padding: 1rem;
  background: #fff3cd;
  border: 1px solid #ffc107;
  border-radius: 6px;
  text-align: center;
}

.login-prompt p {
  margin: 0;
  color: #856404;
}

.link-primary {
  color: var(--accent-blue);
  font-weight: 600;
  text-decoration: none;
}

.link-primary:hover {
  text-decoration: underline;
}

/* Filters Section */
.filters-section {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  align-items: end;
}

.filter-group {
  display: flex;
  flex-direction: column;
}

.filter-label {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.5rem;
  font-size: 0.875rem;
}

.filter-select {
  padding: 0.75rem;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 0.9375rem;
  color: #2c3e50;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-select:focus {
  outline: none;
  border-color: var(--accent-blue);
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
}

.btn-clear-filters {
  padding: 0.75rem 1.5rem;
  background: #e9ecef;
  color: #2c3e50;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  align-self: end;
}

.btn-clear-filters:hover {
  background: #dee2e6;
}

/* Results Summary */
.results-summary {
  margin-bottom: 1rem;
  color: #7f8c8d;
}

.results-summary strong {
  color: var(--accent-blue);
}

/* Organizer Info */
.organizer-info {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  display: flex;
  gap: 1rem;
  align-items: center;
  margin-top: 1rem;
}

.info-label {
  font-weight: 600;
  color: #7f8c8d;
}

.info-value {
  color: #2c3e50;
  font-weight: 600;
}

/* Activity Card */
.activity-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 1rem;
  transition: all 0.3s;
}

.activity-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 1rem;
}

.activity-type {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  margin-bottom: 0.5rem;
}

.activity-title {
  font-size: 1.25rem;
  color: #2c3e50;
  margin: 0;
}

.activity-date {
  font-size: 0.875rem;
  color: #7f8c8d;
  white-space: nowrap;
}

.activity-description {
  color: #7f8c8d;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.activity-meta {
  margin-bottom: 1rem;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  font-size: 0.875rem;
  color: #7f8c8d;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.icon {
  font-size: 1rem;
}

.activity-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid #e9ecef;
}

.interest-count {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #7f8c8d;
  font-weight: 600;
}

.btn-interested {
  padding: 0.5rem 1.5rem;
  border: 2px solid var(--accent-blue);
  background: white;
  color: var(--accent-blue);
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-interested:hover {
  background: var(--accent-blue);
  color: white;
}

.btn-interested.active {
  background: var(--accent-blue);
  color: white;
}

.login-note {
  font-size: 0.875rem;
  color: #7f8c8d;
  font-style: italic;
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

/* Responsive */
@media (max-width: 768px) {
  .filters-section {
    grid-template-columns: 1fr;
  }

  .activity-header {
    flex-direction: column;
    gap: 0.5rem;
  }

  .activity-footer {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .btn-interested {
    width: 100%;
  }

  .meta-row {
    flex-direction: column;
    gap: 0.5rem;
  }
}
</style>


