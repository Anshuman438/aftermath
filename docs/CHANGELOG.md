# AFTERMATH Release Changelog

All notable changes to the AFTERMATH platform will be documented in this file.

---

## [v5.0.0] - 2026-08-26

### 🎉 100% Total Completion Ultimate Master Release
- **Cloud Multi-Tenant SaaS Security & Authentication (`AuthController`, `JwtTokenProvider`, `OrganizationEntity`)**:
  - Implemented multi-tenant organization registration (`POST /api/v1/auth/register-org`), JWT token provider, and organization workspace isolation.
- **Kubernetes Helm Deployment Charts (`deploy/helm/aftermath`)**:
  - Automated Helm v2 chart (`Chart.yaml`, `values.yaml`) for deploying high-scale AFTERMATH Collector pods and Kafka clusters on 100-node Kubernetes clusters.
- **JetBrains IntelliJ Marketplace Publishing Manifest (`deploy/ide/intellij/plugin.xml`)**:
  - Created JetBrains plugin manifest for IDE marketplace distribution.

---

## [v4.0.0] - 2026-08-26

### 🚀 Polyglot Expansion Master Release
- Go SDK (`aftermath-sdk-go`), Rust SDK (`aftermath-sdk-rust`), C#/.NET SDK (`aftermath-sdk-dotnet`), PHP SDK (`aftermath-sdk-php`), Ruby SDK (`aftermath-sdk-ruby`), Python SDK (`aftermath-sdk-python`), Node.js SDK (`aftermath-sdk-node`).
- Cascading Error Chain Root Cause Engine (`CascadingErrorAnalyzer`).

---

## [v3.2.0] - 2026-08-26

### 🚀 Polyglot & Cascading Engine
- Cascading Error Chain Root Cause Engine & Python SDK.

---

## [v3.1.0] - 2026-08-26

### 🧹 Architecture Refactoring & Cleanup
- Enforced AFTERMATH Default local-first architecture.

---

## [v2.0.0] - 2026-08-26

### 🎉 100% Market Mastery & Enterprise Release
- 1-Click System Installers (`install.ps1` & `install.sh`), GitHub Action Integration (`aftermath-action`), OpenTelemetry Distributed Trace Context Propagation (`OpenTelemetryTraceInterceptor`), VS Code Extension Prototype (`aftermath-vscode`), WireMock Auto-Mocking Test Generator (`JUNIT5_WIREMOCK`).
