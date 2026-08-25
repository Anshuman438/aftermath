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
@Command(name = "curl", description = "Export captured incident request capsule as runnable cURL command")
public class CurlCommand implements Runnable {

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
            String method = item.path("httpMethod").asText("POST");
            String uri = item.path("requestUri").asText("/");
            String rawJson = item.path("rawJson").asText();

            StringBuilder curl = new StringBuilder();
            curl.append("curl -X ").append(method.toUpperCase()).append(" \"").append(targetUrl).append(uri).append("\" \\\n");
            curl.append("  -H \"Content-Type: application/json\" \\\n");

            String bodyStr = "";
            if (rawJson != null && !rawJson.isBlank()) {
                try {
                    JsonNode parsed = objectMapper.readTree(rawJson);
                    JsonNode reqNode = parsed.get("request");
                    if (reqNode != null && reqNode.has("body")) {
                        bodyStr = reqNode.get("body").asText();
                    }
                } catch (Exception ignored) {}
            }

            if (!bodyStr.isBlank()) {
                curl.append("  -d '").append(bodyStr.replace("'", "'\\''")).append("'");
            }

            System.out.println("==========================================================================");
            System.out.println("cURL COMMAND EXPORT FOR INCIDENT " + incidentId);
            System.out.println("==========================================================================");
            System.out.println(curl.toString());
            System.out.println("==========================================================================");

        } catch (Exception e) {
            System.err.println("Failed to generate cURL command: " + e.getMessage());
        }
    }
}
