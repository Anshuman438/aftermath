package dev.aftermath.collector.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentResponse {
    private String incidentId;
    private String traceId;
    private String serviceName;
    private String serviceVersion;
    private String environment;
    private String commitHash;
    private String httpMethod;
    private String requestUri;
    private Integer statusCode;
    private String exceptionClass;
    private String exceptionMessage;
    private String stackTrace;
    private String rawJson;
    private String fingerprint;
    private Integer occurrenceCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastSeenAt;
}
