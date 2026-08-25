package dev.aftermath.sdk.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aftermath.sdk.model.IncidentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class HttpEventTransport implements EventTransport {

    private static final Logger log = LoggerFactory.getLogger(HttpEventTransport.class);
    private final String collectorUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpEventTransport(String collectorUrl) {
        this.collectorUrl = collectorUrl.endsWith("/") ? collectorUrl + "api/v1/incidents" : collectorUrl + "/api/v1/incidents";
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void send(IncidentEvent event) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(event);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(collectorUrl))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(2))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Aftermath: Incident {} dispatched to collector [HTTP {}]", event.getIncidentId(), response.statusCode());
            } else {
                log.warn("Aftermath: Collector returned status {} for incident {}", response.statusCode(), event.getIncidentId());
            }
        } catch (Exception e) {
            // Fail-open guarantee: Log warning only, never throw exception to host app
            log.warn("Aftermath: Failed to send incident {} to collector (Collector unavailable: {})", event.getIncidentId(), e.getMessage());
        }
    }
}
