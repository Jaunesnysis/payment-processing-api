# Payment Processing API

A backend REST API simulating a real-world **account-to-account payment processing system**, modelled after core banking payment workflows used in financial institutions.

Built with **Java**, **Spring Boot**, and **PostgreSQL**.

---

## What It Does

1. **Account Management** — create and manage bank accounts with balances
2. **Payment Initiation** — transfer funds between accounts with full validation
3. **Insufficient Funds Detection** — rejects payments exceeding available balance
4. **Idempotency** — duplicate payment references are rejected
5. **Refunds** — reverse completed payments and restore balances
6. **Transaction History** — full payment history per account

---

## Payment Lifecycle

PENDING → PROCESSING → COMPLETED
→ FAILED (insufficient funds)
COMPLETED → REFUNDED

---

## Tech Stack

| Layer          | Technology                  |
| -------------- | --------------------------- |
| Language       | Java 17                     |
| Framework      | Spring Boot 3.5             |
| Database       | PostgreSQL 15               |
| ORM            | Spring Data JPA / Hibernate |
| Validation     | Jakarta Validation          |
| API Docs       | SpringDoc OpenAPI (Swagger) |
| Infrastructure | Docker                      |

---

## Project Structure

src/main/java/com/payments/payment_processing_api/
├── controller/ # REST endpoints
├── service/ # Business logic
├── repository/ # Data access layer
├── model/ # JPA entities
├── dto/ # Request/Response objects
└── exception/ # Global error handling

---

## Getting Started

### Prerequisites

- Docker
- Java 17+
- Maven

### Run locally

```bash
# Start PostgreSQL
docker-compose up -d

# Start the API
./mvnw spring-boot:run
```

API available at `http://localhost:8080`
Swagger UI at `http://localhost:8080/swagger-ui/index.html`

---

## API Endpoints

| Method | Endpoint                                | Description                |
| ------ | --------------------------------------- | -------------------------- |
| POST   | `/api/accounts`                         | Create a new bank account  |
| GET    | `/api/accounts`                         | Get all accounts           |
| GET    | `/api/accounts/{accountNumber}`         | Get account by number      |
| POST   | `/api/payments`                         | Initiate a payment         |
| POST   | `/api/payments/{paymentRef}/refund`     | Refund a completed payment |
| GET    | `/api/payments`                         | Get all payments           |
| GET    | `/api/payments/account/{accountNumber}` | Get payments by account    |

---

## Example Workflow

### 1. Create accounts

```json
{
  "accountNumber": "LT647044001231456789",
  "ownerName": "Jonas Jonaitis",
  "balance": 5000.0,
  "currency": "EUR"
}
```

### 2. Initiate payment

```json
{
  "paymentRef": "PAY-20240501-001",
  "senderAccountNumber": "LT647044001231456789",
  "receiverAccountNumber": "LT647044001231456790",
  "amount": 500.0,
  "currency": "EUR"
}
```

### 3. Successful response

```json
{
  "paymentRef": "PAY-20240501-001",
  "status": "COMPLETED",
  "amount": 500.0,
  "currency": "EUR"
}
```

### 4. Insufficient funds response

```json
{
  "error": "Insufficient funds in account: LT647044001231456789",
  "timestamp": "2026-05-06T00:37:39.591881"
}
```

### 5. Refund

POST /api/payments/PAY-20240501-001/refund

```json
{
  "paymentRef": "PAY-20240501-001",
  "status": "REFUNDED"
}
```

---

## Author

Built as a portfolio project demonstrating knowledge of core banking payment workflows, account-to-account transfers, and backend development with Java and Spring Boot.
