# Payments Microservice

## Overview
The **Payments Microservice** is responsible for handling financial transactions, including payment processing, refunds, fraud detection, and caching mechanisms using Redis. It integrates with **Kafka** for event-driven communication and ensures high observability with **Micrometer** and **Prometheus**.

## Tech Stack
- **Java 21** - Primary language for backend development.
- **Spring Boot 3.4.2** - Framework for building microservices.
- **Spring Data JPA** - ORM layer for database interaction.
- **PostgreSQL** - Relational database for storing payment records.
- **Liquibase** - Database version control.
- **Redis** - Caching mechanism for payment statuses.
- **Apache Kafka** - Message broker for event-driven communication.
- **Micrometer + Prometheus** - Observability and monitoring.
- **Testcontainers** - Integration testing with isolated environments.
- **JUnit 5 & Mockito** - Unit and integration testing framework.
- **GitHub Actions (CI/CD)** - Automated pipeline for building, testing, and deploying the microservice.

---

## Architecture
The microservice follows the **Hexagonal Architecture** with strict adherence to **SOLID principles**, **Clean Code**, and **Design Patterns**.

### Core Components:
- **Application Layer**: Contains the business logic.
- **Infrastructure Layer**: Handles database, caching, messaging, and external service communication.
- **Web Layer**: Exposes RESTful APIs for payment operations.

---

## Features
- **Payment Processing**: Handles various payment methods (Credit Card, Bank Transfer, etc.).
- **Refund Management**: Supports full and partial refunds.
- **Fraud Detection**: Identifies potentially fraudulent transactions.
- **Caching with Redis**: Stores frequently accessed data to reduce database load.
- **Kafka Event-Driven Architecture**: Publishes and consumes payment events.
- **Observability**: Integrated logging, metrics, and tracing for enhanced monitoring.
- **Security**: Implements role-based access control (RBAC) and secure transactions.
- **Scalability**: Designed for high performance and horizontal scaling.

---

## Database Schema
The database is managed using **PostgreSQL** with schema migrations handled by **Liquibase**.

### Tables:
- `payments` - Stores transaction details.
- `refunds` - Tracks refunded payments.
- `fraud_analysis` - Logs fraudulent activities.

---

## Redis Caching
Redis is used for caching payment statuses to improve performance.
- `Key Format:` `payment-status-{id}`
- **Expiration:** Cached entries have a defined TTL to prevent stale data.

---

## Kafka Integration
The microservice interacts with Kafka for real-time event-driven processing.

### Topics:
- **`payment-events`** - Publishes events related to payment processing.
- **`refund-events`** - Publishes refund-related events.

### Consumers:
- **KafkaConsumer** listens to `payment-events` for transaction updates.

### Producers:
- **KafkaProducer** publishes events when a payment status changes.

---

## Observability
Observability is implemented using **Micrometer** and **Prometheus**.
- **Metrics Tracked:**
  - `payment_success_total` - Number of successful transactions.
  - `payment_failure_total` - Number of failed transactions.
  - `cache_hit_ratio` - Redis cache hit/miss ratio.
- **Tracing**: Distributed tracing via **OpenTelemetry**.
- **Logging**: Structured logs with **SLF4J + Logback**.

---

## CI/CD Pipeline
The project uses **GitHub Actions** for automated testing, building, and deployment.

### Workflow:
1. **Build & Test**: Runs unit and integration tests using JUnit and Testcontainers.
2. **Code Quality Checks**: Linting and static code analysis.
3. **Docker Build**: Packages the microservice as a Docker image.
4. **Deploy to Staging/Production**: Pushes the Docker image to a registry and deploys using Kubernetes.

---

## API Endpoints
The microservice exposes the following RESTful endpoints:

### **Payments**
| Method | Endpoint | Description |
|--------|-------------|-------------|
| `POST` | `/payments` | Create a new payment |
| `GET` | `/payments/{id}` | Retrieve payment by ID |
| `GET` | `/payments/transaction/{transactionId}` | Retrieve payment by transaction ID |
| `PUT` | `/payments/transaction/{transactionId}/status` | Update payment status |
| `DELETE` | `/payments/{id}` | Delete a payment |
| `GET` | `/payments/status/{status}` | Get payments by status |
| `GET` | `/payments/payer/{payerId}` | Get payments by payer ID |
| `GET` | `/payments/payee/{payeeId}` | Get payments by payee ID |
| `GET` | `/payments/fraudulent` | Get all fraudulent payments |
| `POST` | `/payments/transaction/{transactionId}/refund` | Process a refund |
| `POST` | `/payments/{id}/retry` | Retry a failed payment |
| `GET` | `/payments/{id}/status` | Get cached payment status |

---

## Running the Microservice
### **Prerequisites**
- Docker & Docker Compose
- Java 21
- PostgreSQL & Redis
- Kafka (Zookeeper + Brokers)

### **Local Setup**
1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/payments-microservice.git
   cd payments-microservice
   ```
2. Start dependencies using Docker:
   ```bash
   docker-compose up -d
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Access the API at:
   ```
   http://localhost:8080/payments
   ```

---

## Testing
- **Unit Tests:** Run with JUnit 5 & Mockito.
- **Integration Tests:** Uses **Testcontainers** for PostgreSQL & Kafka.
- **Run all tests:**
  ```bash
  ./mvnw test
  ```

---

## Deployment
The service can be deployed to **Kubernetes** with **Helm Charts** or **Docker Swarm**.

```bash
docker build -t payments-microservice .
docker run -p 8080:8080 payments-microservice
```

Alternatively, use **GitHub Actions CI/CD** to deploy automatically.

---

## Contributors
- **[Saulo Capistrano]** - Developer

---

## License
This project is licensed under the MIT License.

