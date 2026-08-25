# AFTERMATH — Complete Development Guide

### From Zero to Production-Ready MVP

> **Document Version**: 1.0  
> **Based on**: Aftermath SRS v0.1  
> **Target Stack**: Java 21 · Spring Boot 3.x · React + Vite · SQLite/PostgreSQL · Docker · JUnit 5  
> **Estimated Timeline**: 3–4 months (full-time) | 5–7 months (part-time)

---

## Table of Contents

1. [Development Environment Setup](#1-development-environment-setup)
2. [Project Structure & Repository](#2-project-structure--repository)
3. [Phase 0 — Foundation: Sample Application](#3-phase-0--foundation-sample-application)
4. [Phase 1 — Capture SDK](#4-phase-1--capture-sdk)
5. [Phase 2 — Collector Service & Storage](#5-phase-2--collector-service--storage)
6. [Phase 3 — Local Web UI](#6-phase-3--local-web-ui)
7. [Phase 4 — Replay Engine](#7-phase-4--replay-engine)
8. [Phase 5 — Test Generator](#8-phase-5--test-generator)
9. [Phase 6 — CLI Tool](#9-phase-6--cli-tool)
10. [Phase 7 — Integration, Polish & Hardening](#10-phase-7--integration-polish--hardening)
11. [Phase 8 — Documentation & Release](#11-phase-8--documentation--release)
12. [Phase 9 — Post-MVP Enhancements](#12-phase-9--post-mvp-enhancements)
13. [Appendix A — Technology Reference](#13-appendix-a--technology-reference)
14. [Appendix B — Coding Standards](#14-appendix-b--coding-standards)
15. [Appendix C — Git Workflow](#15-appendix-c--git-workflow)

---

## 1. Development Environment Setup

### 1.1 Required Software Installation

| # | Software | Version | Purpose | Install Command / Source |
|:-:|:---------|:--------|:--------|:-------------------------|
| 1 | **JDK 21** (LTS) | 21.x | Backend runtime | `winget install EclipseAdoptium.Temurin.21.JDK` or [adoptium.net](https://adoptium.net) |
| 2 | **Maven** | 3.9+ | Java build tool | `winget install Apache.Maven` |
| 3 | **Node.js** | 20 LTS | Frontend runtime | `winget install OpenJS.NodeJS.LTS` |
| 4 | **Git** | Latest | Version control | `winget install Git.Git` |
| 5 | **Docker Desktop** | Latest | Container runtime | `winget install Docker.DockerDesktop` |
| 6 | **IntelliJ IDEA Community** | Latest | Java IDE | `winget install JetBrains.IntelliJIDEA.Community` |
| 7 | **VS Code** | Latest | Frontend IDE | `winget install Microsoft.VisualStudio.Code` |
| 8 | **Postman** (optional) | Latest | API testing | `winget install Postman.Postman` |

### 1.2 Verify Installations

```bash
# Run each command and verify output
java --version          # Should show: openjdk 21.x.x
mvn --version           # Should show: Apache Maven 3.9.x
node --version          # Should show: v20.x.x
npm --version           # Should show: 10.x.x
git --version           # Should show: git version 2.x.x
docker --version        # Should show: Docker version 2x.x.x
```

### 1.3 IDE Configuration

#### 1.3.1 IntelliJ IDEA Setup
- Install plugins: `Lombok`, `Spring Boot Assistant`, `Docker`
- Set Project SDK → JDK 21
- Enable annotation processing: `Settings → Build → Compiler → Annotation Processors → Enable`
- Set code style: `Settings → Editor → Code Style → Java → Import Google Java Style`

#### 1.3.2 VS Code Setup
- Install extensions: `ES7+ React Snippets`, `Prettier`, `ESLint`, `Tailwind CSS IntelliSense`
- Set default formatter to Prettier
- Enable format-on-save

### 1.4 Docker Desktop Configuration
- Ensure WSL 2 backend is enabled (Windows)
- Allocate minimum: 4 GB RAM, 2 CPUs to Docker
- Verify: `docker run hello-world`

---

## 2. Project Structure & Repository

### 2.1 Initialize Git Repository

```bash
mkdir aftermath
cd aftermath
git init
```

### 2.2 Create Monorepo Structure

```
aftermath/
├── README.md
├── LICENSE
├── .gitignore
├── docs/
│   ├── SRS.md                          # Your SRS document
│   ├── ARCHITECTURE.md                 # Architecture decisions
│   └── CHANGELOG.md                    # Release notes
│
├── aftermath-sdk/                      # Phase 1: Capture SDK (Java library)
│   ├── pom.xml
│   └── src/
│       ├── main/java/dev/aftermath/sdk/
│       └── test/java/dev/aftermath/sdk/
│
├── aftermath-collector/                # Phase 2: Collector backend service
│   ├── pom.xml
│   └── src/
│       ├── main/java/dev/aftermath/collector/
│       ├── main/resources/
│       └── test/java/dev/aftermath/collector/
│
├── aftermath-ui/                       # Phase 3: React frontend
│   ├── package.json
│   └── src/
│
├── aftermath-replay/                   # Phase 4: Replay engine
│   ├── pom.xml
│   └── src/
│       ├── main/java/dev/aftermath/replay/
│       └── test/java/dev/aftermath/replay/
│
├── aftermath-testgen/                  # Phase 5: Test generator
│   ├── pom.xml
│   └── src/
│       ├── main/java/dev/aftermath/testgen/
│       ├── main/resources/templates/
│       └── test/java/dev/aftermath/testgen/
│
├── aftermath-cli/                      # Phase 6: CLI tool
│   ├── pom.xml
│   └── src/
│       ├── main/java/dev/aftermath/cli/
│       └── test/java/dev/aftermath/cli/
│
├── sample-app/                         # Phase 0: Demo application
│   ├── payment-service/
│   │   ├── pom.xml
│   │   └── src/
│   ├── coupon-service/
│   │   ├── pom.xml
│   │   └── src/
│   └── docker-compose.yml
│
├── pom.xml                             # Parent POM (multi-module Maven)
└── docker-compose.yml                  # Full stack local deployment
```

### 2.3 Create Parent POM

Create `aftermath/pom.xml` — the root Maven multi-module project:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>
    <groupId>dev.aftermath</groupId>
    <artifactId>aftermath-parent</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>Aftermath</name>
    <description>Production failures into reproducible tests</description>

    <modules>
        <module>aftermath-sdk</module>
        <module>aftermath-collector</module>
        <module>aftermath-replay</module>
        <module>aftermath-testgen</module>
        <module>aftermath-cli</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <spring-boot.version>3.3.0</spring-boot.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 2.4 Create .gitignore

```gitignore
# Java
target/
*.class
*.jar
*.war
*.log
.idea/
*.iml

# Node
node_modules/
dist/
.env
.env.local

# Docker
docker-compose.override.yml

# OS
.DS_Store
Thumbs.db

# SQLite
*.db
*.sqlite
```

### 2.5 Initial Commit

```bash
git add .
git commit -m "chore: initial project structure and parent POM"
```

---

## 3. Phase 0 — Foundation: Sample Application

> **Goal**: A working multi-service Spring Boot app with an intentional, reproducible production-like failure.  
> **Duration**: 3–5 days  
> **Deliverable**: Two services (payment + coupon) where a specific request triggers a 500 error

### 3.1 Create Coupon Service

#### 3.1.1 Initialize Spring Boot Project

```bash
cd sample-app/coupon-service
```

Generate via [start.spring.io](https://start.spring.io) or Maven archetype:
- **Group**: `dev.aftermath.sample`
- **Artifact**: `coupon-service`
- **Dependencies**: Spring Web, Spring Boot Actuator, Lombok

#### 3.1.2 Create Coupon REST Controller

```
src/main/java/dev/aftermath/sample/coupon/
├── CouponServiceApplication.java       # @SpringBootApplication main class
├── controller/
│   └── CouponController.java           # GET /api/coupons/{code}
├── model/
│   └── CouponResponse.java             # { code, discount, valid }
└── service/
    └── CouponService.java              # Business logic with intentional bug
```

**Intentional Bug**: When coupon code is `"PREMIUM50"`, return a response with `discount: null` instead of a valid number. This simulates a real data issue.

#### 3.1.3 Configure Application

```yaml
# application.yml
server:
  port: 8081

spring:
  application:
    name: coupon-service

management:
  endpoints:
    web:
      exposure:
        include: health, info
```

#### 3.1.4 Test the Coupon Service

```bash
mvn spring-boot:run
# Test normal:    curl http://localhost:8081/api/coupons/SAVE10    → 200 {discount: 10}
# Test buggy:     curl http://localhost:8081/api/coupons/PREMIUM50 → 200 {discount: null}
```

### 3.2 Create Payment Service

#### 3.2.1 Initialize Spring Boot Project

Same as coupon service but:
- **Artifact**: `payment-service`
- **Additional Dependencies**: Spring WebClient (for calling coupon service)

#### 3.2.2 Create Payment REST Controller & Service

```
src/main/java/dev/aftermath/sample/payment/
├── PaymentServiceApplication.java
├── controller/
│   └── PaymentController.java           # POST /api/payments
├── model/
│   ├── PaymentRequest.java              # { amount, couponCode, customerId }
│   └── PaymentResponse.java             # { transactionId, finalAmount, status }
├── client/
│   └── CouponClient.java               # WebClient call to coupon-service
└── service/
    └── PaymentService.java              # Applies discount — NPE when discount is null
```

**The Failure Chain**:
1. Client sends `POST /api/payments` with `couponCode: "PREMIUM50"`
2. Payment service calls coupon service → gets `discount: null`
3. Payment service does `amount - discount.doubleValue()` → **NullPointerException**
4. HTTP 500 returned to client

#### 3.2.3 Configure Application

```yaml
# application.yml
server:
  port: 8080

spring:
  application:
    name: payment-service

coupon-service:
  base-url: http://localhost:8081
```

#### 3.2.4 Add Trace ID Support

Add a filter that generates/propagates a trace ID:

```java
// TraceIdFilter.java — Servlet filter
// If X-Trace-Id header exists, use it; otherwise generate UUID
// Store in MDC for logging
// Add to response headers
```

### 3.3 Docker Compose for Sample App

```yaml
# sample-app/docker-compose.yml
version: '3.8'

services:
  coupon-service:
    build: ./coupon-service
    ports:
      - "8081:8081"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 10s
      retries: 3

  payment-service:
    build: ./payment-service
    ports:
      - "8080:8080"
    environment:
      COUPON_SERVICE_BASE_URL: http://coupon-service:8081
    depends_on:
      coupon-service:
        condition: service_healthy
```

### 3.4 Create Dockerfiles

```dockerfile
# sample-app/payment-service/Dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.5 End-to-End Verification

```bash
# Build both services
cd sample-app/coupon-service && mvn clean package -DskipTests
cd ../payment-service && mvn clean package -DskipTests

# Start with Docker Compose
cd .. && docker-compose up --build

# Trigger the failure
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer test-secret-token-12345" \
  -d '{"amount": 100.00, "couponCode": "PREMIUM50", "customerId": "cust-42"}'

# Expected: HTTP 500 with NullPointerException
```

### 3.6 Phase 0 Checklist

- [ ] Coupon service runs and returns coupons
- [ ] Payment service runs and calls coupon service
- [ ] `PREMIUM50` coupon triggers NullPointerException → 500
- [ ] Trace ID propagates between services
- [ ] Docker Compose runs both services together
- [ ] Committed to Git: `git commit -m "feat(sample): add payment and coupon services with intentional NPE bug"`

---

## 4. Phase 1 — Capture SDK

> **Goal**: A lightweight Java library that auto-captures failed HTTP requests in any Spring Boot app  
> **Duration**: 1–2 weeks  
> **Deliverable**: `aftermath-sdk` JAR that intercepts 4xx/5xx and emits structured failure events

### 4.1 SDK Module Setup

#### 4.1.1 Create `aftermath-sdk/pom.xml`

```xml
<project>
    <parent>
        <groupId>dev.aftermath</groupId>
        <artifactId>aftermath-parent</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </parent>

    <artifactId>aftermath-sdk</artifactId>
    <name>Aftermath SDK</name>
    <description>Capture SDK for Spring Boot applications</description>

    <dependencies>
        <!-- Spring Boot Web (provided — app already has it) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- Jackson for JSON serialization -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>

        <!-- SLF4J logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>

        <!-- Test dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### 4.2 SDK Package Structure

```
aftermath-sdk/src/main/java/dev/aftermath/sdk/
├── AftermathAutoConfiguration.java      # Spring Boot auto-configuration
├── AftermathProperties.java             # Configuration properties
├── capture/
│   ├── CaptureFilter.java              # Servlet filter — the main interceptor
│   ├── RequestWrapper.java             # Caches request body for re-reading
│   ├── ResponseWrapper.java            # Caches response body for inspection
│   └── FailureDetector.java            # Decides if a response is a "failure"
├── model/
│   ├── IncidentEvent.java              # The failure capsule data structure
│   ├── RequestSnapshot.java            # Sanitized request data
│   ├── ErrorSnapshot.java              # Exception/error data
│   └── DeploymentInfo.java             # Version, git commit, service name
├── redaction/
│   ├── RedactionEngine.java            # Orchestrates all redaction rules
│   ├── RedactionRule.java              # Interface for a single rule
│   ├── HeaderRedactor.java             # Redacts Authorization, Cookie, etc.
│   ├── BodyRedactor.java               # Redacts PII patterns in body
│   └── PatternRegistry.java            # Built-in patterns (email, phone, API key)
├── transport/
│   ├── EventTransport.java             # Interface: how to send captured events
│   ├── HttpEventTransport.java         # Sends events to collector via HTTP
│   ├── LogEventTransport.java          # Logs events to file (fallback)
│   └── AsyncEventDispatcher.java       # Async wrapper — non-blocking dispatch
└── context/
    ├── TraceContextExtractor.java       # Reads trace/request IDs from headers
    └── DeploymentContextProvider.java   # Reads service name, version, git commit
```

### 4.3 Core Component: Capture Filter

#### 4.3.1 Design the CaptureFilter

The `CaptureFilter` is a `javax.servlet.Filter` (or `jakarta.servlet.Filter` for Spring Boot 3.x) that:

1. Wraps the request to cache the body (so it can be read twice)
2. Wraps the response to capture the status code and body
3. Lets the request proceed normally through the filter chain
4. After the response is committed, checks if it's a failure (4xx/5xx or exception)
5. If failure → builds an `IncidentEvent` → sends it asynchronously to the collector

```
Request ──→ [CaptureFilter] ──→ [App Controller] ──→ Response
                │                                        │
                └── After response: Is status >= 400? ───┘
                          │ YES
                          ▼
                    Build IncidentEvent
                          │
                          ▼
                    Redact sensitive data
                          │
                          ▼
                    Async dispatch to collector
```

#### 4.3.2 Implementation Priorities

| Sub-task | Priority | Details |
|:---------|:---------|:--------|
| Request body caching wrapper | P0 | Use `ContentCachingRequestWrapper` or custom impl |
| Response status capture | P0 | Read from `ContentCachingResponseWrapper` |
| Exception capture | P0 | Use `HandlerExceptionResolver` or `@ControllerAdvice` integration |
| Async non-blocking dispatch | P0 | Use `CompletableFuture` or `ExecutorService` — never block the request thread |
| Configurable failure detection | P1 | Allow users to define custom failure conditions beyond status codes |

#### 4.3.3 Key Design Decision: Fail-Open

**Critical**: If the Aftermath SDK or collector is broken/unavailable, the application must continue working normally. The capture filter must:
- Catch all internal exceptions silently
- Log warnings but never throw
- Use timeouts on HTTP dispatch (e.g., 2 seconds max)
- Queue events in memory with bounded size (e.g., 100 events max)

### 4.4 Core Component: Redaction Engine

#### 4.4.1 Built-in Redaction Rules

| Pattern | Target | Replacement |
|:--------|:-------|:------------|
| `Authorization` header | Request headers | `[REDACTED]` |
| `Cookie` header | Request headers | `[REDACTED]` |
| `X-API-Key` header | Request headers | `[REDACTED]` |
| Email regex | Request/response body | `[EMAIL_REDACTED]` |
| Phone regex | Request/response body | `[PHONE_REDACTED]` |
| Credit card regex | Request/response body | `[CC_REDACTED]` |
| JWT token pattern | Anywhere | `[TOKEN_REDACTED]` |
| Custom user patterns | Configurable | User-defined replacement |

#### 4.4.2 Redaction Engine Design

```java
// RedactionEngine applies all rules in sequence
public class RedactionEngine {
    private final List<RedactionRule> rules;

    public String redact(String input, RedactionScope scope) {
        String result = input;
        for (RedactionRule rule : rules) {
            if (rule.appliesTo(scope)) {
                result = rule.apply(result);
            }
        }
        return result;
    }
}
```

#### 4.4.3 Redaction Tests (Write These FIRST)

```
aftermath-sdk/src/test/java/dev/aftermath/sdk/redaction/
├── HeaderRedactorTest.java              # Test: Authorization header → [REDACTED]
├── BodyRedactorTest.java                # Test: email/phone/CC patterns masked
├── PatternRegistryTest.java             # Test: all built-in patterns match correctly
└── RedactionEngineTest.java             # Test: full pipeline, no secrets in output
```

**Test cases to cover:**
- Authorization Bearer token → redacted
- JSON body with `"email": "user@example.com"` → masked
- Nested JSON with API key → masked
- Body with no sensitive data → unchanged
- Empty body → no crash
- Malformed JSON body → treated as raw string, patterns still applied

### 4.5 Core Component: Event Transport

#### 4.5.1 HTTP Transport

```java
// Sends the IncidentEvent as JSON POST to the collector
// URL default: http://localhost:8090/api/v1/incidents
// Timeout: 2 seconds
// On failure: log warning, do NOT retry (MVP), do NOT throw
```

#### 4.5.2 Async Dispatcher

```java
// Wraps any EventTransport in an async executor
// Uses a bounded thread pool (2-4 threads)
// Uses a bounded queue (100 events max)
// If queue full → drop oldest event, log warning
```

### 4.6 Auto-Configuration

#### 4.6.1 Spring Boot Auto-Configuration

Create `AftermathAutoConfiguration.java`:
- Annotate with `@Configuration` and `@ConditionalOnWebApplication`
- Register the `CaptureFilter` as a bean
- Read configuration from `aftermath.*` properties
- Default: enabled, collector at `http://localhost:8090`

#### 4.6.2 Configuration Properties

```yaml
# application.yml of the USER's app
aftermath:
  enabled: true                          # Kill switch
  collector-url: http://localhost:8090    # Where to send events
  capture:
    status-codes: "400-599"              # Which codes to capture
    exclude-paths:                       # Paths to ignore
      - /actuator/**
      - /health
    max-body-size: 64KB                  # Truncate large bodies
    sampling-rate: 1.0                   # 1.0 = capture all, 0.1 = 10%
  redaction:
    enabled: true
    additional-header-patterns:          # Extra headers to redact
      - X-Custom-Secret
    additional-body-patterns:            # Extra body patterns
      - "ssn"
      - "password"
```

#### 4.6.3 Register Auto-Configuration

Create `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:
```
dev.aftermath.sdk.AftermathAutoConfiguration
```

### 4.7 Integrate SDK into Sample App

#### 4.7.1 Add SDK Dependency to Payment Service

```xml
<!-- sample-app/payment-service/pom.xml -->
<dependency>
    <groupId>dev.aftermath</groupId>
    <artifactId>aftermath-sdk</artifactId>
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

#### 4.7.2 Verify Capture

1. Start the payment service
2. Trigger the 500 error
3. Check logs — SDK should log: `"Aftermath: Captured incident for POST /api/payments [500]"`
4. At this point, the collector isn't built yet — SDK should gracefully log a warning about failed delivery

### 4.8 SDK Test Suite

```
aftermath-sdk/src/test/java/dev/aftermath/sdk/
├── capture/
│   ├── CaptureFilterTest.java           # Unit test: filter captures 500, ignores 200
│   ├── CaptureFilterIntegrationTest.java # Integration: full Spring Boot request cycle
│   ├── RequestWrapperTest.java          # Body caching works correctly
│   └── FailureDetectorTest.java         # Configurable failure detection
├── redaction/
│   ├── HeaderRedactorTest.java
│   ├── BodyRedactorTest.java
│   └── RedactionEngineTest.java
├── transport/
│   ├── HttpEventTransportTest.java      # Mocked HTTP — verifies JSON sent
│   ├── AsyncEventDispatcherTest.java    # Async non-blocking behavior
│   └── FailOpenTest.java               # SDK never crashes the app
└── model/
    └── IncidentEventTest.java           # Serialization/deserialization
```

### 4.9 Phase 1 Checklist

- [ ] SDK auto-configures in any Spring Boot app
- [ ] 4xx/5xx responses are captured with request context
- [ ] Uncaught exceptions are captured
- [ ] Authorization, Cookie, API keys are redacted
- [ ] PII patterns (email, phone, CC) are redacted
- [ ] Body size is bounded (64KB default)
- [ ] Capture is async — request latency is unaffected
- [ ] SDK failure does NOT crash the host application
- [ ] All redaction tests pass with zero leaked secrets
- [ ] Git commit: `feat(sdk): capture filter, redaction engine, async transport`

---

## 5. Phase 2 — Collector Service & Storage

> **Goal**: A backend service that receives, validates, stores, and serves incident data  
> **Duration**: 1–2 weeks  
> **Deliverable**: REST API + SQLite database for incidents

### 5.1 Collector Module Setup

#### 5.1.1 Create `aftermath-collector/pom.xml`

Key dependencies:
```xml
<dependencies>
    <!-- Spring Boot Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- SQLite JDBC driver -->
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.45.0.0</version>
    </dependency>

    <!-- SQLite Hibernate dialect -->
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-community-dialects</artifactId>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Flyway for DB migrations -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
</dependencies>
```

### 5.2 Collector Package Structure

```
aftermath-collector/src/main/java/dev/aftermath/collector/
├── CollectorApplication.java            # @SpringBootApplication (port 8090)
├── config/
│   ├── DatabaseConfig.java              # SQLite/PostgreSQL datasource setup
│   ├── CorsConfig.java                  # Allow localhost:5173 (Vite dev server)
│   └── JacksonConfig.java              # JSON serialization config
├── controller/
│   ├── IncidentController.java          # CRUD endpoints for incidents
│   ├── ReplayController.java           # Replay management endpoints (Phase 4)
│   └── TestArtifactController.java     # Test artifact endpoints (Phase 5)
├── dto/
│   ├── CreateIncidentRequest.java       # Inbound event from SDK
│   ├── IncidentResponse.java           # Outbound incident detail
│   ├── IncidentListResponse.java       # Paginated list response
│   └── IncidentFilterParams.java       # Search/filter query params
├── entity/
│   ├── IncidentEntity.java             # JPA entity: incidents table
│   ├── EvidenceEntity.java             # JPA entity: evidence table
│   ├── ReplayJobEntity.java            # JPA entity: replay_jobs table
│   └── TestArtifactEntity.java         # JPA entity: test_artifacts table
├── repository/
│   ├── IncidentRepository.java          # Spring Data JPA repository
│   ├── EvidenceRepository.java
│   ├── ReplayJobRepository.java
│   └── TestArtifactRepository.java
├── service/
│   ├── IncidentService.java             # Business logic for incidents
│   ├── IncidentDeduplicator.java       # Group similar incidents
│   └── IncidentValidator.java          # Validate incoming events
├── mapper/
│   └── IncidentMapper.java             # Entity ↔ DTO conversions
└── exception/
    ├── GlobalExceptionHandler.java      # @ControllerAdvice error handling
    └── IncidentNotFoundException.java
```

### 5.3 Database Schema

#### 5.3.1 Flyway Migration: `V1__initial_schema.sql`

```sql
CREATE TABLE incidents (
    id              TEXT PRIMARY KEY,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    service         TEXT NOT NULL,
    environment     TEXT DEFAULT 'local',
    method          TEXT NOT NULL,
    path            TEXT NOT NULL,
    status_code     INTEGER NOT NULL,
    error_type      TEXT,
    error_message   TEXT,
    request_headers TEXT,           -- JSON, sanitized
    request_body    TEXT,           -- JSON, sanitized
    response_body   TEXT,           -- JSON, truncated
    trace_id        TEXT,
    deployment_version TEXT,
    git_commit      TEXT,
    status          TEXT NOT NULL DEFAULT 'OPEN',  -- OPEN, REPRODUCED, FIXED, ARCHIVED
    tags            TEXT,           -- JSON array
    updated_at      TIMESTAMP
);

CREATE INDEX idx_incidents_service ON incidents(service);
CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_created_at ON incidents(created_at DESC);
CREATE INDEX idx_incidents_status_code ON incidents(status_code);

CREATE TABLE evidence (
    id              TEXT PRIMARY KEY,
    incident_id     TEXT NOT NULL REFERENCES incidents(id),
    type            TEXT NOT NULL,  -- LOG, TRACE, STACKTRACE, DEPLOYMENT, CUSTOM
    payload         TEXT NOT NULL,  -- JSON
    source          TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_evidence_incident ON evidence(incident_id);

CREATE TABLE replay_jobs (
    id              TEXT PRIMARY KEY,
    incident_id     TEXT NOT NULL REFERENCES incidents(id),
    target_url      TEXT NOT NULL,
    status          TEXT NOT NULL DEFAULT 'PENDING',  -- PENDING, RUNNING, COMPLETED, FAILED
    started_at      TIMESTAMP,
    completed_at    TIMESTAMP,
    original_status INTEGER,
    replay_status   INTEGER,
    replay_body     TEXT,
    match_result    TEXT,          -- MATCH, MISMATCH, ERROR
    error_detail    TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_replay_jobs_incident ON replay_jobs(incident_id);

CREATE TABLE test_artifacts (
    id              TEXT PRIMARY KEY,
    incident_id     TEXT NOT NULL REFERENCES incidents(id),
    replay_job_id   TEXT REFERENCES replay_jobs(id),
    framework       TEXT NOT NULL DEFAULT 'junit5',
    file_name       TEXT NOT NULL,
    content         TEXT NOT NULL,  -- Generated test source code
    content_hash    TEXT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_test_artifacts_incident ON test_artifacts(incident_id);
```

### 5.4 REST API Endpoints

#### 5.4.1 Incident Endpoints

| Method | Path | Purpose | Request Body | Response |
|:-------|:-----|:--------|:-------------|:---------|
| `POST` | `/api/v1/incidents` | Create incident (from SDK) | `CreateIncidentRequest` | `201` + `IncidentResponse` |
| `GET` | `/api/v1/incidents` | List incidents (paginated) | Query: `?status=OPEN&service=payment-service&page=0&size=20` | `200` + `Page<IncidentListResponse>` |
| `GET` | `/api/v1/incidents/{id}` | Get incident detail | — | `200` + `IncidentResponse` |
| `PATCH` | `/api/v1/incidents/{id}/status` | Update incident status | `{ "status": "FIXED" }` | `200` + `IncidentResponse` |
| `DELETE` | `/api/v1/incidents/{id}` | Archive/delete incident | — | `204` |
| `GET` | `/api/v1/incidents/stats` | Summary statistics | — | `200` + `{ total, open, fixed, topEndpoints[] }` |

#### 5.4.2 Implement Incident Deduplication

When a new incident arrives, check if a similar incident exists (same service + path + error_type within last 1 hour). If yes, increment a counter on the existing incident instead of creating a new record.

### 5.5 Configuration

```yaml
# application.yml
server:
  port: 8090

spring:
  application:
    name: aftermath-collector
  datasource:
    url: jdbc:sqlite:./aftermath.db
    driver-class-name: org.sqlite.JDBC
  jpa:
    database-platform: org.hibernate.community.dialect.SQLiteDialect
    hibernate:
      ddl-auto: validate          # Flyway manages schema
    show-sql: false
  flyway:
    enabled: true
    locations: classpath:db/migration

aftermath:
  collector:
    max-body-size: 65536          # 64KB
    deduplication-window: PT1H    # 1 hour
```

### 5.6 Collector Test Suite

```
aftermath-collector/src/test/java/dev/aftermath/collector/
├── controller/
│   ├── IncidentControllerTest.java      # MockMvc tests for all endpoints
│   └── IncidentControllerIntTest.java   # Full integration with SQLite
├── service/
│   ├── IncidentServiceTest.java         # Unit tests with mocked repo
│   └── IncidentDeduplicatorTest.java    # Dedup logic tests
├── repository/
│   └── IncidentRepositoryTest.java      # JPA query tests
└── validation/
    └── CreateIncidentRequestTest.java   # Bean validation tests
```

### 5.7 Integration Test: SDK → Collector

Create an end-to-end test:
1. Start collector (in-memory SQLite)
2. Start sample payment-service with SDK pointed at collector
3. Trigger the 500 error
4. Assert: incident appears in collector's GET /api/v1/incidents
5. Assert: Authorization header is redacted in stored incident

### 5.8 Phase 2 Checklist

- [ ] Collector starts on port 8090 with SQLite
- [ ] `POST /api/v1/incidents` accepts and stores events
- [ ] `GET /api/v1/incidents` returns paginated list with filters
- [ ] `GET /api/v1/incidents/{id}` returns full incident detail
- [ ] Duplicate incidents within 1 hour are grouped
- [ ] Database schema managed by Flyway migrations
- [ ] CORS configured for `localhost:5173`
- [ ] End-to-end: SDK captures → Collector stores → API returns
- [ ] Git commit: `feat(collector): REST API, SQLite storage, deduplication`

---

## 6. Phase 3 — Local Web UI

> **Goal**: A clean, functional React UI to view incidents, inspect details, and trigger replay/test actions  
> **Duration**: 1–2 weeks  
> **Deliverable**: React + Vite app running at `localhost:5173`

### 6.1 Initialize React Project

```bash
cd aftermath-ui
npm create vite@latest . -- --template react-ts
npm install
npm install axios react-router-dom @tanstack/react-query
npm install -D tailwindcss @tailwindcss/vite
```

### 6.2 UI Project Structure

```
aftermath-ui/src/
├── main.tsx                             # Entry point
├── App.tsx                              # Router setup
├── api/
│   ├── client.ts                        # Axios instance (baseURL: localhost:8090)
│   └── incidents.ts                     # API functions: getIncidents, getIncident, etc.
├── hooks/
│   ├── useIncidents.ts                  # React Query hook for incident list
│   ├── useIncident.ts                   # React Query hook for single incident
│   └── useReplay.ts                     # React Query mutation for replay trigger
├── pages/
│   ├── IncidentListPage.tsx             # Main dashboard — incident table
│   └── IncidentDetailPage.tsx           # Full incident view with actions
├── components/
│   ├── layout/
│   │   ├── Header.tsx                   # "AFTERMATH" brand bar
│   │   ├── Sidebar.tsx                  # Navigation (optional for MVP)
│   │   └── Layout.tsx                   # Page wrapper
│   ├── incidents/
│   │   ├── IncidentTable.tsx            # Sortable, filterable table
│   │   ├── IncidentRow.tsx              # Single row component
│   │   ├── IncidentStatusBadge.tsx      # Colored status pill (OPEN/FIXED/etc.)
│   │   ├── IncidentFilters.tsx          # Search bar + status/service dropdowns
│   │   └── IncidentStats.tsx            # Summary cards (total, open, fixed)
│   ├── detail/
│   │   ├── RequestPanel.tsx             # Method, path, headers, body display
│   │   ├── ErrorPanel.tsx               # Exception type, message, stacktrace
│   │   ├── TracePanel.tsx               # Trace ID, service chain visualization
│   │   ├── DeploymentPanel.tsx          # Version, git commit, environment
│   │   ├── ReplayPanel.tsx              # Replay result comparison
│   │   └── TestArtifactPanel.tsx        # Generated test display + copy button
│   └── common/
│       ├── Button.tsx                   # Reusable button component
│       ├── CodeBlock.tsx                # Syntax-highlighted code display
│       ├── JsonViewer.tsx               # Collapsible JSON tree
│       ├── DiffViewer.tsx               # Side-by-side diff display
│       ├── StatusBadge.tsx              # Generic status indicator
│       ├── Pagination.tsx               # Page controls
│       └── EmptyState.tsx               # "No incidents yet" placeholder
├── types/
│   ├── incident.ts                      # TypeScript types matching API responses
│   └── replay.ts                        # Replay-related types
├── utils/
│   ├── formatDate.ts                    # Date formatting helpers
│   ├── formatStatus.ts                  # Status code → human label
│   └── copyToClipboard.ts              # Clipboard utility
└── styles/
    └── globals.css                      # Tailwind imports + custom styles
```

### 6.3 Core Screens Design

#### 6.3.1 Incident List Page (Dashboard)

```
┌──────────────────────────────────────────────────────────┐
│  AFTERMATH                                    [Search…]  │
├──────────────────────────────────────────────────────────┤
│  [All: 24]  [Open: 8]  [Reproduced: 5]  [Fixed: 11]    │
├──────────────────────────────────────────────────────────┤
│  ID       Method  Path              Status  Time   Svc  │
│  ─────────────────────────────────────────────────────── │
│  INC-104  POST    /api/payments     500     14:03  pay  │
│  INC-103  GET     /api/orders       503     13:52  ord  │
│  INC-102  POST    /api/coupons      500     12:41  cpn  │
│  INC-101  PUT     /api/users/42     400     11:20  usr  │
│  …                                                       │
├──────────────────────────────────────────────────────────┤
│  ← Prev  Page 1 of 3  Next →                            │
└──────────────────────────────────────────────────────────┘
```

#### 6.3.2 Incident Detail Page

```
┌──────────────────────────────────────────────────────────┐
│  ← Back    INC-104    POST /api/payments    500   OPEN  │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  [ REPRODUCE ]    [ CREATE TEST ]    [ COPY cURL ]      │
│                                                          │
│  ┌─ Error ──────────────────────────────────────────┐   │
│  │  NullPointerException                             │   │
│  │  at PaymentService.java:47                        │   │
│  │  "Cannot invoke doubleValue() on null object"     │   │
│  └───────────────────────────────────────────────────┘   │
│                                                          │
│  ┌─ Request ────────────────────────────────────────┐   │
│  │  POST /api/payments                               │   │
│  │  Headers: Content-Type: application/json          │   │
│  │           Authorization: [REDACTED]               │   │
│  │  Body: { "amount": 100, "couponCode": "PREMIUM50" │   │
│  │          "customerId": "[EMAIL_REDACTED]" }       │   │
│  └───────────────────────────────────────────────────┘   │
│                                                          │
│  ▶ Trace    ▶ Deployment    ▶ Replay Result             │
└──────────────────────────────────────────────────────────┘
```

### 6.4 API Integration Layer

#### 6.4.1 Create Axios Client

```typescript
// src/api/client.ts
import axios from 'axios';

export const apiClient = axios.create({
  baseURL: 'http://localhost:8090/api/v1',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
});
```

#### 6.4.2 Create API Functions

```typescript
// src/api/incidents.ts
export const getIncidents = (params: IncidentFilterParams) =>
  apiClient.get<Page<IncidentSummary>>('/incidents', { params });

export const getIncident = (id: string) =>
  apiClient.get<IncidentDetail>(`/incidents/${id}`);

export const triggerReplay = (id: string, target: string) =>
  apiClient.post<ReplayJob>(`/incidents/${id}/replays`, { targetUrl: target });

export const generateTest = (id: string, framework: string) =>
  apiClient.post<TestArtifact>(`/incidents/${id}/tests`, { framework });
```

### 6.5 Key UI Components to Build (in order)

| Order | Component | Complexity | Notes |
|:-----:|:----------|:----------:|:------|
| 1 | `Layout` + `Header` | 🟢 Easy | Basic shell with brand name |
| 2 | `IncidentTable` + `IncidentRow` | 🟡 Medium | Main list view, clickable rows |
| 3 | `IncidentFilters` | 🟢 Easy | Search box + status dropdown |
| 4 | `Pagination` | 🟢 Easy | Simple prev/next |
| 5 | `IncidentDetailPage` | 🟡 Medium | Full incident view |
| 6 | `RequestPanel` + `JsonViewer` | 🟡 Medium | Formatted request display |
| 7 | `ErrorPanel` + `CodeBlock` | 🟡 Medium | Stack trace display |
| 8 | `StatusBadge` | 🟢 Easy | Colored pills |
| 9 | `ReplayPanel` (placeholder) | 🟢 Easy | Ready for Phase 4 |
| 10 | `TestArtifactPanel` (placeholder) | 🟢 Easy | Ready for Phase 5 |

### 6.6 Development Server

```bash
cd aftermath-ui
npm run dev
# Opens at http://localhost:5173
# Proxy API calls to http://localhost:8090
```

### 6.7 Phase 3 Checklist

- [ ] React app starts at `localhost:5173`
- [ ] Incident list loads from collector API
- [ ] Search by service name, status code, path works
- [ ] Clicking a row opens incident detail
- [ ] Request headers/body display with redacted values visible
- [ ] Stack trace displays in formatted code block
- [ ] Status badges show correct colors (red=open, green=fixed)
- [ ] "REPRODUCE" and "CREATE TEST" buttons render (disabled until Phase 4/5)
- [ ] "Copy cURL" button copies the captured request as a cURL command
- [ ] Mobile-responsive layout
- [ ] Git commit: `feat(ui): incident list, detail page, search and filters`

---

## 7. Phase 4 — Replay Engine

> **Goal**: Execute a captured request against a sandbox and compare the result to the original failure  
> **Duration**: 2–3 weeks  
> **Deliverable**: One-click replay with Docker sandbox isolation and result comparison

### 7.1 Replay Module Setup

#### 7.1.1 Create `aftermath-replay/pom.xml`

Key dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>dev.aftermath</groupId>
        <artifactId>aftermath-collector</artifactId>
        <version>${project.version}</version>
    </dependency>

    <!-- HTTP client for replaying requests -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <!-- Testcontainers for sandbox isolation -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
    </dependency>

    <!-- Docker Java client -->
    <dependency>
        <groupId>com.github.docker-java</groupId>
        <artifactId>docker-java-core</artifactId>
    </dependency>
</dependencies>
```

### 7.2 Replay Package Structure

```
aftermath-replay/src/main/java/dev/aftermath/replay/
├── ReplayService.java                   # Orchestrates the full replay pipeline
├── pipeline/
│   ├── ReplayPipeline.java              # Step-by-step replay execution
│   ├── ReplayStep.java                  # Interface for each pipeline step
│   ├── ValidateRequestStep.java         # Validate the captured request is replayable
│   ├── ResolveTargetStep.java           # Determine replay target (localhost / Docker)
│   ├── ApplySafetyPolicyStep.java       # Enforce safety rules before execution
│   ├── ExecuteRequestStep.java          # Actually send the HTTP request
│   ├── CaptureResponseStep.java         # Record the replay response
│   └── CompareResultStep.java           # Compare original vs replay
├── target/
│   ├── ReplayTarget.java                # Interface: where to send the replay
│   ├── LocalhostTarget.java             # Direct localhost replay
│   ├── DockerTarget.java                # Replay into a Docker container
│   └── TargetResolver.java             # Pick the right target based on config
├── safety/
│   ├── SafetyPolicy.java                # Interface for safety checks
│   ├── ProductionBlockPolicy.java       # Block any production-looking URLs
│   ├── AllowlistPolicy.java             # Only allow specified hosts/ports
│   ├── PayloadSizePolicy.java           # Limit request body size
│   └── TimeoutPolicy.java              # Enforce max execution time
├── comparison/
│   ├── ReplayComparator.java            # Compare original vs replay response
│   ├── StatusCodeComparator.java        # Did the status code match?
│   ├── ErrorTypeComparator.java         # Did the same exception type occur?
│   └── ComparisonResult.java            # MATCH / MISMATCH / PARTIAL_MATCH / ERROR
├── sandbox/
│   ├── SandboxManager.java              # Lifecycle management for Docker containers
│   ├── SandboxConfig.java               # Container image, ports, env vars
│   └── SandboxHealthCheck.java          # Wait for sandbox to be ready
└── model/
    ├── ReplayRequest.java               # Input to the replay pipeline
    ├── ReplayResult.java                # Output of the replay pipeline
    └── ReplayStatus.java                # PENDING, RUNNING, COMPLETED, FAILED
```

### 7.3 Replay Pipeline Design

```
Captured Incident
       │
       ▼
┌─────────────────┐
│ 1. VALIDATE     │  Is the request complete? Method, path, headers present?
└────────┬────────┘
         ▼
┌─────────────────┐
│ 2. SAFETY CHECK │  Is the target safe? Not production? Within allowlist?
└────────┬────────┘
         ▼
┌─────────────────┐
│ 3. RESOLVE      │  Where to send? localhost:8080? Docker container?
│    TARGET       │
└────────┬────────┘
         ▼
┌─────────────────┐
│ 4. PREPARE      │  Replace secrets with test values, inject test trace ID
│    REQUEST      │
└────────┬────────┘
         ▼
┌─────────────────┐
│ 5. EXECUTE      │  Send HTTP request via WebClient, capture response
└────────┬────────┘
         ▼
┌─────────────────┐
│ 6. COMPARE      │  Original: 500 NPE → Replay: 500 NPE → MATCH ✅
└────────┬────────┘
         ▼
┌─────────────────┐
│ 7. PERSIST      │  Store replay job, result, and comparison in DB
└─────────────────┘
```

### 7.4 Safety Rules Implementation

#### 7.4.1 Production Block (P0 — Must Have)

```java
// ProductionBlockPolicy.java
// Block any URL that:
//   - Contains "prod", "production", "live" in hostname
//   - Uses a non-private IP address
//   - Uses port 443 (HTTPS) without explicit opt-in
//   - Is not in the local allowlist
```

#### 7.4.2 Default Allowlist

```yaml
aftermath:
  replay:
    allowed-targets:
      - "http://localhost:*"
      - "http://127.0.0.1:*"
      - "http://host.docker.internal:*"
    blocked-keywords:
      - "prod"
      - "production"
      - "live"
      - "amazonaws.com"
      - "azure"
    max-timeout: 30s
    max-body-size: 1MB
```

### 7.5 Docker Sandbox Mode

#### 7.5.1 Sandbox Lifecycle

```
1. User clicks REPRODUCE
2. ReplayService checks: is Docker sandbox mode enabled?
   ├── YES → SandboxManager starts a container from configured image
   │         Wait for health check
   │         Execute replay against container
   │         Capture result
   │         Stop and remove container
   └── NO  → Execute replay against configured localhost target
```

#### 7.5.2 Sandbox Configuration

```yaml
aftermath:
  replay:
    sandbox:
      enabled: true
      image: "aftermath-sample/payment-service:latest"
      port: 8080
      startup-timeout: 60s
      environment:
        COUPON_SERVICE_BASE_URL: "http://mock-coupon:8081"
```

### 7.6 Wire Replay into UI

#### 7.6.1 New API Endpoint

```
POST /api/v1/incidents/{id}/replays
Body: { "targetUrl": "http://localhost:8080" }  (optional, uses default)
Response: { "replayId": "...", "status": "PENDING" }
```

```
GET /api/v1/replays/{replayId}
Response: { "status": "COMPLETED", "matchResult": "MATCH",
            "originalStatus": 500, "replayStatus": 500, ... }
```

#### 7.6.2 UI: Enable REPRODUCE Button

- Click REPRODUCE → show target selection modal (localhost / Docker)
- Show loading spinner during replay
- On completion → show result: ✅ MATCH / ❌ MISMATCH / ⚠️ ERROR
- Display side-by-side: original response vs. replay response

### 7.7 Replay Test Suite

```
aftermath-replay/src/test/java/dev/aftermath/replay/
├── pipeline/
│   └── ReplayPipelineTest.java          # Full pipeline with mocked HTTP
├── safety/
│   ├── ProductionBlockPolicyTest.java   # Production URLs are blocked
│   ├── AllowlistPolicyTest.java         # Only allowed hosts pass
│   └── TimeoutPolicyTest.java           # Requests timeout correctly
├── comparison/
│   ├── StatusCodeComparatorTest.java    # 500 == 500 → MATCH
│   └── ErrorTypeComparatorTest.java     # Same exception type → MATCH
├── sandbox/
│   └── SandboxManagerTest.java          # Docker container lifecycle
└── integration/
    └── ReplayIntegrationTest.java       # End-to-end: incident → replay → MATCH
```

### 7.8 Phase 4 Checklist

- [ ] Replay executes captured request against localhost
- [ ] Docker sandbox mode starts/stops containers
- [ ] Production URLs are blocked by default
- [ ] Only allowlisted targets are permitted
- [ ] Original vs. replay response comparison works
- [ ] MATCH/MISMATCH/ERROR result is clear
- [ ] Replay result persists in database
- [ ] UI REPRODUCE button triggers replay and shows result
- [ ] Replay timeout enforced (30s default)
- [ ] Idempotent: duplicate replay requests don't create chaos
- [ ] Git commit: `feat(replay): pipeline, safety policies, Docker sandbox, comparison`

---

## 8. Phase 5 — Test Generator

> **Goal**: Generate a compilable JUnit 5 test from a successful replay  
> **Duration**: 1.5–2.5 weeks  
> **Deliverable**: Click "CREATE TEST" → get a `.java` test file

### 8.1 Test Generator Package Structure

```
aftermath-testgen/src/main/java/dev/aftermath/testgen/
├── TestGeneratorService.java            # Orchestrates test generation
├── generator/
│   ├── TestGenerator.java               # Interface for generators
│   ├── JUnit5TestGenerator.java         # JUnit 5 + MockMvc generator
│   ├── JUnit5IntegrationGenerator.java  # JUnit 5 + Testcontainers generator
│   └── TestGeneratorFactory.java        # Select generator by framework
├── template/
│   ├── TemplateEngine.java              # FreeMarker/Mustache wrapper
│   ├── TemplateContext.java             # Data passed into templates
│   └── TemplateUtils.java              # Helper methods for templates
├── model/
│   ├── TestGenerationRequest.java       # Input: incident + replay + options
│   ├── TestGenerationResult.java        # Output: generated code + metadata
│   └── TestFramework.java              # Enum: JUNIT5_UNIT, JUNIT5_INTEGRATION
└── naming/
    ├── TestClassNamer.java              # Generate class name from incident
    └── TestMethodNamer.java             # Generate method name from error
```

### 8.2 Templates Directory

```
aftermath-testgen/src/main/resources/templates/
├── junit5-unit-test.ftl                 # Unit test with MockMvc
├── junit5-integration-test.ftl          # Integration test with Testcontainers
└── junit5-test-base.ftl                 # Common imports and annotations
```

### 8.3 Template Design: JUnit 5 Unit Test

The generator should produce output like this:

```java
package com.example.payment.regression;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regression test auto-generated by Aftermath.
 *
 * Incident:    INC-104
 * Service:     payment-service
 * Endpoint:    POST /api/payments
 * Error:       NullPointerException
 * Captured at: 2026-08-25T14:03:00Z
 * Replay:      MATCH (confirmed reproducible)
 *
 * This test reproduces the exact failure captured in production
 * to guard against regression.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PaymentServiceINC104ReproductionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("INC-104: POST /api/payments returns 500 (NullPointerException)")
    void shouldReproduceINC104_NullPointerException() throws Exception {
        String requestBody = """
            {
                "amount": 100.00,
                "couponCode": "PREMIUM50",
                "customerId": "test-customer-42"
            }
            """;

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isInternalServerError());
    }
}
```

### 8.4 Test Generation Steps

```
1. Receive: incident ID + framework choice (unit / integration)
2. Load incident from DB (request, error, replay result)
3. Generate class name:
     "PaymentServiceINC104ReproductionTest"
     Pattern: {ServiceName}{IncidentId}ReproductionTest
4. Generate method name:
     "shouldReproduceINC104_NullPointerException"
     Pattern: shouldReproduce{IncidentId}_{ErrorType}
5. Build template context:
     - method, path, headers (safe ones only), body
     - expected status code
     - exception type
     - incident metadata for Javadoc
6. Render template → Java source string
7. Store in DB as TestArtifact
8. Return to UI for display + copy/download
```

### 8.5 Wire into UI

#### 8.5.1 API Endpoint

```
POST /api/v1/incidents/{id}/tests
Body: { "framework": "junit5" }
Response: { "id": "...", "fileName": "...Test.java", "content": "..." }
```

#### 8.5.2 UI: Enable CREATE TEST Button

- Click CREATE TEST → choose unit/integration test
- Show generated Java code in a syntax-highlighted CodeBlock
- Buttons: "Copy to Clipboard" | "Download .java file"
- Show test metadata: incident ID, framework, file name

### 8.6 Phase 5 Checklist

- [ ] JUnit 5 unit test generated from incident + replay
- [ ] Generated test is syntactically valid Java
- [ ] Generated test contains incident metadata in Javadoc
- [ ] Class name and method name follow Java conventions
- [ ] Sensitive data is NOT present in generated tests
- [ ] Test artifact stored in database
- [ ] UI shows generated code with syntax highlighting
- [ ] Copy-to-clipboard and download work
- [ ] Generated test compiles against sample project
- [ ] Generated test reproduces the original failure when run
- [ ] Git commit: `feat(testgen): JUnit 5 generator, templates, UI integration`

---

## 9. Phase 6 — CLI Tool

> **Goal**: Command-line interface for developers who prefer terminal over browser  
> **Duration**: 1–2 weeks  
> **Deliverable**: `after` CLI with core commands

### 9.1 CLI Module Setup

Add Picocli dependency:

```xml
<dependency>
    <groupId>info.picocli</groupId>
    <artifactId>picocli-spring-boot-starter</artifactId>
    <version>4.7.6</version>
</dependency>
```

### 9.2 CLI Package Structure

```
aftermath-cli/src/main/java/dev/aftermath/cli/
├── AftermathCli.java                    # Main @Command entry point
├── commands/
│   ├── IncidentsCommand.java            # `after incidents` — list incidents
│   ├── OpenCommand.java                 # `after open INC-104` — view detail
│   ├── ReplayCommand.java              # `after replay INC-104` — trigger replay
│   ├── TestCommand.java                # `after test INC-104` — generate test
│   ├── ExportCommand.java              # `after export INC-104 --format json`
│   ├── SetupCommand.java               # `after setup` — configure collector URL
│   └── StatusCommand.java              # `after status` — check collector health
├── output/
│   ├── TableFormatter.java             # Format output as ASCII table
│   ├── JsonFormatter.java              # Format output as JSON
│   └── ColorPrinter.java               # ANSI color output
└── config/
    └── CliConfig.java                   # Read/write ~/.aftermath/config.yml
```

### 9.3 CLI Commands Reference

```bash
# List all open incidents
after incidents
after incidents --status OPEN --service payment-service

# View incident detail
after open INC-104

# Trigger replay
after replay INC-104
after replay INC-104 --target http://localhost:8080

# Generate test
after test INC-104
after test INC-104 --framework junit5 --output ./src/test/java/

# Export incident
after export INC-104 --format json
after export INC-104 --format curl

# Check system health
after status

# Initial setup
after setup --collector-url http://localhost:8090
```

### 9.4 CLI Output Example

```
$ after incidents

  AFTERMATH — Open Incidents

  ID       Method  Path              Status  Time         Service
  ──────── ─────── ───────────────── ─────── ──────────── ───────────────
  INC-104  POST    /api/payments     500     14:03 today  payment-service
  INC-103  GET     /api/orders       503     13:52 today  order-service
  INC-102  POST    /api/coupons      500     12:41 today  coupon-service

  3 open incidents | after open <ID> for details
```

### 9.5 Build Native Executable (Optional)

Use GraalVM Native Image for instant startup:

```bash
mvn -Pnative native:compile
# Produces: target/after.exe (Windows) or target/after (Linux/Mac)
```

### 9.6 Phase 6 Checklist

- [ ] `after incidents` lists incidents with formatting
- [ ] `after open <ID>` shows full incident detail
- [ ] `after replay <ID>` triggers replay from terminal
- [ ] `after test <ID>` generates and displays JUnit test
- [ ] `after export <ID>` outputs JSON or cURL
- [ ] `after status` checks collector connectivity
- [ ] Colored output for status codes (red=5xx, yellow=4xx)
- [ ] Error messages are helpful, not cryptic
- [ ] Git commit: `feat(cli): command-line interface with Picocli`

---

## 10. Phase 7 — Integration, Polish & Hardening

> **Goal**: Connect all components, handle edge cases, harden for real use  
> **Duration**: 2–3 weeks  
> **Deliverable**: Stable, polished MVP

### 10.1 Full-Stack Docker Compose

```yaml
# docker-compose.yml (root)
version: '3.8'

services:
  # The Aftermath platform
  aftermath-collector:
    build: ./aftermath-collector
    ports:
      - "8090:8090"
    volumes:
      - aftermath-data:/data
    environment:
      SPRING_DATASOURCE_URL: jdbc:sqlite:/data/aftermath.db

  aftermath-ui:
    build: ./aftermath-ui
    ports:
      - "5173:80"
    depends_on:
      - aftermath-collector

  # The sample application (for demos)
  coupon-service:
    build: ./sample-app/coupon-service
    ports:
      - "8081:8081"

  payment-service:
    build: ./sample-app/payment-service
    ports:
      - "8080:8080"
    environment:
      COUPON_SERVICE_BASE_URL: http://coupon-service:8081
      AFTERMATH_COLLECTOR_URL: http://aftermath-collector:8090
    depends_on:
      - coupon-service
      - aftermath-collector

volumes:
  aftermath-data:
```

### 10.2 One-Command Startup

```bash
# The dream: entire platform + sample app in one command
docker-compose up --build

# Then trigger the demo failure:
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{"amount": 100, "couponCode": "PREMIUM50", "customerId": "demo"}'

# Open browser: http://localhost:5173
# See the incident, click REPRODUCE, click CREATE TEST
```

### 10.3 Error Handling Hardening

| Scenario | Expected Behavior |
|:---------|:-----------------|
| Collector is down when SDK captures | SDK logs warning, app continues |
| SQLite file is locked | Collector retries with backoff, returns 503 |
| Docker not running for replay | Clear error: "Docker is not available. Install Docker or use localhost replay." |
| Replay target is unreachable | Replay status = FAILED with clear error message |
| Generated test has compilation error | Show warning + offer raw code for manual fix |
| User clicks REPRODUCE twice rapidly | Idempotent — same replay job, no duplicates |
| Very large request body (>1MB) | Truncated with notice: "Body truncated at 64KB" |
| Malformed JSON body | Stored as raw string, redaction still applied |

### 10.4 Performance Verification

| Metric | Target | How to Measure |
|:-------|:-------|:---------------|
| Capture overhead | < 2ms per request (p99) | JMH microbenchmark on CaptureFilter |
| Collector API latency | < 50ms for incident creation | Spring Boot actuator metrics |
| UI initial load | < 2 seconds | Lighthouse / browser DevTools |
| Replay execution | < 10 seconds (p95) | Timer in ReplayPipeline |

### 10.5 Phase 7 Checklist

- [ ] Full stack starts with single `docker-compose up`
- [ ] End-to-end demo works in under 5 minutes
- [ ] All error scenarios handled gracefully
- [ ] No leaked secrets in any stored data
- [ ] Performance targets met
- [ ] All tests pass: `mvn test` from root
- [ ] Git commit: `feat: full stack integration, error handling, performance`

---

## 11. Phase 8 — Documentation & Release

> **Goal**: Make the project presentable and usable by anyone  
> **Duration**: 1–2 weeks

### 11.1 README.md

Structure:
```markdown
# Aftermath
> Every production failure leaves a test behind.

## What is Aftermath?
[One paragraph explanation]

## Quick Start (5 minutes)
[Step-by-step with commands]

## Screenshot / Demo GIF
[Embedded image or link to video]

## How It Works
[Diagram: Capture → Replay → Test]

## Features
- [Bullet list]

## Installation
[For adding to your own Spring Boot app]

## Configuration
[Key configuration options]

## Contributing
[How to contribute]

## License
[License info]
```

### 11.2 Demo Video / GIF

Record a 2-minute screencast:
1. Start docker-compose
2. Trigger the failure with cURL
3. Open browser → incident appears
4. Click REPRODUCE → shows MATCH
5. Click CREATE TEST → generated JUnit test
6. Copy test → paste into IDE → run → test fails (bug exists)
7. Fix the bug → run test → test passes ✅

### 11.3 License Selection

| License | Best For |
|:--------|:---------|
| **MIT** | Maximum adoption, easy for companies to use |
| **Apache 2.0** | Good for enterprise, includes patent grant |
| **AGPL 3.0** | Forces open-source for hosted versions (protects your commercial path) |

Recommendation: **Apache 2.0** — balances openness with enterprise trust.

### 11.4 Phase 8 Checklist

- [ ] README.md is complete, clear, and professional
- [ ] Demo GIF/video embedded or linked
- [ ] CONTRIBUTING.md with setup instructions
- [ ] LICENSE file present
- [ ] CHANGELOG.md with v0.1.0 entry
- [ ] All code has Javadoc on public classes/methods
- [ ] API documented (OpenAPI/Swagger or manual)
- [ ] Git tag: `v0.1.0`

---

## 12. Phase 9 — Post-MVP Enhancements

> **Priority order for features after MVP launch**

### 12.1 Tier 1 — High Impact, Low Effort

| Feature | Effort | Value |
|:--------|:-------|:------|
| 12.1.1 Export as cURL command | 1 day | Instant usability boost |
| 12.1.2 Incident deduplication grouping | 2–3 days | Reduces noise dramatically |
| 12.1.3 Desktop notification on new incident | 1–2 days | Keeps dev aware |
| 12.1.4 Dark mode for UI | 1 day | Developers expect this |

### 12.2 Tier 2 — High Impact, Medium Effort

| Feature | Effort | Value |
|:--------|:-------|:------|
| 12.2.1 IntelliJ IDEA plugin | 1–2 weeks | Keeps developers in their IDE |
| 12.2.2 Auto-mock generation (WireMock stubs) | 1 week | Makes replay more deterministic |
| 12.2.3 Incident analytics dashboard | 1 week | "Top 5 failing endpoints" |
| 12.2.4 GitHub PR integration | 1 week | Auto-create PR with generated test |
| 12.2.5 Response body diff viewer | 3–5 days | Visual diff between original and replay |

### 12.3 Tier 3 — Medium Impact, High Effort

| Feature | Effort | Value |
|:--------|:-------|:------|
| 12.3.1 Node.js / Express SDK | 2–3 weeks | Expands addressable market |
| 12.3.2 Python / FastAPI SDK | 2–3 weeks | Expands addressable market |
| 12.3.3 AI-powered root cause analysis | 2–3 weeks | Impressive but not critical |
| 12.3.4 Similar incident search (embeddings) | 2 weeks | "Have we seen this before?" |
| 12.3.5 Team mode with authentication | 3–4 weeks | Required for commercial path |
| 12.3.6 Kubernetes sidecar deployment | 2–3 weeks | Enterprise deployment |

---

## 13. Appendix A — Technology Reference

### 13.1 Key Dependencies & Versions

| Library | Version | Module | Purpose |
|:--------|:--------|:-------|:--------|
| Spring Boot | 3.3.x | All Java | Application framework |
| Spring Data JPA | 3.3.x | Collector | Database access |
| SQLite JDBC | 3.45.x | Collector | Local database driver |
| Flyway | 10.x | Collector | Database migrations |
| Jackson | 2.17.x | SDK, Collector | JSON processing |
| WebClient | 6.1.x | Replay | HTTP client for replay |
| Testcontainers | 1.19.x | Replay | Docker sandbox |
| FreeMarker | 2.3.x | TestGen | Test templates |
| Picocli | 4.7.x | CLI | Command-line framework |
| React | 18.x | UI | Frontend framework |
| Vite | 5.x | UI | Build tool |
| TanStack Query | 5.x | UI | Data fetching |
| Tailwind CSS | 3.x | UI | Styling |
| Axios | 1.x | UI | HTTP client |

### 13.2 Port Allocation

| Port | Service |
|:-----|:--------|
| 8080 | Sample payment-service |
| 8081 | Sample coupon-service |
| 8090 | Aftermath Collector |
| 5173 | Aftermath UI (Vite dev) |

---

## 14. Appendix B — Coding Standards

### 14.1 Java Standards

- **Package naming**: `dev.aftermath.{module}.{layer}`
- **Class naming**: PascalCase, suffixed by role (`*Service`, `*Controller`, `*Repository`)
- **Method naming**: camelCase, verb-first (`createIncident`, `validateRequest`)
- **Constants**: UPPER_SNAKE_CASE
- **Null handling**: Use `Optional<T>` for return types, never return null
- **Logging**: SLF4J with structured fields: `log.info("Incident captured", kv("incidentId", id))`
- **Error handling**: Custom exceptions extending `RuntimeException`, caught by `@ControllerAdvice`

### 14.2 React/TypeScript Standards

- **Components**: PascalCase functional components with TypeScript props interface
- **Files**: PascalCase for components (`IncidentTable.tsx`), camelCase for utilities
- **State management**: React Query for server state, React context for UI state
- **Styling**: Tailwind utility classes, no inline styles
- **Types**: Strict TypeScript, no `any`

### 14.3 Testing Standards

- **Test naming**: `should{ExpectedBehavior}_when{Condition}` or `@DisplayName`
- **Test structure**: Arrange → Act → Assert (AAA pattern)
- **Coverage target**: 80%+ for business logic, 60%+ overall
- **Test data**: Builders or factory methods, never hardcoded across multiple tests

---

## 15. Appendix C — Git Workflow

### 15.1 Branch Strategy

```
main                    ← Stable releases only
  └── develop           ← Integration branch
       ├── feature/sdk-capture-filter
       ├── feature/collector-api
       ├── feature/ui-incident-list
       ├── feature/replay-engine
       └── fix/redaction-jwt-edge-case
```

### 15.2 Commit Message Convention

```
type(scope): short description

Types: feat, fix, refactor, test, docs, chore, ci
Scope: sdk, collector, ui, replay, testgen, cli, sample

Examples:
  feat(sdk): add capture filter for 4xx/5xx responses
  fix(redaction): handle JWT in custom X-Auth header
  test(replay): add safety policy integration tests
  docs: update README with quick start guide
```

### 15.3 Release Tagging

```bash
git tag -a v0.1.0 -m "MVP: Capture + Replay + Test Generation"
git push origin v0.1.0
```

---

> **"A production bug should never have to be rediscovered by hand."**  
> — Aftermath

---

*End of Development Guide v1.0*
