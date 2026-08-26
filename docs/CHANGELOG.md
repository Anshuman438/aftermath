# AFTERMATH Release Changelog

All notable changes to the AFTERMATH platform will be documented in this file.

---

## [v3.0.0] - 2026-08-26

### 🚀 Polyglot & Automation Engine Release
- **Polyglot Node.js / Express SDK (`aftermath-sdk-node`)**:
  - NPM package module providing Express error interceptor middleware (`aftermathExpressMiddleware`).
  - Automatic JS PII & Bearer token redactor.
  - Fail-open non-blocking HTTP event transport.
- **Automated Git PR Bot Payload Generator (`GitPrBotService`)**:
  - Automatically generates branch names (`aftermath/fix-incident-xxx`), pull request title, rich markdown description, generated test code, and recommended code fix diff.
- **OpenAPI / Pact Contract Test Generator (`ContractTestGenerator`)**:
  - Generates REST-Assured JSON schema contract validation tests ensuring API contract compliance when schema drift occurs.
- **Automation REST Controller (`AutomationController`)**:
  - Exposed `/api/v1/incidents/{id}/git-pr-payload` and `/api/v1/incidents/{id}/contract-test`.

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
