# PharmaOrder Backend - Microservices

Enterprise-grade Spring Boot microservices architecture for the PharmaOrder pharmacy e-commerce platform.

## 📋 Overview

The backend consists of 12 microservices following domain-driven design principles, with infrastructure services for configuration, discovery, and API gateway.

## 🏗️ Architecture

### Infrastructure Services

| Service | Port | Purpose |
|---------|------|---------|
| **Config Server** | 8888 | Centralized configuration management |
| **Eureka Server** | 8761 | Service discovery and registration |
| **API Gateway** | 8080 | Single entry point, routing, security |

### Business Services

| Service | Port | Purpose |
|---------|------|---------|
| **User Service** | 8081 | Authentication, authorization, user management |
| **Product Service** | 8082 | Product catalogue, categories, search |
| **Inventory Service** | 8083 | Stock management, reservations |
| **Prescription Service** | 8084 | Prescription upload and validation |
| **Order Service** | 8085 | Cart, checkout, order lifecycle |
| **Notification Service** | 8086 | Email/SMS notifications |
| **File Service** | 8087 | File upload/download, S3 integration |
| **Loyalty Service** | 8088 | Points, discounts, offers |

### Common Module

Shared utilities, constants, and exception handlers used across all services.

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.4
- **Cloud**: Spring Cloud 2023.0.0
- **Language**: Java 17
- **Build Tool**: Maven 3.9+
- **Database**: PostgreSQL 15+
- **Cache**: Redis 7+
- **Message Queue**: RabbitMQ 3.12+
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Security**: Spring Security, JWT
- **File Storage**: AWS S3 / MinIO
- **Monitoring**: Spring Boot Actuator, Micrometer

## 📁 Project Structure

```
backend/
├── common/                      # Shared module
│   └── src/main/java/com/pharmaorder/common/
│       ├── constants/           # Shared constants
│       └── exception/           # Global exception handlers
├── config-server/               # Configuration server
├── eureka-server/               # Service registry
├── api-gateway/                 # API Gateway
├── user-service/                # User & auth service
├── product-service/             # Product catalogue
├── inventory-service/           # Inventory management
├── prescription-service/        # Prescription handling
├── order-service/               # Order management
├── notification-service/        # Notifications
├── file-service/                # File storage
├── loyalty-service/             # Loyalty program
├── docker-compose.yml           # Docker orchestration
└── pom.xml                      # Parent POM
```

## 🚀 Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.9+
- Docker and Docker Compose
- PostgreSQL 15+
- Redis 7+
- RabbitMQ 3.12+

### Quick Start with Docker

```bash
# Start all infrastructure services
docker-compose up -d postgres redis rabbitmq minio

# Build all services
mvn clean install -DskipTests

# Start services using Docker Compose
docker-compose up -d
```

### Manual Start (Development)

#### 1. Start Infrastructure Services

```bash
docker-compose up -d postgres redis rabbitmq minio
```

#### 2. Start Services in Order

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

# 4. Business Services (can start in parallel)
cd ../user-service && mvn spring-boot:run &
cd ../product-service && mvn spring-boot:run &
cd ../inventory-service && mvn spring-boot:run &
cd ../prescription-service && mvn spring-boot:run &
cd ../order-service && mvn spring-boot:run &
cd ../notification-service && mvn spring-boot:run &
cd ../file-service && mvn spring-boot:run &
cd ../loyalty-service && mvn spring-boot:run &
```

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the backend directory:

```env
# Database
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_USER=pharmauser
POSTGRES_PASSWORD=yourpassword

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# JWT
JWT_SECRET_KEY=your-256-bit-secret-key-base64-encoded
JWT_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# AWS S3
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
AWS_S3_BUCKET=pharmaorder-files
AWS_REGION=us-east-1

# MinIO (Local Development)
MINIO_URL=http://localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin

# Email
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
```

### Database Setup

```bash
# Create databases
psql -U postgres

CREATE DATABASE user_db;
CREATE DATABASE product_db;
CREATE DATABASE inventory_db;
CREATE DATABASE prescription_db;
CREATE DATABASE order_db;
CREATE DATABASE notification_db;
CREATE DATABASE loyalty_db;

CREATE USER pharmauser WITH PASSWORD 'yourpassword';

GRANT ALL PRIVILEGES ON DATABASE user_db TO pharmauser;
GRANT ALL PRIVILEGES ON DATABASE product_db TO pharmauser;
GRANT ALL PRIVILEGES ON DATABASE inventory_db TO pharmauser;
GRANT ALL PRIVILEGES ON DATABASE prescription_db TO pharmauser;
GRANT ALL PRIVILEGES ON DATABASE order_db TO pharmauser;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO pharmauser;
GRANT ALL PRIVILEGES ON DATABASE loyalty_db TO pharmauser;
```

## 🔐 Security

### JWT Authentication

All services (except auth endpoints) require JWT authentication:

```http
Authorization: Bearer <access_token>
```

### Role-Based Access Control

- **ROLE_CUSTOMER**: Browse, order, upload prescriptions
- **ROLE_PHARMACIST**: Validate prescriptions, view orders
- **ROLE_ADMIN**: Full system access

### Rate Limiting

Implemented at API Gateway level using Bucket4j + Redis:

- Anonymous: 30 requests/minute
- Authenticated: 100 requests/minute
- Admin: 500 requests/minute
- Login endpoint: 10 requests/minute (per IP)

## 📡 Service Communication

### Synchronous (REST)

Services communicate via OpenFeign clients with Eureka service discovery:

```java
@FeignClient(name = "product-service")
public interface ProductServiceClient {
    @GetMapping("/api/v1/products/{id}")
    ProductResponse getProduct(@PathVariable UUID id);
}
```

### Asynchronous (Events)

Services publish and consume events via RabbitMQ:

```java
// Publisher
rabbitTemplate.convertAndSend("order.exchange", "order.placed", event);

// Consumer
@RabbitListener(queues = "order.placed.queue")
public void handleOrderPlaced(OrderPlacedEvent event) {
    // Handle event
}
```

## 📊 Monitoring & Observability

### Health Checks

All services expose health endpoints:

```bash
# Check service health
curl http://localhost:8081/actuator/health

# Check all registered services
curl http://localhost:8761/eureka/apps
```

### Metrics

Prometheus-compatible metrics available at:

```bash
curl http://localhost:8081/actuator/prometheus
```

### Distributed Tracing

Zipkin integration for request tracing:

```bash
# Access Zipkin UI
http://localhost:9411
```

### Logging

Structured JSON logging with correlation IDs for request tracking.

## 🧪 Testing

### Unit Tests

```bash
# Test all services
mvn test

# Test specific service
cd user-service
mvn test
```

### Integration Tests

```bash
# Run integration tests
mvn verify -P integration-tests
```

### API Testing

Import Postman collection from `docs/postman/` directory.

## 🐳 Docker Deployment

### Build Images

```bash
# Build all service images
mvn clean package
docker-compose build
```

### Run Containers

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### Docker Compose Services

```yaml
services:
  - postgres
  - redis
  - rabbitmq
  - minio
  - config-server
  - eureka-server
  - api-gateway
  - user-service
  - product-service
  - inventory-service
  - prescription-service
  - order-service
  - notification-service
  - file-service
  - loyalty-service
  - zipkin
```

## 📚 API Documentation

### Swagger UI

Access Swagger documentation for each service:

- User Service: http://localhost:8081/swagger-ui.html
- Product Service: http://localhost:8082/swagger-ui.html
- Order Service: http://localhost:8085/swagger-ui.html

### API Gateway Routes

All APIs are accessible through the gateway at `http://localhost:8080`:

```
/api/v1/auth/**         → User Service
/api/v1/users/**        → User Service
/api/v1/products/**     → Product Service
/api/v1/categories/**   → Product Service
/api/v1/inventory/**    → Inventory Service
/api/v1/prescriptions/** → Prescription Service
/api/v1/orders/**       → Order Service
/api/v1/loyalty/**      → Loyalty Service
/api/v1/files/**        → File Service
```

## 🔍 Troubleshooting

### Service Not Registering with Eureka

1. Check Eureka Server is running: http://localhost:8761
2. Verify `eureka.client.service-url.defaultZone` in service config
3. Check network connectivity
4. Review service logs for errors

### Database Connection Issues

1. Verify PostgreSQL is running: `docker ps | grep postgres`
2. Check database credentials in configuration
3. Ensure databases are created
4. Test connection: `psql -h localhost -U pharmauser -d user_db`

### RabbitMQ Connection Issues

1. Check RabbitMQ is running: http://localhost:15672
2. Verify credentials (default: guest/guest)
3. Check queue and exchange configuration
4. Review RabbitMQ logs

### Redis Connection Issues

1. Verify Redis is running: `redis-cli ping`
2. Check Redis host and port in configuration
3. Test connection: `redis-cli -h localhost -p 6379`

## 📈 Performance Optimization

### Caching Strategy

- Product data cached in Redis (10-minute TTL)
- User sessions cached
- Rate limit counters in Redis

### Database Optimization

- Indexes on frequently queried columns
- Connection pooling (HikariCP)
- Query optimization with JPA Specifications

### Async Processing

- Email notifications processed asynchronously
- Event-driven architecture for non-critical operations
- Background jobs for cleanup and maintenance

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean install
      - name: Run tests
        run: mvn test
      - name: Build Docker images
        run: docker-compose build
```

## 📄 License

MIT License - Part of PharmaOrder Platform

## 🤝 Contributing

1. Follow Java coding conventions
2. Write unit tests for new features
3. Update API documentation
4. Follow microservice best practices
5. Use meaningful commit messages

## 📞 Support

For issues and questions:
- Create GitHub issue
- Contact: backend-team@pharmaorder.com

---

**Built with ❤️ by the PharmaOrder Backend Team**
