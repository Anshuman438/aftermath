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
@Command(name = "view", description = "View detailed incident capsule and stack trace")
public class ViewCommand implements Runnable {

    @Parameters(index = "0", description = "Incident ID")
    private String incidentId;

    @Option(names = {"-s", "--collector"}, description = "Collector base URL", defaultValue = "http://localhost:8090")
    private String collectorUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(collectorUrl + "/api/v1/incidents/" + incidentId))
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                System.err.println("Incident not found: " + incidentId);
                return;
            }

            JsonNode item = objectMapper.readTree(resp.body());
            System.out.println("==========================================================================");
            System.out.println("INCIDENT DETAILS: " + item.path("incidentId").asText());
            System.out.println("==========================================================================");
            System.out.println("Service:      " + item.path("serviceName").asText() + " (" + item.path("serviceVersion").asText() + ")");
            System.out.println("HTTP Target:  " + item.path("httpMethod").asText() + " " + item.path("requestUri").asText() + " -> HTTP " + item.path("statusCode").asText());
            System.out.println("Trace ID:     " + item.path("traceId").asText());
            System.out.println("Exception:    " + item.path("exceptionClass").asText());
            System.out.println("Message:      " + item.path("exceptionMessage").asText());
            System.out.println("\n--- Stack Trace ---");
            System.out.println(item.path("stackTrace").asText());
            System.out.println("==========================================================================");

        } catch (Exception e) {
            System.err.println("Failed to view incident: " + e.getMessage());
        }
    }
}
