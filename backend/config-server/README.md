# Config Server - Centralized Configuration

Spring Cloud Config Server for managing externalized configuration across all PharmaOrder microservices.

## 📋 Overview

The Config Server provides centralized configuration management for all microservices. Configuration files are stored in the classpath (or Git repository in production) and served to services on startup and refresh.

**Port**: 8888

## 🎯 Responsibilities

- **Centralized Configuration**: Single source of truth for all service configs
- **Environment-Specific Configs**: Support for dev, staging, prod profiles
- **Dynamic Refresh**: Update configs without service restart
- **Encryption/Decryption**: Secure sensitive configuration values
- **Version Control**: Track configuration changes (when using Git backend)
- **Audit Trail**: Log configuration access and changes

## 🛠️ Technology Stack

- Spring Cloud Config Server
- Spring Boot 3.2.4
- Spring Boot Actuator

## 🔧 Configuration

### application.yml

```yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/config
  profiles:
    active: native

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
```

### Main Application Class

```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

## 📁 Configuration Files

Configuration files are stored in `src/main/resources/config/`:

```
config/
├── api-gateway.yml
├── user-service.yml
├── product-service.yml
├── inventory-service.yml
├── prescription-service.yml
├── order-service.yml
├── notification-service.yml
├── file-service.yml
└── loyalty-service.yml
```

### Example: user-service.yml

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/user_db
    username: pharmauser
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect

jwt:
  secret-key: ${JWT_SECRET_KEY}
  expiration: 900000
  refresh-expiration: 604800000

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## 🔌 Client Configuration

Microservices connect to Config Server with `bootstrap.yml`:

```yaml
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      retry:
        max-attempts: 6
        initial-interval: 1000
        multiplier: 1.1
        max-interval: 2000
  config:
    import: optional:configserver:http://localhost:8888
```

## 🔄 Dynamic Configuration Refresh

### Enable Refresh Scope

```java
@RestController
@RefreshScope
public class ConfigController {
    
    @Value("${custom.property}")
    private String customProperty;
    
    @GetMapping("/config")
    public String getConfig() {
        return customProperty;
    }
}
```

### Trigger Refresh

```bash
# Refresh specific service
curl -X POST http://localhost:8081/actuator/refresh

# Refresh all services (using Spring Cloud Bus)
curl -X POST http://localhost:8888/actuator/bus-refresh
```

## 🔐 Encryption/Decryption

### Setup Encryption Key

```yaml
encrypt:
  key: ${ENCRYPT_KEY}
```

### Encrypt Sensitive Values

```bash
# Encrypt a value
curl http://localhost:8888/encrypt -d "mysecretpassword"
# Returns: {cipher}AQA23...encrypted...value

# Decrypt a value
curl http://localhost:8888/decrypt -d "{cipher}AQA23...encrypted...value"
```

### Use Encrypted Values

```yaml
spring:
  datasource:
    password: '{cipher}AQA23...encrypted...value'
```

## 🌐 Git Backend (Production)

For production, use Git repository:

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/yourorg/pharmaorder-config
          username: ${GIT_USERNAME}
          password: ${GIT_PASSWORD}
          default-label: main
          search-paths: '{application}'
          clone-on-start: true
```

### Git Repository Structure

```
pharmaorder-config/
├── api-gateway/
│   ├── api-gateway.yml
│   ├── api-gateway-dev.yml
│   └── api-gateway-prod.yml
├── user-service/
│   ├── user-service.yml
│   ├── user-service-dev.yml
│   └── user-service-prod.yml
└── ...
```

## 📊 Monitoring

### Actuator Endpoints

- `GET /actuator/health` - Config server health
- `GET /actuator/env` - Environment properties
- `POST /actuator/refresh` - Refresh configuration
- `POST /actuator/bus-refresh` - Refresh all services (with Spring Cloud Bus)

### Configuration Endpoints

- `GET /{application}/{profile}` - Get configuration
- `GET /{application}/{profile}/{label}` - Get configuration from specific branch
- `GET /{application}-{profile}.yml` - Get raw YAML
- `GET /{application}-{profile}.properties` - Get raw properties

### Example Requests

```bash
# Get user-service default config
curl http://localhost:8888/user-service/default

# Get user-service dev config
curl http://localhost:8888/user-service/dev

# Get raw YAML
curl http://localhost:8888/user-service-dev.yml
```

## 🚀 Running the Service

### Prerequisites

- JDK 17+
- Maven 3.9+

### Build

```bash
mvn clean package
```

### Run

```bash
mvn spring-boot:run
```

### Docker

```bash
# Build image
docker build -t pharmaorder/config-server:latest .

# Run container
docker run -p 8888:8888 \
  -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/ \
  pharmaorder/config-server:latest
```

## 🧪 Testing

### Verify Configuration

```bash
# Test config server is running
curl http://localhost:8888/actuator/health

# Get user-service configuration
curl http://localhost:8888/user-service/default | jq

# Get product-service dev configuration
curl http://localhost:8888/product-service/dev | jq
```

## 🔍 Troubleshooting

### Common Issues

**Issue**: Services can't connect to config server
- Ensure config server is running before starting services
- Check bootstrap.yml has correct config server URI
- Verify network connectivity

**Issue**: Configuration not refreshing
- Ensure @RefreshScope annotation is present
- Check actuator refresh endpoint is enabled
- Verify Spring Cloud Bus is configured (for bus-refresh)

**Issue**: Encrypted values not decrypting
- Check encrypt.key is set correctly
- Verify cipher text format: `{cipher}...`
- Ensure encryption is enabled

## 🔐 Security Best Practices

1. **Use Git Backend**: Store configs in private Git repository
2. **Encrypt Secrets**: Never store plain text passwords
3. **Secure Endpoints**: Enable authentication for config server
4. **Use HTTPS**: Encrypt communication in production
5. **Audit Access**: Log all configuration access
6. **Rotate Keys**: Regularly rotate encryption keys

### Enable Security

```yaml
spring:
  security:
    user:
      name: config-admin
      password: ${CONFIG_SERVER_PASSWORD}
```

Client configuration:

```yaml
spring:
  cloud:
    config:
      uri: http://localhost:8888
      username: config-admin
      password: ${CONFIG_SERVER_PASSWORD}
```

## 📚 Configuration Properties

### Common Properties

| Property | Description | Example |
|----------|-------------|---------|
| `server.port` | Service port | 8081 |
| `spring.datasource.url` | Database URL | jdbc:postgresql://... |
| `eureka.client.service-url.defaultZone` | Eureka URL | http://localhost:8761/eureka/ |
| `spring.redis.host` | Redis host | localhost |
| `spring.rabbitmq.host` | RabbitMQ host | localhost |

### Profile-Specific Properties

Use profiles for environment-specific configs:
- `default` - Local development
- `dev` - Development environment
- `staging` - Staging environment
- `prod` - Production environment

## 📄 License

MIT License - Part of PharmaOrder Platform
