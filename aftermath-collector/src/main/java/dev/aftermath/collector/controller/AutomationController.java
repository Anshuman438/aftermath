package dev.aftermath.collector.controller;

import dev.aftermath.collector.service.ContractTestGenerator;
import dev.aftermath.collector.service.ContractTestGenerator.ContractTestArtifact;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AutomationController {

    private final ContractTestGenerator contractTestGenerator;

    @GetMapping("/incidents/{incidentId}/contract-test")
    public ResponseEntity<ContractTestArtifact> getContractTest(@PathVariable(name = "incidentId") String incidentId) {
        ContractTestArtifact artifact = contractTestGenerator.generateContractTest(incidentId);
        return ResponseEntity.ok(artifact);
    }
}
