# EduLink - University Document Management Platform

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Running Tests](#running-tests)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### Student Features
- Upload and download course documents
- Rate documents (1-5 stars)
- Participate in course forums
- View and register for student activities
- Earn reputation points

### Teacher Features
- View course statistics
- Auto-approve uploaded documents
- Respond to student questions
- Create student activities

### Admin Features
- Validate pending documents
- Manage users (CRUD operations)
- Role management

---

## Tech Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Database:** PostgreSQL 15
- **Security:** Spring Security + JWT
- **Build Tool:** Maven

### Frontend
- **Framework:** Vue.js 3
- **Build Tool:** Vite
- **HTTP Client:** Axios
- **Styling:** Custom CSS

### DevOps
- **Containerization:** Docker + Docker Compose
- **CI/CD:** GitHub Actions
- **Version Control:** Git

---

## Quick Start (5 Minutes)

### 1. Clone the Repository
```bash
git clone https://github.com/izarofserrano/EduLink.git
cd EduLink
```

### 2. Start PostgreSQL
```bash
cd edulink-backend
docker-compose up -d
```

Verify PostgreSQL is running:
```bash
docker ps
# Should show: edulink-postgres on port 5433
```

### 3. Start Backend
```bash
cd edulink-backend
mvn spring-boot:run
```

Backend will start on: http://localhost:8080

### 4. Start Frontend
```bash
cd edulink-frontend
npm install
npm run dev
```

Frontend will start on: http://localhost:3000

### 5. Access the Application

Open your browser: **http://localhost:3000**

#### Test Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Teacher | `Maria Garcia` | `teacher123` |
| Student | `izarofserrano` | `student123` |

---

## Project Structure
```
EduLink/
├── edulink-backend/          # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/edulink/
│   │   │   │   ├── controller/      # REST controllers
│   │   │   │   ├── service/         # Business logic
│   │   │   │   ├── repository/      # Data access
│   │   │   │   ├── model/           # JPA entities
│   │   │   │   ├── dto/             # Data transfer objects
│   │   │   │   ├── security/        # JWT & Security config
│   │   │   │   └── exception/       # Exception handling
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   ├── pom.xml
│   └── docker-compose.yml
│
├── edulink-frontend/         # Vue.js frontend
│   ├── src/
│   │   ├── assets/           
│   │   ├── pages/            
│   │   ├── App.vue         
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
│
├── .github/
│   └── workflows/
│       └── ci-cd.yml        # GitHub Actions CI/CD
│
└── README.md
```

---

## API Documentation

### Key Endpoints

#### Authentication
```
POST /api/auth/register    - Register new user
POST /api/auth/login       - User login
GET  /api/auth/me          - Get current user
```

#### Documents
```
GET    /api/documents              - Get all documents
POST   /api/documents/upload       - Upload document
GET    /api/documents/{id}         - Get document details
DELETE /api/documents/{id}         - Delete document
POST   /api/documents/{id}/rate    - Rate document
```

#### Courses
```
GET /api/courses           - Get all courses
GET /api/courses/{id}      - Get course details
```

#### Forum
```
GET  /api/forum/threads           - Get all threads
POST /api/forum/threads           - Create thread
POST /api/forum/threads/{id}/reply - Reply to thread
```

---

## Running Tests

### Backend Tests
```bash
cd edulink-backend
mvn test
```

#### Speed Tests
```bash
mvn test -Dtest=SpeedTest
```

Expected Results:
- Health endpoint: < 200ms
- Courses endpoint: < 500ms
- Documents endpoint: < 1000ms


```

---

## Docker Deployment

### Build and Run with Docker Compose
```bash
# Build images
docker-compose build

# Start all services
docker-compose up -d

# Stop all services
docker-compose down
```

### Services

| Service | Port | URL |
|---------|------|-----|
| Frontend | 3000 | http://localhost:3000 |
| Backend | 8080 | http://localhost:8080 |
| PostgreSQL | 5432 | localhost:5433 |

---

## Configuration

### Backend (application.properties)
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5433/edulink
spring.datasource.username=edulink_user
spring.datasource.password=edulink_pass

# JWT
jwt.secret=your-secret-key
jwt.expiration=3600000

# File Upload
spring.servlet.multipart.max-file-size=50MB
```

### Frontend (main.js)
```javascript
axios.defaults.baseURL = 'http://localhost:8080'
```

---

## Database

### Core Tables

- `users` - User accounts
- `students` - Student-specific data
- `teachers` - Teacher-specific data
- `admin` - Admin-specific data
- `courses` - Course information
- `documents` - Uploaded documents
- `document_ratings` - Document ratings
- `forum_threads` - Forum discussions
- `forum_replies` - Forum responses
- `activities` - Student activities

---

## Author

**Izaro Fuzhi Serrano Otaduy**
- GitHub: [@izarofserrano](https://github.com/izarofserrano)
- University: Kaunas University of Technology


