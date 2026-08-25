package dev.aftermath.cli.attach;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class YamlConfigInjector {

    public boolean injectConfiguration(File projectDir, String serviceName, String collectorUrl) throws Exception {
        File resourcesDir = new File(projectDir, "src/main/resources");
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs();
        }

        File appYml = new File(resourcesDir, "application.yml");
        File appYaml = new File(resourcesDir, "application.yaml");
        File appProps = new File(resourcesDir, "application.properties");

        File targetConfig = appYml;
        if (appYaml.exists()) {
            targetConfig = appYaml;
        } else if (appProps.exists()) {
            targetConfig = appProps;
        }

        if (collectorUrl == null || collectorUrl.isBlank()) {
            collectorUrl = "http://localhost:8090/api/v1/incidents";
        }

        String aftermathBlock = String.format("""

# AFTERMATH Auto-Configuration Block
aftermath:
  sdk:
    enabled: true
    service-name: %s
    service-version: 1.0.0
    collector-url: %s
    fail-open: true
""", serviceName, collectorUrl);

        if (targetConfig.exists()) {
            String existingContent = Files.readString(targetConfig.toPath());
            if (existingContent.contains("aftermath:")) {
                return false; // Config already present
            }
            Files.writeString(targetConfig.toPath(), existingContent + aftermathBlock);
        } else {
            Files.writeString(targetConfig.toPath(), aftermathBlock.trim() + "\n");
        }

        return true;
    }
}
