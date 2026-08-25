package dev.aftermath.cli.command;

import dev.aftermath.cli.attach.PomXmlInjector;
import dev.aftermath.cli.attach.ProjectDetector;
import dev.aftermath.cli.attach.YamlConfigInjector;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;

@Component
@Command(name = "attach", description = "Zero-Touch Auto-Attacher: Automatically inject AFTERMATH SDK into any Spring Boot project")
public class AttachCommand implements Runnable {

    @Option(names = {"-p", "--path"}, description = "Target project root directory path", defaultValue = ".")
    private String projectPath;

    @Option(names = {"-n", "--name"}, description = "Custom service name override")
    private String serviceNameOverride;

    @Option(names = {"-s", "--collector"}, description = "Collector endpoint URL", defaultValue = "http://localhost:8090/api/v1/incidents")
    private String collectorUrl;

    private final ProjectDetector detector = new ProjectDetector();
    private final PomXmlInjector pomInjector = new PomXmlInjector();
    private final YamlConfigInjector configInjector = new YamlConfigInjector();

    @Override
    public void run() {
        try {
            File targetDir = new File(projectPath).getCanonicalFile();
            System.out.println("==========================================================================");
            System.out.println("⚡ AFTERMATH ZERO-TOUCH AUTO-ATTACHER");
            System.out.println("==========================================================================");
            System.out.println("Target Directory: " + targetDir.getAbsolutePath());

            ProjectDetector.ProjectMetadata metadata = detector.detect(targetDir);
            String activeServiceName = (serviceNameOverride != null && !serviceNameOverride.isBlank())
                    ? serviceNameOverride
                    : metadata.getServiceName();

            System.out.println("Detected Build Tool: " + metadata.getBuildTool());
            System.out.println("Service Name:        " + activeServiceName);

            if (metadata.getBuildTool() == ProjectDetector.BuildTool.UNKNOWN) {
                System.err.println("❌ Error: No pom.xml or build.gradle found in " + targetDir.getAbsolutePath());
                return;
            }

            if (metadata.getBuildTool() == ProjectDetector.BuildTool.MAVEN) {
                System.out.println("\n[1/2] Injecting aftermath-sdk dependency into pom.xml...");
                boolean pomModified = pomInjector.injectSdkDependency(metadata.getBuildFile());
                if (pomModified) {
                    System.out.println("✅ Injected <dependency>dev.aftermath:aftermath-sdk:0.1.0-SNAPSHOT</dependency>");
                } else {
                    System.out.println("ℹ️  aftermath-sdk dependency is already present in pom.xml");
                }
            } else {
                System.out.println("ℹ️  Gradle build detected. Please add implementation 'dev.aftermath:aftermath-sdk:0.1.0-SNAPSHOT' to build.gradle");
            }

            System.out.println("\n[2/2] Injecting aftermath configuration into application.yml...");
            boolean configModified = configInjector.injectConfiguration(targetDir, activeServiceName, collectorUrl);
            if (configModified) {
                System.out.println("✅ Injected aftermath.sdk configuration block into application.yml");
            } else {
                System.out.println("ℹ️  aftermath.sdk configuration is already present in application.yml");
            }

            System.out.println("\n==========================================================================");
            System.out.println("🚀 SUCCESS: AFTERMATH is now attached to " + activeServiceName + "!");
            System.out.println("Run 'mvn clean package' or 'mvn spring-boot:run' to start monitoring.");
            System.out.println("==========================================================================");

        } catch (Exception e) {
            System.err.println("❌ Failed to attach AFTERMATH to project: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
