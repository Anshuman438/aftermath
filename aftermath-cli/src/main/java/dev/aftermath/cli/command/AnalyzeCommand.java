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
@Command(name = "analyze", description = "Perform automated AI-style Root Cause Analysis on an incident")
public class AnalyzeCommand implements Runnable {

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
                    .uri(URI.create(collectorUrl + "/api/v1/incidents/" + incidentId + "/analysis"))
                    .GET()
                    .build();

            System.out.println("Analyzing incident " + incidentId + "...");
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                System.err.println("Analysis failed: HTTP " + resp.statusCode());
                return;
            }

            JsonNode res = objectMapper.readTree(resp.body());

            System.out.println("==========================================================================");
            System.out.println("🤖 AFTERMATH ROOT CAUSE ANALYSIS");
            System.out.println("==========================================================================");
            System.out.println("Incident ID:         " + res.path("incidentId").asText());
            System.out.println("Exception:           " + res.path("exceptionClass").asText());
            System.out.println("Failing Class:       " + res.path("failingClass").asText());
            System.out.println("Failing Method:      " + res.path("failingMethod").asText() + " (Line " + res.path("lineNumber").asInt() + ")");
            System.out.println("\n--- Root Cause Summary ---");
            System.out.println(res.path("rootCauseSummary").asText());
            System.out.println("\n--- Recommended Code Fix ---");
            System.out.println(res.path("recommendedFix").asText());
            System.out.println("\n--- Suggested Git Diff ---");
            System.out.println(res.path("suggestedDiff").asText());
            System.out.println("==========================================================================");

        } catch (Exception e) {
            System.err.println("Failed to execute analyze command: " + e.getMessage());
        }
    }
}
