# Translation Management Service

This is a Spring Boot-based Translation Management Service providing APIs to create, export, and search translations. The service is secured with basic authentication and supports Docker-based deployment.

---

## 🛠 Build and Run

### Prerequisites

* Java 21
* Maven 3.9+
* Docker (optional for containerized builds)

### Profiles

* `dev`: Development profile

    * Uses in-memory database
    * Creates default user `test` with password `12345678`
    * Loads test data on startup
* `prod`: Production profile

    * Expects external database configuration (e.g., PostgreSQL, MySQL)

### Running with Maven (dev profile by default)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Running with Maven (prod profile)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

---

## 🧪 Build the Application

### Build with Tests

```bash
mvn clean install
```

### Build without Tests

```bash
mvn clean install -DskipTests
```

---

## 🐳 Docker Build and Run (Optional)

### Step 1: Build Docker Image

```bash
docker build -t translation-service:latest .
```

### Step 2: Run the Container

```bash
docker run -e SPRING_PROFILES_ACTIVE=dev -p 8080:8080 translation-service:latest
docker run -e SPRING_PROFILES_ACTIVE=prod -p 8081:8080 translation-service:latest
```

> Note: The container uses the `dev` profile by default unless overridden.

---

## 🔐 Default Credentials (dev profile)

* **Username:** `test`
* **Password:** `12345678`

These credentials are created automatically on startup under the `dev` profile.

---


## 📄 API Documentation

* OpenAPI Swagger UI available at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## ⚙️ Notes

* All endpoints are protected; unauthorized requests will result in `401 Unauthorized`.
* The `dev` profile uses an in-memory H2 database; for production, configure an external database.
* Customize user credentials and DB configs via `application-dev.yml`, `application-prod.yml`, or environment variables.

---

## 📧 Contact

For questions or support, please reach out to ahmadbilal5246@gmail.com
