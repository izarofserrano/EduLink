<template>
  <div>
    <div class="page-header">
      <div class="container">
        <h1>Course Forum</h1>
      </div>
    </div>

    <div class="container">
      <!-- Course Selection -->
      <div class="card mb-3">
        <label class="form-label">Select Course</label>
        <select class="form-select" v-model="selectedCourse" @change="loadThreads">
          <option value="">All Courses</option>
          <option v-for="course in courses" :key="course.courseId" :value="course.courseId">
            {{ course.code }} - {{ course.courseName }}
          </option>
        </select>
      </div>

      <!-- Current Course Info -->
      <div v-if="selectedCourse" class="card">
        <h2 class="card-title">
          {{ currentCourse?.code }} - {{ currentCourse?.courseName }} Forum
        </h2>
        
        <button
          v-if="isLoggedIn"
          class="btn btn-primary mt-2"
          @click="showAskQuestion = true"
        >
          + Ask Question
        </button>
        <div v-else class="login-prompt">
          <p>🔒 Please <router-link to="/login" class="link-primary">login</router-link> to ask questions</p>
        </div>
      </div>

      <!-- Ask Question Form -->
      <div v-if="showAskQuestion && isLoggedIn" class="card mt-3">
        <h3 class="card-title">Ask a Question</h3>
        <form @submit.prevent="postQuestion">
          <div class="form-group">
            <label class="form-label">Question Title *</label>
            <input
              type="text"
              class="form-input"
              placeholder="What's your question?"
              v-model="newQuestion.title"
              required
              minlength="5"
              maxlength="200"
            />
          </div>
          <div class="form-group">
            <label class="form-label">Question Details *</label>
            <textarea
              class="form-textarea"
              placeholder="Describe your question in detail..."
              v-model="newQuestion.content"
              rows="6"
              required
              minlength="10"
              maxlength="5000"
            ></textarea>
          </div>
          <div style="display: flex; gap: 1rem;">
            <button type="submit" class="btn btn-primary" :disabled="submitting">
              {{ submitting ? 'Posting...' : 'Post Question' }}
            </button>
            <button type="button" class="btn btn-secondary" @click="cancelAskQuestion">
              Cancel
            </button>
          </div>
        </form>
      </div>

      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>Loading forum threads...</p>
      </div>

      <!-- Forum Threads List -->
      <div v-else-if="!selectedThread" class="mt-3">
        <div class="section-header">
          <h3>Recent Questions</h3>
          <span class="results-count">{{ threads.length }} question(s)</span>
        </div>
        
        <div
          v-for="thread in threads"
          :key="thread.threadId"
          class="forum-thread"
          @click="openThread(thread.threadId)"
        >
          <div class="thread-content">
            <div class="thread-title">{{ thread.title }}</div>
            <div class="thread-preview">{{ truncateText(thread.content, 100) }}</div>
            <div class="thread-meta">
              <span class="meta-item">
                {{ thread.authorUsername }}
                <span v-if="thread.authorRole === 'TEACHER'" class="badge-teacher">👨‍🏫</span>
              </span>
              <span class="meta-item">
                {{ formatDate(thread.createdAt) }}
              </span>
              <span class="meta-item">
                {{ thread.replyCount }} answer(s)
              </span>
              
              <span v-if="thread.status === 'RESOLVED'" class="thread-status resolved">
                ✓ Resolved
              </span>
              <span v-else-if="thread.hasTeacherResponse" class="thread-status answered">
                👨‍🏫 Answered
              </span>
              <span v-else class="thread-status pending">
                ⏳ Pending
              </span>
            </div>
          </div>
          <div class="thread-arrow">→</div>
        </div>

        <div v-if="threads.length === 0 && !loading" class="empty-state">
          <div class="empty-icon">💬</div>
          <p>No questions yet for this course.</p>
          <button
            v-if="isLoggedIn"
            class="btn btn-primary"
            @click="showAskQuestion = true"
          >
            Be the first to ask!
          </button>
        </div>
      </div>

      <!-- Thread Detail View -->
      <div v-else class="mt-3">
        <button class="btn-back" @click="closeThread">
          ← Back to Questions
        </button>

        <!-- Loading Thread Detail -->
        <div v-if="loadingThread" class="loading-state">
          <div class="spinner"></div>
          <p>Loading thread...</p>
        </div>

        <!-- Thread Detail -->
        <div v-else-if="threadDetail">
          <!-- Question Card -->
          <div class="card question-detail">
            <div class="question-header">
              <h2>{{ threadDetail.title }}</h2>
              <span
                class="thread-status"
                :class="threadDetail.status === 'RESOLVED' ? 'resolved' : 'pending'"
              >
                {{ threadDetail.status === 'RESOLVED' ? '✓ Resolved' : '⏳ Open' }}
              </span>
            </div>

            <div class="question-body">
              <p style="white-space: pre-wrap;">{{ threadDetail.content }}</p>
            </div>

            <div class="question-footer">
              <div class="question-meta">
                <span class="meta-item">
                  Asked by <strong>{{ threadDetail.authorUsername }}</strong>
                  <span v-if="threadDetail.authorRole === 'TEACHER'" class="badge-teacher">| </span>
                </span>
                <span class="meta-item">
                  {{ formatDate(threadDetail.createdAt) }}
                </span>
                
              </div>
              <div class="thread-actions">
                <button
                  v-if="isLoggedIn && threadDetail.status !== 'RESOLVED' && canResolveThread"
                  class="btn btn-success btn-sm"
                  @click="markAsResolved"
                  :disabled="submitting"
                >
                  ✓ Mark as Resolved
                </button>
                <button
                  v-if="isLoggedIn && canDeleteThread"
                  class="btn btn-danger btn-sm"
                  @click="confirmDeleteThread"
                  :disabled="submitting"
                >
                  🗑️ Delete
                </button>
              </div>
            </div>
          </div>

          <!-- Answers Section -->
          <div class="card answers-section">
            <h3 class="section-title">
              {{ threadDetail.replies.length }} Answer(s)
            </h3>

            <!-- Answer List -->
            <div v-if="threadDetail.replies.length > 0" class="answers-list">
              <div
                v-for="reply in threadDetail.replies"
                :key="reply.replyId"
                class="answer-item"
                :class="{ 'teacher-answer': reply.isTeacherResponse }"
              >
                <div class="answer-header">
                  <div class="answer-author">
                    <div class="author-avatar">{{ getInitials(reply.username) }}</div>
                    <div>
                      <div class="author-name">
                        {{ reply.username }}
                        <span v-if="reply.userRole === 'TEACHER'" class="badge-teacher">Teacher</span>
                        <span v-if="reply.isTeacherResponse" class="badge-verified">✓ Verified</span>
                      </div>
                      <div class="answer-time">{{ formatDate(reply.createdAt) }}</div>
                    </div>
                  </div>
                  <button
                    v-if="isLoggedIn && canDeleteReply(reply)"
                    class="btn-delete-reply"
                    @click="confirmDeleteReply(reply.replyId)"
                    title="Delete reply"
                  >
                    🗑️
                  </button>
                </div>
                <div class="answer-body">
                  <p style="white-space: pre-wrap;">{{ reply.content }}</p>
                </div>
              </div>
            </div>

            <div v-else class="empty-answers">
              <p>No answers yet. Be the first to help!</p>
            </div>

            <!-- Post Answer Form -->
            <div v-if="isLoggedIn && threadDetail.status !== 'RESOLVED'" class="answer-form">
              <h4>Your Answer</h4>
              <form @submit.prevent="postAnswer">
                <textarea
                  class="form-textarea"
                  placeholder="Write your answer here..."
                  v-model="newAnswer"
                  rows="5"
                  required
                  minlength="10"
                  maxlength="5000"
                ></textarea>
                <div class="form-actions">
                  <button type="submit" class="btn btn-primary" :disabled="submitting">
                    {{ submitting ? 'Posting...' : 'Post Answer' }}
                  </button>
                </div>
              </form>
            </div>

            <div v-else-if="!isLoggedIn" class="login-prompt-box">
              <p>
                Please <router-link to="/login" class="link-primary">login</router-link> to post an answer
              </p>
            </div>

            <div v-else-if="threadDetail.status === 'RESOLVED'" class="resolved-notice">
              <p>✓ This thread has been marked as resolved. No more answers can be posted.</p>
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
  name: 'CourseForum',
  data() {
    return {
      courses: [],
      selectedCourse: '',
      showAskQuestion: false,
      selectedThread: null,
      threadDetail: null,
      newQuestion: {
        title: '',
        content: ''
      },
      newAnswer: '',
      threads: [],
      loading: false,
      loadingThread: false,
      submitting: false
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
    currentCourse() {
      return this.courses.find(c => c.courseId === this.selectedCourse)
    },
    canResolveThread() {
      if (!this.threadDetail || !this.currentUser) return false
      // Author or teacher can resolve
      return this.threadDetail.authorId === this.currentUser.userId ||
             this.currentUser.role === 'TEACHER'
    },
    canDeleteThread() {
      if (!this.threadDetail || !this.currentUser) return false
      // Author or admin can delete
      return this.threadDetail.authorId === this.currentUser.userId ||
             this.currentUser.role === 'ADMIN'
    }
  },
  mounted() {
    this.loadCourses()
  },
  methods: {
    async loadCourses() {
      try {
        const response = await axios.get('http://localhost:8080/api/courses')
        this.courses = response.data
        if (this.courses.length > 0) {
          this.selectedCourse = this.courses[0].courseId
          await this.loadThreads()
        }
      } catch (error) {
        console.error('Error loading courses:', error)
        alert('Failed to load courses')
      }
    },


    async loadThreads() {
      this.loading = true
      try {
        const params = this.selectedCourse ? { courseId: this.selectedCourse } : {}
        const response = await axios.get('http://localhost:8080/api/forum/threads', { params })
        this.threads = response.data
        console.log('Threads loaded:', this.threads.length)
      } catch (error) {
        console.error('Error loading threads:', error)
        alert('Failed to load forum threads')
      } finally {
        this.loading = false
      }
    },


    async openThread(threadId) {
      this.selectedThread = threadId
      this.loadingThread = true
     
      try {
        const response = await axios.get(`http://localhost:8080/api/forum/threads/${threadId}`)
        this.threadDetail = response.data
        console.log('Thread detail loaded:', this.threadDetail)
        window.scrollTo({ top: 0, behavior: 'smooth' })
      } catch (error) {
        console.error('Error loading thread detail:', error)
        alert('Failed to load thread details')
        this.closeThread()
      } finally {
        this.loadingThread = false
      }
    },


    closeThread() {
      this.selectedThread = null
      this.threadDetail = null
      this.newAnswer = ''
    },


    async postQuestion() {
      if (!this.isLoggedIn) {
        alert('Please login to ask questions')
        return
      }


      if (!this.selectedCourse) {
        alert('Please select a course')
        return
      }


      this.submitting = true


      try {
        const token = localStorage.getItem('token')
       
        const response = await axios.post(
          'http://localhost:8080/api/forum/threads',
          {
            title: this.newQuestion.title,
            content: this.newQuestion.content,
            courseId: this.selectedCourse
          },
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )


        console.log('Thread created:', response.data)


        // Reset form
        this.newQuestion = { title: '', content: '' }
        this.showAskQuestion = false


        // Reload threads
        await this.loadThreads()


        alert('✅ Question posted successfully!')


      } catch (error) {
        console.error('Error posting question:', error)
        if (error.response?.status === 401) {
          alert('Session expired. Please login again.')
          this.$router.push('/login')
        } else {
          alert('Failed to post question: ' + (error.response?.data?.message || error.message))
        }
      } finally {
        this.submitting = false
      }
    },


    cancelAskQuestion() {
      this.newQuestion = { title: '', content: '' }
      this.showAskQuestion = false
    },


    async postAnswer() {
      if (!this.isLoggedIn) {
        alert('Please login to post answers')
        return
      }


      if (!this.newAnswer.trim()) return


      this.submitting = true


      try {
        const token = localStorage.getItem('token')
       
        const response = await axios.post(
          `http://localhost:8080/api/forum/threads/${this.selectedThread}/replies`,
          {
            content: this.newAnswer
          },
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )


        console.log('Reply created:', response.data)


        // Add reply to local list
        this.threadDetail.replies.push(response.data)


        // Reset form
        this.newAnswer = ''


        alert('✅ Answer posted successfully!')


      } catch (error) {
        console.error('Error posting answer:', error)
        if (error.response?.status === 401) {
          alert('Session expired. Please login again.')
          this.$router.push('/login')
        } else {
          alert('Failed to post answer: ' + (error.response?.data?.message || error.message))
        }
      } finally {
        this.submitting = false
      }
    },


    async markAsResolved() {
      if (!this.isLoggedIn) return


      const confirm = window.confirm('Mark this thread as resolved?')
      if (!confirm) return


      this.submitting = true


      try {
        const token = localStorage.getItem('token')
       
        await axios.put(
          `http://localhost:8080/api/forum/threads/${this.selectedThread}/resolve`,
          {},
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )


        this.threadDetail.status = 'RESOLVED'
        alert('✅ Thread marked as resolved!')


      } catch (error) {
        console.error('Error marking as resolved:', error)
        alert('Failed to mark as resolved: ' + (error.response?.data?.message || error.message))
      } finally {
        this.submitting = false
      }
    },


    async confirmDeleteThread() {
      const confirm = window.confirm('Are you sure you want to delete this thread? This action cannot be undone.')
      if (!confirm) return


      this.submitting = true


      try {
        const token = localStorage.getItem('token')
       
        await axios.delete(
          `http://localhost:8080/api/forum/threads/${this.selectedThread}`,
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )


        alert('✅ Thread deleted successfully!')
        this.closeThread()
        await this.loadThreads()


      } catch (error) {
        console.error('Error deleting thread:', error)
        alert('Failed to delete thread: ' + (error.response?.data?.message || error.message))
      } finally {
        this.submitting = false
      }
    },


    async confirmDeleteReply(replyId) {
      const confirm = window.confirm('Are you sure you want to delete this reply?')
      if (!confirm) return


      try {
        const token = localStorage.getItem('token')
       
        await axios.delete(
          `http://localhost:8080/api/forum/replies/${replyId}`,
          {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          }
        )


        // Remove reply from local list
        this.threadDetail.replies = this.threadDetail.replies.filter(r => r.replyId !== replyId)


        alert('✅ Reply deleted successfully!')


      } catch (error) {
        console.error('Error deleting reply:', error)
        alert('Failed to delete reply: ' + (error.response?.data?.message || error.message))
      }
    },


    canDeleteReply(reply) {
      if (!this.currentUser) return false
      // Author or admin can delete
      return reply.userId === this.currentUser.userId || this.currentUser.role === 'ADMIN'
    },


    getInitials(name) {
      return name
        .split(' ')
        .map(n => n[0])
        .join('')
        .toUpperCase()
        .substring(0, 2)
    },


    truncateText(text, maxLength) {
      if (text.length <= maxLength) return text
      return text.substring(0, maxLength) + '...'
    },


    formatDate(dateString) {
      if (!dateString) return 'Unknown'
     
      const date = new Date(dateString)
      const now = new Date()
      const diffTime = Math.abs(now - date)
      const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
      const diffHours = Math.floor(diffTime / (1000 * 60 * 60))
      const diffMinutes = Math.floor(diffTime / (1000 * 60))


      if (diffMinutes < 1) return 'Just now'
      if (diffMinutes < 60) return `${diffMinutes} minute(s) ago`
      if (diffHours < 24) return `${diffHours} hour(s) ago`
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


.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}


.section-header h3 {
  font-size: 1.25rem;
  color: var(--text-dark);
  margin: 0;
}


.results-count {
  color: #7f8c8d;
  font-size: 0.875rem;
}


.forum-thread {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  margin-bottom: 1rem;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}


.forum-thread:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}


.thread-content {
  flex: 1;
}


.thread-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}


.thread-preview {
  color: #7f8c8d;
  font-size: 0.9375rem;
  margin-bottom: 0.75rem;
  line-height: 1.5;
}


.thread-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
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


.thread-status {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}


.thread-status.resolved {
  background: #d4edda;
  color: #155724;
}


.thread-status.pending {
  background: #fff3cd;
  color: #856404;
}


.thread-arrow {
  font-size: 1.5rem;
  color: #7f8c8d;
  margin-left: 1rem;
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


/* Question Detail */
.btn-back {
  background: none;
  border: none;
  color: var(--accent-blue);
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 1.5rem;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  transition: all 0.3s;
}


.btn-back:hover {
  background: #e3f2fd;
}


.question-detail {
  margin-bottom: 2rem;
}


.question-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 1.5rem;
  gap: 1rem;
}


.question-header h2 {
  flex: 1;
  font-size: 1.75rem;
  color: #2c3e50;
  margin: 0;
}


.question-body {
  padding: 1.5rem 0;
  border-top: 1px solid #e9ecef;
  border-bottom: 1px solid #e9ecef;
  margin-bottom: 1.5rem;
}


.question-body p {
  font-size: 1.0625rem;
  line-height: 1.6;
  color: #2c3e50;
  margin: 0;
}


.question-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}


.question-meta {
  display: flex;
  gap: 1.5rem;
  font-size: 0.9375rem;
  color: #7f8c8d;
}


.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}


.btn-success {
  background: #27ae60;
  color: white;
}


.btn-success:hover {
  background: #229954;
}


/* Answers Section */
.answers-section {
  margin-bottom: 2rem;
}


.section-title {
  font-size: 1.25rem;
  color: #2c3e50;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #e9ecef;
}


.answers-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 2rem;
}


.answer-item {
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid var(--accent-blue);
}


.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}


.answer-author {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}


.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.875rem;
}


.author-name {
  font-weight: 600;
  color: #2c3e50;
}


.answer-time {
  font-size: 0.875rem;
  color: #7f8c8d;
}


.answer-votes {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}


.vote-count {
  font-size: 0.875rem;
  color: #7f8c8d;
}


.answer-body p {
  color: #2c3e50;
  line-height: 1.6;
  margin: 0;
}


.empty-answers {
  text-align: center;
  padding: 3rem 2rem;
  color: #7f8c8d;
}


.answer-form {
  padding-top: 2rem;
  border-top: 2px solid #e9ecef;
}


.answer-form h4 {
  font-size: 1.125rem;
  color: #2c3e50;
  margin-bottom: 1rem;
}


.form-actions {
  margin-top: 1rem;
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


/* Responsive */
@media (max-width: 768px) {
  .question-header {
    flex-direction: column;
  }


  .question-footer {
    flex-direction: column;
    gap: 1rem;
    align-items: start;
  }


  .thread-meta {
    flex-direction: column;
    gap: 0.5rem;
  }


  .answer-header {
    flex-direction: column;
    align-items: start;
    gap: 1rem;
  }
}
</style>

