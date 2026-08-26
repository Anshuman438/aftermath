package dev.aftermath.collector.controller;

import dev.aftermath.collector.dto.CreateIncidentRequest;
import dev.aftermath.collector.dto.IncidentListResponse;
import dev.aftermath.collector.dto.IncidentResponse;
import dev.aftermath.collector.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(@Valid @RequestBody CreateIncidentRequest request) {
        IncidentResponse response = incidentService.saveIncident(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<IncidentListResponse> getIncidents(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "search", required = false) String search) {
        int cappedSize = Math.min(Math.max(1, size), 100);
        IncidentListResponse response = incidentService.getIncidents(page, cappedSize, search);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{incidentId}")
    public ResponseEntity<IncidentResponse> getIncidentById(@PathVariable(name = "incidentId") String incidentId) {
        IncidentResponse response = incidentService.getIncidentById(incidentId);
        return ResponseEntity.ok(response);
    }
}
