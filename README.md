Spring Boot JWT Authentication \& Authorization Service

A production-ready, security-first Authentication and Authorization microservice built using Spring Boot 3, Spring Security 6, and JSON Web Tokens (JWT).

This project demonstrates clean coding practices, modern Java 21 features (like Records), defensive input validation, database-backed token management, and global error resolution.

🚀 Key Features

Dual-Token Architecture: High-security configuration using short-lived stateless JWT Access Tokens paired with long-lived database-backed UUID Refresh Tokens.

Spring Security 6 Integration: A customized, state-of-the-art security filter chain that manages authorization seamlessly.

Idempotent Token Management (Upsert Logic): Gracefully updates existing database tokens instead of crashing with duplicate key errors on repeat logins.

Defensive Input Validation: Active request screening at the API boundary using jakarta.validation annotations.

Graceful Filter Exception Handling: Captures ExpiredJwtException directly inside the filter chain to allow public requests through without system crashes.

Robust Global Exception Handling: Translates system crashes and validation failures into clean, unified, and standard JSON error payloads.

Cryptographic Protection: Strong password hashing with Spring Security's BCryptPasswordEncoder.

🛠️ Tech Stack

Runtime Environment: Java 21 (LTS)

Framework: Spring Boot 3.4.2

Security: Spring Security 6, JJWT (Java JWT Library)

Database: MySQL / H2

Build Tool: Gradle 8.5+

📁 API Endpoints

|Method|Endpoint|Access|Request Body|Response Body|Description|
|-|-|-|-|-|-|
|POST|/api/auth/register|public|RegisterRequest|String (Success Message)|Registers a new user account with encrypted credentials.|
|POST|/api/auth/login|public|LoginRequest|AuthenticationResponse|Authenticates user and returns an Access Token and a Refresh Token.|
|POST|/api/auth/refresh|public|RefreshTokenRequest|AuthenticationResponse|Validates a refresh token to rotate and issue a new Access Token.|
|GET|/api/v1/users/me|protected|None (Bearer Token)|String (Profile confirmation|A mock resource requiring a valid, unexpired JWT bearer authentication.|

📦 API Request \& Response Contracts

1. User Registration (POST /api/auth/register)

Payload:

{
"username": "feysel",
"email": "feysel@example.com",
"password": "SecurePassword123"
}



Response (200 OK):

User registered successfully!



2. User Login (POST /api/auth/login)

Payload:

{
"username": "feysel",
"password": "SecurePassword123"
}



Response (200 OK):

{
"accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOi...",
"refreshToken": "7a3b5c92-d3f4-4e92-a1f2-b8c9d0e1f2a3"
}



3. Token Refresh (POST /api/auth/refresh)

Payload:
Note: Set Postman content type format to JSON (application/json)

{
"refreshToken": "7a3b5c92-d3f4-4e92-a1f2-b8c9d0e1f2a3"
}



Response (200 OK):

{
"accessToken": "eyJhbGciOiJIUzI1NiJ9.NEW\_ACCESS\_TOKEN\_HERE...",
"refreshToken": "7a3b5c92-d3f4-4e92-a1f2-b8c9d0e1f2a3"
}



4. Validation Failure Response Example

If incorrect or malformed parameters are submitted, the API returns a structured response:

{
"email": "Must be a valid email format",
"password": "Password must be at least 8 characters long"
}

## Lessons Learned

During development I implemented:

* JWT authentication using Spring Security 6
* Refresh token persistence and rotation
* Global exception handling
* Request validation with Jakarta Validation
* Custom security filters
* Stateless authentication architecture

🚦 Getting Started

Clone the repository.

Update application.properties with your database credentials.

Run ./gradlew bootRun.

Use your preferred API client (Postman/Insomnia) to test the security flows. Make sure to pass the JWT under Authorization: Bearer <your\_access\_token> when calling the protected "/api/v1/users/me".

📈 Roadmap

\[x] Refresh Token implementation \& automated rotation.

\[x] Defensive Input validation \& explicit exception handling mapping.

Steps left:

\[ ] Role-Based Access Control (RBAC) / Method-level authorization.

\[ ] Email verification for account activation.

