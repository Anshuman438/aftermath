package dev.aftermath.collector.controller;

import dev.aftermath.collector.service.CascadingErrorAnalyzer;
import dev.aftermath.collector.service.CascadingErrorAnalyzer.CascadingChainReport;
import dev.aftermath.collector.service.ContractTestGenerator;
import dev.aftermath.collector.service.ContractTestGenerator.ContractTestArtifact;
import dev.aftermath.collector.service.DataRetentionService;
import dev.aftermath.collector.service.S3ArchiveExporter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AutomationController {

    private final ContractTestGenerator contractTestGenerator;
    private final CascadingErrorAnalyzer cascadingErrorAnalyzer;
    private final DataRetentionService dataRetentionService;
    private final S3ArchiveExporter s3ArchiveExporter;

    @GetMapping("/incidents/{incidentId}/contract-test")
    public ResponseEntity<ContractTestArtifact> getContractTest(@PathVariable(name = "incidentId") String incidentId) {
        ContractTestArtifact artifact = contractTestGenerator.generateContractTest(incidentId);
        return ResponseEntity.ok(artifact);
    }

    @GetMapping("/incidents/{incidentId}/cascading-analysis")
    public ResponseEntity<CascadingChainReport> getCascadingAnalysis(@PathVariable(name = "incidentId") String incidentId) {
        CascadingChainReport report = cascadingErrorAnalyzer.analyzeCascadingErrors(incidentId);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/retention/purge")
    public ResponseEntity<String> purgeExpiredData() {
        int count = dataRetentionService.purgeExpiredIncidents();
        return ResponseEntity.ok("Purged " + count + " expired raw incident capsules. Test artifacts preserved permanently.");
    }

    @GetMapping("/retention/archive-gzip")
    public ResponseEntity<byte[]> downloadColdArchive(@RequestParam(name = "daysOld", defaultValue = "30") int daysOld) {
        byte[] gzipData = s3ArchiveExporter.exportIncidentsToGzipArchive(daysOld);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=aftermath_compliance_archive_" + daysOld + "d.json.gz")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(gzipData);
    }
}
