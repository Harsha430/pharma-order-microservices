# AI Setup & Execution Guide - PharmaOrder

This guide is optimized for AI agents to deploy and verify the PharmaOrder microservices platform in a local environment.

## Instructions

### 1. Environment Verification
Ensure **Docker Desktop** is running and the engine is healthy.
- `docker info`

### 2. Backend Bootstrapping
The backend utilizes a centralized configuration server. Services must be built before first launch to pack configurations into JARs.

```bash
# Navigate to backend root
cd backend

# Build all microservices
mvn clean install -DskipTests

# Launch the full stack
docker-compose up -d
```

### 3. Service Readiness Check
Wait for approximately 2-3 minutes for total propagation. Verify registration via Eureka:
- **Dashboard**: `http://localhost:8761`
- **Expected Services**: UP status for `API-GATEWAY`, `USER-SERVICE`, `PRODUCT-SERVICE`, `ORDER-SERVICE`, `NOTIFICATION-SERVICE`, `INVENTORY-SERVICE`, `PRESCRIPTION-SERVICE`, `LOYALTY-SERVICE`, `FILE-SERVICE`.

### 4. Frontend Launch
```bash
cd frontend
npm install
npm run dev
```
- **Access**: `http://localhost:5173`

## 🔌 API Network Map
Key entry points for the AI to monitor:
- **API Gateway**: `http://localhost:8080/api/v1/...`
- **Config Server**: `http://localhost:8888`
- **Eureka Server**: `http://localhost:8761`

## 🛡️ Authentication & Testing
For testing flows, use the pre-configured Gmail SMTP for notifications:
- **Email**: `abburimanohar1234@gmail.com`
- **SMTP Host**: `smtp.gmail.com:587`
