# Eureka Server - Service Registry

Netflix Eureka-based service discovery server for the PharmaOrder microservices architecture. Enables dynamic service registration and discovery.

## 📋 Overview

The Eureka Server acts as a service registry where all microservices register themselves and discover other services. This enables dynamic load balancing and failover without hardcoded service locations.

**Port**: 8761

## 🎯 Responsibilities

- **Service Registration**: Accept registration from microservices
- **Service Discovery**: Provide service locations to clients
- **Health Monitoring**: Track service health via heartbeats
- **Load Balancing**: Enable client-side load balancing
- **Failover Support**: Remove unhealthy instances automatically
- **Dashboard**: Web UI for monitoring registered services

## 🛠️ Technology Stack

- Spring Cloud Netflix Eureka Server
- Spring Boot 3.2.4
- Spring Boot Actuator

## 🔧 Configuration

### application.yml

```yaml
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    enable-self-preservation: true
    eviction-interval-timer-in-ms: 60000
    renewal-percent-threshold: 0.85
```

### Main Application Class

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

## 📊 Dashboard

Access the Eureka Dashboard at: **http://localhost:8761**

The dashboard shows:
- Registered instances
- Service status (UP/DOWN)
- Instance metadata
- Renewal statistics
- General server info

## 🔄 Self-Preservation Mode

Eureka enters self-preservation mode when:
- More than 15% of services fail to send heartbeats
- Network partition is suspected
- Prevents mass eviction of services during network issues

When in self-preservation mode:
- Eureka stops evicting instances
- Warning message appears in dashboard
- Services remain registered even without heartbeats

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

Or run the JAR:

```bash
java -jar target/eureka-server-1.0.0-SNAPSHOT.jar
```

### Docker

```bash
# Build image
docker build -t pharmaorder/eureka-server:latest .

# Run container
docker run -p 8761:8761 pharmaorder/eureka-server:latest
```

## 🔌 Client Configuration

Microservices connect to Eureka with this configuration:

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
```

## 📊 Monitoring

### Actuator Endpoints

- `GET /actuator/health` - Server health status
- `GET /actuator/info` - Server information
- `GET /actuator/metrics` - Server metrics

### Key Metrics

- `eureka.server.registry.count` - Number of registered instances
- `eureka.server.renewals` - Renewal rate
- `eureka.server.evictions` - Eviction count

## 🔐 Security (Production)

For production, enable security:

```yaml
spring:
  security:
    user:
      name: admin
      password: ${EUREKA_PASSWORD}

eureka:
  client:
    service-url:
      defaultZone: http://admin:${EUREKA_PASSWORD}@eureka-server:8761/eureka/
```

## 🌐 High Availability Setup

For production, run multiple Eureka servers:

### Eureka Server 1

```yaml
eureka:
  instance:
    hostname: eureka-server-1
  client:
    service-url:
      defaultZone: http://eureka-server-2:8761/eureka/,http://eureka-server-3:8761/eureka/
```

### Eureka Server 2

```yaml
eureka:
  instance:
    hostname: eureka-server-2
  client:
    service-url:
      defaultZone: http://eureka-server-1:8761/eureka/,http://eureka-server-3:8761/eureka/
```

## 🧪 Testing

### Verify Registration

```bash
# Check registered services
curl http://localhost:8761/eureka/apps

# Check specific service
curl http://localhost:8761/eureka/apps/USER-SERVICE
```

### Health Check

```bash
curl http://localhost:8761/actuator/health
```

## 🔍 Troubleshooting

### Common Issues

**Issue**: Services not registering
- Check network connectivity
- Verify eureka.client.service-url.defaultZone is correct
- Check firewall rules

**Issue**: Self-preservation mode activated
- Check if services are sending heartbeats
- Review network stability
- Adjust renewal-percent-threshold if needed

**Issue**: Stale instances not removed
- Check eviction-interval-timer-in-ms
- Verify lease-expiration-duration-in-seconds on clients

## 📚 Registered Services

Expected services in PharmaOrder:

1. API-GATEWAY (8080)
2. USER-SERVICE (8081)
3. PRODUCT-SERVICE (8082)
4. INVENTORY-SERVICE (8083)
5. PRESCRIPTION-SERVICE (8084)
6. ORDER-SERVICE (8085)
7. NOTIFICATION-SERVICE (8086)
8. FILE-SERVICE (8087)
9. LOYALTY-SERVICE (8088)

## 🔄 Service Discovery Flow

```
1. Service starts → Registers with Eureka
2. Eureka stores service metadata
3. Service sends heartbeat every 30s
4. Client queries Eureka for service location
5. Eureka returns available instances
6. Client caches registry locally
7. Client refreshes cache every 30s
```

## 📄 License

MIT License - Part of PharmaOrder Platform
