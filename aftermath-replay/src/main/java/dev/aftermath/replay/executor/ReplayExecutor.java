package dev.aftermath.replay.executor;

import dev.aftermath.replay.model.ReplayRequest;
import dev.aftermath.replay.model.ReplayResult;
import dev.aftermath.sdk.model.IncidentEvent;
import dev.aftermath.sdk.model.RequestSnapshot;
import dev.aftermath.sdk.model.ErrorSnapshot;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class ReplayExecutor {

    private static final Set<String> RESTRICTED_HEADERS = Set.of(
            "connection", "content-length", "date", "expect", "from", "host",
            "origin", "proxy-authorization", "via", "upgrade"
    );

    private final HttpClient httpClient;

    public ReplayExecutor() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public ReplayResult executeReplay(IncidentEvent incident, ReplayRequest request) {
        String jobId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        String timestamp = Instant.now().toString();

        RequestSnapshot reqSnapshot = incident.getRequest();
        ErrorSnapshot errSnapshot = incident.getError();
        int expectedStatus = (errSnapshot != null && errSnapshot.getStatusCode() != 0)
                ? errSnapshot.getStatusCode() : 500;

        String baseUrl = request.getTargetBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:8082";
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        String uriPath = reqSnapshot != null ? reqSnapshot.getUri() : "/";
        String fullUrl = baseUrl + uriPath;

        try {
            HttpRequest.Builder httpReqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .timeout(Duration.ofMillis(request.getTimeoutMs() != null ? request.getTimeoutMs() : 10000));

            String method = (reqSnapshot != null && reqSnapshot.getMethod() != null)
                    ? reqSnapshot.getMethod().toUpperCase() : "GET";
            String bodyContent = request.getOverrideBody() != null 
                    ? request.getOverrideBody() 
                    : (reqSnapshot != null ? reqSnapshot.getBody() : null);

            if ("POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method)) {
                httpReqBuilder.method(method, bodyContent != null ? HttpRequest.BodyPublishers.ofString(bodyContent) : HttpRequest.BodyPublishers.noBody());
            } else {
                httpReqBuilder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            Map<String, String> headersToSet = new HashMap<>();
            if (reqSnapshot != null && reqSnapshot.getHeaders() != null) {
                headersToSet.putAll(reqSnapshot.getHeaders());
            }
            if (request.getOverrideHeaders() != null) {
                headersToSet.putAll(request.getOverrideHeaders());
            }

            for (Map.Entry<String, String> entry : headersToSet.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if (k != null && v != null && !RESTRICTED_HEADERS.contains(k.toLowerCase()) && !v.equals("[REDACTED]")) {
                    try {
                        String sanitizedVal = v.replaceAll("[\\r\\n]", "").trim();
                        httpReqBuilder.header(k, sanitizedVal);
                    } catch (Exception ignored) {}
                }
            }

            if (!headersToSet.containsKey("content-type") && !headersToSet.containsKey("Content-Type")) {
                httpReqBuilder.header("Content-Type", "application/json");
            }

            HttpRequest httpRequest = httpReqBuilder.build();
            log.info("Replaying incident {} against {}", incident.getIncidentId(), fullUrl);

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            long elapsed = System.currentTimeMillis() - startTime;

            int replayedStatus = response.statusCode();
            boolean statusMatch = (replayedStatus == expectedStatus);
            boolean reproduced = (replayedStatus >= 400);

            Map<String, String> replayedHeaders = new HashMap<>();
            response.headers().map().forEach((k, vList) -> {
                if (vList != null && !vList.isEmpty()) {
                    replayedHeaders.put(k, vList.get(0));
                }
            });

            return ReplayResult.builder()
                    .jobId(jobId)
                    .incidentId(incident.getIncidentId())
                    .targetBaseUrl(baseUrl)
                    .reproduced(reproduced)
                    .originalStatusCode(expectedStatus)
                    .replayedStatusCode(replayedStatus)
                    .statusMatch(statusMatch)
                    .originalResponseBody(errSnapshot != null ? errSnapshot.getMessage() : "")
                    .replayedResponseBody(response.body())
                    .replayedResponseHeaders(replayedHeaders)
                    .executionTimeMs(elapsed)
                    .timestamp(timestamp)
                    .build();

        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("Failed to execute replay for incident {}: {}", incident.getIncidentId(), e.getMessage(), e);

            return ReplayResult.builder()
                    .jobId(jobId)
                    .incidentId(incident.getIncidentId())
                    .targetBaseUrl(baseUrl)
                    .reproduced(false)
                    .originalStatusCode(expectedStatus)
                    .replayedStatusCode(0)
                    .statusMatch(false)
                    .errorMessage("Replay connection failure: " + e.getMessage())
                    .executionTimeMs(elapsed)
                    .timestamp(timestamp)
                    .build();
        }
    }
}
