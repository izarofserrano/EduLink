<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>Document Validation</h1>
        <p class="subtitle">Review and approve submitted documents</p>
      </div>
    </div>
  
    <div class="container">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading documents...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <p class="error-message">{{ error }}</p>
        <button @click="loadDocuments" class="btn btn-primary">Retry</button>
      </div>

      <!-- Content -->
      <div v-else>
        <!-- Statistics Cards -->
        <div class="stats-grid">
          <div class="stat-card pending">
            <div class="stat-content">
              <p class="stat-label">Pending Review</p>
              <h3>{{ pendingCount }}</h3>
            </div>
          </div>
          <div class="stat-card approved">
            <div class="stat-content">
              <p class="stat-label">Approved</p>
              <h3>{{ approvedCount }}</h3>
            </div>
          </div>
          <div class="stat-card total">
            <div class="stat-content">
              <p class="stat-label">Total Documents</p>
              <h3>{{ totalCount }}</h3>
            </div>
          </div>
        </div>

        <!-- Pending Documents Section -->
        <div v-if="pendingDocuments.length > 0" class="section">
          <div class="section-header">
            <h2>Pending Validation ({{ pendingDocuments.length }})</h2>
            <p class="section-subtitle">Documents waiting for approval</p>
          </div>

          <div class="documents-list">
            <div 
              v-for="doc in pendingDocuments" 
              :key="doc.documentId"
              class="document-card pending-card"
            >
              <div class="doc-header">
                <div class="doc-title-section">
                  <h3 class="doc-title">{{ doc.docTitle }}</h3>
                  <span class="doc-type-badge">{{ formatDocType(doc.documentType) }}</span>
                  <span class="status-badge pending-badge">Pending</span>
                </div>
              </div>

              <div class="doc-body">
                <p class="doc-description">
                  {{ doc.docDescription || 'No description provided' }}
                </p>

                <div class="doc-meta">
                  <span class="meta-item">
                    <strong>Uploader:</strong> {{ doc.uploader?.username || 'Unknown' }}
                  </span>
                  <span class="meta-item" v-if="doc.course">
                    <strong>Course:</strong> {{ doc.course.code }} - {{ doc.course.courseName }}
                  </span>
                  <span class="meta-item">
                    <strong>Uploaded:</strong> {{ formatDate(doc.uploadedAt) }}
                  </span>
                  <span class="meta-item">
                    <strong>Downloads:</strong> {{ doc.downloadCount }}
                  </span>
                </div>
              </div>

              <div class="doc-actions">
                <button 
                  @click="previewDocument(doc)" 
                  class="btn btn-secondary"
                >
                  Preview
                </button>
                <button 
                  @click="approveDocument(doc.documentId)" 
                  class="btn btn-success"
                  :disabled="processing"
                >
                  Approve
                </button>
                <button 
                  @click="confirmReject(doc)" 
                  class="btn btn-danger"
                  :disabled="processing"
                >
                  Reject
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- No Pending Documents -->
        <div v-else class="empty-state">
          <h3>All Caught Up!</h3>
          <p>No pending documents to review</p>
        </div>

        <!-- Approved Documents Section -->
        <div v-if="approvedDocuments.length > 0" class="section">
          <div class="section-header">
            <h2>Approved Documents ({{ approvedDocuments.length }})</h2>
            <p class="section-subtitle">Previously validated documents</p>
          </div>

          <div class="documents-list">
            <div 
              v-for="doc in approvedDocuments" 
              :key="doc.documentId"
              class="document-card approved-card"
            >
              <div class="doc-header">
                <div class="doc-title-section">
                  <h3 class="doc-title">{{ doc.docTitle }}</h3>
                  <span class="doc-type-badge">{{ formatDocType(doc.documentType) }}</span>
                  <span class="status-badge approved-badge">Approved</span>
                </div>
              </div>

              <div class="doc-body">
                <p class="doc-description">
                  {{ doc.docDescription || 'No description provided' }}
                </p>

                <div class="doc-meta">
                  <span class="meta-item">
                    <strong>Uploader:</strong> {{ doc.uploader?.username || 'Unknown' }}
                  </span>
                  <span class="meta-item" v-if="doc.course">
                    <strong>Course:</strong> {{ doc.course.code }}
                  </span>
                  <span class="meta-item">
                    <strong>Uploaded:</strong> {{ formatDate(doc.uploadedAt) }}
                  </span>
                  <span class="meta-item">
                    <strong>Downloads:</strong> {{ doc.downloadCount }}
                  </span>
                </div>
              </div>

              <div class="doc-actions">
                <button 
                  @click="previewDocument(doc)" 
                  class="btn btn-secondary"
                >
                  Preview
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Preview Modal -->
    <div v-if="previewDoc" class="modal-overlay" @click.self="closePreview">
      <div class="modal-content">
        <button class="modal-close" @click="closePreview">&times;</button>
        
        <div class="modal-header">
          <h2>{{ previewDoc.docTitle }}</h2>
          <span class="doc-type-badge">{{ formatDocType(previewDoc.documentType) }}</span>
        </div>

        <div class="modal-body">
          <div v-if="previewDoc.fileUrl && previewDoc.fileUrl.endsWith('.pdf')" class="file-preview-frame">
            <iframe
              :src="`http://localhost:8080/api/documents/${previewDoc.documentId}/preview`"
              class="pdf-preview"
              frameborder="0"
            ></iframe>
          </div>
          <div v-else class="file-preview">
            <p>Preview is only available for PDF files.</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Reject Confirmation Modal -->
    <div v-if="rejectConfirmDoc" class="modal-overlay" @click.self="cancelReject">
      <div class="modal-content confirm-modal">
        <div class="modal-header">
          <h2>Confirm Rejection</h2>
        </div>
        <div class="modal-body">
          <p>Are you sure you want to reject this document?</p>
          <p class="confirm-detail"><strong>{{ rejectConfirmDoc.docTitle }}</strong></p>
          <p class="confirm-warning">This action cannot be undone. The document will be permanently deleted.</p>
        </div>
        <div class="modal-actions">
          <button @click="cancelReject" class="btn btn-secondary">Cancel</button>
          <button @click="rejectDocument" class="btn btn-danger" :disabled="processing">
            {{ processing ? 'Rejecting...' : 'Reject Document' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'ValidateDocumentsPage',
  data() {
    return {
      loading: true,
      error: null,
      documents: [],
      pendingDocuments: [],
      approvedDocuments: [],
      pendingCount: 0,
      approvedCount: 0,
      totalCount: 0,
      processing: false,
      previewDoc: null,
      rejectConfirmDoc: null
    }
  },
  mounted() {
    this.checkAdminAccess()
    this.loadDocuments()
    this.loadStats()
  },
  methods: {
    checkAdminAccess() {
      const user = JSON.parse(localStorage.getItem('edulink_user') || '{}')
      if (user.role !== 'ADMIN') {
        this.$router.push('/')
      }
    },

    async loadDocuments() {
      this.loading = true
      this.error = null

      try {
        const token = localStorage.getItem('token')
        
        const response = await axios.get(
          'http://localhost:8080/api/admin/documents/validate',
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )

        this.documents = response.data
        this.pendingDocuments = this.documents.filter(doc => !doc.isApproved)
        this.approvedDocuments = this.documents.filter(doc => doc.isApproved)

      } catch (error) {
        console.error('Error loading documents:', error)
        if (error.response?.status === 403) {
          this.error = 'Access denied. Admin privileges required.'
          setTimeout(() => this.$router.push('/'), 2000)
        } else if (error.response?.status === 401) {
          this.error = 'Session expired. Please login again.'
          setTimeout(() => this.$router.push('/login'), 2000)
        } else {
          this.error = 'Failed to load documents. Please try again.'
        }
      } finally {
        this.loading = false
      }
    },

    async loadStats() {
      try {
        const token = localStorage.getItem('token')
        
        const response = await axios.get(
          'http://localhost:8080/api/admin/documents/stats',
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )

        this.pendingCount = response.data.pendingCount
        this.approvedCount = response.data.approvedCount
        this.totalCount = response.data.totalCount

      } catch (error) {
        console.error('Error loading stats:', error)
      }
    },

    async approveDocument(documentId) {
      if (this.processing) return

      this.processing = true

      try {
        const token = localStorage.getItem('token')
        
        await axios.post(
          `http://localhost:8080/api/admin/documents/${documentId}/approve`,
          {},
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )

        await this.loadDocuments()
        await this.loadStats()

        alert('Document approved successfully!')

      } catch (error) {
        console.error('Error approving document:', error)
        alert('Failed to approve document. Please try again.')
      } finally {
        this.processing = false
      }
    },

    confirmReject(doc) {
      this.rejectConfirmDoc = doc
    },

    cancelReject() {
      this.rejectConfirmDoc = null
    },

    async rejectDocument() {
      if (this.processing || !this.rejectConfirmDoc) return

      this.processing = true

      try {
        const token = localStorage.getItem('token')
        
        await axios.delete(
          `http://localhost:8080/api/admin/documents/${this.rejectConfirmDoc.documentId}/reject`,
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )

        this.rejectConfirmDoc = null
        await this.loadDocuments()
        await this.loadStats()

        alert('Document rejected successfully!')

      } catch (error) {
        console.error('Error rejecting document:', error)
        alert('Failed to reject document. Please try again.')
      } finally {
        this.processing = false
      }
    },

    previewDocument(doc) {
      this.previewDoc = doc
    },

    closePreview() {
      this.previewDoc = null
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
    }
  }
}
</script>

<style scoped>

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


.loading-state,
.error-state {
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

.error-message {
  color: #e74c3c;
  margin-bottom: 1.5rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  border-left: 4px solid;
}

.stat-card.pending { border-left-color: #f39c12; }
.stat-card.approved { border-left-color: #27ae60; }
.stat-card.total { border-left-color: #3498db; }

.stat-label {
  margin: 0 0 0.5rem 0;
  color: #7f8c8d;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-content h3 {
  margin: 0;
  font-size: 2.5rem;
  color: #2c3e50;
}

.section {
  margin-bottom: 3rem;
}

.section-header {
  margin-bottom: 1.5rem;
}

.section-header h2 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  font-size: 1.5rem;
}

.section-subtitle {
  margin: 0;
  color: #7f8c8d;
}

.documents-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.document-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 1.5rem;
  border-left: 4px solid;
  transition: box-shadow 0.3s;
}

.document-card:hover {
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.document-card.pending-card {
  border-left-color: #f39c12;
}

.document-card.approved-card {
  border-left-color: #27ae60;
}

.doc-header {
  margin-bottom: 1rem;
}

.doc-title-section {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.doc-title {
  margin: 0;
  font-size: 1.25rem;
  color: #2c3e50;
}

.doc-type-badge {
  padding: 0.25rem 0.75rem;
  background: #ecf0f1;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  color: #7f8c8d;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.pending-badge {
  background: #fff3cd;
  color: #856404;
}

.approved-badge {
  background: #d4edda;
  color: #155724;
}

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

.doc-actions {
  display: flex;
  gap: 0.75rem;
  padding-top: 1rem;
  border-top: 1px solid #ecf0f1;
}

.btn {
  padding: 0.625rem 1.25rem;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9375rem;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover {
  background: #5568d3;
}

.btn-secondary {
  background: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background: #7f8c8d;
}

.btn-success {
  background: #27ae60;
  color: white;
}

.btn-success:hover {
  background: #229954;
}

.btn-danger {
  background: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background: #c0392b;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.empty-state h3 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
}

.empty-state p {
  margin: 0;
  color: #7f8c8d;
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
}

.modal-content {
  background: white;
  border-radius: 8px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 10px 40px rgba(0,0,0,0.3);
}

.confirm-modal {
  max-width: 500px;
}

.modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #7f8c8d;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.3s;
  z-index: 1;
}

.modal-close:hover {
  background: #ecf0f1;
}

.modal-header {
  padding: 2rem;
  border-bottom: 1px solid #ecf0f1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.5rem;
  color: #2c3e50;
}

.modal-body {
  padding: 2rem;
}

.file-preview-frame {
  background: #f8f9fa;
  border: 2px solid #ecf0f1;
  border-radius: 8px;
  overflow: hidden;
  height: 600px;
}

.pdf-preview {
  width: 100%;
  height: 100%;
}

.file-preview {
  background: #f8f9fa;
  border: 2px dashed #ecf0f1;
  border-radius: 8px;
  padding: 3rem 2rem;
  text-align: center;
  color: #7f8c8d;
}

.confirm-detail {
  font-size: 1.125rem;
  color: #2c3e50;
  margin: 1rem 0;
}

.confirm-warning {
  color: #e74c3c;
  font-weight: 600;
  margin-top: 1.5rem;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  padding: 1.5rem 2rem;
  border-top: 1px solid #ecf0f1;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .doc-title-section {
    flex-direction: column;
    align-items: flex-start;
  }

  .doc-actions {
    flex-direction: column;
  }

  .modal-content {
    margin: 0;
    max-height: 100vh;
    border-radius: 0;
  }
}
</style>