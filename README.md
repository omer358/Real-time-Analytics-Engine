# 📊 Real-Time E-Commerce Analytics Engine

## 📌 Overview

This project is a **real-time analytics backend** for e-commerce platforms, built with **Spring Boot (Java)**, **Kafka**, and **PostgreSQL**. It ingests order and user activity events, processes them in real-time, and provides aggregated analytics via REST APIs.

The system is designed to mimic **production-grade event-driven architectures** used in modern e-commerce companies like Amazon, Shopify, and Flipkart.

---

## 🏗️ Architecture

* **Event Producers** → Simulate e-commerce events (orders, user activity, cart updates).
* **Kafka** → Streams events for real-time processing.
* **Analytics Processor Service** → Consumes Kafka topics, processes events, and stores aggregated results.
* **Analytics Query Service** → Exposes REST APIs for fetching analytics data.
* **PostgreSQL** → Stores aggregated analytics.
* **Prometheus + Grafana (optional)** → For metrics and visualization.

---

## 📂 Project Structure

```bash
real-time-analytics/
│── analytics-common/         # Shared models, DTOs, and utils
│── event-producer/           # Service that simulates e-commerce events
│── analytics-processor/      # Kafka consumer that processes events
│── analytics-query/          # REST APIs for querying aggregated data
│── docker-compose.yml        # Infra (Kafka, PostgreSQL, ZooKeeper)
│── README.md
```

---

## 🔑 Key Features (Planned)

✅ Simulate real-time **order and user activity events**<br>
✅ Stream events through **Kafka**<br>
✅ Process events with **Spring Boot + Kafka Consumer**<br>
✅ Store aggregated results in **PostgreSQL**<br>
✅ Expose **REST APIs** for analytics (sales trends, top products, user activity)<br>
✅ Scalable microservices architecture<br>

---

## 🚀 Getting Started

### Prerequisites

* Java 21
* Docker & Docker Compose
* Maven

### Run Infrastructure

```bash
docker-compose up -d
```

### Run Services

```bash
cd event-producer && mvn spring-boot:run
cd analytics-processor && mvn spring-boot:run
cd analytics-query && mvn spring-boot:run
```

---

## 📡 Example APIs (To Be Implemented)

* `GET /analytics/sales/daily` → Daily sales trend
* `GET /analytics/sales/top-products` → Top-selling products
* `GET /analytics/users/active` → Active users in real-time

---

## 🎯 Roadmap

* [ ] Implement event producer with simulated data
* [ ] Process order events in analytics-processor
* [ ] Store results in PostgreSQL
* [ ] Expose analytics APIs in analytics-query
* [ ] Add Prometheus/Grafana dashboards

---

## 📖 Learning Goals

* Hands-on experience with **Kafka event streaming**
* Designing **microservices architecture** with Spring Boot
* Real-world use case: **e-commerce analytics**
* Building **resume-worthy project** to showcase event-driven systems

---