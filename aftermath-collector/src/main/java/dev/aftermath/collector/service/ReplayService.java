package dev.aftermath.collector.service;

import dev.aftermath.collector.entity.EvidenceEntity;
import dev.aftermath.collector.entity.IncidentEntity;
import dev.aftermath.collector.entity.ReplayJobEntity;
import dev.aftermath.collector.repository.EvidenceRepository;
import dev.aftermath.collector.repository.IncidentRepository;
import dev.aftermath.collector.repository.ReplayJobRepository;
import dev.aftermath.replay.executor.ReplayExecutor;
import dev.aftermath.replay.model.ReplayRequest;
import dev.aftermath.replay.model.ReplayResult;
import dev.aftermath.sdk.model.ErrorSnapshot;
import dev.aftermath.sdk.model.IncidentEvent;
import dev.aftermath.sdk.model.RequestSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplayService {

    private final IncidentRepository incidentRepository;
    private final EvidenceRepository evidenceRepository;
    private final ReplayJobRepository replayJobRepository;
    private final ReplayExecutor replayExecutor = new ReplayExecutor();

    @Transactional
    public ReplayResult replayIncident(String incidentId, ReplayRequest request) {
        IncidentEntity entity = incidentRepository.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("Incident not found: " + incidentId));

        List<EvidenceEntity> headers = evidenceRepository.findByIncidentIdAndType(incidentId, "HEADER");
        Map<String, String> headerMap = new HashMap<>();
        for (EvidenceEntity h : headers) {
            headerMap.put(h.getKeyName(), h.getValueData());
        }

        List<EvidenceEntity> bodies = evidenceRepository.findByIncidentIdAndType(incidentId, "BODY");
        String bodyContent = (bodies != null && !bodies.isEmpty()) ? bodies.get(0).getValueData() : null;

        RequestSnapshot reqSnapshot = new RequestSnapshot();
        reqSnapshot.setMethod(entity.getHttpMethod());
        reqSnapshot.setUri(entity.getRequestUri());
        reqSnapshot.setHeaders(headerMap);
        reqSnapshot.setBody(bodyContent);

        ErrorSnapshot errSnapshot = new ErrorSnapshot();
        errSnapshot.setStatusCode(entity.getStatusCode() != null ? entity.getStatusCode() : 500);
        errSnapshot.setExceptionClass(entity.getExceptionClass());
        errSnapshot.setMessage(entity.getExceptionMessage());
        errSnapshot.setStackTrace(entity.getStackTrace());

        IncidentEvent incidentEvent = new IncidentEvent();
        incidentEvent.setIncidentId(entity.getIncidentId());
        incidentEvent.setTraceId(entity.getTraceId());
        incidentEvent.setRequest(reqSnapshot);
        incidentEvent.setError(errSnapshot);

        if (request == null) {
            request = ReplayRequest.builder()
                    .incidentId(incidentId)
                    .targetBaseUrl("http://localhost:8082")
                    .build();
        } else {
            request.setIncidentId(incidentId);
        }

        ReplayResult result = replayExecutor.executeReplay(incidentEvent, request);

        String responseBody = result.getReplayedResponseBody();
        if (responseBody != null && responseBody.length() > 4000) {
            responseBody = responseBody.substring(0, 4000) + "... [truncated]";
        }

        try {
            ReplayJobEntity jobEntity = ReplayJobEntity.builder()
                    .jobId(result.getJobId())
                    .incidentId(incidentId)
                    .targetBaseUrl(result.getTargetBaseUrl())
                    .reproduced(result.isReproduced())
                    .originalStatusCode(result.getOriginalStatusCode())
                    .replayedStatusCode(result.getReplayedStatusCode())
                    .statusMatch(result.isStatusMatch())
                    .executionTimeMs(result.getExecutionTimeMs())
                    .replayedResponseBody(responseBody)
                    .createdAt(result.getTimestamp())
                    .build();

            replayJobRepository.save(jobEntity);
            log.info("Saved replay job {} for incident {}, reproduced: {}", result.getJobId(), incidentId, result.isReproduced());
        } catch (Exception e) {
            log.warn("Failed to persist ReplayJobEntity to DB: {}", e.getMessage());
        }

        return result;
    }

    public List<ReplayJobEntity> getReplaysByIncidentId(String incidentId) {
        return replayJobRepository.findByIncidentIdOrderByCreatedAtDesc(incidentId);
    }

    public ReplayJobEntity getReplayJobById(String jobId) {
        return replayJobRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Replay job not found: " + jobId));
    }
}
