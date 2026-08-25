package dev.aftermath.collector.controller;

import dev.aftermath.collector.entity.TestArtifactEntity;
import dev.aftermath.collector.service.TestGenService;
import dev.aftermath.testgen.model.GeneratedTestArtifact;
import dev.aftermath.testgen.model.TestGeneratorOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestGenController {

    private final TestGenService testGenService;

    @PostMapping("/incidents/{incidentId}/generate-test")
    public ResponseEntity<GeneratedTestArtifact> generateTest(
            @PathVariable(name = "incidentId") String incidentId,
            @RequestBody(required = false) TestGeneratorOptions options) {
        GeneratedTestArtifact artifact = testGenService.generateTestForIncident(incidentId, options);
        return ResponseEntity.ok(artifact);
    }

    @GetMapping("/incidents/{incidentId}/test-artifacts")
    public ResponseEntity<List<TestArtifactEntity>> getIncidentTestArtifacts(
            @PathVariable(name = "incidentId") String incidentId) {
        List<TestArtifactEntity> list = testGenService.getArtifactsByIncidentId(incidentId);
        return ResponseEntity.ok(list);
    }
}
