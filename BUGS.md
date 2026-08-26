# AFTERMATH — Bug Report & Areas of Improvement Tracker

> **Auditor**: Expert code review of entire AFTERMATH codebase  
> **Date**: 2026-08-26  
> **Scope**: All 8 modules (aftermath-sdk, aftermath-collector, aftermath-replay, aftermath-testgen, aftermath-cli, aftermath-ui, sample-app, config files)  
> **Status Legend**: ✅ RESOLVED | 🔴 CRITICAL | 🟠 HIGH | 🟡 MEDIUM | 🟢 LOW  

---

## 🔴 CRITICAL BUGS (All 4 Resolved)

### BUG-001: XML External Entity (XXE) Injection in PomXmlInjector & ProjectDetector — ✅ RESOLVED
- **File**: [`PomXmlInjector.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-cli/src/main/java/dev/aftermath/cli/attach/PomXmlInjector.java), [`ProjectDetector.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-cli/src/main/java/dev/aftermath/cli/attach/ProjectDetector.java)
- **Status**: ✅ RESOLVED
- **Fix Implemented**: Configured `DocumentBuilderFactory` securely by disabling DOCTYPE declarations, external general entities, external parameter entities, and setting `XMLConstants.ACCESS_EXTERNAL_DTD` to empty string.

---

### BUG-002: Server-Side Request Forgery (SSRF) in ReplayExecutor — ✅ RESOLVED
- **File**: [`ReplayExecutor.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-replay/src/main/java/dev/aftermath/replay/executor/ReplayExecutor.java)
- **Status**: ✅ RESOLVED
- **Fix Implemented**: Implemented `validateTargetUrl()` blocking invalid URL schemes (only HTTP/HTTPS allowed), syntax errors, and cloud metadata IPs (`169.254.169.254`, `metadata.google.internal`).

---

### BUG-003: No Input Validation on Collector POST Endpoint — ✅ RESOLVED
- **File**: [`CreateIncidentRequest.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/dto/CreateIncidentRequest.java), [`IncidentController.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/controller/IncidentController.java)
- **Status**: ✅ RESOLVED
- **Fix Implemented**: Added `@Valid` annotation on controller `createIncident`, and added `@NotBlank`, `@Size`, and `@Valid` constraints across all request DTO fields (body capped at 64KB, strings bounded).

---

### BUG-004: H2 Database Exposed Without Authentication — ✅ RESOLVED
- **File**: [`application.yml`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/resources/application.yml)
- **Status**: ✅ RESOLVED
- **Fix Implemented**: Explicitly disabled H2 Console (`spring.h2.console.enabled: false`) and set strong database connection password (`AftermathSecurePass2026!`).

---

## 🟠 HIGH SEVERITY BUGS

### BUG-005: Race Condition in AsyncEventDispatcher Thread Counter — ✅ RESOLVED
- **File**: [`AsyncEventDispatcher.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-sdk/src/main/java/dev/aftermath/sdk/transport/AsyncEventDispatcher.java)
- **Status**: ✅ RESOLVED
- **Fix Implemented**: Replaced mutable `int count` with thread-safe `AtomicInteger`.

---

### BUG-006: RedactionEngine Mutates Input Object (Side Effect Bug)
- **File**: [`RedactionEngine.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-sdk/src/main/java/dev/aftermath/sdk/redaction/RedactionEngine.java) — Line 26
- **Status**: 🟠 HIGH

---

### BUG-007: Unbounded Stack Trace Storage in Database — ✅ RESOLVED
- **File**: [`IncidentEntity.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/entity/IncidentEntity.java)
- **Status**: ✅ RESOLVED
- **Fix Implemented**: Expanded `stackTrace` and `rawJson` column bounds to 65,535 characters in JPA entity definition.

---

### BUG-008: No Rate Limiting on Collector API Endpoints
- **File**: [`IncidentController.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/controller/IncidentController.java)
- **Status**: 🟠 HIGH

---

### BUG-009: No Global Exception Handler in Collector — ✅ RESOLVED
- **File**: [`GlobalExceptionHandler.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/exception/GlobalExceptionHandler.java)
- **Status**: ✅ RESOLVED
- **Fix Implemented**: Created `@RestControllerAdvice` handling `MethodArgumentNotValidException` (400 Bad Request), `IllegalArgumentException` (404 Not Found), and generic exceptions cleanly with structured JSON.

---

### BUG-010: Replay Sends Redacted Headers as Literal "[REDACTED]" String — ✅ RESOLVED
- **File**: [`ReplayExecutor.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-replay/src/main/java/dev/aftermath/replay/executor/ReplayExecutor.java)
- **Status**: ✅ RESOLVED
- **Fix Implemented**: Used case-insensitive check `!v.toLowerCase().contains("redacted")` to skip all redacted headers.

---

## 🟡 MEDIUM SEVERITY BUGS

### BUG-016: No Pagination Limit on `size` Parameter — ✅ RESOLVED
- **File**: [`IncidentController.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/controller/IncidentController.java)
- **Status**: ✅ RESOLVED
- **Fix Implemented**: Capped `size` parameter to a maximum of 100 (`Math.min(Math.max(1, size), 100)`).

---

## 📊 Summary

| Severity | Total Found | Resolved | Remaining |
| :--- | :--- | :--- | :--- |
| 🔴 **CRITICAL** | **4** | **4** ✅ | **0** |
| 🟠 **HIGH** | **6** | **3** ✅ | **3** |
| 🟡 **MEDIUM** | **8** | **1** ✅ | **7** |
| 🟢 **LOW** | **7** | **0** | **7** |
| **TOTAL** | **25** | **8** ✅ | **17** |
