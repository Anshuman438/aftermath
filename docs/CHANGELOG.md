# AFTERMATH Release Changelog

All notable changes to the AFTERMATH platform will be documented in this file.

---

## [v3.2.0] - 2026-08-26

### 🚀 Polyglot Expansion & Cascading Error Root Cause Engine
- **Cascading Error Chain Root Cause Engine (`CascadingErrorAnalyzer`)**:
  - Automatically unwraps nested `Throwable` cause chains (`Caused by:...`) to isolate the **Primary Root Cause Culprit** (the first domino that fell) during cascading failures across microservices.
  - Exposed via REST endpoint `GET /api/v1/incidents/{incidentId}/cascading-analysis`.
- **Polyglot Python SDK (`aftermath-sdk-python`)**:
  - Built Python package module (`aftermath`) for FastAPI, Flask, and Django exception interception, PII redaction, and non-blocking background transport.
- **Polyglot Node.js / Express SDK (`aftermath-sdk-node`)**:
  - Express error interceptor middleware (`aftermathExpressMiddleware`) with PII redaction.

---

## [v3.1.0] - 2026-08-26

### 🧹 Architecture Refactoring & Cleanup
- Enforced AFTERMATH Default local-first architecture (removed automatic Git PR pushing code to maintain zero-fuss developer experience).

---

## [v2.0.0] - 2026-08-26

### 🎉 100% Market Mastery & Enterprise Release
- **1-Click System Installers (`install.ps1` & `install.sh`)**
- **GitHub Action Integration (`aftermath-action`)**
- **OpenTelemetry Distributed Trace Context Propagation (`OpenTelemetryTraceInterceptor`)**
- **VS Code Extension Prototype (`aftermath-vscode`)**
- **WireMock Auto-Mocking Test Generator (`JUNIT5_WIREMOCK`)**
- **Non-HTTP Background & Async Interceptor (`AftermathAspectInterceptor`)**
- **JVM System Context Snapshot (`SystemSnapshot`)**

---

## [v1.3.0] - 2026-08-26

### 🚀 Enterprise Features
- **WireMock Auto-Mocking Test Generator (`JUNIT5_WIREMOCK`)**
- **Non-HTTP Background & Async Exception Interceptor**
- **JVM System Context & Health Snapshot**

---

## [v1.2.1] - 2026-08-26

### 🛡️ Security & Reliability Fixes
- **XXE Injection Vulnerability Fix (BUG-001)**
- **SSRF Prevention Fix (BUG-002)**
- **Input Validation Fix (BUG-003)**
- **Database Hardening Fix (BUG-004)**

---

## [v1.2.0] - 2026-08-26

### 🚀 Added
- **SHA-256 Incident Deduplication Engine**
- **cURL Command Exporter**
- **AI-Powered Root Cause Analysis**

---

## [v1.1.0] - 2026-08-26

### 🚀 Added
- **Zero-Touch Developer Auto-Attacher (`aftermath attach`)**

---

## [v1.0.0] - 2026-08-26

### 🎉 Initial MVP Release
- `aftermath-sdk`, `aftermath-collector`, `aftermath-replay`, `aftermath-testgen`, `aftermath-ui`, `aftermath-cli`, `sample-app`.
