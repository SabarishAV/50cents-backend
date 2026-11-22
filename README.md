<div align="center">

<img src="assets/logo.png" width="140" alt="50cents logo" />

# 50cents

**A focused, privacy-first UPI companion — and the API that runs it.**

A Spring Boot service that powers the 50cents app: authentication, payments, categories, OTP mail flows, and a smart redirect page that hands every UPI link back to the app that opened it.

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org)
[![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white)](https://redis.io)
[![Build](https://img.shields.io/badge/Build-Gradle%20(Kotlin%20DSL)-02303A?logo=gradle&logoColor=white)](https://gradle.org)
[![API Docs](https://img.shields.io/badge/API%20Docs-OpenAPI%203-85EA2D?logo=openapiinitiative&logoColor=black)](#api-documentation)
[![Issues](https://img.shields.io/badge/Issues-welcome-00B4FF)](https://github.com/SabarishAV/50cents-backend/issues)

</div>

---

## Table of contents

- [Overview](#overview)
- [Features](#features)
- [Tech stack](#tech-stack)
- [Repository map](#repository-map)
- [Prerequisites](#prerequisites)
- [Getting started](#getting-started)
- [Environment configuration](#environment-configuration)
- [Redirect page](#redirect-page)
- [API documentation](#api-documentation)
- [Database seeding](#database-seeding)
- [Category auto-seeding](#category-auto-seeding)
- [Code quality](#code-quality)
- [Security & privacy](#security--privacy)
- [Roadmap](#roadmap)
- [Acknowledgements](#acknowledgements)

---

## Overview

**50cents** is a Flutter app that lets you pay anyone over UPI without ever handling your money, your UPI PIN, or your bank credentials. This repository is the **backend that serves it**.

The service owns everything that happens around the payment, not the payment itself:

- **Identity** — email/password and Google sign-in, issued as stateless JWT sessions.
- **Ledger** — every payment you initiate in the app is recorded here with its amount, payee, category, and note. Payments made outside the app can be logged manually.
- **Intelligence** — a keyword-and-LLM hybrid predicts a category from the payee and note.
- **Lifecycles** — OTP-driven password reset, email change, and account deletion over Gmail SMTP.
- **Caching** — Redis backs OTPs and rate-sensitive operations.
- **Redirect** — a self-contained HTML page that catches a UPI link, then bounces the user back into the app they came from.

> **Why a redirect page?** A UPI link opened from a browser, an email, or a QR scan has nowhere to go on its own. The `assets/` page is that landing: it detects the environment, routes to the installed UPI app if it can, and falls back to the store page otherwise — so no intent is ever a dead end.

### Design goals

- **Trust by subtraction** — the backend never sees a PIN, a card, or a balance.
- **Predictable responses** — one error envelope, one pagination shape, everywhere.
- **Stateless by default** — JWTs only; no server-side sessions to leak or expire badly.
- **Boring reliability** — defensive parsing, graceful fallbacks, explicit failure codes.

---

## Features

### Authentication & authorization

- Email/password registration and login
- **Google OAuth 2.0** sign-in via a server-issued ID token
- **OTP-based** flows for password reset, email change, and account deletion
- **JWT** stateless authentication with role-based access control (Admin / User)
- Method-level security and password reset with expiring, single-use tokens

### User management

- Profile read and update
- Email change with OTP verification
- Account deletion with OTP verification and immediate session revocation
- Password management

### Payment records

- Create, read, update, and delete payment records
- Paginated listing with filters and custom date ranges
- Credit and debit separation
- Payment summary: total income, total expense, net balance

### Categories

- Full category catalogue with icons
- **Category prediction** from recipient name and note
- Admin-only category management
- Keyword heuristics backed by a Groq LLM fallback

### Platform

- Spring Security with JWT filter chain and CORS configuration
- BCrypt password hashing
- Custom exception handling with a consistent error envelope
- Redis-backed caching for OTPs
- Interactive Swagger UI and OpenAPI 3 schema
- Spotless-enforced Google Java Format via a Git pre-commit hook

---

## Tech stack

| Layer | Technology | Version |
| --- | --- | --- |
| Language | Java | 17 |
| Framework | Spring Boot | 4.1.1 |
| Build tool | Gradle (Kotlin DSL) | 8.x |
| Database | PostgreSQL | 12+ |
| Cache | Redis | 6+ |
| ORM | Spring Data JPA / Hibernate | — |
| Security | Spring Security + JJWT | 0.12.5 |
| OAuth | Google API Client | 2.7.0 |
| Email | Spring Boot Mail (Gmail SMTP) | — |
| AI | Groq API (category prediction) | — |
| API docs | SpringDoc OpenAPI | 3.1.1 |
| Formatting | Spotless · Google Java Format | 7.0.2 |
| Container | Docker · `.dockerignore` | — |

---

## Repository map

The repository root **is** the Spring Boot project — there is no nested `backend/` directory. Clone it and build from where you land.

```text
50cents-backend/
├── src/
│   └── main/
│       ├── java/io/app/_cents/backend/
│       │   ├── auth/                  # Registration, login, Google, OTP, password reset
│       │   │   ├── controllers/       # REST controllers
│       │   │   ├── services/          # Business logic
│       │   │   ├── dtos/              # Data transfer objects
│       │   │   └── exceptions/        # Custom exceptions
│       │   ├── users/                 # Profile, email change, account deletion
│       │   ├── payments/              # Payment records, filters, summaries
│       │   ├── categories/            # Category catalogue + prediction
│       │   │   └── services/llm/      # Groq-backed predictor
│       │   ├── common/                # Shared building blocks
│       │   │   ├── annotations/       # Custom annotations
│       │   │   ├── exceptions/        # Global exception handling
│       │   │   ├── pagination/        # Pagination envelope
│       │   │   ├── services/          # Email and Redis services
│       │   │   └── utils/             # OTP, current-user, helpers
│       │   ├── configs/               # Security, JWT, Redis, Swagger, CORS
│       │   ├── seeders/               # Seed and seed-down runners
│       │   └── BackendApplication.java
│       └── resources/
│           └── application.yaml       # Spring configuration
├── assets/
│   ├── index.html                     # UPI redirect landing page
│   └── logo.png                       # Brand logo
├── .githooks/                         # Pre-commit format enforcement
├── .env.example                       # Environment template
├── build.gradle.kts                   # Build, seed, seedDown, installGitHooks
├── settings.gradle.kts
├── Dockerfile
├── gradlew / gradlew.bat
└── README.md
```

---

## Prerequisites

- **JDK 17** or newer
- **Gradle 8.x** — or just use the included `gradlew` wrapper
- **PostgreSQL 12+**
- **Redis 6+**
- **Git**

---

## Getting started

### 1. Clone

```bash
git clone https://github.com/SabarishAV/50cents-backend.git
cd 50cents-backend
```

### 2. Configure the environment

```bash
# macOS / Linux
cp .env.example .env

# Windows PowerShell
Copy-Item .env.example .env
```

Fill in the values described in [Environment configuration](#environment-configuration).

### 3. Install the Git hook

```bash
./gradlew installGitHooks
```

This points Git at the committed `.githooks/` directory and runs automatically on every `compileJava`.

### 4. Build

```bash
./gradlew build
```

### 5. Run

```bash
./gradlew bootRun
```

The API starts on `http://localhost:8080`, with Swagger UI at `http://localhost:8080/api-docs`.

---

## Environment configuration

The app reads `.env` through `spring.config.import`. Copy `.env.example` and set:

### Database

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=50cents
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### Redis

```properties
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_SSL=false
```

### JWT

```properties
JWT_SECRET=a-long-random-string-at-least-32-chars
```

### Google OAuth

```properties
GOOGLE_WEB_CLIENT_ID=your-google-client-id
```

Get a client ID from the [Google Cloud Console](https://console.cloud.google.com/).

### Email (Gmail SMTP)

```properties
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password
```

Use a Gmail [App Password](https://support.google.com/accounts/answer/185833), not your account password.

### Groq (category prediction)

```properties
GROQ_API_KEY=your-groq-api-key
```

Used by the LLM fallback when keyword matching is inconclusive. Prediction degrades to a heuristic default when unset.

### Category auto-seeding (production)

```properties
ENV=production
```

Set `ENV=production` in your `.env` file to enable **automatic category seeding on application startup** in production environments. The seeder checks the database and inserts only missing categories (idempotent).

Alternatively, use the `seed` profile for development:
```properties
spring.profiles.active=seed
```

---

## Redirect page

`assets/` contains a single, dependency-free **`index.html`** plus `logo.png`. It is the landing page a user sees when a `upi://pay` link cannot resolve on its own — for example, when it is opened from a browser, an email, or a scanned QR.

What it does:

- **Self-contained** — inline CSS, no frameworks, no build step, no external requests.
- **Brand-consistent** — same neon palette, grid, and glow as the app, built around `logo.png`.
- **Environment-aware** — switches its accent between a normal state and an error state (red) so failures read instantly.
- **Two outcomes** — either hands off to an installed UPI app, or shows a friendly store fallback so the user can install the app and retry.

Because it ships as a plain static asset, it can be served by the Spring Boot app, by any CDN, or by a static host such as GitHub Pages — the same file works everywhere.

---

## API documentation

Interactive docs are generated by SpringDoc / OpenAPI 3.

| Resource | URL |
| --- | --- |
| Swagger UI | `http://localhost:8080/api-docs` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |

### Authenticating

1. Register or log in through the Auth endpoints.
2. Copy the JWT from the response.
3. Send it on protected calls:

```
Authorization: Bearer <your-jwt-token>
```

Swagger UI can store the token for the session (`persist-authorization` is enabled).

### Error contract

Every failure returns the same envelope:

```json
{
  "success": false,
  "statusCode": 400,
  "code": "VALIDATION_ERROR",
  "message": "Please check the highlighted fields.",
  "fields": {
    "newPassword": "Must be at least 8 characters."
  },
  "path": "/auth/password",
  "timestamp": "2026-11-15T10:00:00Z"
}
```

`fields` carries per-field validation messages so clients can render them directly against the form.

---

## Database seeding

### Seed

```bash
./gradlew seed
```

Populates categories, sample users, and payment records.

### Reset

```bash
./gradlew seedDown
```

Deletes all seeded data. Destructive — use only in development.

---

## Category auto-seeding

The application automatically seeds the category catalogue on startup when **either** of these conditions is met:

| Trigger | How to activate |
| --- | --- |
| **`seed` profile** | `--spring.profiles.active=seed` (or `spring.profiles.active=seed` in config) |
| **`ENV=production`** | Environment variable `ENV=production` (via `.env` or deployment config) |

### Behavior

- Runs as a `CommandLineRunner` at startup (`@Order(2)`)
- Queries existing categories by `code`
- Inserts only the missing ones (idempotent — safe to run repeatedly)
- Logs: `Seeded X new categories (Y already existed)`


### Production deployment (Docker)

The app reads environment variables from `.env` via `spring.config.import`. Add `ENV=production` to your `.env` file:

```properties
# .env
ENV=production
DB_HOST=your-db-host
DB_PORT=5432
DB_NAME=50cents
DB_USERNAME=postgres
DB_PASSWORD=your-password
# ... other variables
```

Build and run:

```bash
docker build -t 50cents-backend .
docker run --env-file .env -p 8080:8080 50cents-backend
```

With **docker-compose**, the `.env` file is loaded automatically:

```yaml
# docker-compose.yml
services:
  backend:
    build: .
    env_file: .env
    ports:
      - "8080:8080"
```

```bash
docker-compose up -d
```

The `ENV=production` flag triggers automatic category seeding on container startup.

---

## Code quality

[Spotless](https://github.com/diffplug/spotless) with Google Java Format keeps the codebase consistent. The Git hook installed in step 3 blocks commits that are not formatted.

```bash
./gradlew spotlessApply    # format the code
./gradlew spotlessCheck    # fail if anything is unformatted
```

---

## Security & privacy

50cents is deliberately conservative about what it touches.

| Data | Handling |
| --- | --- |
| UPI PIN / bank credentials | Never received, read, or stored. |
| Money and balances | Never handled — payments execute inside the user's UPI app. |
| Passwords | BCrypt-hashed; never logged, never returned. |
| OTPs | Generated with `SecureRandom`, stored in Redis with a short TTL, single-use. |
| Session | Stateless JWT; validated on every protected request. |
| Email | Used only for account and security notifications via Gmail SMTP. |

**Responsible disclosure.** If you find a security issue, please contact the maintainer privately instead of opening a public issue.

> UPI is a trademark of the National Payments Corporation of India (NPCI). 50cents is an independent project and is not affiliated with NPCI, any bank, or any UPI application.

---

## Roadmap

- [x] JWT authentication with Google OAuth 2.0
- [x] OTP-based password reset, email change, and account deletion
- [x] Payment ledger with filters, summaries, and pagination
- [x] Category prediction with keyword heuristics + Groq fallback
- [x] Redirect landing page for unresolved UPI intents
- [ ] **Who owes whom** — a lightweight ledger for shared and borrowed money: track who I owe and who owes me, settle up, and see running balances per person
- [ ] Broader automated coverage for auth and ledger flows
- [ ] Production hardening: rate limiting, observability, and containerised deployment
- [ ] Full iOS parity validation for Google sign-in and scheme probing

Roadmap items are directional, not commitments.

---

## Acknowledgements

Built with the Spring ecosystem and the following open-source projects:

- [Spring Boot](https://spring.io/projects/spring-boot) · [Spring Security](https://spring.io/projects/spring-security) · [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL](https://www.postgresql.org) · [Redis](https://redis.io)
- [JJWT](https://github.com/jwtk/jjwt) · [Google API Client](https://github.com/googleapis/google-api-java-client)
- [SpringDoc OpenAPI](https://springdoc.org) · [Spotless](https://github.com/diffplug/spotless)
- [Groq](https://groq.com)

And to the broader UPI ecosystem for a payment standard that makes interoperable handoffs possible.

---

<div align="center">

**Made for the small ones — the chai, the auto ride, the split bill.**

[Report a bug](https://github.com/SabarishAV/50cents-backend/issues) · [View API docs](#api-documentation) · [Client app](https://github.com/SabarishAV/50cents)

</div>