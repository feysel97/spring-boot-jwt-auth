# Spring Boot JWT Authentication & Authorization Service

A production-ready, security-first Authentication and Authorization microservice built using Spring Boot 3, Spring Security 6, JSON Web Tokens (JWT), and RabbitMQ.

This project demonstrates clean coding practices, modern Java 21 features (like Records), defensive input validation, database-backed token management, global error resolution, and event-driven architecture for inter-service communication.

## 🚀 Key Features

* **Event-Driven Architecture:** Integrates RabbitMQ to asynchronously publish `UserRegisteredEvent` messages upon successful signup, decoupling identity management from downstream domain services.
* **Role-Based Access Control (RBAC):** Hardened endpoint protection using strict URL path matchers and `@EnableMethodSecurity` to enforce `@PreAuthorize` rules seamlessly, preventing security bypasses.
* **Dual-Token Architecture:** High-security configuration using short-lived stateless JWT Access Tokens paired with long-lived database-backed UUID Refresh Tokens.
* **Spring Security 6 Integration:** A customized, state-of-the-art security filter chain that explicitly separates public endpoints from protected domain resources.
* **Idempotent Token Management (Upsert Logic):** Gracefully updates existing database tokens instead of crashing with duplicate key errors on repeat logins.
* **Defensive Input Validation:** Active request screening at the API boundary using `jakarta.validation` annotations.
* **Graceful Filter Exception Handling:** Captures `ExpiredJwtException` directly inside the filter chain to allow public requests through without system crashes.
* **Robust Global Exception Handling:** Translates system crashes and validation failures into clean, unified, and standard JSON error payloads.
* **Cryptographic Protection:** Strong password hashing with Spring Security's `BCryptPasswordEncoder`.

## 🛠️ Tech Stack

* **Runtime Environment:** Java 21 (LTS)
* **Framework:** Spring Boot 3.4.2
* **Security:** Spring Security 6, JJWT (Java JWT Library)
* **Messaging:** RabbitMQ (Spring AMQP)
* **Database:** MySQL / H2
* **Build Tool:** Gradle 8.5+

## 🛤️ API Endpoints

| Method | Endpoint | Access | Request Body | Response Body | Description |
|---|---|---|---|---|---|
| `POST` | `/api/auth/register` | public | `RegisterRequest` | String (Success Message) | Registers a new user account and publishes an event to RabbitMQ. |
| `POST` | `/api/auth/login` | public | `LoginRequest` | `AuthenticationResponse` | Authenticates user and returns an Access Token and a Refresh Token. |
| `POST` | `/api/auth/refresh` | public | `RefreshTokenRequest`| `AuthenticationResponse` | Validates a refresh token to rotate and issue a new Access Token. |
| `PATCH` | `/api/auth/admin/users/{id}/role` | admin | `UpdateRoleRequest` | String (Success Message) | Updates a target user's role (requires `ROLE_ADMIN` authority). |
| `GET` | `/api/v1/users/me` | protected | None (Bearer Token) | String (Profile confirmation) | A mock resource requiring a valid, unexpired JWT bearer authentication. |

## 📄 API Request & Response Contracts

**1. User Registration (`POST /api/auth/register`)**

Payload:
JSON
{
  "username": "feysel",
  "email": "feysel@example.com",
  "password": "SecurePassword123"
}

Response (200 OK):

User registered successfully!

2. User Login (POST /api/auth/login)

Payload:

JSON
{
  "username": "feysel",
  "password": "SecurePassword123"
}
Response (200 OK):

JSON
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOi...",
  "refreshToken": "7a3b5c92-d3f4-4e92-a1f2-b8c9d0e1f2a3"
}
3. Token Refresh (POST /api/auth/refresh)

Note: Set Postman content type format to JSON (application/json)

Payload:

JSON
{
  "refreshToken": "7a3b5c92-d3f4-4e92-a1f2-b8c9d0e1f2a3"
}
Response (200 OK):

JSON
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.NEW_ACCESS_TOKEN_HERE...",
  "refreshToken": "7a3b5c92-d3f4-4e92-a1f2-b8c9d0e1f2a3"
}

4. Admin Role Update (PATCH /api/auth/admin/users/{id}/role)

Payload:

JSON
{
  "role": "ROLE_ADMIN"
}
Response (200 OK):

Plaintext
User role successfully updated to ROLE_ADMIN

5. Validation Failure Response Example

If incorrect or malformed parameters are submitted, the API returns a structured response:

{
  "email": "Must be a valid email format",
  "password": "Password must be at least 8 characters long",
  "role": "Role is required"
}

🏗️ Asynchronous Event Architecture
When a user registers:

Credentials are validated and saved to auth_service_db with a default ROLE_USER.

An asynchronous UserRegisteredEvent payload (userId, username, email) is published to the user-registration-exchange.

Downstream microservices (such as User-Service) consume the message to initialize profile records locally without synchronous HTTP blocking.

🧠 Lessons Learned
During development I implemented:

JWT authentication using Spring Security 6

Refresh token persistence and automated rotation

Event-driven microservice communication via RabbitMQ

Strict Security Filter Chain configuration resolving wildcard bypass vulnerabilities

Method-level security authorization (@PreAuthorize / @EnableMethodSecurity)

Global exception handling and defensive request validation with Jakarta Validation

Stateless authentication architecture

🏃 Getting Started
Clone the repository.

Start RabbitMQ via Docker:docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

Update application.properties with your database credentials.

Run ./gradlew bootRun.

Use your preferred API client (Postman/Insomnia) to test the security flows. Make sure to pass the JWT under Authorization: Bearer <your_access_token> when calling protected endpoints.

🗺️ Roadmap
[x] Refresh Token implementation & automated rotation.

[x] Defensive Input validation & explicit exception handling mapping.

[x] Role-Based Access Control (RBAC) / Method-level authorization.

Steps left:

[ ] Email verification for account activation

