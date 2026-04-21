# API Gateway Service

Single entry point for all client requests in the PharmaOrder microservices architecture. Handles routing, authentication, rate limiting, and cross-cutting concerns.

## 📋 Overview

The API Gateway acts as a reverse proxy that routes requests to appropriate microservices, validates JWT tokens, enforces rate limits, and provides circuit breaker patterns for resilience.

**Port**: 8080

## 🎯 Responsibilities

- **Request Routing**: Route traffic to downstream microservices based on path patterns
- **JWT Validation**: Validate access tokens using public key (no auth service call needed)
- **Rate Limiting**: Prevent abuse using Redis-backed token bucket algorithm
- **CORS Handling**: Configure cross-origin resource sharing
- **Circuit Breaker**: Protect against cascading failures
- **Load Balancing**: Distribute requests via Eureka service discovery
- **Request/Response Logging**: Track all API traffic
- **Security Headers**: Add security-related HTTP headers

## 🛠️ Technology Stack

- Spring Cloud Gateway
- Spring Cloud Netflix Eureka Client
- Spring Data Redis
- Bucket4j (Rate Limiting)
- Resilience4j (Circuit Breaker, Retry)
- JJWT (JWT Validation)

## 🔧 Configuration

### application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/api/v1/auth/**, /api/v1/users/**
          filters:
            - name: CircuitBreaker
              args:
                name: userServiceCircuitBreaker
                fallbackUri: forward:/fallback/user-service
        
        - id: product-service
          uri: lb://PRODUCT-SERVICE
          predicates:
            - Path=/api/v1/products/**, /api/v1/categories/**
          filters:
            - name: CircuitBreaker
              args:
                name: productServiceCircuitBreaker
        
        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/api/v1/orders/**
          filters:
            - name: CircuitBreaker
              args:
                name: orderServiceCircuitBreaker
        
        - id: prescription-service
          uri: lb://PRESCRIPTION-SERVICE
          predicates:
            - Path=/api/v1/prescriptions/**
        
        - id: inventory-service
          uri: lb://INVENTORY-SERVICE
          predicates:
            - Path=/api/v1/inventory/**
        
        - id: loyalty-service
          uri: lb://LOYALTY-SERVICE
          predicates:
            - Path=/api/v1/loyalty/**, /api/v1/offers/**
        
        - id: file-service
          uri: lb://FILE-SERVICE
          predicates:
            - Path=/api/v1/files/**

  redis:
    host: localhost
    port: 6379
    timeout: 2000ms

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    fetch-registry: true
    register-with-eureka: true

jwt:
  public-key: classpath:public-key.pem

resilience4j:
  circuitbreaker:
    instances:
      default:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-number-of-calls-in-half-open-state: 5
```

## 🚦 Rate Limiting

### Configuration

Rate limits are enforced per user (authenticated) or per IP (anonymous):

| User Type | Capacity | Refill Rate |
|-----------|----------|-------------|
| Anonymous | 30 requests | 30/minute |
| Authenticated | 100 requests | 100/minute |
| Admin | 500 requests | 500/minute |
| Login Endpoint | 10 requests | 10/minute (per IP) |

### Implementation

```java
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String key = getRateLimitKey(exchange);
        Bucket bucket = getBucket(key);
        
        if (bucket.tryConsume(1)) {
            return chain.filter(exchange);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("Retry-After", "60");
            return exchange.getResponse().setComplete();
        }
    }
    
    @Override
    public int getOrder() {
        return -1; // High priority
    }
}
```

## 🔐 JWT Validation

The gateway validates JWT tokens using RS256 public key:

```java
@Component
public class JwtAuthenticationFilter implements GlobalFilter {
    
    @Value("${jwt.public-key}")
    private Resource publicKeyResource;
    
    private PublicKey publicKey;
    
    @PostConstruct
    public void init() throws Exception {
        publicKey = loadPublicKey(publicKeyResource);
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = extractToken(exchange);
        
        if (token != null && validateToken(token)) {
            Claims claims = parseToken(token);
            exchange.getRequest().mutate()
                .header("X-User-Id", claims.getSubject())
                .header("X-User-Email", claims.get("email", String.class))
                .header("X-User-Roles", String.join(",", claims.get("roles", List.class)))
                .build();
        }
        
        return chain.filter(exchange);
    }
}
```

## 🌐 CORS Configuration

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://pharmaorder.com"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        config.setMaxAge(3600L);
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return new CorsWebFilter(source);
    }
}
```

## 🔄 Circuit Breaker

Resilience4j circuit breaker protects against cascading failures:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      userServiceCircuitBreaker:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-number-of-calls-in-half-open-state: 5
        automatic-transition-from-open-to-half-open-enabled: true
```

### Fallback Controller

```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/user-service")
    public ResponseEntity<ErrorResponse> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse("User service is temporarily unavailable"));
    }
}
```

## 📊 Monitoring

### Actuator Endpoints

- `GET /actuator/health` - Gateway health status
- `GET /actuator/gateway/routes` - List all configured routes
- `GET /actuator/metrics` - Gateway metrics
- `GET /actuator/circuitbreakers` - Circuit breaker status

### Metrics

Key metrics exposed:
- `gateway.requests.total` - Total requests processed
- `gateway.requests.duration` - Request duration histogram
- `gateway.rate.limit.exceeded` - Rate limit violations
- `gateway.circuit.breaker.state` - Circuit breaker states

## 🚀 Running the Service

### Prerequisites

- JDK 17+
- Maven 3.9+
- Redis running on localhost:6379
- Eureka Server running on localhost:8761

### Build

```bash
mvn clean package
```

### Run

```bash
mvn spring-boot:run
```

Or with custom profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Docker

```bash
# Build image
docker build -t pharmaorder/api-gateway:latest .

# Run container
docker run -p 8080:8080 \
  -e EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/ \
  -e SPRING_REDIS_HOST=redis \
  pharmaorder/api-gateway:latest
```

## 🧪 Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify -P integration-tests
```

### Test Rate Limiting

```bash
# Send 35 requests (exceeds anonymous limit of 30)
for i in {1..35}; do
  curl -w "\n%{http_code}\n" http://localhost:8080/api/v1/products
done

# Should see 429 responses after 30 requests
```

## 🔍 Troubleshooting

### Common Issues

**Issue**: Routes not working
- Check Eureka dashboard to ensure services are registered
- Verify route predicates in application.yml

**Issue**: Rate limiting not working
- Ensure Redis is running and accessible
- Check Redis connection in logs

**Issue**: JWT validation failing
- Verify public key file exists and is readable
- Check token format and expiration

**Issue**: Circuit breaker always open
- Check downstream service health
- Review failure rate threshold configuration

## 📚 API Routes

| Path Pattern | Target Service | Methods |
|--------------|----------------|---------|
| `/api/v1/auth/**` | User Service | POST |
| `/api/v1/users/**` | User Service | GET, PUT, DELETE |
| `/api/v1/products/**` | Product Service | GET, POST, PUT, DELETE |
| `/api/v1/categories/**` | Product Service | GET, POST, PUT, DELETE |
| `/api/v1/inventory/**` | Inventory Service | GET, POST |
| `/api/v1/prescriptions/**` | Prescription Service | GET, POST, PUT |
| `/api/v1/orders/**` | Order Service | GET, POST, PUT, DELETE |
| `/api/v1/loyalty/**` | Loyalty Service | GET, POST |
| `/api/v1/files/**` | File Service | GET, POST, DELETE |

## 📄 License

MIT License - Part of PharmaOrder Platform
