<template>
  <div class="admin-page">
    <!-- Header -->
    <div class="page-header">
      <div class="container">
        <h1>User Management</h1>
        <p class="subtitle">View and manage system users</p>
      </div>
    </div>

    <div class="container">
      <!-- Stats -->
      <div class="stats">
        <div class="stat">
          <div class="stat-number">{{ stats.totalUsers }}</div>
          <div class="stat-label">Total Users</div>
        </div>
        <div class="stat">
          <div class="stat-number">{{ stats.totalStudents }}</div>
          <div class="stat-label">Students</div>
        </div>
        <div class="stat">
          <div class="stat-number">{{ stats.totalTeachers }}</div>
          <div class="stat-label">Teachers</div>
        </div>
        <div class="stat">
          <div class="stat-number">{{ stats.totalAdmins }}</div>
          <div class="stat-label">Admins</div>
        </div>
      </div>

      <!-- Filters -->
      <div class="filters">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search users..."
          class="search-input"
          @input="searchUsers"
        />
        
        <div class="role-filters">
          <button 
            v-for="role in ['ALL', 'STUDENT', 'TEACHER', 'ADMIN']" 
            :key="role"
            :class="['role-filter', { active: selectedRole === role }]"
            @click="filterByRole(role === 'ALL' ? null : role)"
          >
            {{ role }}
          </button>
        </div>
      </div>

      <!-- Users Table -->
      <div class="table-container">
        <table class="users-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Documents</th>
              <th>Joined</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in filteredUsers" :key="user.userId">
              <td>{{ user.userId }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.email }}</td>
              <td>
                <span :class="['role-badge', user.role.toLowerCase()]">
                  {{ user.role }}
                </span>
              </td>
              <td>{{ user.documentsUploaded || 0 }}</td>
              <td>{{ formatDate(user.createdAt) }}</td>
              <td>
                <div class="actions">
                  <button @click="editUser(user)" class="action-btn edit" title="Edit">
                    Edit
                  </button>
                  <button @click="resetPassword(user)" class="action-btn reset" title="Reset Password">
                    Reset
                  </button>
                  <button 
                    @click="deleteUser(user)" 
                    class="action-btn delete" 
                    title="Delete"
                    :disabled="isCurrentUser(user)"
                  >
                    Delete
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div v-if="filteredUsers.length === 0" class="empty-table">
          <p>No users found</p>
        </div>
      </div>
    </div>

    <!-- Edit Modal -->
    <div v-if="editingUser" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Edit User</h3>
          <button @click="cancelEdit" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Username</label>
            <input v-model="editForm.username" class="form-input">
          </div>
          <div class="form-group">
            <label>Email</label>
            <input v-model="editForm.email" type="email" class="form-input">
          </div>
          <div class="form-group">
            <label>Role</label>
            <select v-model="editForm.role" class="form-select">
              <option value="STUDENT">Student</option>
              <option value="TEACHER">Teacher</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="cancelEdit" class="btn btn-outline">Cancel</button>
          <button @click="saveUser" class="btn btn-primary">Save</button>
        </div>
      </div>
    </div>

    <!-- Reset Password Modal -->
    <div v-if="resettingUser" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>Reset Password</h3>
          <button @click="cancelReset" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <p>Reset password for <strong>{{ resettingUser.username }}</strong></p>
          <div class="form-group">
            <label>New Password</label>
            <input v-model="newPassword" type="password" class="form-input">
          </div>
          <div class="form-group">
            <label>Confirm Password</label>
            <input v-model="confirmPassword" type="password" class="form-input">
          </div>
        </div>
        <div class="modal-footer">
          <button @click="cancelReset" class="btn btn-outline">Cancel</button>
          <button @click="confirmReset" class="btn btn-primary">Reset</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ManageUsersPage',
  data() {
    return {
      loading: false,
      users: [],
      filteredUsers: [],
      stats: {
        totalUsers: 0,
        totalStudents: 0,
        totalTeachers: 0,
        totalAdmins: 0
      },
      searchQuery: '',
      selectedRole: null,
      
      editingUser: null,
      editForm: {
        username: '',
        email: '',
        role: ''
      },
      
      resettingUser: null,
      newPassword: '',
      confirmPassword: ''
    }
  },
  mounted() {
    this.checkAdminAccess()
    this.loadUsers()
    this.loadStats()
  },
  methods: {
    checkAdminAccess() {
      const user = JSON.parse(localStorage.getItem('edulink_user') || '{}')
      if (user.role !== 'ADMIN') {
        this.$router.push('/')
      }
    },

    async loadUsers() {
      this.loading = true
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get(
          'http://localhost:8080/api/admin/users',
          { headers: { 'Authorization': `Bearer ${token}` } }
        )
        this.users = response.data
        this.filteredUsers = response.data

        this.users.sort((a, b) => a.userId - b.userId)

        
      } catch (error) {
        console.error('Error loading users:', error)
        alert('Failed to load users')
      } finally {
        this.loading = false
      }
    },

    async loadStats() {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get(
          'http://localhost:8080/api/admin/users/stats',
          { headers: { 'Authorization': `Bearer ${token}` } }
        )
        this.stats = response.data
      } catch (error) {
        console.error('Error loading stats:', error)
      }
    },

    searchUsers() {
      if (!this.searchQuery.trim()) {
        this.applyFilters()
        return
      }
      
      const query = this.searchQuery.toLowerCase()
      this.filteredUsers = this.users.filter(user =>
        user.username.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query)
      )
      
      this.applyRoleFilter()
    },

    filterByRole(role) {
      this.selectedRole = role
      this.applyFilters()
    },

    applyFilters() {
      let result = [...this.users]

      if (this.searchQuery.trim()) {
        const query = this.searchQuery.toLowerCase()
        result = result.filter(user =>
          user.username.toLowerCase().includes(query) ||
          user.email.toLowerCase().includes(query)
        )
      }

      if (this.selectedRole) {
        result = result.filter(user => user.role === this.selectedRole)
      }

      this.filteredUsers = result
    },

    applyRoleFilter() {
      if (this.selectedRole) {
        this.filteredUsers = this.filteredUsers.filter(
          user => user.role === this.selectedRole
        )
      }
    },

    editUser(user) {
      this.editingUser = user
      this.editForm = {
        username: user.username,
        email: user.email,
        role: user.role
      }
    },

    cancelEdit() {
      this.editingUser = null
      this.editForm = { username: '', email: '', role: '' }
    },

    async saveUser() {
      try {
        const token = localStorage.getItem('token')
        await axios.put(
          `http://localhost:8080/api/admin/users/${this.editingUser.userId}`,
          this.editForm,
          { headers: { 'Authorization': `Bearer ${token}` } }
        )
        await this.loadUsers()
        await this.loadStats()
        this.cancelEdit()
      } catch (error) {
        console.error('Error updating user:', error)
        alert('Failed to update user')
      }
    },

    resetPassword(user) {
      this.resettingUser = user
      this.newPassword = ''
      this.confirmPassword = ''
    },

    cancelReset() {
      this.resettingUser = null
      this.newPassword = ''
      this.confirmPassword = ''
    },

    async confirmReset() {
      if (!this.newPassword || !this.confirmPassword) {
        alert('Please enter and confirm password')
        return
      }

      if (this.newPassword !== this.confirmPassword) {
        alert('Passwords do not match')
        return
      }

      try {
        const token = localStorage.getItem('token')
        await axios.post(
          `http://localhost:8080/api/admin/users/${this.resettingUser.userId}/reset-password`,
          { newPassword: this.newPassword },
          { headers: { 'Authorization': `Bearer ${token}` } }
        )
        this.cancelReset()
      } catch (error) {
        console.error('Error resetting password:', error)
        alert('Failed to reset password')
      }
    },

    deleteUser(user) {
      if (this.isCurrentUser(user)) {
        alert('Cannot delete your own account')
        return
      }

      if (!confirm(`Delete user ${user.username}?`)) return
      
      this.performDelete(user.userId)
    },

    async performDelete(userId) {
      try {
        const token = localStorage.getItem('token')
        await axios.delete(
          `http://localhost:8080/api/admin/users/${userId}`,
          { headers: { 'Authorization': `Bearer ${token}` } }
        )
        await this.loadUsers()
        await this.loadStats()
      } catch (error) {
        console.error('Error deleting user:', error)
        alert('Failed to delete user')
      }
    },

    isCurrentUser(user) {
      const currentUser = JSON.parse(localStorage.getItem('edulink_user') || '{}')
      return user.username === currentUser.username
    },

    formatDate(dateString) {
      if (!dateString) return 'Unknown'
      const date = new Date(dateString)
      return date.toLocaleDateString()
    }
  }
}
</script>

<style scoped>
/* Reuse styles from ValidateDocumentsPage */

.admin-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  background: #a30202;
  color: white;
  padding: 2rem 0;
  margin-bottom: 2rem;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.subtitle {
  color: rgba(255, 255, 255, 0.8);
  margin-top: 0.5rem;
}

.stats {
  display: flex;
  gap: 2rem;
  margin-bottom: 3rem;
  justify-content: center;
  flex-wrap: wrap;
}

.stat {
  background: white;
  padding: 1.5rem 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  text-align: center;
  min-width: 120px;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.filters {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
}

.search-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  margin-bottom: 1rem;
}

.search-input:focus {
  outline: none;
  border-color: #3498db;
}

.role-filters {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.role-filter {
  padding: 0.5rem 1rem;
  border: 2px solid #ecf0f1;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s;
}

.role-filter:hover {
  background: #f8f9fa;
}

.role-filter.active {
  background: #3498db;
  color: white;
  border-color: #3498db;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  overflow-x: auto;
}

.users-table {
  width: 100%;
  border-collapse: collapse;
}

.users-table th {
  background: #f8f9fa;
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #dee2e6;
}

.users-table td {
  padding: 1rem;
  border-bottom: 1px solid #e9ecef;
}

.users-table tr:hover {
  background: #f8f9fa;
}

.role-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: bold;
  text-transform: uppercase;
}

.role-badge.student {
  background: #e3f2fd;
  color: #1976d2;
}

.role-badge.teacher {
  background: #f3e5f5;
  color: #7b1fa2;
}

.role-badge.admin {
  background: #ffebee;
  color: #c62828;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  padding: 0.25rem 0.75rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.edit {
  border-color: #3498db;
  color: #3498db;
}

.action-btn.edit:hover {
  background: #3498db;
  color: white;
}

.action-btn.reset {
  border-color: #f39c12;
  color: #f39c12;
}

.action-btn.reset:hover {
  background: #f39c12;
  color: white;
}

.action-btn.delete {
  border-color: #e74c3c;
  color: #e74c3c;
}

.action-btn.delete:hover:not(:disabled) {
  background: #e74c3c;
  color: white;
}

.action-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.empty-table {
  padding: 3rem;
  text-align: center;
  color: #7f8c8d;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #dee2e6;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #6c757d;
}

.close-btn:hover {
  color: #343a40;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #495057;
}

.form-input,
.form-select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #3498db;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  padding: 1.5rem;
  border-top: 1px solid #dee2e6;
}

.btn {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outline {
  background: white;
  border: 1px solid #dee2e6;
  color: #495057;
}

.btn-outline:hover {
  background: #f8f9fa;
}

.btn-primary {
  background: #3498db;
  color: white;
}

.btn-primary:hover {
  background: #2980b9;
}

@media (max-width: 768px) {
  .stats {
    flex-direction: column;
    align-items: center;
  }
  
  .stat {
    width: 100%;
  }
  
  .actions {
    flex-direction: column;
  }
  
  .users-table {
    min-width: 700px;
  }
}
</style>