package dev.aftermath.cli.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

@Component
@Command(name = "testgen", description = "Generate runnable JUnit 5 test file for an incident")
public class TestGenCommand implements Runnable {

    @Parameters(index = "0", description = "Incident ID")
    private String incidentId;

    @Option(names = {"-f", "--framework"}, description = "Test framework: JUNIT5_RESTASSURED, JUNIT5_MOCKMVC, JUNIT5_WEBTESTCLIENT", defaultValue = "JUNIT5_RESTASSURED")
    private String framework;

    @Option(names = {"-o", "--out"}, description = "Output file path to save generated .java file")
    private String outputPath;

    @Option(names = {"-s", "--collector"}, description = "Collector base URL", defaultValue = "http://localhost:8090")
    private String collectorUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void run() {
        try {
            String payload = objectMapper.writeValueAsString(java.util.Map.of(
                    "framework", framework,
                    "packageName", "dev.aftermath.generated",
                    "targetBaseUrl", "http://localhost:8082"
            ));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(collectorUrl + "/api/v1/incidents/" + incidentId + "/generate-test"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            System.out.println("Generating " + framework + " test for incident " + incidentId + "...");
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() != 200) {
                System.err.println("Test generation failed: HTTP " + resp.statusCode());
                return;
            }

            JsonNode res = objectMapper.readTree(resp.body());
            String fileName = res.path("fileName").asText("ReproductionTest.java");
            String codeContent = res.path("codeContent").asText();

            if (outputPath != null && !outputPath.isBlank()) {
                File targetFile = new File(outputPath);
                if (targetFile.isDirectory()) {
                    targetFile = new File(targetFile, fileName);
                }
                Files.writeString(targetFile.toPath(), codeContent);
                System.out.println("✅ Generated test file saved to: " + targetFile.getAbsolutePath());
            } else {
                System.out.println("==========================================================================");
                System.out.println("GENERATED TEST FILE: " + fileName);
                System.out.println("==========================================================================");
                System.out.println(codeContent);
                System.out.println("==========================================================================");
            }

        } catch (Exception e) {
            System.err.println("Failed to execute testgen command: " + e.getMessage());
        }
    }
}
