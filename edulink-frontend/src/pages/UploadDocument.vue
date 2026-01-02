<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>Upload Document</h1>
      </div>
    </div>

    <div class="container">
      <!-- Authentication Check -->
      <div v-if="!isLoggedIn" class="card">
        <div class="login-required">
          <h2>Authentication Required</h2>
          <p>You must be logged in to upload documents.</p>
          <router-link to="/login" class="btn btn-primary">
            Go to Login
          </router-link>
        </div>
      </div>

      <!-- Upload Form (Only when logged in) -->
      <div v-else class="card">
        <h2 class="card-title mb-3">Upload Study Material</h2>
       
        <form @submit.prevent="uploadDocument">
          <!-- File Upload Area -->
          <div class="form-group">
            <label class="form-label">
              Document File <span>*</span>
            </label>
            <div 
              class="upload-area" 
              :class="{ 'has-file': selectedFile }"
              @click="triggerFileInput"
              @dragover.prevent
              @drop.prevent="handleFileDrop"
            >
              <div class="upload-icon">📄</div>
              <h3>{{ selectedFile ? 'File Selected' : 'Click to upload or drag and drop' }}</h3>
              <p v-if="!selectedFile">PDF, DOC, DOCX, PPT, PPTX (Max 50MB)</p>
              <input
                type="file"
                ref="fileInput"
                @change="handleFileSelect"
                accept=".pdf,.doc,.docx,.ppt,.pptx"
                style="display: none;"
                required
              />
              <div v-if="selectedFile" class="selected-file">
                <div class="file-icon">📎</div>
                <div class="file-details">
                  <div class="file-name">{{ selectedFile.name }}</div>
                  <div class="file-size">{{ formatFileSize(selectedFile.size) }}</div>
                </div>
                <button type="button" class="btn-remove" @click.stop="removeFile">✕</button>
              </div>
            </div>
          </div>

          <!-- Title -->
          <div class="form-group">
            <label class="form-label">
              Title <span>*</span>
            </label>
            <input
              type="text"
              class="form-input"
              placeholder="e.g., Midterm Exam Solutions"
              v-model="form.title"
              required
            />
          </div>

          <!-- Description -->
          <div class="form-group">
            <label class="form-label">Description</label>
            <textarea
              class="form-textarea"
              placeholder="Brief description of the content..."
              v-model="form.description"
              rows="4"
            ></textarea>
          </div>

          <!-- Course -->
          <div class="form-group">
            <label class="form-label">
              Course <span>*</span>
            </label>
            <select class="form-select" v-model="form.courseId" required>
              <option value="">Select a course</option>
              <option v-for="course in courses" :key="course.courseId" :value="course.courseId">
                {{ course.code }} - {{ course.courseName }}
              </option>
            </select>
          </div>

          <!-- Document Type -->
          <div class="form-group">
            <label class="form-label">
              Document Type <span>*</span>
            </label>
            <select class="form-select" v-model="form.docType" required>
              <option value="">Select type</option>
              <option value="exam">Exam</option>
              <option value="notes">Notes</option>
              <option value="lab_report">Lab Report</option>
              <option value="summary">Summary</option>
              <option value="assignment">Assignment</option>
              <option value="project">Project</option>
            </select>
          </div>

          <!-- Submit Button -->
          <div class="form-group">
            <button type="submit" class="btn btn-primary" :disabled="uploading || !selectedFile">
              {{ uploading ? 'Uploading...' : 'Upload Document' }}
            </button>
            <button type="button" class="btn btn-secondary" @click="resetForm" :disabled="uploading">
              Reset Form
            </button>
          </div>

          <!-- Upload Progress -->
          <div v-if="uploading" class="upload-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
            </div>
            <p>{{ uploadProgress }}% Complete</p>
          </div>

          <!-- Success Message -->
          <div v-if="uploadSuccess" class="alert alert-success">
            ✓ Document uploaded successfully! It will be reviewed by administrators before being published.
          </div>

          <!-- Error Message -->
          <div v-if="uploadError" class="alert alert-error">
            ✗ {{ uploadError }}
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'UploadDocument',
  data() {
    return {
      courses: [],
      selectedFile: null,
      uploading: false,
      uploadProgress: 0,
      uploadSuccess: false,
      uploadError: null,
      form: {
        title: '',
        description: '',
        courseId: '',
        docType: ''
      }
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
    }
  },
  mounted() {
    if (this.isLoggedIn) {
      this.loadCourses()
    }
  },
  methods: {
    async loadCourses() {
      try {
        const response = await axios.get('http://localhost:8080/api/courses')
        this.courses = response.data
      } catch (error) {
        console.error('Error loading courses:', error)
      }
    },

    triggerFileInput() {
      if (!this.uploading) {
        this.$refs.fileInput.click()
      }
    },

    handleFileSelect(event) {
      const file = event.target.files[0]
      this.validateAndSetFile(file)
    },

    handleFileDrop(event) {
      const file = event.dataTransfer.files[0]
      this.validateAndSetFile(file)
    },

    validateAndSetFile(file) {
      if (!file) return

      // Check file size (50MB max)
      const maxSize = 50 * 1024 * 1024 // 50MB in bytes
      if (file.size > maxSize) {
        this.uploadError = 'File size must be less than 50MB'
        setTimeout(() => this.uploadError = null, 5000)
        return
      }

      // Check file type
      const allowedTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-powerpoint',
        'application/vnd.openxmlformats-officedocument.presentationml.presentation'
      ]

      if (!allowedTypes.includes(file.type)) {
        this.uploadError = 'Only PDF, DOC, DOCX, PPT, and PPTX files are allowed'
        setTimeout(() => this.uploadError = null, 5000)
        return
      }

      this.selectedFile = file
      this.uploadError = null
    },

    removeFile() {
      this.selectedFile = null
      this.$refs.fileInput.value = ''
    },

    async uploadDocument() {
      if (!this.isLoggedIn) {
        this.uploadError = 'Please login to upload documents'
        return
      }

      if (!this.selectedFile) {
        this.uploadError = 'Please select a file to upload'
        return
      }

      this.uploading = true
      this.uploadSuccess = false
      this.uploadError = null
      this.uploadProgress = 0

      try {
        const token = localStorage.getItem('token')

        console.log('=== UPLOADING DOCUMENT ===')
        console.log('Token:', token ? 'Present' : 'Missing')
        console.log('File:', this.selectedFile.name)
        console.log('Form data:', this.form)

        // Create FormData
        const formData = new FormData()
        formData.append('file', this.selectedFile)
        formData.append('docTitle', this.form.title)
        formData.append('docDescription', this.form.description || '')
        formData.append('courseId', this.form.courseId)
        formData.append('documentType', this.form.docType)

        const response = await axios.post(
          'http://localhost:8080/api/documents/upload',  
          formData,
          {
            headers: {
              'Authorization': `Bearer ${token}`
              // NO incluir Content-Type, axios lo establece automáticamente con boundary
            },
            onUploadProgress: (progressEvent) => {
              this.uploadProgress = Math.round(
                (progressEvent.loaded * 100) / progressEvent.total
              )
            }
          }
        )

        console.log('Upload response:', response.data)

        this.uploadSuccess = true
        
        // Reset form after 3 seconds
        setTimeout(() => {
          this.resetForm()
          this.uploadSuccess = false
        }, 3000)

      } catch (error) {
        console.error('Upload error:', error)
        console.error('Error response:', error.response?.data)
        
        if (error.response?.status === 401) {
          this.uploadError = 'Session expired. Please login again.'
          setTimeout(() => this.$router.push('/login'), 2000)
        } else if (error.response) {
          this.uploadError = error.response.data.error || 'Failed to upload document'
        } else if (error.request) {
          this.uploadError = 'Cannot connect to server. Please check if backend is running.'
        } else {
          this.uploadError = 'Failed to upload document. Please try again.'
        }
      } finally {
        this.uploading = false
        this.uploadProgress = 0
      }
    },

    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
    }
  }
}
</script>

<style scoped>
.mb-3 {
  margin-bottom: 2rem;
}

.login-required {
  text-align: center;
  padding: 4rem 2rem;
}

.login-required h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.login-required p {
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.info-label {
  font-weight: 600;
  color: #1976d2;
}

.info-value {
  color: #1976d2;
  font-weight: 700;
  font-size: 1.125rem;
}

.upload-area {
  border: 3px dashed #dee2e6;
  border-radius: 12px;
  padding: 3rem 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #f8f9fa;
}

.upload-area:hover {
  border-color: var(--accent-blue);
  background: #e3f2fd;
}

.upload-area.has-file {
  border-color: #27ae60;
  background: #d4edda;
}

.upload-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.upload-area h3 {
  font-size: 1.25rem;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.upload-area p {
  color: #7f8c8d;
  margin: 0;
}

.selected-file {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  margin-top: 1rem;
  text-align: left;
}

.file-icon {
  font-size: 2rem;
}

.file-details {
  flex: 1;
}

.file-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.25rem;
}

.file-size {
  font-size: 0.875rem;
  color: #7f8c8d;
}

.btn-remove {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: #e74c3c;
  color: white;
  font-size: 1.25rem;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-remove:hover {
  background: #c0392b;
}

.form-group {
  display: flex;
  gap: 1rem;
}

.form-group .btn {
  flex: 1;
}

.upload-progress {
  margin-top: 1rem;
}

.progress-bar {
  width: 100%;
  height: 24px;
  background: #e9ecef;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
}

.upload-progress p {
  text-align: center;
  color: #7f8c8d;
  font-weight: 600;
}

.alert {
  padding: 1rem;
  border-radius: 6px;
  margin-top: 1rem;
  font-weight: 500;
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

span {
  color: #e74c3c;
}
</style>

