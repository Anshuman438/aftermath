package dev.aftermath.collector.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_artifacts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestArtifactEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String artifactId;

    @Column(nullable = false)
    private String incidentId;

    private String framework;
    private String packageName;
    private String className;
    private String fileName;

    @Column(length = 50000)
    private String codeContent;

    private String createdAt;
}
