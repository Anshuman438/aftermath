package dev.aftermath.collector.controller;

import dev.aftermath.collector.entity.ReplayJobEntity;
import dev.aftermath.collector.service.ReplayService;
import dev.aftermath.replay.model.ReplayRequest;
import dev.aftermath.replay.model.ReplayResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReplayController {

    private final ReplayService replayService;

    @PostMapping("/incidents/{incidentId}/replay")
    public ResponseEntity<ReplayResult> triggerReplay(
            @PathVariable(name = "incidentId") String incidentId,
            @RequestBody(required = false) ReplayRequest request) {
        ReplayResult result = replayService.replayIncident(incidentId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/incidents/{incidentId}/replays")
    public ResponseEntity<List<ReplayJobEntity>> getIncidentReplays(
            @PathVariable(name = "incidentId") String incidentId) {
        List<ReplayJobEntity> list = replayService.getReplaysByIncidentId(incidentId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/replays/{jobId}")
    public ResponseEntity<ReplayJobEntity> getReplayJob(
            @PathVariable(name = "jobId") String jobId) {
        ReplayJobEntity job = replayService.getReplayJobById(jobId);
        return ResponseEntity.ok(job);
    }
}
