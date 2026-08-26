# AFTERMATH Release Changelog

All notable changes to the AFTERMATH platform will be documented in this file.

---

## [v6.1.0] - 2026-08-26

### 🧠 Co-Founder Failure Audit & System Hardening
- **Co-Founder Failure Analysis Master Report (`cofounder_report.md`)**:
  - Published 12-point failure scenario matrix addressing memory pressure, queue exhaustion, thread safety, SSRF, XXE, data retention, and cascading error chains.
- **Enhanced PII Regex Redactor (`PatternRegistry`)**:
  - Hardened `SENSITIVE_JSON_KEY_PATTERN` regex to redact numeric PINs, CVVs, booleans, and string keys.
- **Codebase Integrity & Clean Compilation**:
  - Audited all source files, removed unused imports, and verified clean compilation across all 8 Java modules.

---

## [v6.0.0] - 2026-08-26

### 🚀 Tiered Data Retention & Cold Compliance Engine
- Auto-Pruning TTL Data Retention Service (`DataRetentionService`) and Cold Compliance GZIP Archive Exporter (`S3ArchiveExporter`).

---

## [v5.0.0] - 2026-08-26

### 🎉 100% Total Completion Ultimate Master Release
- Cloud Multi-Tenant SaaS Auth (`AuthController`, `JwtTokenProvider`), Kubernetes Helm Charts (`deploy/helm/aftermath`), JetBrains IntelliJ Manifest (`plugin.xml`).

---

## [v4.0.0] - 2026-08-26

### 🚀 Polyglot Expansion Master Release
- Go, Rust, C#, PHP, Ruby, Python, Node.js SDKs & Cascading Error Engine.
