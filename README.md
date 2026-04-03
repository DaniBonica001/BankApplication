# BankApplication

Two Spring Boot microservices (`account` and `client`) that model a simple banking system with clients, accounts, transactions, overdraft protection, and reporting.

## Architecture

- **client service** (port **8001**)
  - Owns `Person`/`Client` domain.
  - Exposes CRUD REST API at `/api/clients`.
  - Persists data in its own PostgreSQL database.

- **account service** (port **8000**)
  - Owns `Account` and `Transaction` domains.
  - Exposes:
    - `/api/accounts` for account CRUD.
    - `/api/transactions` for registering and listing transactions.
    - `/api/transactions/client/{clientId}/report` for client account statements over a date range.
  - Publishes transaction events to Kafka (`app.kafka.topic.account-transactions`).
  - Persists data in its own PostgreSQL database.

Both services follow the BankApplication Constitution: Java 17, Spring Boot 2.4.2 (MVC), JPA to Postgres, H2 for tests, Kafka for asynchronous events, English + camelCase, and strong automated tests.

## Tech Stack

- Java **17**
- Spring Boot **2.4.2** (Web, Data JPA, Validation)
- PostgreSQL (runtime DBs for `account` and `client`)
- H2 (in-memory DB for tests)
- Apache Kafka + Spring for Apache Kafka
- Maven (with wrappers in each module)
- Docker & Docker Compose

## Project Structure

```text
account/
  src/main/java/com/devsu/hackerearth/backend/account/...
  src/test/java/com/devsu/hackerearth/backend/account/...
  pom.xml

client/
  src/main/java/com/devsu/hackerearth/backend/client/...
  src/test/java/com/devsu/hackerearth/backend/client/...
  pom.xml

specs/001-account-client-transactions-api/
  spec.md        # Feature specification (user stories, requirements)
  plan.md        # Implementation plan
  data-model.md  # Domain model
  research.md    # Design research
  quickstart.md  # Detailed run instructions
  contracts/     # HTTP + Kafka contracts
  tasks.md       # Implementation task list (phased)
```

## Prerequisites

- Java 17 (JDK)
- Maven 3.x
- Docker and Docker Compose (for the full environment)

## Build

From the repository root:

```bash
mvn clean package -DskipTests
```

This builds both `account` and `client` modules using Java 17 and Spring Boot 2.4.2.

To build a single module:

```bash
cd account
./mvnw clean package -DskipTests

cd ../client
./mvnw clean package -DskipTests
```

## Run with Docker Compose

A Docker Compose file is provided to start both services plus their dependencies (Postgres and Kafka).

From the repository root:

```bash
docker-compose up --build
```

This starts:

- `account` service on `http://localhost:8000`
- `client` service on `http://localhost:8001`
- PostgreSQL databases for each service (host ports typically 5433 and 5434)
- Kafka broker (for account transactions topic)

Check health (if actuator is enabled) or hit basic endpoints, for example:

```bash
curl http://localhost:8000/api/accounts
curl http://localhost:8001/api/clients
```

## Run Locally without Docker

You can also run the services directly with Maven. Make sure Postgres and Kafka are available and match the configuration in each module's `application.properties`.

```bash
cd account
./mvnw spring-boot:run

# in another terminal
cd client
./mvnw spring-boot:run
```

Services will listen on:

- `account`: `http://localhost:8000`
- `client`: `http://localhost:8001`

## Key REST Endpoints

### Client Service (`client`)

Base URL: `http://localhost:8001`

- `POST /api/clients` – create a new client
- `GET /api/clients` – list clients
- `GET /api/clients/{id}` – get a client by id
- `PUT /api/clients/{id}` – update a client
- `PATCH /api/clients/{id}` – partial update (e.g., activate/deactivate)
- `DELETE /api/clients/{id}` – delete/deactivate a client

### Account Service (`account`)

Base URL: `http://localhost:8000`

**Accounts**

- `POST /api/accounts` – create a new account for a client
- `GET /api/accounts` – list accounts
- `GET /api/accounts/{id}` – get account by id
- `PUT /api/accounts/{id}` – update account
- `PATCH /api/accounts/{id}` – partial update
- `DELETE /api/accounts/{id}` – delete/deactivate account

**Transactions**

- `POST /api/transactions` – register a transaction (positive or negative amount)
- `GET /api/transactions` – list transactions
- `GET /api/transactions/{id}` – get transaction by id

Behavior:

- On valid transactions, the account's `currentBalance` is updated and the transaction is persisted.
- On a debit that would overdraw the account, the request is rejected with HTTP 400 and JSON body:

  ```json
  { "message": "Saldo no disponible" }
  ```

  No transaction is stored and the balance remains unchanged.

**Client Account Statement Report**

- `GET /api/transactions/client/{clientId}/report?dateTransactionStart=yyyy-MM-dd&dateTransactionEnd=yyyy-MM-dd`

Returns a JSON array of rows summarizing the client's accounts and transactions in the given date range. Each element is a `BankStatementDto` containing:

- `date` – transaction date
- `client` – client identifier (as string)
- `accountNumber` – account number
- `accountType` – type of account
- `initialAmount` – initial amount at account creation
- `active` – whether the account is active
- `transactionType` – transaction type (e.g., CREDIT/DEBIT)
- `amount` – transaction amount
- `balance` – balance after the transaction

Validation:

- If `dateTransactionStart` is after `dateTransactionEnd`, the API returns HTTP 400 with `{ "message": "Invalid date range" }`.
- If no accounts are found for the given `clientId`, the API returns HTTP 400 with

  ```json
  { "message": "No accounts found for client id: <id>" }
  ```

## Postman Collection

A ready-to-use Postman collection is provided with all major endpoints grouped by microservice and controller:

- [postman-collection.json](postman-collection.json) (legacy root-level file)
- [specs/001-account-client-transactions-api/postman-collection.json](specs/001-account-client-transactions-api/postman-collection.json)

Import one of these into Postman. It defines:

- Variables `{{account_base_url}}` and `{{client_base_url}}` (defaulting to `http://localhost:8000` and `http://localhost:8001`).
- Requests for all `/api/clients`, `/api/accounts`, `/api/transactions`, and the report endpoint.

## Testing

Both microservices use H2 in tests so they do not require running PostgreSQL.

From the repository root:

```bash
mvn test
```

Or per module:

```bash
cd account
./mvnw test

cd ../client
./mvnw test
```

Notable tests include:

- `TransactionBalanceTests` – unit tests for debit/credit balance calculations.
- `InsufficientFundsTests` and `InsufficientFundsIntegrationTests` – ensure overdrafts are rejected and balances remain unchanged.
- `ReportIntegrationTests` and `ReportErrorTests` – verify client report contents and error handling for bad input.

## Further Documentation

For more detailed design and implementation notes, see:

- [specs/001-account-client-transactions-api/spec.md](specs/001-account-client-transactions-api/spec.md)
- [specs/001-account-client-transactions-api/plan.md](specs/001-account-client-transactions-api/plan.md)
- [specs/001-account-client-transactions-api/data-model.md](specs/001-account-client-transactions-api/data-model.md)
- [specs/001-account-client-transactions-api/contracts/http-apis.md](specs/001-account-client-transactions-api/contracts/http-apis.md)
- [specs/001-account-client-transactions-api/quickstart.md](specs/001-account-client-transactions-api/quickstart.md)
