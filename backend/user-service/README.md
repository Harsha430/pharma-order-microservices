# User Service - Authentication & User Management

Handles user registration, authentication, JWT token management, and user profile operations for the PharmaOrder platform.

## 📋 Overview

The User Service is responsible for all user-related operations including authentication, authorization, profile management, and address management. It uses JWT tokens for stateless authentication and BCrypt for password hashing.

**Port**: 8081

## 🎯 Responsibilities

- **User Registration**: Register customers, pharmacists, and admins
- **Authentication**: Login with email/password, JWT generation
- **Token Management**: Access token, refresh token, token blacklisting
- **Profile Management**: CRUD operations for user profiles
- **Address Management**: Multiple delivery addresses per user
- **Password Management**: Change password, forgot/reset password flow
- **Role Management**: Assign and manage user roles (ADMIN only)
- **User Administration**: View, update, deactivate users (ADMIN only)

## 🛠️ Technology Stack

- Spring Boot 3.2.4
- Spring Security 6.x
- Spring Data JPA
- PostgreSQL
- JJWT 0.12.3 (JWT)
- BCrypt (Password hashing)
- Redis (Token blacklist)
- Spring Boot Validation

## 🗄️ Database Schema

### Tables

**users**
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**roles**
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

INSERT INTO roles (name) VALUES 
    ('ROLE_CUSTOMER'),
    ('ROLE_PHARMACIST'),
    ('ROLE_ADMIN');
```

**user_roles**
```sql
CREATE TABLE user_roles (
    user_id UUID REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

**addresses**
```sql
CREATE TABLE addresses (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    label VARCHAR(50),
    line1 VARCHAR(255) NOT NULL,
    line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**refresh_tokens**
```sql
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    token_hash VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**password_reset_tokens**
```sql
CREATE TABLE password_reset_tokens (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    token VARCHAR(255) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🔐 Security

### Password Hashing

```java
@Service
public class PasswordService {
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    
    public String hashPassword(String plainPassword) {
        return encoder.encode(plainPassword);
    }
    
    public boolean matches(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }
}
```

### JWT Token Generation

```java
@Service
public class JwtService {
    
    @Value("${jwt.secret-key}")
    private String secretKey;
    
    @Value("${jwt.expiration}")
    private long accessTokenExpiration; // 15 minutes
    
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration; // 7 days
    
    public String generateAccessToken(User user) {
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("roles", user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()))
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .setId(UUID.randomUUID().toString())
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
}
```

## 📡 REST API Endpoints

### Authentication

#### Register User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "dateOfBirth": "1990-01-15"
}

Response: 201 Created
{
  "id": "uuid",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["ROLE_CUSTOMER"]
}
```

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123!"
}

Response: 200 OK
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "roles": ["ROLE_CUSTOMER"]
  }
}
```

#### Refresh Token
```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}

Response: 200 OK
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "new-refresh-token-uuid",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

#### Logout
```http
POST /api/v1/auth/logout
Authorization: Bearer {accessToken}

Response: 204 No Content
```

### User Profile

#### Get Current User
```http
GET /api/v1/users/me
Authorization: Bearer {accessToken}

Response: 200 OK
{
  "id": "uuid",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "dateOfBirth": "1990-01-15",
  "status": "ACTIVE",
  "roles": ["ROLE_CUSTOMER"]
}
```

#### Update Profile
```http
PUT /api/v1/users/me
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "phone": "+1234567890"
}

Response: 200 OK
```

### Address Management

#### Add Address
```http
POST /api/v1/users/me/addresses
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "label": "Home",
  "line1": "123 Main St",
  "line2": "Apt 4B",
  "city": "New York",
  "state": "NY",
  "pincode": "10001",
  "isDefault": true
}

Response: 201 Created
```

#### Get Addresses
```http
GET /api/v1/users/me/addresses
Authorization: Bearer {accessToken}

Response: 200 OK
[
  {
    "id": "uuid",
    "label": "Home",
    "line1": "123 Main St",
    "line2": "Apt 4B",
    "city": "New York",
    "state": "NY",
    "pincode": "10001",
    "isDefault": true
  }
]
```

### Password Management

#### Change Password
```http
POST /api/v1/auth/change-password
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "currentPassword": "OldPass123!",
  "newPassword": "NewPass456!"
}

Response: 200 OK
```

#### Forgot Password
```http
POST /api/v1/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}

Response: 200 OK
{
  "message": "Password reset email sent"
}
```

#### Reset Password
```http
POST /api/v1/auth/reset-password
Content-Type: application/json

{
  "token": "reset-token-from-email",
  "newPassword": "NewPass456!"
}

Response: 200 OK
```

### Admin Endpoints

#### Get All Users
```http
GET /api/v1/users?page=0&size=20
Authorization: Bearer {adminToken}

Response: 200 OK
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 5,
  "number": 0,
  "size": 20
}
```

#### Update User Role
```http
PUT /api/v1/users/{userId}/role
Authorization: Bearer {adminToken}
Content-Type: application/json

{
  "role": "ROLE_PHARMACIST"
}

Response: 200 OK
```

## 🔄 Token Blacklisting

When a user logs out, the JWT's JTI (JWT ID) is added to Redis blacklist:

```java
@Service
public class TokenBlacklistService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public void blacklistToken(String jti, long expirationTime) {
        String key = "blacklist:" + jti;
        long ttl = expirationTime - System.currentTimeMillis();
        redisTemplate.opsForValue().set(key, "true", ttl, TimeUnit.MILLISECONDS);
    }
    
    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey("blacklist:" + jti)
        );
    }
}
```

## 🚀 Running the Service

### Prerequisites

- JDK 17+
- Maven 3.9+
- PostgreSQL 15+
- Redis 7+
- Config Server running
- Eureka Server running

### Configuration

Create `application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/user_db
    username: pharmauser
    password: yourpassword
  redis:
    host: localhost
    port: 6379

jwt:
  secret-key: your-256-bit-secret-key-here
  expiration: 900000
  refresh-expiration: 604800000
```

### Build & Run

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Docker

```bash
docker build -t pharmaorder/user-service:latest .
docker run -p 8081:8081 pharmaorder/user-service:latest
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

### Manual Testing

```bash
# Register user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!","firstName":"Test","lastName":"User"}'

# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!"}'
```

## 📊 Monitoring

### Actuator Endpoints

- `GET /actuator/health` - Service health
- `GET /actuator/metrics` - Service metrics
- `GET /actuator/info` - Service info

### Key Metrics

- `user.registrations.total` - Total registrations
- `user.logins.total` - Total logins
- `user.logins.failed` - Failed login attempts
- `jwt.tokens.generated` - Tokens generated
- `jwt.tokens.refreshed` - Tokens refreshed

## 🔍 Troubleshooting

**Issue**: JWT validation failing
- Check secret key matches across services
- Verify token hasn't expired
- Check token isn't blacklisted

**Issue**: Password reset not working
- Verify email service is configured
- Check token hasn't expired (30 minutes)
- Ensure token hasn't been used

**Issue**: User can't login
- Check password is correct
- Verify user status is ACTIVE
- Check account isn't locked

## 📄 License

MIT License - Part of PharmaOrder Platform
