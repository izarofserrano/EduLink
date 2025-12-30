<template>
  <div>
    <div class="page-header">
      <div class="container">
        <button @click="goBack" class="btn-back">← Back</button>
        <h1>Upload Course Material</h1>
      </div>
    </div>

    <div class="container">
      <!-- Info Banner for Teachers -->
      <div class="teacher-info-banner">
        <div class="info-icon"></div>
        <div class="info-content">
          <strong>Note:</strong> Your uploads are automatically approved and marked as "Official Course Material"
        </div>
      </div>

      <!-- Upload Form -->
      <div class="card">
        <h2 class="card-title mb-3">Upload Study Material</h2>
        
        <form @submit.prevent="uploadMaterial">
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
              placeholder="e.g., Week 5 - Introduction to Algorithms"
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


          <!-- Document Type -->
          <div class="form-group">
            <label class="form-label">
              Document Type <span>*</span>
            </label>
            <select class="form-select" v-model="form.docType" required>
              <option value="">Select type</option>
              <option value="notes">Lecture Notes</option>
              <option value="exam">Exam</option>
              <option value="assignment">Assignment</option>
              <option value="lab_report">Lab Materials</option>
              <option value="project">Project</option>
              <option value="summary">Summary</option>
            </select>
          </div>

          <!-- Submit Button -->
          <div class="form-group form-buttons">
            <button type="submit" class="btn btn-primary" :disabled="uploading || !selectedFile">
              {{ uploading ? 'Uploading...' : 'Upload Material' }}
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
            ✓ Material uploaded successfully and is now available to students!
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
  name: 'TeacherUploadMaterial',
  data() {
    return {
      courses: [],
      selectedFile: null,
      uploading: false,
      uploadProgress: 0,
      uploadSuccess: false,
      uploadError: null,
      form: {
        courseId: '',
        title: '',
        description: '',
        docType: ''
      }
    }
  },
  computed: {
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
    this.loadCourses()
    
    // Pre-select course if coming from course detail
    const courseId = this.$route.query.courseId
    if (courseId) {
      this.form.courseId = courseId
    }
  },
  methods: {
    async loadCourses() {
      try {
        const token = localStorage.getItem('token')
        const response = await axios.get(
          'http://localhost:8080/api/teacher/courses',
          {
            headers: { 'Authorization': `Bearer ${token}` }
          }
        )
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
      const maxSize = 50 * 1024 * 1024
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

    async uploadMaterial() {
      if (!this.selectedFile) {
        this.uploadError = 'Please select a file'
        return
      }

      this.uploading = true
      this.uploadSuccess = false
      this.uploadError = null
      this.uploadProgress = 0

      try {
        const token = localStorage.getItem('token')
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
        
        setTimeout(() => {
          this.$router.push('/teacher/dashboard')
        }, 2000)

      } catch (error) {
        console.error('Upload error:', error)
        this.uploadError = error.response?.data?.error || 'Upload failed. Please try again.'
      } finally {
        this.uploading = false
      }
    },

    resetForm() {
      this.form = {
        courseId: this.$route.query.courseId || '',
        title: '',
        description: '',
        docType: ''
      }
      this.selectedFile = null
      this.uploadSuccess = false
      this.uploadError = null
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = ''
      }
    },

    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
    },

    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
/* Base Styles */
.mb-3 {
  margin-bottom: 2rem;
}

/* Page Header */
.page-header {
  background: rgb(95, 179, 84);
  padding: 2rem 0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.page-header h1 {
  color: white;
  margin: 0.5rem 0 0 0;
  font-size: 2rem;
}

.btn-back {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 0.5rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-back:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* Container */
.container {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 1rem;
}

/* Teacher Info Banner */
.teacher-info-banner {
  background: var(--accent-blue);
  color: white;
  padding: 1rem 1.5rem;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.info-icon {
  font-size: 2rem;
}

.info-content {
  flex: 1;
  font-size: 0.95rem;
}

/* Card */
.card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.card-title {
  color: #2c3e50;
  margin: 0 0 1.5rem 0;
  font-size: 1.5rem;
}

/* Form */
.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #2c3e50;
}

.form-label span {
  color: #e74c3c;
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
  font-family: inherit;
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  outline: none;
  border-color: #667eea;
}

/* Upload Area */
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
  border-color: #667eea;
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

/* Form Buttons */
.form-buttons {
  display: flex;
  gap: 1rem;
}

.form-buttons .btn {
  flex: 1;
}

/* Buttons */
.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: inherit;
}

.btn-primary {
  background: var(--accent-blue);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
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

.btn-secondary:hover:not(:disabled) {
  background: #d0d0d0;
}

/* Progress */
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
}

.upload-progress p {
  text-align: center;
  color: #7f8c8d;
  font-weight: 600;
}

/* Alerts */
.alert {
  padding: 1rem;
  border-radius: 8px;
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

/* Responsive */
@media (max-width: 768px) {
  .form-buttons {
    flex-direction: column;
  }

  .upload-area {
    padding: 2rem 1rem;
  }
}
</style>