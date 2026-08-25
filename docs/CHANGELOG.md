# AFTERMATH Release Changelog

All notable changes to the AFTERMATH platform will be documented in this file.

---

## [v1.1.0] - 2026-08-26

### 🚀 Added
- **Zero-Touch Developer Auto-Attacher (`aftermath attach`)**:
  - Picocli subcommand `aftermath attach [--path <targetDir>] [--name <serviceName>] [--collector <url>]`.
  - `ProjectDetector`: Auto-detects Maven (`pom.xml`) and Gradle (`build.gradle`).
  - `PomXmlInjector`: Safely parses XML DOM and injects `dev.aftermath:aftermath-sdk:0.1.0-SNAPSHOT` dependency.
  - `YamlConfigInjector`: Scans `src/main/resources/` and injects `aftermath.sdk.enabled: true` configuration block.

---

## [v1.0.0] - 2026-08-26

### 🎉 Initial MVP Release
- **`aftermath-sdk`**: Spring Boot Auto-Configuration interceptor, fail-open async dispatcher, and redaction engine (Bearer tokens, cookies, PII).
- **`aftermath-collector`**: Central ingestion service with H2/SQLite database persistence for incident capsules.
- **`aftermath-replay`**: Replay Engine using Java 11 `HttpClient` with live reproduction verification (`reproduced: true`).
- **`aftermath-testgen`**: Automated JUnit 5 / REST-Assured / MockMvc / WebTestClient `.java` code generator.
- **`aftermath-ui`**: React 18 + Vite + Tailwind CSS dashboard with Incident Explorer, stack trace viewer, live replay inspector, and `.java` file download button.
- **`aftermath-cli`**: Picocli terminal tool (`status`, `list`, `view`, `replay`, `testgen`).
- **System Orchestrator**: `start_aftermath_stack.ps1` for 1-click startup of all services.
