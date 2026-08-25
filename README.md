# 💥 AFTERMATH

> **Turn unexpected production failures into zero-touch, reproducible JUnit 5 integration tests.**

[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://jdk.java.net/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React 18](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

AFTERMATH is an automated incident capture, live replay, and test generation engine for microservices. When an unhandled runtime error occurs in production or staging, AFTERMATH captures the complete request snapshot (headers, body, trace ID, stack trace), automatically redacts sensitive data (tokens, cookies, PII), re-executes the failure live to verify reproduction, and outputs a ready-to-run **JUnit 5 / REST-Assured** unit test `.java` file.

---

## ⚡ 1-Click Zero-Touch Auto-Attacher (`aftermath attach`)

Attach AFTERMATH to **any existing Spring Boot microservice** automatically in 1 second with **zero manual code or configuration editing**:

```cmd
# Run inside any Spring Boot project directory:
java -jar aftermath-cli/target/aftermath-cli-0.1.0-SNAPSHOT.jar attach --path .
```

What `aftermath attach` does automatically:
1. Detects Maven (`pom.xml`) or Gradle (`build.gradle`).
2. Safely injects the `aftermath-sdk` dependency into `pom.xml`.
3. Injects the `aftermath.sdk` configuration block into `application.yml`.

---

## 🏛️ System Architecture

```mermaid
flowchart TD
    A["Client HTTP Request"] --> B["Target Service (e.g. payment-service)"]
    B --> C["Aftermath Capture Filter (aftermath-sdk)"]
    C -->|HTTP 200 OK| D["Return Response"]
    C -->|HTTP 500 Failure| E["Redaction Engine & Failure Detector"]
    E -->|Scrub Secrets & PII| F["Async Daemon Dispatcher (Fail-Open)"]
    F -->|POST Capsule| G["Collector Service (aftermath-collector :8090)"]
    G --> H[("SQLite / H2 Database")]
    
    H --> I["Web UI Dashboard (:5173)"]
    H --> J["CLI Tool (aftermath-cli)"]
    
    I & J --> K["Replay Engine (aftermath-replay)"]
    I & J --> L["Test Generator (aftermath-testgen)"]
    
    K -->|Re-executes Request| B
    L -->|Outputs .java File| M["JUnit 5 / REST-Assured Test Class"]
```

---

## ✨ Key Features

- **⚡ Zero-Touch Auto-Attacher (`aftermath attach`)**: 1-Second CLI project injector that auto-detects and configures Spring Boot applications automatically.
- **⚡ Zero-Overhead Capture SDK (`aftermath-sdk`)**: Spring Boot `@AutoConfiguration` interceptor with `< 1ms` overhead on normal requests and guaranteed **Fail-Open** execution.
- **🔒 Redaction Engine**: Automatically masks `Authorization: Bearer`, cookies, credit cards, emails, passwords, and sensitive PII before transmission.
- **📡 Central Collector Service (`aftermath-collector`)**: High-performance persistence layer with REST APIs for incident retrieval and management.
- **🔄 Live Replay Engine (`aftermath-replay`)**: Re-executes captured HTTP snapshots against target microservices to verify bug reproduction in real time.
- **🧪 Automated Test Generator (`aftermath-testgen`)**: Generates standalone, ready-to-commit **JUnit 5 + REST-Assured**, **Spring MockMvc**, or **Spring WebTestClient** test classes.
- **🌐 React Web Dashboard (`aftermath-ui`)**: Interactive React 18 + Vite + Tailwind CSS UI for stack trace inspection, header view, live replay, and one-click test code download.
- **💻 Developer CLI (`aftermath-cli`)**: Terminal command line tool (`aftermath attach`, `status`, `list`, `view`, `replay`, `testgen`).

---

## 🚀 1-Minute Quickstart

### Prerequisites
- **JDK 21**
- **Maven 3.8+**
- **Node.js 18+ & npm**

### Step 1: Clone & Start Full Stack
```powershell
git clone https://github.com/Anshuman438/aftermath.git
cd aftermath
powershell -ExecutionPolicy Bypass -File "start_aftermath_stack.ps1"
```

### Step 2: Trigger a Sample Production Bug
In a second terminal window, trigger an intentional `NullPointerException` on the sample `payment-service`:
```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/payments" -Method Post -Body '{"amount":100, "couponCode":"PREMIUM50", "customerId":"cust-42"}' -ContentType "application/json" -Headers @{ Authorization = "Bearer secret-token-999" }
```

### Step 3: Open Dashboard & Generate Test
1. Open **`http://localhost:5173`** in your browser.
2. Click the incident card to inspect the stack trace and redacted headers (`Authorization: Bearer [REDACTED]`).
3. Click **Replay Incident** to verify reproduction (`reproduced: true`).
4. Click **Generate JUnit Test** and **Download .java**!

---

## 💻 CLI Commands (`aftermath-cli`)

```cmd
# Auto-attach AFTERMATH SDK to any Spring Boot project automatically
java -jar aftermath-cli/target/aftermath-cli-0.1.0-SNAPSHOT.jar attach --path <PROJECT_DIR>

# Check Collector service connectivity status
java -jar aftermath-cli/target/aftermath-cli-0.1.0-SNAPSHOT.jar status

# List captured incidents in ASCII table format
java -jar aftermath-cli/target/aftermath-cli-0.1.0-SNAPSHOT.jar list

# Inspect detailed stack trace for an incident
java -jar aftermath-cli/target/aftermath-cli-0.1.0-SNAPSHOT.jar view <INCIDENT_ID>

# Replay an incident against target service
java -jar aftermath-cli/target/aftermath-cli-0.1.0-SNAPSHOT.jar replay <INCIDENT_ID> --target http://localhost:8082

# Generate JUnit 5 test file directly to disk
java -jar aftermath-cli/target/aftermath-cli-0.1.0-SNAPSHOT.jar testgen <INCIDENT_ID> --out src/test/java
```

---

## 📦 Project Structure

```
aftermath/
├── aftermath-sdk/          # Lightweight Spring Boot Interceptor SDK & Redactor
├── aftermath-collector/    # Central Ingestion & Database Persistence Service
├── aftermath-replay/       # Replay Engine using Java 11 HttpClient
├── aftermath-testgen/      # JUnit 5 / REST-Assured Test Generator Engine
├── aftermath-cli/          # Picocli Developer CLI (status, list, view, replay, testgen, attach)
├── aftermath-ui/           # React 18 + Vite + Tailwind CSS Dashboard
├── sample-app/             # Demo Microservices (coupon-service, payment-service)
└── start_aftermath_stack.ps1 # 1-Click System Orchestrator Script
```

---

## 📄 License

Distributed under the Apache 2.0 License. See `LICENSE` for details.
