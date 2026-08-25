package dev.aftermath.collector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aftermath.collector.dto.CreateIncidentRequest;
import dev.aftermath.collector.dto.IncidentListResponse;
import dev.aftermath.collector.dto.IncidentResponse;
import dev.aftermath.collector.entity.EvidenceEntity;
import dev.aftermath.collector.entity.IncidentEntity;
import dev.aftermath.collector.repository.EvidenceRepository;
import dev.aftermath.collector.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final EvidenceRepository evidenceRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public IncidentResponse saveIncident(CreateIncidentRequest request) {
        String serviceName = request.getDeployment() != null ? request.getDeployment().getServiceName() : "unknown";
        String httpMethod = request.getRequest() != null ? request.getRequest().getMethod() : "POST";
        String requestUri = request.getRequest() != null ? request.getRequest().getUri() : "/";
        String exceptionClass = request.getError() != null ? request.getError().getExceptionClass() : "Exception";
        String exceptionMessage = request.getError() != null ? request.getError().getMessage() : "";

        String fingerprint = computeFingerprint(serviceName, httpMethod, requestUri, exceptionClass, exceptionMessage);

        Optional<IncidentEntity> existing = incidentRepository.findByFingerprint(fingerprint);
        if (existing.isPresent()) {
            IncidentEntity entity = existing.get();
            entity.setOccurrenceCount(entity.getOccurrenceCount() + 1);
            entity.setLastSeenAt(LocalDateTime.now());
            log.info("Deduplicated recurring incident: {} (occurrences: {})", entity.getIncidentId(), entity.getOccurrenceCount());
            return mapToResponse(incidentRepository.save(entity));
        }

        log.info("Saving new incident: {} for service: {}", request.getIncidentId(), serviceName);

        String rawJson = null;
        try {
            rawJson = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            log.warn("Failed to serialize raw incident JSON", e);
        }

        IncidentEntity entity = IncidentEntity.builder()
                .incidentId(request.getIncidentId())
                .traceId(request.getTraceId())
                .serviceName(serviceName)
                .serviceVersion(request.getDeployment() != null ? request.getDeployment().getServiceVersion() : null)
                .environment(request.getDeployment() != null ? request.getDeployment().getEnvironment() : null)
                .commitHash(request.getDeployment() != null ? request.getDeployment().getCommitHash() : null)
                .httpMethod(httpMethod)
                .requestUri(requestUri)
                .statusCode(request.getError() != null ? request.getError().getStatusCode() : null)
                .exceptionClass(exceptionClass)
                .exceptionMessage(exceptionMessage)
                .stackTrace(request.getError() != null ? request.getError().getStackTrace() : null)
                .rawJson(rawJson)
                .fingerprint(fingerprint)
                .occurrenceCount(1)
                .createdAt(LocalDateTime.now())
                .lastSeenAt(LocalDateTime.now())
                .build();

        IncidentEntity saved = incidentRepository.save(entity);

        // Store request headers and body as evidence items
        if (request.getRequest() != null) {
            if (request.getRequest().getHeaders() != null) {
                request.getRequest().getHeaders().forEach((k, v) -> {
                    EvidenceEntity ev = EvidenceEntity.builder()
                            .incidentId(saved.getIncidentId())
                            .type("HEADER")
                            .keyName(k)
                            .valueData(v)
                            .build();
                    evidenceRepository.save(ev);
                });
            }
            if (request.getRequest().getBody() != null && !request.getRequest().getBody().isBlank()) {
                EvidenceEntity bodyEv = EvidenceEntity.builder()
                        .incidentId(saved.getIncidentId())
                        .type("BODY")
                        .keyName("requestBody")
                        .valueData(request.getRequest().getBody())
                        .build();
                evidenceRepository.save(bodyEv);
            }
        }

        return mapToResponse(saved);
    }

    public IncidentListResponse getIncidents(int page, int size, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastSeenAt"));
        Page<IncidentEntity> pageResult;

        if (search != null && !search.isBlank()) {
            pageResult = incidentRepository.findByServiceNameContainingIgnoreCaseOrExceptionClassContainingIgnoreCase(
                    search, search, pageRequest);
        } else {
            pageResult = incidentRepository.findAll(pageRequest);
        }

        List<IncidentResponse> content = pageResult.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return IncidentListResponse.builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    public IncidentResponse getIncidentById(String incidentId) {
        IncidentEntity entity = incidentRepository.findByIncidentId(incidentId)
                .orElseThrow(() -> new RuntimeException("Incident not found: " + incidentId));
        return mapToResponse(entity);
    }

    private String computeFingerprint(String service, String method, String uri, String exceptionClass, String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = String.format("%s|%s|%s|%s|%s", service, method, uri, exceptionClass, message != null ? message : "");
            byte[] hash = digest.digest(raw.getBytes("UTF-8"));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            return service + "_" + exceptionClass;
        }
    }

    private IncidentResponse mapToResponse(IncidentEntity entity) {
        return IncidentResponse.builder()
                .incidentId(entity.getIncidentId())
                .traceId(entity.getTraceId())
                .serviceName(entity.getServiceName())
                .serviceVersion(entity.getServiceVersion())
                .environment(entity.getEnvironment())
                .commitHash(entity.getCommitHash())
                .httpMethod(entity.getHttpMethod())
                .requestUri(entity.getRequestUri())
                .statusCode(entity.getStatusCode())
                .exceptionClass(entity.getExceptionClass())
                .exceptionMessage(entity.getExceptionMessage())
                .stackTrace(entity.getStackTrace())
                .rawJson(entity.getRawJson())
                .fingerprint(entity.getFingerprint())
                .occurrenceCount(entity.getOccurrenceCount())
                .createdAt(entity.getCreatedAt())
                .lastSeenAt(entity.getLastSeenAt())
                .build();
    }
}
