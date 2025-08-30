# 📊 Real-Time E-Commerce Analytics Engine

## 📌 Overview

The **Real-Time E-Commerce Analytics Engine** is a backend system that demonstrates how modern e-commerce platforms (like Amazon or Shopify) track and analyze events in **real-time**.

It is built with **Spring Boot (Java)** and **Apache Kafka**, with **PostgreSQL integration planned** for persistence.

The system is designed to:

1. **Ingest e-commerce events** — such as purchases, product views, and cart updates — through REST APIs.
2. **Stream events into Kafka** for scalable and decoupled processing.
3. **Consume events in real-time** to log or eventually process analytics.
4. **Store aggregated results** in PostgreSQL (planned for future phases).

This project follows an **event-driven architecture**, which enables **scalability, loose coupling, and near real-time analytics**, similar to what large-scale distributed systems use in production.

---

## 🏗️ Current Architecture

* **Ingestion Service** → REST API endpoints to accept e-commerce events and publish them into Kafka.
* **Processing Service** → Kafka consumer service that listens to events, validates them, and logs incoming events.
* **Analytics Service** → planned REST API service for analytics queries.
* **Kafka** → acts as the event backbone.
* **PostgreSQL** → planned for storing processed/aggregated results.

---

## 📂 Project Structure

```bash
real-time-analytics/
│── ingestion-service/     # Handles incoming events and publishes to Kafka
│── processing-service/    # Consumes Kafka events and logs them (processing logic planned)
│── analytics-service/     # Placeholder for future analytics REST APIs
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

Create a `.env` file in the project root:

```dotenv
# Kafka bootstrap servers
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Kafka producer settings
SPRING_KAFKA_PRODUCER_ACKS=all
SPRING_KAFKA_PRODUCER_RETRIES=3
SPRING_KAFKA_PRODUCER_LINGER_MS=5
SPRING_KAFKA_PRODUCER_DELIVERY_TIMEOUT_MS=30000
SPRING_KAFKA_PRODUCER_REQUEST_TIMEOUT_MS=20000

# Kafka consumer settings
SPRING_KAFKA_CONSUMER_GROUP_ID=processing-group
SPRING_KAFKA_CONSUMER_AUTO_OFFSET_RESET=earliest

# Server ports
INGESTION_SERVICE_PORT=8001
PROCESSING_SERVICE_PORT=8002
```

These variables are injected automatically by Docker Compose. `application.properties` contains fallback defaults to run services locally.

---

### Running Kafka via Docker Compose

```bash
docker-compose up -d
```

This will start:

* Kafka broker (internal port 9092, external mapped port 29092)
* No Zookeeper (Kafka Kraft mode)

Check logs via:

```bash
docker-compose logs -f kafka
```

> When running Spring Boot locally, use `localhost:29092` as the Kafka bootstrap server.

---

### Running Services

#### Option 1: Inside Docker

```bash
docker-compose up -d ingestion-service
docker-compose up -d processing-service
```

#### Option 2: Locally (IntelliJ / Maven)

1. Ensure Kafka is running (`docker-compose up kafka`).
2. Run Spring Boot services:

```bash
cd ingestion-service
mvn spring-boot:run
```

---

### Testing Ingestion Endpoints

Send a purchase event:

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

Processing service logs incoming events to verify consumption.

---

## 🎯 Current Features

* **Ingestion service** with PurchaseEvent endpoint
* **Processing service**: consumes, validates, and logs events from Kafka
* Dockerized services with environment variable configuration

---

## 🛠️ Future Enhancements

* Implement real **analytics processing** in processing service
* Persist processed metrics into **PostgreSQL**
* Add REST API endpoints in analytics-service for querying analytics
* Implement dead-letter queues for invalid events

---

## 📖 Learning Goals

* Practice with **Kafka event streaming**
* Build a **modular microservices architecture** with Spring Boot
* Learn **real-time e-commerce analytics pipelines**
* Create a **resume-ready project** demonstrating event-driven design

---