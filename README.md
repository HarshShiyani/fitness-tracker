# 🏋️‍♀️ Fitness Tracker API

A comprehensive Spring Boot 3 application that enables users to manage **Users, Workout Plans, and Activity Logs** with robust **JWT-based authentication**, **role-based authorization**, and complete CRUD operations.

This project showcases **secure API design**, **role-based access control**, **comprehensive testing**, and **production-ready architecture**.

---

## ✨ Key Features

- 🔐 **JWT Authentication** - Secure token-based authentication system
- 👥 **Role-Based Access Control** - Three-tier permission system (ADMIN, USER, GUEST)
- 🔄 **Complete CRUD Operations** for:
    - **Users** - Account management and profile operations
    - **Workout Plans** - Custom fitness routine creation
    - **Activity Logs** - Exercise tracking and progress monitoring
- 🛡️ **Global Exception Handling** - Consistent, user-friendly error responses
- 💾 **H2 In-Memory Database** - Quick setup for development and testing
- 📚 **Interactive API Documentation** - Swagger UI for easy API exploration
- 🧪 **Comprehensive Testing** - JUnit 5, Mockito, and Spring Boot Test integration

---

## 🛠️ Technology Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.5 |
| **Spring Modules** | Web, Data JPA, Security, Validation |
| **Database** | H2 (In-Memory) |
| **Authentication** | JWT (JSON Web Tokens) |
| **Documentation** | Swagger/OpenAPI |
| **Testing** | JUnit 5, Mockito |
| **Build Tool** | Gradle |

---

## 🚀 Quick Start Guide

### Prerequisites
- Java 17 or higher
- Git
- Internet connection (for dependency downloads)

### 1. Clone the Repository
```bash
git clone https://github.com/HarshShiyani/fitness-tracker.git
cd fitness-tracker
```

### 2. Branch Structure
This project follows a two-branch development workflow:

- **`main` branch** - Production-ready, stable code
- **`dev` branch** - Development branch with complete commit history

**Note:** All development work and commit history is maintained in the `dev` branch. The `dev` branch has been merged into `main` for the final release.

To work with the development branch:
```bash
# Switch to development branch
git checkout dev

# Or clone and checkout dev branch directly
git clone -b dev https://github.com/HarshShiyani/fitness-tracker.git
```

### 3. Build and Run
```bash
# Clean and build the project
./gradlew clean build

# Start the application
./gradlew bootRun
```

### 4. Access the Application

| Service | URL | Credentials |
|---------|-----|-------------|
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - |
| **H2 Console** | http://localhost:8080/h2-console | URL: `jdbc:h2:mem:testdb`<br>Username: `sa`<br>Password: `password` |

---

## 🔑 Authentication System

### Default Administrator Account
When the application starts in development/test mode, an administrator account is automatically created:

```
Email: admin@fitnesstracker.com
Password: Admin@123
```

### Authentication Flow

1. **Login Request**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@fitnesstracker.com",
    "password": "Admin@123"
  }'
```

2. **Response** (contains JWT token):
```json
{
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "name": "Admin User",
      "email": "admin@fitnesstracker.com",
      "role": "ADMIN"
    }
  }
}
```

3. **Using the Token**:
   Include the JWT token in subsequent requests:
```
Authorization: Bearer <your-jwt-token>
```

---

## 👥 Role-Based Access Control

### 🔴 ADMIN Role
- **Full System Access** - Complete CRUD operations on all resources
- **User Management** - Create, update, delete any user account
- **Global Data Access** - View all workout plans and activity logs
- **System Administration** - Manage application-wide settings

### 🟡 USER Role
- **Personal Data Management** - Full control over own workout plans and activity logs
- **Profile Management** - View and update personal profile information
- **Restricted Access** - Cannot access other users' data or perform admin functions

### 🟢 GUEST Role
- **Read-Only Access** - Limited viewing permissions
- **Basic Operations** - Can view user details and workout plans
- **No Modifications** - Cannot create, update, or delete any resources

---

## 🧪 Testing

### Running Tests
```bash
# Run all tests
./gradlew test

# Generate detailed coverage report
./gradlew jacocoTestReport
```

### Coverage Report
After running tests with coverage, view the detailed report at:
```
build/reports/jacoco/test/html/index.html
```

### Test Categories
- ✅ **Unit Tests** - Controllers, Services, Data Transfer Object Mappers
- ✅ **Integration Tests** - End-to-end API testing for all modules
- ✅ **Security Tests** - Authentication flows and role-based access validation
- ✅ **Database Tests** - Repository layer and data persistence validation

---

## ⚡ Error Handling

The API provides consistent, user-friendly error responses across all endpoints:

### Standard Error Response Format
```json
{
  "message": "Descriptive error message",
  "data": null
}
```

### Handled Exception Types
- **CustomException** - Domain-specific business logic errors
- **MethodArgumentNotValidException** - Request validation failures
- **AuthorizationDeniedException** - Access denied (403 Forbidden)
- **Generic Exception** - Fallback handler for unexpected errors

### Common HTTP Status Codes
- `200 OK` - Successful operation
- `201 Created` - Resource successfully created
- `400 Bad Request` - Invalid request data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server-side error

---

## 🏗️ Project Architecture

```
src/
├── main/
│   ├── java/com/fitness/tracker/
│   │   ├── controller/     # REST API endpoints
│   │   ├── service/        # Business logic layer
│   │   ├── repository/     # Data access layer
│   │   ├── model/          # Entity classes
│   │   ├── dto/            # Data transfer objects
│   │   ├── config/         # Configuration classes
│   │   ├── security/       # Authentication & authorization
│   │   └── exception/      # Custom exception handlers
│   └── resources/
│       └── application.properties # Application configuration
└── test/
    ├── unit/               # Unit tests
    └── integration/        # Integration tests
```

---

## 🔧 Configuration

### Application Properties
Key configuration settings in `application.properties`:

```properties
spring.application.name=fitness-tracker

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.defer-datasource-initialization=true

```

### Environment Profiles
- **Development** - H2 in-memory database, detailed logging
- **Testing** - Isolated test environment with test data
- **Production** - External database configuration (to be configured)

---

## 📚 API Documentation

### Swagger UI
Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

The Swagger interface provides:
- Complete API endpoint documentation
- Interactive request/response examples
- Authentication testing capabilities
- Data model definitions