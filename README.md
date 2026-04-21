<div align="center">
  <img src="https://images.unsplash.com/photo-1587854685352-25a8111e6e56?q=80&w=200&auto=format&fit=crop" width="100" height="100" />
  <h1>PharmaOrder</h1>
  <p><strong>A Modern, Microservices-Powered Pharmacy Ecosystem</strong></p>

  [![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.2.4-brightgreen?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
  [![React](https://img.shields.io/badge/React-18-blue?style=for-the-badge&logo=react)](https://react.dev/)
  [![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge&logo=docker)](https://www.docker.com/)
  [![License](https://img.shields.io/badge/License-MIT-orange?style=for-the-badge)](./LICENSE)
</div>

---

## 🌿 The Vision
PharmaOrder is a premium, enterprise-grade e-commerce platform designed to bridge the gap between patients and pharmacies. Built with a robust **12-service microservices architecture**, it offers seamless order management, real-time inventory tracking, and an intelligent health-pack engine.

## ✨ Key Features

### 🧠 Intelligent Health Pack Engine
Automatic detection of medicine bundles. When a patient adds individual recovery components (e.g., ORS, Dolo, Zincovit), the system instantly recognizes the **Health Pack** and applies transparent, bundled pricing.

### 🍱 Radical Transparency
Every health pack and medicine bundle includes a detailed "Includes" breakdown. Patients know exactly what they are buying, from the individual tablet counts to the verified prescription status.

### 📧 Real-World Notification System
Integrated with live SMTP (Gmail) to deliver professional order confirmations, prescription verification alerts, and loyalty updates directly to the user's inbox in their local currency (**₹**).

### 🏆 Loyalty & Loyalty Program
A integrated points system that rewards consistent wellness. Patients earn points on every order, redeemable for future healthcare needs.

## 🏗️ Architecture Map

```mermaid
graph TD
    User((Patient)) --> |HTTPS| Gateway[API Gateway :8080]
    
    subgraph "Infrastructure"
        Gateway --> Eureka[Eureka Server :8761]
        Gateway --> Config[Config Server :8888]
    end

    subgraph "Core Services"
        Gateway --> US[User Service :8081]
        Gateway --> PS[Product Service :8082]
        Gateway --> IS[Inventory Service :8083]
        Gateway --> OS[Order Service :8085]
        Gateway --> NS[Notification Service :8086]
    end

    subgraph "Data Layer"
        US --> mysql1[(Auth DB)]
        PS --> mysql2[(Catalog DB)]
        OS --> Rabbit[(RabbitMQ)]
        Rabbit --> NS
    end
```

## 🚀 Quick Start (Docker Preferred)

1.  **Clone the Repo**
    ```bash
    git clone https://github.com/Harsha430/pharma-order-microservices.git
    cd pharma-order-microservices
    ```

2.  **Launch Backend**
    ```bash
    cd backend
    mvn clean install -DskipTests
    docker-compose up -d
    ```

3.  **Launch Frontend**
    ```bash
    cd frontend
    npm install
    npm run dev
    ```

## 🛠️ Tech Stack
- **Backend**: Java 17, Spring Boot, Spring Cloud (Eureka, Config, Gateway), Feign, Hibernate.
- **Frontend**: React 18, TypeScript, TailwindCSS (Earthy Aesthetic), TanStack Query.
- **Infrastructure**: MySQL, Redis, RabbitMQ, Docker, Zipkin.

---
Built with ❤️ for the Modern Healthcare Experience.
