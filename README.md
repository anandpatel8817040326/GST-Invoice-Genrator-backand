# Enterprise Free GST Invoice Generator (Microservices Architecture)

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![React](https://img.shields.io/badge/React-18-cyan.svg)](https://react.dev/)
[![Tailwind CSS](https://img.shields.io/badge/TailwindCSS-3.4-teal.svg)](https://tailwindcss.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A complete, production-quality, statutory Indian GST Invoice Generator inspired by **Zoho Invoice**. Built strictly as a decoupled **REST API Microservices Architecture** with 12 Spring Boot services, Netflix Eureka service discovery, Spring Cloud API Gateway, OpenFeign inter-service communication, Resilience4j circuit breakers, JWT authentication with RBAC, independent PostgreSQL databases per service, an OpenPDF A4 vector export engine, and a React + TypeScript frontend.

---

## Architecture Overview

```
                      +-----------------------------+
                      |   React + TypeScript UI     |
                      |   (Vite + Tailwind CSS)     |
                      +--------------+--------------+
                                     |
                                     | Port 8080 (REST / Bearer JWT)
                                     v
                      +-----------------------------+
                      |      Spring Cloud Gateway   |
                      |    (JWT Filter + Routing)   |
                      +--------------+--------------+
                                     |
    +--------------------------------+--------------------------------+
    |                                |                                |
    v                                v                                v
+-------------+              +---------------+                +---------------+
| eureka-     |              |  auth-service |                |business-      |
| server      |              |  (Port 8081)  |                |service (8082) |
| (Port 8761) |              |  [auth_db]    |                | [business_db] |
+-------------+              +---------------+                +---------------+
    ^                                |                                |
    | OpenFeign / Service Discovery  v                                v
+-------------+              +---------------+                +---------------+
| customer-   |              |product-service|                | gst-service   |
| service     |              |  (Port 8084)  |                | (Port 8086)   |
| (Port 8083) |              |  [product_db] |                | [gst_db]      |
| [customer_db|              +---------------+                +---------------+
+-------------+                      |                                |
    ^                                |                                |
    +--------------------------------+--------------------------------+
                                     | OpenFeign Orchestration
                                     v
                      +-----------------------------+
                      |       invoice-service       |
                      |         (Port 8085)         |
                      |        [invoice_db]         |
                      +--------------+--------------+
                                     |
    +--------------------------------+--------------------------------+
    |                                |                                |
    v                                v                                v
+-------------+              +---------------+                +---------------+
| payment-    |              |  pdf-service  |                | notification- |
| service     |              |  (Port 8088)  |                | service (8089)|
| (Port 8087) |              | (Vector Engine|                | [notif_db]    |
| [payment_db]|              +---------------+                +---------------+
+-------------+                                                       |
    |                                                                 v
    +----------------------------------------------------> +---------------+
                                                           | report-service|
                                                           |  (Port 8090)  |
                                                           |  [report_db]  |
                                                           +---------------+
```

---

## Microservices Breakdown

| Service | Port | Database | Primary Responsibility | Key Technologies |
| :--- | :--- | :--- | :--- | :--- |
| **`eureka-server`** | `8761` | None | Service Registry & Dynamic Discovery | Netflix Eureka Server |
| **`api-gateway`** | `8080` | None | Unified Entry Point, JWT Auth Filter, CORS | Spring Cloud Gateway, JJWT |
| **`auth-service`** | `8081` | `auth_db` | User Auth, JWT Issuance, Refresh Rotation, RBAC | Spring Security, BCrypt, JPA |
| **`business-service`** | `8082` | `business_db` | Company Profiles, GSTIN, PAN, Bank Details, Team | Spring Data JPA, OpenFeign |
| **`customer-service`** | `8083` | `customer_db` | Customer Directory, GSTIN, Place of Supply | Spring Data JPA |
| **`product-service`** | `8084` | `product_db` | Product/Service Catalog, HSN/SAC, Rates | Spring Data JPA, BigDecimal |
| **`invoice-service`** | `8085` | `invoice_db` | Core Invoicing, Sequential Numbers, Lifecycle | OpenFeign, Resilience4j, JPA |
| **`gst-service`** | `8086` | `gst_db` | Statutory GST Splits, Slabs, HSN Master | Tax Engine, Indian Currency Util |
| **`payment-service`** | `8087` | `payment_db` | Payment Recording, Receipts, UPI/NEFT tracking | Spring Data JPA, OpenFeign |
| **`pdf-service`** | `8088` | Temp Storage | Vector Print-Ready A4 PDF Generation | OpenPDF (LibrePDF), A4 Layout |
| **`notification-service`** | `8089` | `notification_db` | Invoice Dispatch, Receipts, Email Notifications | Spring Mail, JPA Logs |
| **`report-service`** | `8090` | `report_db` | Dashboard Analytics, GSTR-1, Receivables | OpenFeign Read Models, JPA |
| **`frontend`** | `5173` | Local Storage | SaaS Billing Portal Inspired by Zoho Invoice | React 18, TypeScript, Tailwind |

---

## Indian GST Statutory Routing Rules

The calculation engine strictly conforms to the **Central Goods and Services Tax Act**:

### 1. Place of Supply (POS) Decision Rule
$$\text{Supply Type} = \begin{cases} \text{Intra-State}, & \text{if Seller State Code} = \text{Buyer State Code} \\ \text{Inter-State}, & \text{if Seller State Code} \neq \text{Buyer State Code} \end{cases}$$

- **Intra-State Supply**: Tax is split 50/50:
  $$\text{CGST Rate} = \frac{\text{GST Rate}}{2}, \quad \text{SGST Rate} = \frac{\text{GST Rate}}{2}, \quad \text{IGST} = 0$$
- **Inter-State Supply**: 100% Integrated Tax applies:
  $$\text{IGST Rate} = \text{GST Rate}, \quad \text{CGST} = 0, \quad \text{SGST} = 0$$

### 2. Financial Math Pipeline (`BigDecimal` with `HALF_UP`)
1. **Taxable Amount**:
   $$\text{Taxable Amount} = (\text{Unit Price} \times \text{Quantity}) - \text{Discount}$$
2. **Line Item Tax**:
   $$\text{Total Tax} = \frac{\text{Taxable Amount} \times \text{GST Rate}}{100}$$
3. **Grand Total**:
   $$\text{Grand Total} = \text{Taxable Subtotal} - \text{Overall Discount} + \text{Total Tax} + \text{Round Off}$$

---

## Quick Start & Execution

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- Node.js 20+ & pnpm / npm
- PostgreSQL 16+ OR Docker Desktop

### 1. Database Setup
Create the independent databases in PostgreSQL:
```bash
psql -U postgres -f init-databases.sql
```

### 2. Start Services via Docker Compose
To run all 12 microservices, PostgreSQL, and the React frontend simultaneously:
```bash
docker-compose up -d --build
```

### 3. Running Services Locally (Step-by-Step)
If running services individually without Docker:

1. **Start Eureka Server**:
   ```bash
   cd eureka-server
   mvn spring-boot:run
   # Access dashboard at: http://localhost:8761
   ```
2. **Start API Gateway**:
   ```bash
   cd api-gateway
   mvn spring-boot:run
   # Running on: http://localhost:8080
   ```
3. **Start Core Microservices** (in separate terminal tabs):
   ```bash
   cd auth-service && mvn spring-boot:run
   cd business-service && mvn spring-boot:run
   cd customer-service && mvn spring-boot:run
   cd product-service && mvn spring-boot:run
   cd gst-service && mvn spring-boot:run
   cd invoice-service && mvn spring-boot:run
   cd payment-service && mvn spring-boot:run
   cd pdf-service && mvn spring-boot:run
   cd notification-service && mvn spring-boot:run
   cd report-service && mvn spring-boot:run
   ```
4. **Start Frontend (React + Vite)**:
   ```bash
   cd frontend
   npm install
   npm run dev
   # Open browser at: http://localhost:5173
   ```

---

## Postman API Collection

A full Postman test collection is provided in:
`postman/GST_Invoice_Microservices.postman_collection.json`

Import into Postman to test:
- User signup & JWT token generation
- Automatic Place of Supply calculation
- OpenFeign synchronous invoice generation
- Binary PDF streaming endpoint: `GET /api/pdf/invoice/{id}`
- GSTR-1 statutory sales reporting

---

## Interview Preparation & Architecture Defense

> **Q1: Why choose a microservices architecture over a monolith for a GST billing platform?**  
> *Answer:* High-throughput invoice generation and PDF rendering are CPU-intensive operations that would bottleneck a monolithic application. By isolating the calculation engine (`gst-service`) and vector generation (`pdf-service`), we can scale them independently without affecting user authentication or inventory queries.

> **Q2: How is data consistency maintained without a shared database?**  
> *Answer:* Each microservice owns its schema strictly. For critical operations like invoice issuance and payment settlements, services communicate via OpenFeign with Resilience4j circuit breakers and transactional audit records (`InvoiceHistory`). Invoices store an immutable snapshot of seller, customer, and tax calculations at the moment of issuance.

> **Q3: How is floating point rounding prevented in tax calculations?**  
> *Answer:* In Indian accounting, floating point arithmetic (`float`/`double`) introduces IEEE 754 precision leakage. All financial values use Java's `java.math.BigDecimal` with explicit scale `2` and `RoundingMode.HALF_UP`.

