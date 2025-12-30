# EduLink Backend - Interactive Web Technologies Project

## Overview
EduLink is a student-centered web application where users can upload, search and share study materials, participate in course-specific forums, and browse off-campus student activities.

**Author:** Izaro Fuzhi Serrano Otaduy

## Technologies Used
- **Backend:** Java Spring Boot 3.2.0
- **Database:** PostgreSQL 15
- **Authentication:** OAuth2 (configured, not yet implemented)
- **Build Tool:** Maven
- **Containerization:** Docker

## Project Structure
```
edulink-backend/
├── src/
│   └── main/
│       ├── java/com/edulink/
│       │   ├── EdulinkApplication.java
│       │   ├── config/
│       │   │   └── SecurityConfig.java
│       │   ├── controller/
│       │   │   ├── ActivityController.java
│       │   │   ├── CourseController.java
│       │   │   └── DocumentController.java
│       │   ├── model/
│       │   │   ├── User.java
│       │   │   ├── Student.java
│       │   │   ├── Teacher.java
│       │   │   ├── Admin.java
│       │   │   ├── Course.java
│       │   │   ├── Document.java
│       │   │   ├── Activity.java
│       │   │   └── enums/
│       │   │       ├── UserRole.java
│       │   │       └── ActivityType.java
│       │   └── repository/
│       │       ├── UserRepository.java
│       │       ├── CourseRepository.java
│       │       ├── DocumentRepository.java
│       │       └── ActivityRepository.java
│       └── resources/
│           └── application.properties
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

## Prerequisites
- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (optional, but recommended)
- PostgreSQL 15 (if not using Docker)

## Setup Instructions

### Option 1: Using Docker (Recommended)

1. **Clone the repository or navigate to the project directory**

2. **Build and run with Docker Compose:**
   ```bash
   docker-compose up --build
   ```

3. **The application will be available at:**
   - Backend API: http://localhost:8080
   - PostgreSQL: localhost:5432

4. **To stop the application:**
   ```bash
   docker-compose down
   ```

### Option 2: Local Development

1. **Install PostgreSQL and create database:**
   ```sql
   CREATE DATABASE edulink;
   CREATE USER edulink_user WITH PASSWORD 'edulink_pass';
   GRANT ALL PRIVILEGES ON DATABASE edulink TO edulink_user;
   ```

2. **Update application.properties if needed** (already configured for local PostgreSQL)

3. **Build the project:**
   ```bash
   mvn clean install
   ```

4. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

## API Endpoints

### Courses
- `GET /api/courses` - Get all courses
- `GET /api/courses/{id}` - Get course by ID
- `POST /api/courses` - Create new course
- `PUT /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course

### Documents
- `GET /api/documents` - Get all documents
- `GET /api/documents/{id}` - Get document by ID
- `GET /api/documents/course/{courseId}` - Get documents by course
- `POST /api/documents` - Create new document
- `PUT /api/documents/{id}` - Update document
- `DELETE /api/documents/{id}` - Delete document

### Activities
- `GET /api/activities` - Get all activities
- `GET /api/activities/{id}` - Get activity by ID
- `POST /api/activities` - Create new activity
- `PUT /api/activities/{id}` - Update activity
- `DELETE /api/activities/{id}` - Delete activity

## Testing the API

### Create a Course
```bash
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "courseName": "Data Structures",
    "code": "CS201",
    "teacherName": "Prof. Johnson",
    "semester": 1
  }'
```

### Get All Courses
```bash
curl http://localhost:8080/api/courses
```

### Create an Activity
```bash
curl -X POST http://localhost:8080/api/activities \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Study Group Session",
    "description": "Weekly study session for Data Structures",
    "activityType": "STUDY_SESSION",
    "attendance": 0
  }'
```

## Database Schema
The application uses JPA to automatically create tables based on the entity models:
- `users` (parent table)
- `students` (inherits from users)
- `teachers` (inherits from users)
- `admin` (inherits from users)
- `courses`
- `documents`
- `activities`

## Security Notes
⚠️ **Important:** For this initial version, security is disabled to facilitate testing. Before deploying to production:
1. Enable proper authentication
2. Configure OAuth2 providers
3. Implement role-based access control
4. Enable CSRF protection
5. Use HTTPS

## Next Steps
This is a basic running version. Future enhancements will include:
- User registration and authentication
- OAuth2 integration (Google, etc.)
- File upload to Amazon S3
- Forum functionality
- Rating system for documents
- Advanced search and filtering
- User profile management
- Email notifications

## Troubleshooting

### Port already in use
If port 8080 or 5432 is already in use, you can change them in:
- `application.properties` for the backend port
- `docker-compose.yml` for both ports

### Database connection issues
Make sure PostgreSQL is running and the credentials in `application.properties` match your database setup.

### Build fails
Ensure you have Java 17 and Maven installed:
```bash
java -version
mvn -version
```

## Contact
Created by Izaro Serrano for Interactive Web Technologies course.
