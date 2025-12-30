<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <!-- Logo and Title -->
        <div class="auth-header">
          <h1 class="auth-logo">EduLink</h1>
          <p class="auth-tagline">Welcome back! Please login to continue.</p>
        </div>

        <!-- Toggle between Login and Register -->
        <div class="auth-tabs">
          <button
            class="auth-tab"
            :class="{ active: isLogin }"
            @click="switchToLogin"
          >
            Login
          </button>
          <button
            class="auth-tab"
            :class="{ active: !isLogin }"
            @click="switchToRegister"
          >
            Register
          </button>
        </div>

        <!-- Login Form -->
        <form v-if="isLogin" @submit.prevent="handleLogin" class="auth-form">
          <div class="form-group">
            <label class="form-label">Username</label>
            <input
              type="text"
              class="form-input"
              placeholder="Enter your username"
              v-model="loginForm.username"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">Password</label>
            <input
              type="password"
              class="form-input"
              placeholder="Enter your password"
              v-model="loginForm.password"
              required
            />
          </div>

          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? 'Logging in...' : 'Login' }}
          </button>

          <div class="auth-message">
            Don't have an account?
            <a href="#" @click.prevent="switchToRegister" class="link-bold">Register here</a>
          </div>
        </form>

        <!-- Register Form -->
        <form v-else @submit.prevent="handleRegister" class="auth-form">
          <div class="form-group">
            <label class="form-label">Username <span>*</span></label>
            <input
              type="text"
              class="form-input"
              placeholder="Choose a username (3-50 characters)"
              v-model="registerForm.username"
              minlength="3"
              maxlength="50"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">Email <span>*</span></label>
            <input
              type="email"
              class="form-input"
              placeholder="your.email@university.edu"
              v-model="registerForm.email"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">Password <span>*</span></label>
            <input
              type="password"
              class="form-input"
              placeholder="Create a strong password (min 6 characters)"
              v-model="registerForm.password"
              minlength="6"
              required
            />
          </div>

          <div class="form-group">
            <label class="form-label">User Type <span>*</span></label>
            <select class="form-select" v-model="registerForm.role" required>
              <option value="">Select your role</option>
              <option value="STUDENT">Student</option>
              <option value="TEACHER">Teacher</option>
            </select>
          </div>

          <button type="submit" class="btn-submit" :disabled="loading">
            {{ loading ? 'Creating account...' : 'Create Account' }}
          </button>

          <div class="auth-message">
            Already have an account?
            <a href="#" @click.prevent="switchToLogin" class="link-bold">Login here</a>
          </div>
        </form>

        <!-- Success/Error Messages -->
        <div v-if="successMessage" class="alert alert-success">
          {{ successMessage }}
        </div>
        <div v-if="errorMessage" class="alert alert-error">
          {{ errorMessage }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'LoginPage',
  data() {
    return {
      isLogin: true,
      loading: false,
      successMessage: '',
      errorMessage: '',
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        email: '',
        password: '',
        role: ''
      }
    }
  },
  methods: {
    switchToLogin() {
      this.isLogin = true
      this.clearMessages()
    },

    switchToRegister() {
      this.isLogin = false
      this.clearMessages()
    },

    clearMessages() {
      this.successMessage = ''
      this.errorMessage = ''
    },

    async handleLogin() {
      this.loading = true
      this.clearMessages()

      try {
        const response = await axios.post(
          'http://localhost:8080/api/auth/login',
          {
            username: this.loginForm.username,
            password: this.loginForm.password
          }
        )

        // Save token and user data
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('edulink_user', JSON.stringify({
          username: response.data.username,
          email: response.data.email,
          role: response.data.role
        }))

        // Success message
        this.successMessage = 'Login successful! Redirecting...'

        // Force page reload to update navbar
        setTimeout(() => {
          window.location.href = '/'
        }, 500)

      } catch (error) {
        this.errorMessage = 'Invalid username or password.'
      } finally {
        this.loading = false
      }
    },

    async handleRegister() {
      this.loading = true
      this.clearMessages()

      try {
        // Validate form
        if (this.registerForm.username.length < 3) {
          this.errorMessage = 'Username must be at least 3 characters long.'
          this.loading = false
          return
        }

        if (this.registerForm.password.length < 6) {
          this.errorMessage = 'Password must be at least 6 characters long.'
          this.loading = false
          return
        }

        if (!this.registerForm.role) {
          this.errorMessage = 'Please select a user type.'
          this.loading = false
          return
        }

        // Call JWT register endpoint
        const response = await axios.post('http://localhost:8080/api/auth/register', {
          username: this.registerForm.username,
          email: this.registerForm.email,
          password: this.registerForm.password,
          role: this.registerForm.role
        })

        this.successMessage = 'Account created successfully! Logging you in...'

        // Store JWT token and user info
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('edulink_user', JSON.stringify({
          username: response.data.username,
          email: response.data.email,
          role: response.data.role,
          loggedIn: true
        }))

        // Trigger storage event for navbar update
        window.dispatchEvent(new Event('storage'))

        // Redirect to home after successful registration
        setTimeout(() => {
          this.$router.push('/')
        }, 1500)

      } catch (error) {
        console.error('Registration error:', error)
        if (error.response) {
          this.errorMessage = error.response.data.error || 'Registration failed. Please try again.'
        } else if (error.request) {
          this.errorMessage = 'Cannot connect to server. Please check if the backend is running.'
        } else {
          this.errorMessage = 'An error occurred. Please try again.'
        }
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  background: 
  linear-gradient(rgba(255, 255, 255, 0.7), rgba(255, 255, 255, 0.7)),
  url('../assets/auth-bg.jpg') center/cover no-repeat fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.auth-container {
  max-width: 600px;
  width: 100%;
  align-items: start;
}

.auth-card {
  background: white;
  border-radius: 16px;
  padding: 3rem;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.auth-header {
  text-align: center;
  margin-bottom: 2rem;
}

.auth-logo {
  font-size: 2.5rem;
  font-weight: 800;
  color: var(--accent-blue);
  margin-bottom: 0.5rem;
}

.auth-tagline {
  color: #7f8c8d;
  font-size: 1rem;
}

.auth-tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 2rem;
  background: #f8f9fa;
  padding: 0.25rem;
  border-radius: 8px;
}

.auth-tab {
  flex: 1;
  padding: 0.75rem;
  border: none;
  background: transparent;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  color: #7f8c8d;
}

.auth-tab.active {
  background: white;
  color: var(--accent-blue);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.auth-form {
  margin-bottom: 1rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #2c3e50;
}

.form-label span {
  color: #e74c3c;
}

.form-input,
.form-select {
  width: 100%;
  padding: 0.875rem;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  transition: all 0.3s;
}

.form-input:focus,
.form-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.link-bold {
  color: var(--accent-blue);
  text-decoration: none;
  font-weight: 600;
}

.link-bold:hover {
  text-decoration: underline;
}

.btn-submit {
  width: 100%;
  padding: 1rem;
  background: var(--accent-blue);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-message {
  text-align: center;
  margin-top: 1.5rem;
  color: #7f8c8d;
}

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
  .auth-card {
    padding: 2rem 1.5rem;
  }
  .auth-page {
    /* Para móviles, menos desenfoque para mejor rendimiento */
    background-attachment: scroll;
  }
}
</style>

