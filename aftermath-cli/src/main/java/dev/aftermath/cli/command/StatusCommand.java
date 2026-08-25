package dev.aftermath.cli.command;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@Command(name = "status", description = "Check Aftermath collector & system connectivity status")
public class StatusCommand implements Runnable {

    @Option(names = {"-s", "--collector"}, description = "Collector base URL", defaultValue = "http://localhost:8090")
    private String collectorUrl;

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();

    @Override
    public void run() {
        System.out.println("Checking Aftermath Collector status at " + collectorUrl + "...");
        try {
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(collectorUrl + "/api/v1/incidents?page=0&size=1")).GET().build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() == 200) {
                System.out.println("🟢 COLLECTOR SERVICE: CONNECTED & ONLINE (HTTP 200)");
            } else {
                System.out.println("🟡 COLLECTOR SERVICE: RESPONDED WITH HTTP " + resp.statusCode());
            }
        } catch (Exception e) {
            System.out.println("🔴 COLLECTOR SERVICE: OFFLINE (" + e.getMessage() + ")");
        }
    }
}
