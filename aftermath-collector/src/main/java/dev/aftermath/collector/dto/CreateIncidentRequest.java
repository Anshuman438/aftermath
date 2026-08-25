package dev.aftermath.collector.dto;

import lombok.Data;

import java.util.Map;

@Data
public class CreateIncidentRequest {
    private String incidentId;
    private String traceId;
    private long timestamp;
    private RequestData request;
    private ErrorData error;
    private DeploymentData deployment;

    @Data
    public static class RequestData {
        private String method;
        private String uri;
        private Map<String, String> queryParams;
        private Map<String, String> headers;
        private String body;
        private long timestamp;
    }

    @Data
    public static class ErrorData {
        private String exceptionClass;
        private String message;
        private String stackTrace;
        private int statusCode;
    }

    @Data
    public static class DeploymentData {
        private String serviceName;
        private String serviceVersion;
        private String environment;
        private String commitHash;
    }
}
