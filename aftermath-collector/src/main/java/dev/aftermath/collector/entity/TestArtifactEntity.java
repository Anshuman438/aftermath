package dev.aftermath.collector.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    private String testType; // JUNIT5, REST_ASSURED, CURL, POSTMAN

    private String fileName;

    @Column(length = 50000)
    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
