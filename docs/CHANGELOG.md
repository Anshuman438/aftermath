# AFTERMATH Release Changelog

All notable changes to the AFTERMATH platform will be documented in this file.

---

## [v2.0.0] - 2026-08-26

### 🎉 100% Market Mastery & Enterprise Release
- **1-Click System Installers (`install.ps1` & `install.sh`)**: Global installation scripts for Windows PowerShell and Linux/macOS bash adding `aftermath` CLI to user PATH.
- **GitHub Action Integration (`aftermath-action`)**: GitHub Composite Action (`action.yml`) to automatically verify Pull Requests against captured reproduction tests.
- **OpenTelemetry Distributed Trace Context Propagation (`OpenTelemetryTraceInterceptor`)**: Extracts W3C `traceparent`, B3, Datadog `x-datadog-trace-id`, and SLF4J MDC trace context across microservices.
- **VS Code Extension Prototype (`aftermath-vscode`)**: VS Code extension manifest (`package.json`) and `extension.js` providing inline incident fetching and project attachment.
- **WireMock Auto-Mocking Test Generator (`JUNIT5_WIREMOCK`)**: Generates standalone WireMock stubs in `.java` test files for zero-dependency CI/CD execution.
- **Non-HTTP Background & Async Interceptor (`AftermathAspectInterceptor`)**: Spring `@Aspect` catching uncaught failures in `@Scheduled` and `@Async` tasks.
- **JVM System Context Snapshot (`SystemSnapshot`)**: Captures free memory, max memory, thread count, and CPU load at failure time.

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
