<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>Search Materials</h1>
      </div>
    </div>

    <div class="container">
      <!-- Search Bar -->
      <div class="search-bar">
        <input
          type="text"
          class="search-input"
          placeholder="Search for notes, exams, lab reports..."
          v-model="searchQuery"
          @input="filterDocuments"
        />
      </div>

      <!-- Advanced Filters -->
      <div class="filters-section">
        <div class="filter-group">
          <label class="filter-label">Semester</label>
          <select class="filter-select" v-model="filters.semester" @change="filterDocuments">
            <option value="">All Semesters</option>
            <option value="1">Semester 1</option>
            <option value="2">Semester 2</option>
            <option value="3">Semester 3</option>
            <option value="4">Semester 4</option>
            <option value="5">Semester 5</option>
            <option value="6">Semester 6</option>
            <option value="7">Semester 7</option>
            <option value="8">Semester 8</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">Course</label>
          <select class="filter-select" v-model="filters.course" @change="filterDocuments">
            <option value="">All Courses</option>
            <option v-for="course in courses" :key="course.courseId" :value="course.courseId">
              {{ course.code }} - {{ course.courseName }}
            </option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">Document Type</label>
          <select class="filter-select" v-model="filters.docType" @change="filterDocuments">
            <option value="">All Types</option>
            <option value="exam">Exam</option>
            <option value="notes">Notes</option>
            <option value="lab_report">Lab Report</option>
            <option value="summary">Summary</option>
            <option value="assignment">Assignment</option>
            <option value="project">Project</option>
          </select>
        </div>

        <div class="filter-group">
          <label class="filter-label">Status</label>
          <select class="filter-select" v-model="filters.approved" @change="filterDocuments">
            <option value="">All</option>
            <option value="true">Approved Only</option>
            <option value="false">Pending Only</option>
          </select>
        </div>

        <button class="btn-clear-filters" @click="clearFilters">
          Clear All Filters
        </button>
      </div>

      <!-- Results Summary -->
      <div class="results-summary">
        <p>Found <strong>{{ filteredDocuments.length }}</strong> document(s)</p>
      </div>

      <!-- Document Results -->
      <div class="document-list">
        <div
          v-for="doc in filteredDocuments"
          :key="doc.documentId"
          class="document-item"
          @click="openDocumentPreview(doc)"
        >
          <div class="doc-header">
            <h3 class="doc-title">{{ doc.docTitle }}</h3>
            <span class="doc-type-badge" :class="'type-' + (doc.documentType || 'exam')">
              {{ formatDocType(doc.documentType) }}
            </span>
          </div>
          
          <div class="doc-body">
            <p class="doc-description">
              {{ doc.docDescription || 'No description provided' }}
            </p>

            <div class="doc-meta">
              <span class="meta-item">
                <strong>Uploader:</strong> {{ doc.uploaderUsername || 'Unknown' }}
              </span>
              <span class="meta-item">
                <strong>Course:</strong> {{ doc.courseName || 'Unknown' }}
              </span>
              <span class="meta-item">
                <strong>Uploaded:</strong> {{ formatDate(doc.uploadedAt) }}
              </span>
              <span class="meta-item">
                <strong>Downloads:</strong> {{ doc.downloadCount }}
              </span>
            </div>
          </div>

          <div class="doc-footer">
            <div class="doc-stats">
              <span class="stat-item">
                <span class="icon">⭐</span>
                {{ doc.rating || 0 }} ({{ doc.totalRatings || 0 }})
              </span>
              <span v-if="doc.isApproved" class="validated">
                ✓ Validated
              </span>
              <span v-else class="pending">
                ⏳ Pending Review
              </span>
            </div>
          </div>
        </div>

        <div v-if="filteredDocuments.length === 0" class="empty-state">
          <div class="empty-icon">📭</div>
          <p>No documents found matching your criteria.</p>
          <button class="btn-primary" @click="clearFilters">Clear Filters</button>
        </div>
      </div>
    </div>

    <!-- Document Preview Modal -->
    <div v-if="selectedDocument" class="modal-overlay" @click.self="closeDocumentPreview">
      <div class="modal-content">
        <button class="modal-close" @click="closeDocumentPreview">×</button>
        
        <div class="modal-header">
          <h2>{{ selectedDocument.docTitle }}</h2>
          <span class="doc-type-badge" :class="'type-' + (selectedDocument.documentType || 'exam')">
            {{ formatDocType(selectedDocument.documentType) }}
          </span>
        </div>

        <div class="modal-body">
          <!-- Document Information -->
          <div class="doc-section">
            <h3>Document Information</h3>
            <div class="info-grid">
              <div class="info-item">
                <label>Course:</label>
                <span>{{ selectedDocument.courseName || 'Unknown' }}</span>
              </div>
              <div class="info-item">
                <label>Course Code:</label>
                <span>{{ selectedDocument.courseCode || 'N/A' }}</span>
              </div>
              <div class="info-item">
                <label>Uploader:</label>
                <span>{{ selectedDocument.uploaderUsername || 'Unknown' }}</span>
              </div>
              <div class="info-item">
                <label>Type:</label>
                <span>{{ formatDocType(selectedDocument.documentType) }}</span>
              </div>
              <div class="info-item">
                <label>Status:</label>
                <span v-if="selectedDocument.isApproved" class="status-approved">
                  ✓ Approved
                </span>
                <span v-else class="status-pending">
                  Pending Review
                </span>
              </div>
              <div class="info-item">
                <label>Downloads:</label>
                <span>{{ selectedDocument.downloadCount }}</span>
              </div>
              <div class="info-item">
                <label>Uploaded:</label>
                <span>{{ formatDate(selectedDocument.uploadedAt) }}</span>
              </div>
              <div class="info-item">
                <label>Rating:</label>
                <span class="rating-display">
                  ⭐ {{ selectedDocument.rating || 0 }} / 5.0 ({{ selectedDocument.totalRatings || 0 }} ratings)
                </span>
              </div>
            </div>
          </div>

          <!-- Rating Section (Only for logged in users) -->
          <div v-if="isLoggedIn" class="doc-section rating-section">
            <h3>Rate this Document</h3>
            <div class="star-rating">
              <button
                v-for="star in 5"
                :key="star"
                class="star-button"
                :class="{ active: star <= currentRating, hover: star <= hoverRating }"
                @click="rateDocument(star)"
                @mouseenter="hoverRating = star"
                @mouseleave="hoverRating = 0"
              >
                {{ star <= (hoverRating || currentRating) ? '★' : '☆' }}
              </button>
            </div>
            <p v-if="currentRating > 0" class="rating-text">
              Your rating: {{ currentRating }} / 5
            </p>
          </div>

          <!-- Login Required for Rating -->
          <div v-else class="doc-section">
            <div class="login-prompt-box">
              <p>
                Please <router-link to="/login" class="link-primary">login</router-link> to rate this document
              </p>
            </div>
          </div>

          <!-- Description -->
          <div v-if="selectedDocument.docDescription" class="doc-section">
            <h3>Description</h3>
            <p class="description-text">{{ selectedDocument.docDescription }}</p>
          </div>

          <!-- File Preview -->
          <div class="doc-section">
            <h3>File Preview</h3>
            
            <div v-if="selectedDocument.isApproved && selectedDocument.fileUrl && selectedDocument.fileUrl.toLowerCase().includes('.pdf')" class="file-preview-frame">
              <iframe
                :src="`http://localhost:8080/api/documents/${selectedDocument.documentId}/preview`"
                class="pdf-preview"
                frameborder="0"
                @error="handlePreviewError"
              ></iframe>
              <p class="preview-note">💡 If preview doesn't load, click "Download" to view the document.</p>
            </div>
            
            <!-- No preview available -->
            <div v-else class="file-preview">
              <div class="file-icon">📄</div>
              <p class="file-info">
                <span v-if="!selectedDocument.isApproved">
                  ⏳ Document is pending approval. Preview will be available after approval.
                </span>
                <span v-else>
                  Preview is only available for PDF files.
                  <br>
                  Click "Download" to view the full document.
                </span>
              </p>
            </div>
          </div>



          <!-- Actions -->
          <div class="modal-actions">
            <button
              v-if="isLoggedIn"
              class="btn btn-primary"
              @click="downloadDocument"
            >
              Download Document
            </button>
            <div v-else class="download-login-required">
              <button class="btn btn-disabled" disabled>
                🔒 Download (Login Required)
              </button>
              <router-link to="/login" class="btn btn-secondary">
                Go to Login
              </router-link>
            </div>
            <button class="btn btn-secondary" @click="closeDocumentPreview">
              Close
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>




<script>
import axios from 'axios'


export default {
  name: 'SearchMaterials',
  data() {
    return {
      searchQuery: '',
      filters: {
        semester: '',
        course: '',
        docType: '',
        approved: ''
      },
      documents: [],
      courses: [],
      filteredDocuments: [],
      selectedDocument: null,
      currentRating: 0,
      hoverRating: 0
    }
  },
  computed: {
    isLoggedIn() {
      const token = localStorage.getItem('token')
      return !!token
    }
  },
  mounted() {
    this.loadCourses()
    this.loadDocuments()
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


    async loadDocuments() {
      try {
        const response = await axios.get('http://localhost:8080/api/documents')
        this.documents = response.data

        this.documents.sort((a, b) => {
          const dateA = new Date(a.uploadedAt)
          const dateB = new Date(b.uploadedAt)
          return dateB - dateA
        })
        
        this.documents = this.documents.map(doc => ({
          ...doc,
          rating: doc.averageRating ? doc.averageRating.toFixed(1) : 0,
          totalRatings: doc.totalRatings || 0,
          userRating: 0 // Will be loaded when document is opened
        }))
        
        this.filteredDocuments = this.documents
      } catch (error) {
        console.error('Error loading documents:', error)
      }
    },


    async loadDocumentRating(doc) {
      try {
        const token = localStorage.getItem('token')
        const headers = token ? { 'Authorization': `Bearer ${token}` } : {}
        
        const response = await axios.get(
          `http://localhost:8080/api/documents/${doc.documentId}/rating`,
          { headers }
        )
        
        doc.rating = response.data.averageRating
          ? response.data.averageRating.toFixed(1)
          : 0
        doc.totalRatings = response.data.totalRatings || 0
        doc.userRating = response.data.userRating || 0
        
      } catch (error) {
        doc.rating = 0
        doc.totalRatings = 0
        doc.userRating = 0
      }
    },


    async reloadDocumentRating(documentId) {
      try {
        const doc = this.documents.find(d => d.documentId === documentId)
        if (doc) {
          await this.loadDocumentRating(doc)
          
          if (this.selectedDocument && this.selectedDocument.documentId === documentId) {
            this.selectedDocument.rating = doc.rating
            this.selectedDocument.totalRatings = doc.totalRatings
            this.selectedDocument.userRating = doc.userRating
            this.currentRating = doc.userRating
          }
        }
      } catch (error) {
        console.error('Error reloading rating:', error)
      }
    },


    filterDocuments() {
      let results = [...this.documents]


      // Text search
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        results = results.filter(doc => {
          return (
            doc.docTitle?.toLowerCase().includes(query) ||
            doc.docDescription?.toLowerCase().includes(query) ||
            doc.courseName?.toLowerCase().includes(query) ||
            doc.courseCode?.toLowerCase().includes(query)
          )
        })
      }


      // Semester filter
      if (this.filters.semester) {
        results = results.filter(doc => {
          const course = this.courses.find(c => c.courseId === doc.courseId)
          return course && course.semester === parseInt(this.filters.semester)
        })
      }


      // Course filter
      if (this.filters.course) {
        results = results.filter(doc =>
          doc.courseId === parseInt(this.filters.course)
        )
      }


      // Document type filter
      if (this.filters.docType) {
        results = results.filter(doc =>
          doc.documentType === this.filters.docType
        )
      }


      // Approval status filter
      if (this.filters.approved !== '') {
        const isApproved = this.filters.approved === 'true'
        results = results.filter(doc => doc.isApproved === isApproved)
      }


      this.filteredDocuments = results
    },


    clearFilters() {
      this.searchQuery = ''
      this.filters = {
        semester: '',
        course: '',
        docType: '',
        approved: ''
      }
      this.filteredDocuments = this.documents
    },


    async openDocumentPreview(doc) {
      this.selectedDocument = doc
      this.hoverRating = 0
      
      if (this.isLoggedIn) {
        await this.reloadDocumentRating(doc.documentId)
        this.currentRating = doc.userRating || 0
      } else {
        this.currentRating = 0
      }
    },


    closeDocumentPreview() {
      this.selectedDocument = null
      this.currentRating = 0
      this.hoverRating = 0
    },


    async rateDocument(rating) {
      if (!this.isLoggedIn) {
        alert('Please login to rate documents')
        return
      }


      try {
        const token = localStorage.getItem('token')
        
        const response = await axios.post(
          `http://localhost:8080/api/documents/${this.selectedDocument.documentId}/rate`,
          { rating },
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )


        this.currentRating = rating
        
        this.selectedDocument.rating = response.data.averageRating.toFixed(1)
        this.selectedDocument.totalRatings = response.data.totalRatings
        this.selectedDocument.userRating = rating


        const docIndex = this.documents.findIndex(
          d => d.documentId === this.selectedDocument.documentId
        )
        if (docIndex !== -1) {
          this.documents[docIndex].rating = this.selectedDocument.rating
          this.documents[docIndex].totalRatings = this.selectedDocument.totalRatings
          this.documents[docIndex].userRating = rating
        }


        const filteredIndex = this.filteredDocuments.findIndex(
          d => d.documentId === this.selectedDocument.documentId
        )
        if (filteredIndex !== -1) {
          this.filteredDocuments[filteredIndex].rating = this.selectedDocument.rating
          this.filteredDocuments[filteredIndex].totalRatings = this.selectedDocument.totalRatings
          this.filteredDocuments[filteredIndex].userRating = rating
        }


        alert(`Thank you for rating this document ${rating} stars!`)


      } catch (error) {
        console.error('Error rating document:', error)
        alert('Failed to rate document. Please try again.')
      }
    },


    async downloadDocument() {
      if (!this.isLoggedIn) {
        alert('Please login to download documents')
        this.$router.push('/login')
        return
      }


      if (!this.selectedDocument) return


      try {
        const token = localStorage.getItem('token')
        if (!token) {
          alert('No authentication token found')
          this.$router.push('/login')
          return
        }


        const confirmDownload = confirm(`Download "${this.selectedDocument.docTitle}"?`)
        if (!confirmDownload) return


        const response = await fetch(
          `http://localhost:8080/api/documents/${this.selectedDocument.documentId}/download`,
          {
            method: 'GET',
            headers: {
              'Authorization': `Bearer ${token}`,
              'Accept': 'application/octet-stream'
            },
            credentials: 'include'
          }
        )


        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}))
          throw new Error(`HTTP ${response.status}: ${errorData.message || response.statusText}`)
        }


        const blob = await response.blob()
        
        let filename = this.selectedDocument.docTitle.replace(/[^a-zA-Z0-9._-]/g, '_')
        const contentDisposition = response.headers.get('content-disposition')
        
        if (contentDisposition) {
          const filenameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
          if (filenameMatch && filenameMatch[1]) {
            filename = filenameMatch[1].replace(/['"]/g, '')
          }
        }


        if (!filename.toLowerCase().endsWith('.pdf')) {
          filename += '.pdf'
        }


        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = filename
        link.style.display = 'none'
        
        document.body.appendChild(link)
        link.click()
        
        setTimeout(() => {
          document.body.removeChild(link)
          window.URL.revokeObjectURL(url)
        }, 100)


        this.selectedDocument.downloadCount = (this.selectedDocument.downloadCount || 0) + 1
        
        const docIndex = this.documents.findIndex(
          d => d.documentId === this.selectedDocument.documentId
        )
        if (docIndex !== -1) {
          this.documents[docIndex].downloadCount = this.selectedDocument.downloadCount
        }


        alert(`Downloading: ${filename}`)


      } catch (error) {
        console.error('Download error:', error)
        
        if (error.message.includes('401')) {
          alert('Session expired. Please login again.')
          localStorage.removeItem('token')
          localStorage.removeItem('edulink_user')
          this.$router.push('/login')
        } else if (error.message.includes('403')) {
          alert('Access denied. You do not have permission to download this document.')
        } else if (error.message.includes('404')) {
          alert('Document not found on server.')
        } else {
          alert(`Download failed: ${error.message}`)
        }
      }
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


    formatDate(dateString) {
      if (!dateString) return 'Unknown'
      
      const date = new Date(dateString)
      const now = new Date()
      const diffTime = Math.abs(now - date)
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))


      if (diffDays === 0) return 'Today'
      if (diffDays === 1) return 'Yesterday'
      if (diffDays < 7) return `${diffDays} days ago`
      if (diffDays < 30) return `${Math.floor(diffDays / 7)} weeks ago`
      
      return date.toLocaleDateString()
    },
    handlePreviewError() {
      console.error('Error loading document preview iframe')
      alert('Failed to load document preview. You can still download the document to view it.')
    }
}
}
</script>


<style scoped>
/* Previous styles remain the same... */
/* Adding new rating styles */

.rating-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
}

.star-rating {
  display: flex;
  gap: 0.5rem;
  margin: 1rem 0;
}

.star-button {
  background: none;
  border: none;
  font-size: 2.5rem;
  cursor: pointer;
  color: #dee2e6;
  transition: all 0.2s;
  padding: 0;
}

.star-button.hover,
.star-button.active {
  color: #ffc107;
  transform: scale(1.1);
}

.star-button:hover {
  transform: scale(1.2);
}

.rating-text {
  color: #2c3e50;
  font-weight: 600;
  margin-top: 0.5rem;
}

.rating-display {
  color: #ffc107;
  font-weight: 600;
}

.login-prompt-box {
  padding: 2rem;
  background: #fff3cd;
  border: 2px dashed #ffc107;
  border-radius: 8px;
  text-align: center;
}

.login-prompt-box p {
  margin: 0;
  color: #856404;
  font-size: 1rem;
}

.link-primary {
  color: var(--accent-blue);
  font-weight: 600;
  text-decoration: none;
}

.link-primary:hover {
  text-decoration: underline;
}

.download-login-required {
  display: flex;
  gap: 1rem;
  flex: 1;
}

.btn-disabled {
  padding: 0.75rem 1.5rem;
  background: #dee2e6;
  color: #6c757d;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: not-allowed;
  flex: 1;
}

/* Keep all previous styles from the original file */
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

.results-summary {
  margin-bottom: 1rem;
  color: #7f8c8d;
}

.results-summary strong {
  color: var(--accent-blue);
}

.document-item {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.document-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.doc-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 0.75rem;
}

.doc-title {
  font-size: 1.25rem;
  margin: 0;
  color: #2c3e50;
  flex: 1;
}

.doc-type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  white-space: nowrap;
  margin-left: 1rem;
}

.doc-type-badge.type-exam { background: #e3f2fd; color: #1976d2; }
.doc-type-badge.type-notes { background: #f3e5f5; color: #7b1fa2; }
.doc-type-badge.type-lab_report { background: #e8f5e9; color: #388e3c; }
.doc-type-badge.type-summary { background: #fff3e0; color: #f57c00; }
.doc-type-badge.type-assignment { background: #fce4ec; color: #c2185b; }
.doc-type-badge.type-project { background: #e0f2f1; color: #00796b; }

.doc-body {
  margin-bottom: 1rem;
}

.doc-description {
  color: #7f8c8d;
  margin-bottom: 1rem;
  line-height: 1.6;
}

.doc-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  font-size: 0.875rem;
  color: #7f8c8d;
}

.meta-item strong {
  color: #2c3e50;
  margin-right: 0.25rem;
}

.validated {
  color: #27ae60;
  font-weight: 600;
}

.pending {
  color: #f39c12;
  font-weight: 600;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 2rem;
  overflow-y: auto;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 10px 40px rgba(0,0,0,0.3);
}

.modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #7f8c8d;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.modal-close:hover {
  background: #e9ecef;
  color: #2c3e50;
}

.modal-header {
  padding: 2rem 2rem 1rem;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: start;
  gap: 1rem;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.75rem;
  color: #2c3e50;
  flex: 1;
}

.modal-body {
  padding: 2rem;
}

.doc-section {
  margin-bottom: 2rem;
}

.doc-section h3 {
  font-size: 1.125rem;
  margin-bottom: 1rem;
  color: #2c3e50;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.info-item label {
  font-weight: 600;
  color: #7f8c8d;
  font-size: 0.875rem;
}

.info-item span {
  color: #2c3e50;
}

.status-approved {
  color: #27ae60;
  font-weight: 600;
}

.status-pending {
  color: #f39c12;
  font-weight: 600;
}

.description-text {
  color: #7f8c8d;
  line-height: 1.6;
}

.file-preview {
  background: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  padding: 3rem 2rem;
  text-align: center;
}

.file-preview-frame {
  background: #f8f9fa;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  overflow: hidden;
  height: 600px;
}

.pdf-preview {
  width: 100%;
  height: 100%;
}

.file-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.file-info {
  color: #7f8c8d;
  margin: 0;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e9ecef;
}

@media (max-width: 768px) {
  .filters-section {
    grid-template-columns: 1fr;
  }

  .doc-header {
    flex-direction: column;
    align-items: start;
  }

  .doc-type-badge {
    margin-left: 0;
    margin-top: 0.5rem;
  }

  .modal-content {
    margin: 0;
    max-height: 100vh;
    border-radius: 0;
  }

  .modal-actions {
    flex-direction: column;
  }

  .download-login-required {
    flex-direction: column;
  }
}
</style>

