# 50cents Backend

A robust Spring Boot backend API for the 50cents application, featuring user authentication, payment tracking, and category management with comprehensive security and data persistence.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Configuration](#environment-configuration)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Database Seeding](#database-seeding)
- [Code Quality](#code-quality)

## Features

- **Authentication & Authorization**
    - Email/password registration and login
    - Google OAuth 2.0 integration
    - OTP-based authentication for secure operations
    - JWT token-based stateless authentication
    - Password reset functionality
    - Role-based access control (Admin/User)

- **User Management**
    - Profile updates
    - Email change with OTP verification
    - Account deletion with OTP verification
    - Password management

- **Payment Records**
    - Create, read, update, delete payment records
    - Paginated listing with filters
    - Payment summary (total income, expenses, net balance)
    - Custom date range support

- **Categories**
    - List all expense categories
    - Category prediction based on recipient and note
    - Admin-only category management

- **Security**
    - Spring Security with method-level security
    - BCrypt password encryption
    - JWT token validation
    - CORS configuration
    - Exception handling with custom filters

## Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 4.1.1 |
| **Build Tool** | Gradle (Kotlin DSL) | - |
| **Database** | PostgreSQL | - |
| **Cache** | Redis | - |
| **ORM** | Spring Data JPA / Hibernate | - |
| **Security** | Spring Security | - |
| **JWT** | JJWT | 0.12.5 |
| **OAuth** | Google API Client | 2.7.0 |
| **Email** | Spring Boot Mail | - |
| **API Docs** | SpringDoc OpenAPI | 3.1.1 |
| **Code Quality** | Spotless (Google Java Format) | 7.0.2 |

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** 17 or higher
- **Gradle** 8.x (or use the included Gradle wrapper)
- **PostgreSQL** 12 or higher
- **Redis** 6 or higher
- **Git**

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/SabarishAV/50cents-backend.git
cd 50cents-backend
```

### 2. Configure Environment Variables

Copy the example environment file and configure it:

```bash
cp .env.example .env
```

Edit `.env` with your actual configuration values (see [Environment Configuration](#environment-configuration)).

### 3. Install Git Hooks

```bash
./gradlew installGitHooks
```

### 4. Build the Project

```bash
./gradlew build
```

### 5. Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## Environment Configuration

The application requires the following environment variables to be set in the `.env` file:

### Database Configuration

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=50cents
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### Redis Configuration

```properties
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_SSL=false
```

### JWT Configuration

```properties
JWT_SECRET=your-super-secret-jwt-key
```

### Google OAuth Configuration

```properties
GOOGLE_WEB_CLIENT_ID=your-google-client-id
```

Get your Google Client ID from the [Google Cloud Console](https://console.cloud.google.com/).

### Email Configuration (Gmail SMTP)

```properties
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password
```

Note: For Gmail, use an [App Password](https://support.google.com/accounts/answer/185833) instead of your regular password.

## Project Structure

```
50cents-backend/
├── backend/
│   └── src/
│       └── main/
│           ├── java/io/app/_cents/backend/
│           │   ├── auth/                    # Authentication module
│           │   │   ├── controllers/         # REST controllers
│           │   │   ├── services/            # Business logic
│           │   │   ├── dtos/                # Data transfer objects
│           │   │   └── exceptions/          # Custom exceptions
│           │   ├── users/                   # User management
│           │   ├── payments/                # Payment records
│           │   ├── categories/              # Category management
│           │   ├── common/                  # Shared utilities
│           │   │   ├── annotations/        # Custom annotations
│           │   │   ├── exceptions/          # Global exception handling
│           │   │   ├── pagination/          # Pagination utilities
│           │   │   ├── services/            # Email, Redis services
│           │   │   └── utils/               # Utility classes
│           │   ├── configs/                 # Spring configuration
│           │   │   ├── jwt/                 # JWT configuration
│           │   │   └── redis/               # Redis configuration
│           │   ├── seeders/                 # Database seeders
│           │   └── BackendApplication.java  # Main application class
│           └── resources/
│               ├── application.yaml          # Spring configuration
│               └── .env                      # Environment variables
│   ├── .githooks/                           # Git hooks
│   ├── build.gradle.kts                     # Gradle build configuration
│   └── settings.gradle.kts                  # Gradle settings
└── .env.example                             # Environment template
```

## API Documentation

The application includes interactive API documentation powered by Swagger/OpenAPI 3.

**Access the Swagger UI:** `http://localhost:8080/api-docs`

**Access the OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### Authentication

Most endpoints require JWT authentication. To authenticate:

1. Register or login via the Auth endpoints
2. Copy the JWT token from the response
3. Include it in the Authorization header: `Bearer <your-jwt-token>`

## Database Seeding

The project includes database seeders for development and testing:

### Seed the Database

```bash
./gradlew seed
```

This will populate the database with sample categories, users, and payment records.

### Clear the Database

```bash
./gradlew seedDown
```

This will delete all data from the database.

## Code Quality

The project uses [Spotless](https://github.com/diffplug/spotless) with Google Java Format for consistent code formatting.

### Format Code

```bash
./gradlew spotlessApply
```

### Check Code Format

```bash
./gradlew spotlessCheck
```

## Related Repositories

- **Frontend Repository:** [https://github.com/SabarishAV/50cents](https://github.com/SabarishAV/50cents)
- **Backend Repository:** [https://github.com/SabarishAV/50cents-backend](https://github.com/SabarishAV/50cents-backend)
