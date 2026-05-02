# Spring Boot JWT Authentication Service

A production-ready Authentication and Authorization microservice built with **Spring Boot 3** and **JSON Web Tokens (JWT)**.

## 🚀 Features
- **JWT-based Security**: Stateless authentication using bearer tokens.
- **Spring Security 6**: Custom security filter chain and authentication providers.
- **Global Exception Handling**: Consistent JSON error responses for all API failures.
- **Bcrypt Encryption**: Secure password hashing.
- **Stateless Architecture**: Designed for microservice scalability.

## 🛠️ Tech Stack
- **Backend:** Java 21, Spring Boot 3.4.2
- **Security:** Spring Security, JWT (JJWT Library)
- **Database:** MySQL (or H2)
- **Build Tool:** Gradle

## 🚦 Getting Started
1. Clone the repository.
2. Update `application.properties` with your database credentials.
3. Run `./gradlew bootRun`.
4. Use Postman to hit `/api/auth/register` and `/api/auth/login`.

## 📈 Roadmap (Next Steps)
- [ ] Refresh Token implementation.
- [ ] Role-Based Access Control (RBAC).
- [ ] Email verification for account activation.