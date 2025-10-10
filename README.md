# 🧠 TaskFodge – Smart Task Management API

TaskFodge is a production-grade **Spring Boot microservice-ready backend** designed for scalability, observability, and developer experience.  
It combines **PostgreSQL, Redis, Kafka, and Spring Security (JWT)** into one powerful platform for managing tasks with caching, rate-limiting, and async notifications.

---

## ⚙️ Tech Stack
- **Java 21 / Spring Boot 3.4**
- **PostgreSQL** (DB)
- **Redis** (Cache / Rate limiting)
- **Kafka / Spring Events** (Async notifications)
- **Spring Security + JWT** (Auth)
- **Docker + Docker Compose**
- **Prometheus + Actuator** (Metrics)
- **Swagger / OpenAPI** (Docs)

---

## 🚀 Quick Setup

### 1️⃣ Clone & Build
```bash
git clone https://github.com/anweshandha/TaskFodge.git
cd TaskFodge
mvn clean package -DskipTests
