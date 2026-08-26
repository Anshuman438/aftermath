# AFTERMATH Release Changelog

All notable changes to the AFTERMATH platform will be documented in this file.

---

## [v6.0.0] - 2026-08-26

### 🚀 Tiered Data Retention & Cold Compliance Engine
- **Auto-Pruning TTL Data Retention Service (`DataRetentionService`)**:
  - Daily `@Scheduled` cron task automatically purging raw incident capsules older than 30 days (`aftermath.retention.days=30`).
  - **Permanent Test Code Preservation**: Strictly preserves all generated test artifacts (`TestArtifactEntity`) permanently so `.java` test files are NEVER lost when raw logs are purged.
- **Cold Compliance GZIP Archive Exporter (`S3ArchiveExporter`)**:
  - Generates compressed GZIP JSON compliance archives (`.json.gz`) for long-term 7-year cold storage in AWS S3 / GCS buckets.
- **Data Retention REST Endpoints (`AutomationController`)**:
  - Exposed `POST /api/v1/retention/purge` and `GET /api/v1/retention/archive-gzip`.

---

## [v5.0.0] - 2026-08-26

### 🎉 100% Total Completion Ultimate Master Release
- Cloud Multi-Tenant SaaS Auth (`AuthController`, `JwtTokenProvider`), Kubernetes Helm Charts (`deploy/helm/aftermath`), JetBrains IntelliJ Manifest (`plugin.xml`).

---

## [v4.0.0] - 2026-08-26

### 🚀 Polyglot Expansion Master Release
- Go, Rust, C#, PHP, Ruby, Python, Node.js SDKs & Cascading Error Engine.
