# 🐛  Defect Management System
 
Defect Management is a robust **Spring Boot** application designed to orchestrate the software testing lifecycle. It provides a secure, role-based RESTful API for tracking bugs, managing resolutions, and maintaining a transparent audit trail of project defects.

---

## 🛠️ Technologies Used

### 🔹 Backend
- Spring Boot 3.2.2
- Spring Data JPA (Persistence Layer)
- Spring Web (RESTful Services)
- Spring Security 6 (JWT & RBAC)
- Spring AOP (Audit & Logging)

### 🔹 Database
- PostgreSQL (Production - Azure Flexible Server)
- H2 Database (Local Development & Testing)

### 🔹 Security & Authentication
- jjwt 0.11.5 (JSON Web Tokens)

### 🔹 Documentation
- springdoc-openapi 2.4.0 (Swagger UI)

### 🔹 Utilities
- Lombok
- SLF4J
- Hibernate Validator

---

## 🚀 Getting Started

### ✅ Prerequisites
- Java 17 (LTS)
- Maven 3.8+
- PostgreSQL (Optional for local setup)

---

### ⚙️ Setup Instructions

#### 1. Clone the Repository
git clone https://github.com/krtksharma/defect-tracker-backend.git
cd defect-tracker-backend

#### 2. Configure Database

Update `src/main/resources/application-azure.yml` or set environment variables:

spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USER}
    password: ${DB_PASSWORD}

#### 3. Build and Run the Application
mvn clean install
mvn spring-boot:run

#### 4. Access API Documentation
http://localhost:8080/swagger-ui/index.html

---

## 📡 API Endpoints

### 🔐 Authentication

- URL: `/api/users/login`  
- Method: `POST`  
- Description: Validates credentials and returns a Bearer Token  

Request Body:
{
  "userName": "tester",
  "password": "password123"
}

Response (200 OK):
{
  "token": "eyJhbG...",
  "role": "ROLE_TESTER"
}

---

### 🐛 Defect Management

#### ➤ Create Defect
- Endpoint: `POST /api/defects/new`
- Role: Tester  
- Features:
  - Daily assignment limits
  - Automatic SLA calculation

#### ➤ Resolve Defect
- Endpoint: `PUT /api/defects/resolve`
- Role: Developer  
- Features:
  - Status transition
  - Resolution history tracking

#### ➤ Get All Defects
- Endpoint: `GET /api/defects/getAll`
- Description: Fetch all active and closed defects

---

### 📁 Attachments & Audit

#### ➤ Upload Attachment
- Endpoint: `POST /api/defects/{id}/attachments`
- Constraint: Max file size 10MB

#### ➤ View Defect History
- Endpoint: `GET /api/defects/{id}/history`
- Description: Retrieve immutable audit logs

---

## 🏗️ Technical Highlights

- Stateless Authentication: Uses JWT for scalable, session-less security across distributed systems.

- Persistence Strategy: Optimized for PostgreSQL with fallback to H2 for easy onboarding.

- Persistent Storage: Uses `/home` directory in Azure App Service Linux to persist file uploads across restarts.

- Aspect-Oriented Programming (AOP): Clean separation of audit logging from business logic.

---

## 👨‍💻 Author

Kartik Sharma


