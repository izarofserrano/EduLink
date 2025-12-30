<template>
  <div class="landing-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">Welcome to EduLink</h1>
        <p class="hero-subtitle">Your Student-Centered Platform for Sharing Knowledge</p>
        <p class="hero-description">
          Connect with peers, share study materials, collaborate in course forums, 
          and discover campus activities—all in one place.
        </p>
        <div class="hero-buttons">
          <router-link to="/login" class="btn-hero btn-primary">
            Get Started!
          </router-link>
          <router-link to="/search" class="btn-hero btn-secondary">
            Browse Materials
          </router-link>
        </div>
      </div>
      
      <div class="hero-image">
        <img src="../assets/landing-hero.jpg" alt="Students collaborating on EduLink" class="hero-img">      
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <h2 class="section-title">Everything You Need to Succeed</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">📄</div>
          <h3>Upload & Share</h3>
          <p>Upload your notes, solved exams, lab reports, and summaries. Help your peers while building your reputation.</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon">🔍</div>
          <h3>Smart Search</h3>
          <p>Find materials quickly by course, teacher, semester, or tags. Filter and discover what you need instantly.</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon">⭐</div>
          <h3>Quality Content</h3>
          <p>Peer ratings and admin moderation ensure high-quality, validated study materials you can trust.</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon">💭</div>
          <h3>Course Forums</h3>
          <p>Get help with challenging topics. Ask questions and receive answers from students and teachers.</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon">🎉</div>
          <h3>Campus Activities</h3>
          <p>Stay connected with off-campus activities, clubs, study groups, sports, and housing opportunities.</p>
        </div>

        <div class="feature-card">
          <div class="feature-icon">🏆</div>
          <h3>Reputation System</h3>
          <p>Earn points for helpful contributions. Top contributors get recognized and rewarded.</p>
        </div>
      </div>
    </section>

    <!-- How It Works Section -->
    <section class="how-it-works">
      <h2 class="section-title">How It Works</h2>
      <div class="steps">
        <div class="step">
          <div class="step-number">1</div>
          <h3>Create Account</h3>
          <p>Sign up with your university email</p>
        </div>
        <div class="step-arrow">→</div>
        <div class="step">
          <div class="step-number">2</div>
          <h3>Explore & Upload</h3>
          <p>Browse materials or share your own</p>
        </div>
        <div class="step-arrow">→</div>
        <div class="step">
          <div class="step-number">3</div>
          <h3>Collaborate</h3>
          <p>Participate in forums and activities</p>
        </div>
        <div class="step-arrow">→</div>
        <div class="step">
          <div class="step-number">4</div>
          <h3>Succeed Together</h3>
          <p>Build reputation and help others</p>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta">
      <h2>Ready to Join the Community?</h2>
      <p>Join thousands of students already using EduLink to improve their academic journey</p>
      <router-link to="/login" class="btn-cta">
        Start Learning Now
      </router-link>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <p>&copy; 2024 EduLink - Created by Izaro Serrano</p>
      <p>Interactive Web Technologies Final Project</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'LandingPage'
}
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  background: 
  linear-gradient(rgba(255, 255, 255, 0.7), rgba(255, 255, 255, 0.7)),
  url('../assets/landing-hero-bg.jpeg') center/cover no-repeat fixed;}

/* Hero Section */
.hero {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  max-width: 1200px;
  margin: 0 auto;
  padding: 6rem 2rem;
  align-items: center;
  min-height: 80vh;
}

.hero-content {
  color: #2c3e50; /* Texto oscuro para contraste con fondo beige */
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 1rem;
  line-height: 1.2;
  color: #2c3e50;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: #5d6d7e;
  font-weight: 500;
}

.hero-description {
  font-size: 1.125rem;
  line-height: 1.8;
  margin-bottom: 2rem;
  color: #7f8c8d;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.btn-hero {
  display: inline-block;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-size: 1.125rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.btn-primary {
  background: var(--accent-blue);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.3);
}

.btn-secondary {
  background: white;
  color: var(--accent-blue);
  border: 2px solid var(--accent-blue);
}

.btn-secondary:hover {
  background: #f8f9fa;
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.2);
}

.hero-image {
  position: relative;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  animation: float 6s ease-in-out infinite;
}

/* Features Section */
.features {
  background: white;
  padding: 6rem 2rem;
  border-top: 1px solid #eee;
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 3rem;
  color: #2c3e50;
}

.features-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.feature-card {
  padding: 2.5rem 2rem;
  background: white;
  border-radius: 16px;
  transition: all 0.3s;
  border: 1px solid #eee;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}


.feature-icon {
  font-size: 3rem;
  margin-bottom: 1.5rem;
  display: inline-block;
  padding: 1rem;
  background: var(--accent-blue);

  border-radius: 12px;
}

.feature-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #2c3e50;
}

.feature-card p {
  color: #7f8c8d;
  line-height: 1.7;
}

/* How It Works Section */
.how-it-works {
  background: #faf9f6; /* Beige gradient */
  padding: 6rem 2rem;
  color: #2c3e50;
  border-top: 1px solid #eee;
}

.how-it-works .section-title {
  color: #2c3e50;
}

.steps {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.step {
  text-align: center;
  flex: 1;
}

.step-number {
  width: 70px;
  height: 70px;
  background: var(--accent-blue);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0 auto 1.5rem;
}

.step h3 {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
  color: #2c3e50;
}

.step p {
  color: #5d6d7e;
  opacity: 0.9;
}

.step-arrow {
  font-size: 2rem;
  color: #667eea;
  opacity: 0.7;
}

/* CTA Section */
.cta {
  background: white;
  padding: 6rem 2rem;
  text-align: center;
  border-top: 1px solid #eee;
}

.cta h2 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: #2c3e50;
}

.cta p {
  font-size: 1.125rem;
  color: #7f8c8d;
  margin-bottom: 2rem;
}

.btn-cta {
  display: inline-block;
  background: var(--accent-blue);

  color: white;
  padding: 1.25rem 3rem;
  border-radius: 50px;
  font-size: 1.125rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s;
}

.btn-cta:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 40px rgba(102, 126, 234, 0.4);
}

/* Footer */
.footer {
  background: #2c3e50;
  color: white;
  padding: 2rem;
  text-align: center;
}

.footer p {
  margin: 0.5rem 0;
  opacity: 0.8;
}



/* Responsive */
@media (max-width: 768px) {
  .hero {
    grid-template-columns: 1fr;
    padding: 3rem 1rem;
    text-align: center;
  }

  .hero-title {
    font-size: 2.5rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  

  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }

  .btn-hero {
    width: 100%;
    max-width: 300px;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1.25rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
}
</style>