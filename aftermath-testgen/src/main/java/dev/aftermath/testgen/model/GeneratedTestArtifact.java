package dev.aftermath.testgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedTestArtifact {
    private String artifactId;
    private String incidentId;
    private String framework;
    private String packageName;
    private String className;
    private String fileName;
    private String codeContent;
    private String createdAt;
}
