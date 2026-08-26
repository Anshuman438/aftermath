package dev.aftermath.collector.controller;

import dev.aftermath.collector.service.ContractTestGenerator;
import dev.aftermath.collector.service.ContractTestGenerator.ContractTestArtifact;
import dev.aftermath.collector.service.GitPrBotService;
import dev.aftermath.collector.service.GitPrBotService.GitPrPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AutomationController {

    private final GitPrBotService gitPrBotService;
    private final ContractTestGenerator contractTestGenerator;

    @GetMapping("/incidents/{incidentId}/git-pr-payload")
    public ResponseEntity<GitPrPayload> getGitPrPayload(@PathVariable(name = "incidentId") String incidentId) {
        GitPrPayload payload = gitPrBotService.generatePrPayload(incidentId);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/incidents/{incidentId}/contract-test")
    public ResponseEntity<ContractTestArtifact> getContractTest(@PathVariable(name = "incidentId") String incidentId) {
        ContractTestArtifact artifact = contractTestGenerator.generateContractTest(incidentId);
        return ResponseEntity.ok(artifact);
    }
}
