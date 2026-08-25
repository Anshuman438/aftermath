# AFTERMATH — Architecture & Technical Specifications

> **System Overview**: Automated incident capture, live replay, zero-touch dependency injection, and JUnit 5 test generation platform for Spring Boot microservice architectures.

---

## 🏛️ System Component Topology

```
+-----------------------------------------------------------------------------------+
|                                  TARGET MICROSERVICE                              |
|                                                                                   |
|  [HTTP Controller] --> [CaptureFilter (aftermath-sdk)]                            |
|                             |                                                     |
|                             v                                                     |
|                      [FailureDetector] (Status >= 400 or Uncaught Exception)      |
|                             |                                                     |
|                             v                                                     |
|                      [RedactionEngine] (Mask Bearer Tokens, Cookies, PII)          |
|                             |                                                     |
|                             v (Async Fail-Open Daemon Dispatcher)                 |
+-----------------------------|-----------------------------------------------------+
                              | HTTP POST /api/v1/incidents
                              v
+-----------------------------------------------------------------------------------+
|                        AFTERMATH COLLECTOR SERVICE (:8090)                        |
|                                                                                   |
|  [IncidentController] --> [IncidentService] --> [SQLite / H2 Persistence Engine]   |
|         |                        |                         |                      |
|         v                        v                         v                      |
|  [ReplayController]     [TestGenService]          [TestArtifactRepository]        |
+---------|------------------------|------------------------------------------------+
          |                        |
          v                        v
+-------------------+    +-------------------+
| AFTERMATH REPLAY  |    | AFTERMATH TESTGEN |
| Re-executes HTTP  |    | Generates JUnit 5 |
| Request Snapshot  |    | .java Code File   |
+-------------------+    +-------------------+
          |                        |
          +------------+-----------+
                       |
                       v
+-----------------------------------------------------------------------------------+
|                            DEVELOPER INTERFACES                                   |
|                                                                                   |
|  1. Web Dashboard (:5173) -- React 18 + Vite + Tailwind CSS                       |
|  2. Developer CLI (aftermath-cli) -- status, list, view, replay, testgen, attach  |
+-----------------------------------------------------------------------------------+
```

---

## ⚡ Core Subsystems & Operational Guarantees

### 1. Fail-Open Isolation Guarantee (`aftermath-sdk`)
- The SDK utilizes a non-blocking `AsyncEventDispatcher` with a bounded queue size (100 events max) and daemon thread pool.
- If the Collector Service (`:8090`) is unreachable or offline, the SDK logs a single warning and drops the payload. It **never throws exceptions** to the host application or impacts client user experience.
- Overhead on normal successful HTTP 200 requests is `< 1ms`.

### 2. Redaction Engine Security Architecture
- Executed on the application host before the capsule leaves JVM memory.
- **Header Redaction**: `Authorization: Bearer ***`, `Cookie`, `X-API-Key`, and secret tokens are replaced with `[REDACTED]`.
- **Body Redaction**: Regular expression pattern matching for emails (`[EMAIL_REDACTED]`), phone numbers (`[PHONE_REDACTED]`), and credit cards (`[CC_REDACTED]`).

### 3. Replay Engine Verification (`aftermath-replay`)
- Reconstructs exact HTTP request snapshots using Java 11 `HttpClient`.
- Sanitizes host/restricted headers (`host`, `content-length`, `connection`) to prevent `IllegalArgumentException`.
- Re-submits payload to target microservice URL, records execution duration, and asserts whether status code matches (`reproduced: true`).

### 4. Zero-Touch Developer Auto-Attacher (`aftermath attach`)
- Allows 1-second onboarding of any existing Spring Boot microservice.
- **`ProjectDetector`**: Auto-detects Maven (`pom.xml`) or Gradle (`build.gradle`).
- **`PomXmlInjector`**: Parses XML DOM, checks if `aftermath-sdk` exists, and injects `<dependency>dev.aftermath:aftermath-sdk:0.1.0-SNAPSHOT</dependency>`.
- **`YamlConfigInjector`**: Parses `application.yml` or `application.properties` and injects `aftermath.sdk.enabled: true`.

---

## 📡 Port Allocation & Services

| Service | Module | Default Port | Description |
| :--- | :--- | :--- | :--- |
| **Collector API** | `aftermath-collector` | `8090` | Ingestion, storage, replay, and testgen REST endpoints |
| **Web Dashboard** | `aftermath-ui` | `5173` | React + Vite developer dashboard |
| **Payment Service** | `sample-app/payment-service` | `8082` | Demo service monitored by `aftermath-sdk` |
| **Coupon Service** | `sample-app/coupon-service` | `8081` | Demo upstream service |
