<template>
  <div id="app">
    <!-- Navigation bar - shown on all pages except login -->
    <nav class="navbar" v-if="showNavbar">
      <div class="nav-content">
        <router-link to="/" class="logo">EduLink</router-link>
        
        <!-- ========== NAVBAR FOR NOT LOGGED IN (GENERAL) ========== -->
        <ul class="nav-links" v-if="!isLoggedIn">
          <li><router-link to="/">Home</router-link></li>
          <li><router-link to="/search">Search Materials</router-link></li>
          <li><router-link to="/forums">Forums</router-link></li>
          <li><router-link to="/activities">Activities</router-link></li>
          <li class="nav-item-btn">
            <button class="nav-btn" @click="goToLogin">Login</button>
          </li>
        </ul>

        <!-- ========== NAVBAR FOR STUDENT ========== -->
        <ul class="nav-links" v-else-if="isStudent">
          <li><router-link to="/">Home</router-link></li>
          <li><router-link to="/search">Search Materials</router-link></li>
          <li><router-link to="/forums">Forums</router-link></li>
          <li><router-link to="/activities">Activities</router-link></li>
          <li><router-link to="/upload">Upload</router-link></li>
          <li><router-link to="/profile">Profile</router-link></li>
          <li class="nav-item-btn">
            <button class="nav-btn" @click="logout">Logout</button>
          </li>
        </ul>

        <!-- ========== NAVBAR FOR ADMIN ========== -->
        <ul class="nav-links" v-else-if="isAdmin">
          <li><router-link to="/">Home</router-link></li>
          <li><router-link to="/admin/validate">Validate Documents</router-link></li>
          <li><router-link to="/admin/manage">Manage Users</router-link></li>
          <li><router-link to="/profile">Profile</router-link></li>
          <li class="nav-item-btn">
            <button class="nav-btn" @click="logout">Logout</button>
          </li>
        </ul>

        <!-- ========== NAVBAR FOR TEACHER ========== -->
        <ul class="nav-links" v-else-if="isTeacher">
          <li><router-link to="/">Home</router-link></li>
          <li><router-link to="/teacher/dashboard">Teacher Dashboard</router-link></li>
          <li><router-link to="/teacher/upload">Upload Materials</router-link></li>
          <li><router-link to="/forums">Forum Questions</router-link></li>
          <li><router-link to="/profile">Profile</router-link></li>
          <li class="nav-item-btn">
            <button class="nav-btn" @click="logout">Logout</button>
          </li>
        </ul>
      </div>
    </nav>
    
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      isLoggedIn: false
    }
  },
  computed: {
    showNavbar() {
      return this.$route.name !== 'login'
    },
    userRole() {
      const user = JSON.parse(localStorage.getItem('edulink_user') || '{}')
      return user.role || null
    },
    isStudent() {
      return this.userRole === 'STUDENT'
    },
    isAdmin() {
      return this.userRole === 'ADMIN'
    },
    isTeacher() {
      return this.userRole === 'TEACHER'
    }
  },
  mounted() {
    this.checkLoginStatus()
    window.addEventListener('storage', this.checkLoginStatus)
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.checkLoginStatus)
  },
  watch: {
    '$route'() {
      this.checkLoginStatus()
    }
  },
  methods: {
    checkLoginStatus() {
      const token = localStorage.getItem('token')
      this.isLoggedIn = !!token
    },
    goToLogin() {
      this.$router.push('/login')
    },
    async logout() {
      try {
        const token = localStorage.getItem('token')
        
        if (token) {
          try {
            await fetch('http://localhost:8080/api/auth/logout', {
              method: 'POST',
              headers: {
                'Authorization': `Bearer ${token}`
              }
            })
          } catch (e) {
            // Ignore backend errors
          }
        }
      } finally {
        localStorage.removeItem('token')
        localStorage.removeItem('edulink_user')
        this.isLoggedIn = false
        
        window.dispatchEvent(new Event('storage'))
        
        this.$router.push('/')
      }
    }
  }
}
</script>

<style>
/* Base Styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Navbar */
.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  /*position: sticky;*/
  top: 0;
  z-index: 100;
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 1.8rem;
  font-weight: bold;
  color: white;
  text-decoration: none;
  transition: all 0.3s;
}

.logo:hover {
  transform: scale(1.05);
  text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
}

/* Navigation Links */
.nav-links {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-links li {
  display: flex;
  align-items: center;
}

.nav-links a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
  white-space: nowrap;
}

.nav-links a:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.nav-links a.router-link-active {
  background: rgba(255, 255, 255, 0.3);
  font-weight: 600;
}

/* Login/Logout Button */
.nav-item-btn {
  margin-left: 0.5rem;
}

.nav-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem 1.5rem;
  border-radius: 25px;
  background: white;
  color: #667eea;
  font-weight: 600;
  text-decoration: none;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
  font-family: inherit;
  font-size: 1rem;
}

.nav-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

/* Mobile Responsive */
@media (max-width: 1024px) {
  .nav-links {
    gap: 0.25rem;
  }

  .nav-links a {
    padding: 0.5rem 0.75rem;
    font-size: 0.9rem;
  }

  .nav-btn {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 768px) {
  .nav-content {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }

  .nav-links {
    flex-wrap: wrap;
    justify-content: center;
    gap: 0.5rem;
  }

  .nav-links a {
    padding: 0.5rem 0.75rem;
    font-size: 0.85rem;
  }

  .nav-btn {
    padding: 0.5rem 1.25rem;
  }
}

@media (max-width: 480px) {
  .logo {
    font-size: 1.5rem;
  }

  .nav-links {
    flex-direction: column;
    width: 100%;
    gap: 0.25rem;
  }

  .nav-links li {
    width: 100%;
  }

  .nav-links a {
    width: 100%;
    text-align: center;
    padding: 0.75rem;
  }

  .nav-item-btn {
    width: 100%;
    margin-left: 0;
  }

  .nav-btn {
    width: 100%;
  }
}
</style>