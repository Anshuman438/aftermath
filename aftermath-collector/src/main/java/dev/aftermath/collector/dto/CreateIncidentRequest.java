package dev.aftermath.collector.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class CreateIncidentRequest {

    @NotBlank(message = "incidentId cannot be blank")
    @Size(max = 128, message = "incidentId length cannot exceed 128 characters")
    private String incidentId;

    @Size(max = 128, message = "traceId length cannot exceed 128 characters")
    private String traceId;

    private long timestamp;

    @Valid
    private RequestData request;

    @Valid
    private ErrorData error;

    @Valid
    private DeploymentData deployment;

    @Data
    public static class RequestData {
        @Size(max = 16, message = "HTTP method length cannot exceed 16 characters")
        private String method;

        @Size(max = 2048, message = "URI length cannot exceed 2048 characters")
        private String uri;

        private Map<String, String> queryParams;
        private Map<String, String> headers;

        @Size(max = 65535, message = "Request body size cannot exceed 64KB")
        private String body;

        private long timestamp;
    }

    @Data
    public static class ErrorData {
        @Size(max = 256, message = "exceptionClass length cannot exceed 256 characters")
        private String exceptionClass;

        @Size(max = 4096, message = "Error message length cannot exceed 4KB")
        private String message;

        @Size(max = 65535, message = "Stack trace length cannot exceed 64KB")
        private String stackTrace;

        private int statusCode;
    }

    @Data
    public static class DeploymentData {
        @Size(max = 128, message = "serviceName length cannot exceed 128 characters")
        private String serviceName;

        @Size(max = 64, message = "serviceVersion length cannot exceed 64 characters")
        private String serviceVersion;

        @Size(max = 64, message = "environment length cannot exceed 64 characters")
        private String environment;

        @Size(max = 64, message = "commitHash length cannot exceed 64 characters")
        private String commitHash;
    }
}
