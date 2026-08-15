## 📦 Tech Stack & Dependencies (`pom.xml`)

This project is built with **Spring Boot** and **Java 17+**. Below is the breakdown of core starters, third-party libraries, and testing tools.

---

### 1. Web & Real-Time
* **`spring-boot-starter-web`**: Provides Tomcat embedded server, MVC structure, and REST API routing.
* **`spring-boot-starter-websocket`**: Enables STOMP/WebSocket protocols for real-time, bi-directional communication.

### 2. Security & Authentication
* **`spring-boot-starter-security`**: Secures HTTP endpoints, filters incoming requests, and handles user authorization.
* **JJWT (0.12.x)**: Token generation, signing, and validation. Split across 3 modules:
  * `jjwt-api`: Compile-time interfaces.
  * `jjwt-impl` *(runtime)*: JJWT engine implementation.
  * `jjwt-jackson` *(runtime)*: Jackson integration for JSON parsing without classpath conflicts.

### 3. Database, ORM & Migrations
* **`spring-boot-starter-data-jpa`**: Hibernate ORM for entity mapping, CRUD repositories, and transaction management.
* **`postgresql`** *(runtime)*: JDBC driver for connecting to PostgreSQL.
* **`flyway-core` & `flyway-database-postgresql`**: Automated database version control and migration management.
* **`spring-boot-starter-data-redis`**: Key-value caching and session state management.

### 4. API Docs, Validation & Utilities
* **`spring-boot-starter-validation`**: Bean validation annotations (`@NotNull`, `@Size`, `@Email`) via Hibernate Validator.
* **`springdoc-openapi-starter-webmvc-ui`**: Generates interactive Swagger UI documentation at `/swagger-ui.html`.
* **`bucket4j-core`**: Token-bucket algorithm for rate limiting and API throttling.
* **`lombok`**: Compile-time annotation processor for boilerplate reduction (getters, setters, builders).

### 5. Testing & Environment
* **`spring-boot-starter-test`**: JUnit 5, Mockito, and AssertJ test suite.
* **`spring-security-test`**: Mock security contexts (`@WithMockUser`) for testing protected endpoints.
* **`spring-boot-testcontainers` / `testcontainers-postgresql`**: Spawns real, disposable Docker instances of PostgreSQL during integration tests.

---

### 💡 Quick Reference: Initializr vs Manual Additions

| Source | Dependencies |
| :--- | :--- |
| **Spring Initializr (`start.spring.io`)** | Web, WebSocket, Security, Data JPA, PostgreSQL, Flyway, Redis, Validation, Lombok, Testcontainers |
| **Manual Addition (`pom.xml`)** | `jjwt-api/impl/jackson`, `springdoc-openapi-starter-webmvc-ui`, `bucket4j-core` |
