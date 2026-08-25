package dev.aftermath.collector.controller;

import dev.aftermath.collector.service.AnalysisService;
import dev.aftermath.collector.service.AnalysisService.AnalysisResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    @GetMapping("/incidents/{incidentId}/analysis")
    public ResponseEntity<AnalysisResult> analyzeIncident(@PathVariable(name = "incidentId") String incidentId) {
        AnalysisResult result = analysisService.analyzeIncident(incidentId);
        return ResponseEntity.ok(result);
    }
}
