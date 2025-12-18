# 📊 Real-Time E-Commerce Analytics Engine

## 📌 Overview

The **Real-Time E-Commerce Analytics Engine** is a backend system that demonstrates **real-time event ingestion and analytics** for e-commerce platforms. It shows how events like orders or purchases can be processed and analyzed in **real-time** using **Apache Kafka**, **Kafka Streams** and **Apache Pinot**.

The system is designed to:

1. **Ingest e-commerce events** — e.g., orders — via REST APIs.
2. **Stream events into Kafka** for scalable, decoupled processing.
3. **Process events in real time using Kafka Streams** (validation, transformation, enrichment).
4. **Query aggregated analytics** from **Pinot** in real-time.

This project follows an **event-driven architecture**, enabling **scalability, loose coupling, and real-time insights**, similar to enterprise-level e-commerce platforms.

---

## 🏗️ Current Architecture

* **Ingestion Service** → REST endpoints to accept events and publish them to Kafka.
* **Processing Service (Kafka Stream)** 
  → Uses Kafka Streams to process events in real time:
  * Validates incoming order events 
  * Transforms orders into flat, analytics-friendly records 
  * Publishes derived events back to Kafka
* **Analytics Service** → Queries real-time analytics from **Pinot**.
* **Apache Kafka** → Event backbone for decoupled streaming.
* **Apache Pinot** → Real-time OLAP store for analytics queries.
* **PostgreSQL** → Planned for storing historical/aggregated results.

---

## 📂 Project Structure

```bash
real-time-analytics/
│── ingestion-service/      # Accepts events, publishes to Kafka
│── processing-service/      # Kafka Streams topology (real-time processing)
│── analytics-service/      # Queries analytics from Pinot
│── pinot/                  # Pinot table configs and schemas
│── api-requests/           # Sample API requests for testing
│── docker-compose.yml
│── pom.xml                 # Parent Maven configuration
│── README.md
```

---

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot 3
* **Event Streaming:** Apache Kafka
* **Real-time Analytics:** Apache Pinot
* **Database (Planned):** PostgreSQL
* **Build Tool:** Maven
* **Containerization:** Docker & Docker Compose
* **Testing:** JUnit 5, Spring Boot Test

---

## 🚀 Getting Started

### Prerequisites

* Java 17
* Maven
* Docker & Docker Compose
* Optional: IntelliJ for running services locally

---

### Environment Variables

Create a `.env` file:

```dotenv
# -----------------------
# Kafka Configuration
# -----------------------
KAFKA_ADVERTISED_LISTENER_INTERNAL=kafka:9092
KAFKA_ADVERTISED_LISTENER_EXTERNAL=<host:port>  # e.g., localhost:29092
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Kafka producer & consumer settings
SPRING_KAFKA_PRODUCER_ACKS=all
SPRING_KAFKA_PRODUCER_RETRIES=3
SPRING_KAFKA_CONSUMER_PROCESSING_GROUP_ID=processing-group
SPRING_KAFKA_CONSUMER_ANALYTICS_GROUP_ID=analytics-group
SPRING_KAFKA_CONSUMER_AUTO_OFFSET_RESET=earliest

# -----------------------
# PostgreSQL Configuration
# -----------------------
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/<database>  # e.g., postgres:5432/analytics_db
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=true
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true

# -----------------------
# Service Ports
# -----------------------
INGESTION_SERVER_PORT=8001
PROCESSING_SERVER_PORT=8002
ANALYTICS_SERVER_PORT=8003

# -----------------------
# Pinot Configuration
# -----------------------
ANALYTICS_PINOT_ORDER_PLACED_RT_TABLE=<pinot_table_name>  # e.g., order_placed_rt
ANALYTICS_PINOT_BROKER_HOSTS=<pinot_host:port>           # e.g., pinot:8000

```

These variables are injected automatically by Docker Compose. `application.properties` provides fallback defaults for local runs.

---

### Running Services via Docker Compose

```bash
docker-compose up -d
```

This will start:

* **Kafka** broker
* **Pinot** server (QuickStart streaming mode)
* **Ingestion, Processing, and Analytics services**

Check logs:

```bash
docker-compose logs -f ingestion-service
docker-compose logs -f processing-service
docker-compose logs -f analytics-service
```

---

### Running Services Locally

1. Start Kafka and Pinot via Docker Compose:

```bash
docker-compose up -d kafka pinot
```

2. Run services from IntelliJ or Maven:

```bash
cd ingestion-service
mvn spring-boot:run

cd processing-service
mvn spring-boot:run

cd analytics-service
mvn spring-boot:run
```

> Use `localhost:29092` as Kafka bootstrap server for local runs.
> Use `localhost:8000` as Pinot broker for local runs.

---

### Testing Event Ingestion

POST an order-placed event:

```bash
curl -X POST http://localhost:8001/order-placed \
-H "Content-Type: application/json" \
-d "{
  \"orderId\": \"1236\",
  \"customerId\": \"347\",
  \"totalAmount\": 70.99,
  \"timestamp\": \"$(date -u +%Y-%m-%dT%H:%M:%SZ)\",
  \"products\": [
    {
      \"productId\": \"PROD-457\",
      \"name\": \"Sample Product 2\",
      \"price\": 70.99,
      \"quantity\": 1
    }
  ]
}"
```

Processing service logs incoming events to verify consumption.

---

### Testing Analytics Queries

GET total revenue for the last hour:

```bash
curl http://localhost:8003/analytics/revenue?interval=hour
```

GET total order count:

```bash
curl http://localhost:8003/analytics/orders?interval=hour
```

---

## 🎯 Current Features

* **Event ingestion service** — receives order events and pushes to Kafka
* **Kafka Streams–based processing service**
  → Real-time validation and transformation of events using a declarative stream topology.
* **Analytics service** — queries **Pinot** for total revenue and order count
* **Dockerized services** with environment variable support

---

## 🛠️ Next Steps

* Persist enriched analytics and aggregated metrics to **PostgreSQL** for long-term and historical reporting
* Introduce core domain tables (e.g., **products**, **categories**) as systems of record to support richer analytics
* Stream database changes to Kafka using **CDC (Debezium + Kafka Connect)** instead of manual event publishing
* Expand `analytics-service` with advanced queries (e.g., top-selling products, revenue by category/customer, time-windowed KPIs)
* Add real-time aggregations feeding analytical stores (e.g., **Apache Pinot** or **ClickHouse**)
* Implement **dead-letter queues (DLQ)** for invalid, malformed, or non-processable events
* Add unit and integration tests covering the full ingestion → processing → persistence pipeline

This makes the roadmap explicit, realistic, and clearly senior-level.


---

## 📖 Learning Goals

* Kafka event streaming and real-time processing
* Apache Pinot real-time analytics queries
* Microservices architecture with Spring Boot
* Event-driven design for scalable e-commerce systems
* Docker Compose orchestration for local development