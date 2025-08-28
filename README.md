# 📊 Real-Time E-Commerce Analytics Engine

## 📌 Overview

The **Real-Time E-Commerce Analytics Engine** is a backend system that demonstrates how modern e-commerce platforms (like Amazon or Shopify) track and analyze events in **real-time**.

It is built with **Spring Boot (Java)**, **Apache Kafka**, and later will integrate **PostgreSQL** for persistence.

The system is designed to:

1. **Ingest e-commerce events** — such as purchases, product views, and cart updates — through REST APIs.
2. **Stream events into Kafka** for scalable and decoupled processing.
3. **Consume and process events in real-time** to extract insights (e.g., sales trends, active users, popular products).
4. **Store aggregated results** in PostgreSQL for querying and reporting.

This project follows an **event-driven architecture**, which enables **scalability, loose coupling, and near real-time analytics**, similar to what large-scale distributed systems use in production.

---

## 🏗️ Current Architecture

* **Ingestion Service** → REST API endpoints to accept e-commerce events and publish them into Kafka.
* **Processing Service** → Kafka consumer service that listens to events (skeleton ready, processing logic to be added).
* **Analytics Service** → will provide REST APIs for analytics queries (planned).
* **Kafka** → acts as the event backbone.
* **PostgreSQL** → will be used later for storing processed/aggregated results.

---

## 📂 Project Structure

```bash
real-time-analytics/
│── ingestion-service/     # Handles incoming events (e.g., PurchaseEvent) and publishes to Kafka
│── processing-service/    # Consumes Kafka events (processing logic coming soon)
│── analytics-service/     # Placeholder for exposing analytics data via APIs
│── api-requests/          # HTTP request samples for testing ingestion
│── pom.xml                # Parent Maven configuration
│── README.md
```

---

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3
* **Event Streaming:** Apache Kafka (KRaft mode, no Zookeeper)
* **Database (Planned):** PostgreSQL
* **Build Tool:** Maven
* **Containerization:** Docker & Docker Compose
* **Testing:** JUnit 5, Spring Boot Test

---

## 🚀 Getting Started

### Prerequisites

* Java 17
* Maven
* Docker & Docker Compose (for Kafka and local multi-service setup)
* Optional: IntelliJ for running services locally

---

### Environment Variables

Create a `.env` file in the project root with the following variables:

```dotenv
# Kafka bootstrap servers
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Kafka producer settings
SPRING_KAFKA_PRODUCER_ACKS=all
SPRING_KAFKA_PRODUCER_RETRIES=3
SPRING_KAFKA_PRODUCER_LINGER_MS=5
SPRING_KAFKA_PRODUCER_DELIVERY_TIMEOUT_MS=30000
SPRING_KAFKA_PRODUCER_REQUEST_TIMEOUT_MS=20000
```

These variables are automatically injected into the Spring Boot services by Docker Compose.
`application.properties` contains fallback defaults so the services can run outside Docker as well.

---

### Running Kafka via Docker Compose

```bash
docker-compose up -d
```

This will start:

* Kafka broker (internal port 9092, external mapped port 29092)
* Zookeeper is not required (Kafka Kraft mode)
* You can check logs via `docker-compose logs -f kafka`

> Note: When running Spring Boot locally, use `localhost:29092` as the Kafka bootstrap server.

---

### Running Services

#### Option 1: Inside Docker

```bash
docker-compose up -d ingestion-service
docker-compose up -d processing-service
# analytics-service will be added later
```

Services will automatically use the `.env` variables.

#### Option 2: Running Locally (e.g., IntelliJ or Maven)

1. Make sure Kafka is running in Docker (`docker-compose up kafka`).
2. Run the Spring Boot service in IntelliJ or via Maven:

```bash
cd ingestion-service
mvn spring-boot:run
```

3. The app will use the fallback defaults in `application.properties` or env vars from `.env`.

---

### Testing Ingestion Endpoints

Send a purchase event to ingestion service:

```bash
curl -X POST http://localhost:8001/api/v1/purchase \
-H "Content-Type: application/json" \
-d '{
  "orderId": "423",
  "productId": "PROD-456",
  "customerId": "123",
  "amount": 99.99,
  "timestamp": "2025-08-27T10:00:00Z"
}'
```

---

## 🎯 Roadmap

### Phase 1: Event Ingestion ✅

* Setup ingestion service with **PurchaseEvent endpoint**
* Configure Kafka in Docker with external and internal listeners

### Phase 2: Event Processing 🔄

* Implement Kafka consumer logic in processing-service
* Process additional events: product views, add-to-cart

### Phase 3: Analytics APIs ⏳

* Store processed results in PostgreSQL
* Add REST APIs in analytics-service for querying analytics

---

## 📖 Learning Goals

* Practice with **Kafka event streaming**
* Build a **modular microservices architecture** with Spring Boot
* Learn how **real-time e-commerce analytics pipelines** are designed
* Create a **resume-ready project** to showcase event-driven design

---