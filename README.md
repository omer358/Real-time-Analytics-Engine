# 📊 Real-Time E-Commerce Analytics Engine

## 📌 Overview

This project is a **real-time analytics backend** for e-commerce platforms, built with **Spring Boot (Java)**, **Kafka**, and **PostgreSQL**.

The system is designed to:

1. Ingest **e-commerce events** (like purchases, product views, cart updates).
2. Stream them into **Kafka** for decoupled processing.
3. Consume and process events in **real-time**.
4. Store insights for analytics and reporting.

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

## 🚀 Getting Started

### Prerequisites

* Java 17
* Maven

## 🎯 Roadmap

* [x] Setup ingestion service with **PurchaseEvent endpoint**
* [ ] Add more event types (product views, add-to-cart)
* [ ] Implement Kafka consumer logic in processing-service
* [ ] Store processed results in PostgreSQL
* [ ] Add REST APIs in analytics-service for querying analytics
* 
---

## 📖 Learning Goals

* Practice with **Kafka event streaming**
* Build a **modular microservices architecture** with Spring Boot
* Learn how **real-time e-commerce analytics pipelines** are designed
* Create a **resume-ready project** to showcase event-driven design

---
