package dev.aftermath.cli.attach;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ProjectDetector {

    public enum BuildTool {
        MAVEN,
        GRADLE,
        UNKNOWN
    }

    public static class ProjectMetadata {
        private final BuildTool buildTool;
        private final String serviceName;
        private final File buildFile;

        public ProjectMetadata(BuildTool buildTool, String serviceName, File buildFile) {
            this.buildTool = buildTool;
            this.serviceName = serviceName;
            this.buildFile = buildFile;
        }

        public BuildTool getBuildTool() {
            return buildTool;
        }

        public String getServiceName() {
            return serviceName;
        }

        public File getBuildFile() {
            return buildFile;
        }
    }

    public ProjectMetadata detect(File projectDir) {
        if (projectDir == null || !projectDir.exists()) {
            projectDir = new File(".");
        }

        File pomXml = new File(projectDir, "pom.xml");
        if (pomXml.exists()) {
            String serviceName = extractMavenArtifactId(pomXml);
            if (serviceName == null || serviceName.isBlank()) {
                serviceName = projectDir.getAbsoluteFile().getName();
            }
            return new ProjectMetadata(BuildTool.MAVEN, serviceName, pomXml);
        }

        File buildGradle = new File(projectDir, "build.gradle");
        File buildGradleKts = new File(projectDir, "build.gradle.kts");
        if (buildGradle.exists() || buildGradleKts.exists()) {
            File targetGradle = buildGradle.exists() ? buildGradle : buildGradleKts;
            String serviceName = projectDir.getAbsoluteFile().getName();
            return new ProjectMetadata(BuildTool.GRADLE, serviceName, targetGradle);
        }

        return new ProjectMetadata(BuildTool.UNKNOWN, projectDir.getAbsoluteFile().getName(), null);
    }

    private String extractMavenArtifactId(File pomXml) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pomXml);
            doc.getDocumentElement().normalize();

            NodeList artifactList = doc.getElementsByTagName("artifactId");
            if (artifactList.getLength() > 0) {
                return artifactList.item(0).getTextContent().trim();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
