# AFTERMATH Release Changelog

All notable changes to the AFTERMATH platform will be documented in this file.

---

## [v1.2.1] - 2026-08-26

### 🛡️ Security & Reliability Fixes
- **XXE Injection Vulnerability Fix (BUG-001)**: Disabled DTDs and external entities in `PomXmlInjector` and `ProjectDetector` XML parsers.
- **SSRF Prevention Fix (BUG-002)**: Added target URL scheme and host validation in `ReplayExecutor` blocking AWS/GCP cloud metadata IPs (`169.254.169.254`).
- **Input Validation Fix (BUG-003)**: Added `@Valid` and Jakarta Validation (`@NotBlank`, `@Size`) to `CreateIncidentRequest` and `IncidentController`.
- **Database Hardening Fix (BUG-004)**: Disabled H2 Web Console in `application.yml` and configured secure database password.
- **Thread Safety Fix (BUG-005)**: Replaced raw `int` thread counter in `AsyncEventDispatcher` with `AtomicInteger`.
- **Database Storage Capacity Fix (BUG-007)**: Expanded `stackTrace` and `rawJson` database columns in `IncidentEntity` to 65,535 characters.
- **Global Error Handling (BUG-009)**: Created `@RestControllerAdvice` `GlobalExceptionHandler` for structured HTTP 400/404/500 error responses.

---

## [v1.2.0] - 2026-08-26

### 🚀 Added
- **SHA-256 Incident Deduplication Engine**: `fingerprint`, `occurrenceCount`, `lastSeenAt`, and Web UI occurrence badge (`2x`, `3x`).
- **cURL Command Exporter**: `aftermath curl <incident-id>` command to export request capsules as runnable cURL scripts.
- **AI-Powered Root Cause Analysis**: `aftermath analyze <incident-id>` command outputting failing class, line number, root cause summary, recommended fix, and suggested Git code diffs.

---

## [v1.1.0] - 2026-08-26

### 🚀 Added
- **Zero-Touch Developer Auto-Attacher (`aftermath attach`)**: Picocli subcommand auto-detecting Maven/Gradle and auto-injecting `aftermath-sdk` dependency and `application.yml` config.

---

## [v1.0.0] - 2026-08-26

### 🎉 Initial MVP Release
- `aftermath-sdk`, `aftermath-collector`, `aftermath-replay`, `aftermath-testgen`, `aftermath-ui`, `aftermath-cli`, `sample-app`.
