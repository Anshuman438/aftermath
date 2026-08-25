package dev.aftermath.testgen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestGeneratorOptions {
    private String packageName;
    private String className;
    private TestFramework framework;
    private String targetBaseUrl;
}
