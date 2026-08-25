package dev.aftermath.cli.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Command(name = "list", description = "List captured incident capsules")
public class ListCommand implements Runnable {

    @Option(names = {"-s", "--collector"}, description = "Collector base URL", defaultValue = "http://localhost:8090")
    private String collectorUrl;

    @Option(names = {"-q", "--search"}, description = "Filter search term")
    private String search;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run() {
        try {
            String urlStr = collectorUrl + "/api/v1/incidents?page=0&size=20";
            if (search != null && !search.isBlank()) {
                urlStr += "&search=" + search;
            }

            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(urlStr)).GET().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                System.err.println("Error fetching incidents: HTTP " + resp.statusCode());
                return;
            }

            JsonNode root = objectMapper.readTree(resp.body());
            JsonNode content = root.get("content");

            System.out.println("==========================================================================================");
            System.out.printf("%-38s | %-15s | %-6s | %-30s%n", "INCIDENT ID", "SERVICE", "STATUS", "METHOD & URI");
            System.out.println("==========================================================================================");

            if (content != null && content.isArray() && content.size() > 0) {
                for (JsonNode item : content) {
                    System.out.printf("%-38s | %-15s | HTTP %-1d | %-4s %-25s%n",
                            item.path("incidentId").asText("N/A"),
                            item.path("serviceName").asText("unknown"),
                            item.path("statusCode").asInt(500),
                            item.path("httpMethod").asText("GET"),
                            item.path("requestUri").asText("/"));
                }
            } else {
                System.out.println("No captured incidents found.");
            }
            System.out.println("==========================================================================================");

        } catch (Exception e) {
            System.err.println("Failed to list incidents: " + e.getMessage());
        }
    }
}
