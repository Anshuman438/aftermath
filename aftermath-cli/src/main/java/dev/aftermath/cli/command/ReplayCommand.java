package dev.aftermath.cli.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Command(name = "replay", description = "Re-execute an incident payload against target service")
public class ReplayCommand implements Runnable {

    @Parameters(index = "0", description = "Incident ID")
    private String incidentId;

    @Option(names = {"-t", "--target"}, description = "Target service base URL", defaultValue = "http://localhost:8082")
    private String targetUrl;

    @Option(names = {"-s", "--collector"}, description = "Collector base URL", defaultValue = "http://localhost:8090")
    private String collectorUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run() {
        try {
            String payload = objectMapper.writeValueAsString(java.util.Map.of("targetBaseUrl", targetUrl));
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(collectorUrl + "/api/v1/incidents/" + incidentId + "/replay"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            System.out.println("Replaying incident " + incidentId + " against " + targetUrl + "...");
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                System.err.println("Replay execution failed: HTTP " + resp.statusCode());
                return;
            }

            JsonNode res = objectMapper.readTree(resp.body());
            boolean reproduced = res.path("reproduced").asBoolean();

            System.out.println("==========================================================================");
            System.out.println("REPLAY RESULT: " + (reproduced ? "BUG REPRODUCED [SUCCESS]" : "DIFFERENT RESPONSE"));
            System.out.println("==========================================================================");
            System.out.println("Job ID:               " + res.path("jobId").asText());
            System.out.println("Original Status:      HTTP " + res.path("originalStatusCode").asInt());
            System.out.println("Replayed Status:      HTTP " + res.path("replayedStatusCode").asInt());
            System.out.println("Status Match:         " + res.path("statusMatch").asBoolean());
            System.out.println("Execution Time:       " + res.path("executionTimeMs").asLong() + " ms");
            System.out.println("\n--- Replayed Response Body ---");
            System.out.println(res.path("replayedResponseBody").asText());
            System.out.println("==========================================================================");

        } catch (Exception e) {
            System.err.println("Failed to execute replay command: " + e.getMessage());
        }
    }
}
