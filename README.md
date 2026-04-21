# PharmaOrder - Enterprise Pharmacy E-Commerce Platform

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![React](https://img.shields.io/badge/React-19.2.0-blue.svg)](https://reactjs.org/)
[![TanStack](https://img.shields.io/badge/TanStack-Router%20%26%20Query-red.svg)](https://tanstack.com/)
[![Microservices](https://img.shields.io/badge/Architecture-Microservices-blueviolet.svg)](https://microservices.io/)

## 📋 Table of Contents

- [Overview](#overview)
- [System Architecture](#system-architecture)
- [Microservices Overview](#microservices-overview)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Deployment](#deployment)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

PharmaOrder is a cloud-native, microservices-based pharmacy e-commerce platform built with Spring Boot and React. It enables customers to browse medicines, upload prescriptions, place orders, and track delivery while providing pharmacy administrators complete control over inventory, prescription validation, and catalogue management.

### Core Capabilities

- 🔍 **Smart Medicine Search** - Browse with category, dosage, and packaging filters
- 📄 **Prescription Management** - Secure upload and pharmacist validation workflow
- 🛒 **Order Management** - End-to-end order lifecycle with inventory integration
- 👨‍⚕️ **Admin Panel** - Centralized medicine, category, and stock management
- 🔐 **Enterprise Security** - JWT authentication with role-based authorization
- 🚦 **Rate Limiting** - Prevent abuse with intelligent request throttling
- 📧 **Email Notifications** - Automated order and prescription status updates
- 🎁 **Loyalty Program** - Points-based rewards and discount management

## 🏗️ System Architecture

```
                          ┌─────────────────────────────┐
                          │      API GATEWAY (8080)     │
                          │  Spring Cloud Gateway       │
                          │  • JWT Validation           │
                          │  • Rate Limiting (Redis)    │
                          │  • Request Routing          │
                          │  • Circuit Breaker          │
                          └──────────────┬──────────────┘
                                         │
           ┌─────────────────────────────┼─────────────────────────────┐
           │                             │                             │
    ┌──────▼──────┐             ┌────────▼────────┐           ┌───────▼───────┐
    │   USER      │             │   PRODUCT /     │           │   ORDER       │
    │  SERVICE    │             │   CATALOGUE     │           │   SERVICE     │
    │  (8081)     │             │   SERVICE       │           │   (8085)      │
    │             │             │   (8082)        │           │               │
    │ • Auth/JWT  │             │                 │           │ • Cart        │
    │ • Profile   │             │ • Products      │           │ • Checkout    │
    │ • Addresses │             │ • Categories    │           │ • Lifecycle   │
    └─────────────┘             │ • Search        │           └───────────────┘
                                └─────────────────┘
           │                             │                             │
    ┌──────▼──────┐             ┌────────▼────────┐           ┌───────▼───────┐
    │PRESCRIPTION │             │   INVENTORY     │           │ NOTIFICATION  │
    │  SERVICE    │             │   SERVICE       │           │   SERVICE     │
    │  (8084)     │             │   (8083)        │           │   (8086)      │
    │             │             │                 │           │               │
    │ • Upload    │             │ • Stock Mgmt    │           │ • Email       │
    │ • Validate  │             │ • Reservation   │           │ • Templates   │
    │ • Approval  │             │ • Audit Log     │           │ • Queue       │
    └─────────────┘             └─────────────────┘           └───────────────┘
           │                                                          │
    ┌──────▼──────┐      ┌──────────────────┐              ┌─────────▼──────┐
    │    FILE     │      │  SERVICE REGISTRY │              │  MESSAGE QUEUE │
    │  SERVICE    │      │  EUREKA SERVER    │              │   RabbitMQ /   │
    │  (8087)     │      │     (8761)        │              │    Kafka       │
    │             │      │                   │              │                │
    │ • S3/MinIO  │      │ • Discovery       │              │ • Async Events │
    │ • Pre-sign  │      │ • Health Check    │              │ • Pub/Sub      │
    └─────────────┘      └──────────────────┘              └────────────────┘
           │
    ┌──────▼──────┐      ┌──────────────────┐
    │  LOYALTY /  │      │  CONFIG SERVER   │
    │ OFFERS SVC  │      │     (8888)       │
    │  (8088)     │      │                  │
    │             │      │ • Centralized    │
    │ • Points    │      │   Configuration  │
    │ • Discounts │      │ • Git Backend    │
    └─────────────┘      └──────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                         │
│                                                                 │
│  PostgreSQL  │  Redis  │  MinIO/S3  │  Zipkin  │  Prometheus  │
└─────────────────────────────────────────────────────────────────┘
```

## 🔧 Microservices Overview

| Service | Port | Purpose | Database | Key Technologies |
|---------|------|---------|----------|------------------|
| **API Gateway** | 8080 | Single entry point, routing, security | Redis | Spring Cloud Gateway, Bucket4j |
| **Eureka Server** | 8761 | Service discovery and registration | - | Spring Cloud Netflix Eureka |
| **Config Server** | 8888 | Centralized configuration management | - | Spring Cloud Config |
| **User Service** | 8081 | Authentication, authorization, profiles | PostgreSQL | Spring Security, JWT, BCrypt |
| **Product Service** | 8082 | Medicine catalogue, categories, search | PostgreSQL | Spring Data JPA, Redis Cache |
| **Inventory Service** | 8083 | Stock management, reservations | PostgreSQL | Optimistic Locking, Events |
| **Prescription Service** | 8084 | Upload, validation, approval workflow | PostgreSQL | File integration |
| **Order Service** | 8085 | Cart, checkout, order lifecycle | PostgreSQL | Saga Pattern, Events |
| **Notification Service** | 8086 | Email/SMS notifications | PostgreSQL | RabbitMQ, Thymeleaf |
| **File Service** | 8087 | File upload/download, S3 integration | - | AWS S3, MinIO |
| **Loyalty Service** | 8088 | Points, discounts, offers | PostgreSQL | Event-driven |

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.4, Spring Cloud 2023.0.0
- **Language**: Java 17
- **Security**: Spring Security, JWT (JJWT 0.12.3), BCrypt
- **Database**: PostgreSQL 15+
- **Caching**: Redis 7+
- **Message Queue**: RabbitMQ / Apache Kafka
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Rate Limiting**: Bucket4j
- **Resilience**: Resilience4j (Circuit Breaker, Retry)
- **File Storage**: AWS S3 / MinIO
- **Build Tool**: Maven 3.9+
- **Monitoring**: Spring Boot Actuator, Micrometer, Zipkin
- **Documentation**: Swagger/OpenAPI 3.0

### Frontend
- **Framework**: React 19.2.0
- **Routing**: TanStack Router 1.168.0
- **State Management**: Zustand 5.0.12, TanStack Query 5.99.2
- **UI Components**: Radix UI, Tailwind CSS 4.2.1
- **Forms**: React Hook Form 7.71.2, Zod 3.24.2
- **HTTP Client**: Axios 1.15.1
- **Build Tool**: Vite 7.3.1
- **Language**: TypeScript 5.8.3

### DevOps
- **Containerization**: Docker, Docker Compose
- **CI/CD**: GitHub Actions (recommended)
- **Monitoring**: Prometheus, Grafana
- **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Tracing**: Zipkin, Spring Cloud Sleuth

## ✨ Features

### Customer Features
- ✅ User registration and authentication
- ✅ Browse medicines by category, brand, dosage form
- ✅ Advanced search with filters (price range, prescription required)
- ✅ Upload prescription documents (PDF, JPG, PNG)
- ✅ Shopping cart management
- ✅ Prescription validation before checkout
- ✅ Multiple delivery addresses
- ✅ Order history and tracking
- ✅ Quick reorder from past orders
- ✅ Loyalty points earning and redemption
- ✅ Email notifications for order status

### Pharmacist Features
- ✅ Prescription review queue
- ✅ Approve/reject prescriptions with notes
- ✅ View prescription images via secure URLs
- ✅ Inventory visibility

### Admin Features
- ✅ Complete product catalogue management
- ✅ Category and brand management
- ✅ Inventory management with batch tracking
- ✅ Order management and status updates
- ✅ User management and role assignment
- ✅ Discount code and offer management
- ✅ Low stock alerts
- ✅ Inventory audit logs
- ✅ System-wide analytics

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17+**
- **Maven 3.9+**
- **Node.js 18+ and npm/yarn**
- **Docker and Docker Compose**
- **PostgreSQL 15+** (or use Docker)
- **Redis 7+** (or use Docker)
- **Git**

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/pharmaorder.git
cd pharmaorder
```

### 2. Start Infrastructure Services

```bash
cd backend
docker-compose up -d postgres redis rabbitmq minio
```

This starts:
- PostgreSQL on port 5432
- Redis on port 6379
- RabbitMQ on port 5672 (Management UI: 15672)
- MinIO on port 9000 (Console: 9001)

### 3. Build Backend Services

```bash
# From backend directory
mvn clean install -DskipTests
```

### 4. Start Backend Services (in order)

```bash
# 1. Config Server (must start first)
cd config-server
mvn spring-boot:run

# 2. Eureka Server (wait for config server)
cd ../eureka-server
mvn spring-boot:run

# 3. API Gateway (wait for Eureka)
cd ../api-gateway
mvn spring-boot:run

# 4. Start all microservices (in separate terminals or use Docker Compose)
cd ../user-service && mvn spring-boot:run
cd ../product-service && mvn spring-boot:run
cd ../inventory-service && mvn spring-boot:run
cd ../prescription-service && mvn spring-boot:run
cd ../order-service && mvn spring-boot:run
cd ../notification-service && mvn spring-boot:run
cd ../file-service && mvn spring-boot:run
cd ../loyalty-service && mvn spring-boot:run
```

**Alternative**: Use Docker Compose to start all services:

```bash
cd backend
docker-compose up -d
```

### 5. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend will be available at: `http://localhost:3000`

### 6. Access Services

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:3000 | - |
| API Gateway | http://localhost:8080 | - |
| Eureka Dashboard | http://localhost:8761 | - |
| RabbitMQ Management | http://localhost:15672 | guest/guest |
| MinIO Console | http://localhost:9001 | minioadmin/minioadmin |

### 7. Create Admin User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@pharmaorder.com",
    "password": "Admin@123",
    "firstName": "Admin",
    "lastName": "User",
    "phone": "+1234567890",
    "role": "ADMIN"
  }'
```

## 📁 Project Structure

```
pharmaorder/
├── backend/
│   ├── api-gateway/              # API Gateway service
│   ├── common/                   # Shared utilities and constants
│   ├── config-server/            # Configuration server
│   ├── eureka-server/            # Service registry
│   ├── file-service/             # File upload/download service
│   ├── inventory-service/        # Inventory management
│   ├── loyalty-service/          # Loyalty and offers
│   ├── notification-service/     # Email/SMS notifications
│   ├── order-service/            # Order management
│   ├── prescription-service/     # Prescription handling
│   ├── product-service/          # Product catalogue
│   ├── user-service/             # Authentication and users
│   ├── docker-compose.yml        # Docker orchestration
│   └── pom.xml                   # Parent POM
├── frontend/
│   ├── src/
│   │   ├── components/           # Reusable UI components
│   │   ├── routes/               # TanStack Router routes
│   │   ├── lib/                  # Utilities and helpers
│   │   ├── hooks/                # Custom React hooks
│   │   └── stores/               # Zustand stores
│   ├── package.json
│   └── vite.config.ts
├── docs/                         # Additional documentation
├── .gitignore
└── README.md
```

## 📚 API Documentation

### Authentication

All API requests (except registration and login) require JWT authentication:

```bash
Authorization: Bearer <access_token>
```

### Key Endpoints

#### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login and get JWT tokens
- `POST /api/v1/auth/refresh` - Refresh access token
- `POST /api/v1/auth/logout` - Logout and invalidate token

#### Products
- `GET /api/v1/products` - List products (paginated, filterable)
- `GET /api/v1/products/{id}` - Get product details
- `GET /api/v1/products/search?q=` - Search products
- `POST /api/v1/products` - Create product (ADMIN)
- `PUT /api/v1/products/{id}` - Update product (ADMIN)

#### Orders
- `POST /api/v1/orders/cart/add` - Add item to cart
- `GET /api/v1/orders/cart` - View cart
- `POST /api/v1/orders/checkout` - Place order
- `GET /api/v1/orders/me` - View order history
- `GET /api/v1/orders/{id}` - Get order details

#### Prescriptions
- `POST /api/v1/prescriptions/upload` - Upload prescription
- `GET /api/v1/prescriptions/me` - View my prescriptions
- `GET /api/v1/prescriptions/pending` - Pending prescriptions (PHARMACIST)
- `PUT /api/v1/prescriptions/{id}/approve` - Approve prescription (PHARMACIST)

### Swagger Documentation

Once services are running, access Swagger UI:
- User Service: http://localhost:8081/swagger-ui.html
- Product Service: http://localhost:8082/swagger-ui.html
- Order Service: http://localhost:8085/swagger-ui.html

## 🔐 Security

### JWT Token Strategy

- **Access Token**: RS256 signed, 15-minute TTL
  - Payload: `{ sub: userId, email, roles: [...], iat, exp, jti }`
- **Refresh Token**: Opaque token stored in DB, 7-day TTL
- **Token Validation**: Gateway validates using public key (no auth service call)
- **Logout**: Adds JTI to Redis blacklist

### Rate Limiting

Implemented using Bucket4j + Redis:

| User Type | Limit | Refill Rate |
|-----------|-------|-------------|
| Anonymous | 30 requests | 30/minute |
| Authenticated | 100 requests | 100/minute |
| Admin | 500 requests | 500/minute |
| Login Endpoint | 10 requests | 10/minute (per IP) |

### Role-Based Access Control

- **ROLE_CUSTOMER**: Browse, order, upload prescriptions
- **ROLE_PHARMACIST**: Validate prescriptions, view orders
- **ROLE_ADMIN**: Full system access

## 🚢 Deployment

### Docker Deployment

```bash
# Build all services
cd backend
mvn clean package -DskipTests

# Build and start containers
docker-compose up -d --build
```

### Kubernetes Deployment

Kubernetes manifests are available in the `k8s/` directory:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmaps/
kubectl apply -f k8s/secrets/
kubectl apply -f k8s/deployments/
kubectl apply -f k8s/services/
kubectl apply -f k8s/ingress.yaml
```

### Environment Variables

Key environment variables for production:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/pharmaorder
SPRING_DATASOURCE_USERNAME=pharmauser
SPRING_DATASOURCE_PASSWORD=<secure-password>

# Redis
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379

# JWT
JWT_SECRET_KEY=<base64-encoded-secret>
JWT_EXPIRATION=900000

# AWS S3
AWS_ACCESS_KEY_ID=<access-key>
AWS_SECRET_ACCESS_KEY=<secret-key>
AWS_S3_BUCKET=pharmaorder-files

# RabbitMQ
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest

# Email
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=<email>
SPRING_MAIL_PASSWORD=<app-password>
```

## 🧪 Testing

### Run Unit Tests

```bash
cd backend
mvn test
```

### Run Integration Tests

```bash
mvn verify -P integration-tests
```

### Frontend Tests

```bash
cd frontend
npm run test
```

## 📊 Monitoring and Observability

### Health Checks

All services expose health endpoints:
- `GET /actuator/health` - Service health status
- `GET /actuator/info` - Service information
- `GET /actuator/metrics` - Service metrics

### Distributed Tracing

Access Zipkin UI at: http://localhost:9411

### Logging

Logs are aggregated using ELK Stack:
- Elasticsearch: http://localhost:9200
- Kibana: http://localhost:5601

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Coding Standards

- Follow Java Code Conventions
- Use Lombok for boilerplate reduction
- Write unit tests for new features
- Update documentation as needed
- Follow REST API best practices

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.


## 📞 Support

For support and questions:
- 📧 Email: support@pharmaorder.com
- 💬 Slack: [Workspace Link]
- 🐛 Issues: [GitHub Issues](https://github.com/yourusername/pharmaorder/issues)

## 🗺️ Roadmap

- [ ] Mobile app (React Native)
- [ ] Payment gateway integration (Razorpay/Stripe)
- [ ] Real-time order tracking
- [ ] AI-powered medicine recommendations
- [ ] Telemedicine integration
- [ ] Multi-language support
- [ ] Advanced analytics dashboard
- [ ] Subscription-based medicine delivery

---

**Built with ❤️ by the PharmaOrder Team**
