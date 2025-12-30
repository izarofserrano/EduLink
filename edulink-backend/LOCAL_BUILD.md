# Local Build Guide (Recommended for Development)

If you're experiencing Docker build issues with Maven dependencies, it's easier to run the application locally during development.

## Prerequisites
1. **Java 17+** - [Download](https://adoptium.net/)
2. **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi)
3. **PostgreSQL 15** - [Download](https://www.postgresql.org/download/)

## Quick Setup

### Step 1: Install PostgreSQL
Download and install PostgreSQL, then create the database:

```sql
CREATE DATABASE edulink;
CREATE USER edulink_user WITH PASSWORD 'edulink_pass';
GRANT ALL PRIVILEGES ON DATABASE edulink TO edulink_user;
\c edulink
GRANT ALL ON SCHEMA public TO edulink_user;
```

### Step 2: Verify Java and Maven

```bash
java -version
# Should show Java 17 or higher

mvn -version
# Should show Maven 3.6 or higher
```

### Step 3: Build the Project

```bash
cd edulink-backend
mvn clean install
```

If you get dependency download errors, try:
```bash
mvn clean install -U
# The -U flag forces update of dependencies
```

### Step 4: Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR directly:
```bash
java -jar target/edulink-backend-0.0.1-SNAPSHOT.jar
```

### Step 5: Test the API

```bash
# Test if server is running
curl http://localhost:8080/api/courses

# Create a course
curl -X POST http://localhost:8080/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "courseName": "Data Structures",
    "code": "CS201",
    "teacherName": "Prof. Johnson",
    "semester": 1
  }'

# Get all courses
curl http://localhost:8080/api/courses
```

## Alternative: Docker with Local Maven Cache

If you prefer Docker but want to avoid network issues during build:

### Step 1: Build locally first
```bash
mvn clean package -DskipTests
```

### Step 2: Use simplified Dockerfile
Create a file called `Dockerfile.prebuilt`:

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Step 3: Build and run
```bash
docker build -f Dockerfile.prebuilt -t edulink-backend .
docker-compose up
```

## Troubleshooting

### PostgreSQL Connection Issues

If you can't connect to PostgreSQL, update `src/main/resources/application.properties`:

```properties
# If PostgreSQL is on a different host/port
spring.datasource.url=jdbc:postgresql://localhost:5432/edulink

# If you have different credentials
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Port Already in Use

Change the port in `application.properties`:
```properties
server.port=8081
```

### Maven Download Timeouts

Add this to your `~/.m2/settings.xml`:

```xml
<settings>
  <mirrors>
    <mirror>
      <id>central-mirror</id>
      <name>Central Mirror</name>
      <url>https://repo.maven.apache.org/maven2</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```

## Running in Your IDE

### IntelliJ IDEA
1. Open the project
2. Right-click on `EdulinkApplication.java`
3. Click "Run 'EdulinkApplication'"

### Eclipse
1. Import as Maven project
2. Right-click on project → Run As → Spring Boot App

### VS Code
1. Install "Spring Boot Extension Pack"
2. Open the project
3. Press F5 or use the Spring Boot Dashboard

## What's Running

Once started, you should see:
```
Started EdulinkApplication in X seconds
```

The application will be available at:
- **API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/api/courses (example endpoint)

## Next Steps

Once you verify everything works locally, you can:
1. Add more features
2. Connect a Vue.js frontend
3. Deploy to a cloud service
4. Set up proper authentication

## PostgreSQL via Docker (Optional)

If you don't want to install PostgreSQL locally, you can run just the database in Docker:

```bash
docker run --name edulink-postgres \
  -e POSTGRES_DB=edulink \
  -e POSTGRES_USER=edulink_user \
  -e POSTGRES_PASSWORD=edulink_pass \
  -p 5432:5432 \
  -d postgres:15-alpine
```

Then run your Spring Boot application locally with `mvn spring-boot:run`.
