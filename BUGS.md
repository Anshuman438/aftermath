# AFTERMATH — Bug Report & Areas of Improvement Tracker

> **Auditor**: Expert code review of entire AFTERMATH codebase  
> **Date**: 2026-08-26  
> **Scope**: All 8 modules (aftermath-sdk, aftermath-collector, aftermath-replay, aftermath-testgen, aftermath-cli, aftermath-ui, sample-app, config files)  
> **Severity Legend**: 🔴 CRITICAL | 🟠 HIGH | 🟡 MEDIUM | 🟢 LOW  

---

## 🔴 CRITICAL BUGS (Must Fix Immediately)

### BUG-001: XML External Entity (XXE) Injection in PomXmlInjector
- **File**: [`PomXmlInjector.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-cli/src/main/java/dev/aftermath/cli/attach/PomXmlInjector.java) — Line 24
- **Severity**: 🔴 CRITICAL
- **Description**: `DocumentBuilderFactory` is created without disabling external entity processing. A maliciously crafted `pom.xml` could exploit XXE to read arbitrary files from the developer's machine or perform SSRF attacks.
- **Current Code**:
  ```java
  DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
  DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
  ```
- **Fix**: Disable external entities:
  ```java
  dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
  dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
  dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
  ```

---

### BUG-002: Server-Side Request Forgery (SSRF) in ReplayExecutor
- **File**: [`ReplayExecutor.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-replay/src/main/java/dev/aftermath/replay/executor/ReplayExecutor.java) — Line 57
- **Severity**: 🔴 CRITICAL
- **Description**: The Replay Engine accepts any `targetBaseUrl` from the user (via API or CLI) and issues HTTP requests to it without validation. An attacker could use this to probe internal network services (`http://169.254.169.254/` AWS metadata, `http://localhost:xxxx`, internal IPs).
- **Fix**: Validate and whitelist allowed target URLs. At minimum block private IP ranges and cloud metadata endpoints.

---

### BUG-003: No Input Validation on Collector POST Endpoint
- **File**: [`IncidentController.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/controller/IncidentController.java) — Line 20
- **Severity**: 🔴 CRITICAL
- **Description**: The `POST /api/v1/incidents` endpoint accepts any `@RequestBody` with zero validation. No `@Valid`, no size limits, no field validation. An attacker can send unlimited-size JSON payloads to cause OOM, or inject arbitrary data into the database.
- **Fix**: Add `@Valid` annotation, add `@Size`, `@NotBlank` constraints on `CreateIncidentRequest` fields, and configure `spring.servlet.multipart.max-request-size`.

---

### BUG-004: H2 Database Exposed Without Authentication
- **File**: [`application.yml`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/resources/application.yml) — Lines 10-11
- **Severity**: 🔴 CRITICAL
- **Description**: The H2 database is configured with `username: sa` and empty password. If the H2 console is enabled (Spring Boot default can expose it), the entire database is accessible without authentication.
- **Fix**: Set a strong password, explicitly disable H2 console: `spring.h2.console.enabled: false`.

---

## 🟠 HIGH SEVERITY BUGS

### BUG-005: Race Condition in AsyncEventDispatcher Thread Counter
- **File**: [`AsyncEventDispatcher.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-sdk/src/main/java/dev/aftermath/sdk/transport/AsyncEventDispatcher.java) — Line 23
- **Severity**: 🟠 HIGH
- **Description**: The `count` variable in the `ThreadFactory` is not thread-safe. Multiple threads calling `newThread()` concurrently will cause a data race on the `++count` operation.
- **Current Code**: `private int count = 0;`
- **Fix**: Use `AtomicInteger`:
  ```java
  private final AtomicInteger count = new AtomicInteger(0);
  // Then: "aftermath-dispatcher-" + count.incrementAndGet()
  ```

---

### BUG-006: RedactionEngine Mutates Input Object (Side Effect Bug)
- **File**: [`RedactionEngine.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-sdk/src/main/java/dev/aftermath/sdk/redaction/RedactionEngine.java) — Line 26
- **Severity**: 🟠 HIGH
- **Description**: `redactRequest()` modifies the original `RequestSnapshot` object's headers and body in-place via `setHeaders()` and `setBody()`. This mutates the original request object that the downstream application might still need.
- **Fix**: Create a deep copy of `RequestSnapshot` before redacting, or return a new redacted instance.

---

### BUG-007: Unbounded Stack Trace Storage in Database
- **File**: [`IncidentEntity.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/entity/IncidentEntity.java) — Line 39
- **Severity**: 🟠 HIGH
- **Description**: `stackTrace` column is limited to `@Column(length = 10000)` but the actual Java stack trace from deeply nested frameworks (Spring, Hibernate, Tomcat) can easily exceed 10,000 characters. This causes silent data truncation and loss of debugging information.
- **Fix**: Increase to `@Column(length = 65000)` or use `@Lob` for unlimited TEXT storage. Also truncate on the application side before persisting.

---

### BUG-008: No Rate Limiting on Collector API Endpoints
- **File**: [`IncidentController.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/controller/IncidentController.java)
- **Severity**: 🟠 HIGH
- **Description**: All collector REST endpoints have zero rate limiting. A misbehaving SDK or attacker could flood the Collector with thousands of requests per second, exhausting disk, memory, and database connections.
- **Fix**: Add Spring Boot rate limiting via `bucket4j` or a simple in-memory token bucket filter.

---

### BUG-009: No Global Exception Handler in Collector
- **File**: `aftermath-collector` — Missing `@ControllerAdvice`
- **Severity**: 🟠 HIGH
- **Description**: When an `IllegalArgumentException` or `RuntimeException` is thrown (e.g., "Incident not found"), Spring Boot returns a raw HTML Whitelabel Error Page with internal server details. This leaks stack traces and internal paths to the client.
- **Fix**: Add `@ControllerAdvice` with `@ExceptionHandler` methods that return structured JSON error responses.

---

### BUG-010: Replay Sends Redacted Headers as Literal "[REDACTED]" String
- **File**: [`ReplayExecutor.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-replay/src/main/java/dev/aftermath/replay/executor/ReplayExecutor.java) — Line 87
- **Severity**: 🟠 HIGH
- **Description**: While the code skips headers with value `[REDACTED]`, the check is case-sensitive. If the redaction engine ever produces `[Redacted]` or `[redacted]`, those would be sent to the target service as literal header values, causing authentication failures and confusing debugging.
- **Fix**: Use case-insensitive check: `v.toLowerCase().contains("redacted")`

---

## 🟡 MEDIUM SEVERITY BUGS

### BUG-011: Memory Leak — Blob URL Not Revoked in UI Download
- **File**: [`IncidentDetailModal.jsx`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-ui/src/components/IncidentDetailModal.jsx) — Line 38
- **Severity**: 🟡 MEDIUM
- **Description**: `URL.createObjectURL(file)` creates a Blob URL that persists in browser memory until the page is unloaded. The code never calls `URL.revokeObjectURL()`, causing a memory leak with each file download.
- **Fix**: Add `URL.revokeObjectURL(element.href)` after `element.click()`.

---

### BUG-012: Hardcoded API Base URL in Frontend
- **File**: [`collectorClient.js`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-ui/src/api/collectorClient.js) — Line 1
- **Severity**: 🟡 MEDIUM
- **Description**: `const API_BASE = 'http://localhost:8090/api/v1'` is hardcoded. This breaks immediately when deploying to any non-localhost environment.
- **Fix**: Use Vite environment variable: `const API_BASE = import.meta.env.VITE_API_BASE || '/api/v1'` and configure the Vite proxy.

---

### BUG-013: Hardcoded Default Target URL in ReplayService
- **File**: [`ReplayService.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/service/ReplayService.java) — Line 69
- **Severity**: 🟡 MEDIUM
- **Description**: `"http://localhost:8082"` is hardcoded as the default replay target. This should be configurable via `application.yml`.
- **Fix**: Inject via `@Value("${aftermath.replay.default-target-url:http://localhost:8082}")`.

---

### BUG-014: AnalysisService Stack Trace Parser Skips Application Frames
- **File**: [`AnalysisService.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/service/AnalysisService.java) — Line 48
- **Severity**: 🟡 MEDIUM
- **Description**: The stack trace parser gives priority to `dev.aftermath.*` frames but the actual user's application (e.g., `dev.aftermath.sample.*`) is what matters. The filter also excludes *all* `org.springframework` frames, but some Spring frames (like `@RestController` handlers) are user-written code.
- **Fix**: Prioritize user application package frames and add configurable package prefix filtering.

---

### BUG-015: No Escape/Sanitization of Stack Trace in UI Rendering
- **File**: [`IncidentDetailModal.jsx`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-ui/src/components/IncidentDetailModal.jsx) — Line 198
- **Severity**: 🟡 MEDIUM
- **Description**: Stack traces are rendered directly into `<pre>` tags. While React auto-escapes JSX content, if raw HTML were ever injected into the stack trace field, it could be rendered unsafely in the Raw JSON tab via `JSON.stringify`.
- **Fix**: Ensure all dynamic content is rendered through React's JSX escape mechanism (which it currently is via `{}`), but add explicit sanitization for the raw JSON tab.

---

### BUG-016: No Pagination Limit on `size` Parameter
- **File**: [`IncidentController.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/java/dev/aftermath/collector/controller/IncidentController.java) — Line 28
- **Severity**: 🟡 MEDIUM
- **Description**: The `size` query parameter has no maximum limit. A request with `?size=999999` would attempt to load all incidents into memory at once, causing OOM.
- **Fix**: Cap `size` to a maximum of 100: `int effectiveSize = Math.min(size, 100);`

---

### BUG-017: HttpEventTransport Uses Blocking I/O on Async Thread
- **File**: [`HttpEventTransport.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-sdk/src/main/java/dev/aftermath/sdk/transport/HttpEventTransport.java) — Line 40
- **Severity**: 🟡 MEDIUM
- **Description**: `httpClient.send()` is a blocking call executed on the `AsyncEventDispatcher` thread pool. With only 2-4 threads and a 2-second timeout, if the Collector is slow, the thread pool saturates quickly, silently dropping all subsequent incidents.
- **Fix**: Use `httpClient.sendAsync()` for non-blocking I/O, or increase the thread pool size.

---

### BUG-018: YamlConfigInjector Appends Duplicate Config on Re-run Edge Case
- **File**: [`YamlConfigInjector.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-cli/src/main/java/dev/aftermath/cli/attach/YamlConfigInjector.java) — Line 43
- **Severity**: 🟡 MEDIUM
- **Description**: The deduplication check is `existingContent.contains("aftermath:")`. If the user has a YAML comment like `# aftermath: disabled`, the injector incorrectly thinks configuration is already present and skips injection.
- **Fix**: Use a more robust YAML-aware check, e.g., parse the YAML and check for the `aftermath.sdk.enabled` key programmatically.

---

## 🟢 LOW SEVERITY ISSUES

### BUG-019: No `Escape` Keyboard Shortcut to Close Modal
- **File**: [`IncidentDetailModal.jsx`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-ui/src/components/IncidentDetailModal.jsx)
- **Severity**: 🟢 LOW
- **Description**: The modal has no keyboard event listener for `Escape` key to close. Users must click the X button or Close button.
- **Fix**: Add `useEffect` with `keydown` event listener for `Escape`.

---

### BUG-020: No Loading State for CLI Commands
- **File**: All CLI command files (`ListCommand.java`, `ViewCommand.java`, etc.)
- **Severity**: 🟢 LOW
- **Description**: CLI commands print no "Loading..." or progress indicator while waiting for HTTP responses from the Collector. On slow networks, the CLI appears frozen.
- **Fix**: Add `System.out.println("Fetching data from Collector...")` before HTTP calls.

---

### BUG-021: `ddl-auto: update` in Production Config
- **File**: [`application.yml`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-collector/src/main/resources/application.yml) — Line 15
- **Severity**: 🟢 LOW
- **Description**: `hibernate.ddl-auto: update` automatically modifies the database schema on startup. In production, this can cause data loss or schema corruption. Should use `validate` in production and managed migrations (Flyway/Liquibase).

---

### BUG-022: RequestWrapper Doesn't Close Original InputStream
- **File**: [`RequestWrapper.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-sdk/src/main/java/dev/aftermath/sdk/capture/RequestWrapper.java) — Line 20
- **Severity**: 🟢 LOW
- **Description**: The original `request.getInputStream()` is read but never explicitly closed in a try-with-resources block. While the Servlet container typically manages this, it's a resource management best practice violation.

---

### BUG-023: CurlCommand Does Not Escape Special Characters in Body
- **File**: [`CurlCommand.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-cli/src/main/java/dev/aftermath/cli/command/CurlCommand.java) — Line 61
- **Severity**: 🟢 LOW
- **Description**: The body content is placed inside single quotes in the cURL output, but only single quotes are escaped. Other shell-special characters (`$`, `` ` ``, `!`) could cause issues when users paste the command into bash.

---

### BUG-024: No Auto-Refresh / Polling on Dashboard
- **File**: [`App.jsx`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-ui/src/App.jsx)
- **Severity**: 🟢 LOW
- **Description**: The dashboard only loads incidents on mount and manual refresh. In a real production scenario, new incidents should appear automatically via polling or WebSocket.
- **Fix**: Add `setInterval` polling every 10 seconds, or implement Server-Sent Events (SSE).

---

### BUG-025: ProjectDetector Returns First `<artifactId>` Which May Be Parent's
- **File**: [`ProjectDetector.java`](file:///c:/Users/Singh/Desktop/fail2test/aftermath-cli/src/main/java/dev/aftermath/cli/attach/ProjectDetector.java) — Line 75
- **Severity**: 🟢 LOW
- **Description**: `getElementsByTagName("artifactId").item(0)` grabs the first `<artifactId>` in the document. In a child POM with a `<parent>` block, this could return the parent's artifact ID instead of the project's own.
- **Fix**: Get the `<artifactId>` that is a direct child of the `<project>` root element only.

---

## 📊 Summary

| Severity | Count |
| :--- | :--- |
| 🔴 CRITICAL | 4 |
| 🟠 HIGH | 6 |
| 🟡 MEDIUM | 8 |
| 🟢 LOW | 7 |
| **TOTAL** | **25** |

---

## 🛠️ Areas of Improvement (Non-Bug Enhancements)

| # | Area | Module | Description |
| :--- | :--- | :--- | :--- |
| IMP-01 | **Unit Test Coverage** | All modules | No unit tests exist for Collector services, CLI commands, or Replay engine. Target 80%+ coverage. |
| IMP-02 | **API Documentation** | aftermath-collector | Add OpenAPI/Swagger annotations for auto-generated API docs. |
| IMP-03 | **Graceful Shutdown** | aftermath-sdk | `AsyncEventDispatcher.shutdown()` is never called. Register a Spring `@PreDestroy` hook. |
| IMP-04 | **Structured Logging** | All Java modules | Use structured JSON logging (Logback JSON encoder) for production log aggregation (ELK/Splunk). |
| IMP-05 | **Health Check Endpoint** | aftermath-collector | Add Spring Actuator `/actuator/health` for monitoring and container orchestration readiness probes. |
| IMP-06 | **Docker Compose for Full Stack** | Root | Create a single `docker-compose.yml` that starts Collector, UI, and sample services together. |
| IMP-07 | **Environment Profiles** | aftermath-collector | Add `application-prod.yml` with secure defaults (strong password, `ddl-auto: validate`, disabled H2 console). |
| IMP-08 | **Retry Logic** | aftermath-sdk | Add exponential backoff retry (max 3 attempts) in `HttpEventTransport` before silently dropping incidents. |
| IMP-09 | **Accessibility (a11y)** | aftermath-ui | Add `aria-label`, keyboard navigation, focus trapping in modal, and screen reader support. |
| IMP-10 | **Dark/Light Mode Toggle** | aftermath-ui | Currently hardcoded dark mode only. Add theme toggle for developer preference. |

---

> **"A production bug tracker should never itself have production bugs."**  
> — Aftermath QA Team
