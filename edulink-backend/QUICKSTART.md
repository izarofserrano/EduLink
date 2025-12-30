# EduLink - Quick Start Guide

## ⚠️ Recommended Approach: Local Build

Due to potential network issues with Maven dependencies in Docker, **we recommend building locally first**.

## Option 1: Full Local Setup (Recommended for Development)

### Prerequisites
- Java 17+ ([Download](https://adoptium.net/))
- Maven 3.6+ ([Download](https://maven.apache.org/download.cgi))
- PostgreSQL 15 ([Download](https://www.postgresql.org/download/))

### 1. Setup PostgreSQL
```sql
CREATE DATABASE edulink;
CREATE USER edulink_user WITH PASSWORD 'edulink_pass';
GRANT ALL PRIVILEGES ON DATABASE edulink TO edulink_user;
```

### 2. Build and Run
```bash
cd edulink-backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### 3. Test the API
```bash
curl http://localhost:8080/api/courses
```

**✅ This approach works 100% of the time and is fastest for development!**

---

## Option 2: Local Build + Docker (Hybrid)

### 1. Build locally (avoid Docker network issues)
```bash
cd edulink-backend
mvn clean package -DskipTests
```

### 2. Run with Docker
```bash
docker-compose -f docker-compose.prebuilt.yml up
```

This uses the pre-built JAR, avoiding Maven downloads in Docker.

---

## Option 3: PostgreSQL in Docker, App Locally

Best of both worlds - database in Docker, app runs locally for easy debugging.

### 1. Start PostgreSQL only
```bash
docker run --name edulink-postgres \
  -e POSTGRES_DB=edulink \
  -e POSTGRES_USER=edulink_user \
  -e POSTGRES_PASSWORD=edulink_pass \
  -p 5432:5432 \
  -d postgres:15-alpine
```

### 2. Run the application
```bash
mvn spring-boot:run
```

---

## Option 4: Full Docker Build (May Have Network Issues)

If you have a stable network connection:

```bash
docker-compose up --build
```

If you get Maven download errors, try one of the options above instead.

---

### 2. Test the API
Open a new terminal and run:
```bash
# Test if the server is running
curl http://localhost:8080/api/courses

# Create a sample course
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "courseName": "Introduction to Computer Science",
    "code": "CS101",
    "teacherName": "Dr. Smith",
    "semester": 1
  }'

# Get all courses (should now show 1 course)
curl http://localhost:8080/api/courses
```

### 3. Run Automated Tests
```bash
./test-api.sh
```

## What You Have Now

✅ **Working Backend API** running on http://localhost:8080
✅ **PostgreSQL Database** running on localhost:5432
✅ **Three REST Controllers:**
   - Courses: `/api/courses`
   - Documents: `/api/documents`
   - Activities: `/api/activities`

✅ **Core Entities:**
   - User (with Student, Teacher, Admin subtypes)
   - Course
   - Document
   - Activity

## API Examples

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
    "title": "Rock Band Looking for Drummer",
    "description": "Local rock band seeking drummer for weekly rehearsals",
    "activityType": "CLUB",
    "attendance": 0
  }'
```

### Get All Activities
```bash
curl http://localhost:8080/api/activities
```

## Next Steps

This is your **basic working foundation**. From here we can add:

1. **User Authentication** - Register/login functionality
2. **File Upload** - Connect to Amazon S3 for document storage
3. **Forum System** - Question/answer functionality
4. **Advanced Search** - Filter by course, teacher, semester
5. **Rating System** - Vote on documents and activities
6. **Frontend** - Vue.js interface
7. **OAuth2** - Google/GitHub login
8. **Email Notifications**
9. **Moderation Tools** - Admin approval workflow

## Troubleshooting

**Problem:** Port 8080 already in use
**Solution:** Stop other applications using port 8080 or change the port in `application.properties`

**Problem:** Database connection failed
**Solution:** Make sure Docker is running and no other PostgreSQL is using port 5432

**Problem:** Build errors
**Solution:** Ensure you have Java 17+ and Maven 3.6+ installed

## Stop the Application
```bash
docker-compose down
```

To also remove the database volume:
```bash
docker-compose down -v
```
