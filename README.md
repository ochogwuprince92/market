# Market API

A production-ready RESTful marketplace backend built with Spring Boot 4. The API supports full e-commerce functionality including user management, product listings, cart operations, order processing, and payment integration via Paystack.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [API Reference](#api-reference)
- [Authentication](#authentication)
- [Role-Based Access Control](#role-based-access-control)
- [Payment Integration](#payment-integration)
- [Deployment](#deployment)
- [Contributing](#contributing)

---

## Overview

Market API is a backend service for a marketplace platform. It allows customers to browse products, manage their cart, place orders, and pay securely via Paystack. Sellers can list and manage their own products. Admins have full access across the system.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0 |
| Security | Spring Security 6 + JWT (jjwt 0.12.5) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Containerization | Docker |
| Payment | Paystack |
| HTTP Client | OkHttp3 |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Maven |
| Cloud | AWS |

---

## Features

- JWT-based stateless authentication
- Role-based access control (ADMIN, SELLER, CUSTOMER)
- BCrypt password hashing
- User registration and login
- Product management with user association
- Shopping cart management
- Order creation and status tracking
- Paystack payment initialization and verification
- Webhook handling for real-time payment updates
- Structured error handling and validation
- Swagger UI documentation

---

## Architecture

```
market/
├── config/          # Security, JWT, HTTP client configuration
├── controller/      # REST API endpoints
├── dto/             # Request and response data transfer objects
│   ├── auth/
│   ├── cart/
│   ├── order/
│   ├── payment/
│   ├── product/
│   └── user/
├── entity/          # JPA entities
│   ├── cart/
│   ├── order/
│   ├── product/
│   └── user/
├── exception/       # Custom exceptions and global exception handler
├── repository/      # Spring Data JPA repositories
└── service/         # Business logic
```

---

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 15+
- Docker (optional)

### Local Setup

**1. Clone the repository:**
```bash
git clone https://github.com/your-username/market.git
cd market
```

**2. Create the database:**
```sql
CREATE DATABASE market_db;
```

**3. Create `src/main/resources/application-local.properties`:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/market_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
app.jwt.secret=your_jwt_secret_minimum_64_characters_long
app.jwt.expiration=86400000
paystack.api.key=your_paystack_public_key
paystack.api.secret=your_paystack_secret_key
paystack.api.url=https://api.paystack.co
paystack.callback.url=https://your-ngrok-url/api/v1/webhooks/paystack
```

**4. Activate the local profile in IntelliJ:**

Run → Edit Configurations → Environment variables:
```
SPRING_PROFILES_ACTIVE=local
```

**5. Run the application:**
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8085`

Swagger UI: `http://localhost:8085/swagger-ui.html`

### Docker Setup

**Build and run with Docker:**
```bash
docker build -t market-api .
docker run -p 8085:8085 \
  -e DATASOURCE_URL=jdbc:postgresql://host:5432/market_db \
  -e DATASOURCE_USERNAME=postgres \
  -e DATASOURCE_PASSWORD=your_password \
  -e JWT_SECRET=your_jwt_secret \
  -e PAYSTACK_API_KEY=your_paystack_key \
  -e PAYSTACK_SECRET_KEY=your_paystack_secret \
  market-api
```

---

## Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `DATASOURCE_URL` | PostgreSQL JDBC connection URL | Yes |
| `DATASOURCE_USERNAME` | Database username | Yes |
| `DATASOURCE_PASSWORD` | Database password | Yes |
| `JWT_SECRET` | Secret key for signing JWT tokens (min 64 chars) | Yes |
| `JWT_EXPIRATION` | Token expiry in milliseconds (default: 86400000) | No |
| `PAYSTACK_API_KEY` | Paystack public key | Yes |
| `PAYSTACK_SECRET_KEY` | Paystack secret key | Yes |
| `PAYSTACK_API_URL` | Paystack base URL | Yes |
| `APP_CALLBACK_URL` | Payment callback URL | Yes |
| `PORT` | Server port (default: 8080) | No |

---

## API Reference

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/v1/auth/register` | Register a new user | No |
| POST | `/api/v1/auth/login` | Login and get JWT token | No |
| GET | `/api/v1/auth/health` | Auth service health check | No |

**Register:**
```json
POST /api/v1/auth/register
{
  "name": "John Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "id": "8639d76f-9f5b-431b-beb7-4ac11aaf6b88",
  "email": "john@example.com",
  "name": "John Doe",
  "roles": ["CUSTOMER"],
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "message": "Registration successful"
}
```

**Login:**
```json
POST /api/v1/auth/login
{
  "email": "john@example.com",
  "password": "password123"
}
```

---

### Users

> Requires `ADMIN` role

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/user/create` | Create a user |
| GET | `/api/v1/user` | Get all users |
| GET | `/api/v1/user/{id}` | Get user by ID |
| PUT | `/api/v1/user/{id}` | Update user |
| DELETE | `/api/v1/user/{id}` | Delete user |

---

### Products

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/v1/product` | Get all products | No |
| GET | `/api/v1/product/{id}` | Get product by ID | No |
| POST | `/api/v1/product/add` | Add a product | SELLER, ADMIN |
| PUT | `/api/v1/product/{id}` | Update a product | SELLER, ADMIN |
| DELETE | `/api/v1/product/{id}` | Delete a product | SELLER, ADMIN |
| POST | `/api/v1/product/{userId}/products` | Add product to user | SELLER, ADMIN |
| GET | `/api/v1/product/{userId}/products` | Get products by user | SELLER, ADMIN |

**Add Product:**
```json
POST /api/v1/product/add
Authorization: Bearer <token>
{
  "name": "Wireless Headphones",
  "description": "Noise cancelling headphones",
  "price": 25000.00,
  "quantity": 10
}
```

---

### Cart

> Requires authentication (CUSTOMER, SELLER, ADMIN)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/carts/user/{userId}` | Create cart for user |
| GET | `/api/v1/carts/user/{userId}` | Get cart by user |
| POST | `/api/v1/carts/addProduct` | Add product to cart |
| DELETE | `/api/v1/carts/{cartId}/product/{productId}` | Remove product from cart |

**Add Product to Cart:**
```json
POST /api/v1/carts/addProduct
Authorization: Bearer <token>
{
  "cartId": "uuid",
  "productId": "uuid",
  "quantity": 2
}
```

---

### Orders

> Requires authentication (CUSTOMER, SELLER, ADMIN)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/orders/user/{userId}` | Create an order |
| GET | `/api/v1/orders/{orderId}` | Get order by ID |
| GET | `/api/v1/orders/user/{userId}` | Get all orders for a user |
| PUT | `/api/v1/orders/{orderId}/status` | Update order status |

**Order Status values:** `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`

---

### Payments

> Requires authentication (CUSTOMER, SELLER, ADMIN)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/payments/initialize` | Initialize payment for an order |
| GET | `/api/v1/payments/verify/{reference}` | Verify payment by reference |
| GET | `/api/v1/payments/status/{orderId}` | Get payment status for an order |

**Initialize Payment:**
```json
POST /api/v1/payments/initialize
Authorization: Bearer <token>
{
  "orderId": "uuid",
  "email": "john@example.com",
  "callbackUrl": "https://your-frontend.com/payment/callback"
}
```

---

### Webhooks

> Public — no authentication required

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/webhooks/paystack` | Handle Paystack payment webhook |
| GET | `/api/v1/webhooks/paystack/health` | Webhook health check |

---

## Authentication

The API uses stateless JWT authentication. Include the token in every protected request:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

Tokens are valid for 24 hours by default. A new token is issued on each login.

---

## Role-Based Access Control

| Role | Permissions |
|------|-------------|
| `ADMIN` | Full access to all endpoints |
| `SELLER` | Manage own products, access cart, orders, payments |
| `CUSTOMER` | Browse products, manage cart, place orders, make payments |
| Unauthenticated | View products, register, login, webhooks only |

New users are assigned the `CUSTOMER` role by default. Roles can be updated directly in the database by an admin.

---

## Payment Integration

The API integrates with [Paystack](https://paystack.com) for payment processing.

**Payment flow:**

1. Customer creates an order
2. Customer calls `/api/v1/payments/initialize` with the order ID
3. API returns a Paystack authorization URL
4. Customer completes payment on Paystack's hosted page
5. Paystack sends a webhook to `/api/v1/webhooks/paystack`
6. API verifies the signature and marks the order as paid

For local development, use [ngrok](https://ngrok.com) to expose your local server for webhook testing:
```bash
ngrok http 8085
```

Then set `APP_CALLBACK_URL` to your ngrok URL.

---

## Deployment

The API is deployed on **AWS** using **Docker**.

**Build the Docker image:**
```bash
docker build -t market-api .
```

**Push to AWS ECR:**
```bash
aws ecr get-login-password --region your-region | docker login --username AWS --password-stdin your-account-id.dkr.ecr.your-region.amazonaws.com
docker tag market-api:latest your-account-id.dkr.ecr.your-region.amazonaws.com/market-api:latest
docker push your-account-id.dkr.ecr.your-region.amazonaws.com/market-api:latest
```

Set all required environment variables in your AWS ECS task definition or EC2 instance.

---

## Error Responses

All errors return a consistent JSON structure:

```json
{
  "timestamp": "2026-05-16T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "User Not Found",
  "path": "/api/v1/user/uuid"
}
```

| Status Code | Meaning |
|-------------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request / Validation Error |
| 401 | Unauthorized — invalid or missing token |
| 403 | Forbidden — insufficient role |
| 404 | Resource not found |
| 409 | Conflict — duplicate resource |
| 500 | Internal Server Error |

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feat/your-feature`
3. Commit your changes: `git commit -m "feat: add your feature"`
4. Push to the branch: `git push origin feat/your-feature`
5. Open a Pull Request

---
                    
## Ochogwu Prince 
